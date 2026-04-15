package com.papuakoala.shapeapp;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NetActivity extends Activity {

    private NetView netView;
    private int currentShape = 0;
    private static final String[] NAMES = {"정육면체", "원기둥", "원뿔", "사각뿔"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(0xFFFFF8F0);

        TextView title = new TextView(this);
        title.setText("📐 전개도 - 정육면체");
        title.setTextSize(22);
        title.setTextColor(0xFF4CC9F0);
        title.setPadding(40, 30, 40, 10);
        root.addView(title);

        LinearLayout btnRow = new LinearLayout(this);
        btnRow.setOrientation(LinearLayout.HORIZONTAL);
        btnRow.setPadding(20, 0, 20, 10);
        int[] colors = {0xFFFF6B35, 0xFF4CC9F0, 0xFF06D6A0, 0xFFFFD166};
        for (int i = 0; i < 4; i++) {
            Button b = new Button(this);
            b.setText(NAMES[i]);
            b.setTextSize(13);
            b.setBackgroundColor(colors[i]);
            b.setTextColor(0xFFFFFFFF);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, 90, 1f);
            lp.setMargins(6, 0, 6, 0);
            b.setLayoutParams(lp);
            final int idx = i;
            b.setOnClickListener(v -> {
                currentShape = idx;
                netView.setShape(idx);
                title.setText("📐 전개도 - " + NAMES[idx]);
            });
            btnRow.addView(b);
        }
        root.addView(btnRow);

        netView = new NetView(this);
        LinearLayout.LayoutParams np = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f);
        netView.setLayoutParams(np);
        root.addView(netView);

        Button btnAnim = new Button(this);
        btnAnim.setText("▶ 접기 / 펼치기");
        btnAnim.setTextSize(18);
        btnAnim.setBackgroundColor(0xFF4CC9F0);
        btnAnim.setTextColor(0xFFFFFFFF);
        btnAnim.setPadding(40, 20, 40, 20);
        btnAnim.setOnClickListener(v -> netView.toggleAnimation());
        LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 100);
        bp.setMargins(20, 10, 20, 20);
        btnAnim.setLayoutParams(bp);
        root.addView(btnAnim);

        setContentView(root);
    }

    static class NetView extends View {
        private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private float progress = 0f;
        private boolean unfolding = true;
        private Handler handler = new Handler();
        private int shapeType = 0;
        private boolean animating = false;

        public NetView(Activity ctx) { super(ctx); }

        public void setShape(int t) { shapeType = t; progress = 0f; invalidate(); }

        public void toggleAnimation() {
            animating = true;
            unfolding = !unfolding;
            animate();
        }

        private void animate() {
            progress += unfolding ? 0.03f : -0.03f;
            if (progress >= 1f) { progress = 1f; animating = false; }
            else if (progress <= 0f) { progress = 0f; animating = false; }
            invalidate();
            if (animating) handler.postDelayed(this::animate, 16);
        }

        @Override
        protected void onDraw(Canvas c) {
            float cx = getWidth() / 2f, cy = getHeight() / 2f;
            float s = Math.min(getWidth(), getHeight()) / 6f;
            paint.setStyle(Paint.Style.FILL);
            paint.setStrokeWidth(3f);

            switch (shapeType) {
                case 0: drawCubeNet(c, cx, cy, s); break;
                case 1: drawCylinderNet(c, cx, cy, s); break;
                case 2: drawConeNet(c, cx, cy, s); break;
                case 3: drawPyramidNet(c, cx, cy, s); break;
            }
        }

        private void drawCubeNet(Canvas c, float cx, float cy, float s) {
            int[][] faces = {{0,-2},{0,-1},{-1,0},{0,0},{1,0},{0,1}};
            int[] colors = {0xFFFF6B35,0xFF4CC9F0,0xFF06D6A0,0xFFFFD166,0xFFFF6B35,0xFF4CC9F0};
            float spread = 1f + progress * 0.5f;
            for (int i = 0; i < 6; i++) {
                float fx = cx + faces[i][0] * s * spread;
                float fy = cy + faces[i][1] * s * spread;
                paint.setColor(colors[i]);
                paint.setAlpha((int)(155 + progress * 100));
                c.drawRect(fx - s/2, fy - s/2, fx + s/2, fy + s/2, paint);
                paint.setColor(Color.WHITE);
                paint.setStyle(Paint.Style.STROKE);
                c.drawRect(fx - s/2, fy - s/2, fx + s/2, fy + s/2, paint);
                paint.setStyle(Paint.Style.FILL);
            }
        }

        private void drawCylinderNet(Canvas c, float cx, float cy, float s) {
            float spread = 1f + progress * 0.3f;
            paint.setColor(0xFF4CC9F0);
            c.drawRect(cx - s, cy - s/2 * spread, cx + s, cy + s/2 * spread, paint);
            paint.setColor(0xFF06D6A0);
            c.drawCircle(cx, cy - s * spread, s/2, paint);
            c.drawCircle(cx, cy + s * spread, s/2, paint);
        }

        private void drawConeNet(Canvas c, float cx, float cy, float s) {
            float spread = 1f + progress * 0.3f;
            paint.setColor(0xFFFF6B35);
            Path p = new Path();
            p.moveTo(cx, cy - s * spread);
            p.lineTo(cx - s * spread, cy + s/2 * spread);
            p.lineTo(cx + s * spread, cy + s/2 * spread);
            p.close();
            c.drawPath(p, paint);
            paint.setColor(0xFFFFD166);
            c.drawCircle(cx, cy + s * spread, s/2, paint);
        }

        private void drawPyramidNet(Canvas c, float cx, float cy, float s) {
            float spread = 1f + progress * 0.4f;
            paint.setColor(0xFFFFD166);
            c.drawRect(cx - s/2, cy - s/2, cx + s/2, cy + s/2, paint);
            int[][] dirs = {{0,-1},{1,0},{0,1},{-1,0}};
            int[] cols = {0xFFFF6B35, 0xFF4CC9F0, 0xFF06D6A0, 0xFFFF6B35};
            for (int i = 0; i < 4; i++) {
                float bx = cx + dirs[i][0] * s * spread;
                float by = cy + dirs[i][1] * s * spread;
                paint.setColor(cols[i]);
                Path p = new Path();
                p.moveTo(bx, by);
                if (dirs[i][0] == 0) {
                    p.lineTo(bx - s/2, by - dirs[i][1] * s/2);
                    p.lineTo(bx + s/2, by - dirs[i][1] * s/2);
                } else {
                    p.lineTo(bx - dirs[i][0] * s/2, by - s/2);
                    p.lineTo(bx - dirs[i][0] * s/2, by + s/2);
                }
                p.close();
                c.drawPath(p, paint);
            }
        }
    }
}
