package towerdefence.level;

import towerdefence.util.Vector2D;

import java.util.List;

public class LevelData {
    private final String levelName;
    private final int lives;
    private final int resources;
    private final List<Vector2D> path;
    private final List<Vector2D> towerSpots;
    private final List<WaveData> waves;

    /**
     * Constructs a new LevelData object with the specified parameters.
     * @param levelName the name of the level
     * @param lives the number of lives the player starts with in this level
     * @param resources the amount of resources the player starts with in this level
     * @param path the path that enemies will follow, represented as a list of waypoints (Vector2D)
     * @param towerSpots the locations where the player can build towers, represented as a list of Vector2D
     * @param waves the list of waves of enemies that will spawn in this level, represented as a list of WaveData
     */
    public LevelData(String levelName, int lives, int resources, List<Vector2D> path, List<Vector2D> towerSpots, List<WaveData> waves) {
        this.levelName = levelName;
        this.lives = lives;
        this.resources = resources;
        this.path = List.copyOf(path);
        this.towerSpots = List.copyOf(towerSpots);
        this.waves = List.copyOf(waves);
    }

    /**
     * Returns the name of the level.
     * @return the level name
     */
    public String getLevelName() {
        return levelName;
    }

    /**
     * Returns the number of lives the player starts with in this level.
     * @return the number of lives
     */
    public int getLives() {
        return lives;
    }

    /**
     * Returns the amount of resources the player starts with in this level.
     * @return the amount of resources
     */
    public int getResources() {
        return resources;
    }

    /**
     * Returns the path that enemies will follow in this level, represented as a list of waypoints (Vector2D).
     * @return the enemy path
     */
    public List<Vector2D> getPath() {
        return path;
    }

    /**
     * Returns the locations where the player can build towers in this level, represented as a list of Vector2D.
     * @return the tower spots
     */
    public List<Vector2D> getTowerSpots() {
        return towerSpots;
    }

    /**
     * Returns the list of waves of enemies that will spawn in this level, represented as a list of WaveData.
     * @return the waves of enemies
     */
    public List<WaveData> getWaves() {
        return waves;
    }

    /**
     * Returns a string representation of the LevelData object, including the level name, lives, resources, path, tower spots, and waves.
     * @return a string representation of the LevelData
     */
    @Override
    public String toString() {
        return "LevelData{" +
                "levelName='" + levelName + '\'' +
                ", lives=" + lives +
                ", resources=" + resources +
                ", path=" + path +
                ", towerSpots=" + towerSpots +
                ", waves=" + waves +
                '}';
    }
}
