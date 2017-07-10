/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.emjei.opengles20;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

/**
 * Provides drawing instructions for a GLSurfaceView object. This class
 * must override the OpenGL ES drawing lifecycle methods:
 * <ul>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onSurfaceCreated}</li>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onDrawFrame}</li>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onSurfaceChanged}</li>
 * </ul>
 */
public class MyGLRenderer implements GLSurfaceView.Renderer, SensorEventListener {

    private static final String TAG = "MyGLRenderer";

    private Context mContext;
    private SensorManager mSensorManager;
    private Sensor mGameRotationVectorSensor;
    private Triangle mTriangle;
    private Square   mSquare;

    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float[] mRotationMatrix = new float[16];

    private float mAngle;

    public MyGLRenderer(Context context) {
        mContext = context;

        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        mGameRotationVectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {

        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        mTriangle = new Triangle();
        mSquare   = new Square();
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        float[] scratch = new float[16];

        // Draw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Set the camera position (View matrix)
        float result[] = new float[4];
        float vector[] = { 0f, 0f, 3f, 1f };
        Matrix.multiplyMV(result, 0, mRotationMatrix, 0, vector, 0);
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 0, result[0], result[1], result[2], 0f, 1.0f, 0.0f);
//        for (int i = 0; i < mViewMatrix.length; i++) {
//            Log.v("TAag view", "" + i + ": " + mViewMatrix[i]);
//        }

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
//        for (int i = 0; i < mProjectionMatrix.length; i++) {
//            Log.v("TAag", "" + i + ": " + mProjectionMatrix[i]);
//        }
//
//        for (int i = 0; i < mMVPMatrix.length; i++) {
//            Log.v("TAag mvp", "" + i + ": " + mMVPMatrix[i]);
//        }

//        float[] mVR = new float[4];
//        float mV[] = { 0.5f, -0.311004243f, 0.0f, 1.f };
//        Matrix.multiplyMV(mVR, 0, mMVPMatrix, 0, mV, 0);
//        for (int i = 0; i < mVR.length; ++i) {
//            Log.v("TAag vector", "" + i + ": " + mVR[i]);
//        }

        // Draw square
//        mSquare.draw(mMVPMatrix);

        // Create a rotation for the triangle

        // Use the following code to generate constant rotation.
        // Leave this code out when using TouchEvents.
        // long time = SystemClock.uptimeMillis() % 4000L;
        // float angle = 0.090f * ((int) time);

//        Matrix.setRotateM(mRotationMatrix, 0, mAngle, 0, 0, 1.0f);

        // Combine the rotation matrix with the projection and camera view
        // Note that the mMVPMatrix factor *must be first* in order
        // for the matrix multiplication product to be correct.
//        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);

        // Draw triangle
//        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
        mTriangle.draw(mMVPMatrix);
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        // Adjust the viewport based on geometry changes,
        // such as screen rotation
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Log.v("Taaag", "ratio: " + ratio);
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
//        Log.v(TAG, "onSensorChanged");

        if (sensorEvent.sensor.getType() == Sensor.TYPE_GAME_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(mRotationMatrix , sensorEvent.values);
        }
    }

    void start() {
        mSensorManager.registerListener(this, mGameRotationVectorSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    void stop() {
        mSensorManager.unregisterListener(this);
    }

    /**
     * Utility method for compiling a OpenGL shader.
     *
     * <p><strong>Note:</strong> When developing shaders, use the checkGlError()
     * method to debug shader coding errors.</p>
     *
     * @param type - Vertex or fragment shader type.
     * @param shaderCode - String containing the shader code.
     * @return - Returns an id for the shader.
     */
    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    /**
    * Utility method for debugging OpenGL calls. Provide the name of the call
    * just after making it:
    *
    * <pre>
    * mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
    * MyGLRenderer.checkGlError("glGetUniformLocation");</pre>
    *
    * If the operation is not successful, the check throws an error.
    *
    * @param glOperation - Name of the OpenGL call to check.
    */
    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }

    /**
     * Returns the rotation angle of the triangle shape (mTriangle).
     *
     * @return - A float representing the rotation angle.
     */
    public float getAngle() {
        return mAngle;
    }

    /**
     * Sets the rotation angle of the triangle shape (mTriangle).
     */
    public void setAngle(float angle) {
        mAngle = angle;
    }

}