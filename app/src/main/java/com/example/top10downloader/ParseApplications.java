package com.example.top10downloader;

import android.util.Log;

import androidx.annotation.LongDef;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

public class ParseApplications {
    private static final String TAG = "ParseApplications";
    private ArrayList<FeedEntry> applications;

    public ParseApplications() {
        this.applications = new ArrayList<>();
    }

    public ArrayList<FeedEntry> getApplications() {
        return applications;
    }

    public boolean parse(String xmlData) {
        boolean status = true;
        boolean inEntry = false;
        FeedEntry currentRecord = null;
        String textValue = "";


        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new StringReader(xmlData));
            int eventType = xmlPullParser.getEventType();

            while (eventType != xmlPullParser.END_DOCUMENT) {
                String tagName = xmlPullParser.getName();
                //<START_TAG> TEXT </END_TAG>
                //<name> Hello world </name>
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        Log.d(TAG, "parse: Starting tag for: " + tagName);
                        if ("entry".equalsIgnoreCase(tagName)) {
                            inEntry = true; //beginning of entry tag
                            currentRecord = new FeedEntry();
                        }
                        break;
                    case XmlPullParser.TEXT:
                        textValue = xmlPullParser.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        Log.d(TAG, "parse: ending tag for: " + tagName);

                        //only get these tag values if we are in an entry tag

                        if (inEntry) {
                            //ignoreCase (normalizes it) -> entry == ENTRY
                            if ("entry".equalsIgnoreCase(tagName)) {
                                inEntry = false; //end of entry tag
                                applications.add(currentRecord);
                            } else if ("name".equalsIgnoreCase(tagName)) {
                                currentRecord.setName(textValue);
                            } else if ("artist".equalsIgnoreCase(tagName)) {
                                currentRecord.setArtist(textValue);
                            } else if ("releaseDate".equalsIgnoreCase(tagName)) {
                                currentRecord.setReleaseData(textValue);
                            } else if ("summary".equalsIgnoreCase(tagName)) {
                                currentRecord.setSummary(textValue);
                            } else if ("image".equalsIgnoreCase(tagName)) {
                                currentRecord.setImageUrl(textValue);
                            }
                        }
                        break;
                    default:
                        break;

                }
                //get next parsing event
                eventType = xmlPullParser.next();
            }

            this.printApplications();

        } catch (Exception e) {
            status = false;
            e.printStackTrace();
        }
        return status;
    }


    public void printApplications() {
        for (FeedEntry app : applications
        ) {
            Log.d(TAG, "************************");
            Log.d(TAG, "printApplications: " + app.toString());
            Log.d(TAG, "************************");

        }
    }
}
