package towerdefence;

import towerdefence.game.Game;
import towerdefence.system.config.Config;
import towerdefence.system.logging.GameLogger;
import towerdefence.ui.j2d.J2dFactory;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    void main() {
        Logger logger = null;

        try {
            // config to set up logging levels
            Config config = new Config();

            GameLogger.setup(config.getLogLevelFile(), config.getLogLevelConsole());
            logger = GameLogger.getLogger();

            logger.info("Startup complete.");
            logger.info("Selected level: " + config.getSelectedLevel());

            // create factory and pass to game
            J2dFactory factory = new J2dFactory();

            Game game = Game.getInstance();
            game.init(config, factory);
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
