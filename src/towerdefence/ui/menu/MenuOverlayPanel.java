package towerdefence.ui.menu;

import towerdefence.game.Game;
import towerdefence.game.GameListener;
import towerdefence.game.GameState;
import towerdefence.game.entities.tower.Tower;
import towerdefence.system.config.Config;
import towerdefence.system.config.Constants;
import towerdefence.ui.j2d.assets.AssetManager;
import towerdefence.ui.j2d.assets.MapTheme;
import towerdefence.util.Vector2D;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

public class MenuOverlayPanel extends JPanel implements GameListener {
    private final Game game;
    private final Config config;
    private final Consumer<MapTheme> onThemeChanged;

    private static final String CARD_MENU = "MENU";
    private static final String CARD_PAUSE = "PAUSE";
    private static final String CARD_GAME_OVER = "GAME_OVER";
    private static final String CARD_LEVEL_WON = "LEVEL_WON";
    private static final String CARD_LEVELS = "LEVELS";
    private static final String SETTINGS = "SETTINGS";

    private final JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
    private final JPanel centerPanel = new JPanel();
    private final JPanel cards = new JPanel(new CardLayout());

    private final GameButton pauseButton;
    private final GameButton nextLevelButton = GameButton.primary(" Continue ");

    private final Cursor hoverCursor;

    private BufferedImage background;

    /**
     * Constructor for the menu overlay panel, which displays the main menu, pause menu, game over screen, and level won screen.
     * @param game the game, used to control game state and listen for game events
     * @param config the config, used to read and write settings
     * @param hover the cursor to use when hovering over buttons
     * @param onThemeChanged callback for when the map theme is changed in the settings, used to update the background image
     * @param assetManager the asset manager, used to load sprites for the background and buttons
     */
    public MenuOverlayPanel(Game game, Config config, Cursor hover, Consumer<MapTheme> onThemeChanged, AssetManager assetManager) {
        this.game = game;
        this.hoverCursor = hover;
        this.config=config;
        this.onThemeChanged = onThemeChanged;

        this.pauseButton = GameButton.icon(assetManager.getSprite("sprites/hud/pause.png"),5);

        updateThemeBackground(MapTheme.valueOf(config.getMapTheme().toUpperCase()),assetManager);

        setOpaque(false);
        setLayout(new BorderLayout());

        MouseAdapter blocker = new MouseAdapter() {
        };
        addMouseListener(blocker);
        addMouseMotionListener(blocker);

        buildTopBar();
        buildCenterPanel();
        buildSettingsCard();
        wireTopBar();

        add(topBar, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);

        refresh();
        applyCustomCursors(this);
    }

    // ----------------------------------------------------------
    // factory methods voor herbruikbare buttons
    // ----------------------------------------------------------

    private GameButton makeSettingsButton() {
        GameButton btn = GameButton.muted(" Settings ");
        btn.addActionListener(_ -> showCard(SETTINGS));
        return btn;
    }

    private GameButton makeExitButton() {
        GameButton btn = GameButton.danger("   Quit   ");
        btn.addActionListener(_ -> {
            game.exit();
            refresh();
        });
        return btn;
    }

    private GameButton makeLevelButton() {
        GameButton btn = GameButton.primary("   Level  ");
        btn.addActionListener(_ -> showCard(CARD_LEVELS));
        return btn;
    }

    // ----------------------------------
    // top bar
    // ----------------------------------

    private void buildTopBar() {
        topBar.setOpaque(false);
        topBar.add(pauseButton);
    }

    private void wireTopBar() {
        pauseButton.addActionListener(_ -> {
            game.togglePause();
            refresh();
        });
    }

    // ---------------------------------------
    // cards
    // ---------------------------------------

    private void buildCenterPanel() {
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new GridBagLayout());

        cards.setOpaque(false);
        cards.add(buildMainMenuCard(), CARD_MENU);
        cards.add(buildPauseCard(), CARD_PAUSE);
        cards.add(buildGameOverCard(), CARD_GAME_OVER);
        cards.add(buildLevelWonCard(), CARD_LEVEL_WON);
        cards.add(buildLevelPickerCard(), CARD_LEVELS);
        cards.add(buildSettingsCard(), SETTINGS);

        centerPanel.add(cards);
    }

    private JPanel buildMainMenuCard() {
        GameButton startButton = GameButton.primary("Start Game");
        startButton.addActionListener(_ -> {
            game.start();
            refresh();
            requestFocusForGame();
        });

        return column("Tower Defence", startButton, makeLevelButton(), makeSettingsButton(), makeExitButton());
    }

    private JPanel buildPauseCard() {
        GameButton resumeButton = GameButton.success("  Resume  ");
        resumeButton.addActionListener(_ -> {
            game.togglePause();
            refresh();
            requestFocusForGame();
        });

        return column("Paused", resumeButton, makeSettingsButton(), makeExitButton());
    }

    private JPanel buildGameOverCard() {
        GameButton retryButton = GameButton.primary("   Retry  ");
        retryButton.addActionListener(_ -> {
            game.stop();
            game.start();
        });
        return column("Game Over", retryButton, makeLevelButton(), makeSettingsButton(), makeExitButton());
    }

    private JPanel buildLevelWonCard() {
        nextLevelButton.addActionListener(_ -> game.getNextLevel().ifPresent(nextLevel -> {
            game.loadLevel(nextLevel);
            game.start();
            refresh();
            requestFocusForGame();
        }));

        return column("Level Won!", nextLevelButton, makeLevelButton(), makeSettingsButton(), makeExitButton());
    }

    private JPanel buildSettingsCard() {
        JPanel panel = emptyColumn();
        panel.add(styledLabel("Settings", 32));
        panel.add(Box.createVerticalStrut(24));

        ToggleButton fpsToggle = new ToggleButton("Debugmode", config.getDebug());
        fpsToggle.setAlignmentX(Component.CENTER_ALIGNMENT);
        fpsToggle.addActionListener(_ -> {
            config.setDebug(fpsToggle.isSelected());
            config.save();
        });
        panel.add(fpsToggle);
        panel.add(Box.createVerticalStrut(16));

        panel.add(styledLabel("Map Theme", 16));
        panel.add(Box.createVerticalStrut(8));

        DropdownMenu<String> themeBox = getDropdownMenu();
        panel.add(themeBox);
        panel.add(Box.createVerticalStrut(16));

        GameButton backButton = GameButton.muted("  Back  ");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener(_ -> refresh());
        panel.add(backButton);

        return panel;
    }

    private DropdownMenu<String> getDropdownMenu() {
        DropdownMenu<String> themeBox = new DropdownMenu<>(MapTheme.getTypes());
        themeBox.setCursor(hoverCursor);
        themeBox.setSelectedItem(config.getMapTheme());
        themeBox.setMaximumSize(new Dimension(220, 40));
        themeBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        themeBox.addActionListener(_ -> {
            String selected = (String) themeBox.getSelectedItem();
            if (selected != null) {
                config.setMapTheme(selected);
                config.save();
                onThemeChanged.accept(MapTheme.valueOf(selected.toUpperCase()));
            }
        });
        return themeBox;
    }

    private JPanel buildLevelPickerCard() {
        JPanel panel = emptyColumn();
        panel.add(styledLabel("Select Level", 32));
        panel.add(Box.createVerticalStrut(24));

        for (String level : Constants.LevelConstants.LEVEL_NAMES) {
            GameButton btn = GameButton.primary(level.substring(0, level.lastIndexOf('.')));
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.addActionListener(_ -> {
                game.loadLevel(level);
                game.start();
                refresh();
                requestFocusForGame();
            });
            panel.add(btn);
            panel.add(Box.createVerticalStrut(8));
        }

        GameButton backButton = GameButton.muted("  Back  ");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener(_ -> refresh());
        panel.add(Box.createVerticalStrut(12));
        panel.add(backButton);

        return panel;
    }

    public void updateThemeBackground(MapTheme theme, AssetManager assets) {
        this.background = assets.getSprite("sprites/map/" + theme.folder() + "/background.png");
    }

    // --------------------------------------------
    // helpers
    // --------------------------------------------

    private void applyCustomCursors(Container container) {
        for (Component c: container.getComponents()) {
            if (c instanceof AbstractButton btn) {
                btn.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        btn.setCursor(hoverCursor);
                    }
                    @Override
                    public void mouseExited(MouseEvent e) {
                        btn.setCursor(null);
                    }
                });
            }
            if (c instanceof Container sub) {
                applyCustomCursors(sub);
            }
        }
    }

    private JPanel column(String title, GameButton... buttons) {
        JPanel panel = emptyColumn();
        panel.add(styledLabel(title, 32));
        panel.add(Box.createVerticalStrut(24));
        for (GameButton btn : buttons) {
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(btn);
            panel.add(Box.createVerticalStrut(12));
        }
        return panel;
    }

    private JPanel emptyColumn() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(24, 32, 24, 32));
        return panel;
    }

    private JLabel styledLabel(String text, int size) {
        JLabel label = new JLabel(text);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Consolas", Font.BOLD, size));
        return label;
    }

    private void showCard(String card) {
        ((CardLayout) cards.getLayout()).show(cards, card);
    }

    private void requestFocusForGame() {
        Container parent = getParent();
        if (parent != null) parent.requestFocusInWindow();
    }



    // refresh

    /**
     * Refreshes the menu overlay based on the current game state, showing or hiding components and switching cards as necessary.
     */
    public void refresh() {
        GameState state = game.getGameState();
        boolean running = state == GameState.RUNNING;
        boolean paused = state == GameState.PAUSE;

        topBar.setVisible(running || paused);
        pauseButton.setVisible(running);
        nextLevelButton.setVisible(game.getNextLevel().isPresent());
        centerPanel.setVisible(state != GameState.RUNNING);

        String card = switch (state) {
            case PAUSE -> CARD_PAUSE;
            case GAME_OVER -> CARD_GAME_OVER;
            case LEVEL_WON -> CARD_LEVEL_WON;
            default -> CARD_MENU;
        };
        showCard(card);

        revalidate();
        repaint();
    }

    // paint & input blocking

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        GameState state = game.getGameState();

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        if (state == GameState.MENU) {

            if (background != null) {
                g2.drawImage(background, 0, 0, getWidth(), getHeight(), null);
            }
            g2.setColor(new Color(0, 0, 0, 140));
            g2.fillRect(0, 0, getWidth(), getHeight());

        } else if (state == GameState.PAUSE) {

            g2.setColor(new Color(0, 0, 0, 150));
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

        g2.dispose();
    }

    /**
     * Overrides the contains method to block input to the game when the menu overlay is visible, while still allowing interaction with the menu components. It checks if the current game state is one where the menu should be active, and if so, it returns true to indicate that the overlay should consume mouse events.
     * If the game is running, it checks if any child components of the overlay contain the point, allowing interaction with buttons and other UI elements.
     * @param x   the <i>x</i> coordinate of the point
     * @param y   the <i>y</i> coordinate of the point
     * @return
     */
    @Override
    public boolean contains(int x, int y) {
        GameState state = game.getGameState();
        if (state == GameState.MENU || state == GameState.PAUSE
                || state == GameState.GAME_OVER || state == GameState.LEVEL_WON) {
            return true;
        }
        for (Component child : getComponents()) {
            if (!child.isVisible()) continue;
            Point p = SwingUtilities.convertPoint(this, x, y, child);
            if (child.contains(p)) return true;
        }
        return false;
    }

    // ----------------------------------------
    // GameListener
    // ----------------------------------------

    @Override
    public void onGameStarted() {
        refresh();
    }

    @Override
    public void onGamePaused() {
        refresh();
    }

    @Override
    public void onGameResumed() {
        refresh();
    }

    @Override
    public void onGameStopped() {
        refresh();
    }

    @Override
    public void onGameOver() {
        refresh();
    }

    @Override
    public void onLevelWon() {
        refresh();
    }

    @Override
    public void onBuildMenuRequested(int i, Vector2D v) {
    }

    @Override
    public void onBuildMenuClosed() {
    }

    @Override
    public void onTowerSelected(Tower tower, int index) {}
}