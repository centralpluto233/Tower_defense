package towerdefence.ui.j2d;

import java.awt.*;

public class J2dGraphicsContext {
    private Graphics2D g2d;

    public void setGraphics2D(Graphics2D g2d) {
        this.g2d = g2d;
    }

    public Graphics2D getGraphics2D() {
        return g2d;
    }
}
