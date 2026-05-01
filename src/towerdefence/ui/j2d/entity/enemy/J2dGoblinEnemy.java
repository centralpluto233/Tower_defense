package towerdefence.ui.j2d.entity.enemy;

import towerdefence.game.entity.enemy.GoblinEnemy;
import towerdefence.game.util.Vector2D;
import towerdefence.ui.j2d.J2dGraphicsContext;

import java.awt.geom.Rectangle2D;
import java.util.List;

import java.awt.*;

public class J2dGoblinEnemy extends GoblinEnemy {
    private final J2dGraphicsContext graphicsContext;

    public J2dGoblinEnemy(double x, double y, List<Vector2D> path, J2dGraphicsContext graphicsContext) {
        super(x,y,path);
        this.graphicsContext = graphicsContext;
    }

    @Override
    public void draw() {
        Graphics2D g2d = graphicsContext.getGraphics2D();
        double x = getX() - getWidth() / 2.0;
        double y = getY() - getHeight() / 2.0;

        g2d.setColor(Color.GREEN);
        g2d.fill(new Rectangle2D.Double(x, y, getWidth(), getHeight()));
    }

}