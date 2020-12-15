package com.example.background_services;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class MyForegroundService extends Service {

    public static final String MY_TAG = "MY_TAG";

    public MyForegroundService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        showNotification();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(MY_TAG,"Run Starting Download");
                int i =0;
                while (i<10){
                    Log.d(MY_TAG,"Processing... download "+(i+1));
                    try{
                        Thread.sleep(1000);
                    }catch (Exception e){e.printStackTrace();}
                    i++;
                }
            Log.d(MY_TAG,"Download Completed");
                stopForeground(true);
                stopSelf();

            }
        });
        thread.start();
        return START_STICKY;
    }

    private void showNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"channelID");

        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentText("This is started Sarvices")
                .setContentTitle("Title");
        Notification notification = builder.build();
        startForeground(123, notification);
    }



    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}