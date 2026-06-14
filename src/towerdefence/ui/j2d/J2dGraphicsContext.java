package towerdefence.ui.j2d;

import java.awt.*;

// A simple wrapper for Graphics2D to be used in the J2dEntityHelper and other classes that need to draw things on the screen.
// This allows us to pass around a single object that contains all the necessary information for drawing, and also allows us to easily switch to a different graphics library in the future if we want to.
public class J2dGraphicsContext {
    private Graphics2D g2d;

    public Graphics2D getGraphics2D() {
        return g2d;
    }

    public void setGraphics2D(Graphics2D g2d) {
        this.g2d = g2d;
    }
}