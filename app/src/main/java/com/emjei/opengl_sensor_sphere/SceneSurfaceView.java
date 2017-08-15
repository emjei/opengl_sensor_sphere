package com.emjei.opengl_sensor_sphere;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class SceneSurfaceView extends GLSurfaceView {

    private final SceneRenderer mRenderer;

    public SceneSurfaceView(Context context) {
        super(context);

        setEGLContextClientVersion(2);

        mRenderer = new SceneRenderer(getContext());
        setRenderer(mRenderer);

//        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public void start() {
        mRenderer.start();
    }

    public void stop() {
        mRenderer.stop();
    }
}
