package towerdefence.level;

import towerdefence.util.Vector2D;

import java.util.List;

public record LevelData(String levelName, String levelFile, int lives, int resources, List<List<Vector2D>> paths,
                        List<Vector2D> towerSpots, List<WaveData> waves, GameGrid grid) {
    /**
     * Constructs a new LevelData object with the specified parameters.
     *
     * @param levelName  the name of the level
     * @param lives      the number of lives the player starts with in this level
     * @param resources  the amount of resources the player starts with in this level
     * @param paths      the paths that enemies will follow in this level, represented as a list of lists of Vector2D
     * @param towerSpots the locations where the player can build towers, represented as a list of Vector2D
     * @param waves      the list of waves of enemies that will spawn in this level, represented as a list of WaveData
     */
    public LevelData(String levelName, String levelFile, int lives, int resources, List<List<Vector2D>> paths, List<Vector2D> towerSpots, List<WaveData> waves, GameGrid grid) {
        this.levelName = levelName;
        this.levelFile = levelFile;
        this.lives = lives;
        this.resources = resources;
        this.paths = paths.stream().map(List::copyOf).toList();
        this.towerSpots = List.copyOf(towerSpots);
        this.waves = List.copyOf(waves);
        this.grid = grid;
    }

    /**
     * Returns the name of the level.
     *
     * @return the level name
     */
    @Override
    public String levelName() {
        return levelName;
    }

    /**
     * Returns the file path of the level's Lua script, which can be used for debugging or display purposes.
     *
     * @return the level file path
     */
    @Override
    public String levelFile() {
        return levelFile;
    }

    /**
     * Returns the number of lives the player starts with in this level.
     *
     * @return the number of lives
     */
    @Override
    public int lives() {
        return lives;
    }

    /**
     * Returns the amount of resources the player starts with in this level.
     *
     * @return the amount of resources
     */
    @Override
    public int resources() {
        return resources;
    }

    /**
     * Returns the paths that enemies will follow in this level, represented as a list of lists of Vector2D.
     *
     * @param index the index of the path to retrieve, since there can be multiple paths in a level
     * @return the path at the specified index
     */
    public List<Vector2D> getPath(int index) {
        return paths.get(index);
    }

    /**
     * Returns the paths that enemies will follow in this level, represented as a list of lists of Vector2D.
     *
     * @return the list of paths
     */
    @Override
    public List<List<Vector2D>> paths() {
        return paths;
    }

    /**
     * Returns the locations where the player can build towers in this level, represented as a list of Vector2D.
     *
     * @return the tower spots
     */
    @Override
    public List<Vector2D> towerSpots() {
        return towerSpots;
    }

    /**
     * Returns the list of waves of enemies that will spawn in this level, represented as a list of WaveData.
     *
     * @return the waves of enemies
     */
    @Override
    public List<WaveData> waves() {
        return waves;
    }

    /**
     * Returns the GameGrid object representing the grid layout of the level, which can be used for pathfinding and other grid-based calculations.
     *
     * @return the GameGrid for this level
     */
    @Override
    public GameGrid grid() {
        return grid;
    }

    /**
     * Returns a string representation of the LevelData object, including the level name, lives, resources, path, tower spots, and waves.
     *
     * @return a string representation of the LevelData
     */
    @Override
    public String toString() {
        return "LevelData{" +
                "levelName='" + levelName + '\'' +
                ", lives=" + lives +
                ", resources=" + resources +
                ", paths=" + paths +
                ", towerSpots=" + towerSpots +
                ", waves=" + waves +
                '}';
    }
}
