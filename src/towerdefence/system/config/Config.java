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
     * @param key the property key to look up
     * @return the value of the property (never null)
     */
    private String lookup(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            throw new RuntimeException("Missing config key: '"+ key + "' - check config.properties");
        }
        return value;
    }

    /**
     * Public method to get a configuration value by key, which internally calls the lookup method.
     * @param key the property key to look up
     * @return the value of the property (never null)
     */
    private String get(String key) {
        return lookup(key);
    }

    /**
     * Public method to get a configuration value as an integer by key, which internally calls the lookup method and parses the result.
     * @param key the property key to look up
     * @return the integer value of the property
     */
    private int getInt(String key) {
        String value = lookup(key);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Config key '"+key+"' is not a valid integer: "+value+" - check config.properties");
        }
    }

    /**
     * Public method to get a configuration value as a double by key, which internally calls the lookup method and parses the result.
     * @param key the property key to look up
     * @return the double value of the property
     */
    private double getDouble(String key) {
        String value = lookup(key);
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Config key '"+key+"' is not a valid double: "+value+" - check config.properties");
        }
    }

    /**
     * Public method to get a configuration value as a boolean by key, which internally calls the lookup method and parses the result.
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
     * @param key the key to set
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

    public Level getLogLevelConsole() {
        String level = get("logging.level.console");
        return Level.parse(level);
    }

    public Level getLogLevelFile() {
        String level = get("logging.level.file");
        return Level.parse(level);
    }

    public String getSelectedLevel() {
        return get("selected.level");
    }

    public int getGameWidth() {
        return getViewWidth()+2*getSpawnmargin();
    }

    public int getGameHeight() {
        return getViewHeight()+2*getSpawnmargin();
    }

    public int getViewWidth() {return getInt("view.width");}

    public int getViewHeight() {return getInt("view.height");}

    public int getTargetFps() {
        return getInt("target.fps");
    }

    public String getWindowTitle() {
        return get("window.title");
    }

    public boolean getDebug() {return getBoolean("debugmode.enabled");}

    public double getDebugSpeedup() {return getDouble("debugmode.speedup");}

    public int getTowerSpotRadius() {return getInt("interaction.towerspot.radius");}

    public int getTargetRenderFps() {return getInt("target.render.FPS");}

    public int getSpawnmargin() {return getInt("game.spawnmargin");}
}
