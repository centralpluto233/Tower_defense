package towerdefence.game.ecs.components;

public class SpawnCountComponent {
    private int remaining;

    public SpawnCountComponent(int remaining) {
        this.remaining = remaining;
    }

    public boolean isDone() {
        return remaining <= 0;
    }

    public void consumeOne() {
        if (!this.isDone()) {
            this.remaining--;
        }
    }
}
