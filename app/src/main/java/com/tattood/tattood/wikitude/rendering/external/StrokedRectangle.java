package com.tattood.tattood.wikitude.rendering.external;

import android.opengl.GLES20;
import android.util.Log;

import com.wikitude.tracker.Target;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class StrokedRectangle {

    public enum Type {
        FACE, STANDARD, EXTENDED, TRACKING_3D
    }

    private final static String TAG = "StrokedRectangle";

    String mFragmentShaderCode =
            "precision mediump float;" +
            "void main()" +
            "{" +
            " gl_FragColor = vec4(1.0, 0.58, 0.16, 1.0);" +
            "}";

    String mVertexShaderCode =
            "attribute vec4 v_position;" +
            "uniform mat4 Projection;" +
            "uniform mat4 ModelView;" +
            "void main()" +
            "{" +
            "gl_Position = Projection * ModelView * v_position;" +
            "}";

    private int mAugmentationProgram = -1;
    private int mPositionSlot;
    private int mProjectionUniform;
    private int mModelViewUniform;

    static float sRectVerts[] = {
            -0.5f, -0.5f, 0.0f,
            -0.5f,  0.5f, 0.0f,
            0.5f,  0.5f, 0.0f,
            0.5f, -0.5f, 0.0f };

    static float sRectVerts3D[] = {
            -1, -1, 0.0f,
            -1,  1, 0.0f,
            1,  1, 0.0f,
            1, -1, 0.0f };

    static float sRectVertsExtended[] = {
            -0.7f, -0.7f, 0.0f,
            -0.7f,  0.7f, 0.0f,
            0.7f,  0.7f, 0.0f,
            0.7f, -0.7f, 0.0f };

    static float sRectVertsFace[] = {
            -0.5f, -0.5f, 0.0f,
            -0.5f,  0.5f, 0.0f,
            0.5f,  0.5f, 0.0f,
            0.5f, -0.5f, 0.0f };


    private final ShortBuffer mIndicesBuffer;
    private final FloatBuffer mRectBuffer;

    private final short mIndices[] = { 0, 1, 2, 3 };

    public StrokedRectangle() {
        this(Type.STANDARD);
    }

    public StrokedRectangle(Type type) {
        ByteBuffer dlb = ByteBuffer.allocateDirect(mIndices.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        mIndicesBuffer = dlb.asShortBuffer();
        mIndicesBuffer.put(mIndices);
        mIndicesBuffer.position(0);

        ByteBuffer bb = ByteBuffer.allocateDirect(sRectVerts.length * 4);
        bb.order(ByteOrder.nativeOrder());
        mRectBuffer = bb.asFloatBuffer();
        if (type == Type.EXTENDED) {
            mRectBuffer.put(sRectVertsExtended);
        } else if (type == Type.FACE || type == Type.TRACKING_3D) {
            mRectBuffer.put(sRectVertsFace);
        } else {
            mRectBuffer.put(sRectVerts);
        }
        mRectBuffer.position(0);
    }

    public void onDrawFrame(final float[] projectionMatrix, final float[] viewMatrix) {
        if (mAugmentationProgram == -1) {
            compileShaders();
            mPositionSlot = GLES20.glGetAttribLocation(mAugmentationProgram, "v_position");
            mModelViewUniform = GLES20.glGetUniformLocation(mAugmentationProgram, "ModelView");
            mProjectionUniform = GLES20.glGetUniformLocation(mAugmentationProgram, "Projection");
            GLES20.glDisable(GLES20.GL_DEPTH_TEST);
            GLES20.glLineWidth(10.0f);
        }

        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        GLES20.glUseProgram(mAugmentationProgram);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);

        GLES20.glVertexAttribPointer(mPositionSlot, 3, GLES20.GL_FLOAT, false, 0, mRectBuffer);
        GLES20.glEnableVertexAttribArray(mPositionSlot);

        GLES20.glUniformMatrix4fv(mProjectionUniform, 1, false, projectionMatrix, 0);
        GLES20.glUniformMatrix4fv(mModelViewUniform, 1, false, viewMatrix, 0);

        GLES20.glDrawElements(GLES20.GL_LINE_LOOP, mIndices.length, GLES20.GL_UNSIGNED_SHORT, mIndicesBuffer);
    }

    public void onDrawFrame(final Target currentlyRecognizedTarget) {
        this.onDrawFrame(currentlyRecognizedTarget.getProjectionMatrix(), currentlyRecognizedTarget.getViewMatrix());
    }

    private void compileShaders() {
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, mVertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, mFragmentShaderCode);
        mAugmentationProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mAugmentationProgram, vertexShader);
        GLES20.glAttachShader(mAugmentationProgram, fragmentShader);
        GLES20.glLinkProgram(mAugmentationProgram);
    }

    public static int loadShader(int type, String shaderCode){
        int shader = GLES20.glCreateShader(type);

        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }


    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }

}
