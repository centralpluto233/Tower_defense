package towerdefence.game.ecs.components;

public class SpawnCountComponent {
    private int remaining;
    private int spawnedCount = 0;

    /**
     * Component to keep count of how many enemies left to spawn
     *
     * @param remaining amount
     */
    public SpawnCountComponent(int remaining) {
        this.remaining = remaining;
    }

    /**
     * check if all enemies spawned in
     *
     * @return true if all enemies spawned, else false
     */
    public boolean isDone() {
        return remaining <= 0;
    }

    /**
     * check if at least one enemy has spawned
     *
     * @return true if at least one enemy has spawned, else false
     */
    public boolean hasNotSpawnedAny() {
        return spawnedCount <= 0;
    }

    /**
     * Subtracts one from the spawn amount
     */
    public void consumeOne() {
        if (!this.isDone()) {
            this.remaining--;
            this.spawnedCount++;
        }
    }
}
