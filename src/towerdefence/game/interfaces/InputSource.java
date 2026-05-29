package towerdefence.game.interfaces;

import towerdefence.game.input.InputCommand;

import java.util.List;

public interface InputSource {
    List<InputCommand> pollCommands();

    /**
     * Installs necessary event handlers for the input source. This is called after the game view is created, allowing the input source to attach listeners to the view's components if needed.
     */
    default void installHandlers() {
    }

}
