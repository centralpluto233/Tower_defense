package towerdefence.ui.j2d;

import towerdefence.game.interfaces.RenderContext;

import java.awt.*;

public class J2dRenderContext  implements RenderContext {
    private final Graphics2D g2d;

    public J2dRenderContext(Graphics2D g2d) {
        this.g2d = g2d;
    }
    public Graphics2D getGraphics2D() {
        return g2d;
    }
}
