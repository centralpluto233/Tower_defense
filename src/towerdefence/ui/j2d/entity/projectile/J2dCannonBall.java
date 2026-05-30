package towerdefence.ui.j2d.entity.projectile;

import towerdefence.game.entities.projectile.CannonBall;
import towerdefence.ui.j2d.J2dGraphicsContext;
import towerdefence.ui.j2d.assets.AssetManager;
import towerdefence.util.Vector2D;

import java.awt.*;
import java.awt.image.BufferedImage;

public class J2dCannonBall extends CannonBall {
    private final J2dGraphicsContext graphicsContext;
    private final BufferedImage sprite;

    public J2dCannonBall(double x, double y, int damage, Vector2D targetPosition, J2dGraphicsContext graphicsContext, AssetManager assetManager) {
        super(x, y, damage, targetPosition);
        this.graphicsContext = graphicsContext;
        this.sprite = assetManager.getSprite("sprites/entities/projectiles/cannonball.png");
    }

    @Override
    public void draw(double deltaTime) {
        Graphics2D g2d = graphicsContext.getGraphics2D();

        double cx = getX() + getWidth() / 2.0;
        double cy = getY() + getHeight() / 2.0;

        double angle = getVelocity().angle();

        Graphics2D g2dcopy = (Graphics2D) g2d.create();
        g2dcopy.rotate(angle, cx, cy);
        g2dcopy.drawImage(sprite,
                (int) (cx - getWidth() / 2.0),
                (int) (cy - getHeight() / 2.0),
                (int) getWidth(),
                (int) getHeight(),
                null);
        g2dcopy.dispose();
    }
}
