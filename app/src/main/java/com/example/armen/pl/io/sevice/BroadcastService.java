package com.example.armen.pl.io.sevice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.armen.pl.ui.fragment.AboutFragment;

import java.util.ArrayList;

/**
 * Created by Armen on 7/3/2017.
 */

public class BroadcastService extends Service {
    private String LOG_TAG = this.getClass().getSimpleName();
    private ArrayList<String> mList;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(LOG_TAG, "In onCreate");
        mList = new ArrayList<>();
        mList.add("Object 1");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(LOG_TAG, "In onStartCommand");
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(AboutFragment.mBroadcastStringAction);
                broadcastIntent.putExtra("Data", "Broadcast Data");
                sendBroadcast(broadcastIntent);
            }
        }).start();
        return START_REDELIVER_INTENT;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}