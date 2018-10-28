package com.twelvenines.radiosai;

import com.google.appengine.api.datastore.*;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by raj on 20/07/2017.
 */
public class AudioItem implements Serializable, Comparable<AudioItem> {

    public static final String ENTITY_KIND_NAME = "AudioItem";
    public static final String ENTITY_IDENTIFIER = "identifier";
    public static final String ENTITY_DATE_STRING = "dateString";
    public static final String ENTITY_DATE = "date";
    public static final String ENTITY_TITLE = "title";
    public static final String ENTITY_URL = "url";

    private int id;
    private String dateString;
    private Date date;
    private String title;
    private String url;

    public static final SimpleDateFormat RADIO_SAI_DATE_FORMAT = new SimpleDateFormat("MMM dd, yyyy");

    public AudioItem(int id, String dateString, String title, String url) throws ParseException {
        this(id, dateString, RADIO_SAI_DATE_FORMAT.parse(dateString), title, url);
    }

    public AudioItem(int id, String dateString, Date date, String title, String url) {
        this.id = id;
        this.dateString = dateString;
        this.date = date;
        this.title = title;
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public String getDateString() {
        return dateString;
    }

    public Date getDate() {
        return date;
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

    public Entity toEntity() {
        Entity audioItemEntity = new Entity(ENTITY_KIND_NAME, getId());
        audioItemEntity.setProperty(ENTITY_IDENTIFIER, getId());
        audioItemEntity.setProperty(ENTITY_DATE_STRING, getDateString());
        audioItemEntity.setProperty(ENTITY_DATE, getDate());
        audioItemEntity.setProperty(ENTITY_TITLE, getTitle());
        audioItemEntity.setProperty(ENTITY_URL, getUrl());
        return audioItemEntity;
    }

    public static AudioItem fromEntity(Entity e) {
        if (e.getKind().equals(ENTITY_KIND_NAME)) {
            int id = ((Long) e.getProperty(ENTITY_IDENTIFIER)).intValue();
            String dateString = (String)  e.getProperty(ENTITY_DATE_STRING);
            Date date = (Date) e.getProperty(ENTITY_DATE);
            String title = (String)  e.getProperty(ENTITY_TITLE);
            String url = (String)  e.getProperty(ENTITY_URL);
            return new AudioItem(
                    id,
                    dateString,
                    date,
                    title,
                    url
            );
        }
        else {
            throw new IllegalArgumentException("Wrong type of entity passed, expecting " + ENTITY_KIND_NAME + " but received " + e.getKind());
        }
    }

    @Override
    public int compareTo(AudioItem audioItem) {
        return this.id = audioItem.id;
    }
}
