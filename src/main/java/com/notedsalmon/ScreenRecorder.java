package com.notedsalmon;

import lombok.extern.slf4j.Slf4j;
import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ScreenRecorder extends ClipRecorderPlugin {
    private Process recordingProcess;
    private final ClipRecorderConfig config;

    // Constructor
    public ScreenRecorder(ClipRecorderConfig config) {
        this.config = config;
    }

    public void startRollingBuffer() throws IOException {
        stopRecording(); // Ensure no old process is running

        // Set video resolution, bitrate, and frame rate based on config
        int width, height, fps = 60;

        String outputPath = Paths.get(config.storagePath(), "test_clip_" + System.currentTimeMillis() + ".mov").toString();
        File directory = new File(config.storagePath());

// Check if the directory exists, and create it if not
        if (!directory.exists()) {
            boolean created = directory.mkdirs(); // Creates the directory and any necessary parent directories
            if (created) {
                log.info("Directory created at: " + directory.getAbsolutePath());
            } else {
                log.error("Failed to create directory at: " + directory.getAbsolutePath());
            }
        }

// Now you can safely use the outputPath
        log.info("Recording will be saved at: " + outputPath);

        VideoQuality videoQuality = config.videoQuality();

        switch (videoQuality) {
            case HIGH_1080:
                width = 1920;
                height = 1080;
                break;
            case MEDIUM_720:
                width = 1280;
                height = 720;
                break;
            case LOW_480:
            default:
                width = 854;
                height = 480;
                break;
        }

        // Set FPS
        switch (config.videoFPS()){
            case FPS_60:
                fps = 60;
                break;
            case FPS_30:
                fps = 30;
                break;
        }

        // Audio input configuration
        String micInput = config.captureMic() ? "-f dshow -i audio=\"Headset Microphone (3- Corsair VOID Wireless Gaming Dongle)\" " : "";
        //String gameAudio = config.captureGameAudio() ? "-f dshow -i audio=\"Stereo Mix (Realtek Audio)\" " : "";

        // Construct FFmpeg command to record only RuneLite window and save to MOV format
        String command = String.format(
                "\"%s\" -y -f gdigrab -probesize 10M -analyzeduration 10M -framerate %d -i title=\"RuneLite - Noted Salmon\" " +
                        "%s -c:v libx264 -preset fast -b:v 4000k -pix_fmt yuv420p -t 10 %s",
                getFFmpegPath(), fps, micInput, outputPath
        );


        //& "C:\Users\these\ffmpeg\ffmpeg-7.1.1-full_build\bin\ffmpeg.exe" -y -f gdigrab -framerate 60 -i title="RuneLite" -f dshow -i audio="Headset Microphone (3- Corsair VOID Wireless Gaming Dongle)" -c:v libx264 -preset fast -b:v 4000k -t 10 clips\output.mov

        log.info("Starting rolling buffer recording at " + width + "x" + height + " and fps " + fps);
        recordingProcess = new ProcessBuilder("cmd", "/c", command)
                .redirectErrorStream(true)  // Merges standard and error output
                .redirectOutput(new File("ffmpeg_log.txt"))  // Saves log
                .start();

        new Thread(() -> {
            try {
                int exitCode = recordingProcess.waitFor();  // Wait for FFmpeg to exit
                log.info("FFmpeg finished with exit code: " + exitCode);
            } catch (InterruptedException e) {
                log.error("Recording process interrupted", e);
            }
        }).start();



    }

    public void stopRecording() throws IOException {
        log.info("Stopping recording...");

        // Kill the ffmpeg process
        if (recordingProcess != null) {
            log.info("Recording Info{}", recordingProcess.info().toString());
            log.info("Is it alive? {}", String.valueOf(recordingProcess.isAlive()));
            log.info("Full log{}", recordingProcess.getOutputStream().toString());
            recordingProcess.destroy();
            try {
                if (!recordingProcess.waitFor(10, TimeUnit.SECONDS)) { // Wait for process to terminate
                    recordingProcess.destroyForcibly(); // Force kill if not stopping
                }
            } catch (InterruptedException e) {
                log.error("Failed to stop recording process", e);
                Thread.currentThread().interrupt();
            }
            log.info("Stopped rolling buffer.");
        }
    }

    public void saveFinalClip(int duration, String outputPath) throws IOException {
        File dir = new File("clips");
        File[] files = dir.listFiles((d, name) -> name.matches("output\\d+\\.mp4"));

        if (files == null || files.length == 0) return;

        // Sort files by latest timestamp
        Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());

        // Select only the needed files
        List<String> fileNames = new ArrayList<>();
        int totalDuration = 0;
        for (File file : files) {
            fileNames.add("file '" + file.getAbsolutePath() + "'");
            totalDuration += 10;
            if (totalDuration >= duration) break;
        }

        // Write file list for FFmpeg concatenation
        File concatList = new File("clips/concat.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(concatList))) {
            for (String fileName : fileNames) writer.write(fileName + "\n");
        }

        // Run FFmpeg to merge clips
        String command = String.format("\"%s\" -f concat -safe 0 -i clips/concat.txt -c copy %s", getFFmpegPath(), outputPath);
        new ProcessBuilder("cmd", "/c", command).start();

        log.info("Saved clip: " + outputPath);
    }

    private String getFFmpegPath() {
        return "C:\\Users\\these\\ffmpeg\\ffmpeg-7.1.1-full_build\\bin\\ffmpeg.exe";  // Adjust path as needed
    }
}
