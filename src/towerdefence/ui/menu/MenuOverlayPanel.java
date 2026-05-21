package towerdefence.ui.menu;

import towerdefence.game.Game;
import towerdefence.game.GameListener;
import towerdefence.game.GameState;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;

public class MenuOverlayPanel extends JPanel implements GameListener {
    private final Game game;

    private final JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
    private final JPanel centerPanel = new JPanel();
    private final JPanel buttonColumn = new JPanel();

    private final JLabel titleLabel = new JLabel("Tower Defence");

    private final GameButton startButton = GameButton.primary("Start Game");
    private final GameButton resumeButton = GameButton.success("  Resume  ");
    private final GameButton pauseButton = GameButton.muted("Pause");
    private final GameButton settingsButton = GameButton.muted("Settings");
    private final GameButton exitButton = GameButton.danger("   Quit   ");

    public MenuOverlayPanel(Game game) {
        this.game = game;

        setOpaque(false);
        setLayout(new BorderLayout());

        MouseAdapter blocker = new MouseAdapter() {};
        addMouseListener(blocker);
        addMouseMotionListener(blocker);

        buildTopBar();
        buildCenterPanel();
        wireActions();

        add(topBar, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);

        refresh();
    }

    private void buildTopBar() {
        topBar.setOpaque(false);
        topBar.add(settingsButton);
        topBar.add(pauseButton);
    }

    private void buildCenterPanel() {
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new GridBagLayout());

        buttonColumn.setOpaque(false);
        buttonColumn.setLayout(new BoxLayout(buttonColumn, BoxLayout.Y_AXIS));
        buttonColumn.setBorder(new EmptyBorder(24, 32, 24, 32));

        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Consolas", Font.BOLD, 32));

        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        resumeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        settingsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        buttonColumn.add(titleLabel);
        buttonColumn.add(Box.createVerticalStrut(24));
        buttonColumn.add(startButton);
        buttonColumn.add(resumeButton);
        buttonColumn.add(Box.createVerticalStrut(12));
        buttonColumn.add(exitButton);

        centerPanel.add(buttonColumn);
    }

    private void wireActions() {
        startButton.addActionListener(e -> {
            game.start();
            refresh();
            requestFocusForGame();
        });

        resumeButton.addActionListener(e -> {
            game.togglePause();
            refresh();
            requestFocusForGame();
        });

        pauseButton.addActionListener(e -> {
            game.togglePause();
            refresh();
        });

        settingsButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Settings komt later.");
        });

        exitButton.addActionListener(e -> {
            game.stop();
            refresh();
        });
    }

    private void requestFocusForGame() {
        Container parent = getParent();
        if (parent != null) {
            parent.requestFocusInWindow();
        }
    }

    public void refresh() {
        GameState state = game.getGameState();

        boolean inMenu = state == GameState.MENU;
        boolean running = state == GameState.RUNNING;
        boolean paused = state == GameState.PAUSE;

        titleLabel.setText(inMenu ? "Tower Defence" : "Paused");

        startButton.setVisible(inMenu);
        resumeButton.setVisible(paused);

        pauseButton.setVisible(running);
        settingsButton.setVisible(true);

        topBar.setVisible(running || paused);
        centerPanel.setVisible(inMenu || paused);

        revalidate();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        GameState state = game.getGameState();
        if (state == GameState.MENU || state == GameState.PAUSE) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(new Color(0, 0, 0, 150));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }

    @Override
    public boolean contains(int x, int y) {
        GameState state = game.getGameState();

        if (state == GameState.MENU || state == GameState.PAUSE) {
            return true; // blokkeer alles onder de overlay
        }

        for (Component child : getComponents()) {
            if (!child.isVisible()) continue;
            Point p = SwingUtilities.convertPoint(this, x, y, child);
            if (child.contains(p)) return true;
        }
        return false;
    }

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
}