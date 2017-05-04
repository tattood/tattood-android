package com.tattood.tattood;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by eksi on 02/05/17.
 */

public class BaseApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
    }
}
