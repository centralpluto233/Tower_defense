package towerdefence.system.time;

import towerdefence.system.logging.GameLogger;

import java.util.logging.Logger;

public class Stopwatch {
    private long lastTick;
    private final double maxDeltaTime;
    private static final Logger logger = GameLogger.getLogger();

    /**
     * Constructs a Stopwatch with a target FPS.
     * @param targetFps the taget frames per second
     */
    public Stopwatch(int targetFps) {
        lastTick = System.currentTimeMillis();
        maxDeltaTime = 1.0/targetFps;
        logger.fine("Stopwatch started with max deltaTime: " + maxDeltaTime + "s");
    }

    /**
     * Returns the time elapsed since the last tick
     * capped with fps goal to prevent large jumps on slow machines
     * @return returns deltatime (or capped deltatime)
     */
    public double tick() {
        long now = System.currentTimeMillis();
        double deltaTime = (now- lastTick)/1000.0;
        lastTick = now;
        double cappedDelta = Math.min(deltaTime, maxDeltaTime);
        logger.finest("DeltaTime: "+ cappedDelta + "s");
        return cappedDelta;
    }

    /**
     * Resets the stopwatch
     */
    public void reset() {
        lastTick = System.currentTimeMillis();
        logger.fine("Stopwatch reset");
    }

    /**
     * Returns the time elapsed since the last tick without resetting
     * @return elapsed time in seconds
     */
    public double peek() {
        return (System.currentTimeMillis() - lastTick)/1000.0;
    }


}
