package com.example.camera;

import android.hardware.Camera;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.TextureView;
import android.widget.FrameLayout;

public class CameraPreviewActivity extends AppCompatActivity{

    Camera mCamera;
    CameraPreview mPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_preview);

        mCamera = getCamera(0);
        mPreview = new CameraPreview(this, mCamera);
        mPreview.setSurfaceTextureListener(mPreview);
        //mPreview.setAlpha(0.5f);
        FrameLayout preview = (FrameLayout)findViewById(R.id.fl_camera_preview);
        preview.addView(mPreview);

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
}
