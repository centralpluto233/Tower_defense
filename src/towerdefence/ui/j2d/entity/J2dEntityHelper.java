package towerdefence.ui.j2d.entity;

import towerdefence.game.entities.enemy.EnemyEntity;
import towerdefence.game.entities.projectile.Projectile;
import towerdefence.game.entities.tower.Tower;
import towerdefence.ui.j2d.J2dGamePanel;
import towerdefence.ui.j2d.J2dGraphicsContext;
import towerdefence.ui.j2d.assets.AssetManager;
import towerdefence.ui.j2d.assets.SpriteAnimation;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

public class J2dEntityHelper {
    private static SpriteAnimation poisonOverlayAnimation;

    /**
     * Initializes the poison overlay animation. Should be called once during game setup.
     * @param assetManager The AssetManager to load the poison sprite sheet from.
     */
    public static void initPoisonOverlayAnimation(AssetManager assetManager) {
        BufferedImage sheet = assetManager.getSprite("sprites/entities/enemies/effects/poison.png");
        poisonOverlayAnimation = new SpriteAnimation(sheet, 6, 20);
    }

    public static void drawEnemy(double deltaTime, SpriteAnimation animation, J2dGraphicsContext graphicsContext, EnemyEntity entity) {
        animation.update(deltaTime);
        BufferedImage frame = animation.getCurrentFrame();
        Graphics2D g2d = graphicsContext.getGraphics2D();
        double x = entity.getDrawPosition().x();
        double y = entity.getDrawPosition().y();

        if (entity.isHitFlashing()) {
            BufferedImage scaled = new BufferedImage((int) entity.getWidth(), (int) entity.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D tmp = scaled.createGraphics();
            tmp.drawImage(frame, 0, 0, (int) entity.getWidth(), (int) entity.getHeight(), null);
            tmp.dispose();

            RescaleOp redTint = new RescaleOp(
                    new float[]{2.0f, 0.4f, 0.4f, 1.0f},
                    new float[]{0, 0, 0, 0},
                    null
            );
            g2d.drawImage(scaled, redTint, (int) x, (int) y);
        } else {
            g2d.drawImage(frame, (int) x, (int) y, (int) entity.getWidth(), (int) entity.getHeight(), null);
        }

        if (entity.isPoisoned() && poisonOverlayAnimation != null) {
            poisonOverlayAnimation.update(deltaTime);
            BufferedImage poisonFrame = poisonOverlayAnimation.getCurrentFrame();
            x = entity.getPosition().x();
            y = entity.getPosition().y();
            int frameSize = 64;
            g2d.drawImage(poisonFrame,
                    (int) x-frameSize/2,
                    (int) y-frameSize/2,
                     frameSize,
                     frameSize,
                    null);
        }

        J2dGamePanel.drawEntityHealthBar(g2d, entity.getX(), entity.getY(), entity.getWidth(), entity.getHealth(), entity.getMaxHealth());
    }

    public static void drawProjectile(J2dGraphicsContext graphicsContext, Projectile projectile, BufferedImage sprite) {
        Graphics2D g2d = graphicsContext.getGraphics2D();

        double cx = projectile.getX() + projectile.getWidth() / 2.0;
        double cy = projectile.getY() + projectile.getHeight() / 2.0;

        double angle = projectile.getVelocity().angle();

        Graphics2D g2dcopy = (Graphics2D) g2d.create();
        g2dcopy.rotate(angle, cx, cy);
        g2dcopy.drawImage(sprite,
                (int) (cx - projectile.getWidth() / 2.0),
                (int) (cy - projectile.getHeight() / 2.0),
                (int) projectile.getWidth(),
                (int) projectile.getHeight(),
                null);
        g2dcopy.dispose();
    }

    public static BufferedImage buildRangeImage(int range) {
        int size = range * 2 + 2;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(255, 255, 0, 120));
        g.setStroke(new BasicStroke(1.5f));
        g.drawOval(1, 1, range * 2, range * 2);

        g.dispose();
        return img;
    }

    public static void drawTower(J2dGraphicsContext graphicsContext, Tower tower, BufferedImage cannonImage, BufferedImage rangeImage) {
        Graphics2D g2d = graphicsContext.getGraphics2D();
        int r = (int) Math.round(tower.getRange());

        g2d.drawImage(rangeImage,
                (int) (tower.getX() - r - 1),
                (int) (tower.getY() - r - 1),
                null);

        g2d.drawImage(cannonImage, (int) tower.getDrawPosition().x(), (int) tower.getDrawPosition().y(), (int) tower.getWidth(), (int) tower.getHeight(), null);
    }
}
