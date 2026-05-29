package towerdefence.game.ecs.components;

import towerdefence.util.Vector2D;

import java.util.List;

public class SpawnPathComponent {
    private final List<Vector2D> path;

    /**
     * Keeps a list of points that enemies should follow when they spawn.
     *
     * @param path the list of 2D coords that the enemies should follow
     */
    public SpawnPathComponent(List<Vector2D> path) {
        this.path = List.copyOf(path);
    }

    /**
     * Gets the path that enemies should follow when they spawn.
     *
     * @return the list of 2D coords that the enemies should follow
     */
    public List<Vector2D> getPath() {
        return path;
    }
}
