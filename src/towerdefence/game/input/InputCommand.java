package towerdefence.game.input;

import java.util.Optional;

public record InputCommand(InputAction action, int spotIndex, Optional<String> towerType) {

    public static InputCommand openBuildMenu(int spotIndex) {
        return new InputCommand(InputAction.PLACE_TOWER_AT_INDEX, spotIndex, Optional.empty());
    }

    public static InputCommand placeTowerAtSpot(int spotIndex, String towerType) {
        return new InputCommand(InputAction.PLACE_TOWER_AT_INDEX, spotIndex, Optional.of(towerType));
    }

    public static InputCommand pauseToggled() {
        return new InputCommand(InputAction.PAUSE_TOGGLE, -1, Optional.empty());
    }

    public static InputCommand quitGame() {
        return new InputCommand(InputAction.QUIT_GAME, -1, Optional.empty());
    }
}
