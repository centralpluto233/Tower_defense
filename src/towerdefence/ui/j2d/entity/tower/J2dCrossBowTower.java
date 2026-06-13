package towerdefence.ui.j2d.entity.tower;

import towerdefence.game.entities.tower.CrossBowTower;
import towerdefence.game.interfaces.ProjectileFactory;
import towerdefence.ui.j2d.J2dGraphicsContext;
import towerdefence.ui.j2d.assets.AssetManager;
import towerdefence.ui.j2d.entity.J2dEntityHelper;
import towerdefence.util.Vector2D;

import java.awt.image.BufferedImage;

public class J2dCrossBowTower extends CrossBowTower {
    private final J2dGraphicsContext graphicsContext;
    private final BufferedImage rangeImage;
    private final BufferedImage sprite;

    public J2dCrossBowTower(double x, double y, ProjectileFactory projectileFactory, J2dGraphicsContext ctx, AssetManager assetManager) {
        super(x, y, projectileFactory);
        this.graphicsContext = ctx;
        this.rangeImage = J2dEntityHelper.buildRangeImage((int) getRange());
        this.sprite = assetManager.getSprite("sprites/entities/towers/crossbow.png");
    }

    @Override
    public Vector2D getSpawnPosition() {
        return new Vector2D(this.getX(), this.getY() - getHeight() / 4);
    }

    @Override
    public void draw(double deltaTime) {
        J2dEntityHelper.drawTower(graphicsContext, this, sprite, rangeImage);
    }


}
