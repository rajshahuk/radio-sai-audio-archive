package com.twelvenines.radiosai;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by raj on 20/07/2017.
 */
public class AudioItem implements Serializable, Comparable<AudioItem> {

    public static final String ENTITY_KIND_NAME = "AudioItem";
    public static final String ENTITY_IDENTIFIER = "identifier";
    public static final String ENTITY_DATE_STRING = "dateString";
    public static final String ENTITY_TITLE = "title";
    public static final String ENTITY_URL = "url";

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

    public Entity toEntity() {
        Entity audioItemEntity = new Entity(ENTITY_KIND_NAME, getId());
        audioItemEntity.setProperty(ENTITY_IDENTIFIER, getId());
        audioItemEntity.setProperty(ENTITY_DATE_STRING, getDateString());
        audioItemEntity.setProperty(ENTITY_TITLE, getTitle());
        audioItemEntity.setProperty(ENTITY_URL, getUrl());
        return audioItemEntity;
    }

    public static AudioItem fromEntity(Entity e) {
        if (e.getKind().equals(ENTITY_KIND_NAME)) {
            int id = ((Long) e.getProperty(ENTITY_IDENTIFIER)).intValue();
            String dateString = (String)  e.getProperty(ENTITY_DATE_STRING);
            String title = (String)  e.getProperty(ENTITY_TITLE);
            String url = (String)  e.getProperty(ENTITY_URL);
            return new AudioItem(
                    id,
                    dateString,
                    title,
                    url
            );
        }
        else {
            throw new IllegalArgumentException("Wrong type of entity passed, expecting " + ENTITY_KIND_NAME + " but received " + e.getKind());
        }
    }

    public static List<AudioItem> getAllFromDataStore() {
        List<AudioItem> audioItems = new ArrayList<AudioItem>();
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        List<Entity> results = datastore.prepare(new Query(AudioItem.ENTITY_KIND_NAME).addSort(AudioItem.ENTITY_IDENTIFIER, Query.SortDirection.DESCENDING)).asList(FetchOptions.Builder.withDefaults());
        for (int i = 0; i < results.size(); i++) {
            Entity entity =  results.get(i);
            audioItems.add(AudioItem.fromEntity(entity));
        }
        return audioItems;
    }

    public static List getListOfKeysForMemcache() {
        List<Integer> l = new ArrayList<>();
        for(int i=6000; i>5000; i--) {
            l.add(new Integer(i));
        }
        return l;
    }

    public static Collection getAllFromMemcache() {
        MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();
        Map m = memcacheService.getAll(getListOfKeysForMemcache());
        return m.values();
    }

    @Override
    public int compareTo(AudioItem audioItem) {
        return this.id = audioItem.id;
    }
}
