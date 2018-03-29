package com.vastcast.vastcast;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/*
Test Feeds
Revisionist History - "http://feeds.feedburner.com/RevisionistHistory"
Common Sense - "http://feeds.feedburner.com/dancarlin/commonsense"
Congratulations with Chris D'elia - "http://congratulations.libsyn.com/rss"
Freakonomics - "http://feeds.feedburner.com/freakonomicsradio"
My Brother My Brother and Me - "http://feeds.feedburner.com/mbmbam"
Why Oh Why - "https://feeds.megaphone.fm/whyohwhy"
Planet Money - "https://www.npr.org/rss/podcast.php?id=510289"
Top Scallops - "https://rss.simplecast.com/podcasts/1497/rss"
 */

public class RSSFetcher {
    public static Collection fetch(URL source) throws IOException, XmlPullParserException {
        //Log.d("RSS", "Began Fetching");
        InputStream in = source.openStream();
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(in, null);
        parser.nextTag();
        Collection c = readRSS(parser, source);
        in.close();
        return c;
    }

    private static Collection readRSS(XmlPullParser parser, URL source) throws XmlPullParserException, IOException {
        //Log.d("RSS", "Began Reading RSS");
        parser.require(XmlPullParser.START_TAG, null, "rss");
        while(parser.next() != XmlPullParser.END_TAG) {
            if(parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if(name.equals("channel")) {
                return readChannel(parser, source);
            }
            else {
                skip(parser);
            }
        }
        Log.d("RSS", "Invalid Feed: Missing RSS Tag");
        return null;
    }

    private static Collection readChannel(XmlPullParser parser, URL source) throws XmlPullParserException, IOException {
        //Log.d("RSS", "Began Reading Channel");
        parser.require(XmlPullParser.START_TAG, null, "channel");
        String title = null;
        String description = null;
        URL image = null;
        String author = null;
        ArrayList<Episode> episodes = new ArrayList<>();
        while(parser.next() != XmlPullParser.END_TAG) {
            if(parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch(name) {
                case "title": {
                    title = readTitle(parser);
                    break;
                }
                case "description": {
                    description = readDescription(parser);
                    break;
                }
                case "image": {
                    image = readImage(parser);
                    break;
                }
                case "itunes:author": {
                    author = readAuthor(parser);
                    break;
                }
                case "item": {
                    Episode e = readEpisode(parser);
                    if(e != null) episodes.add(e);
                    break;
                }
                default: {
                    skip(parser);
                }
            }
        }
        if(title == null || description == null || image == null || author == null || episodes.size() == 0 || source == null) {
            Log.d("RSS", "Invalid Feed: Missing Element Required for Podcast");
            Log.d("RSS", title + "\n" + description);
            if(image != null) Log.d("RSS", image.toString());
            Log.d("RSS", author + "\n" + episodes.size());
            if(source != null) Log.d("RSS", source.toString());
            return null;
        }
        else {
            return new Collection(title, description, image, author, episodes, source, true);
        }
    }

    private static String readTitle(XmlPullParser parser) throws XmlPullParserException, IOException {
        //Log.d("RSS", "Began Reading Title");
        parser.require(XmlPullParser.START_TAG, null, "title");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, "title");
        return title;
    }

    private static String readDescription(XmlPullParser parser) throws XmlPullParserException, IOException {
        //Log.d("RSS", "Began Reading Description");
        parser.require(XmlPullParser.START_TAG, null, "description");
        String description = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, "description");
        return description;
    }

    private static URL readImage(XmlPullParser parser) throws XmlPullParserException, IOException {
        //Log.d("RSS", "Began Reading Image");
        parser.require(XmlPullParser.START_TAG, null, "image");
        String image = null;
        while(parser.next() != XmlPullParser.END_TAG) {
            if(parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if(name.equals("url")) {
                parser.require(XmlPullParser.START_TAG, null, "url");
                image = readText(parser);
                parser.require(XmlPullParser.END_TAG, null, "url");
            }
            else {
                skip(parser);
            }
        }
        if(image == null) {
            Log.d("RSS", "No Image Available");
            return null;
        }
        else {
            return new URL(image);
        }
    }

    private static String readAuthor(XmlPullParser parser) throws XmlPullParserException, IOException {
        //Log.d("RSS", "Began Reading Author");
        parser.require(XmlPullParser.START_TAG, null, "itunes:author");
        String author = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, "itunes:author");
        return author;
    }

    private static Episode readEpisode(XmlPullParser parser) throws XmlPullParserException, IOException {
        //Log.d("RSS", "Began Reading Episode");
        parser.require(XmlPullParser.START_TAG, null, "item");
        String title = null;
        String description = null;
        boolean isTrailer = false;
        int season = -1;
        int episode = -1;
        int duration = -1;
        URL link = null;
        while(parser.next() != XmlPullParser.END_TAG) {
            if(parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch(name) {
                case "title": {
                    title = readTitle(parser);
                    break;
                }
                case "description": {
                    description = readDescription(parser);
                    break;
                }
                case "itunes:episodeType": {
                    isTrailer = readEpisodeType(parser).equals("trailer");
                    break;
                }
                case "itunes:season": {
                    season = readSeasonNumber(parser);
                    break;
                }
                case "itunes:episode": {
                    episode = readEpisodeNumber(parser);
                    break;
                }
                case "itunes:duration": {
                    duration = readDuration(parser);
                    break;
                }
                case "enclosure": {
                    link = readLink(parser);
                    break;
                }
                default: {
                    skip(parser);
                }
            }
        }
        /*Todo: adjust so that episodes don't require numbers and they are generated when missing*/
        if(title == null || description == null /*|| (episode == -1 && !isTrailer)*/ || duration == -1 || link == null) {
            Log.d("RSS", "Invalid Feed: Missing Element Required for Episode");
            Log.d("RSS", title + "\n" + description + "\n" + isTrailer + "\n" + season + "\n" + episode + "\n" + duration);
            if(link != null) Log.d("RSS", link.toString());
            return null;
        }
        else {
            return new Episode(title, description, season, episode, duration, link);
        }
    }

    private static String readEpisodeType(XmlPullParser parser) throws XmlPullParserException, IOException {
        //Log.d("RSS", "Began Reading Episode Type");
        parser.require(XmlPullParser.START_TAG, null, "itunes:episodeType");
        String type = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, "itunes:episodeType");
        return type;
    }

    private static int readSeasonNumber(XmlPullParser parser) throws XmlPullParserException, IOException {
        //Log.d("RSS", "Began Reading Season Number");
        parser.require(XmlPullParser.START_TAG, null, "itunes:season");
        int season = readNumber(parser);
        parser.require(XmlPullParser.END_TAG, null, "itunes:season");
        return season;
    }

    private static int readEpisodeNumber(XmlPullParser parser) throws XmlPullParserException, IOException {
        //Log.d("RSS", "Began Reading Episode Number");
        parser.require(XmlPullParser.START_TAG, null, "itunes:episode");
        int episode = readNumber(parser);
        parser.require(XmlPullParser.END_TAG, null, "itunes:episode");
        return episode;
    }

    private static int readDuration(XmlPullParser parser) throws XmlPullParserException, IOException {
        //Log.d("RSS", "Began Reading Duration");
        parser.require(XmlPullParser.START_TAG, null, "itunes:duration");
        String time = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, "itunes:duration");
        if(time != null) {
            String times[] = time.split(":");
            int duration = 0;
            for(String number: times) {
                if(duration > 0) {
                    duration *= 60;
                }
                duration += Integer.parseInt(number);
            }
            return duration;
        }
        else {
            return -1;
        }
    }

    private static URL readLink(XmlPullParser parser) throws XmlPullParserException, IOException {
        //Log.d("RSS", "Began Reading Episode Link");
        if(parser.getEventType() == XmlPullParser.START_TAG) {
            String url = parser.getAttributeValue(null, "url");
            skip(parser);
            return new URL(url);
        }
        else {
            return null;
        }
    }

    private static String readText(XmlPullParser parser) throws XmlPullParserException, IOException {
        String text = null;
        if(parser.next() == XmlPullParser.TEXT) {
            text = parser.getText();
            parser.nextTag();
        }
        return text;
    }

    private static int readNumber(XmlPullParser parser) throws XmlPullParserException, IOException {
        String number = null;
        if(parser.next() == XmlPullParser.TEXT) {
            number = parser.getText();
            parser.nextTag();
        }
        if(number != null) {
            return Integer.parseInt(number);
        }
        else return -1;
    }

    private static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        //Log.d("RSS", "Began Skipping");
        if(parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while(depth != 0) {
            switch(parser.next()) {
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
            }
        }
    }
}