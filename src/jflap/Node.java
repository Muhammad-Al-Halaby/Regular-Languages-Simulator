package jflap;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;

public class Node {

    private int nodeNumber;
    private int status;
    private JButton nodeButton;
    private JLabel nodeLabel;
    private JCheckBoxMenuItem checkbox;

    public Node(int n, JButton nodeButton, JLabel nodeLabel, JCheckBoxMenuItem checkbox) {
        this.nodeNumber = n;
        this.nodeButton = nodeButton;
        this.nodeLabel = nodeLabel;
        this.checkbox = checkbox;
    }

    public boolean isFinal() {
        return (status == 2 || status == 3);
    }

    public JCheckBoxMenuItem getCheckbox() {
        return checkbox;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public JButton getNodeButton() {
        return nodeButton;
    }

    public JLabel getNodeLabel() {
        return nodeLabel;
    }

    public int getNodeNumber() {
        return nodeNumber;
    }

    public int getStatus() {
        return status;
    }

}
