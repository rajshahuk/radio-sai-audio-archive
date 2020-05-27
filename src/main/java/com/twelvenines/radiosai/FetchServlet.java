package com.twelvenines.radiosai;

import io.muserver.MuRequest;
import io.muserver.MuResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class FetchServlet {

    private static final String[] RADIO_SAI_URLS = {"https://media.radiosai.org/journals/Archives/live_audio_2020_archive.htm"};

    private static final Logger log = LoggerFactory.getLogger(FetchServlet.class.getName());

    public void FetchServlet() {
        AudioStore.getInstance();
    }

    private void saveItemsToDataStore(List<AudioItem> audioItems) {
        AudioStore audioStore = AudioStore.getInstance();
        int count = 0;
        for (AudioItem audioItem : audioItems) {
            audioStore.put(audioItem.getUrl(), audioItem);
            count++;
        }
        log.info("Number of Entities created: " + count);
    }

    public void doGet(MuRequest request, MuResponse resp)  throws Exception {
        PrintWriter out = resp.writer();
        out.println("Fetching items from: " + RADIO_SAI_URLS);
        List<AudioItem> listOfAudioItems = getRadioSaiAudioArchive();
        out.println("Number of items returned: " + listOfAudioItems.size());
        out.println("==========================");
        for (AudioItem audioItem : listOfAudioItems) {
            out.println(audioItem.toString());
        }
        out.flush();
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
            assert idAsString != null;
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
            for (Element next : newsHeadlines) {
                AudioItem audioItem = null;
                try {
                    audioItem = extractDataItem(next);
                } catch (ParseException e) {
                    log.warn("Skipping item... {}", next);
                }
                if (audioItem != null && audioItem.getUrl().contains("mp3")) {
                    listOfAudioItems.add(audioItem);
                }
            }
        }
        return listOfAudioItems;
    }

    public void main() throws Exception {
        getRadioSaiAudioArchive();
    }
}

