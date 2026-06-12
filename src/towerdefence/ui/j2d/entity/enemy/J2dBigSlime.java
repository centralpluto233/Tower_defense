package towerdefence.ui.j2d.entity.enemy;

import towerdefence.game.entities.enemy.BigSlime;
import towerdefence.ui.j2d.J2dGraphicsContext;
import towerdefence.ui.j2d.assets.AssetManager;
import towerdefence.ui.j2d.assets.SpriteAnimation;
import towerdefence.ui.j2d.entity.J2dEntityHelper;
import towerdefence.util.Vector2D;

import java.awt.image.BufferedImage;
import java.util.List;

public class J2dBigSlime extends BigSlime {
    private final J2dGraphicsContext graphicsContext;
    private final SpriteAnimation animation;

    public J2dBigSlime(double x, double y, List<Vector2D> path, J2dGraphicsContext graphicsContext, AssetManager assetManager) {
        super(x, y, path);
        this.graphicsContext = graphicsContext;
        BufferedImage sheet = assetManager.getSprite("sprites/entities/enemies/big_slime.png");
        this.animation = new SpriteAnimation(sheet, 4, 25);
    }

    @Override
    public void draw(double deltaTime) {
        J2dEntityHelper.drawEnemy(deltaTime, animation, graphicsContext, this);
    }

}