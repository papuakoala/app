package com.papuakoala.shapeapp;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class ShapeRenderer implements GLSurfaceView.Renderer {

    private float rotX = 20f, rotY = 30f;
    private int shapeType = 0; // 0=cube, 1=cylinder, 2=cone, 3=pyramid
    private float[] projMatrix = new float[16];
    private float[] viewMatrix = new float[16];
    private float[] mvpMatrix = new float[16];
    private float[] modelMatrix = new float[16];
    private int program;

    private static final String VERTEX_SHADER =
        "uniform mat4 uMVPMatrix;" +
        "attribute vec4 vPosition;" +
        "attribute vec4 vColor;" +
        "varying vec4 fColor;" +
        "void main() {" +
        "  gl_Position = uMVPMatrix * vPosition;" +
        "  fColor = vColor;" +
        "}";

    private static final String FRAGMENT_SHADER =
        "precision mediump float;" +
        "varying vec4 fColor;" +
        "void main() {" +
        "  gl_FragColor = fColor;" +
        "}";

    public void setShape(int type) { this.shapeType = type; }
    public void setRotation(float dx, float dy) { rotX += dy; rotY += dx; }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(1f, 0.97f, 0.94f, 1f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        int vs = loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER);
        int fs = loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER);
        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vs);
        GLES20.glAttachShader(program, fs);
        GLES20.glLinkProgram(program);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int w, int h) {
        GLES20.glViewport(0, 0, w, h);
        float ratio = (float) w / h;
        Matrix.frustumM(projMatrix, 0, -ratio, ratio, -1, 1, 2, 10);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        Matrix.setLookAtM(viewMatrix, 0, 0, 0, 5, 0, 0, 0, 0, 1, 0);
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.rotateM(modelMatrix, 0, rotX, 1, 0, 0);
        Matrix.rotateM(modelMatrix, 0, rotY, 0, 1, 0);
        float[] temp = new float[16];
        Matrix.multiplyMM(temp, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(mvpMatrix, 0, projMatrix, 0, temp, 0);
        GLES20.glUseProgram(program);
        switch (shapeType) {
            case 0: drawCube(); break;
            case 1: drawCylinder(); break;
            case 2: drawCone(); break;
            case 3: drawPyramid(); break;
        }
    }

    private void drawCube() {
        float[] v = {
            -0.6f,-0.6f, 0.6f,  0.6f,-0.6f, 0.6f,  0.6f, 0.6f, 0.6f, -0.6f, 0.6f, 0.6f,
            -0.6f,-0.6f,-0.6f,  0.6f,-0.6f,-0.6f,  0.6f, 0.6f,-0.6f, -0.6f, 0.6f,-0.6f
        };
        float[][] faceColors = {
            {1f,0.42f,0.21f,1f}, {0.3f,0.79f,0.63f,1f}, {0.3f,0.79f,0.63f,1f},
            {1f,0.84f,0.04f,1f}, {0.4f,0.6f,1f,1f}, {1f,0.42f,0.21f,1f}
        };
        int[][] faces = {{0,1,2,3},{4,5,6,7},{0,4,7,3},{1,5,6,2},{3,7,6,2},{0,4,5,1}};
        for (int f = 0; f < 6; f++) {
            float[] fv = new float[12];
            for (int i = 0; i < 4; i++) {
                fv[i*3]=v[faces[f][i]*3]; fv[i*3+1]=v[faces[f][i]*3+1]; fv[i*3+2]=v[faces[f][i]*3+2];
            }
            float[] fc = new float[16];
            for (int i = 0; i < 4; i++) {
                fc[i*4]=faceColors[f][0]; fc[i*4+1]=faceColors[f][1]; fc[i*4+2]=faceColors[f][2]; fc[i*4+3]=1f;
            }
            drawQuad(fv, fc);
        }
    }

    private void drawCylinder() {
        int seg = 20;
        float r = 0.5f, h = 0.7f;
        for (int i = 0; i < seg; i++) {
            float a1 = (float)(2*Math.PI*i/seg), a2 = (float)(2*Math.PI*(i+1)/seg);
            float[] fv = {
                r*(float)Math.cos(a1), h, r*(float)Math.sin(a1),
                r*(float)Math.cos(a2), h, r*(float)Math.sin(a2),
                r*(float)Math.cos(a2),-h, r*(float)Math.sin(a2),
                r*(float)Math.cos(a1),-h, r*(float)Math.sin(a1)
            };
            float[] fc = {0.3f,0.79f,0.63f,1f, 0.3f,0.79f,0.63f,1f, 0.4f,0.6f,1f,1f, 0.4f,0.6f,1f,1f};
            drawQuad(fv, fc);
        }
    }

    private void drawCone() {
        int seg = 20;
        float r = 0.6f, h = 0.8f;
        for (int i = 0; i < seg; i++) {
            float a1 = (float)(2*Math.PI*i/seg), a2 = (float)(2*Math.PI*(i+1)/seg);
            float[] tv = {0f, h, 0f, r*(float)Math.cos(a1),-h,r*(float)Math.sin(a1), r*(float)Math.cos(a2),-h,r*(float)Math.sin(a2)};
            float[] tc = {1f,0.42f,0.21f,1f, 1f,0.84f,0.04f,1f, 1f,0.84f,0.04f,1f};
            drawTriangle(tv, tc);
        }
    }

    private void drawPyramid() {
        float[][] base = {{-0.6f,-0.6f,0.6f},{0.6f,-0.6f,0.6f},{0.6f,-0.6f,-0.6f},{-0.6f,-0.6f,-0.6f}};
        float[] apex = {0f, 0.8f, 0f};
        float[][] sideColors = {{1f,0.42f,0.21f,1f},{0.3f,0.79f,0.63f,1f},{1f,0.84f,0.04f,1f},{0.4f,0.6f,1f,1f}};
        for (int i = 0; i < 4; i++) {
            int j = (i+1)%4;
            float[] tv = {apex[0],apex[1],apex[2], base[i][0],base[i][1],base[i][2], base[j][0],base[j][1],base[j][2]};
            float[] tc = {sideColors[i][0],sideColors[i][1],sideColors[i][2],1f, sideColors[i][0],sideColors[i][1],sideColors[i][2],1f, sideColors[i][0],sideColors[i][1],sideColors[i][2],1f};
            drawTriangle(tv, tc);
        }
    }

    private void drawQuad(float[] verts, float[] colors) {
        FloatBuffer vb = makeBuffer(verts), cb = makeBuffer(colors);
        int vPos = GLES20.glGetAttribLocation(program, "vPosition");
        int cPos = GLES20.glGetAttribLocation(program, "vColor");
        int mvp = GLES20.glGetUniformLocation(program, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(mvp, 1, false, mvpMatrix, 0);
        GLES20.glEnableVertexAttribArray(vPos);
        GLES20.glEnableVertexAttribArray(cPos);
        GLES20.glVertexAttribPointer(vPos, 3, GLES20.GL_FLOAT, false, 0, vb);
        GLES20.glVertexAttribPointer(cPos, 4, GLES20.GL_FLOAT, false, 0, cb);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 4);
        GLES20.glDisableVertexAttribArray(vPos);
        GLES20.glDisableVertexAttribArray(cPos);
    }

    private void drawTriangle(float[] verts, float[] colors) {
        FloatBuffer vb = makeBuffer(verts), cb = makeBuffer(colors);
        int vPos = GLES20.glGetAttribLocation(program, "vPosition");
        int cPos = GLES20.glGetAttribLocation(program, "vColor");
        int mvp = GLES20.glGetUniformLocation(program, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(mvp, 1, false, mvpMatrix, 0);
        GLES20.glEnableVertexAttribArray(vPos);
        GLES20.glEnableVertexAttribArray(cPos);
        GLES20.glVertexAttribPointer(vPos, 3, GLES20.GL_FLOAT, false, 0, vb);
        GLES20.glVertexAttribPointer(cPos, 4, GLES20.GL_FLOAT, false, 0, cb);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
        GLES20.glDisableVertexAttribArray(vPos);
        GLES20.glDisableVertexAttribArray(cPos);
    }

    private FloatBuffer makeBuffer(float[] arr) {
        ByteBuffer bb = ByteBuffer.allocateDirect(arr.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(arr); fb.position(0);
        return fb;
    }

    private int loadShader(int type, String src) {
        int s = GLES20.glCreateShader(type);
        GLES20.glShaderSource(s, src);
        GLES20.glCompileShader(s);
        return s;
    }
}
