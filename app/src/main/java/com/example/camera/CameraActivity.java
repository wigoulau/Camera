package com.example.camera;

import android.hardware.Camera;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private final String TAG = "CameraActivity";

    private SurfaceView mCameraSurface;
    private SurfaceHolder mHolder;


    private Camera mCamera = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        mCameraSurface = findViewById(R.id.camera_preview);
        mHolder = mCameraSurface.getHolder();
        mHolder.addCallback(CameraActivity.this);
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

}
