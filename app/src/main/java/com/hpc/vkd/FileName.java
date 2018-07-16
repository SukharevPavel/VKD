package com.hpc.vkd;

/**
 * Class describing name of song
 */

public class FileName {

    private String artist;
    private String title;

    public FileName(String artist, String title) {
        this.artist = artist;
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }

    public boolean isCompleted(){
        return artist != null && title != null;
    }

    public String generateFileName(){
        return (artist != null? artist : Constants.EMPTY_STRING) +
                Constants.SPACE +
                Constants.DASH +
                Constants.SPACE +
                (title != null? title : Constants.EMPTY_STRING)
                + Constants.AUDIO_EXT;
    }
}
