package com.example.junzhen.systemrecovery;

import android.app.Application;
import android.content.Context;

/**
 * Created by junzhen on 2015/10/19.
 */
public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate(){
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext(){
        return context;
    }
}
