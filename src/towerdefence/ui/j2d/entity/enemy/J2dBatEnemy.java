package towerdefence.ui.j2d.entity.enemy;

import towerdefence.game.entities.enemy.BatEnemy;
import towerdefence.ui.j2d.J2dGraphicsContext;
import towerdefence.ui.j2d.assets.AssetManager;
import towerdefence.ui.j2d.assets.SpriteAnimation;
import towerdefence.util.Vector2D;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.util.List;

public class J2dBatEnemy extends BatEnemy {
    private final J2dGraphicsContext graphicsContext;
    private final SpriteAnimation animation;

    public J2dBatEnemy(double x, double y, List<Vector2D> path, J2dGraphicsContext graphicsContext, AssetManager assetManager) {
        super(x,y,path);
        this.graphicsContext = graphicsContext;
        BufferedImage sheet = assetManager.getSprite("sprites/entities/enemies/bat.png");
        this.animation = new SpriteAnimation(sheet, 4, 40);
    }

    @Override
    public void draw(double deltaTime) {
        animation.update(deltaTime);
        BufferedImage frame = animation.getCurrentFrame();
        Graphics2D g2d = graphicsContext.getGraphics2D();
        double x = getDrawPosition().x();
        double y = getDrawPosition().y();

        if (isHitFlashing()) {
            BufferedImage scaled = new BufferedImage((int)getWidth(), (int)getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D tmp = scaled.createGraphics();
            tmp.drawImage(frame, 0, 0, (int)getWidth(), (int)getHeight(), null);
            tmp.dispose();

            RescaleOp redTint = new RescaleOp(
                    new float[]{2.0f, 0.4f, 0.4f, 1.0f},
                    new float[]{0, 0, 0, 0},
                    null
            );
            g2d.drawImage(scaled, redTint, (int)x, (int)y);
        } else {
            g2d.drawImage(frame, (int)x, (int)y, (int)getWidth(), (int)getHeight(), null);
        }
    }

}