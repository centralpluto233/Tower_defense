package towerdefence.ui.j2d.entity.projectile;

import towerdefence.game.entities.projectile.ArcherArrow;
import towerdefence.ui.j2d.J2dGraphicsContext;
import towerdefence.ui.j2d.assets.AssetManager;
import towerdefence.ui.j2d.entity.J2dEntityHelper;
import towerdefence.util.Vector2D;

import java.awt.image.BufferedImage;

public class J2dArcherArrow extends ArcherArrow {
    private final J2dGraphicsContext graphicsContext;
    private final BufferedImage sprite;

    public J2dArcherArrow(double x, double y, int damage, Vector2D targetPosition, J2dGraphicsContext graphicsContext, AssetManager assetManager) {
        super(x, y, damage, targetPosition);
        this.graphicsContext = graphicsContext;
        this.sprite = assetManager.getSprite("sprites/entities/projectiles/arrow_archer.png");
    }

    @Override
    public void draw(double deltaTime) {
        J2dEntityHelper.drawProjectile(graphicsContext, this,sprite);
    }
}
