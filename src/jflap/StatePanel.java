package jflap;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.MatteBorder;

public class StatePanel extends JPanel implements MouseListener {

    JLabel node;
    JPanel currentState;
    JLabel finished, rest;
    private String stateName;
    int stateID;
    int nodeStatus;
    boolean selected = false;
    boolean frozen = false;
    public static Border[] stateBorders;

    public StatePanel() {
        setLayout(new GridLayout(0, 2));
        stateBorders = new Border[]{new CompoundBorder(new MatteBorder(2, 2, 2, 2, Color.GRAY), new MatteBorder(4, 4, 4, 4, Color.GRAY)),
            new CompoundBorder(new MatteBorder(2, 2, 2, 2, Color.GRAY), new MatteBorder(4, 4, 4, 4, JFlapFrame.ICY_BLUE)),
            new CompoundBorder(new MatteBorder(2, 2, 2, 2, JFlapFrame.ROSE_MADDER), new MatteBorder(4, 4, 4, 4, getBackground())),
            new CompoundBorder(new MatteBorder(2, 2, 2, 2, JFlapFrame.ROSE_MADDER), new MatteBorder(4, 4, 4, 4, JFlapFrame.ICY_BLUE))};
        setBorder(stateBorders[0]);

        node = new JLabel() {
            @Override
            protected void paintComponent(Graphics grphcs) {
                super.paintComponent(grphcs);
                Graphics2D g2 = (Graphics2D) grphcs;
                g2.setColor(JFlapFrame.MAASTRICHT_BLUE);
                g2.fillOval(35, 2, 30, 30);
                g2.setColor(JFlapFrame.BABY_POWDER);

                if (stateName != null) {
                    g2.setFont(new Font(Font.DIALOG, Font.BOLD, 12 / (stateName.length() / 2)));
                    g2.drawString(stateName, 42, 20);
                }

                if (nodeStatus == 1 || nodeStatus == 3) {
                    Polygon p = new Polygon();
                    Point a, b, c;
                    a = new Point(15, 2);
                    b = new Point(35, 15);
                    c = new Point(15, 30);
                    p.addPoint(a.x, a.y);
                    p.addPoint(b.x, b.y);
                    p.addPoint(c.x, c.y);
                    g2.setColor(JFlapFrame.MAASTRICHT_BLUE);
                    g2.fillPolygon(p);

                }
                if (nodeStatus == 2 || nodeStatus == 3) {
                    g2.setColor(JFlapFrame.BABY_POWDER);
                    g2.setStroke(new BasicStroke(4f));
                    g2.drawOval(35, 2, 30, 30);

                }
            }

        };

        node.setPreferredSize(new Dimension(100, 50));
        currentState = new JPanel(new FlowLayout(SwingConstants.LEADING, 0, 0));
        finished = new JLabel();
        rest = new JLabel();
        finished.setForeground(Color.GRAY);
        finished.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 24));
        rest.setForeground(JFlapFrame.MAASTRICHT_BLUE);
        rest.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 24));
        currentState.add(node);
        currentState.add(finished);
        currentState.add(rest);

        add(currentState);

        this.addMouseListener(this);
    }

    public StatePanel(String stateName, int nodeStatus) {
        this();
        removeMouseListener(this.getMouseListeners()[0]);
        setBorder(StatePanel.stateBorders[0]);
        this.stateName = stateName;
        this.nodeStatus = nodeStatus;
        repaint();
    }

    void refreshStatePanel(int stateID, int nodeStatus, String stateName, String[] currentStateString) {
        this.stateID = stateID;
        this.stateName = stateName;
        this.nodeStatus = nodeStatus;
        finished.setText(currentStateString[0]);
        rest.setText(currentStateString[1]);
        currentState.setBackground(this.getBackground());
        stateBorders[2] = new CompoundBorder(new MatteBorder(2, 2, 2, 2, JFlapFrame.ROSE_MADDER), new MatteBorder(4, 4, 4, 4, getBackground()));
        repaint();
    }

    public void refreshBorder(boolean frozen) {
        if (frozen) {
            this.frozen = true;
        } else {
            this.frozen = false;
        }

        int s = (frozen ? 1 : 0) + (selected ? 2 : 0);
        this.setBorder(stateBorders[s]);
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        stateBorders[2] = new CompoundBorder(new MatteBorder(2, 2, 2, 2, JFlapFrame.ROSE_MADDER), new MatteBorder(4, 4, 4, 4, getBackground()));
        if (!selected) {
            StatesScrollPanel.selectState(stateID);
            selected = true;
            refreshBorder(frozen);
        } else {
            StatesScrollPanel.unSelectState(stateID);
            selected = false;
            refreshBorder(frozen);
        }
    }

    @Override
    public void mousePressed(MouseEvent me) {
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
}
