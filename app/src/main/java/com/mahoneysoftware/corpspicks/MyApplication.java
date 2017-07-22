package com.mahoneysoftware.corpspicks;

import android.app.Application;

/**
 * Created by Dylan on 7/18/2017.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
