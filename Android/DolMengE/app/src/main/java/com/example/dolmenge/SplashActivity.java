package com.example.dolmenge;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;

public class SplashActivity extends Activity {
    enum PRECHECK {
        BLUE_TOOTH,
        CAMERA,
        LOCAL_STORAGE
    }

    private BluetoothAdapter mBluetoothAdapter = null;
    private static final int REQUEST_ENABLE_BT = 3;
    private static final int REQUEST_DISCOVERABLE_BT = 0;

    private boolean waitUntilLoop = true;

    ProgressBar progressObj = null;
    TextView textStatusObj = null;

    private boolean cameraPermission = false;
    private boolean externalPermission = false;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                Log.d("SJM", "RESULT OK");
                waitUntilLoop = false;

                //           progressToNextCheck();
            } else {
                waitUntilLoop = false;

            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.getProgressDrawable().setColorFilter(
                Color.WHITE, android.graphics.PorterDuff.Mode.SRC_IN);
        progressObj = (ProgressBar) findViewById(R.id.progressBar) ;
        textStatusObj = (TextView) findViewById(R.id.textViewStatus);

        startLoading();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==0){
            Log.d("SJM SPLASH","PERMISSION");
            if(permissions.length == grantResults.length){
                int length = permissions.length;
                for(int i = 0 ; i < length; i++){
                    String permission = permissions[i];
                    boolean isAccept = grantResults[i] == 0 ? true : false;
                    Log.d("SJM LOCAL" , permission);
                    Log.d("SJM LOCAL" , "isACC " + isAccept);
                    if(permission.equals("android.permission.CAMERA") && isAccept){
                        cameraPermission = true;
                    }
                    if(permission.equals("android.permission.WRITE_EXTERNAL_STORAGE") && isAccept){
                        externalPermission = true;
                    }

                }
            }
            waitUntilLoop = false;
        }
    }

    class RetData{
        boolean isFailed = false;
        String msg = "";
    }

    private void SetStatus(int gage, String msg){
        progressObj.setProgress(gage);
        textStatusObj.setText(msg);
    }

    private RetData CheckBlueTooth() {
        RetData ret = new RetData();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //0 ~ 33
        if (mBluetoothAdapter == null) {
            ret.isFailed = true;
            ret.msg = "단말에서 블루투스 통신 기능을 지원하지 않습니다.";
        } else if (!mBluetoothAdapter.isEnabled()) {
            waitUntilLoop = true;
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent,REQUEST_ENABLE_BT);
            SetStatus(15,"블루투스가 초기화 중...");
            while(waitUntilLoop){

                Log.d("SJM","WHILE");
                try{
                    Thread.sleep(10);
                } catch (Exception e){
                    Log.d("ERR","CRASH " + e);
                }
            }
            Log.d("SJM","SUCCESS BT");
            if(!mBluetoothAdapter.isEnabled()) {
                ret.isFailed = true;
                ret.msg = "블루투스를 꼭 켜주세요.";
            } else {
                SetStatus(33,"블루투스 활성화 완료.");
            }
        } else {
            SetStatus(33,"블루투스 활성화 완료.");
        }
        return ret;
    }

    private RetData CheckCamera() {
        RetData ret = new RetData();

        Context ctx = this;
        PackageManager pmg = ctx.getPackageManager();
        ActivityCompat.requestPermissions(SplashActivity.this,new String[]{Manifest.permission.CAMERA},0);

        waitUntilLoop = true;
        while(waitUntilLoop){

            Log.d("SJM","WHILE");
            try{
                Thread.sleep(10);
            } catch (Exception e){
                Log.d("ERR","CRASH " + e);
            }
        }
        Log.d("SJM", "AFTER CAM PER");
        Log.d("SJM", "feture = " + pmg.hasSystemFeature(PackageManager.FEATURE_CAMERA));
        Log.d("SJM", "camera = " + cameraPermission);
        if(pmg.hasSystemFeature(PackageManager.FEATURE_CAMERA) && cameraPermission){
            SetStatus(66,"카메라 활성화 완료.");
        } else {
            ret.isFailed = true;
            ret.msg = "카메라를 실행 시킬 수 없습니다.";
        }
        return ret;
    }

    private RetData CheckLocalStorage() {
        RetData ret = new RetData();

        String state = Environment.getExternalStorageState();

        ActivityCompat.requestPermissions(SplashActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
        waitUntilLoop = true;
        while(waitUntilLoop){

            Log.d("SJM","WHILE");
            try{
                Thread.sleep(10);
            } catch (Exception e){
                Log.d("ERR","CRASH " + e);
            }
        }

        if (!Environment.MEDIA_MOUNTED.equals(state) || !externalPermission) {
            ret.isFailed = true;
            ret.msg = "외부 저장소 접근 불가.";
        } else {
            String SaveFolderPath = getFilesDir().getAbsolutePath() + GlobalData.DIRECTORY;

            File files = new File(SaveFolderPath);
            if(!files.exists()) {
                files.mkdir();
            }

            if(files.exists()){
                SetStatus(100,"저장소 체크 완료.");
            } else {
                ret.isFailed = true;
                ret.msg = "저장소 생성 실패.";
            }
        }
        return ret;
    }
    RetData ret = new RetData();
    private void startLoading() {
        final SplashActivity that = this;
        final Handler handler = new Handler();
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                for (PRECHECK checkData : PRECHECK.values()) {
                    if(ret.isFailed){  break;  }
                    switch (checkData) {
                        case BLUE_TOOTH:
                            ret = CheckBlueTooth();
                            break;
                        case CAMERA:
                            ret = CheckCamera();
                            break;
                        case LOCAL_STORAGE:
                            ret = CheckLocalStorage();
                            break;
                    }
                    try{
                        Thread.sleep(GlobalUtil.RandomRange(600,800));
                    } catch (Exception e){
                        Log.d("ERR","CRASH " + e);
                    }
                }
                if(ret.isFailed){
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast toast = Toast.makeText(that, ret.msg, Toast.LENGTH_SHORT);
                            toast.show();
                            finish();
                        }
                    });
                } else {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent i = new Intent(that, MainActivity.class);
                            startActivity(i);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            finish();
                        }
                    }, 1);
                }
            }
        });
        th.start();

    }
}
