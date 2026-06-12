package towerdefence.ui.j2d.entity.tower;


import towerdefence.game.entities.tower.CannonTower;
import towerdefence.game.interfaces.ProjectileFactory;
import towerdefence.ui.j2d.J2dGraphicsContext;
import towerdefence.ui.j2d.assets.AssetManager;
import towerdefence.ui.j2d.entity.J2dEntityHelper;
import towerdefence.util.Vector2D;

import java.awt.*;
import java.awt.image.BufferedImage;

public class J2dCannonTower extends CannonTower {
    private final J2dGraphicsContext graphicsContext;
    private final BufferedImage rangeImage;
    private final BufferedImage cannonImage;

    public J2dCannonTower(double x, double y, ProjectileFactory projectileFactory, J2dGraphicsContext ctx, AssetManager assetManager) {
        super(x, y, projectileFactory);
        this.graphicsContext = ctx;
        this.rangeImage = J2dEntityHelper.buildRangeImage((int) getRange());

        this.cannonImage = assetManager.getSprite("sprites/entities/towers/cannon.png");
    }

    @Override
    public Vector2D getSpawnPosition() {
        return new Vector2D(this.getX(), this.getY() - getHeight() / 2.5);
    }

    @Override
    public void draw(double deltaTime) {
        J2dEntityHelper.drawTower(graphicsContext, this, cannonImage, rangeImage);
    }


}
