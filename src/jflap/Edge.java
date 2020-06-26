package jflap;

class Edge {

    private int from, to;
    private char transition;

    public Edge(int from, int to, char transition) {
        this.from = from;
        this.to = to;
        this.transition = transition;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public char getTransition() {
        return transition;
    }

}
