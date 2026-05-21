package towerdefence.ui.j2d.entity.projectile;

import towerdefence.game.entities.enemy.EnemyEntity;
import towerdefence.game.entities.projectile.HomingProjectile;
import towerdefence.ui.j2d.assets.AssetManager;
import towerdefence.ui.j2d.J2dGraphicsContext;
import towerdefence.util.Vector2D;

import java.awt.*;
import java.awt.image.BufferedImage;

public class J2dHomingProjectile extends HomingProjectile {
    private final J2dGraphicsContext graphicsContext;
    private final BufferedImage sprite;

    public J2dHomingProjectile(double x, double y, int damage, EnemyEntity target, J2dGraphicsContext graphicsContext, AssetManager assetManager) {
        super(x, y, damage, target);
        this.graphicsContext = graphicsContext;
        this.sprite = assetManager.getSprite("sprites/entities/projectiles/arrow_crossbow.png");
    }

    @Override
    public void draw(double deltaTime) {
        Graphics2D g2d = graphicsContext.getGraphics2D();
        Vector2D velocity = this.getVelocity();

        Graphics2D g2dcopy = (Graphics2D) g2d.create();
        g2dcopy.rotate(velocity.angle(), getDrawPosition().x(), getDrawPosition().y());

        g2dcopy.drawImage(sprite, (int)(getDrawPosition().x() - getWidth() / 2.0), (int)(getDrawPosition().y() - getHeight() / 2.0), null);
        g2dcopy.dispose();
    }
}
