package towerdefence.ui.j2d.assets;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class AssetManager {
    private final Map<String, BufferedImage> cache = new HashMap<>();
    private final GraphicsConfiguration gc = GraphicsEnvironment
            .getLocalGraphicsEnvironment()
            .getDefaultScreenDevice()
            .getDefaultConfiguration();

    public BufferedImage getSprite(String path) {
        BufferedImage image = cache.get(path);
        if (image == null) {
            throw new IllegalStateException("Sprite not preloaded: "+ path);
        }
        return image;
    }
    
    public void loadAll() {
        load("sprites/entities/enemies/bat.png");
        load("sprites/entities/enemies/goblin.png");
        load("sprites/entities/projectiles/arrow_archer.png");
        load("sprites/entities/projectiles/arrow_crossbow.png");
        load("sprites/entities/towers/archer.png");
        load("sprites/entities/towers/crossbow.png");
        load("sprites/map/path.png");
        load("sprites/map/grass.png");
        load("sprites/map/decoration/mushroom.png");
        load("sprites/map/decoration/mushroom.png");
        load("sprites/map/decoration/rock_1.png");
        load("sprites/map/decoration/rock_2.png");
        load("sprites/map/decoration/rock_3.png");
        load("sprites/map/castle.png");
        load("sprites/entities/towers/archer_icon.png");
        load("stats/heart_full.png");
        load("stats/heart_half.png");
        load("stats/heart_empty.png");
        load("stats/Coin-Sheet.png");
    }

    private void load(String path) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(path)) {
            if (is == null) throw new IllegalArgumentException("Asset not found: " + path);
            cache.put(path, ImageIO.read(is));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load asset: " + path, e);
        }
    }

    private BufferedImage toCompatible(BufferedImage img) {
        BufferedImage compatible = gc.createCompatibleImage(
                img.getWidth(), img.getHeight(), Transparency.TRANSLUCENT);
        Graphics2D g = compatible.createGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();
        return compatible;
    }
}