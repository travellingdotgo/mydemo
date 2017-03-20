package com.seeiner.tool.common;


import android.content.Context;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetUtil {

    public final static String TAG = "NetUtil";

    public static long ping(String addr ){/* deprecated */
        boolean isReachable = true ;
        int  timeOut = 120000;
        long timeSpend = Long.MAX_VALUE;
        try {
            long start = System.currentTimeMillis();
            isReachable = InetAddress.getByName(addr).isReachable(timeOut);
            timeSpend = System.currentTimeMillis() - start;
        }  catch (UnknownHostException e) {
            e.printStackTrace();
            isReachable = false;
        }catch (IOException e) {
            e.printStackTrace();
            isReachable = false;
        }
        if( !isReachable ){
            return Long.MAX_VALUE;
        }else{
            return timeSpend;
        }
    }

    public static long shellPing(){
        boolean isReachable = false;
        long timeSpend = Long.MAX_VALUE;
        try{
            long start = System.currentTimeMillis();
            Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 -w 5 www.baidu.com");
            int returnVal = p1.waitFor();
            timeSpend = System.currentTimeMillis() - start;
            isReachable = (returnVal==0);
        }catch (UnknownHostException e) {
            e.printStackTrace();
            isReachable = false;
        }catch (IOException e) {
            e.printStackTrace();
            isReachable = false;
        }catch (Exception e) {
            e.printStackTrace();
            isReachable = false;
        }

        if (!isReachable){
            return Long.MAX_VALUE;
        }
        return timeSpend;
    }


    public static void setWifiNeverSleep( Context context){
        System.out.println("---> WIFI_SLEEP_POLICY_DEFAULT="+ Settings.System.WIFI_SLEEP_POLICY_DEFAULT);
        System.out.println("---> WIFI_SLEEP_POLICY_NEVER="+ Settings.System.WIFI_SLEEP_POLICY_NEVER);

        int wifiSleepPolicy=0;
        wifiSleepPolicy=Settings.System.getInt(context.getContentResolver(),
                android.provider.Settings.System.WIFI_SLEEP_POLICY,
                Settings.System.WIFI_SLEEP_POLICY_DEFAULT);
        System.out.println("---> 修改前的Wifi休眠策略值 WIFI_SLEEP_POLICY="+wifiSleepPolicy);


        Settings.System.putInt(context.getContentResolver(),
                android.provider.Settings.System.WIFI_SLEEP_POLICY,
                Settings.System.WIFI_SLEEP_POLICY_NEVER);


        wifiSleepPolicy=Settings.System.getInt(context.getContentResolver(),
                android.provider.Settings.System.WIFI_SLEEP_POLICY,
                Settings.System.WIFI_SLEEP_POLICY_DEFAULT);
        System.out.println("---> 修改后的Wifi休眠策略值 WIFI_SLEEP_POLICY="+wifiSleepPolicy);
    }

    public static PowerManager.WakeLock acqureWakeLock( Context context ){
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "MyWakelockTag");
        if (null != wakeLock){
            if (wakeLock.isHeld()){
                Log.e(TAG, "wakeLock Held when created");
            }else{
                wakeLock.acquire();
            }

            return wakeLock;
        }
        else{
            Log.e(TAG, "null == wakeLock");
            return null;
        }


    }
}
