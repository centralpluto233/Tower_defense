package towerdefence.ui.menu;

import towerdefence.game.GameListener;
import towerdefence.game.entities.tower.TowerOption;
import towerdefence.game.input.InputCommand;
import towerdefence.system.config.Constants;
import towerdefence.ui.input.InputMapping;
import towerdefence.ui.j2d.assets.AssetManager;
import towerdefence.util.Vector2D;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;
import java.awt.image.BufferedImage;

public class BuildMenuPanel extends JPanel implements GameListener {
    private Vector2D selectedSpot;
    private Consumer<InputCommand> commandSink = _ -> {};
    private int selectedSpotIndex;
    private InputMapping inputMapping;

    private final JPanel towerOptionPanel = new JPanel();

    private static final List<TowerOption> towerOptions = List.of(
            new TowerOption("Archer",   Constants.TowerConstants.Archer.COST,
                    "sprites/entities/towers/archer_icon.png",
                    Constants.TowerConstants.Archer.WIDTH,
                    Constants.TowerConstants.Archer.HEIGHT),
            new TowerOption("Crossbow", Constants.TowerConstants.CrossBow.COST,
                    "sprites/entities/towers/crossbow.png",
                    Constants.TowerConstants.CrossBow.WIDTH,
                    Constants.TowerConstants.CrossBow.HEIGHT)
    );
    //TODO: images in constants and preloaded at start
    //TODO: full clean up of classes, functions, tasks, ....

    public BuildMenuPanel(InputMapping inputMapping, AssetManager assetManager) {
        this.inputMapping = inputMapping;
        setOpaque(false);
        setLayout(null);

        towerOptionPanel.setLayout(new BoxLayout(towerOptionPanel, BoxLayout.Y_AXIS));
        towerOptionPanel.setBackground(new Color(30,30,30,220));
        towerOptionPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        for (TowerOption towerOption : towerOptions) {
            JButton button = createTowerCard(towerOption, assetManager);
            button.addActionListener(_ -> {
                commandSink.accept(InputCommand.placeTowerAtSpot(selectedSpotIndex, towerOption.type()));
                closeBuildMenu();
            });
            towerOptionPanel.add(button);
            towerOptionPanel.add(Box.createVerticalStrut(4));
        }

        add(towerOptionPanel);
        setVisible(false);
    }

    private JButton createTowerCard(TowerOption option, AssetManager assets) {
        java.awt.image.BufferedImage sprite = assets.getSprite(option.spritePath());
        Image icon = scaleForMenu(sprite, option, 40);

        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isRollover()) {
                    g2.setColor(new Color(80, 80, 80, 220));
                } else {
                    g2.setColor(new Color(50, 50, 50, 200));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                int iconW = icon.getWidth(null);
                int iconH = icon.getHeight(null);
                int iconX = 8 + (40 - iconW) / 2;
                int iconY = 8 + (40 - iconH) / 2;
                g2.drawImage(icon, iconX, iconY, null);

                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 13));
                g2.drawString(option.type(), 56, 22);

                g2.setColor(new Color(255, 210, 60));
                g2.setFont(new Font("Arial", Font.PLAIN, 12));
                g2.drawString("€ " + option.cost(), 56, 40);

                g2.dispose();
            }
        };

        btn.setPreferredSize(new Dimension(160, 56));
        btn.setMaximumSize(new Dimension(160, 56));
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private Image scaleForMenu(BufferedImage src, TowerOption option, int boxSize) {
        double tallest = towerOptions.stream()
                .mapToDouble(TowerOption::gameHeight)
                .max().orElse(1);
        double scale = (boxSize / tallest) * option.gameHeight() / src.getHeight();
        int newW = (int)(src.getWidth()  * scale);
        int newH = (int)(src.getHeight() * scale);
        return src.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
    }

    public void showAt(Vector2D spot, Point screenPos) {
        this.selectedSpot = spot;
        towerOptionPanel.setSize(towerOptionPanel.getPreferredSize());

        int px = Math.min(screenPos.x, getWidth()  - towerOptionPanel.getWidth()  - 8);
        int py = Math.min(screenPos.y, getHeight() - towerOptionPanel.getHeight() - 8);
        towerOptionPanel.setLocation(px, py);

        setVisible(true);
        repaint();
    }

    public void setCommandSink(Consumer<InputCommand> sink) {
        this.commandSink = sink;
    }

    public void closeBuildMenu() {
        setVisible(false);
    }

    @Override
    public boolean contains(int x, int y) {
        if (!isVisible()) return false;
        Point p = SwingUtilities.convertPoint(this, x, y, towerOptionPanel);
        return towerOptionPanel.contains(p);
    }

    @Override
    public void onBuildMenuRequested(int spotIndex, Vector2D spotPosition) {
        this.selectedSpot = spotPosition;
        this.selectedSpotIndex = spotIndex;
        Point screenPos = inputMapping.worldToScreen(spotPosition);
        showAt(spotPosition, screenPos);
    }


    @Override public void onGameStarted() {}
    @Override public void onGamePaused() {closeBuildMenu();}
    @Override public void onGameResumed() {}
    @Override public void onGameStopped() {closeBuildMenu();}
    @Override public void onGameOver() {closeBuildMenu();}
    @Override public void onLevelWon() {closeBuildMenu();}
    @Override public void onBuildMenuClosed() {closeBuildMenu();}
}
