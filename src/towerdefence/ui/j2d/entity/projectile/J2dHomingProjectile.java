package towerdefence.ui.j2d.entity.projectile;

import towerdefence.game.entity.enemy.EnemyEntity;
import towerdefence.game.entity.projectile.HomingProjectile;
import towerdefence.game.interfaces.RenderContext;
import towerdefence.ui.j2d.J2dRenderContext;

import java.awt.*;

public class J2dHomingProjectile extends HomingProjectile {

    public J2dHomingProjectile(double x, double y, int damage, EnemyEntity target) {
        super(x, y, damage, target);
    }

    @Override
    public void draw(RenderContext ctx) {
        Graphics2D g2d = ((J2dRenderContext) ctx).getGraphics2D();
        g2d.setColor(Color.CYAN);
        g2d.fillRect((int)getX(), (int)getY(), (int)getWidth(), (int)getHeight());
    }
}
