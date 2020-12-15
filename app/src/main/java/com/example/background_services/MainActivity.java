package com.example.background_services;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.background_services.constants.Constants;

public class MainActivity extends AppCompatActivity {
    public static final String MESSAGE_KEY = "service" ;
    ImageView splay,spause;
private MusicPlayerService musicPlayerService;
boolean mBound = false;
private ServiceConnection mserviceconn = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        MusicPlayerService.MyServiceBinder myserviceBinder = (MusicPlayerService.MyServiceBinder) service;
        musicPlayerService = myserviceBinder.getService();
        mBound = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Toast.makeText(MainActivity.this,"Disconnected",Toast.LENGTH_LONG).show();
    }
};

private final BroadcastReceiver mbroadcast = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        String result = intent.getStringExtra(MESSAGE_KEY);
        switch (result){
            case "done":{
                    splay.setVisibility(View.VISIBLE);
                    spause.setVisibility(View.GONE);
                    break;
            }
            case "play":{

                splay.setVisibility(View.GONE);
                spause.setVisibility(View.VISIBLE);
                break;
            }
            case "pause":{
                splay.setVisibility(View.VISIBLE);
                spause.setVisibility(View.GONE);
                break;
            }
        }

    }
};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        splay =(ImageView)findViewById(R.id.splay);
        spause = (ImageView)findViewById(R.id.spause);
        if(mBound){
            if(musicPlayerService.isPlaying()){
                splay.setVisibility(View.GONE);
                spause.setVisibility(View.VISIBLE);
            }
        }
        splay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBound){
                    if(!musicPlayerService.isPlaying()){

                        /*
                        start service to use for connect bind service to started services
                         */


                        Intent intent = new Intent(MainActivity.this, MusicPlayerService.class);
                        intent.setAction(Constants.MUSIC_SERVICE_ACTION_START);
                        startService(intent);

                        musicPlayerService.play();
                        splay.setVisibility(View.GONE);
                        spause.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        spause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBound){
                    if(musicPlayerService.isPlaying()){
                        musicPlayerService.pause();
                        spause.setVisibility(View.GONE);
                        splay.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this,MusicPlayerService.class);
        bindService(intent,mserviceconn,BIND_AUTO_CREATE );

        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(mbroadcast, new IntentFilter(MusicPlayerService.MUSICCOMPLETE));
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mBound){
            unbindService(mserviceconn);
            mBound=false;
            spause.setVisibility(View.GONE);
            splay.setVisibility(View.VISIBLE);
        }
        LocalBroadcastManager.getInstance(getApplicationContext())
                .unregisterReceiver(mbroadcast);
    }




}