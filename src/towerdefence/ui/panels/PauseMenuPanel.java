package towerdefence.ui.menu;

import javax.swing.*;
import java.awt.*;

public class PauseMenuPanel extends JPanel {
    public PauseMenuPanel(Runnable onResume, Runnable onSettings, Runnable onMainMenu) {
        setLayout(null);
        setBackground(new Color(0, 0, 0, 180));

        JLabel label = new JLabel("Paused");
        label.setForeground(Color.WHITE);
        label.setBounds(20, 20, 300, 40);
        add(label);

        JButton resumeButton = new JButton("Resume");
        resumeButton.setBounds(280, 220, 240, 50);
        resumeButton.addActionListener(e -> onResume.run());
        add(resumeButton);

        JButton settingsButton = new JButton("Settings");
        settingsButton.setBounds(280, 290, 240, 50);
        settingsButton.addActionListener(e -> onSettings.run());
        add(settingsButton);

        JButton menuButton = new JButton("Main Menu");
        menuButton.setBounds(280, 360, 240, 50);
        menuButton.addActionListener(e -> onMainMenu.run());
        add(menuButton);
    }
}