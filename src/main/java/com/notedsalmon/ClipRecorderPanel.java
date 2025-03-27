package com.notedsalmon;

import net.runelite.client.ui.PluginPanel;
import javax.swing.*;
import java.awt.*;

public class ClipRecorderPanel extends PluginPanel {
    private final ClipRecorderPlugin plugin;

    public ClipRecorderPanel(ClipRecorderPlugin plugin) {
        this.plugin = plugin;
        buildPanel();
    }

    private void buildPanel() {
        setLayout(new BorderLayout());

        JButton testButton = new JButton("Test Recording");
        testButton.addActionListener(e -> plugin.testRecording()); // Call the plugin method

        add(testButton, BorderLayout.CENTER);
    }
}
