package towerdefence.ui.j2d.entity.enemy;

import towerdefence.game.entities.enemy.BatEnemy;
import towerdefence.ui.j2d.J2dGraphicsContext;
import towerdefence.ui.j2d.assets.AssetManager;
import towerdefence.ui.j2d.assets.SpriteAnimation;
import towerdefence.ui.j2d.entity.J2dEntityHelper;
import towerdefence.util.Vector2D;

import java.awt.image.BufferedImage;
import java.util.List;

public class J2dBatEnemy extends BatEnemy {
    private final J2dGraphicsContext graphicsContext;
    private final SpriteAnimation animation;

    public J2dBatEnemy(double x, double y, List<Vector2D> path, J2dGraphicsContext graphicsContext, AssetManager assetManager) {
        super(x, y, path);
        this.graphicsContext = graphicsContext;
        BufferedImage sheet = assetManager.getSprite("sprites/entities/enemies/bat.png");
        this.animation = new SpriteAnimation(sheet, 4, 40);
    }

    @Override
    public void draw(double deltaTime) {
        J2dEntityHelper.drawEnemy(deltaTime, animation, graphicsContext, this);
    }

}