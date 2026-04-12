package towerdefence.game.input;

public record InputCommand(InputAction action, int spotIndex) {

    public static InputCommand placeTowerAtSpot(int spotIndex) {
        return new InputCommand(InputAction.PLACE_TOWER_AT_INDEX, spotIndex);
    }

    public static InputCommand pauseToggled() {
        return new InputCommand(InputAction.PAUSE_TOGGLE, -1);
    }

    public static InputCommand quitGame() {
        return new InputCommand(InputAction.QUIT_GAME, -1);
    }
}
