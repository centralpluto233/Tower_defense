package towerdefence.game.ecs.components.spawn;

public record SpawnTypeComponent(String enemyType) {
    /**
     * Component to keep track of what type of enemy should spawn. This is used by the spawn system to determine what kind of enemy to create when it's time to spawn.
     *
     * @param enemyType the type of enemy to spawn, as a string. This should correspond to a key in the AbstractGameFactory that the spawn system uses to create enemies.
     */
    public SpawnTypeComponent {
    }

    /**
     * Gets the type of enemy that should spawn.
     *
     * @return the type of enemy to spawn, as a string. This should correspond to a key in the AbstractGameFactory that the spawn system uses to create enemies.
     */
    @Override
    public String enemyType() {
        return this.enemyType;
    }
}
