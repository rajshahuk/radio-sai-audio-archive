package com.twelvenines.radiosai;

import com.rometools.rome.feed.module.Module;
import com.rometools.rome.feed.synd.*;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedOutput;

import javax.servlet.http.HttpServlet;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Path("podcast.xml")
public class PodcastFeedServlet extends HttpServlet {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
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
        syndImage.setUrl("https://1.bp.blogspot.com/-O2yG7d3oj0o/UK1HWyfSv8I/AAAAAAAAAhQ/oq6ifvdhDOY/s640/this+day+that+age.jpg");
        syndImage.setHeight(469);
        syndImage.setWidth(497);
        feed.setImage(syndImage);
        List<SyndEntry> feedItems = new ArrayList<SyndEntry>();
        Collection<AudioItem> audioItems = AudioStore.getInstance().getAudioItems();

        for (AudioItem audioItem : audioItems) {
            SyndEntry syndEntry = new SyndEntryImpl();
            syndEntry.setTitle(audioItem.getTitle());
            syndEntry.setLink(audioItem.getUrl());
            List<SyndEnclosure> syndEnl = new ArrayList<SyndEnclosure>();
            SyndEnclosure syndEnclosure = new SyndEnclosureImpl();
            syndEnclosure.setUrl(audioItem.getUrl());
            syndEnclosure.setType("audio/mpeg");

            syndEnl.add(syndEnclosure);
            syndEntry.setEnclosures(syndEnl);
            syndEntry.setPublishedDate(sdf.parse(audioItem.getDateString()));
            feedItems.add(syndEntry);
        }
        feed.setEntries(feedItems);
        SyndFeedOutput output = new SyndFeedOutput();
        return output.outputString(feed);
    }
}
