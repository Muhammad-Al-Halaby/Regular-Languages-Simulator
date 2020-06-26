package jflap;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

public class JFlapFrame extends JFrame {

    /*
        THE COLORS PALETTE
        #011627 MAASTRICHT BLUE
        #FDFFFC BABY POWDER
        #2EC4B6 MAXIMUM BLUE GREEN
        #E71D36 ROSE MADDER
        #FF9F1C BRIGHT YELLOW (CRAYOLA)
     */
    public final static Color MAASTRICHT_BLUE = Color.decode("#011627");
    public final static Color BABY_POWDER = Color.decode("#FDFFFC");
    public final static Color MAXIMUM_BLUE_GREEN = Color.decode("#2EC4B6");
    public final static Color ROSE_MADDER = Color.decode("#E71D36");
    public final static Color BRIGHT_YELLOW = Color.decode("#FF9F1C");
    public final static Color ICY_BLUE = Color.decode("#B9F2FF");

    private ForDrawingPanel drawPanel;
    private StatesScrollPanel statesPanel;
    private ControlPanel controlPanel;
    private JScrollPane scroll;

    public JFlapFrame() {
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(d);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        init();
    }

    private void init() {
        // Intaialize objects
        statesPanel = new StatesScrollPanel(this);
        drawPanel = new ForDrawingPanel(this);
        controlPanel = new ControlPanel(this, drawPanel, statesPanel);
        //-----------------------------------------
        //add draw panel       
        drawPanel.setBackground(MAXIMUM_BLUE_GREEN);
        add(drawPanel);
        //-----------------------------------------
        //Add the states panel        
        statesPanel.setBounds(0, drawPanel.getHeight() + controlPanel.getHeight(), statesPanel.getWidth(), statesPanel.getHeight() - 15);
        //------------------------------------------
        //add the control panel
        controlPanel.setBounds(0, drawPanel.getHeight(), controlPanel.getWidth(), controlPanel.getHeight() + 1);
        controlPanel.setBackground(MAASTRICHT_BLUE);
        add(controlPanel);
        //------------------------------------------
        //add scroll pane
        scroll = new JScrollPane(statesPanel);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scroll.setBounds(statesPanel.getBounds());
        add(scroll);
    }

    public StatesScrollPanel getStatesPanel() {
        return statesPanel;
    }

    public ControlPanel getControlPanel() {
        return controlPanel;
    }

    public ForDrawingPanel getDrawPanel() {
        return drawPanel;
    }

}
