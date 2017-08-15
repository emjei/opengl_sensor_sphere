package com.emjei.opengl_sensor_sphere;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

    private SceneSurfaceView mSceneSurfaceView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSceneSurfaceView = new SceneSurfaceView(this);
        mSceneSurfaceView.setKeepScreenOn(true);

        setContentView(mSceneSurfaceView);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mSceneSurfaceView.onPause();
        mSceneSurfaceView.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mSceneSurfaceView.onResume();
        mSceneSurfaceView.start();
    }
}
