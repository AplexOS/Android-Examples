package com.aplex.erasemmc.erasemmc.base;

import android.app.Application;

import com.cengalabs.flatui.FlatUI;

/**
 * Created by chengmz on 2016/9/14.
 */
public class BaseApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        FlatUI.setDefaultTheme(FlatUI.SKY);
    }
}
