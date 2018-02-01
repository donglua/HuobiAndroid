package com.github.donglua.huobi;

import android.app.Application;

import timber.log.Timber;

/**
 * Application
 *
 * Created by donglua on 17-12-27.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Timber.plant(new Timber.DebugTree());
    }
}
