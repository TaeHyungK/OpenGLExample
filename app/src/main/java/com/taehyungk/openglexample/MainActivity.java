package com.taehyungk.openglexample;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.taehyungk.openglexample.renederer.SquareRenderer;

public class MainActivity extends AppCompatActivity {
    private static final String LOGD = "MainActivity";

    // 렌더링 결과를 그릴 OpenGL view 선언
    private GLSurfaceView mGLView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // GLSurfaceView를 초기화
        mGLView = new GLSurfaceView(this);

        // OpenGL을 지원하는지 체크
        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x00020000;

        if (supportsEs2) {
            // OpenGL ES 2.0 호환 컨텍스트 요청
            mGLView.setEGLContextClientVersion(2);
            //
            mGLView.setRenderer(new SquareRenderer());
        } else {
            return;
        }

        setContentView(mGLView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLView.onPause();
    }
}
