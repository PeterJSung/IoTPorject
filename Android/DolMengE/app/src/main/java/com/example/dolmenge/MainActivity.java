package com.example.dolmenge;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener {
    private BluetoothAdapter mBluetoothAdapter = null;
    public static final String PREFS_BLUETOOTH = "MY_BT_OPTION";
    private String currentBluetoothAddress = new String();

    private  FrameLayout layout;
    private ImageView hillView;
    private ImageView skyObject;

    private Display displayObject;
    private Point screenSize;

    private float HEIGHT_MAX_PER = 90;
    private float HEIGHT_MIN_PER = 70;

    private float ALPHA = 0.0f;
    private float YMAX = 0.0f;
    private float YMIN = 0.0f;

    private final int SKYOBJECT_SIZE = 180;

    private final int REQUEST_CONNECT_DEVICE = 1;

    private BluetoothChatService mChatService = null;

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int CONNECTION_LOST = 6;

    public static final String TOAST = "toast";
    public static final String DEVICE_NAME = "device_name";

    private String receiveData = new String();

    private Boolean isFabOpen = false;
    private FloatingActionButton fab_Main;
    private List<FloatingActionButton> fab_child;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;

    private CameraController cameraController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupMainScreen();
        setupBluetooth();
        setupFAB();
        setupCamera();
        RenderingFuntion(-1,-1,-1,-1);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.fab_main:

                animateFAB();
                break;
            case R.id.fab_child_BT:
                Intent btIntent = new Intent(getApplicationContext(), DeviceListActivity.class);
                startActivityForResult(btIntent, REQUEST_CONNECT_DEVICE);
                Log.d("SJM", "CALLING BT LIST");
                break;
            case R.id.fab_child_CAMERA:
                cameraController.callCamera();
                Log.d("SJM", "CALLING CAMERA");
                break;
            case R.id.fab_child_GALLARY:
                cameraController.callGallary();
                Log.d("SJM", "CALLING GALLERY");
                break;
            case R.id.fab_child_WATER_PURIFY:
                Intent wpIntent = new Intent(getApplicationContext(), WaterPurifyActivity.class);
                startActivity(wpIntent);
                Log.d("SJM", "CALLING WATER PURIFY");
                break;
            case R.id.fab_child_FACE:
                cameraController.callGallary();
                Log.d("SJM", "CALLING FACE");
                break;
        }
    }

    public void animateFAB(){

        if(isFabOpen){

            fab_Main.startAnimation(rotate_backward);
            for (FloatingActionButton eachChildFAB: fab_child) {
                eachChildFAB.startAnimation(fab_close);
                eachChildFAB.setClickable(false);
            }
            isFabOpen = false;
            Log.d("Raj", "close");

        } else {

            fab_Main.startAnimation(rotate_forward);
            for (FloatingActionButton eachChildFAB: fab_child) {
                eachChildFAB.startAnimation(fab_open);
                eachChildFAB.setClickable(true);
            }
            isFabOpen = true;
            Log.d("Raj","open");
        }
    }

    public void doingAction(){

    }

    public final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            Log.d("SJM", "BluetoothChatService.STATE_CONNECTED");
                            //  setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                            //  mConversationArrayAdapter.clear();
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            Log.d("SJM", "BluetoothChatService.STATE_CONNECTING");
                            //setStatus(R.string.title_connecting);
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                            Log.d("SJM", "BluetoothChatService.STATE_LISTEN");
                        case BluetoothChatService.STATE_NONE:
                            Log.d("SJM", "BluetoothChatService.STATE_NONE");
                            //setStatus(R.string.title_not_connected);
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    Log.d("SJM", "BluetoothChatService.MESSAGE_WRITE");
                    Log.d("SJM", writeMessage);
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    for(int i = 0 ; i < readMessage.length(); i++) {
                        char data = readMessage.charAt(i);
                        if(data == '#'){
                            receiveData = new String();
                            doingAction();
                        } else {
                            receiveData += data;
                        }
                    }

                    Log.d("SJM", "BluetoothChatService.MESSAGE_READ");
                    Log.d("SJM", readMessage);
                    //mChatService.write(readMessage.getBytes());
/*
                    if(readBuf[0] == 'C'&& now_Status==connection_Status_On)tx_Confirm('S');
                    if(readBuf[0] == 'Y'&& now_Status==connection_Status_On) {
                        now_Status=connection_Status_Data_RW;

                        Intent myIntent = new Intent(getApplicationContext(),iface.class);

                        startActivity(myIntent);

                    }
                    //readMessage.replaceAll("\\p{space}", "");
                    if(now_Status==connection_Status_Data_RW)iface.sended_String(readMessage);
                    //Log.d("sibla;;", readMessage);
                    */
                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    /*
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "Connected to "
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    now_Status=connection_Status_On;
                    */
                    Log.d("SJM", "BluetoothChatService.MESSAGE_DEVICE_NAME");
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
                case CONNECTION_LOST:
                    Log.d("SJM", "BluetoothChatService.CONNECTION_LOST");
                    //Log.d("dd", "susin");
                    break;
            }
        }
    };

    void RenderingFuntion(int testHour, int testMin,int testPot, int testEmotion){
        int hour= testHour == -1 ? Calendar.getInstance().getTime().getHours() : testHour;
        int min = testMin == -1 ? Calendar.getInstance().getTime().getMinutes() : testMin;

        int xValue = calcOffset(hour,min);

        float renderingX = ((float)xValue/720 * screenSize.x);
        float objectX = (renderingX) - screenSize.x/2;
        float renderingY = (int)(ALPHA * objectX * objectX + YMAX);

        renderingX -= (float)SKYOBJECT_SIZE / 2;
        renderingY -= (float)SKYOBJECT_SIZE / 2;
        skyObject.setX(renderingX);
        skyObject.setY(screenSize.y - renderingY);

        if(hour < 6 || hour >= 18){
            //저녁
            layout.setBackgroundResource(R.drawable.night);
            hillView.setColorFilter(Color.argb(126, 0, 0, 0));
            skyObject.setBackgroundResource(R.drawable.moon);
        } else {
            //아침
            layout.setBackgroundResource(R.drawable.morning);
            hillView.setColorFilter(Color.argb(0, 0, 0, 0));
            skyObject.setBackgroundResource(R.drawable.sun);
        }
    }

    private int calcOffset(int hour,int min){
        int ret = 0;
        if(hour < 6 || hour >= 18){
            //저녁
            if(hour < 6){
                ret = hour + 6;
            } else {
                ret = hour - 18;
            }
        } else {
            //아침
            ret = hour - 6;
        }
        ret *= 60;
        ret += min;
        return  ret;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:

                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data);
                }
                break;
        }
    }

    private void connectDevice(Intent data) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        currentBluetoothAddress = new String(address);
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device);
    }



    private void setupCamera(){
        cameraController = new CameraController(this);
    }

    private void setupBluetooth(){
        mChatService = new BluetoothChatService(this, mHandler);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        SharedPreferences settings = getSharedPreferences(PREFS_BLUETOOTH, 0);
        settings.getString("ADDRESS", currentBluetoothAddress);
    }

    private void setupMainScreen(){
        layout = (FrameLayout)findViewById(R.id.fLayoutMainActivity);
        hillView = (ImageView)findViewById(R.id.hillImage);
        skyObject = (ImageView)findViewById(R.id.skyObject);

        displayObject = getWindowManager().getDefaultDisplay();
        screenSize = new Point();
        displayObject.getSize(screenSize);

        // y Max = HEIGHT_MAX_PER / 100 * screenSize.Y
        // y MIN= HEIGHT_MIN_PER / 100 * screenSize.Y
        // yMIN = a*(screenSize.X/2)^2 + yMax
        // y = aX^2 + b; (X = (xValue/720 * screenSize.X) - screenSize.X/2 )
        YMAX = HEIGHT_MAX_PER / 100 * screenSize.y;
        YMIN = HEIGHT_MIN_PER / 100 * screenSize.y;

        ALPHA = (YMIN - YMAX)/(screenSize.x * screenSize.x / 4);
    }

    private void setupFAB(){
        fab_Main = (FloatingActionButton)findViewById(R.id.fab_main);
        fab_child = new ArrayList<>();
        fab_child.add((FloatingActionButton)findViewById(R.id.fab_child_BT));
        fab_child.add((FloatingActionButton)findViewById(R.id.fab_child_CAMERA));
        fab_child.add((FloatingActionButton)findViewById(R.id.fab_child_GALLARY));
        fab_child.add((FloatingActionButton)findViewById(R.id.fab_child_WATER_PURIFY));
        fab_child.add((FloatingActionButton)findViewById(R.id.fab_child_FACE));

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);

        fab_Main.setOnClickListener(this);
        for (FloatingActionButton eachChildFAB: fab_child) {
            eachChildFAB.setOnClickListener(this);
        }
    }
}