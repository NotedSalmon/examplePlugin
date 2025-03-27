package com.notedsalmon;

import com.google.inject.Provides;
import javax.inject.Inject;
import javax.swing.*;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.ImageUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Slf4j
@PluginDescriptor(
		name = "Clip Recorder"
)
public class ClipRecorderPlugin extends Plugin {

	@Inject
	private Client client;

	@Inject
	private ClipRecorderConfig config;

	@Inject
	private ClientToolbar clientToolbar;

	private volatile ScreenRecorder screenRecorder;
	private ClipRecorderPanel panel;
	private NavigationButton navButton;
	private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

	@Override
	protected void startUp() throws Exception {
		log.info("Clip Recorder Plugin started!");

		if (!FFmpegDownloader.ensureFFmpegInstalled()){
			log.info("FFmpeg could not be installed");
		}

		// Initialize screenRecorder once during plugin start-up
		screenRecorder = new ScreenRecorder(config);
		log.info("screenRecorder initialized: " + screenRecorder);

		// Create a new sidebar panel
		panel = new ClipRecorderPanel(this);  // Pass this ClipRecorderPlugin instance

		// Create a navigation button
		BufferedImage icon = ImageUtil.loadImageResource(getClass(), "/com/notedsalmon/star.png");
		navButton = NavigationButton.builder()
				.tooltip("Clip Recorder")
				.icon(icon)
				.panel(panel)  // Use the panel here
				.build();

		clientToolbar.addNavigation(navButton);
	}

	@Override
	protected void shutDown() throws Exception {
		log.info("Clip Recorder Plugin stopped!");
		clientToolbar.removeNavigation(navButton);
	}

	public void testRecording() {
		log.info("Before testRecording call - screenRecorder: " + screenRecorder);
		executor.execute(() -> {
			try {
				if (screenRecorder == null) {
					log.error("Screen recorder is not initialized!");
					return;
				}

				log.info("Starting test recording...");
				screenRecorder.startRollingBuffer();
				Thread.sleep(20000);
				screenRecorder.stopRecording();
				log.info("Test recording completed.");
				String outputPath = config.storagePath() + File.separator + "test_clip_" + System.currentTimeMillis() + ".mp4";
				//screenRecorder.saveFinalClip(10,outputPath);
				log.info("Test recording saved at: " + outputPath);
				SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, "Test recording saved!"));
			} catch (IOException | InterruptedException ex) {
				log.error("Failed to test recording ", ex);
			}
		});
		log.info("After testRecording call - screenRecorder: " + screenRecorder);
	}

	@Provides
	ClipRecorderConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(ClipRecorderConfig.class);
	}
}
