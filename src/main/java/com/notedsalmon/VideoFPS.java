package com.notedsalmon;

public enum VideoFPS {
    FPS_30(30),
    FPS_60(60);

    private final int fps;

    VideoFPS(int fps) {
        this.fps = fps;
    }

    @Override
    public String toString() {
        return String.valueOf(fps);
    }
}
