package towerdefence.ui.j2d;

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
        return cache.computeIfAbsent(path, this::load);
    }

    private BufferedImage load(String path) {
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is == null) throw new IllegalArgumentException("Asset not found: " + path);
            BufferedImage raw = ImageIO.read(is);
            return toCompatible(raw);
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