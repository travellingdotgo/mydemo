package com.seeiner.tool.common;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;


public class NetworkMonitorIntentService extends IntentService {
    public static final String TAG = "NetworkMonitorIS";
    public static final String NOTIFY_ACTION = "NetworkMonitorIS_NOTIFY_ACTION";
    private long mlServiceStartedTick=0;
    private long mlFirstPingSuccdTick=0;

    private boolean mbWorking = false;

    private long mlSuccessCnt = 0;
    private long mlFailureCnt = 0;

    private long mlLastPingTimeCost = 0;

    private int PING_INTERVAL_MS = 10*1000;// milli sec

    private void notifyNetStatus(){
        Intent intent = new Intent(NOTIFY_ACTION );
        intent.putExtra("SuccessCnt", mlSuccessCnt);
        intent.putExtra("FailureCnt", mlFailureCnt);
        intent.putExtra("LastPingTimeCost", mlLastPingTimeCost);

        if( mlSuccessCnt>0 ){
            intent.putExtra("FirstSuccessTook", mlFirstPingSuccdTick-mlServiceStartedTick);
        }

        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        sendBroadcast(intent);
    }


    public NetworkMonitorIntentService() {
        super("NetworkMonitorIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mlServiceStartedTick = System.currentTimeMillis();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();

            while( mbWorking ){
                try{
                    long time = NetUtil.shellPing();

                    if(time== Long.MAX_VALUE){
                        mlFailureCnt ++;
                        mlLastPingTimeCost = time;
                        notifyNetStatus();

                        if(mlFirstPingSuccdTick!=0) {
                            Log.v(TAG, "mlSuccessCnt=" + mlSuccessCnt + ", mlFailureCnt=" + mlFailureCnt + ", FirstSuccessTook: " + "null"                                               + ",  LastTry:  timeout!" );
                        }
                        else{
                            Log.v(TAG, "mlSuccessCnt=" + mlSuccessCnt + ", mlFailureCnt=" + mlFailureCnt + ", FirstSuccessTook: " + (mlFirstPingSuccdTick - mlServiceStartedTick)  + ",  LastTry:  timeout!" );
                        }

                        Thread.sleep( PING_INTERVAL_MS );
                    }
                    else{
                        mlSuccessCnt ++;
                        mlLastPingTimeCost = time;
                        if(mlSuccessCnt==1){
                            mlFirstPingSuccdTick=System.currentTimeMillis();
                        }
                        notifyNetStatus();

                        Log.v(TAG, "mlSuccessCnt=" + mlSuccessCnt + ", mlFailureCnt=" + mlFailureCnt + ", FirstSuccessTook: " + (mlFirstPingSuccdTick - mlServiceStartedTick) + ",  LastTry: time= " + time);

                        Thread.sleep(PING_INTERVAL_MS);
                    }
                }
                catch (Exception e){
                    Log.e(TAG, "Exception: " + e);
                }
            } // end of while
        }
    }// end of onHandleIntent

}
