package towerdefence.game.ecs.components;

public class SpawnScheduleComponent {
    private final double intervalSeconds;
    private double timeLeftSeconds;

    public SpawnScheduleComponent(double intervalSeconds, double startDelaySeconds) {
        this.intervalSeconds = intervalSeconds;
        this.timeLeftSeconds = startDelaySeconds;
    }

    public double getIntervalSeconds() {
        return intervalSeconds;
    }

    public double getTimeLeftSeconds() {
        return timeLeftSeconds;
    }

    public void tick(double dt) {
        timeLeftSeconds -= dt;
    }

    public void resetToInterval() {
        timeLeftSeconds = intervalSeconds;
    }
}
