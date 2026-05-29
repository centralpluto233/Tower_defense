package towerdefence.game.ecs.components;

public class SpawnScheduleComponent {
    private final double intervalSeconds;
    private double timeLeftSeconds;

    /**
     * Component to keep track of when the next enemy should spawn. It counts down from the start delay, then resets to the interval after each spawn.
     * @param intervalSeconds the time in seconds between spawns after the first one
     * @param startDelaySeconds the time in seconds before the first spawn
     */
    public SpawnScheduleComponent(double intervalSeconds, double startDelaySeconds) {
        this.intervalSeconds = intervalSeconds;
        this.timeLeftSeconds = startDelaySeconds;
    }

    /**
     * Gets the time left in seconds before the next spawn.
     * @return the time left in seconds before the next spawn
     */
    public double getTimeLeftSeconds() {
        return timeLeftSeconds;
    }

    /**
     * Ticks the spawn schedule down by the given amount of seconds.
     * @param dt the amount of seconds to tick down
     */
    public void tick(double dt) {
        timeLeftSeconds -= dt;
    }

    /**
     * Resets the spawn schedule to the interval, so that the next spawn will happen after the interval time has passed.
     */
    public void resetToInterval() {
        timeLeftSeconds = intervalSeconds;
    }
}
