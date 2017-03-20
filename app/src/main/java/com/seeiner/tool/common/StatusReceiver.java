package com.seeiner.tool.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class StatusReceiver extends BroadcastReceiver {
    public static final String TAG = "StatusReceiver";

    public StatusReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String str = "";
        Bundle bundle = intent.getExtras();
        if( bundle!=null ){
            long lSuccessCnt = bundle.getLong("SuccessCnt");    str += " lSuccessCnt="; str+= lSuccessCnt;
            long lFailureCnt = bundle.getLong("FailureCnt");    str += " lFailureCnt="; str+= lFailureCnt;

            if( lSuccessCnt>0 ){
                long lFirstSuccessTook = bundle.getLong("FirstSuccessTook");    str += " lFirstSuccessTook="; str+= lFirstSuccessTook;
                long lLastPingTimeCost = bundle.getLong("LastPingTimeCost");
                if( lLastPingTimeCost!=Long.MAX_VALUE ){
                    str += " lLastPingTimeCost="; str+= lLastPingTimeCost;
                }
            }
        }

        Log.e(TAG, str );
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }
}
