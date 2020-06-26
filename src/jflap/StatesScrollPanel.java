package jflap;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.JPanel;

public class StatesScrollPanel extends JPanel {

    private ArrayList<FullState> states;
    private static ArrayList<Integer> selectedStatesIDs;

    public static void selectState(int id) {
        selectedStatesIDs.add(id);
    }

    public static void unSelectState(int id) {
        selectedStatesIDs.remove(new Integer(id));
    }
    JFlapFrame container;

    public StatesScrollPanel(JFlapFrame container) {
        this.container = container;
        setSize(container.getWidth(), (int) (container.getHeight() * (3.0 / 15)) - 20);
        setLayout(new GridLayout(0, 4));
        setBackground(JFlapFrame.BABY_POWDER);
        selectedStatesIDs = new ArrayList<>();
    }

    public void refreshStatesScrollPanel() {
        this.removeAll();
        selectedStatesIDs.clear();
        this.revalidate();
        this.repaint();
        if (!Observer.simulationMode) {
            return;
        }

        states = Observer.FSM.getStates();
        for (int i = 0; i < states.size(); i++) {
            StatePanel sp = states.get(i).getStatePanel();

            if (states.get(i).isDead()) {
                sp.setBackground(Color.decode("#DB7093"));
            } else if (states.get(i).isFinal()) {
                sp.setBackground(Color.decode("#00FF7F"));
            } else {
                sp.setBackground(Color.decode("#6495ED"));
            }

            //TODO
            if (states.get(i).isFrozen()) {
                sp.refreshBorder(true);
            }

            int lstNode = states.get(i).getCurrentState();
            sp.refreshStatePanel(states.get(i).getStateID(), ControlPanel.getNodesStatus(lstNode), "s" + states.get(i).getCurrentState(), states.get(i).getCurrentState(Observer.input));
            add(sp);

            this.revalidate();
            this.repaint();
        }
    }

    public static ArrayList<Integer> getSelectedStatesIDs() {
        return selectedStatesIDs;
    }

    public StatePanel getStatePanelByID(int id) {
        for (int i = 0; i < states.size(); i++) {
            if (states.get(i).getStateID() == id) {
                return states.get(i).getStatePanel();
            }
        }
        return null;
    }

    public FullState getFullStateByID(int id) {
        for (int i = 0; i < states.size(); i++) {
            if (states.get(i).getStateID() == id) {
                return states.get(i);
            }
        }
        return null;
    }

}
