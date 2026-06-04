package towerdefence.game.input;

import java.util.Optional;

public record InputCommand(InputAction action, Optional<Integer> spotIndex, Optional<String> towerType) {

    /**
     * Creates an InputCommand to open the build menu for a specific tower spot.
     *
     * @param spotIndex the index of the tower spot to open the build menu for
     * @return an InputCommand to open the build menu for the specified tower spot
     */
    public static InputCommand openBuildMenu(Integer spotIndex) {
        return new InputCommand(InputAction.PLACE_TOWER_AT_INDEX, Optional.of(spotIndex), Optional.empty());
    }

    /**
     * Creates an InputCommand to close the build menu.
     *
     * @return an InputCommand to close the build menu
     */
    public static InputCommand closeBuildMenu() {
        return new InputCommand(InputAction.CLOSE_BUILD_MENU, Optional.empty(), Optional.empty());
    }

    /**
     * Creates an InputCommand to place a tower of the specified type at the specified tower spot.
     *
     * @param spotIndex the index of the tower spot to place the tower at
     * @param towerType the type of tower to place (e.g. "Archer", "Crossbow")
     * @return an InputCommand to place a tower of the specified type at the specified tower spot
     */
    public static InputCommand placeTowerAtSpot(Integer spotIndex, String towerType) {
        return new InputCommand(InputAction.PLACE_TOWER_AT_INDEX, Optional.of(spotIndex), Optional.of(towerType));
    }
}
