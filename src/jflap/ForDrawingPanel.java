package jflap;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;

public class ForDrawingPanel extends JPanel {

    private static ArrayList<Node> nodes;
    private static ArrayList<Transition> transitionsList;
    private final JPopupMenu popup = new JPopupMenu();
    private static int startNode = -1;
    private final int FINAL = 2, START = 1;
    private JButton pressedButton;
    private int pressedNodeNumber;
    private JCheckBoxMenuItem isStart;

    public int getStartNode() {
        return startNode;
    }

    private JCheckBoxMenuItem isEnd;
    private static JMenu trans;
    private Graphics graphics;
    private Graphics2D graphics2D;
    private static int nodesCounter = 0;
    private JFlapFrame container;
    private static PriorityQueue<Integer> queuedNumbers;
    public static MouseListener ml;

    private int getNextNumber() {
        if (queuedNumbers.isEmpty()) {
            return nodesCounter++;
        }
        return queuedNumbers.poll();
    }

    public void removeMouseListeners() {
        this.removeMouseListener(ml);
        for (int i = 0; i < nodes.size(); i++) {
            nodes.get(i).getNodeButton().removeMouseListener(ml);
        }
    }

    public void addMouseListeners() {
        this.addMouseListener(ml);
        for (int i = 0; i < nodes.size(); i++) {
            nodes.get(i).getNodeButton().addMouseListener(ml);
        }
    }

    public ForDrawingPanel(JFlapFrame container) {
        setSize(container.getWidth(), (int) (container.getHeight() * (16.0 / 20.0)));
        intializeMl();
        addMouseListener(ml);
        nodes = new ArrayList<>();
        queuedNumbers = new PriorityQueue<>();
        transitionsList = new ArrayList<>();
        graphics = getGraphics();
        graphics2D = (Graphics2D) graphics;
        this.container = container;
        setMenu();

    }

    public ArrayList<Transition> getTransitionsList() {
        return transitionsList;
    }

    public ArrayList<Node> getNodes() {
        return nodes;
    }

    private void addNode(Point p) {

        int nodeNumber = getNextNumber();
        String nodeName = "s" + nodeNumber;

        JButton addBtn = new JButton();
        JLabel label = new JLabel(nodeName);

        // set button atts
        addBtn.setBounds((int) p.getX() - 35, (int) p.getY() - 35, 50, 50);
        addBtn.setBorder(new RoundedBorder(50, JFlapFrame.MAASTRICHT_BLUE)); //10 is the radius
        addBtn.setBackground(JFlapFrame.BRIGHT_YELLOW);
        addBtn.setOpaque(false);
        addBtn.addMouseListener(ml);
        //---------------------------------------------------------------

        //set label att
        label.setBounds((int) p.getX() - 35, (int) p.getY() - 35, 50, 50);
        label.setForeground(JFlapFrame.BABY_POWDER);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        //---------------------------------------------------------------

        //add components
        add(label);

        add(addBtn);

        JCheckBoxMenuItem nodeItem = new JCheckBoxMenuItem(nodeName);

        //Add new node to list
        nodes.add(new Node(nodeNumber, addBtn, label, nodeItem));

        nodeItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                JButton endNode = null;
                int i = 0;
                for (; i < nodes.size(); i++) {
                    if (nodes.get(i).getNodeLabel().getText().equals(nodeName)) {
                        endNode = nodes.get(i).getNodeButton();
                        break;
                    }
                }

                int selectedNodeIndex = getNodeIndex(pressedNodeNumber);
                if (!nodeItem.isSelected()) {
                    for (int j = 0; j < transitionsList.size(); j++) {
                        String u = transitionsList.get(j).getFrom().getNodeLabel().getText();
                        String v = transitionsList.get(j).getTo().getNodeLabel().getText();
                        if (u.equals(nodes.get(selectedNodeIndex).getNodeLabel().getText()) && v.equals(nodes.get(i).getNodeLabel().getText())) {
                            transitionsList.remove(j);
                            repaint();
                            break;
                        }
                    }
                    return;
                }

                if (endNode != null && pressedButton != null) {
                    Node from = nodes.get(selectedNodeIndex);
                    Node to = nodes.get(i);
                    String transitions = JOptionPane.showInputDialog(null, "Please Enter Transtions");
                    if (transitions != null) {
                        String newTrans = "";
                        Set<Character> s = new TreeSet<>();
                        for (int j = 0; j < transitions.length(); j++) {
                            Character c = transitions.charAt(j);
                            if (s.contains(transitions.charAt(j))) {
                                continue;
                            }
                            newTrans += c;
                            s.add(c);
                        }
                        Transition t = new Transition(from, to, newTrans.toCharArray());
                        transitionsList.add(t);
                    }
                }
                repaint();
            }
        });
        trans.add(nodeItem);
        repaint();
        //----------------------------------------------------------------
    }

    private void setMenu() {
        // Add Delete node item
        JMenuItem deleteItem = new JMenuItem("delete node");
        deleteItem.addActionListener(new ActionListener() {

            //TODO set the removeing
            public void actionPerformed(ActionEvent e) {
//                pressedButton.setBackground(Color.red);
//                pressedButton.setForeground(Color.red);

                int selectedNodeIndex = getNodeIndex(pressedNodeNumber);

                remove(pressedButton);
                JLabel temp = nodes.get(selectedNodeIndex).getNodeLabel();
                remove(temp);

                int n = nodes.get(selectedNodeIndex).getNodeNumber();
                nodes.remove(selectedNodeIndex);
                queuedNumbers.add(n);
                if (startNode == pressedNodeNumber) {
                    startNode = -1;
                }
                for (int i = 0; i < transitionsList.size(); i++) {

                    int u = transitionsList.get(i).getFrom().getNodeNumber();
                    int v = transitionsList.get(i).getTo().getNodeNumber();
                    if (u == n || v == n) {
                        transitionsList.remove(i);
                        i = -1;
                    }
                }

                trans.remove(selectedNodeIndex);
                repaint();
//                nodes.remove(pressedButton);
//                nodesLabels.remove(temp);
            }
        });
        popup.add(deleteItem);
        //------------------------------------------------------------

        // Add start and end Menu items
        isStart = new JCheckBoxMenuItem("set as start node");
        isEnd = new JCheckBoxMenuItem("set as final node");
        isStart.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedNodeIndex = getNodeIndex(pressedNodeNumber);
                if (isStart.isSelected()) {
                    if (startNode != -1) {
                        int prevStartNodeIndex = getNodeIndex(startNode);
                        nodes.get(prevStartNodeIndex).setStatus(nodes.get(prevStartNodeIndex).getStatus() - START);
                        startNode = -1;
                    }
                    nodes.get(selectedNodeIndex).setStatus(nodes.get(selectedNodeIndex).getStatus() + START);
                    startNode = pressedNodeNumber;

                } else {
                    nodes.get(selectedNodeIndex).setStatus(nodes.get(selectedNodeIndex).getStatus() - START);
                    startNode = -1;
                }
                repaint();
            }
        });
        isEnd.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                int selectedNodeIndex = getNodeIndex(pressedNodeNumber);
                if (isEnd.isSelected()) {
                    nodes.get(selectedNodeIndex).setStatus(nodes.get(selectedNodeIndex).getStatus() + FINAL);
                } else {
                    nodes.get(selectedNodeIndex).setStatus(nodes.get(selectedNodeIndex).getStatus() - FINAL);
                }
                repaint();
            }
        });
        popup.add(isStart);
        popup.add(isEnd);
        //------------------------------------------------------------

        // Add add transition submenu
        trans = new JMenu("Add transition to node");
        popup.add(trans);

    }

    private void showPopup(MouseEvent e) {

        popup.show(e.getComponent(), e.getX(), e.getY());
        if (startNode == pressedNodeNumber) {
            isStart.setSelected(true);
        } else {
            isStart.setSelected(false);
        }
        boolean finalNodeFlag = false;

        int selectedNodeIndex = getNodeIndex(pressedNodeNumber);
        finalNodeFlag = nodes.get(selectedNodeIndex).isFinal();

        isEnd.setSelected(finalNodeFlag);

        //set all checkboxes to false
        for (int j = 0; j < nodes.size(); j++) {
            nodes.get(j).getCheckbox().setSelected(false);
        }

        for (int i = 0; i < transitionsList.size(); i++) {
            int u = transitionsList.get(i).getFrom().getNodeNumber();
            if (u != pressedNodeNumber) {
                continue;
            }
            Node v = transitionsList.get(i).getTo();
            v.getCheckbox().setSelected(true);
        }

    }

    @Override
    protected void paintComponent(Graphics grphcs) {
        super.paintComponent(grphcs); //To change body of generated methods, choose Tools | Templates.

        drawArrows(transitionsList, grphcs);
        drawStart();
        drawFinals();

    }

    private void drawArrows(ArrayList<Transition> transitionsList, Graphics graphics) {

        graphics2D = (Graphics2D) graphics;
        AffineTransform origin = graphics2D.getTransform();

        for (int i = 0; i < transitionsList.size(); i++) {
            graphics2D.setStroke(new BasicStroke(3f));
            graphics2D.setColor(JFlapFrame.BABY_POWDER);
            Point a, b, c, center, arrowCenter;
            Point from = new Point((int) transitionsList.get(i).getFrom().getNodeButton().getLocation().getX() + 25, (int) transitionsList.get(i).getFrom().getNodeButton().getLocation().getY() + 25);
            Point to = new Point((int) transitionsList.get(i).getTo().getNodeButton().getLocation().getX() + 25, (int) transitionsList.get(i).getTo().getNodeButton().getLocation().getY() + 25);
            center = getCenter(from, to);

            if (transitionsList.get(i).getFrom() != transitionsList.get(i).getTo()) {
                arrowCenter = getCenter(center, to);
                graphics2D.drawLine(from.x, from.y, to.x, to.y);

            } else {
                arrowCenter = new Point(from.x - 1, from.y - 60);
                graphics2D.drawOval(center.x - 22, center.y - 60, 40, 50);
                graphics2D.setColor(JFlapFrame.MAASTRICHT_BLUE);
            }
            a = new Point(0, -8);
            b = new Point(12, 8);
            c = new Point(-12, 8);

            int direction = (getAngle(new Point(from.x, from.y), new Point(to.x, to.y)) >= 0) ? 1 : -1;
            double PI = direction * Math.PI;

            Polygon arrowHead = new Polygon();

            arrowHead.addPoint((int) arrowCenter.getX() + (int) a.getX(), (int) arrowCenter.getY() + (int) a.getY());
            arrowHead.addPoint((int) arrowCenter.getX() + (int) b.getX(), (int) arrowCenter.getY() + (int) b.getY());
            arrowHead.addPoint((int) arrowCenter.getX() + (int) c.getX(), (int) arrowCenter.getY() + (int) c.getY());
            graphics2D.rotate(PI + Math.PI / 2 + getAngle(new Point(from.x, from.y), new Point(to.x, to.y)), arrowCenter.x, arrowCenter.y);
            graphics2D.fillPolygon(arrowHead);

            //Draw Transition String
            graphics2D.setTransform(origin);
            double angle = getAngle(new Point(from.x, from.y), new Point(to.x, to.y));
            double angleDeg = Math.toDegrees(angle);
            if (angleDeg > 90 || angleDeg < -90) {
                direction = 1;
            } else {
                direction = 0;
            }

            graphics2D.rotate(((direction == 1) ? PI : 0) + getAngle(new Point(from.x, from.y), new Point(to.x, to.y)), arrowCenter.x, arrowCenter.y);
            arrowCenter.y += (transitionsList.get(i).getFrom() != transitionsList.get(i).getTo()) ? 50 : -20;
            graphics2D.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
            graphics2D.setColor(Color.black);

            if (transitionsList.get(i).getFrom() == transitionsList.get(i).getTo()) {
                graphics2D.drawString(String.join(",", String.valueOf(transitionsList.get(i).getTransition()).split("")), arrowCenter.x, arrowCenter.y);
            } else {
                for (int j = 0; j < transitionsList.get(i).getTransition().length; j++) {
                    graphics2D.drawString(String.valueOf(transitionsList.get(i).getTransition()[j]), arrowCenter.x, arrowCenter.y);
                    arrowCenter.y += 20;
                }
            }

            graphics2D.setTransform(origin);

        }

    }

    double getAngle(Point a, Point b) {
        return Math.atan2(a.y - b.y, a.x - b.x);
    }

    Point getCenter(Point a, Point b) {
        return (new Point((a.x + b.x) / 2, (a.y + b.y) / 2));
    }

    public int getNodesNumber() {
        return nodes.size();
    }

    public void refreshNodesColors(int[] colors) {
        Color color[] = new Color[]{JFlapFrame.MAASTRICHT_BLUE, JFlapFrame.BRIGHT_YELLOW};
        repaint();
        for (int i = 0; i < nodes.size(); i++) {
            int nodeNum = nodes.get(i).getNodeNumber();
            nodes.get(i).getNodeButton().setBorder(new RoundedBorder(50, color[colors[nodeNum]]));
            repaint();
        }
    }

    private void drawStart() {
        if (startNode != -1) {

            AffineTransform origin = graphics2D.getTransform();

            Point a, b, c, center;
            a = new Point(0, -15);
            b = new Point(25, 15);
            c = new Point(-25, 15);

            center = nodes.get(getNodeIndex(startNode)).getNodeButton().getLocation();
            center.x -= 15;
            center.y += 25;

            Polygon arrowHead = new Polygon();
            arrowHead.addPoint(center.x + a.x, center.y + a.y);
            arrowHead.addPoint(center.x + b.x, center.y + b.y);
            arrowHead.addPoint(center.x + c.x, center.y + c.y);
            graphics2D.rotate(Math.PI / 2, center.x, center.y);
            graphics2D.setColor(JFlapFrame.MAASTRICHT_BLUE);
            graphics2D.fillPolygon(arrowHead);
            graphics2D.setTransform(origin);
        }
    }

    private int getNodeIndex(int n) {
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i).getNodeNumber() == n) {
                return i;
            }
        }
        return -1;
    }

    private void drawFinals() {
        for (int i = 0; i < nodes.size(); i++) {
            if (!nodes.get(i).isFinal()) {
                continue;
            }
            Point center = nodes.get(i).getNodeButton().getLocation();
            center.x -= 5.75;
            center.y -= 5.75;
            graphics2D.setColor(JFlapFrame.BABY_POWDER);
            graphics2D.fillOval(center.x, center.y, 60, 60);
        }

    }

    public void cleanUp() {
        for (int i = 0; i < nodes.size(); i++) {
            Container parent = nodes.get(i).getNodeLabel().getParent();
            parent.remove(nodes.get(i).getNodeLabel());
            parent = nodes.get(i).getNodeButton().getParent();
            parent.remove(nodes.get(i).getNodeButton());
            parent.repaint();
        }
        nodes.clear();
        transitionsList.clear();
        trans.removeAll();
        nodesCounter = 0;
        queuedNumbers.clear();
        startNode = -1;
        this.repaint();
    }

    private void intializeMl() {
        ml = new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent me) {

            }

            @Override
            public void mousePressed(MouseEvent me) {
                if (me.getButton() == MouseEvent.BUTTON1 && me.getClickCount() > 1) {
                    if (me.getSource() instanceof ForDrawingPanel) {
                        Point p = me.getPoint();
                        addNode(p);
                    }

                } else if (me.getButton() == MouseEvent.BUTTON3) {
                    if (me.getSource() instanceof JButton) {
                        pressedButton = (JButton) me.getSource();
                        for (int i = 0; i < nodes.size(); i++) {
                            if (pressedButton == nodes.get(i).getNodeButton()) {
                                pressedNodeNumber = nodes.get(i).getNodeNumber();
                                break;
                            }
                        }
                        showPopup(me);
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent me) {
            }

            @Override
            public void mouseEntered(MouseEvent me) {
            }

            @Override
            public void mouseExited(MouseEvent me) {
            }

        };
    }
}
