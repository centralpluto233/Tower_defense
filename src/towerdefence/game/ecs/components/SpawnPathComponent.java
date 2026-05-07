package towerdefence.game.ecs.components;

import towerdefence.util.Vector2D;

import java.util.List;

public class SpawnPathComponent {
    private final List<Vector2D> path;

    public SpawnPathComponent(List<Vector2D> path) {
        this.path = List.copyOf(path);
    }

    public List<Vector2D> getPath() {
        return path;
    }
}
