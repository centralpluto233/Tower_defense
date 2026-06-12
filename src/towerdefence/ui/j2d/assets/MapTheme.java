package towerdefence.ui.j2d.assets;

import java.util.Arrays;

public enum MapTheme {
    FOLIAGE, // loofbomen
    DEN, // dennenbomen
    AUTUMN; // herfst

    public String folder() {
        return name().toLowerCase();
    }

    public static String[] getTypes() {
        return Arrays.stream(values())
                .map(MapTheme::folder)
                .toArray(String[]::new);
    }
}
