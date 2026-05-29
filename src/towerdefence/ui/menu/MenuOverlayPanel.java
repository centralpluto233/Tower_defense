package towerdefence.ui.menu;

import towerdefence.game.Game;
import towerdefence.game.GameListener;
import towerdefence.game.GameState;
import towerdefence.system.config.Constants;
import towerdefence.util.Vector2D;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;

public class MenuOverlayPanel extends JPanel implements GameListener {
    private final Game game;

    private static final String CARD_MENU      = "MENU";
    private static final String CARD_PAUSE     = "PAUSE";
    private static final String CARD_GAME_OVER = "GAME_OVER";
    private static final String CARD_LEVEL_WON = "LEVEL_WON";
    private static final String CARD_LEVELS    = "LEVELS";

    private final JPanel topBar      = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
    private final JPanel centerPanel = new JPanel();
    private final JPanel cards       = new JPanel(new CardLayout());

    private final GameButton pauseButton = GameButton.muted("Pause");

    public MenuOverlayPanel(Game game) {
        this.game = game;
        setOpaque(false);
        setLayout(new BorderLayout());

        MouseAdapter blocker = new MouseAdapter() {};
        addMouseListener(blocker);
        addMouseMotionListener(blocker);

        buildTopBar();
        buildCenterPanel();
        wireTopBar();

        add(topBar, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);

        refresh();
    }

    // ── factory methods voor herbruikbare buttons ─────────────────────────────

    private GameButton makeSettingsButton() {
        GameButton btn = GameButton.muted(" Settings ");
        btn.addActionListener(_ -> JOptionPane.showMessageDialog(this, "Settings komt later."));
        return btn;
    }

    private GameButton makeExitButton() {
        GameButton btn = GameButton.danger("   Quit   ");
        btn.addActionListener(_ -> { game.exit(); refresh(); });
        return btn;
    }

    private GameButton makeLevelButton() {
        GameButton btn = GameButton.muted("   Level  ");
        btn.addActionListener(_ -> showCard(CARD_LEVELS));
        return btn;
    }

    // ── top bar ───────────────────────────────────────────────────────────────

    private void buildTopBar() {
        topBar.setOpaque(false);
        topBar.add(pauseButton);
    }

    private void wireTopBar() {
        pauseButton.addActionListener(_ -> { game.togglePause(); refresh(); });
    }

    // ── cards ─────────────────────────────────────────────────────────────────

    private void buildCenterPanel() {
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new GridBagLayout());

        cards.setOpaque(false);
        cards.add(buildMainMenuCard(),    CARD_MENU);
        cards.add(buildPauseCard(),       CARD_PAUSE);
        cards.add(buildGameOverCard(),    CARD_GAME_OVER);
        cards.add(buildLevelWonCard(),    CARD_LEVEL_WON);
        cards.add(buildLevelPickerCard(), CARD_LEVELS);

        centerPanel.add(cards);
    }

    private JPanel buildMainMenuCard() {
        GameButton startButton = GameButton.primary("Start Game");
        startButton.addActionListener(_ -> { game.start(); refresh(); requestFocusForGame(); });

        return column("Tower Defence", startButton, makeLevelButton(), makeSettingsButton(), makeExitButton());
    }

    private JPanel buildPauseCard() {
        GameButton resumeButton = GameButton.success("  Resume  ");
        resumeButton.addActionListener(_ -> { game.togglePause(); refresh(); requestFocusForGame(); });

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
        return column("Level Won!", makeLevelButton(), makeSettingsButton(), makeExitButton());
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

    // ── helpers ───────────────────────────────────────────────────────────────

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

    // ── refresh ───────────────────────────────────────────────────────────────

    public void refresh() {
        GameState state = game.getGameState();
        boolean running = state == GameState.RUNNING;
        boolean paused  = state == GameState.PAUSE;

        topBar.setVisible(running || paused);
        pauseButton.setVisible(running);
        centerPanel.setVisible(state != GameState.RUNNING);

        String card = switch (state) {
            case MENU      -> CARD_MENU;
            case PAUSE     -> CARD_PAUSE;
            case GAME_OVER -> CARD_GAME_OVER;
            case LEVEL_WON -> CARD_LEVEL_WON;
            default        -> CARD_MENU;
        };
        showCard(card);

        revalidate();
        repaint();
    }

    // ── paint & input blocking ────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        GameState state = game.getGameState();
        if (state == GameState.MENU || state == GameState.PAUSE
                || state == GameState.GAME_OVER || state == GameState.LEVEL_WON) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(new Color(0, 0, 0, 150));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }

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

    // ── GameListener ──────────────────────────────────────────────────────────

    @Override public void onGameStarted()  { refresh(); }
    @Override public void onGamePaused()   { refresh(); }
    @Override public void onGameResumed()  { refresh(); }
    @Override public void onGameStopped()  { refresh(); }
    @Override public void onGameOver()     { refresh(); }
    @Override public void onLevelWon()     { refresh(); }
    @Override public void onBuildMenuRequested(int i, Vector2D v) {}
    @Override public void onBuildMenuClosed() {}
}