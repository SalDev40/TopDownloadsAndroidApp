package com.example.top10downloader;

public class FeedEntry {
    private String name;
    private String artist;
    private String releaseDate;
    private String summary;
    private String imageUrl;

    public FeedEntry() {

    }

    public FeedEntry(String name,
                     String artist,
                     String releaseDate,
                     String summary,
                     String imageUrl) {
        this.name = name;
        this.artist = artist;
        this.releaseDate = releaseDate;
        this.summary = summary;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getReleaseData() {
        return releaseDate;
    }

    public void setReleaseData(String releaseData) {
        this.releaseDate = releaseData;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "\n FeedEntry{" +
                "name='" + name + '\n' +
                ", artist='" + artist + '\n' +
                ", releaseDate='" + releaseDate + '\n' +
                ", imageUrl='" + imageUrl + '\n' +
                '}';
    }
}
