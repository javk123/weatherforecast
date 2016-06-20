package com.weather.user.weatherforecast;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by user on 2016/6/18.
 */
@SuppressWarnings("serial")
public class RssNews implements Serializable
{
    private String title = null;
    private String link = null;
    private String pubDate = null;
    private ArrayList<String> description = new ArrayList<String>();

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getTitle()
    {
        return title;
    }

    public void setLink(String link)
    {
        this.link = link;
    }

    public String getLink()
    {
        return link;
    }

    public void setPubDate(String pubDate)
    {
        this.pubDate = pubDate;
    }

    public String getPubDate()
    {
        return pubDate;
    }

    public void setdescription(String description)
    {
        this.description.add(description);
    }

    public ArrayList<String> getdescription()
    {
        return description;
    }

    @Override
    public String toString()
    {
        return "RssNews [title=" + title + ", link=" + link + ", pubDate=" + pubDate + "]";
    }
}
