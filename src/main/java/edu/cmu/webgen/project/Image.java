package edu.cmu.webgen.project;

import java.io.File;
import java.time.LocalDateTime;

public class Image extends Media {

    final long imageSize;

    public Image(File mediaPath, LocalDateTime created, LocalDateTime lastUpdate, long imageSize) {
        super(mediaPath, created, lastUpdate);
        this.imageSize = imageSize;
    }

    public long getImageSize() {
        return this.imageSize;
    }
}
