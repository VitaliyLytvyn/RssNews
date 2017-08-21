package com.skyver.rssnews.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.skyver.rssnews.retrofit.Article;

/**
 * Created by skyver on 8/19/17.
 */


@Database(entities = {Article.class}, version = 1)
public abstract class RssDatabase extends RoomDatabase {
    public abstract RssDAO rssDao();
}
