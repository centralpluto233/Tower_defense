package towerdefence.j2d.entity.enemy;

import towerdefence.game.entity.enemy.GoblinEnemy;
import towerdefence.game.interfaces.RenderContext;
import towerdefence.game.util.Vector2D;
import towerdefence.j2d.J2dRenderContext;

import java.awt.geom.Rectangle2D;
import java.util.List;

import java.awt.*;

public class J2dGoblinEnemy extends GoblinEnemy {
    public J2dGoblinEnemy(double x, double y, List<Vector2D> path) {
        super(x,y,path);
    }

    @Override
    public void draw(RenderContext ctx) {
        Graphics2D g2d = ((J2dRenderContext) ctx).getGraphics2D();

        double x = getX() - getWidth() / 2.0;
        double y = getY() - getHeight() / 2.0;

        g2d.setColor(Color.GREEN);
        g2d.fill(new Rectangle2D.Double(x, y, getWidth(), getHeight()));
    }

}