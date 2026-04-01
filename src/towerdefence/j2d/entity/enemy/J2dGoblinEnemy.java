package towerdefence.j2d.entity.enemy;

import towerdefence.game.entity.enemy.GoblinEnemy;

import java.awt.*;

public class J2dGoblinEnemy extends GoblinEnemy {
    private final Graphics2D g2d;
    public J2dGoblinEnemy(double x, double y, Graphics2D g2d) {
        super(x,y);
        this.g2d=g2d;
    }

    public void draw() {
        g2d.setColor(Color.GREEN);
        g2d.fillRect((int)getX(), (int)getY(), (int)getWidth(), (int)getHeight());
    }
}