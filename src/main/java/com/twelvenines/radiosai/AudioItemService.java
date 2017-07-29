package com.twelvenines.radiosai;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by raj on 21/07/2017.
 */
@Path("audioItems")
public class AudioItemService {

    @GET
    @Produces("application/json")
    public List<Object> get() {
//        List audioItems = AudioItem.getAllFromDataStore();
//        System.out.println("Number of items loaded from DS: " + audioItems.size());
//        return audioItems;
        List audioItems = new ArrayList();
        Collection c = AudioItem.getAllFromMemcache();
        for (Iterator i = c.iterator(); i.hasNext(); ) {
            AudioItem next = (AudioItem) i.next();
            audioItems.add(next);
        }
        return audioItems;
    }

}
