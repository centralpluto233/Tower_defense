package towerdefence.ui.j2d.entity.tower;

import towerdefence.game.entity.tower.BasicTower;
import towerdefence.ui.j2d.J2dGraphicsContext;

import java.awt.*;

public class J2dBasicTower extends BasicTower {
    private final J2dGraphicsContext graphicsContext;

    public J2dBasicTower(double x, double y, J2dGraphicsContext graphicsContext) {
        super(x, y);
        this.graphicsContext = graphicsContext;
    }

    @Override
    public void draw() {
        Graphics2D g2d = graphicsContext.getGraphics2D();
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
