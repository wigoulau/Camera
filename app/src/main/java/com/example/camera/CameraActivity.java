package com.example.camera;

import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Message;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Timer;
import java.util.TimerTask;

public class CameraActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private static final String TAG = "CameraActivity";

    private SurfaceView mCameraSurface;
    private SurfaceHolder mHolder;
    private Button btnStartRecord;
    private Button btnChangeModule;
    private TextView mRecordVideoTime;
    private ImageView mRecordActBack;
    private ImageView mRecordVideoTip;

    private static final int MEDIA_TYPE_IMAGE = 1;
    private static final int MEDIA_TYPE_VIDEO = 2;
    private int mRecordType;

    private boolean mIsRecording;
    private int mStartTime = 0;
    private Timer mTimer;
    private Handler mHandler;

    private Camera mCamera = null;
    private MediaRecorder mMediaRecorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        mCameraSurface = findViewById(R.id.camera_preview);
        mHolder = mCameraSurface.getHolder();
        mHolder.addCallback(CameraActivity.this);

        mRecordType = MEDIA_TYPE_IMAGE;
        mIsRecording = false;
        btnStartRecord = findViewById(R.id.btn_start_recording);
        startRecordLinten();
        btnChangeModule = findViewById(R.id.btn_change_module);
        changeModuleListen();
        mRecordVideoTime = findViewById(R.id.record_video_time);
        mRecordActBack = findViewById(R.id.record_act_back);
        mRecordVideoTip = findViewById(R.id.record_video_tip);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                int minute1 = mStartTime / 60;
                int second1 = mStartTime % 60;
                String minuteStr1 = null;
                String secondStr1 = null;
                if (minute1 < 10) {
                    minuteStr1 = "0" + minute1;
                } else {
                    minuteStr1 = String.valueOf(minuteStr1);
                }
                if (second1 < 10) {
                    secondStr1 = "0" + second1;
                } else {
                    secondStr1 = String.valueOf(second1);
                }
                mRecordVideoTime.setText(minuteStr1 + ":" + secondStr1);

                if (mRecordVideoTip.getVisibility() == View.VISIBLE) {
                    mRecordVideoTip.setVisibility(View.GONE);
                } else {
                    mRecordVideoTip.setVisibility(View.VISIBLE);
                }
            }
        };
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

        setCaptureModule(MEDIA_TYPE_VIDEO);
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
                                // make photo
                                mCamera.takePicture(null, null, mPictureCallback);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e(TAG, e.toString());
                            }
                        }
                        break;
                    case MEDIA_TYPE_VIDEO:
                        if (mIsRecording) {
                            stopRecord();
                        } else {
                            startRecord();
                        }
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

    public void changeModuleListen() {
        btnChangeModule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mIsRecording)
                    return;
                switch (mRecordType) {
                    case MEDIA_TYPE_IMAGE:
                        setCaptureModule(MEDIA_TYPE_VIDEO);
                        break;
                    case MEDIA_TYPE_VIDEO:
                        setCaptureModule(MEDIA_TYPE_IMAGE);
                        break;
                }
            }
        });
    }

    public void setCaptureModule(int type) {
        switch (type) {
            case MEDIA_TYPE_VIDEO:
                mRecordType = MEDIA_TYPE_VIDEO;
                btnStartRecord.setBackgroundResource(R.drawable.recording_act_vedio_start);
                btnChangeModule.setBackgroundResource(R.drawable.change_module_video);
                Toast.makeText(CameraActivity.this, "录像模式", Toast.LENGTH_SHORT).show();
                mRecordVideoTip.setVisibility(View.VISIBLE);
                mRecordVideoTime.setVisibility(View.VISIBLE);
                break;
            case MEDIA_TYPE_IMAGE:
                mRecordType = MEDIA_TYPE_IMAGE;
                btnStartRecord.setBackgroundResource(R.drawable.recording_act_photo_start);
                btnChangeModule.setBackgroundResource(R.drawable.change_module_photo);
                Toast.makeText(CameraActivity.this, "拍照模式", Toast.LENGTH_SHORT).show();
                mRecordVideoTip.setVisibility(View.GONE);
                mRecordVideoTime.setVisibility(View.GONE);
                break;
        }
        try {
            if (mCamera != null) {
                // set parameters
                Camera.Parameters params = mCamera.getParameters();
                params.setPictureFormat(ImageFormat.JPEG);
                if (mRecordType == MEDIA_TYPE_VIDEO)
                    params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                else
                    params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                mCamera.setParameters(params);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean prepareVideoRecorder(){
        mMediaRecorder = new MediaRecorder();
        mCamera.unlock();
        try {
            mMediaRecorder.setCamera(mCamera);
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
            mMediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());
            mMediaRecorder.setPreviewDisplay(mHolder.getSurface());
            mMediaRecorder.prepare();
        } catch (Exception e) {
            mMediaRecorder.release();
            mCamera.lock();
            return false;
        }
        return true;
    }

    private void startRecord() {
        if (prepareVideoRecorder()) {
            mMediaRecorder.start();

            mIsRecording = true;
            Toast.makeText(CameraActivity.this, "开始录像", Toast.LENGTH_SHORT).show();
            btnStartRecord.setBackgroundResource(R.drawable.recording_act_vedio_stop);
            mStartTime = 0;
            mTimer = new Timer();
            mTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    mStartTime++;
                    mHandler.sendEmptyMessage(0);
                }
            }, 0, 1000);
        } else {
            mMediaRecorder.release();
            mCamera.lock();
        }
    }

    private void stopRecord() {
        mMediaRecorder.stop();
        mMediaRecorder.reset();
        mMediaRecorder.release();
        mCamera.lock();

        mIsRecording = false;
        btnStartRecord.setBackgroundResource(R.drawable.recording_act_vedio_start);
        mTimer.cancel();

        Toast.makeText(CameraActivity.this, "录像已保存", Toast.LENGTH_SHORT).show();
    }


}
