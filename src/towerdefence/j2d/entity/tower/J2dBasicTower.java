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

        int cx = (int) Math.round(getX());   // exact coordinate = center
        int cy = (int) Math.round(getY());   // exact coordinate = center
        int w = (int) Math.round(getWidth());
        int h = (int) Math.round(getHeight());
        int r = (int) Math.round(getRange());

        // range circle centered on exact coords
        g2d.setColor(new Color(0, 180, 255, 60));
        g2d.fillOval(cx - r, cy - r, r * 2, r * 2);

        g2d.setColor(new Color(0, 180, 255, 160));
        g2d.setStroke(new BasicStroke(2f));
        g2d.drawOval(cx - r, cy - r, r * 2, r * 2);

        // tower centered on exact coords
        int x = cx - w / 2;
        int y = cy - h / 2;
        g2d.setColor(Color.RED);
        g2d.fillRect(x, y, w, h);
    }


}
