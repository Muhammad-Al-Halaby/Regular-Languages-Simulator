package jflap;

import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class TracebackFrame extends JFrame {

    ArrayList<Integer> path;
    ArrayList<Integer> stateLength;
    JPanel container;
    JScrollPane scroller;
    static String currNodeName;

    public TracebackFrame(FullState state) {
        setTitle("Traceback");
        container = new JPanel();
        scroller = new JScrollPane(container);
        setSize(600, 800);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        int cnt = state.getPath().size();
        container.setLayout(new GridLayout(0, 1));

        add(scroller);
        for (int i = 0; i < cnt; i++) {
            int currNode = state.getSpecificState(i);
            currNodeName = "s" + currNode;
            StatePanel sp = new StatePanel(currNodeName, ControlPanel.getNodesStatus(currNode));
            sp.finished.setText(state.getSpecificState(Observer.input, i)[0]);
            sp.rest.setText(state.getSpecificState(Observer.input, i)[1]);
            container.add(sp);
            container.revalidate();
            container.repaint();
        }
        scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        setVisible(true);
        revalidate();
        repaint();
    }

}
