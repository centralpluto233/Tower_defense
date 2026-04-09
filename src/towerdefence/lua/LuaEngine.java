package towerdefence.lua;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jme.JmePlatform;
import towerdefence.game.util.Vector2D;
import towerdefence.system.config.Config;
import towerdefence.system.config.Constants;
import towerdefence.system.logging.GameLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class LuaEngine {
    private static final Logger logger = GameLogger.getLogger();

    private final Config config;
    private final Globals globals = JmePlatform.standardGlobals();

    public LuaEngine(Config config) {
        this.config = config;
    }

    private void loadSelectedLevel() {
        String levelPath = Constants.Paths.LEVELS + config.getSelectedLevel();
        loadScript(levelPath);
    }

    public LevelData loadLevelData() {
        loadSelectedLevel();

        String levelName = this.getString("level_name");
        int lives = this.getInt("lives");
        int resources = this.getInt("resources");
        List<Vector2D> path = this.readVectorTable("main_path");
        List<Vector2D> towerSpots = this.readVectorTable("tower_spots");
        List<WaveData> waves = this.readWaveTable("waves");

        return new LevelData(levelName, lives, resources, path, towerSpots, waves);
    }

    private LuaValue getValue(String name) {
        return globals.get(name);
    }

    private LuaTable getTable(String name) {
        return globals.get(name).checktable();
    }

    private void loadScript(String levelPath) {
        try {
            logger.info("Loading Lua script: " + levelPath);
            globals.loadfile(levelPath).call();
        } catch (Exception e) {
            throw new RuntimeException("Could not load level: " + levelPath, e);
        }
    }

    private String getString(String name) {
        return globals.get(name).checkjstring();
    }

    private int getInt(String name) {
        return globals.get(name).checkint();
    }

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

    private List<WaveData> readWaveTable(String tableName) {
        LuaTable table = getTable(tableName);
        List<WaveData> waves = new ArrayList<>();

        for (int i = 1; i <= table.length(); i++) {
            LuaTable wave = table.get(i).checktable();

            int count = wave.get("count").checkint();
            double interval = wave.get("interval").checkdouble();
            double startDelay = wave.get("startDelay").checkdouble();

            waves.add(new WaveData(count, interval, startDelay));
        }
        return waves;
    }
}
