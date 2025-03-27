package com.notedsalmon;

import lombok.extern.slf4j.Slf4j;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.zip.*;

@Slf4j
public class FFmpegDownloader {

    private static final String FFmpeg_URL = "https://www.gyan.dev/ffmpeg/builds/ffmpeg-release-full.7z"; // URL for the latest full FFmpeg release
    private static final String FFmpeg_DIR = System.getProperty("user.home") + File.separator + "ffmpeg" + File.separator + "ffmpeg-7.1.1-full_build";
    private static final String FFmpeg_BIN_PATH = FFmpeg_DIR + File.separator + "bin" + File.separator + "ffmpeg.exe";
    // Path to the FFmpeg executable

    // Checks if FFmpeg is available, and downloads if it's missing
    public static boolean ensureFFmpegInstalled() throws IOException {
        File ffmpegFile = new File(FFmpeg_BIN_PATH);

        if (ffmpegFile.exists()) {
            log.info("FFmpeg is already installed.");
            return true;
        }

        log.info("FFmpeg not found. Downloading...");

        // Download and extract FFmpeg
        downloadAndExtractFFmpeg();
        return true;
    }

    // Downloads and extracts FFmpeg into the specified directory
    private static void downloadAndExtractFFmpeg() throws IOException {
        // Create the FFmpeg directory if it doesn't exist
        Path ffmpegDirPath = Paths.get(FFmpeg_DIR);
        if (!Files.exists(ffmpegDirPath)) {
            Files.createDirectories(ffmpegDirPath);
        }

        URL url = new URL(FFmpeg_URL);
        String fileName = "ffmpeg-release-full.7z"; // File name for the download

        // Download FFmpeg archive
        try (InputStream in = url.openStream();
             FileOutputStream out = new FileOutputStream(fileName)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }

        // Extract the 7z file (requires 7z utility or you can use a Java library like Apache Commons Compress)
        try {
            extract7z(fileName, FFmpeg_DIR);
        } catch (IOException e) {
            log.error("Failed to extract FFmpeg from 7z archive.", e);
            throw e;
        }

        // Clean up the downloaded 7z file
        Files.delete(Paths.get(fileName));

        log.info("FFmpeg downloaded and extracted.");
    }

    // Method to extract .7z files (using 7z utility)
    private static void extract7z(String fileName, String outputDir) throws IOException {
        // Full path to 7z executable
        ProcessBuilder processBuilder = new ProcessBuilder("C:\\Program Files\\7-Zip\\7z.exe", "x", fileName, "-o" + outputDir);
        processBuilder.inheritIO();
        Process process = processBuilder.start();
        try {
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IOException("Error extracting 7z file: " + exitCode);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Extraction process interrupted", e);
        }
    }

}
