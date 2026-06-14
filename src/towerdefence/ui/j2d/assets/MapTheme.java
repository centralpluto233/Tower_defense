package towerdefence.ui.j2d.assets;

import java.util.Arrays;

// Enum representing different map themes for the game, which can be used to load different sets of assets for each theme.
public enum MapTheme {
    FOLIAGE, // loofbomen
    DEN, // dennenbomen
    AUTUMN,  // herfst
    CHERRY;

    /**
     * Gets an array of the folder names corresponding to each MapTheme, which can be used to load the appropriate assets for each theme.
     *
     * @return an array of folder names for each MapTheme, in the same order as the enum values
     */
    public static String[] getTypes() {
        return Arrays.stream(values())
                .map(MapTheme::folder)
                .toArray(String[]::new);
    }

    /**
     * Converts the name of the MapTheme enum value to lowercase to get the corresponding folder name for asset loading.
     *
     * @return the folder name corresponding to this MapTheme, which is the lowercase version of the enum name
     */
    public String folder() {
        return name().toLowerCase();
    }
}
