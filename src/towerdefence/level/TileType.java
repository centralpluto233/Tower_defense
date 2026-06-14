package towerdefence.level;

// all the possible tiletypes
public enum TileType {
    PATH_H, // horizontal
    PATH_V, // vertical
    CORNER_NW, // turn left-up
    CORNER_SW, // turn left-down
    CORNER_NE, // turn right-up
    CORNER_SE, // turn right-down
    T_NES, // not to West
    T_WES, // not to Nord
    T_WNE, // not to South
    T_WNS, // not to East
    CROSS, // full cross
    GRASS, // no path
    TOWER_SPOT // tower can be placed
}
