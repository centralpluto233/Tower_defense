package towerdefence.j2d.entity.tower;

import towerdefence.game.entity.tower.BasicTower;
import towerdefence.game.interfaces.RenderContext;
import towerdefence.j2d.J2dRenderContext;

import java.awt.*;

public class J2dBasicTower extends BasicTower {
    public J2dBasicTower(double x, double y) {
        super(x, y);
    }

    @Override
    public void draw(RenderContext ctx) {
        Graphics2D g2d = ((J2dRenderContext) ctx).getGraphics2D();

        int x = (int) getX();
        int y = (int) getY();
        int w = (int) getWidth();
        int h = (int) getHeight();

        int cx = x + w / 2;
        int cy = y + h / 2;
        int r = (int) getRange();

        // range circle
        g2d.setColor(new Color(0, 180, 255, 60));
        g2d.fillOval(cx - r, cy - r, r * 2, r * 2);

        g2d.setColor(new Color(0, 180, 255, 160));
        g2d.setStroke(new BasicStroke(2f));
        g2d.drawOval(cx - r, cy - r, r * 2, r * 2);

        // tower
        g2d.setColor(Color.RED);
        g2d.fillRect(x, y, w, h);
    }

}
