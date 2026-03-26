package towerdefence.system.logging;

import java.io.IOException;
import java.util.logging.*;

/**
 * Utility class for setting up and providing a game logger.
 */
public class GameLogger {

    private static final Logger LOGGER = Logger.getLogger("GameLogger");
    private static boolean setupDone = false;

    /**
     * Sets up the logger with the given log level.
     * Call this once in Main before anything else.
     * @param logLevel the log level to use
     */
    public static void setup(Level logLevel) {
        if (setupDone) return;

        LOGGER.setUseParentHandlers(false);

        // Console handler
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(logLevel);
        consoleHandler.setFormatter(new SimpleFormatter());
        LOGGER.addHandler(consoleHandler);

        // File handler
        try {
            FileHandler fileHandler = new FileHandler("logs/game.log", true);
            fileHandler.setLevel(logLevel);
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);
        } catch (IOException e) {
            LOGGER.warning("Could not create log file: " + e.getMessage());
        }

        LOGGER.setLevel(logLevel);
        setupDone = true;
    }

    /**
     * Returns the logger for use in any class.
     * @return the logger
     */
    public static Logger getLogger() {
        if (!setupDone) {
            setup(Level.INFO);
        }
        return LOGGER;
    }
}