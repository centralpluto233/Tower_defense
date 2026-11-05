package towerdefence.system.save;

import towerdefence.system.config.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Stores progress alongside the game's external configuration file.
 */
public final class ProgressStore {
    private static final String SELECTED_LEVEL = "selected.level";
    private static final String UNLOCKED_LEVELS = "unlocked.levels";

    private final Path file = Path.of("config", "progress.properties");
    private final Properties properties = new Properties();
    private final Logger logger;

    public ProgressStore(Logger logger) {
        this.logger = logger;
        load();
        ensureFirstLevelUnlocked();
    }

    public String getSelectedLevel(String fallback) {
        String selected = properties.getProperty(SELECTED_LEVEL, fallback);
        if (isKnownLevel(selected) && isUnlocked(selected)) {
            return selected;
        }
        if (isKnownLevel(fallback) && isUnlocked(fallback)) {
            return fallback;
        }
        return Constants.LevelConstants.LEVEL_NAMES.get(0);
    }

    public void setSelectedLevel(String level) {
        requireKnownLevel(level);
        properties.setProperty(SELECTED_LEVEL, level);
        save();
    }

    public boolean isUnlocked(String level) {
        return getUnlockedLevels().contains(level);
    }

    public void unlock(String level) {
        requireKnownLevel(level);
        Set<String> unlocked = getUnlockedLevels();
        if (unlocked.add(level)) {
            properties.setProperty(UNLOCKED_LEVELS, String.join(",", unlocked));
            save();
        }
    }

    private void load() {
        try {
            if (Files.exists(file)) {
                try (InputStream input = Files.newInputStream(file)) {
                    properties.load(input);
                }
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "Could not load saved progress from " + file, e);
        }
    }

    private void save() {
        try {
            Files.createDirectories(file.getParent());
            try (OutputStream output = Files.newOutputStream(file)) {
                properties.store(output, "Tower Defence progress");
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "Could not save progress to " + file, e);
        }
    }

    private void ensureFirstLevelUnlocked() {
        if (getUnlockedLevels().isEmpty()) {
            properties.setProperty(UNLOCKED_LEVELS, Constants.LevelConstants.LEVEL_NAMES.get(0));
            save();
        }
    }

    private Set<String> getUnlockedLevels() {
        Set<String> result = new LinkedHashSet<>();
        String value = properties.getProperty(UNLOCKED_LEVELS, "");
        for (String level : value.split(",")) {
            if (isKnownLevel(level.trim())) {
                result.add(level.trim());
            }
        }
        return result;
    }

    private boolean isKnownLevel(String level) {
        return Constants.LevelConstants.LEVEL_NAMES.contains(level);
    }

    private void requireKnownLevel(String level) {
        if (!isKnownLevel(level)) {
            throw new IllegalArgumentException("Unknown level: " + level);
        }
    }
}