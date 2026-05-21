package towerdefence.ui.j2d;

import towerdefence.game.Game;
import towerdefence.game.GameListener;
import towerdefence.game.GameState;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MenuOverlayPanel extends JPanel implements GameListener {
    private final Game game;

    private final JButton startButton = new JButton("Start Game");
    private final JButton pauseButton = new JButton("Pause");
    private final JButton resumeButton = new JButton("Resume");
    private final JButton settingsButton = new JButton("Settings");

    private final ClickThroughPanel topBar = new ClickThroughPanel();
    private final ClickThroughPanel centerBox = new ClickThroughPanel();

    public MenuOverlayPanel(Game game) {
        this.game = game;

        setOpaque(false);
        setLayout(new BorderLayout());

        topBar.setOpaque(false);
        topBar.setBorder(new EmptyBorder(12, 12, 12, 12));
        topBar.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 0));

        centerBox.setOpaque(false);
        centerBox.setLayout(new BoxLayout(centerBox, BoxLayout.Y_AXIS));
        centerBox.setBorder(new EmptyBorder(140, 0, 140, 0));

        styleButton(startButton);
        styleButton(pauseButton);
        styleButton(resumeButton);
        styleButton(settingsButton);

        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        resumeButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        topBar.add(settingsButton);
        topBar.add(pauseButton);

        centerBox.add(Box.createVerticalGlue());
        centerBox.add(startButton);
        centerBox.add(Box.createVerticalStrut(12));
        centerBox.add(resumeButton);
        centerBox.add(Box.createVerticalGlue());

        add(topBar, BorderLayout.NORTH);
        add(centerBox, BorderLayout.CENTER);

        startButton.addActionListener(e -> {
            game.start();
            requestGameFocus();
            refresh(game.getGameState());
        });

        pauseButton.addActionListener(e -> {
            game.togglePause();
            refresh(game.getGameState());
        });

        resumeButton.addActionListener(e -> {
            game.togglePause();
            requestGameFocus();
            refresh(game.getGameState());
        });

        settingsButton.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Settings screen comes later.")
        );

        refresh(game.getGameState());
    }

    private void styleButton(JButton button) {
        button.setFocusPainted(false);
        button.setFont(new Font("Consolas", Font.BOLD, 18));
        button.setPreferredSize(new Dimension(180, 42));
    }

    private void requestGameFocus() {
        Container parent = getParent();
        if (parent != null) {
            parent.requestFocusInWindow();
        }
    }

    public void refresh(GameState state) {
        boolean inMenu = state == GameState.MENU;
        boolean running = state == GameState.RUNNING;
        boolean paused = state == GameState.PAUSE;

        startButton.setVisible(inMenu);
        resumeButton.setVisible(paused);
        pauseButton.setVisible(running);
        settingsButton.setVisible(inMenu || running || paused);

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
        for (Component child : getComponents()) {
            if (!child.isVisible()) continue;
            Point p = SwingUtilities.convertPoint(this, x, y, child);
            if (child.contains(p)) return true;
        }
        return false;
    }

    @Override
    public void onGameStarted() {
        refresh(GameState.RUNNING);
    }

    @Override
    public void onGamePaused() {
        refresh(GameState.PAUSE);
    }

    @Override
    public void onGameResumed() {
        refresh(GameState.RUNNING);
    }

    @Override
    public void onGameStopped() {
        refresh(GameState.MENU);
    }

    @Override
    public void onGameOver() {
        refresh(GameState.MENU);
    }

    @Override
    public void onLevelWon() {
        refresh(GameState.MENU);
    }

    private static class ClickThroughPanel extends JPanel {
        @Override
        public boolean contains(int x, int y) {
            for (Component child : getComponents()) {
                if (!child.isVisible()) continue;
                Point p = SwingUtilities.convertPoint(this, x, y, child);
                if (child.contains(p)) return true;
            }
            return false;
        }
    }
}