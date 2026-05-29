package towerdefence.level;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.lib.jme.JmePlatform;
import towerdefence.system.config.Config;
import towerdefence.system.config.Constants;
import towerdefence.system.logging.GameLogger;
import towerdefence.util.Vector2D;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class LuaEngine {
    private static final Logger logger = GameLogger.getLogger();

    private final Config config;
    private final Globals globals = JmePlatform.standardGlobals();

    /**
     * Creates a new LuaEngine with the given configuration.
     *
     * @param config the configuration to use for loading levels, including the selected level and spawn margin
     */
    public LuaEngine(Config config) {
        this.config = config;
    }

    /**
     * Loads the Lua script for the currently selected level as specified in the configuration.
     */
    private void loadSelectedLevel() {
        String levelPath = Constants.Paths.LEVELS + config.getSelectedLevel();
        loadScript(levelPath);
    }

    /**
     * Loads the level data from the currently selected level's Lua script and returns it as a LevelData object.
     *
     * @return a LevelData object containing the level name, initial lives, initial resources, main path, tower spots, and waves of enemies as defined in the Lua script
     */
    public LevelData loadLevelData() {
        loadSelectedLevel();

        String levelName = this.getString("level_name");
        int lives = this.getInt("lives");
        int resources = this.getInt("resources");
        List<Vector2D> path = this.toWorld(this.readVectorTable("main_path"));
        List<Vector2D> towerSpots = this.toWorld(this.readVectorTable("tower_spots"));
        List<WaveData> waves = this.readWaveTable("waves");

        return new LevelData(levelName, lives, resources, path, towerSpots, waves);
    }

    /**
     * Loads the level data from the specified level's Lua script and returns it as a LevelData object.
     *
     * @param levelName the name of the level to load, which corresponds to a Lua script in the levels directory
     * @return a LevelData object containing the level name, initial lives, initial resources, main path, tower spots, and waves of enemies as defined in the Lua script
     */
    public LevelData loadLevelData(String levelName) {
        String levelPath = Constants.Paths.LEVELS + levelName;
        loadScript(levelPath);

        levelName = this.getString("level_name");
        int lives = this.getInt("lives");
        int resources = this.getInt("resources");
        List<Vector2D> path = this.toWorld(this.readVectorTable("main_path"));
        List<Vector2D> towerSpots = this.toWorld(this.readVectorTable("tower_spots"));
        List<WaveData> waves = this.readWaveTable("waves");

        return new LevelData(levelName, lives, resources, path, towerSpots, waves);
    }

    /**
     * Converts a list of Vector2D points from the Lua script's coordinate system to the game's world coordinate system by adding the spawn margin defined in the configuration to each point.
     *
     * @param points the list of Vector2D points to convert, where each point is defined in the Lua script's coordinate system (origin at top-left corner of the level)
     * @return a new list of Vector2D points where each point has been transformed to the game's world coordinate system (origin at top-left corner of the level plus spawn margin)
     */
    private List<Vector2D> toWorld(List<Vector2D> points) {
        points.replaceAll(point -> new Vector2D(config.getSpawnmargin(), config.getSpawnmargin()).add(point));
        return points;
    }

    /**
     * Retrieves a Lua table from the global environment using the specified name and checks that it is indeed a table, throwing an error if it is not.
     *
     * @param name the name of the global variable in the Lua script that is expected to be a table (e.g., "main_path", "tower_spots", "waves")
     * @return the LuaTable corresponding to the specified name, if it exists and is a table; otherwise, an error is thrown indicating that the expected table was not found or is of the wrong type
     */
    private LuaTable getTable(String name) {
        return globals.get(name).checktable();
    }

    /**
     * Loads a Lua script from the specified file path and executes it, allowing the script to define global variables and tables that can be accessed later to construct the level data.
     *
     * @param levelPath the file path to the Lua script that defines the level data, which should be located in the levels directory and correspond to the selected level in the configuration
     */
    private void loadScript(String levelPath) {
        try {
            logger.info("Loading Lua script: " + levelPath);
            globals.loadfile(levelPath).call();
        } catch (Exception e) {
            throw new RuntimeException("Could not load level: " + levelPath, e);
        }
    }

    /**
     * Retrieves a string value from the global environment using the specified name and checks that it is indeed a string, throwing an error if it is not.
     *
     * @param name the name of the global variable in the Lua script that is expected to be a string (e.g., "level_name")
     * @return the string value corresponding to the specified name, if it exists and is a string; otherwise, an error is thrown indicating that the expected string was not found or is of the wrong type
     */
    private String getString(String name) {
        return globals.get(name).checkjstring();
    }

    /**
     * Retrieves an integer value from the global environment using the specified name and checks that it is indeed an integer, throwing an error if it is not.
     *
     * @param name the name of the global variable in the Lua script that is expected to be an integer (e.g., "lives", "resources")
     * @return the integer value corresponding to the specified name, if it exists and is an integer; otherwise, an error is thrown indicating that the expected integer was not found or is of the wrong type
     */
    private int getInt(String name) {
        return globals.get(name).checkint();
    }

    /**
     * Reads a Lua table from the global environment using the specified name, where the table is expected to be an array of points (each point being a table with "x" and "y" fields), and converts it into a list of Vector2D objects representing those points.
     *
     * @param tableName the name of the global variable in the Lua script that is expected to be a table containing an array of points (e.g., "main_path", "tower_spots"), where each point is defined as a table with "x" and "y" fields
     * @return a list of Vector2D objects corresponding to the points defined in the specified Lua table, where each point's x and y coordinates are extracted from the "x" and "y" fields of the Lua table entries; if the specified table does not exist or is not in the expected format, an error is thrown indicating the issue
     */
    private List<Vector2D> readVectorTable(String tableName) {
        LuaTable table = getTable(tableName);
        List<Vector2D> result = new ArrayList<>();

        for (int i = 1; i <= table.length(); i++) {
            LuaTable point = table.get(i).checktable();
            double x = point.get("x").checkdouble();
            double y = point.get("y").checkdouble();
            result.add(new Vector2D(x, y));
        }

        return result;
    }

    /**
     * Reads a Lua table from the global environment using the specified name, where the table is expected to be an array of wave definitions (each wave being a table with "count", "interval", "startDelay", and optionally "enemyType" fields), and converts it into a list of WaveData objects representing those waves.
     *
     * @param tableName the name of the global variable in the Lua script that is expected to be a table containing an array of wave definitions (e.g., "waves"), where each wave is defined as a table with "count", "interval", "startDelay", and optionally "enemyType" fields
     * @return a list of WaveData objects corresponding to the wave definitions in the specified Lua table, where each WaveData object is constructed using the "count", "interval", "startDelay", and optionally "enemyType" fields from the Lua table entries; if the specified table does not exist or is not in the expected format, an error is thrown indicating the issue
     */
    private List<WaveData> readWaveTable(String tableName) {
        LuaTable table = getTable(tableName);
        List<WaveData> waves = new ArrayList<>();

        for (int i = 1; i <= table.length(); i++) {
            LuaTable wave = table.get(i).checktable();

            int count = wave.get("count").checkint();
            double interval = wave.get("interval").checkdouble();
            double startDelay = wave.get("startDelay").checkdouble();
            String enemyType = wave.get("enemyType").optjstring("goblin");

            waves.add(new WaveData(count, interval, startDelay, enemyType));
        }
        return waves;
    }
}
