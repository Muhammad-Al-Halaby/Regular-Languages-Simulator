package jflap;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class ControlPanel extends JPanel implements ActionListener {

    private JFlapFrame container;
    private ForDrawingPanel drawingPanel;
    private StatesScrollPanel statesScrollPanel;
    private JButton fastRun;
    private JButton simulate;
    private JButton reset;
    private JButton freeze;
    private JButton thaw;
    private JButton trace;
    private JButton remove;
    private JButton clearPanels;
    private JButton exit;
    private JButton credits;
    private static int nodesStatus[];

    public ControlPanel(JFlapFrame container, ForDrawingPanel fdp, StatesScrollPanel ssp) {
        this.container = container;
        setSize(container.getWidth(), (int) (container.getHeight() * (1.0 / 20.0)));
        this.drawingPanel = fdp;
        this.statesScrollPanel = ssp;
        Observer.simulationMode = false;
        init();

    }

    public static int getNodesStatus(int i) {
        return nodesStatus[i];
    }

    private void init() {
        fastRun = new JButton("Fast-Run");
        fastRun.addActionListener(this);
        add(fastRun);
        simulate = new JButton("Simulate");
        simulate.addActionListener(this);
        add(simulate);
        reset = new JButton("Reset");
        reset.addActionListener(this);
        add(reset);
        freeze = new JButton("Freeze");
        freeze.addActionListener(this);
        add(freeze);
        thaw = new JButton("Thaw");
        thaw.addActionListener(this);
        add(thaw);
        trace = new JButton("Trace");
        trace.addActionListener(this);
        add(trace);
        remove = new JButton("Remove");
        remove.addActionListener(this);
        add(remove);
        clearPanels = new JButton("Clear");
        clearPanels.addActionListener(this);
        add(clearPanels);
        credits = new JButton("Credits");
        credits.addActionListener(this);
        add(credits);
        exit = new JButton("Exit");
        exit.addActionListener(this);
        add(exit);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        boolean hasFinal = false;
        ArrayList<Node> nodes = drawingPanel.getNodes();
        ArrayList<Transition> trans = drawingPanel.getTransitionsList();
        ArrayList<Edge> edges = new ArrayList<>();
        int n = nodes.size();
        nodesStatus = new int[n];

        for (int i = 0; i < n; i++) {
            nodesStatus[i] = nodes.get(i).getStatus();
            if (nodes.get(i).isFinal()) {
                hasFinal = true;
            }
        }

        for (int i = 0; i < trans.size(); i++) {
            int u = trans.get(i).getFrom().getNodeNumber();
            int v = trans.get(i).getTo().getNodeNumber();
            char[] transitions = trans.get(i).getTransition();
            for (int j = 0; j < transitions.length; j++) {
                char t = transitions[j];
                edges.add(new Edge(u, v, t));
            }
        }

        if (ae.getSource().equals(fastRun)) {
            if (n == 0) {
                JOptionPane.showMessageDialog(null, "You must add some nodes first", "Alert!", JOptionPane.ERROR_MESSAGE);
                return;
            } else if (drawingPanel.getStartNode() == -1) {
                JOptionPane.showMessageDialog(null, "You must define a start node", "Alert!", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Observer.inputFR = JOptionPane.showInputDialog(null, "Input", "Enter The Input", JOptionPane.INFORMATION_MESSAGE);
            if (Observer.inputFR == null) {
                return;
            } else if (Observer.inputFR.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Input can't be empty", "Alert!", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String tempInput = Observer.input;
            Observer.input = Observer.inputFR;

            if (!Observer.simulationMode) {
                Observer.FSM = new FSMGraph(n, nodesStatus, edges, Observer.input);
            }

            ArrayList<FullState> s = Observer.FSM.fastRun(Observer.input);
            int numberAccepted = s.size();
            if (numberAccepted == 0) {
                JOptionPane.showMessageDialog(null, "The input was rejected.", "Results", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, String.valueOf(numberAccepted) + " configuration" + ((numberAccepted == 1) ? "" : "s") + " accepted, and\nother possibilities are exhausted.");
                int input = JOptionPane.showConfirmDialog(null, "Show Accepted Path" + ((numberAccepted == 1) ? "" : "s") + "?", "Select an Option...", JOptionPane.YES_NO_OPTION);
                if (input == 0) {
                    for (int i = 0; i < numberAccepted; i++) {
                        new TracebackFrame(s.get(i));
                    }
                }
            }

            Observer.input = tempInput;

        } else if (ae.getSource().equals(simulate)) {
            if (n == 0) {
                JOptionPane.showMessageDialog(null, "You must add some nodes first", "Alert!", JOptionPane.ERROR_MESSAGE);

            } else if (drawingPanel.getStartNode() == -1) {
                JOptionPane.showMessageDialog(null, "You must define a start node", "Alert!", JOptionPane.ERROR_MESSAGE);
            } else if (!Observer.simulationMode) {
                Observer.input = JOptionPane.showInputDialog(null, "Input", "Enter The Input", JOptionPane.INFORMATION_MESSAGE);
                if (Observer.input == null) {
                    return;
                } else if (Observer.input.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Input can't be empty", "Alert!", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                drawingPanel.removeMouseListeners();
                Observer.FSM = new FSMGraph(n, nodesStatus, edges, Observer.input);
                Observer.simulationMode = true;
            }

            if (Observer.simulationMode) {
                boolean x = Observer.FSM.simulate();
                statesScrollPanel.refreshStatesScrollPanel();
                drawingPanel.refreshNodesColors(Observer.FSM.getColors());
                if (!x) {
                    Observer.simulationMode = false;
                    JOptionPane.showMessageDialog(null, "Simulation has reached an end!", "Alert!", JOptionPane.INFORMATION_MESSAGE);
                    cleanUp();
                }
            }

        } else if (ae.getSource().equals(reset)) {
            cleanUp();
        } else if (ae.getSource().equals(freeze)) {
            ArrayList<Integer> statesIDs = StatesScrollPanel.getSelectedStatesIDs();

            if (statesIDs.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Select at least one configuration!", "No Configuration Selected", 0);
                return;
            }

            for (int i = 0; i < statesIDs.size(); i++) {
                int id = statesIDs.get(i);
                Observer.FSM.freeze(id);
                statesScrollPanel.getStatePanelByID(id).refreshBorder(true);
            }
        } else if (ae.getSource().equals(thaw)) {
            ArrayList<Integer> statesIDs = StatesScrollPanel.getSelectedStatesIDs();

            if (statesIDs.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Select at least one configuration!", "No Configuration Selected", 0);
                return;
            }

            for (int i = 0; i < statesIDs.size(); i++) {
                int id = statesIDs.get(i);
                Observer.FSM.unFreeze(id);
                statesScrollPanel.getStatePanelByID(id).refreshBorder(false);
            }
        } else if (ae.getSource().equals(remove)) {
            ArrayList<Integer> statesIDs = StatesScrollPanel.getSelectedStatesIDs();

            if (statesIDs.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Select at least one configuration!", "No Configuration Selected", 0);
                return;
            }

            for (int i = 0; i < statesIDs.size(); i++) {
                Observer.FSM.remove(statesIDs.get(i));
                statesScrollPanel.refreshStatesScrollPanel();
            }
        } else if (ae.getSource().equals(trace)) {
            ArrayList<Integer> statesIDs = StatesScrollPanel.getSelectedStatesIDs();

            if (statesIDs.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Select at least one configuration!", "No Configuration Selected", 0);
                return;
            }

            for (int i = 0; i < statesIDs.size(); i++) {
                int id = statesIDs.get(i);
                FullState s = statesScrollPanel.getFullStateByID(id);
                new TracebackFrame(s);
            }
        } else if (ae.getSource().equals(clearPanels)) {
            cleanUp();
            drawingPanel.cleanUp();
        } else if (ae.getSource().equals(credits)) {
            JOptionPane.showMessageDialog(null,
                    "-------------------------------------------\n"
                    + "|               Team Work                  |\n"
                    + "|          Mohamed Almahdy         |\n"
                    + "|        Muhammad Al-Halaby      |\n"
                    + "|              Osama Maani               |\n"
                    + "|                     Ammar                    |\n"
                    + "|                      Ismail                      |\n"
                    + "--------------------------------------------", "Team Memmbers Names" + "", JOptionPane.INFORMATION_MESSAGE);
        } else if (ae.getSource().equals(exit)) {
            System.exit(0);
        }
    }

    private void cleanUp() {
        Observer.simulationMode = false;
        drawingPanel.removeMouseListeners();
        drawingPanel.addMouseListeners();
        statesScrollPanel.refreshStatesScrollPanel();
        drawingPanel.refreshNodesColors(new int[drawingPanel.getNodesNumber()]);
    }

}
