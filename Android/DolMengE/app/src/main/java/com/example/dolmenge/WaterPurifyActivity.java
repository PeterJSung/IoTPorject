package com.example.dolmenge;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class WaterPurifyActivity extends Activity {

    static int a = 0;

    private Thread timeThread = null;
    private boolean isSend = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_water_purify);

        final ImageButton scanButton = (ImageButton) findViewById(R.id.water_purify_btn);
        /*
        scanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                BigInteger bi = BigInteger.valueOf(a);
                byte[] bytes = bi.toByteArray();
                MainActivity.mChatService.write(bytes);
                a++;
            }
        });
        */
        timeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                if(isSend == false){
                    try{
                        Thread.sleep(10);
                    } catch (Exception e){

                    }
                    isSend = true;
                }
            }
        });
        timeThread.start();

        scanButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                scanButton.setPressed(true);
                Log.d("SJM", "PRESSED");

                if(isSend){
                    MainActivity.mChatService.write(MainActivity.S2BA(new String("W")));
                }
                return true;
            }
        });

    }
}