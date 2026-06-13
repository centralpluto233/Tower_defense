package towerdefence.system.time;

import towerdefence.system.config.Constants;
import towerdefence.system.logging.GameLogger;

import java.util.logging.Logger;

public class Stopwatch {
    private static final Logger logger = GameLogger.getLogger();
    private final double maxDeltaTime;
    private long lastTick;

    /**
     * Constructs a Stopwatch with a target FPS.
     *
     * @param targetFps the taget frames per second
     */
    public Stopwatch(int targetFps) {
        lastTick = System.nanoTime();
        maxDeltaTime = 1.0 / targetFps;
        logger.fine("Stopwatch started with max deltaTime: " + maxDeltaTime + "s");
    }

    /**
     * Returns the time elapsed since the last tick
     * capped with fps goal to prevent large jumps on slow machines
     *
     * @return returns deltatime (or capped deltatime)
     */
    public double tick() {
        long now = System.nanoTime();
        double deltaTime = (now - lastTick) / Constants.Timing.NANOS_PER_SECOND;
        lastTick = now;
        double cappedDelta = Math.min(deltaTime, maxDeltaTime);
        logger.finest("DeltaTime: " + cappedDelta + "s");
        return cappedDelta;
    }

    /**
     * Returns the time elapsed since the last tick without resetting
     *
     * @return elapsed time in seconds
     */
    public double peek() {
        return (System.nanoTime() - lastTick) / Constants.Timing.NANOS_PER_SECOND;
    }

    /**
     * Checks if the specified time interval has passed since the last tick without resetting
     *
     * @param intervalSeconds the time interval in seconds to check against
     * @return true if the specified time interval has passed, false otherwise
     */
    public boolean hasTimePassed(double intervalSeconds) {
        return peek() >= intervalSeconds;
    }
}
