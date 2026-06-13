package towerdefence.level;

public enum TileType {
    PATH_H, // horizontaal
    PATH_V, // verticaal
    CORNER_NW, // bocht links-omhoog
    CORNER_SW, // bocht links-omlaag
    CORNER_NE, // bocht rechts-omhoog
    CORNER_SE, // bocht rechts-omlaag
    T_NES, // niet naar west
    T_WES, // niet naar noord
    T_WNE, // niet naar zuid
    T_WNS, // niet naar oost
    CROSS, // volledig
    GRASS, // no path
    TOWER_SPOT // tower can be placed
}
