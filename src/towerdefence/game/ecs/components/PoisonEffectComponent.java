package towerdefence.game.ecs.components;

public class PoisonEffectComponent {
    private final double duration;
    private final double tickInterval;
    private final int damagePerTick;

    private double timeRemaining;
    private double timeSinceLastTick;

    /**
     * Component to apply a poison effect to an enemy.
     *
     * @param duration      the total duration of the poison effect, in seconds
     * @param tickInterval  the interval at which the poison effect should deal damage, in seconds.
     * @param damagePerTick the amount of damage that should be dealt to the enemy each time the poison effect ticks
     */
    public PoisonEffectComponent(double duration, double tickInterval, int damagePerTick) {
        this.duration = duration;
        this.tickInterval = tickInterval;
        this.damagePerTick = damagePerTick;
        this.timeRemaining = duration;
        this.timeSinceLastTick = tickInterval;
    }

    /**
     * Ticks the poison effect down by the given amount of seconds. This should be called every frame by the poison system.
     *
     * @param deltaTime the amount of seconds to tick down the poison effect
     * @return true if the poison effect should deal damage to the enemy this tick, false otherwise.
     */
    public boolean update(double deltaTime) {
        timeRemaining -= deltaTime;
        timeSinceLastTick += deltaTime;
        if (timeSinceLastTick > tickInterval) {
            timeSinceLastTick -= tickInterval;
            return true;
        }
        return false;
    }

    /**
     * Checks if the poison effect has expired and should be removed from the enemy.
     *
     * @return true if the poison effect has expired, false otherwise.
     */
    public boolean isExpired() {
        return timeRemaining <= 0;
    }

    /**
     * Gets the amount of damage that should be dealt to the enemy each time the poison effect ticks.
     *
     * @return the amount of damage that should be dealt to the enemy each time the poison effect ticks.
     */
    public int getDamagePerTick() {
        return damagePerTick;
    }

    /**
     * Refreshes the poison effect, resetting its duration back to the initial value.
     * This can be called when an enemy is hit by a new poison attack while already poisoned, so that the poison effect doesn't expire prematurely.
     */
    public void refresh() {
        this.timeRemaining = duration;
    }

    public double getDuration() {
        return duration;
    }

    public double getInterval() {
        return tickInterval;
    }

}
