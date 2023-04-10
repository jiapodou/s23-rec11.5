package edu.cmu.webgen.project;

import java.time.LocalDateTime;

public class YoutubeVideo extends AbstractContent {
    private final String youtubeId;

    public YoutubeVideo(String youtubeId, Metadata metadata, LocalDateTime created, LocalDateTime lastUpdate) {
        super(created, lastUpdate);
        this.youtubeId = youtubeId;
    }

    public String getYoutubeId() {
        return this.youtubeId;
    }
}
