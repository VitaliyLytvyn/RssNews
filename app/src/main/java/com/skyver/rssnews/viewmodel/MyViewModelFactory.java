package com.skyver.rssnews.viewmodel;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.skyver.rssnews.database.RssDAO;

import java.util.concurrent.Executor;

import javax.inject.Inject;

import retrofit2.Retrofit;

/**
 * Created by skyver on 8/19/17.
 */

public class MyViewModelFactory implements ViewModelProvider.Factory {

    @Inject
    Application application;

    @Inject
    Retrofit retrofit;

    @Inject
    RssDAO rssDAO;

    @Inject
    Executor executor;


    @Inject
    public MyViewModelFactory() {
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MyViewModel.class)) {
            return (T) new MyViewModel(application, retrofit, rssDAO, executor);
        }
        throw new IllegalArgumentException("Wrong ViewModel class");
    }
}