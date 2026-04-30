package towerdefence.ui.menu;

import towerdefence.game.GameState;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class MenuScreen {
    private final String title;
    private final List<MenuButton> buttons= new ArrayList<>();

    public MenuScreen(String title){
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void addButton(MenuButton button) {
        if (button != null) {
            buttons.add(button);
        }
    }

    public List<MenuButton> getAllButtons() {
        return Collections.unmodifiableList(buttons);
    }

    public List<MenuButton> getVisibleButtons(GameState state) {
        return buttons.stream()
                .filter(button -> button.isVisibleIn(state))
                .toList();
    }

    public List<MenuButton> getEnabledButtons(GameState state) {
        return buttons.stream()
                .filter(button -> button.isEnabledIn(state))
                .toList();
    }

    public Optional<MenuButton> findHovered(Point p, GameState state) {
        if (p==null) {
            return Optional.empty();
        }
        for (MenuButton button : getVisibleButtons(state)) {
            if (button.contains(p)) {
                return Optional.of(button);
            }
        }
        return Optional.empty();
    }

    public Optional<MenuButton> findClicked(Point p, GameState state) {
        if (p==null) {
            return Optional.empty();
        }
        for (MenuButton button : getEnabledButtons(state)) {
            if (button.contains(p)) {
                return Optional.of(button);
            }
        }
        return Optional.empty();
    }
}
