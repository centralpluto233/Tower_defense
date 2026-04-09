package towerdefence.game.ecs.components;

public class SpawnScheduleComponent {
    private final double intervalSeconds;
    private double startDelaySeconds;

    public SpawnScheduleComponent(double intervalSeconds, double startDelaySeconds) {
        this.intervalSeconds = intervalSeconds;
        this.startDelaySeconds = startDelaySeconds;
    }

    public double getIntervalSeconds() {
        return intervalSeconds;
    }

    public double getStartDelaySeconds() {
        return startDelaySeconds;
    }

    public void tick(double dt) {
        startDelaySeconds -= dt;
    }

    public void resetToInterval() {
        startDelaySeconds = intervalSeconds;
    }
}
