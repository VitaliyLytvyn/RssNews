package com.skyver.rssnews.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.skyver.rssnews.retrofit.Article;

import java.util.List;

/**
 * Created by skyver on 8/19/17.
 */


@Dao
public interface RssDAO {
    @Query("SELECT * FROM Article")
    LiveData<List<Article>> getArticles();

    @Query("SELECT * FROM Article where link = :link")
    LiveData<Article> getArticleById(String link);



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertArticle(Article article);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long [] insertAllArticles(List<Article> articles);


    @Query("DELETE FROM Article")
    void deleteAllArticles();
}
