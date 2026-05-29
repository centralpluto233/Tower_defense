package towerdefence.ui.j2d.entity.projectile;

import towerdefence.game.entities.projectile.CrossBowArrow;
import towerdefence.ui.j2d.J2dGraphicsContext;
import towerdefence.ui.j2d.assets.AssetManager;
import towerdefence.util.Vector2D;

import java.awt.*;
import java.awt.image.BufferedImage;

public class J2dCrossBowArrow extends CrossBowArrow {
    private final J2dGraphicsContext graphicsContext;
    private final BufferedImage sprite;

    public J2dCrossBowArrow(double x, double y, int damage, Vector2D targetPosition, J2dGraphicsContext graphicsContext, AssetManager assetManager) {
        super(x, y, damage, targetPosition);
        this.graphicsContext = graphicsContext;
        this.sprite = assetManager.getSprite("sprites/entities/projectiles/arrow_crossbow.png");
    }

    @Override
    public void draw(double deltaTime) {
        Graphics2D g2d = graphicsContext.getGraphics2D();
        Vector2D velocity = this.getVelocity();

        Graphics2D g2dcopy = (Graphics2D) g2d.create();
        g2dcopy.rotate(velocity.angle(), getX(), getY());

        g2dcopy.drawImage(sprite, (int) (getDrawPosition().x()), (int) (getDrawPosition().y()), (int) getWidth(), (int) getHeight(), null);
        g2dcopy.dispose();
    }
}
