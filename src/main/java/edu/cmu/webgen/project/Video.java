package edu.cmu.webgen.project;

import java.io.File;
import java.time.LocalDateTime;

public class Video extends Media {
    private final long videoSize;

    public Video(File mediaPath, LocalDateTime created, LocalDateTime lastUpdate, long videoSize) {
        super(mediaPath, created, lastUpdate);
        this.videoSize = videoSize;
    }

    public long getVideoSize() {
        return this.videoSize;
    }
}
