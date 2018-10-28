package com.example.dolmenge;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraController {
    private String imagePath;
    private Activity activity;

    public CameraController(Activity act){
        activity = act;
    }

    private File savePictureFile() {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss") .format(new Date());
        String fileName = "IMG_" + timestamp; /** * 사진파일이 저장될 장소를 구한다. * 외장메모리에서 사진을 저장하는 폴더를 찾아서 * 그곳에 MYAPP 이라는 폴더를 만든다. */
        File pictureStorage = new File( Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_PICTURES), GlobalData.DIRECTORY + "/");
        // 만약 장소가 존재하지 않는다면 폴더를 새롭게 만든다.
        if (!pictureStorage.exists()) {
            /** * mkdir은 폴더를 하나만 만들고, * mkdirs는 경로상에 존재하는 모든 폴더를 만들어준다. */
            pictureStorage.mkdirs();
        }
        try {
            File file = File.createTempFile(fileName, ".jpg", pictureStorage);
            // ImageView에 보여주기위해 사진파일의 절대 경로를 얻어온다.
            imagePath = file.getAbsolutePath(); // 찍힌 사진을 "갤러리" 앱에 추가한다.
            Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE );
            File f = new File( imagePath );
            Uri contentUri = Uri.fromFile( f );
            mediaScanIntent.setData( contentUri );
            activity.sendBroadcast( mediaScanIntent );
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void callCamera() {

        Intent cameraApp = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // 찍은 사진을 보관할 파일 객체를 만들어서 보낸다.
        File picture = savePictureFile();
        if (picture != null)
        {
            Uri photoURI = FileProvider.getUriForFile(activity.getApplicationContext(), "com.example.dolmenge.fileprovider", picture);
            cameraApp.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            activity.startActivityForResult(cameraApp, 10000);
        }
        /*
        Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            PackageManager pm = activity.getPackageManager();

            final ResolveInfo mInfo = pm.resolveActivity(i, 0);

            Intent intent = new Intent();
            intent.setComponent(new ComponentName(mInfo.activityInfo.packageName, mInfo.activityInfo.name));
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            activity.startActivity(intent);
        } catch (Exception e){
            Log.i("TAG", "Unable to launch camera: " + e);
        }
    */
    }
    public void callGallary() {
        Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivity(i);
    }

}
