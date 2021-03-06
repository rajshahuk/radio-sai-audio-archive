package com.twelvenines.radiosai;

import com.rometools.rome.feed.synd.*;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedOutput;

import javax.servlet.http.HttpServlet;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Path("podcast.xml")
public class PodcastFeedServlet extends HttpServlet {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
    private static final SimpleDateFormat sdfForTitle = new SimpleDateFormat("E, dd MMM yyyy");
    @GET
    @Produces("application/xml")
    public String get() throws FeedException, ParseException {
        SyndFeed feed = new SyndFeedImpl();
        feed.setFeedType("rss_2.0");
        feed.setTitle("Prashanti Nilayam Live Recordings");
        feed.setLink("https://radio-sai-api.appspot.com");
        feed.setDescription("A feed of all live prayer sessions at Prashanti Nilayam");
        feed.setAuthor("Radio Sai");
        SyndImage syndImage = new SyndImageImpl();
        syndImage.setTitle("Sri Satya Sai Baba");
        syndImage.setDescription("Image of Satya Sai Baba");
        syndImage.setUrl("https://radio-sai-api.appspot.com/images/podcast.jpg");
        syndImage.setHeight(1400);
        syndImage.setWidth(1400);
        feed.setImage(syndImage);
        List<SyndEntry> feedItems = new ArrayList<SyndEntry>();
        List<AudioItem> list = AudioStore.getInstance().getLast100Items();
        list.sort(Collections.reverseOrder());
        for (AudioItem audioItem : list) {
            Date d = sdf.parse(audioItem.getDateString());
            SyndEntry syndEntry = new SyndEntryImpl();
            syndEntry.setTitle(audioItem.getTitle());
            syndEntry.setLink(audioItem.getUrl());
            SyndContent syndContent = new SyndContentImpl();
            syndContent.setValue(sdfForTitle.format(d));
            syndEntry.setDescription(syndContent);
            List<SyndEnclosure> syndEnl = new ArrayList<SyndEnclosure>();
            SyndEnclosure syndEnclosure = new SyndEnclosureImpl();
            syndEnclosure.setUrl(audioItem.getUrl());
            syndEnclosure.setType("audio/mpeg");

            syndEnl.add(syndEnclosure);
            syndEntry.setEnclosures(syndEnl);
            syndEntry.setPublishedDate(d);
            feedItems.add(syndEntry);
        }
        feed.setEntries(feedItems);
        SyndFeedOutput output = new SyndFeedOutput();
        return output.outputString(feed);
    }
}
