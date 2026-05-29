package towerdefence.game.ecs.components;

public class SpawnCountComponent {
    private int remaining;

    /**
     * Component to keep count of how many enemies left to spawn
     * @param remaining amount
     */
    public SpawnCountComponent(int remaining) {
        this.remaining = remaining;
    }

    /**
     * check if all enemies spawned in
     * @return true if all enemies spawned, else false
     */
    public boolean isDone() {
        return remaining <= 0;
    }

    /**
     * Subtracts one from the spawn amount
     */
    public void consumeOne() {
        if (!this.isDone()) {
            this.remaining--;
        }
    }
}
