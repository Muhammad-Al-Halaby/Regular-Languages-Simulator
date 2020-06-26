package jflap;

public class Transition {

    Node from, to;
    private char[] transition;

    private final char epsilon = 'ϵ';

    public Transition(Node from, Node to, char[] transition) {
        this.from = from;
        this.to = to;

        for (int i = 0; i < transition.length; i++) {
            if (transition[i] == ' ') {
                transition[i] = epsilon;
            }
        }
        if (transition.length == 0) {
            transition = new char[]{epsilon};
        }

        this.transition = transition;
    }

    public Node getFrom() {
        return from;
    }

    public Node getTo() {
        return to;
    }

    public char[] getTransition() {
        return transition;
    }

    public void setFrom(Node from) {
        this.from = from;
    }

    public void setTo(Node to) {
        this.to = to;
    }

    public void setTransition(char[] transition) {
        this.transition = transition;
    }
}
