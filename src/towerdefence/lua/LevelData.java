package towerdefence.lua;

import towerdefence.game.util.Vector2D;

import java.util.List;

public class LevelData {
    private final String levelName;
    private final int lives;
    private final int resources;
    private final List<Vector2D> path;
    private final List<Vector2D> towerSpots;

    public LevelData(String levelName, int lives, int resources, List<Vector2D> path, List<Vector2D> towerSpots) {
        this.levelName = levelName;
        this.lives = lives;
        this.resources = resources;
        this.path = List.copyOf(path);
        this.towerSpots = List.copyOf(towerSpots);
    }

    public String getLevelName() {
        return levelName;
    }

    public int getLives() {
        return lives;
    }

    public int getResources() {
        return resources;
    }

    public List<Vector2D> getPath() {
        return path;
    }

    public List<Vector2D> getTowerSpots() {
        return towerSpots;
    }

    @Override
    public String toString() {
        return "LevelData{" +
                "levelName='" + levelName + '\'' +
                ", lives=" + lives +
                ", resources=" + resources +
                ", pathSize=" + path.size() +
                ", towerSpotsSize=" + towerSpots.size() +
                '}';
    }
}
