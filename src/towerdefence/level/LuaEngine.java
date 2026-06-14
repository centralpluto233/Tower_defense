package towerdefence.level;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
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
    private Globals globals = JmePlatform.standardGlobals();

    public LuaEngine(Config config) {
        this.config = config;
    }

    private void loadSelectedLevel() throws Exception {
        String levelPath = Constants.Paths.LEVELS + config.getSelectedLevel();
        loadScript(levelPath);
    }

    public LevelData loadLevelData() throws Exception {
        loadSelectedLevel();
        return loadLevelDataFromLoadedScript();
    }

    private List<Vector2D> toWorld(List<Vector2D> points) {
        int ts = config.getTileSize();
        int margin = config.getSpawnmargin();
        points.replaceAll(p -> new Vector2D(
                margin + p.x() * ts + ts / 2.0,
                margin + p.y() * ts + ts / 2.0
        ));
        return points;
    }

    private LuaTable getTable(String name) {
        return globals.get(name).checktable();
    }

    /**
     * load a Lua script from the given path and set up the globals for reading level data. If loading fails, an exception is thrown with details about the error and the path of the script that was attempted to be loaded.
     * @param levelPath the path to the Lua script to load, relative to the levels directory (e.g. "level1.lua")
     * @throws Exception if there is an error loading the Lua script, including details about the error and the path of the script that was attempted to be loaded
     */
    private void loadScript(String levelPath) throws Exception {
        try {
            globals = JmePlatform.standardGlobals();
            logger.info("Loading Lua script: " + levelPath);
            globals.loadfile(levelPath).call();
        } catch (LuaError e) {
            logger.severe("Lua script kon niet geladen worden: " + levelPath + " — " + e.getMessage());
            throw new Exception("Kon level niet laden: " + levelPath, e);
        } catch (Exception e) {
            logger.severe("Onverwachte fout bij laden van: " + levelPath + " — " + e.getMessage());
            throw new Exception("Kon level niet laden: " + levelPath, e);
        }
    }

    private List<List<Vector2D>> readPathsTable() {
        List<List<Vector2D>> paths = new ArrayList<>();
        int i = 0;
        while (!globals.get("path" + i).isnil()) {
            paths.add(readVectorTable("path" + i));
            i++;
        }
        return paths;
    }

    private LevelData loadLevelDataFromLoadedScript() {
        String levelName = globals.get("level_name").checkjstring();
        String levelFile = globals.get("level_file").checkjstring();
        int lives = globals.get("lives").checkint();
        int resources = globals.get("resources").checkint();
        List<List<Vector2D>> paths = readPathsTable();

        List<Vector2D> towerSpots = this.toWorld(this.readVectorTable("tower_spots"));

        GameGrid grid = new GameGrid(config.getViewHeight() / config.getTileSize(),
                config.getViewWidth() / config.getTileSize());
        paths.forEach(grid::buildFromPath);
        if (config.getDebug()) {
            System.out.println(grid);
        }

        paths = paths.stream()
                .map(p -> toWorld(new ArrayList<>(p)))
                .toList();

        List<WaveData> waves = this.readWaveTable(paths.size(), levelName);

        return new LevelData(levelName, levelFile, lives, resources, paths, towerSpots, waves, grid);
    }

    /**
     * Load level data from a Lua script with the given name. The method constructs the path to the Lua script based on the provided level name and attempts to load it.
     * @return a LevelData object containing the data loaded from the Lua script, including level name, level file, lives, resources, paths, tower spots, waves, and game grid.
     * @throws Exception if there is an error loading the Lua script, including details about the error and the path of the script that was attempted to be loaded
     */
    public LevelData loadLevelData(String levelName) throws Exception {
        String levelPath = Constants.Paths.LEVELS + levelName;
        loadScript(levelPath);
        return loadLevelDataFromLoadedScript();
    }

    /**
     * reads a table and converts it to a list of Vector2D points
     * @param tableName the name of the Lua table to read, which should contain an array of points with 'x' and 'y' fields (e.g. "tower_spots" or "path0")
     * @return a list of Vector2D objects representing the points defined in the specified Lua table, where each point is constructed from the 'x' and 'y' fields of the table entries.
     */
    private List<Vector2D> readVectorTable(String tableName) {
        LuaTable table = getTable(tableName);
        List<Vector2D> result = new ArrayList<>();

        for (int i = Constants.Gameplay.LUA_ARRAY_START_INDEX; i <= table.length(); i++) {
            LuaTable point = table.get(i).checktable();
            double x = point.get("x").checkdouble();
            double y = point.get("y").checkdouble();
            result.add(new Vector2D(x, y));
        }

        return result;
    }

    /**
     * reads the "waves" table from the lua script and converts it to a list of WaveData objects
     * @param pathCount the number of paths defined in the level, used to validate the pathIndex for each wave and log a warning if an invalid pathIndex is found, defaulting to 0 if necessary
     * @param levelName the name of the level being loaded, used for logging purposes to provide context in case of warnings about invalid pathIndex values in the waves table
     * @return a list of WaveData objects
     */
    private List<WaveData> readWaveTable(int pathCount, String levelName) {
        LuaTable table = getTable("waves");
        List<WaveData> waves = new ArrayList<>();

        for (int i = Constants.Gameplay.LUA_ARRAY_START_INDEX; i <= table.length(); i++) {
            LuaTable wave = table.get(i).checktable();

            int count = wave.get("count").checkint();
            double interval = wave.get("interval").checkdouble();
            double startDelay = wave.get("startDelay").checkdouble();
            String enemyType = wave.get("enemyType").optjstring(Constants.Gameplay.DEFAULT_ENEMY_TYPE);
            int pathIndex = wave.get("pathIndex").checkint();

            if (pathIndex < 0 || pathIndex >= pathCount) {
                logger.warning("waves[" + i + "] in level '" + levelName + "' gebruikt pathIndex=" + pathIndex
                        + " maar het level heeft maar " + pathCount + " pad(en). Fallback naar pad 0.");
                pathIndex = 0;
            }

            waves.add(new WaveData(count, interval, startDelay, enemyType, pathIndex));
        }
        return waves;
    }
}