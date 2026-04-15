package com.papuakoala.shapeapp;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ExploreActivity extends Activity {

    private GLSurfaceView glView;
    private ShapeRenderer renderer;
    private float lastX, lastY;
    private int currentShape = 0;

    private static final String[] SHAPE_NAMES = {"정육면체", "원기둥", "원뿔", "사각뿔"};
    private static final String[] SHAPE_INFO = {
        "면: 6개  |  모서리: 12개  |  꼭짓점: 8개",
        "면: 3개  |  모서리: 2개  |  꼭짓점: 0개",
        "면: 2개  |  모서리: 1개  |  꼭짓점: 1개",
        "면: 5개  |  모서리: 8개  |  꼭짓점: 5개"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(0xFFFFF8F0);

        TextView title = new TextView(this);
        title.setText("🔵 도형 탐색");
        title.setTextSize(24);
        title.setTextColor(0xFFFF6B35);
        title.setPadding(40, 30, 40, 10);
        root.addView(title);

        LinearLayout btnRow = new LinearLayout(this);
        btnRow.setOrientation(LinearLayout.HORIZONTAL);
        btnRow.setPadding(20, 0, 20, 0);
        String[] labels = {"정육면체", "원기둥", "원뿔", "사각뿔"};
        int[] colors = {0xFFFF6B35, 0xFF4CC9F0, 0xFF06D6A0, 0xFFFFD166};
        for (int i = 0; i < 4; i++) {
            Button b = new Button(this);
            b.setText(labels[i]);
            b.setTextSize(13);
            b.setBackgroundColor(colors[i]);
            b.setTextColor(0xFFFFFFFF);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, 90, 1f);
            lp.setMargins(6, 0, 6, 0);
            b.setLayoutParams(lp);
            final int idx = i;
            b.setOnClickListener(v -> {
                currentShape = idx;
                renderer.setShape(idx);
                title.setText(SHAPE_NAMES[idx]);
            });
            btnRow.addView(b);
        }
        root.addView(btnRow);

        glView = new GLSurfaceView(this);
        glView.setEGLContextClientVersion(2);
        renderer = new ShapeRenderer();
        glView.setRenderer(renderer);
        glView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        LinearLayout.LayoutParams glParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f);
        glView.setLayoutParams(glParams);
        glView.setOnTouchListener((v, e) -> {
            if (e.getAction() == MotionEvent.ACTION_DOWN) {
                lastX = e.getX(); lastY = e.getY();
            } else if (e.getAction() == MotionEvent.ACTION_MOVE) {
                float dx = e.getX() - lastX, dy = e.getY() - lastY;
                renderer.setRotation(dx * 0.5f, dy * 0.5f);
                lastX = e.getX(); lastY = e.getY();
            }
            return true;
        });
        root.addView(glView);

        TextView info = new TextView(this);
        info.setText(SHAPE_INFO[0]);
        info.setTextSize(18);
        info.setTextColor(0xFF333333);
        info.setPadding(40, 20, 40, 30);
        info.setGravity(android.view.Gravity.CENTER);
        root.addView(info);

        setContentView(root);
    }

    @Override
    protected void onPause() { super.onPause(); glView.onPause(); }
    @Override
    protected void onResume() { super.onResume(); glView.onResume(); }
}
