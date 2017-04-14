package com.tattood.tattood.wikitude.rendering.external;

import android.opengl.GLSurfaceView;

import com.wikitude.common.rendering.RenderExtension;
import com.wikitude.tracker.Target;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLRenderer implements GLSurfaceView.Renderer {

    private RenderExtension mWikitudeRenderExtension = null;
    protected Target mCurrentlyRecognizedTarget = null;
    private StrokedRectangle mStrokedRectangle = null;
    private StrokedRectangle.Type mStrokedRectangleType = StrokedRectangle.Type.STANDARD;

    public GLRenderer(RenderExtension wikitudeRenderExtension) {
        mWikitudeRenderExtension = wikitudeRenderExtension;
        /*
         * Until Wikitude SDK version 2.1 onDrawFrame triggered also a logic update inside the SDK core.
         * This behaviour is deprecated and onUpdate should be used from now on to update logic inside the SDK core. <br>
         *
         * The default behaviour is that onDrawFrame also updates logic. <br>
         *
         * To use the new separated drawing and logic update methods, RenderExtension.useSeparatedRenderAndLogicUpdates should to be called.
         * Otherwise the logic will still be updated in onDrawFrame.
         */
        mWikitudeRenderExtension.useSeparatedRenderAndLogicUpdates();
    }

    @Override
    public void onDrawFrame(final GL10 unused) {
        if (mWikitudeRenderExtension != null) {
            // Will trigger a logic update in the SDK
            mWikitudeRenderExtension.onUpdate();
            // will trigger drawing of the camera frame
            mWikitudeRenderExtension.onDrawFrame(unused);
        }
        if (mCurrentlyRecognizedTarget != null) {
            mStrokedRectangle.onDrawFrame(mCurrentlyRecognizedTarget);
        }
    }

    @Override
    public void onSurfaceCreated(final GL10 unused, final EGLConfig config) {
        if (mWikitudeRenderExtension != null) {
            mWikitudeRenderExtension.onSurfaceCreated(unused, config);
        }
        mStrokedRectangle = new StrokedRectangle(mStrokedRectangleType);
    }

    @Override
    public void onSurfaceChanged(final GL10 unused, final int width, final int height) {
        if (mWikitudeRenderExtension != null) {
            mWikitudeRenderExtension.onSurfaceChanged(unused, width, height);
        }
    }

    public void onResume() {
        if (mWikitudeRenderExtension != null) {
            mWikitudeRenderExtension.onResume();
        }
    }

    public void onPause() {
        if (mWikitudeRenderExtension != null) {
            mWikitudeRenderExtension.onPause();
        }
    }

    public void setCurrentlyRecognizedTarget(final Target currentlyRecognizedTarget) {
        mCurrentlyRecognizedTarget = currentlyRecognizedTarget;
    }

    public void setStrokedRectangleType(StrokedRectangle.Type strokedRectangleType) {
        mStrokedRectangleType = strokedRectangleType;
    }
}
