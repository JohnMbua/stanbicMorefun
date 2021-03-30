package com.stanbicagent;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.stanbicagent.R;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.google.firebase.analytics.FirebaseAnalytics;

import io.fabric.sdk.android.Fabric;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import utils.GlobalVals;

public class ApplicationClass  extends MultiDexApplication {
    private static final String TAG = InjectedApplication.class.getSimpleName();
    private FirebaseAnalytics mFirebaseAnalytics;
    private static ApplicationClass instance;
    private GlobalVals GlobalVals;

    public ApplicationClass(){
        super();
        this.instance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        Fabric.with(this, new Answers(), new Crashlytics());

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Montserrat-Light.otf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        MultiDex.install(this);

        GlobalVals = new GlobalVals();
    }

    public static ApplicationClass get(){
        return instance;
    }

     public GlobalVals getGlobalVals() {
        return GlobalVals;
    }

}