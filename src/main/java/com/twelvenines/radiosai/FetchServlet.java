package com.twelvenines.radiosai;

import com.google.appengine.api.datastore.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// [START example]
@SuppressWarnings("serial")
@WebServlet(name = "fetchServlet", description = "This servlet gets things form the Radio Sai Website",
        value = "/fetch", loadOnStartup = 1)
public class FetchServlet extends HttpServlet {

    private static final String[] RADIO_SAI_URLS = {"http://media.radiosai.org/journals/Archives/live_audio_2018_archive.htm"};

    private static final Logger log = Logger.getLogger(FetchServlet.class.getName());

    public void init() throws ServletException {
        log.fine("In the init() method");
        try {
            AudioStore.getInstance();
        }
        catch(Exception e) {
            throw new ServletException(e);
        }

    }

    private void saveItemsToDataStore(List<AudioItem> audioItems) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        AudioStore audioStore = AudioStore.getInstance();
        int count = 0;
        for (AudioItem audioItem : audioItems) {
            audioStore.put(audioItem.getUrl(), audioItem);
            Key isPopulatedKey = KeyFactory.createKey(AudioItem.ENTITY_KIND_NAME, audioItem.getUrl());
            try {
                datastore.get(isPopulatedKey);
                log.fine("Item already exists not inserting: " + audioItem.getUrl());
            }
            catch(EntityNotFoundException nfex) {
                datastore.put(audioItem.toEntity());
                log.info("Inserting: " + audioItem.getUrl());
                count++;
            }
        }
        log.info("Number of Entities created: " + count);
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

    private AudioItem extractDataItem(Element dataItem) throws ParseException {

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
        List<AudioItem> listOfAudioItems = new ArrayList<>();
        for (String RADIO_SAI_URL : RADIO_SAI_URLS) {
            log.info("Connecting to: " + RADIO_SAI_URL);
            Document doc = Jsoup.connect(RADIO_SAI_URL).get();
            Elements newsHeadlines = doc.select("tr");
            for (Iterator<Element> iterator = newsHeadlines.iterator(); iterator.hasNext(); ) {
                Element next = iterator.next();
                AudioItem audioItem = null;
                try {
                    audioItem = extractDataItem(next);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(audioItem != null && audioItem.getUrl().contains("mp3")) {
                    listOfAudioItems.add(audioItem);
                }
            }
        }

        return listOfAudioItems;
    }

    public void main(String args[]) throws Exception {
        getRadioSaiAudioArchive();
    }
}

