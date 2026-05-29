package towerdefence.system.time;

import towerdefence.system.logging.GameLogger;

import java.util.logging.Logger;

public class Stopwatch {
    private long lastTick;
    private final double maxDeltaTime;
    private static final Logger logger = GameLogger.getLogger();

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
        double deltaTime = (now - lastTick) / 1_000_000_000.0;
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
        return (System.nanoTime() - lastTick) / 1_000_000_000.0;
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

    /**
     * Sleeps until the next tick, which is the time when the elapsed time since the last tick reaches the specified target seconds. This method uses a combination of Thread.sleep and busy-waiting to achieve accurate timing while minimizing CPU usage.
     *
     * @param targetSeconds the target time in seconds to wait until the next tick
     */
    public void sleepUntilNextTick(double targetSeconds) {
        long deadline = lastTick + (long) (targetSeconds * 1_000_000_000.0);

        long sleepMs = (deadline - System.nanoTime()) / 1_000_000 - 1;
        if (sleepMs > 0) {
            try {
                Thread.sleep(sleepMs);
            } catch (InterruptedException ignored) {
            }
        }

        while (System.nanoTime() < deadline) {
            Thread.onSpinWait();
        }
    }
}
