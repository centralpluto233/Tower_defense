package towerdefence.ui.j2d.entity.tower;

import towerdefence.game.entities.tower.ArcherTower;
import towerdefence.game.interfaces.ProjectileFactory;
import towerdefence.ui.j2d.J2dGraphicsContext;
import towerdefence.ui.j2d.assets.AssetManager;
import towerdefence.ui.j2d.assets.SpriteAnimation;
import towerdefence.ui.j2d.entity.J2dEntityHelper;
import towerdefence.util.Vector2D;

import java.awt.image.BufferedImage;

public class J2dArcherTower extends ArcherTower {
    private final J2dGraphicsContext graphicsContext;
    private final BufferedImage rangeImage;
    private final SpriteAnimation animation;

    public J2dArcherTower(double x, double y, ProjectileFactory projectileFactory, J2dGraphicsContext ctx, AssetManager assetManager) {
        super(x, y, projectileFactory);
        this.graphicsContext = ctx;
        this.rangeImage = J2dEntityHelper.buildRangeImage((int) getRange());

        BufferedImage sprite = assetManager.getSprite("sprites/entities/towers/archer.png");
        this.animation = new SpriteAnimation(sprite, 2, 30);
    }

    /**
     * ArcherTower's projectiles should spawn slightly above the tower's center to create a more natural shooting effect.
     *
     * @return The spawn position for projectiles, adjusted to be slightly above the tower's center.
     */
    @Override
    public Vector2D getSpawnPosition() {
        return new Vector2D(this.getX(), this.getY() - getHeight() / 4);
    }

    @Override
    public void draw(double deltaTime) {
        animation.update(deltaTime);
        J2dEntityHelper.drawTower(graphicsContext, this, animation.getCurrentFrame(), rangeImage);
    }


}
