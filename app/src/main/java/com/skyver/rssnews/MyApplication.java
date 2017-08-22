package com.skyver.rssnews;

import android.app.Application;

import com.skyver.rssnews.dagger.AppModule;
import com.skyver.rssnews.dagger.DaggerNetComponent;
import com.skyver.rssnews.dagger.NetComponent;
import com.skyver.rssnews.dagger.NetModule;
import com.zxy.recovery.callback.RecoveryCallback;
import com.zxy.recovery.core.Recovery;

import timber.log.Timber;

import static com.skyver.rssnews.util.Constants.API_URL;

/**
 * Created by skyver on 8/19/17.
 */

public class MyApplication extends Application {


    private NetComponent mNetComponent;

    @Override
    public void onCreate()
    {
        super.onCreate();

        initTimber();

        //////////////////////Recovery FRAMEWORK
        Recovery.getInstance()
                .debug(true)
                .recoverInBackground(false)
                .recoverStack(true)
                .mainPage(MainActivity.class)
                .recoverEnabled(false)
                .callback(new RecoveryCallback() {
                    @Override
                    public void stackTrace(String s) {
                        Timber.d("stackTrace() "+s);
                        //you need to add your crash reporting tool here
                        //Ex: Crashlytics.logException(throwable);

                    }

                    @Override
                    public void cause(String s) {
                        Timber.d("cause() "+s);

                    }

                    @Override
                    public void exception(String s, String s1, String s2, int i) {
                        Timber.d("exception() s: "+s+"\ns1: "+s1+"\ns2; "+s2);

                    }

                    @Override
                    public void throwable(Throwable throwable) {
                        Timber.d("throwable() "+throwable);

                    }
                })
                .silent(true, Recovery.SilentMode.RECOVER_ACTIVITY_STACK)
                //.skip(TestActivity.class)
                .init(this);
        //////////////////////

        //Fabric.with(this, new Crashlytics());

        //FirebaseAnalytics.getInstance(this);

        // other things...

        mNetComponent = DaggerNetComponent.builder()
                .appModule(new AppModule(this))
                .netModule(new NetModule(API_URL))
                .build();
    }
    public NetComponent getNetComponent() {
        return mNetComponent;
    }

    private void initTimber() {
        Timber.plant(new Timber.DebugTree());
    }
}