package com.tattood.tattod;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

@SuppressWarnings("deprecation")
public class CameraFragment extends Fragment {
    private Camera mCamera;
    private CameraPreview mPreview;

    public CameraFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CameraFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CameraFragment newInstance(String param1, String param2) {
        CameraFragment fragment = new CameraFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //[BUG] According to Android documentation onPause should release the camera and onResume should
    // acquire it back. If I comment out the lines below, camera appears pitch black.
    @Override
    public void onPause() {
        super.onPause();
//        destroyCamera();
    }

    @Override
    public void onResume() {
        super.onResume();
//        mCamera = getCameraInstance();
//        mCamera.startPreview();|
    }

    /** A safe way to get an instance of the Camera object. */
    // Might be a virtual device related bug, web-cam bug, Nexus bug.
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
        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        mCamera = getCameraInstance();
        mPreview = new CameraPreview(getContext(), mCamera);
        FrameLayout preview = (FrameLayout) view.findViewById(R.id.camera_preview);
        preview.addView(mPreview);
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
            mCamera.release();
            mCamera = null;
        }
    }
}
