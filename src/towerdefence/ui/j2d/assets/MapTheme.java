package towerdefence.ui.j2d.assets;

import java.util.Arrays;

public enum MapTheme {
    FOLIAGE, // loofbomen
    DEN, // dennenbomen
    AUTUMN,  // herfst
    CHERRY;

    public static String[] getTypes() {
        return Arrays.stream(values())
                .map(MapTheme::folder)
                .toArray(String[]::new);
    }

    public String folder() {
        return name().toLowerCase();
    }
}
