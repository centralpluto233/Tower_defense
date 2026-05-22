package towerdefence;

import towerdefence.game.Game;
import towerdefence.ui.j2d.J2dFactory;
import towerdefence.system.config.Config;
import towerdefence.system.logging.GameLogger;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    static void main() {
        Logger logger = null;

        try {
            Config config = new Config();

            GameLogger.setup(config.getLogLevelFile(), config.getLogLevelConsole());
            logger = GameLogger.getLogger();

            logger.info("Startup complete.");
            logger.info("Selected level: " + config.getSelectedLevel());


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
