package towerdefence.ui.j2d.assets;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class AssetManager {
    private final Map<String, BufferedImage> cache = new HashMap<>();

    /**
     * Retrieves a sprite from the cache. If the sprite is not found, an exception is thrown.
     *
     * @param path the path to the sprite image, relative to the classpath. This should match the path used when preloading the sprite with loadAll().
     * @return the BufferedImage of the sprite
     */
    public BufferedImage getSprite(String path) {
        BufferedImage image = cache.get(path);
        if (image == null) {
            throw new IllegalStateException("Sprite not preloaded: " + path);
        }
        return image;
    }

    /**
     * Preloads all necessary sprites into the cache. This method should be called once at the start of the game to ensure that all sprites are loaded and ready for use when needed.
     */
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

    /**
     * Loads a single sprite from the given path and stores it in the cache. The path should be relative to the classpath and should match the path used when retrieving the sprite with getSprite(). If the sprite cannot be found or loaded, an exception is thrown.
     *
     * @param path the path to the sprite image, relative to the classpath. This should match the path used when retrieving the sprite with getSprite().
     */
    private void load(String path) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(path)) {
            if (is == null) throw new IllegalArgumentException("Asset not found: " + path);
            cache.put(path, ImageIO.read(is));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load asset: " + path, e);
        }
    }
}