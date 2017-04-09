package com.tattood.tattod;

import android.content.Context;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

@SuppressWarnings("deprecation")
public class CameraFragment extends Fragment {
    private Camera mCamera;
    private CameraPreview mPreview;
    private View view;

    public CameraFragment() {
        // Required empty public constructor
    }

    public static CameraFragment newInstance() {
        return new CameraFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
        destroyCamera();
    }

    @Override
    public void onResume() {
        super.onResume();
        initCamera();
    }

    public void initCamera() {
        mCamera = getCameraInstance();
        mCamera.setPreviewCallback(null);
        mPreview = new CameraPreview(getContext(), mCamera);
        FrameLayout preview = (FrameLayout) view.findViewById(R.id.camera_preview);
        preview.addView(mPreview);
        mCamera.startPreview();
    }

    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(0); // attempt to get a Camera instance
            if (c == null)
                throw new Exception("Camera not found");
            c.setDisplayOrientation(90);
        }
        catch (Exception e){
            Log.d("EXCEPTION", "Camera Exception");
        }
        return c; // returns null if camera is unavailable
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_camera, container, false);
        initCamera();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        destroyCamera();
    }

    private void destroyCamera() {
        if(mCamera!=null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mPreview.getHolder().removeCallback(mPreview);
            mCamera.release();
            mCamera = null;
        }
    }
}
