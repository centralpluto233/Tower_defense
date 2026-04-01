package towerdefence.j2d.entity.tower;

import towerdefence.game.entity.tower.BasicTower;

import java.awt.*;

public class J2dBasicTower extends BasicTower {
    private final Graphics2D g2d;
    public J2dBasicTower(double x, double y, Graphics2D g2d) {
        super(x, y);
        this.g2d = g2d;
    }

    @Override
    public void draw() {
        g2d.setColor(Color.RED);
        g2d.fillRect((int)getX(), (int)getY(), (int)getWidth(), (int)getHeight());
    }
}
