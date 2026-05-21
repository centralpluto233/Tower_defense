package towerdefence.ui;

import towerdefence.game.Game;
import towerdefence.system.config.Config;
import towerdefence.ui.j2d.J2dGamePanel;
import towerdefence.ui.menu.MainMenuPanel;
import towerdefence.ui.menu.PauseMenuPanel;
import towerdefence.ui.menu.SettingsPanel;

import javax.swing.*;
import java.awt.*;

public class PanelManager {
    private final Game game;
    private final JFrame frame;
    private final JPanel rootPanel;
    private final CardLayout cardLayout;

    private final J2dGamePanel gamePanel;
    private final MainMenuPanel mainMenuPanel;
    private final PauseMenuPanel pauseMenuPanel;
    private final SettingsPanel settingsPanel;

    public PanelManager(Game game, Config config) {
        this.game = game;

        this.frame = new JFrame(config.getWindowTitle());
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.frame.setUndecorated(true);

        this.cardLayout = new CardLayout();
        this.rootPanel = new JPanel(cardLayout);
        this.gamePanel = new J2dGamePanel(game, config);

        this.mainMenuPanel = new MainMenuPanel(
                this::startGame,
                this::showSettingsFromMenu
        );

        this.pauseMenuPanel = new PauseMenuPanel(
                this::resumeGame,
                this::showSettingsFromPause,
                this::returnToMainMenu
        );

        this.settingsPanel = new SettingsPanel(this::backToPreviousMenu);

        rootPanel.add(mainMenuPanel, "MAIN_MENU");
        rootPanel.add(gamePanel, "GAME");
        rootPanel.add(pauseMenuPanel, "PAUSE");
        rootPanel.add(settingsPanel, "SETTINGS");

        frame.setContentPane(rootPanel);
    }

    public void show() {
        frame.setVisible(true);
        showMainMenu();
    }

    public void showMainMenu() {
        cardLayout.show(rootPanel, "MAIN_MENU");
    }

    public void showGame() {
        cardLayout.show(rootPanel, "GAME");
        SwingUtilities.invokeLater(gamePanel::requestFocusInWindow);
    }

    public void showPauseMenu() {
        cardLayout.show(rootPanel, "PAUSE");
    }

    public void showSettingsFromMenu() {
        cardLayout.show(rootPanel, "SETTINGS");
    }

    public void showSettingsFromPause() {
        cardLayout.show(rootPanel, "SETTINGS");
    }

    private void backToPreviousMenu() {
        if (game.getGameState() == towerdefence.game.GameState.PAUSE) {
            showPauseMenu();
        } else {
            showMainMenu();
        }
    }

    private void startGame() {
        game.start();
        showGame();
    }

    private void resumeGame() {
        game.togglePause();
        showGame();
    }

    private void returnToMainMenu() {
        game.setMenuState(towerdefence.game.MenuState.MAIN_MENU);
        showMainMenu();
    }
}