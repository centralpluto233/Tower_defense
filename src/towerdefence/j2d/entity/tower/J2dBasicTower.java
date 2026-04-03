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
        g2d.setColor(Color.RED);
        g2d.fillRect((int)getX(), (int)getY(), (int)getWidth(), (int)getHeight());
    }
}
