package towerdefence.ui.j2d.entity.tower;

import towerdefence.game.entities.tower.BasicTower;
import towerdefence.ui.j2d.AssetManager;
import towerdefence.ui.j2d.J2dGraphicsContext;

import java.awt.*;
import java.awt.image.BufferedImage;

public class J2dBasicTower extends BasicTower {
    private final J2dGraphicsContext graphicsContext;
    private final BufferedImage rangeImage; // pre-rendered

    public J2dBasicTower(double x, double y, J2dGraphicsContext ctx, AssetManager assetManager) {
        super(x, y);
        this.graphicsContext = ctx;
        this.rangeImage = buildRangeImage((int) getRange());
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
    public void draw() {
        Graphics2D g2d = graphicsContext.getGraphics2D();
        int cx = (int) Math.round(getX());   // exact coordinate = center
        int cy = (int) Math.round(getY());   // exact coordinate = center
        int w = (int) Math.round(getWidth());
        int h = (int) Math.round(getHeight());
        int r = (int) Math.round(getRange());

        g2d.drawImage(rangeImage,
                (int)(getX() - r - 1),
                (int)(getY() - r - 1),
                null);

        // tower centered on exact coords
        int x = cx - w / 2;
        int y = cy - h / 2;
        g2d.setColor(Color.RED);
        g2d.fillRect(x, y, w, h);
    }


}
