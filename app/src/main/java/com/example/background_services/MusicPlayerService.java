package com.example.background_services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.background_services.constants.Constants;

import static android.support.v4.app.NotificationCompat.PRIORITY_MIN;


public class MusicPlayerService extends Service {
    public static final String MUSICCOMPLETE = "Musiccomplete";
    private static final String TAG = "MYTAG";
    private final Binder mBinder = new MyServiceBinder();
    private MediaPlayer mediaPlayer;
    Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = MediaPlayer.create(this,R.raw.treat);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Intent intent = new Intent(MUSICCOMPLETE);
                intent.putExtra(MainActivity.MESSAGE_KEY,"done");
                LocalBroadcastManager.getInstance(getApplicationContext())
                        .sendBroadcast(intent);
                stopForeground(true);
                stopSelf();
            }
        });
    }
    public class MyServiceBinder extends Binder{
        public MusicPlayerService getService(){
            return MusicPlayerService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        switch (intent.getAction()){
            case Constants.MUSIC_SERVICE_ACTION_PLAY :{
                Intent plaintent = new Intent(MUSICCOMPLETE);
                plaintent.putExtra(MainActivity.MESSAGE_KEY,"play");
                LocalBroadcastManager.getInstance(getApplicationContext())
                        .sendBroadcast(plaintent);
                Log.d(TAG, "On start command :Play");
                play();
                break;
            }
            case Constants.MUSIC_SERVICE_ACTION_PAUSE :{
                Intent pauintent = new Intent(MUSICCOMPLETE);
                pauintent.putExtra(MainActivity.MESSAGE_KEY,"pause");
                LocalBroadcastManager.getInstance(getApplicationContext())
                        .sendBroadcast(pauintent);
                Log.d(TAG, "On start command :Pause");
                pause();
                break;
            }
            case Constants.MUSIC_SERVICE_ACTION_STOP :{
                Log.d(TAG, "On start command :Stop");
                stop();
                stopForeground(true);
                stopSelf();
                break;
            }
            case Constants.MUSIC_SERVICE_ACTION_START :{
                Log.d(TAG, "On start command :Start");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    showNotification();
                else
                    startForeground(1, new Notification());
                break;
            }

            default:{
                stopSelf();
            }


        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
//            showNotification();
//        else
//            startForeground(1, new Notification());
        Log.d(TAG, "On start command");
        return START_NOT_STICKY;
    }



    public void showNotification(){
        //Intent for play button
        Intent pintent = new Intent(this, MusicPlayerService.class);
        pintent.setAction(Constants.MUSIC_SERVICE_ACTION_PLAY);
        PendingIntent playIntent = PendingIntent.getService(this, 100, pintent, 0);

        //Intent for pause button

        Intent psintent = new Intent(this, MusicPlayerService.class);
        psintent.setAction(Constants.MUSIC_SERVICE_ACTION_PAUSE);
        PendingIntent pauseIntent = PendingIntent.getService(this, 100, psintent, 0);

        // Intent for stop button

        Intent sintent = new Intent(this, MusicPlayerService.class);
        sintent.setAction(Constants.MUSIC_SERVICE_ACTION_STOP);
        PendingIntent stopIntent = PendingIntent.getService(this, 100, sintent, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {


            String NOTIFICATION_CHANNEL_ID = "com.example.simpleapp";
            String channelName = "My Background Service";
            NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
            Notification notification = notificationBuilder.setOngoing(true)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("App is running in background")
                    .addAction(new NotificationCompat.Action(android.R.drawable.ic_media_play, "play", playIntent))
                    .addAction(new NotificationCompat.Action(android.R.drawable.ic_media_pause, "pause", pauseIntent))
                    .addAction(new NotificationCompat.Action(android.R.drawable.ic_media_rew, "Stop", stopIntent))
                    .setPriority(NotificationManager.IMPORTANCE_MIN)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .build();
            startForeground(2, notification);
        }else{
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channelID");
                    builder.setContentTitle("Abhishek Notification")
                    .setContentText("this is demo music player")
                    .addAction(new NotificationCompat.Action(android.R.drawable.ic_media_play, "play", playIntent))
                    .addAction(new NotificationCompat.Action(android.R.drawable.ic_media_pause, "pause", pauseIntent))
                    .addAction(new NotificationCompat.Action(android.R.drawable.ic_media_rew, "Stop", stopIntent))
                    .setPriority(NotificationManager.IMPORTANCE_MIN)
                    .setPriority(NotificationManager.IMPORTANCE_MIN)
                            .setPriority(NotificationManager.IMPORTANCE_MIN)
                    .setSmallIcon(R.mipmap.ic_launcher);
                    startForeground(123, builder.build());
        }
    }

/*
//    private void showNotification() {
//        @RequiresApi(Build.VERSION_CODES.O){
//        private fun createNotificationChannel(channelId: String, channelName: String): String {
//                val chan = NotificationChannel(channelId,
//                        channelName, NotificationManager.IMPORTANCE_NONE)
//                chan.lightColor = Color.BLUE
//                chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
//                val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//                service.createNotificationChannel(chan)
//
//            }
////        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channelID");
////        builder.setContentTitle("Abhishek Notification")
////                .setContentText("this is demo music player")
////                .setSmallIcon(R.mipmap.ic_launcher);
////        startForeground(123, builder.build());
//    }
run only for less than oreo
 */

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "On Bind");
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "On Destroy");
        mediaPlayer.release();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "On UnBind");
        return super.onUnbind(intent);
    }

    //// public client methods
    public boolean isPlaying(){
        return mediaPlayer.isPlaying();
    }

    public void play(){
        mediaPlayer.start();
    }

    public void pause(){
        mediaPlayer.pause();
    }

    public void stop(){
        mediaPlayer.stop();
    }


}
