package towerdefence.ui.menu;

import towerdefence.game.Game;
import towerdefence.game.GameListener;
import towerdefence.game.entities.tower.TowerOption;
import towerdefence.game.input.InputCommand;
import towerdefence.system.config.Constants;
import towerdefence.ui.input.InputMapping;
import towerdefence.ui.input.SwingInputSource;
import towerdefence.util.Vector2D;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class BuildMenuPanel extends JPanel implements GameListener {
    private Vector2D selectedSpot;
    private Consumer<InputCommand> commandSink = _ -> {};
    private int selectedSpotIndex;
    private InputMapping inputMapping;

    private final JPanel towerOptionPanel = new JPanel();

    private static final List<TowerOption> towerOptions = List.of(
            new TowerOption("Archer", Constants.TowerConstants.Archer.COST),
            new TowerOption("Crossbow", Constants.TowerConstants.CrossBow.COST)
    );

    public BuildMenuPanel(InputMapping inputMapping) {
        this.inputMapping = inputMapping;
        setOpaque(false);
        setLayout(null);

        towerOptionPanel.setLayout(new BoxLayout(towerOptionPanel, BoxLayout.Y_AXIS));
        towerOptionPanel.setBackground(new Color(30,30,30,220));
        towerOptionPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        for (TowerOption towerOption : towerOptions) {
            JButton button = new JButton(towerOption.type());
            button.addActionListener(_ -> {
                commandSink.accept(InputCommand.placeTowerAtSpot(selectedSpotIndex,towerOption.type()));
            closeBuildMenu();
            });
            towerOptionPanel.add(button);
        }

        add(towerOptionPanel);
        setVisible(false);
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
