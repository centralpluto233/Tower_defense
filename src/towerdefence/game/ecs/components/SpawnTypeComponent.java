package towerdefence.game.ecs.components;

public class SpawnTypeComponent {
    private final String enemyType;

    public SpawnTypeComponent(String type) {
        this.enemyType = type;
    }

    public String getEnemyType() {
        return this.enemyType;
    }
}
