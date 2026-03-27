package towerdefence;

import towerdefence.system.config.Config;
import towerdefence.system.config.Constants;
import towerdefence.system.logging.GameLogger;
import towerdefence.game.Game;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
	static void main(String[] args) {
        Logger logger = null;
        try {
            Config config = new Config();
            GameLogger.setup(config.getLogLevelFile(),config.getLogLevelConsole());
            logger = GameLogger.getLogger();

            logger.info("Startup complete. Console level: " + config.getLogLevelConsole() + ", file level: " + config.getLogLevelFile());
            logger.info("Selected level: " + config.getSelectedLevel());
            logger.fine("Config loaded from: " + Constants.Paths.CONFIG);
            logger.fine("Window: " + config.getWindowWidth() + "x" + config.getWindowHeight() + " | FPS: " + config.getTargetFps());

            Game game = new Game();
            game.init();
            game.start();

        } catch (RuntimeException e) {
            if (logger == null) {
                GameLogger.setup(Level.INFO,Level.INFO);
                logger = GameLogger.getLogger();
            }
            logger.log(Level.SEVERE, "Startup failed due to configuration/runtime error.", e);
            throw e;
        }
	}
}
