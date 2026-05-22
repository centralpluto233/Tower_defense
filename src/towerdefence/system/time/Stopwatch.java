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
        lastTick = System.nanoTime();
        maxDeltaTime = 1.0/targetFps;
        logger.fine("Stopwatch started with max deltaTime: " + maxDeltaTime + "s");
    }

    /**
     * Returns the time elapsed since the last tick
     * capped with fps goal to prevent large jumps on slow machines
     * @return returns deltatime (or capped deltatime)
     */
    public double tick() {
        long now = System.nanoTime();
        double deltaTime = (now- lastTick)/1_000_000_000.0;
        lastTick = now;
        double cappedDelta = Math.min(deltaTime, maxDeltaTime);
        logger.finest("DeltaTime: "+ cappedDelta + "s");
        return cappedDelta;
    }

    /**
     * Resets the stopwatch
     */
    private void reset() {
        lastTick = System.nanoTime();
        logger.fine("Stopwatch reset");
    }

    /**
     * Returns the time elapsed since the last tick without resetting
     * @return elapsed time in seconds
     */
    public double peek() {
        return (System.nanoTime() - lastTick)/1_000_000_000.0;
    }

    public boolean hasTimePassed(double intervalSeconds) {
        return peek() >= intervalSeconds;
    }

    public boolean hasTimePassed() {
        return peek() >= maxDeltaTime;
    }

    public void sleepUntilNextTick(double targetSeconds) {
        long deadline = lastTick + (long)(targetSeconds * 1_000_000_000.0);

        long sleepMs = (deadline - System.nanoTime()) / 1_000_000 - 1;
        if (sleepMs > 0) {
            try { Thread.sleep(sleepMs); } catch (InterruptedException ignored) {}
        }

        while (System.nanoTime() < deadline) {
            Thread.onSpinWait();
        }
    }

    public void sleepUntilNextTick() {
        sleepUntilNextTick(maxDeltaTime);
    }
}
