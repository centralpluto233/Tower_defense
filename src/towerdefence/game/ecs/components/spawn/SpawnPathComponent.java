package towerdefence.game.ecs.components.spawn;

import towerdefence.util.Vector2D;

import java.util.List;

public record SpawnPathComponent(List<Vector2D> path) {
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
    @Override
    public List<Vector2D> path() {
        return path;
    }
}
