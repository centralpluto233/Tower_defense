package towerdefence;

import towerdefence.game.Game;
import towerdefence.ui.input.SwingInputSource;
import towerdefence.ui.j2d.J2dFactory;
import towerdefence.ui.j2d.J2dGamePanel;
import towerdefence.lua.LuaEngine;
import towerdefence.system.config.Config;
import towerdefence.system.logging.GameLogger;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) {
        Logger logger = null;

        try {
            Config config = new Config();

            GameLogger.setup(config.getLogLevelFile(), config.getLogLevelConsole());
            logger = GameLogger.getLogger();

            logger.info("Startup complete.");
            logger.info("Selected level: " + config.getSelectedLevel());

            J2dFactory factory = new J2dFactory();

            LuaEngine engine = new LuaEngine(config);

            Game game = Game.getInstance();
            J2dGamePanel panel = new J2dGamePanel(game, config);
            SwingInputSource input = new SwingInputSource(game, config, panel, panel);
            input.installHandlers();

            game.init(config, panel, factory, engine, input);
            game.run();

        } catch (RuntimeException e) {
            if (logger == null) {
                GameLogger.setup(Level.INFO, Level.INFO);
                logger = GameLogger.getLogger();
            }

            logger.log(Level.SEVERE, "Startup failed due to configuration/runtime error.", e);
            throw e;
        }
        System.exit(0);
    }
}
