package com.twelvenines.radiosai;

import com.google.appengine.api.datastore.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// [START example]
@SuppressWarnings("serial")
@WebServlet(name = "fetchServlet", description = "This servlet gets things form the Radio Sai Website",
        value = "/fetch")
public class FetchServlet extends HttpServlet {

    private static final String RADIO_SAI_URL = "http://media.radiosai.org/journals/Archives/live_audio_2017_archive.htm";

    private void saveItemsToDataStore(List<AudioItem> audioItems) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        int count = 0;
        for (AudioItem audioItem : audioItems) {

            Key isPopulatedKey = KeyFactory.createKey(AudioItem.ENTITY_KIND_NAME, audioItem.getId());
            try {
                datastore.get(isPopulatedKey);
                System.out.println("Item already exists not inserting: " + audioItem.getId());
            }
            catch(EntityNotFoundException nfex) {
                datastore.put(audioItem.toEntity());
                System.out.println("Inserting: " + audioItem.getId());
                count++;
            }
        }
        System.out.println("Number of Entities created: " + count);
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter out = resp.getWriter();
        List<AudioItem> listOfAudioItems =  getRadioSaiAudioArchive();
        out.println("Number of items returned: " + listOfAudioItems.size());
        out.println("==========================");
        for (AudioItem audioItem : listOfAudioItems) {
            out.println(audioItem);
        }
        saveItemsToDataStore(listOfAudioItems);
    }

    private String getId(Element dataItem) {
        try {
            return dataItem.children().first().text();
        } catch (NullPointerException npe) {
            return null;
        }

    }

    private String getDate(Element dataItem) {
        try {
            return dataItem.children().next().first().text();
        } catch (NullPointerException npe) {
            return null;
        }
    }

    private String getTitle(Element dataItem) {
        try {
            return dataItem.children().next().next().first().text();
        } catch (NullPointerException npe) {
            return null;
        }
    }

    private String getUrl(Element dataItem) {
        try {
            return dataItem.children().next().next().next().first().getElementsByTag("a").first().attr("href");
        } catch (NullPointerException npe) {
            return null;
        }
    }

    private AudioItem extractDataItem(Element dataItem) {

        String idAsString = getId(dataItem);
        try {
            int id = Integer.parseInt(idAsString);
            return new AudioItem(
                    id,
                    getDate(dataItem),
                    getTitle(dataItem),
                    getUrl(dataItem)
            );
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    private List<AudioItem> getRadioSaiAudioArchive() throws IOException {
        System.out.println("Connecting to: " + RADIO_SAI_URL);
        Document doc = Jsoup.connect(RADIO_SAI_URL).get();
        Elements newsHeadlines = doc.select("tr");
        List<AudioItem> listOfAudioItems = new ArrayList<>();
        for (Iterator<Element> iterator = newsHeadlines.iterator(); iterator.hasNext(); ) {
            Element next = iterator.next();
            AudioItem audioItem = extractDataItem(next);
            if(audioItem != null && audioItem.getUrl().contains("mp3")) {
                listOfAudioItems.add(audioItem);
            }
        }
        return listOfAudioItems;
    }

    public void main(String args[]) throws Exception {
        getRadioSaiAudioArchive();
    }
}
// [END example]
