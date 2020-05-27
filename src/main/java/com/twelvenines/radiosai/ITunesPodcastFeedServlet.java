package com.twelvenines.radiosai;

import org.apache.commons.lang.StringEscapeUtils;

import javax.servlet.http.HttpServlet;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

@Path("itunes.xml")
public class ITunesPodcastFeedServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger(ITunesPodcastFeedServlet.class.getName());

    private static final SimpleDateFormat sdfForTitle = new SimpleDateFormat("E, dd MMM yyyy");
    private static final SimpleDateFormat sdfForPubDate = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");

    @GET
    public Response get() throws URISyntaxException {
        log.info("Sending redirect..");
        return Response.seeOther(new URI("https://h.12nines.com/radiosai/itunes.xml")).build();
    }

    private String getItemString(AudioItem audioItem) throws ParseException {
        Date d = audioItem.getDate();
        return "    <item>\n" +
                "      <title>" + StringEscapeUtils.escapeHtml(audioItem.getPodcastTitleForItunes()) + "</title>\n" +
                "      <link>" + audioItem.getUrl() + "</link>\n" +
                "      <description>" + StringEscapeUtils.escapeHtml(sdfForTitle.format(d)) + "</description>\n" +
                "      <enclosure url=\""+ audioItem.getUrl() + "\" type=\"audio/mpeg\" />\n" +
                "      <pubDate>" + sdfForPubDate.format(d) + "</pubDate>\n" +
                "      <guid>" +  audioItem.getUrl() + "</guid>\n" +
                "    </item>\n";
    }
}
