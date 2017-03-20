package com.seeiner.tool.mydemo;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import com.seeiner.tool.common.NetUtil;

public class TestService extends Service {

    private static final String TAG = "TestService";

    public TestService() {
        Log.i(TAG, "TestService");
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
        NetUtil.setWifiNeverSleep(getApplicationContext());

        long pingTimeCost = NetUtil.shellPing();
        if( pingTimeCost == Long.MAX_VALUE ){
            MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.refuse_call);
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.reset();
                    mp.release();
                }
            });
            mp.start();
        }else{
            MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.opendoor);
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.reset();
                    mp.release();
                }
            });
            mp.start();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.i(TAG, "onBind");
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
