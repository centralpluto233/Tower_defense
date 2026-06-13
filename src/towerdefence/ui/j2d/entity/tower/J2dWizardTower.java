package towerdefence.ui.j2d.entity.tower;

import towerdefence.game.entities.tower.WizardTower;
import towerdefence.game.interfaces.ProjectileFactory;
import towerdefence.ui.j2d.J2dGraphicsContext;
import towerdefence.ui.j2d.assets.AssetManager;
import towerdefence.ui.j2d.entity.J2dEntityHelper;
import towerdefence.util.Vector2D;

import java.awt.image.BufferedImage;

public class J2dWizardTower extends WizardTower {
    private final J2dGraphicsContext graphicsContext;
    private final BufferedImage rangeImage;
    private final BufferedImage towerSprite;

    public J2dWizardTower(double x, double y, ProjectileFactory projectileFactory, J2dGraphicsContext ctx, AssetManager assetManager) {
        super(x, y, projectileFactory);
        this.graphicsContext = ctx;
        this.rangeImage = J2dEntityHelper.buildRangeImage((int) getRange());
        this.towerSprite = assetManager.getSprite("sprites/entities/towers/wizard.png");
    }

    @Override
    public Vector2D getSpawnPosition() {
        return new Vector2D(getX(), getY() - getHeight() / 3.0);
    }

    @Override
    public void draw(double deltaTime) {
        J2dEntityHelper.drawTower(graphicsContext, this, towerSprite, rangeImage);
    }
}