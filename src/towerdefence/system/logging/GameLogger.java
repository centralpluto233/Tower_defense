package towerdefence.system.logging;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

import towerdefence.system.config.Constants;

/**
 * Utility class for setting up and providing a game logger.
 */
public class GameLogger {

    private static final Logger LOGGER = Logger.getLogger("GameLogger");
    private static boolean setupDone = false;

    /**
     * Sets up the logger with the given log level.
     * Call this once in Main before anything else.
     * @param logfileLevel the log level to use for the log file
     * @param consoleLevel the log level to use for console output
     */
    public static void setup(Level logfileLevel, Level consoleLevel) {
        if (setupDone) return;

        LOGGER.setUseParentHandlers(false);

        Formatter formatter = new Formatter() {
            private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");

            @Override
            public String format(LogRecord record) {
                String sourceClass = record.getSourceClassName();
                String sourceMethod = record.getSourceMethodName();
                String source = (sourceClass != null ? sourceClass : record.getLoggerName()) +
                        (sourceMethod != null ? " " + sourceMethod : "");

                return dateFormat.format(new Date(record.getMillis())) + " " +
                        source + " " +
                        record.getLevel() + ": " +
                        record.getMessage() + "\n"+ (record.getThrown() != null ? getThrowable(record) : "");
            }

            private String getThrowable(LogRecord record) {
                StringBuilder builder = new StringBuilder();
                Throwable throwable = record.getThrown();
                builder.append(throwable.toString()).append("\n");
                for (StackTraceElement element : throwable.getStackTrace()) {
                    builder.append("\tat ").append(element).append("\n");
                }
                return builder.toString();
            }
        };

        // Console handler
        StreamHandler consoleHandler = new StreamHandler(System.out, new SimpleFormatter());
        consoleHandler.setLevel(consoleLevel);
        consoleHandler.setFormatter(formatter);
        LOGGER.addHandler(consoleHandler);

        // File handler
        try {
            Path logPath = Paths.get(Constants.Paths.LOG_FILE);
            Path parentDir = logPath.getParent();
            if (parentDir != null) {
                Files.createDirectories(parentDir);
            }

            FileHandler fileHandler = new FileHandler(Constants.Paths.LOG_FILE, true);
            fileHandler.setLevel(logfileLevel);
            fileHandler.setFormatter(formatter);
            LOGGER.addHandler(fileHandler);
        } catch (IOException e) {
            LOGGER.warning("Could not create log file: " + e.getMessage());
        }

        LOGGER.setLevel(Level.ALL);
        setupDone = true;
    }

    /**
     * Returns the logger for use in any class.
     * @return the logger
     */
    public static Logger getLogger() {
        if (!setupDone) {
            setup(Level.INFO, Level.INFO);
        }
        return LOGGER;
    }
}