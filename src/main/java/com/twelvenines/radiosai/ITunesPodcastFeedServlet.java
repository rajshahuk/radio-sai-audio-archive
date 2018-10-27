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

@Path("itunes.xml")
public class ITunesPodcastFeedServlet extends HttpServlet {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
    private static final SimpleDateFormat sdfForTitle = new SimpleDateFormat("E, dd MMM yyyy");
    // Sun, 30 Sep 2018 00:00:00 GMT
    private static final SimpleDateFormat sdfForPubDate = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
    @GET
    @Produces("application/xml")
    public String get() throws FeedException, ParseException {

        StringBuffer sb = new StringBuffer();
        sb.append("<rss xmlns:itunes=\"http://www.itunes.com/dtds/podcast-1.0.dtd\" xmlns:atom=\"http://www.w3.org/2005/Atom\" xmlns:rawvoice=\"http://www.rawvoice.com/rawvoiceRssModule/\" version=\"2.0\">\n");
        sb.append("  <channel>\n");
        sb.append("    <title>Prashanti Nilayam Live Recordings</title>");
        sb.append("    <link>https://radio-sai-api.appspot.com</link>");
        sb.append("    <image>\n" +
                  "      <title>Sri Satya Sai Baba</title>\n" +
                  "      <url>https://radio-sai-api.appspot.com/images/podcast.jpg</url>\n" +
                  "      <width>1400</width>\n" +
                  "      <height>1400</height>\n" +
                  "      <description>Image of Satya Sai Baba</description>\n" +
                  "    </image>\n");
        sb.append("    <description>A feed of all live prayer sessions at Prashanti Nilayam</description>\n");
        sb.append("    <atom:link href=\"https://radio-sai-api.appspot.com/itunes.xml\" rel=\"self\" type=\"application/rss+xml\"/>\n");
        sb.append("    <itunes:author>Radio Sai</itunes:author>\n" +
                  "        <itunes:summary>\n" +
                  "                A feed of all live prayer sessions at Prashanti Nilayam\n" +
                  "        </itunes:summary>\n" +
                  "        <itunes:subtitle>Prashanti Nilayam Live Recordings</itunes:subtitle>\n" +
                  "        <itunes:owner>\n" +
                  "            <itunes:name>Radio Sai</itunes:name>\n" +
                  "            <itunes:email>raj@12nines.com</itunes:email>\n" +
                  "        </itunes:owner>\n" +
                  "        <itunes:explicit>No</itunes:explicit>\n" +
                  "        <itunes:keywords>sai,baba,satya,prashanti,nilyam,spiritual</itunes:keywords>\n" +
                  "        <itunes:image href=\"https://radio-sai-api.appspot.com/images/podcast.jpg\"/>\n");
        sb.append("    <itunes:category text=\"Religion &amp; Spirituality\">\n" +
                "            <itunes:category text=\"Spirituality\"/>\n" +
                "        </itunes:category>\n");
        List<AudioItem> list = AudioStore.getInstance().getLast100Items();
        list.sort(Collections.reverseOrder());
        for (AudioItem audioItem : list) {
            sb.append(getItemString(audioItem));
        }
        sb.append("  </channel>\n");
        sb.append("</rss>");

        return sb.toString();
    }

    private String getItemString(AudioItem audioItem) throws ParseException {
        Date d = sdf.parse(audioItem.getDateString());
        return "    <item>\n" +
                "      <title>" + audioItem.getTitle() + "</title>\n" +
                "      <link>" + audioItem.getUrl() + "</link>\n" +
                "      <description>" + sdfForTitle.format(d) + "</description>\n" +
                "      <enclosure url=\""+ audioItem.getUrl() + "\" type=\"audio/mpeg\" />\n" +
                "      <pubDate>" + sdfForPubDate.format(d) + "</pubDate>\n" +
                "      <guid>" +  audioItem.getUrl() + "</guid>\n" +
                "    </item>\n";
    }
}
