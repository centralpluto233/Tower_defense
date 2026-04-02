package towerdefence.j2d.entity.projectile;

import towerdefence.game.entity.enemy.EnemyEntity;
import towerdefence.game.entity.projectile.HomingProjectile;

import java.awt.*;

public class J2dHomingProjectile extends HomingProjectile {
    private final Graphics2D g2d;

    public J2dHomingProjectile(double x, double y, int damage, EnemyEntity target, Graphics2D g2d) {
        super(x, y, damage, target);
        this.g2d = g2d;
    }

    @Override
    public void draw() {
        g2d.setColor(Color.CYAN);
        g2d.fillRect((int)getX(), (int)getY(), (int)getWidth(), (int)getHeight());
    }
}
