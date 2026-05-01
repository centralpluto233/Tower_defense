package towerdefence.ui.j2d.entity.projectile;

import towerdefence.game.entity.enemy.EnemyEntity;
import towerdefence.game.entity.projectile.HomingProjectile;
import towerdefence.ui.j2d.J2dGraphicsContext;

import java.awt.*;

public class J2dHomingProjectile extends HomingProjectile {
    private final J2dGraphicsContext graphicsContext;

    public J2dHomingProjectile(double x, double y, int damage, EnemyEntity target, J2dGraphicsContext graphicsContext) {
        super(x, y, damage, target);
        this.graphicsContext = graphicsContext;
    }

    @Override
    public void draw() {
        Graphics2D g2d = graphicsContext.getGraphics2D();
        g2d.setColor(Color.CYAN);
        g2d.fillRect((int)getX(), (int)getY(), (int)getWidth(), (int)getHeight());
    }
}
