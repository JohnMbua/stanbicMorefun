package com.stanbicagent;

import android.app.Application;

import security.SecurityLayer;

/**
 * Created by deeru on 15-07-2015.
 */
public class ControlApplication extends Application {
    private static final String TAG=ControlApplication.class.getName();
    private Waiter waiter;  //Thread which controls idle time

    // only lazy initializations here!
    @Override
    public void onCreate()
    {
        super.onCreate();
        SecurityLayer.Log(TAG, "Starting application" + this.toString());
        waiter=new Waiter(1*60*1000); //1 mins
        waiter.start();
    }

    public void touch()
    {
        waiter.touch();
    }
}
