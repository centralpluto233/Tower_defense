package towerdefence.ui.menu;

import towerdefence.game.GameState;
import towerdefence.game.input.InputAction;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MenuFactory {
    private MenuFactory() {}

    public static Map<String, MenuScreen> createMenus() {
        Map<String, MenuScreen> menus = new HashMap<>();

        MenuScreen main = new MenuScreen("Tower Defence");
        main.addButton(new MenuButton("Start Game",
                280,220,240,50,
                InputAction.START_GAME,
                Set.of(GameState.MENU),
                Set.of(GameState.MENU),
                new Color(50, 120, 220),
                new Color(80, 150, 250),
                new Color(90, 90, 90),
                Color.WHITE));
        main.addButton(new MenuButton(
                "Quit",
                280, 290, 240, 50,
                InputAction.QUIT_GAME,
                Set.of(GameState.MENU),
                Set.of(GameState.MENU),
                new Color(160, 60, 60),
                new Color(190, 90, 90),
                new Color(90, 90, 90),
                Color.WHITE
        ));
        menus.put("main", main);

        return menus;
    }
}
