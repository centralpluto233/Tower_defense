package towerdefence.ui.j2d.entity.projectile;

import towerdefence.game.entities.enemy.EnemyEntity;
import towerdefence.game.entities.projectile.HomingProjectile;
import towerdefence.ui.j2d.AssetManager;
import towerdefence.ui.j2d.J2dGraphicsContext;

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
    public void draw() {
        Graphics2D g2d = graphicsContext.getGraphics2D();
        g2d.drawImage(sprite, (int)(getX() - getWidth() / 2.0), (int)(getY() - getHeight() / 2.0), null);
    }
}
