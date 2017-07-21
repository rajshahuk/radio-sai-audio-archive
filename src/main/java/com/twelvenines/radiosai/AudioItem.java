package com.twelvenines.radiosai;

/**
 * Created by raj on 20/07/2017.
 */
public class AudioItem {

    private int id;
    private String dateString;
    private String title;
    private String url;

    public AudioItem(int id, String dateString, String title, String url) {
        this.id = id;
        this.dateString = dateString;
        this.title = title;
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public String getDateString() {
        return dateString;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return "AudioItem{" +
                "id=" + id +
                ", dateString='" + dateString + '\'' +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
