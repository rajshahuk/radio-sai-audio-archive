package com.twelvenines.radiosai;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by raj on 30/07/2017.
 */
public class AudioStore {

    private static AudioStore onlyInstance = null;

    private List<AudioItem> audioItems = new ArrayList<>();

    private AudioStore() {

    }

    public static synchronized AudioStore getInstance() {
        if(onlyInstance == null) {
            onlyInstance = new AudioStore();
        }
        return onlyInstance;
    }

    public List<AudioItem> getAudioItems() {
        return audioItems;
    }

}
