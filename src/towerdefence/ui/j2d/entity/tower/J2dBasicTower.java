package towerdefence.ui.j2d.entity.tower;

import towerdefence.game.entities.tower.BasicTower;
import towerdefence.ui.j2d.assets.AssetManager;
import towerdefence.ui.j2d.J2dGraphicsContext;
import towerdefence.util.Vector2D;

import java.awt.*;
import java.awt.image.BufferedImage;

public class J2dBasicTower extends BasicTower {
    private final J2dGraphicsContext graphicsContext;
    private final BufferedImage rangeImage;
    private final BufferedImage sprite;

    public J2dBasicTower(double x, double y, J2dGraphicsContext ctx, AssetManager assetManager) {
        super(x, y);
        this.graphicsContext = ctx;
        this.rangeImage = buildRangeImage((int) getRange());
        this.sprite = assetManager.getSprite("sprites/entities/towers/crossbow.png");
    }

    private BufferedImage buildRangeImage(int range) {
        int size = range * 2 + 2; // +2 voor stroke
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//        g.setColor(new Color(255, 255, 0, 40)); // transparante fill
//        g.fillOval(1, 1, range * 2, range * 2);
        g.setColor(new Color(255, 255, 0, 120)); // rand
        g.setStroke(new BasicStroke(1.5f));
        g.drawOval(1, 1, range * 2, range * 2);

        g.dispose();
        return img;
    }

    @Override
    public Vector2D getSpawnPosition() {
        return new Vector2D(this.getX(), this.getY()-getHeight()/4);
    }

    @Override
    public void draw(double deltaTime) {
        Graphics2D g2d = graphicsContext.getGraphics2D();
        int r = (int) Math.round(getRange());

        g2d.drawImage(rangeImage,
                (int)(getX() - r - 1),
                (int)(getY() - r - 1),
                null);

        g2d.drawImage(sprite, (int)getDrawPosition().x(), (int)getDrawPosition().y(),(int)getWidth(),(int)getHeight(),null);
    }


}
