package towerdefence.ui.j2d.entity.enemy;

import towerdefence.game.entities.enemy.GoblinEnemy;
import towerdefence.ui.j2d.assets.AssetManager;
import towerdefence.ui.j2d.assets.SpriteAnimation;
import towerdefence.util.Vector2D;
import towerdefence.ui.j2d.J2dGraphicsContext;

import java.awt.image.BufferedImage;
import java.util.List;

import java.awt.*;

public class J2dGoblinEnemy extends GoblinEnemy {
    private final J2dGraphicsContext graphicsContext;
    private final SpriteAnimation animation;

    public J2dGoblinEnemy(double x, double y, List<Vector2D> path, J2dGraphicsContext graphicsContext, AssetManager assetManager) {
        super(x,y,path);
        this.graphicsContext = graphicsContext;
        BufferedImage sheet = assetManager.getSprite("sprites/entities/enemies/goblin.png");
        this.animation = new SpriteAnimation(sheet, 4, 15);
    }

    @Override
    public void draw(double deltaTime) {
        animation.update(deltaTime);
        BufferedImage frame = animation.getCurrentFrame();
        Graphics2D g2d = graphicsContext.getGraphics2D();
        double x = getX() - getWidth() / 2.0;
        double y = getY() - getHeight() / 2.0;

        g2d.drawImage(frame, (int)x, (int)y,(int)getWidth(),(int)getHeight(), null);
    }

}