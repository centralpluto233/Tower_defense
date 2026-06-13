package towerdefence.game.ecs.components;

public class SpawnTypeComponent {
    private final String enemyType;

    /**
     * Component to keep track of what type of enemy should spawn. This is used by the spawn system to determine what kind of enemy to create when it's time to spawn.
     *
     * @param type the type of enemy to spawn, as a string. This should correspond to a key in the AbstractGameFactory that the spawn system uses to create enemies.
     */
    public SpawnTypeComponent(String type) {
        this.enemyType = type;
    }

    /**
     * Gets the type of enemy that should spawn.
     * @return the type of enemy to spawn, as a string. This should correspond to a key in the AbstractGameFactory that the spawn system uses to create enemies.
     */
    public String getEnemyType() {
        return this.enemyType;
    }
}
