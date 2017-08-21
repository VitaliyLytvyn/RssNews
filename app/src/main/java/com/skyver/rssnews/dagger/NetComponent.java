package com.skyver.rssnews.dagger;

import com.skyver.rssnews.DetailsFragment;
import com.skyver.rssnews.MainActivity;
import com.skyver.rssnews.NewsShortFragment;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by skyver on 8/19/17.
 */


@Singleton
@Component(modules = {AppModule.class, NetModule.class, DbModule.class})
public interface NetComponent {
    void inject(MainActivity mainActivity);
    void inject(NewsShortFragment newsShortFragment);
    void inject(DetailsFragment detailsFragment);

}