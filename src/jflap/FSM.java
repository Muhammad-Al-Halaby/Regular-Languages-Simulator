package jflap;

import java.util.*;

class FSMGraph {

    private final int M = (int) 3e5 + 10, START = 1, FINAL = 2, NORMAL = 0, PROCESSING = 1, DEAD = 2;

    private final char epsilon = 'ϵ';

    private int n, m, ne, vid, head[], status[], vis[], nxt[], to[], color[], initial;

    private String input;

    private char transition[];

    private ArrayList<Integer> directNodes;

    private Queue<FullState> q, qfastRun;

    private Set<FullState> currentStates, currentStatesFastRun;

    private boolean accepted, simulationStarted;

    public FSMGraph(int n, int nodesStatus[], ArrayList<Edge> elist, String input) {
        this.n = n;
        this.input = input;
        ne = 0;
        vid = 0;
        FullState.stateCounter = 0;
        accepted = false;
        simulationStarted = false;

        head = new int[n];
        status = new int[n];
        vis = new int[n];
        color = new int[n];
        nxt = new int[M];
        to = new int[M];
        transition = new char[M];
        q = new LinkedList<>();
        qfastRun = new LinkedList<>();
        currentStates = new TreeSet<>(new StatesComparator());
        currentStatesFastRun = new TreeSet<>(new StatesComparator());

        for (int i = 0; i < n; i++) {
            head[i] = -1;
            color[i] = NORMAL;
            status[i] = nodesStatus[i];
            if (status[i] == 1 || status[i] == 3) {
                initial = i;
            }
        }

        for (Edge e : elist) {
            addEdge(e.getFrom(), e.getTo(), e.getTransition());
        }

        epsilon();

    }

    private void initializeQueue(int u, boolean x) {
        if (!x) {
            q.add(new FullState(u));
        } else {
            qfastRun.add(new FullState(u));
        }
        ArrayList<Integer> newPath = new ArrayList<>();
        newPath.add(u);
        visitState(newPath, 0, x);
        setColor(u, PROCESSING);
    }

    private boolean isStateVisited(ArrayList<Integer> path, int len, boolean x) {
        if (!x) {
            return currentStates.contains(new FullState(path, len));
        }
        return currentStatesFastRun.contains(new FullState(path, len));
    }

    private void visitState(ArrayList<Integer> path, int len, boolean x) {
        if (!x) {
            currentStates.add(new FullState(path, len));
        } else {
            currentStatesFastRun.add(new FullState(path, len));
        }
    }

    private void unVisitState(FullState s, boolean x) {
        if (!x) {
            currentStates.remove(s);
        } else {
            currentStatesFastRun.remove(s);
        }
    }

    private void epsilon() {
        directNodes = new ArrayList<Integer>();

        for (int u = 0; u < n; u++) {
            vid++;
            dfs(u);
            for (int v : directNodes) {
                addEdge(u, v, epsilon);
            }
            directNodes.clear();
        }
    }

    private void dfs(int u) {
        if (vis[u] == vid) {
            return;
        }

        vis[u] = vid;

        for (int k = head[u]; ~k != 0; k = nxt[k]) {
            int v = to[k];
            char t = transition[k];
            if (vis[u] != vid && t == epsilon) {
                dfs(v);
                directNodes.add(v);
            }
        }
    }

    private void addEdge(int f, int t, char c) {
        to[ne] = t;
        transition[ne] = c;
        nxt[ne] = head[f];
        head[f] = ne++;
    }

    private boolean isFinal(int u) {
        return status[u] == START + FINAL || status[u] == FINAL;
    }

    private void setColor(int u, int c) {
        color[u] = c;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public boolean simulate() {
        boolean x = false;
        if (!simulationStarted) {
            initializeQueue(initial, x);
            simulationStarted = true;
            return true;
        }

        if (q.size() == 0) {
            return false;
        }

        ArrayList<FullState> statesToBeRemoved = new ArrayList<FullState>();

        //reset all nodes colors to normal
        for (int i = 0; i < n; i++) {
            setColor(i, NORMAL);
        }

        int sz = q.size();
        while (sz > 0) {
            sz--;

            FullState state = q.poll();

            if (state.isFrozen()) {
                q.add(state);
                continue;
            }

            int u = state.getCurrentState();
            int stateLength = state.getCurrentStateLength();

            ArrayList<Integer> currentStatePath = state.getPath();

            //if state is Dead just remove it from the queue, it's done already!
            if (state.isDead()) {
                continue;
            }

            //prepare to get removed
            statesToBeRemoved.add(new FullState(currentStatePath, stateLength));

            if (state.isFinal()) {
                if (isFinal(u)) {
                    accepted = true;
                }
                continue;
            }

            boolean dead = true;

            for (int k = head[u]; ~k != 0; k = nxt[k]) {
                int v = to[k];
                char t = transition[k];

                if (t == epsilon || (stateLength < input.length() && t == input.charAt(stateLength))) {
                    int newStateLength = stateLength + ((t == epsilon) ? 0 : 1);
                    ArrayList<Integer> newStatePath = new ArrayList<>(currentStatePath);
                    newStatePath.add(v);
                    if (isStateVisited(newStatePath, newStateLength, x)) {
                        continue;
                    }

                    //u can't be a dead node!
                    dead = false;

                    // color v as processing
                    setColor(v, PROCESSING);

                    //update the state
                    FullState newState = new FullState(state.getPath(), state.getStateLength());
                    newState.addStateTransition(v, newStateLength);

                    if (isFinal(v) && newStateLength == input.length()) {
                        newState.setFinal();
                    }
                    visitState(newStatePath, newStateLength, x);
                    q.add(newState);
                }
            }//end of for loop

            if (dead) {
                //set state as dead
                state.kill();
                //add it back to the queue
                q.add(state);
            }
        }//end of while loop

        for (FullState s : statesToBeRemoved) {
            unVisitState(s, x);
        }

        if (q.size() == 0) {
            return false;
        }

        return true;
    }

    public void freeze(int stateID) {
        for (FullState s : q) {
            if (s.getStateID() == stateID) {
                s.freeze();
                break;
            }
        }
    }

    public void unFreeze(int stateID) {
        for (FullState s : q) {
            if (s.getStateID() == stateID) {
                s.unFreeze();
                break;
            }
        }
    }

    public void remove(int stateID) {
        FullState toBeRemoved = null;
        int i = 0;
        for (FullState s : q) {
            if (s.getStateID() == stateID) {
                toBeRemoved = s;
                break;
            }
            i++;
        }

        while (i > 0) {
            q.add(q.poll());
            i--;
        }

        q.poll();

        unVisitState(new FullState(toBeRemoved.getPath(), toBeRemoved.getCurrentStateLength()), false);

    }

    public ArrayList<FullState> fastRun(String input) {
        ArrayList<FullState> ans = new ArrayList<>();
        boolean x = true;
        initializeQueue(initial, x);

        int sz = qfastRun.size();

        for (; !(qfastRun.isEmpty()); sz = qfastRun.size()) {

            ArrayList<FullState> statesToBeRemoved = new ArrayList<FullState>();

            while (sz > 0) {
                sz--;
                FullState state = qfastRun.poll();

                int u = state.getCurrentState();
                int stateLength = state.getCurrentStateLength();

                ArrayList<Integer> currentStatePath = state.getPath();

                //prepare to get removed
                statesToBeRemoved.add(new FullState(currentStatePath, stateLength));

                if (state.isFinal()) {
                    if (isFinal(u)) {
                        ans.add(state);
                    }
                    continue;
                }

                boolean infinity = false;

                for (int k = head[u]; ~k != 0; k = nxt[k]) {
                    int v = to[k];
                    char t = transition[k];

                    if (t == epsilon || (stateLength < input.length() && t == input.charAt(stateLength))) {
                        int newStateLength = stateLength + ((t == epsilon) ? 0 : 1);

                        //check for cycles
                        int l = 0, r = currentStatePath.size() - 1;
                        while (l <= r) {
                            int mid = (l + r) >> 1;
                            if (state.getStateLength().get(mid) < newStateLength) {
                                l = mid + 1;
                            } else {
                                r = mid - 1;
                            }
                        }

                        int startIdx = l;
                        for (int i = startIdx; i < currentStatePath.size(); i++) {
                            if (currentStatePath.get(i) == v) {
                                infinity = true;
                                break;
                            }
                        }

                        if (infinity) {
                            break;
                        }

                        ArrayList<Integer> newStatePath = new ArrayList<>(currentStatePath);
                        newStatePath.add(v);
                        if (isStateVisited(newStatePath, newStateLength, x)) {
                            continue;
                        }

                        //update the state
                        FullState newState = new FullState(state.getPath(), state.getStateLength());
                        newState.addStateTransition(v, newStateLength);

                        if (isFinal(v) && newStateLength == input.length()) {
                            newState.setFinal();
                        }
                        visitState(newStatePath, newStateLength, x);
                        qfastRun.add(newState);
                    }
                }//end of inner for loop

            }//end of while loop

            for (FullState s : statesToBeRemoved) {
                unVisitState(s, x);
            }
        }//end of outer for loop

        return ans;
    }

    public int[] getColors() {
        return color;
    }

    public ArrayList<FullState> getStates() {
        ArrayList<FullState> ret = new ArrayList<>();
        for (FullState s : q) {
            ret.add(s);
        }
        Collections.sort(ret);
        return ret;
    }

}
