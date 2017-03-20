package com.seeiner.tool.mydemo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.coolerfall.daemon.Daemon;
import com.orhanobut.logger.Logger;
import com.seeiner.tool.common.NetUtil;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.concurrent.Semaphore;

public class MainActivity extends AppCompatActivity {

    public final static String TAG = "MainActivity";
    public final static int REQUEST_IGNORE_BATTERY_CODE = 0;

    private SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int ApiVersion = Build.VERSION.SDK_INT;
        Log.d(TAG, "APIVersion=" + ApiVersion);
        if( ApiVersion< Build.VERSION_CODES.LOLLIPOP ) {
            Daemon.run( getApplicationContext(), TestService.class, 2);
        }else if(  ApiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP
                &&  ApiVersion < android.os.Build.VERSION_CODES.M ) {
            TestJobService.initDaemon(getApplicationContext());
        }else if( ApiVersion >= android.os.Build.VERSION_CODES.M) {
                boolean b = isIgnoringBatteryOptimizations(this);
            Toast.makeText(this, "isIgnoringBatteryOptimizations: " + b, Toast.LENGTH_SHORT).show();

                tryIgnoreBatteryOption(this);
        //    }
        }





        seekBar = (SeekBar) findViewById(R.id.seekBar1);
        seekBar.setMax(255);

        float curBrightnessValue = 0;
        try {
            curBrightnessValue = android.provider.Settings.System.getInt(
                    getContentResolver(),
                    android.provider.Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        int screen_brightness = (int) curBrightnessValue;
        seekBar.setProgress(screen_brightness);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue,
                                          boolean fromUser) {
                progress = progresValue;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                android.provider.Settings.System.putInt(getContentResolver(),
                        android.provider.Settings.System.SCREEN_BRIGHTNESS,
                        progress);
                Logger.d("progress: " + progress);

                WindowManager.LayoutParams params = getWindow().getAttributes();
                params.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                params.screenBrightness = 0;
                getWindow().setAttributes(params);
            }
        });







    }

    @TargetApi(Build.VERSION_CODES.M)
    public static boolean isIgnoringBatteryOptimizations(Activity activity){
        String packageName = activity.getPackageName();
        PowerManager pm = (PowerManager) activity
                .getSystemService(Context.POWER_SERVICE);
        if (pm.isIgnoringBatteryOptimizations(packageName)) {
            return true;
        }else {
            return false;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static void tryIgnoreBatteryOption(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                Intent intent = new Intent();
                String packageName = activity.getPackageName();
                PowerManager pm = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
                if (!pm.isIgnoringBatteryOptimizations(packageName)) {
//               intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                    intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    intent.setData(Uri.parse("package:" + packageName));
                    Log.d(TAG, "packageName: " +  packageName);
                    activity.startActivityForResult(intent, REQUEST_IGNORE_BATTERY_CODE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IGNORE_BATTERY_CODE){
                Toast.makeText(this, "^_^ Starting Daemon", Toast.LENGTH_SHORT).show();
                TestJobService.initDaemon(getApplicationContext());
            }
        }else if (resultCode == RESULT_CANCELED){
            if (requestCode == REQUEST_IGNORE_BATTERY_CODE){
                Toast.makeText(this, "请开启忽略电池优化", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
