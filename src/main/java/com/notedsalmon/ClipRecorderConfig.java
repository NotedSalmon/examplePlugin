package com.notedsalmon;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.io.File;
import java.nio.file.Paths;

@ConfigGroup("cliprecorder")
public interface ClipRecorderConfig extends Config {
	public String userHome = System.getProperty("user.home");

	@ConfigItem(
			keyName = "videoQuality",
			name = "Video Quality",
			description = "Set video quality (Low, Medium, High)",
			position = 1
	)
	default VideoQuality videoQuality() {
		return VideoQuality.HIGH_1080;
	}

	@ConfigItem(
			keyName = "videoFPS",
			name = "Video FPS",
			description = "Set video FPS (30, 60)",
			position = 2
	)
	default VideoFPS videoFPS() {
		return VideoFPS.FPS_60;
	}

	@ConfigItem(
			keyName = "videoLength",
			name = "Video Length (seconds)",
			description = "Set the duration of saved clips"
	)
	default int videoLength() {
		return 20;
	}

	@ConfigItem(
			keyName = "storagePath",
			name = "Storage Path",
			description = "Set where to save clips"
	)
	default String storagePath() {
		return Paths.get(System.getProperty("user.home"), "runelite-clips").toString();
	}

	@ConfigItem(
			keyName = "captureMic",
			name = "Capture Microphone",
			description = "Include microphone audio in clips"
	)
	default boolean captureMic() {
		return false;
	}

	@ConfigItem(
			keyName = "captureGameAudio",
			name = "Capture Game Audio",
			description = "Include in-game sounds in clips"
	)
	default boolean captureGameAudio() {
		return true;
	}
}
