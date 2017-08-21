package com.skyver.rssnews.retrofit;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;


/**
 * Created by skyver on 8/19/17.
 */

@Entity
@Root(name = "item", strict = false)
public class Article {

    @PrimaryKey()
    @Element(name = "guid")
    private String guid;

    @Element(name = "title")
    private String title;

    @Element(name = "link")
    private String link;

    @Element(name = "fulltext")
    private String fulltext;

    @Element(name = "image")
    private String image;

    @Element(name = "pubDate")
    private String pubDate;

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getFulltext() {
        return fulltext;
    }

    public void setFulltext(String fulltext) {
        this.fulltext = fulltext;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

}