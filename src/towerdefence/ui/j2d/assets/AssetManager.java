package towerdefence.ui.j2d.assets;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
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
        List<String> spritePaths = List.of(
                // Enemies
                "sprites/entities/enemies/bat.png",
                "sprites/entities/enemies/goblin.png",
                "sprites/entities/enemies/big_slime.png",

                // Projectiles
                "sprites/entities/projectiles/arrow_archer.png",
                "sprites/entities/projectiles/arrow_crossbow.png",
                "sprites/entities/projectiles/cannonball.png",
                "sprites/entities/projectiles/poison.png",

                // Towers
                "sprites/entities/towers/archer.png",
                "sprites/entities/towers/crossbow.png",
                "sprites/entities/towers/cannon.png",
                "sprites/entities/towers/wizard.png",

                // Effects
                "sprites/entities/enemies/effects/poison.png",

                // Map
                "sprites/map/decoration/mushroom.png",
                "sprites/map/decoration/rock_1.png",
                "sprites/map/decoration/rock_2.png",
                "sprites/map/decoration/rock_3.png",
                "sprites/map/castle.png",


                // HUD
                "sprites/entities/towers/archer_icon.png",
                "sprites/hud/heart_full.png",
                "sprites/hud/heart_half.png",
                "sprites/hud/heart_empty.png",
                "sprites/hud/Coin-Sheet.png",
                "sprites/hud/pause.png",


                // Cursors
                "sprites/hud/Cursor_1.png",
                "sprites/hud/Cursor_2.png",
                "sprites/hud/BuildCursor.png",
                "sprites/hud/Cursor_3.png",
                "sprites/hud/sellcursor.png",
                "sprites/hud/box.png"
        );

        // Themes
        for (String theme : MapTheme.getTypes()) {
            load("sprites/map/" + theme + "/grass.png");
            load("sprites/map/" + theme + "/tree1.png");
            load("sprites/map/" + theme + "/tree2.png");
            load("sprites/map/" + theme + "/path.png");
            load("sprites/map/" + theme + "/towerspot.png");
            load("sprites/map/" + theme + "/background.png");
        }

        for (String path : spritePaths) {
            load(path);
        }
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