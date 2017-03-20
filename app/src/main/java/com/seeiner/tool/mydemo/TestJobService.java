package com.seeiner.tool.mydemo;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.util.Log;


import com.seeiner.tool.common.NetUtil;
import java.util.LinkedList;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class TestJobService extends JobService {
    private static final String TAG = "TestJobService";

    private boolean mbRunning = false;

    private static long slInstance = 0;
    public static int sJobId = 1;

    PowerManager.WakeLock wakeLock = null;

    @Override
    public void onCreate() {
        super.onCreate();
        slInstance ++;
        Log.i(TAG, "Service created, slInstance=" + slInstance);

        NetUtil.setWifiNeverSleep(getApplicationContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        slInstance --;
        Log.i(TAG, "Service destroyed, slInstance=" + slInstance);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }



    @Override
    public boolean onStartJob(final JobParameters params) {
        Log.i(TAG, "on start job: " + params.getJobId());

        mbRunning = true;

        new Thread(new Runnable() {
           public void run() {
                for(int i=0; i<1; i++){
                    Log.i(TAG, "onStartJob while loop, ThreadID=" + Thread.currentThread().getId() );

                    long pingTimeCost = Long.MAX_VALUE;
                    try{
                        pingTimeCost = NetUtil.shellPing();
                    }catch (Exception e){
                        Log.e(TAG, "shellPing exception: " + e.toString() );
                    }
                    if( pingTimeCost == Long.MAX_VALUE ){
                        Log.e(TAG, "shellPing ret: Long.MAX_VALUE" );
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
                        Log.e(TAG, "shellPing ret: " + pingTimeCost );
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
                }// end of while

               jobFinished( params, false );
            }
        }).start();

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        mbRunning = false;

        Log.i(TAG, "on stop job: " + params.getJobId());
        return false;
    }

    public void scheduleJob(JobInfo t) {
        Log.d(TAG, "Scheduling job");
        JobScheduler tm =
                (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        tm.schedule(t);
    }



    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void initDaemon( Context context ){
        ComponentName mServiceComponent = new ComponentName(context, TestJobService.class);

        JobInfo jobInfo = new JobInfo.Builder( TestJobService.sJobId++, mServiceComponent)
                .setPeriodic(60*1000)
                .setPersisted(true)
                .build();
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if( jobScheduler==null ){
            Log.e(TAG, "getSystemService(Context.JOB_SCHEDULER_SERVICE) failed !!!");
            return;
        }
        int result = jobScheduler.schedule(jobInfo);
        if (result == JobScheduler.RESULT_SUCCESS ) {
            Log.i(TAG, "Job scheduled successfully!");
        } else {
            Log.e(TAG, "Job scheduled failed: result=" + result);
        }
    }

}
