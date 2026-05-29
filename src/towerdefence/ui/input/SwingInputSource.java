package towerdefence.ui.input;

import towerdefence.game.Game;
import towerdefence.game.input.InputCommand;
import towerdefence.game.interfaces.InputSource;
import towerdefence.util.Vector2D;
import towerdefence.system.logging.GameLogger;
import towerdefence.system.config.Config;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;


public class SwingInputSource implements InputSource {
    private final Game game;
    private final Config config;
    private final InputMapping inputMapping;
    private final JComponent component;

    private static final Logger logger = GameLogger.getLogger();
    private final Queue<InputCommand> inputQueue = new ConcurrentLinkedQueue<>();

    public SwingInputSource(Game game, Config config, InputMapping inputMapping, JComponent component) {
        this.game = game;
        this.config = config;
        this.inputMapping = inputMapping;
        this.component = component;
    }

    @Override
    public List<InputCommand> pollCommands() {
        List<InputCommand> commands = new ArrayList<>();
        InputCommand command;
        while ((command = inputQueue.poll()) != null) {
            commands.add(command);
        }
        return commands;
    }

    private Integer findTowerSpotIndexFromPoint(Point p) {
        Vector2D pScaled = inputMapping.screenToWorld(p);
        if (pScaled == null) return null;

        List<Vector2D> spots = game.getTowerSpots();

        int best = -1;
        double bestD = config.getGameWidth()*2;

        for (int i = 0; i< spots.size(); i++) {
            double d = pScaled.distanceTo(spots.get(i));
            if (d <= config.getTowerSpotRadius() && d < bestD) {
                bestD = d;
                best = i;
            }
        }

        if (best >=0) {
            return best;
        } else {
            return null;
        }
    }

    public void installHandlers() {
        component.setFocusable(true);
        component.setFocusTraversalKeysEnabled(false);
        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
            if (!SwingUtilities.isLeftMouseButton(e)) {
                return;
            }
            Integer spotIndex = findTowerSpotIndexFromPoint(e.getPoint());
            if (spotIndex != null) {
                inputQueue.add(InputCommand.openBuildMenu(spotIndex));
            } else {
                inputQueue.add(InputCommand.closeBuildMenu());
            }
            }
        });
        SwingUtilities.invokeLater(component::requestFocusInWindow);
    }

    public void queueCommand(InputCommand command) {
        inputQueue.add(command);
    }
}
