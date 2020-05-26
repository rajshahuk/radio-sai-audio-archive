package com.twelvenines.radiosai;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class App {

    private static void extractDataItem(Element dataItem) {
        System.out.println("=== dataItem:" + dataItem);
    }

    public static void main(String[] args) throws Exception {
        Map formData = new HashMap<>();
        formData.put("from", "search");
        formData.put("page", "1");
        formData.put("description_s", "Bhajan Classroom");
        formData.put("filesperpage_s", "900");
        Document doc = Jsoup.connect("http://www.radiosai.org/program/SearchProgramme.php")
                .data(formData)
                .post();

        Elements newsHeadlines = doc.select("#sea > tbody > tr");
        for (Element next : newsHeadlines) {
            extractDataItem(next);
        }
    }
}
