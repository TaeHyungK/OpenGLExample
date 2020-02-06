package com.taehyungk.openglexample.renederer;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class SquareRenderer implements GLSurfaceView.Renderer {
    private static final String LOGD = "SquareRenderer";

    public float mAngle;

    private int mProgram;
    private int maPositionHandle;

    private FloatBuffer vbuf;
    private int muMVPMatrixHandle;
    private float[] mMVPMatrix = new float[16];
    private float[] mMMatrix = new float[16];
    private float[] mVMatrix = new float[16];
    private float[] mProjMatrix = new float[16];

    private final String vertexShaderCode = "uniform mat4 uMVPMatrix; \n"
            + "attribute vec4 aPosition; \n"
            + "void main() { \n"
            + "gl_Position = uMVPMatrix * aPosition; \n"
            + "} \n";

    private final String fragmentShaderCode = "precision mediump float; \n"
            + "void main() { \n"
            + "gl_FragColor = vec4 (0, 0.5, 0, 1.0); \n"
            + "} \n";

    private void initShapes(){
        float squareCoords[] = {
                // X, Y, Z
                -0.5f, 0.5f, 0f,
                -0.5f, -0.5f, 0f,
                0.5f, -0.5f, 0f,

                -0.5f, 0.5f, 0f,
                0.5f, -0.5f, 0f,
                0.5f, 0.5f, 0f

        };
        // initialize vertex Buffer for triangle
        ByteBuffer vbb = ByteBuffer.allocateDirect(squareCoords.length * 4);
        vbb.order(ByteOrder.nativeOrder());	// use the device hardware's native byte order
        vbuf = vbb.asFloatBuffer();	// create a floating point buffer from the ByteBuffer
        vbuf.put(squareCoords);		// add the coordinates to the FloatBuffer
        vbuf.position(0);		// set the buffer to read the first coordinate

    }

    private int loadShader(int type, String shaderCode){
        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);
        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    /**
     * GLSurfaceView가 생성될 때 시스템에 의해 한번 호출되는 메소드
     *
     * @param gl10
     * @param eglConfig
     */
    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES20.glClearColor(0.0f,0.0f,0.0f,1.0f);
        initShapes();
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
        mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);                  // creates OpenGL program executables
        // get handle to the vertex shader's vPosition member
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
    }

    /**
     * GLSurfaceView의 크기, 장치의 orientation 등의 geomtery가 변경되면 호출되는 메소드
     *
     * @param gl10
     * @param width
     * @param height
     */
    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        //this projection matrix is applied to object coodinates
        //in the onDrawFrame() method
        Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, 3, 7); // docs
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        Matrix.setLookAtM(mVMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
    }

    /**
     * GLSurfaceView가 draw 될 때마다 호출되는 메소드
     *
     * @param gl10
     */
    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Add program to OpenGL environment
        GLES20.glUseProgram(mProgram);
        // Prepare the square data
        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false, 12, vbuf);
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        Matrix.setIdentityM(mMMatrix, 0);
        Matrix.setRotateM(mMMatrix, 0, mAngle, 0, 0, 1.0f);
        Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, mMMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);
        // Apply a ModelView Projection transformation
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        // Draw the square(2개의 삼각형이므로 정점은 6개)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
    }
}
