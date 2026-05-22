package towerdefence.game.interfaces;

import towerdefence.game.input.InputCommand;

import java.util.List;

public interface InputSource {
    List<InputCommand> pollCommands();

    default void installHandlers() {}

}
