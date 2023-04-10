package edu.cmu.webgen.project;

import java.io.File;
import java.time.LocalDateTime;

public abstract class Media extends AbstractContent {
    private final File mediaPath;

    Media(File mediaPath, LocalDateTime created, LocalDateTime lastUpdate) {
        super(created, lastUpdate);
        this.mediaPath = mediaPath;
    }

    public File getMediaPath() {
        return this.mediaPath;
    }

}
