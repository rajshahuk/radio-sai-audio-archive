package com.twelvenines.radiosai;

import com.google.appengine.api.datastore.*;

import java.util.*;

/**
 * Created by raj on 30/07/2017.
 */
public class AudioStore {

    private static AudioStore onlyInstance = null;

    private Map<Integer, AudioItem> audioItemMap = new HashMap();

    private AudioStore() {
        populateAudioStore();
    }

    public static synchronized AudioStore getInstance() {
        if(onlyInstance == null) {
            onlyInstance = new AudioStore();
        }
        return onlyInstance;
    }

    public void put(Integer k, AudioItem v) {
        this.audioItemMap.put(k, v);
    }

    public Collection<AudioItem> getAudioItems() {
        return audioItemMap.values();
    }

    public void populateAudioStore() {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        List<Entity> results = datastore.prepare(new Query(AudioItem.ENTITY_KIND_NAME).addSort(AudioItem.ENTITY_IDENTIFIER, Query.SortDirection.DESCENDING)).asList(FetchOptions.Builder.withDefaults());
        for (int i = 0; i < results.size(); i++) {
            Entity entity =  results.get(i);
            AudioItem a = AudioItem.fromEntity(entity);
            audioItemMap.put(a.getId(), a);
        }
    }

}
