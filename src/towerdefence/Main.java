package towerdefence;
import towerdefence.system.config.Config;
import towerdefence.system.config.Constants;
import towerdefence.system.logging.GameLogger;

import java.util.logging.Logger;

public class Main {
	public static void main(String[] args) {
		Config config = new Config();
        GameLogger.setup(config.getLogLevel());
        Logger logger = GameLogger.getLogger();
        logger.info("Log level set to: " + config.getLogLevel());
        logger.fine("Config loaded from: " + Constants.Paths.CONFIG);
	}
}
