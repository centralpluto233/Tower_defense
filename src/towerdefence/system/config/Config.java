package towerdefence.system.config;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;

/**
 * Loads configuration properties from a file and provides access to them.
 */
public class Config {
    private final Properties properties;

    /**
     * Constructs a ConfigLoader and loads the config file from resources.
     */
    public Config() {
        properties = new Properties();
        try (InputStream input = Config.class.getResourceAsStream(Constants.Paths.CONFIG)) {
            if (input == null) {
                throw new RuntimeException("config.properties not found in resources.");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config: " + e.getMessage());
        }
    }

    /**
     * Retrieves the value of a configuration property by key.
     *
     * @param key the property key to look up
     * @return the value of the property (never null)
     */
    private String lookup(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            throw new RuntimeException("Missing config key: '" + key + "' - check config.properties");
        }
        return value;
    }

    /**
     * Public method to get a configuration value by key, which internally calls the lookup method.
     *
     * @param key the property key to look up
     * @return the value of the property (never null)
     */
    private String get(String key) {
        return lookup(key);
    }

    /**
     * Public method to get a configuration value as an integer by key, which internally calls the lookup method and parses the result.
     *
     * @param key the property key to look up
     * @return the integer value of the property
     */
    private int getInt(String key) {
        String value = lookup(key);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Config key '" + key + "' is not a valid integer: " + value + " - check config.properties");
        }
    }

    /**
     * Public method to get a configuration value as a double by key, which internally calls the lookup method and parses the result.
     *
     * @param key the property key to look up
     * @return the double value of the property
     */
    private double getDouble(String key) {
        String value = lookup(key);
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Config key '" + key + "' is not a valid double: " + value + " - check config.properties");
        }
    }

    /**
     * Public method to get a configuration value as a boolean by key, which internally calls the lookup method and parses the result.
     *
     * @param key the property key to look up
     * @return the boolean value of the property
     */
    private boolean getBoolean(String key) {
        String value = lookup(key).trim();

        if (value.equalsIgnoreCase("true") || value.equals("1")) {
            return true;
        }
        if (value.equalsIgnoreCase("false") || value.equals("0")) {
            return false;
        }

        throw new RuntimeException(
                "Config key '" + key + "' is not a valid boolean: " + value + " (expected true/false/1/0) - check config.properties"
        );
    }

    /**
     * Sets a value in the config.
     *
     * @param key   the key to set
     * @param value the value to set
     */
    private void set(String key, String value) {
        properties.setProperty(key, value);
    }

    /**
     * Saves the current config back to the file.
     */
    public void save() {
        try (OutputStream output = new FileOutputStream(Objects.requireNonNull(Config.class.getResource(Constants.Paths.CONFIG)).getPath())) {
            properties.store(output, "Game Configuration");
        } catch (IOException e) {
            throw new RuntimeException("Failed to save config: " + e.getMessage());
        }
    }

    /**
     * Gets the log level for console output from the config and parses it into a Level object.
     *
     * @return the log level for console output
     */
    public Level getLogLevelConsole() {
        String level = get("logging.level.console");
        return Level.parse(level);
    }

    /**
     * Gets the log level for file output from the config and parses it into a Level object.
     *
     * @return the log level for file output
     */
    public Level getLogLevelFile() {
        String level = get("logging.level.file");
        return Level.parse(level);
    }

    /**
     * Gets the selected level from the config.
     *
     * @return the selected level
     */
    public String getSelectedLevel() {
        return get("selected.level");
    }

    /**
     * Calculates the total game width by adding the view width and twice the spawn margin.
     *
     * @return the total game width
     */
    public int getGameWidth() {
        return getViewWidth() + 2 * getSpawnmargin();
    }

    /**
     * Calculates the total game height by adding the view height and twice the spawn margin.
     *
     * @return the total game height
     */
    public int getGameHeight() {
        return getViewHeight() + 2 * getSpawnmargin();
    }

    /**
     * Gets the view width from the config.
     *
     * @return the view width
     */
    public int getViewWidth() {
        return getInt("view.width");
    }

    /**
     * Gets the view height from the config.
     *
     * @return the view height
     */
    public int getViewHeight() {
        return getInt("view.height");
    }

    /**
     * Gets the target frames per second (FPS) for the game from the config.
     *
     * @return the target FPS for the game
     */
    public int getTargetFps() {
        return getInt("target.fps");
    }

    /**
     * Gets the title of the game window from the config.
     *
     * @return the title of the game window
     */
    public String getWindowTitle() {
        return get("window.title");
    }

    /**
     * Gets whether debug mode is enabled from the config.
     *
     * @return true if debug mode is enabled, false otherwise
     */
    public boolean getDebug() {
        return getBoolean("debugmode.enabled");
    }

    public void setDebug(boolean state) {
        this.set("debugmode.enabled", Boolean.toString(state));
    }

    /**
     * Gets the speedup factor for debug mode from the config.
     *
     * @return the speedup factor for debug mode
     */
    public double getDebugSpeedup() {
        return getDouble("debugmode.speedup");
    }

    /**
     * Gets the radius for tower spot interaction from the config.
     *
     * @return the radius for tower spot interaction
     */
    public int getTowerSpotRadius() {
        return getInt("interaction.towerspot.radius");
    }

    /**
     * Gets the target frames per second (FPS) for rendering from the config.
     *
     * @return the target FPS for rendering
     */
    public int getTargetRenderFps() {
        return getInt("target.render.FPS");
    }

    /**
     * Gets the spawn margin for the game from the config, which is used to determine how far outside the view enemies should spawn.
     *
     * @return the spawn margin for the game
     */
    public int getSpawnmargin() {
        return getInt("game.spawnmargin");
    }

    public int getTileSize() {
        return getInt("game.tileSize");
    }

    public String getMapTheme() {
        return get("map.theme");
    }

    public void setMapTheme(String mapTheme) {
        this.set("map.theme", mapTheme);
    }


}
