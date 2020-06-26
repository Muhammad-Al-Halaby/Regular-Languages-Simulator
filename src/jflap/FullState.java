package jflap;

import java.util.ArrayList;
import java.util.Comparator;

class FullState implements Comparable<FullState> {

    private ArrayList<Integer> path;
    private ArrayList<Integer> stateLength;
    private StatePanel statePanel;
    private boolean dead, finalState, frozen;
    private int stateID;
    public static int stateCounter;

    public FullState(Integer u) {
        this.path = new ArrayList<>();
        this.stateLength = new ArrayList<>();
        this.statePanel = new StatePanel();
        this.dead = false;
        this.finalState = false;
        this.frozen = false;
        this.stateID = stateCounter++;
        addStateTransition(u, 0);
    }

    public FullState(ArrayList<Integer> path, ArrayList<Integer> stateLength) {
        this.path = new ArrayList<>(path);
        this.stateLength = new ArrayList<>(stateLength);
        this.statePanel = new StatePanel();
        this.dead = false;
        this.finalState = false;
        this.frozen = false;
        this.stateID = stateCounter++;
    }

    public FullState(ArrayList<Integer> path, Integer stateLength) {
        this.path = new ArrayList<>(path);
        this.stateLength = new ArrayList<>();
        this.stateLength.add(stateLength);
    }

    public void addStateTransition(Integer u, Integer len) {
        this.path.add(u);
        this.stateLength.add(len);
    }

    public Integer getCurrentStateLength() {
        return stateLength.get(stateLength.size() - 1);
    }

    public Integer getCurrentState() {
        return path.get(path.size() - 1);
    }

    public int getSpecificState(int i) {
        return path.get(i);
    }

    public String[] getSpecificState(String input, int i) {
        if (stateLength.isEmpty()) {
            return new String[]{"", ""};
        }
        int len = stateLength.get(i);
        return new String[]{input.substring(0, len), input.substring(len)};
    }

    public String[] getCurrentState(String input) {
        if (stateLength.isEmpty()) {
            return new String[]{"", ""};
        }
        int len = stateLength.get(stateLength.size() - 1);
        return new String[]{input.substring(0, len), input.substring(len)};
    }

    public ArrayList<Integer> getPath() {
        return path;
    }

    public ArrayList<Integer> getStateLength() {
        return stateLength;
    }

    public StatePanel getStatePanel() {
        return statePanel;
    }

    public void freeze() {
        frozen = true;
    }

    public void unFreeze() {
        frozen = false;
    }

    public boolean isFrozen() {
        return frozen;
    }

    public void kill() {
        dead = true;
    }

    public boolean isDead() {
        return dead;
    }

    public boolean isFinal() {
        return finalState;
    }

    public void setFinal() {
        this.finalState = true;
    }

    public int getStateID() {
        return stateID;
    }

    @Override
    public int compareTo(FullState s2) {
        if (this.isFrozen() && !s2.isFrozen()) {
            return -1;
        } else if (!this.isFrozen() && s2.isFrozen()) {
            return 1;
        } else if (this.getCurrentState() == s2.getCurrentState()) {
            return 0;
        }
        return (this.getCurrentState() < s2.getCurrentState()) ? -1 : 1;
    }

}

class StatesComparator implements Comparator<FullState> {

    @Override
    public int compare(FullState s1, FullState s2) {
        if (s1.getPath().equals(s2.getPath()) && s1.getCurrentStateLength() == s2.getCurrentStateLength()) {
            return 0;
        }
        return (s1.getCurrentStateLength() < s2.getCurrentStateLength()) ? -1 : 1;
    }
}
