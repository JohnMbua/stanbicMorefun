package com.stanbicagent;

import androidx.appcompat.app.AppCompatActivity;

import security.SecurityLayer;

/**
 * Created by deeru on 15-07-2015.
 */
public class ControlActivity extends AppCompatActivity {
    private static final String TAG=ControlActivity.class.getName();

    /**
     * Gets reference to global Application
     * @return must always be type of ControlApplication! See AndroidManifest.xml
     */
    public ControlApplication getApp()
    {
        return (ControlApplication )this.getApplication();
    }

    @Override
    public void onUserInteraction()
    {
        super.onUserInteraction();
        getApp().touch();
        SecurityLayer.Log(TAG, "User interaction to " + this.toString());
    }
}
