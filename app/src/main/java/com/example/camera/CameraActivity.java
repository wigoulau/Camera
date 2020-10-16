package com.example.camera;

import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

public class CameraActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private static final String TAG = "CameraActivity";

    private SurfaceView mCameraSurface;
    private SurfaceHolder mHolder;
    private Button btnStartRecord;

    private static final int MEDIA_TYPE_IMAGE = 1;
    private static final int MEDIA_TYPE_VIDEO = 2;
    private int mRecordType;

    private Camera mCamera = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        mCameraSurface = findViewById(R.id.camera_preview);
        mHolder = mCameraSurface.getHolder();
        mHolder.addCallback(CameraActivity.this);

        mRecordType = MEDIA_TYPE_IMAGE;
        btnStartRecord = findViewById(R.id.btn_start_recording);
        startRecordLinten();

    }

    // camera preview
    private Camera getCamera(int cameraId) {
        Camera camera;
        try {
            camera = Camera.open(cameraId);
        } catch (Exception e) {
            camera = null;
        }
        return camera;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera = getCamera(0);
            mCamera.setPreviewDisplay(holder);
            mCamera.setDisplayOrientation(90);
            mCamera.startPreview();
        } catch (Exception e) {
            finish();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mCamera != null) {
            try {
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /** Create a file Uri for saving an image or video */
    public static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    public static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCamera");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                return null;
            }
        }

        // Create a media file name
        // String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_Camera.jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_Camera.mp4");
        } else {
            return null;
        }
        Log.d(TAG, "file path is " + mediaFile.getPath());

        return mediaFile;
    }
    public void startRecordLinten() {
        btnStartRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mRecordType) {
                    case MEDIA_TYPE_IMAGE:
                        if (mCamera != null) {
                            try {
                                // set parameters
                                Camera.Parameters params = mCamera.getParameters();
                                params.setPictureFormat(ImageFormat.JPEG);
                                params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                                mCamera.setParameters(params);
                                // make photo
                                mCamera.takePicture(null, null, mPictureCallback);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e(TAG, e.toString());
                            }
                        }
                        break;
                    case MEDIA_TYPE_VIDEO:
                        break;
                }
            }
        });
    }

    private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (pictureFile != null) {
                try {
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    fos.write(data);
                    fos.close();
                    Toast.makeText(CameraActivity.this, "图片已保存", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(CameraActivity.this, "图片保存错误", Toast.LENGTH_SHORT).show();
                }
            }
            // preview again
            camera.startPreview();
        }
    };

}
