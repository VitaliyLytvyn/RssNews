package com.skyver.rssnews.dagger;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.skyver.rssnews.database.RssDAO;
import com.skyver.rssnews.database.RssDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by skyver on 8/19/17.
 */

@Module
public class DbModule {

    @Singleton
    @Provides
    public RssDAO getRssDAO(RssDatabase rssDatabase){
        return rssDatabase.rssDao();
    }

    @Singleton
    @Provides
    public RssDatabase getRssDatabase(Application application){
        return Room.databaseBuilder(application.getApplicationContext(),
                RssDatabase.class, "rss.db")
                .build();
    }
}
