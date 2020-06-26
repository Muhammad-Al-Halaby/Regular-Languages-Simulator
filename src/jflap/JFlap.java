package jflap;

import javax.swing.SwingUtilities;

public class JFlap {
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFlapFrame d = new JFlapFrame();
            d.setTitle("JFLAP");
            d.setUndecorated(true);
            d.setVisible(true);
            d.repaint();
        });
    }
}
