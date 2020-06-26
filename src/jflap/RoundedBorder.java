package jflap;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import javax.swing.border.Border;

class RoundedBorder implements Border {

    private int radius;
    private Color c;

    RoundedBorder(int radius, Color c) {
        this.radius = radius;
        this.c = c;
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(this.radius + 1, this.radius + 1, this.radius + 2, this.radius);
    }

    @Override
    public boolean isBorderOpaque() {
        return true;
    }

    @Override
    public void paintBorder(Component cmpnt, Graphics grphcs, int i, int i1, int i2, int i3) {
        Graphics2D g = (Graphics2D) grphcs;
        g.setStroke(new BasicStroke(3));
        g.setColor(c);
        grphcs.fillRoundRect(i, i1, i2 - 1, i3 - 1, radius, radius);
    }

}
