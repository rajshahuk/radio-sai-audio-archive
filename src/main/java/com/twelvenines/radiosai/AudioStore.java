package com.twelvenines.radiosai;

import org.json.JSONArray;

import java.util.*;
import java.util.logging.Logger;

/**
 * Created by raj on 30/07/2017.
 */
public class AudioStore {

    private static final Logger log = Logger.getLogger(AudioStore.class.getName());

    private static AudioStore onlyInstance = null;

    private static final Map<String, AudioItem> audioItemMap = new HashMap();

    private AudioStore() { }

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
        Collections.sort(list, Collections.reverseOrder());
        return list;
    }

    public List<AudioItem> getLast100Items() {
        List<AudioItem> list = new ArrayList((audioItemMap.values()));
        Collections.sort(list);
        Collections.reverse(list);
        return list.subList(0, 100);
    }

    public JSONArray asJsonArray() {
        JSONArray a = new JSONArray();
        getAudioItems().stream().forEach(audioItem -> a.put(audioItem.toJson()));
        return a;
    }

}
