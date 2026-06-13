package towerdefence.level;

import static towerdefence.system.config.Constants.Gameplay.MIN_WAVE_COUNT;
import static towerdefence.system.config.Constants.Gameplay.MIN_WAVE_INTERVAL_SECONDS;
import static towerdefence.system.config.Constants.Gameplay.MIN_WAVE_START_DELAY_SECONDS;

public record WaveData(int count, double intervalSeconds, double startDelaySeconds, String enemyType, int pathIndex) {
    /**
     * Constructs a new WaveData record with the specified parameters. Validates that the count is at least 1, the intervalSeconds is greater than 0, and the startDelaySeconds is non-negative.
     *
     * @param count             amount of enemies to spawn
     * @param intervalSeconds   time in seconds between each enemy spawn
     * @param startDelaySeconds time in seconds before the first enemy spawns
     * @param enemyType         the type of enemy to spawn in this wave
     * @param pathIndex         the index of the path that enemies will follow in this wave
     */
    public WaveData {
        if (count < MIN_WAVE_COUNT) {
            throw new IllegalArgumentException("Wave count must be >=1");
        }
        if (intervalSeconds <= MIN_WAVE_INTERVAL_SECONDS) {
            throw new IllegalArgumentException("Wave interval seconds must be >0");
        }
        if (startDelaySeconds < MIN_WAVE_START_DELAY_SECONDS) {
            throw new IllegalArgumentException("Wave start delay seconds must be >= 0");
        }
        if (pathIndex < 0) {
            throw new IllegalArgumentException("path index must be >= 0");
        }

    }

    /**
     * Returns a string representation of the WaveData, including the count, intervalSeconds, and startDelaySeconds.
     *
     * @return the string representation of the WaveData
     */
    @Override
    public String toString() {
        return "WaveData{" +
                "count=" + count +
                ", intervalSeconds=" + intervalSeconds +
                ", startDelaySeconds=" + startDelaySeconds +
                ", enemyType='" + enemyType + '\'' +
                ", pathIndex=" + pathIndex +
                '}';
    }
}
