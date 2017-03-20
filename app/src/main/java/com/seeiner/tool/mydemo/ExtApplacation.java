package com.seeiner.tool.mydemo;

import android.app.Application;
import android.content.Intent;
import android.support.multidex.MultiDexApplication;

import com.seeiner.tool.common.NetworkMonitorIntentService;

/**
 * Created by Administrator on 2016/8/10.
 */
public class ExtApplacation extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

    //    Intent intent = new Intent( ExtApplacation.this, NetworkMonitorIntentService.class);
    //    startService( intent );
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
