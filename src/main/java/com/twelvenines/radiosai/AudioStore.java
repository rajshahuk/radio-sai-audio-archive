package com.twelvenines.radiosai;

import com.google.appengine.api.datastore.*;

import java.util.*;
import java.util.logging.Logger;

/**
 * Created by raj on 30/07/2017.
 */
public class AudioStore {


    private static final Logger log = Logger.getLogger(AudioStore.class.getName());

    private static AudioStore onlyInstance = null;

    private static final Map<String, AudioItem> audioItemMap = new HashMap();

    private AudioStore() {
        populateAudioStore();
    }

    public static synchronized AudioStore getInstance() {
        if(onlyInstance == null) {
            onlyInstance = new AudioStore();
        }
        return onlyInstance;
    }

    public void put(String k, AudioItem v) {
        audioItemMap.put(k, v);
    }

    public Collection<AudioItem> getAudioItems() {
        List<AudioItem> list = new ArrayList((audioItemMap.values()));
        Collections.sort(list);
        return list;
    }

    public List<AudioItem> getLast100Items() {
        List<AudioItem> list = new ArrayList((audioItemMap.values()));
        Collections.sort(list);
        Collections.reverse(list);
        return list.subList(0, 100);
    }

    public void populateAudioStore() {
        log.info("Populating Audio Store");
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        List<Entity> results = datastore.prepare(new Query(AudioItem.ENTITY_KIND_NAME).addSort(AudioItem.ENTITY_DATE, Query.SortDirection.DESCENDING)).asList(FetchOptions.Builder.withDefaults());
        for (Entity entity : results) {
            AudioItem a = AudioItem.fromEntity(entity);
            audioItemMap.put(a.getUrl(), a);
        }
    }

}
