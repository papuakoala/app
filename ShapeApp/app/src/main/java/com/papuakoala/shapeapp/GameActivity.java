package com.papuakoala.shapeapp;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.Random;

public class GameActivity extends Activity {

    private SilhouetteView silhouetteView;
    private TextView scoreText, questionText;
    private int score = 0, currentAnswer = 0;
    private Random random = new Random();
    private static final String[] NAMES = {"정육면체", "원기둥", "원뿔", "사각뿔"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(0xFFFFF8F0);
        root.setPadding(20, 20, 20, 20);

        TextView title = new TextView(this);
        title.setText("🎮 맞추기 게임");
        title.setTextSize(24);
        title.setTextColor(0xFF06D6A0);
        title.setPadding(20, 10, 20, 5);
        root.addView(title);

        scoreText = new TextView(this);
        scoreText.setText("점수: 0");
        scoreText.setTextSize(20);
        scoreText.setTextColor(0xFFFF6B35);
        scoreText.setPadding(20, 0, 20, 10);
        root.addView(scoreText);

        questionText = new TextView(this);
        questionText.setText("이 도형은 무엇일까요?");
        questionText.setTextSize(18);
        questionText.setTextColor(0xFF333333);
        questionText.setGravity(android.view.Gravity.CENTER);
        root.addView(questionText);

        silhouetteView = new SilhouetteView(this);
        LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f);
        silhouetteView.setLayoutParams(sp);
        root.addView(silhouetteView);

        LinearLayout btnRow1 = new LinearLayout(this);
        btnRow1.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout btnRow2 = new LinearLayout(this);
        btnRow2.setOrientation(LinearLayout.HORIZONTAL);

        int[] colors = {0xFFFF6B35, 0xFF4CC9F0, 0xFF06D6A0, 0xFFFFD166};
        for (int i = 0; i < 4; i++) {
            Button b = new Button(this);
            b.setText(NAMES[i]);
            b.setTextSize(16);
            b.setBackgroundColor(colors[i]);
            b.setTextColor(0xFFFFFFFF);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, 110, 1f);
            lp.setMargins(8, 8, 8, 8);
            b.setLayoutParams(lp);
            final int idx = i;
            b.setOnClickListener(v -> checkAnswer(idx));
            if (i < 2) btnRow1.addView(b);
            else btnRow2.addView(b);
        }
        root.addView(btnRow1);
        root.addView(btnRow2);

        nextQuestion();
        setContentView(root);
    }

    private void nextQuestion() {
        currentAnswer = random.nextInt(4);
        silhouetteView.setShape(currentAnswer);
        questionText.setText("이 도형은 무엇일까요?");
        questionText.setTextColor(0xFF333333);
    }

    private void checkAnswer(int selected) {
        if (selected == currentAnswer) {
            score++;
            scoreText.setText("점수: " + score);
            questionText.setText("🎉 정답! " + NAMES[currentAnswer] + "이에요!");
            questionText.setTextColor(0xFF06D6A0);
        } else {
            questionText.setText("😅 아니에요! " + NAMES[currentAnswer] + "이에요!");
            questionText.setTextColor(0xFFFF6B35);
        }
        silhouetteView.postDelayed(this::nextQuestion, 1500);
    }

    static class SilhouetteView extends View {
        private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private int shapeType = 0;

        public SilhouetteView(Activity ctx) { super(ctx); }
        public void setShape(int t) { shapeType = t; invalidate(); }

        @Override
        protected void onDraw(Canvas c) {
            float cx = getWidth() / 2f, cy = getHeight() / 2f;
            float s = Math.min(getWidth(), getHeight()) * 0.35f;
            paint.setColor(0xFF333333);
            paint.setStyle(Paint.Style.FILL);
            switch (shapeType) {
                case 0:
                    c.drawRect(cx - s, cy - s, cx + s, cy + s, paint);
                    break;
                case 1:
                    c.drawRoundRect(cx-s*0.6f, cy-s, cx+s*0.6f, cy+s, 30, 30, paint);
                    break;
                case 2:
                    Path cone = new Path();
                    cone.moveTo(cx, cy - s);
                    cone.lineTo(cx - s, cy + s);
                    cone.lineTo(cx + s, cy + s);
                    cone.close();
                    c.drawPath(cone, paint);
                    break;
                case 3:
                    Path pyr = new Path();
                    pyr.moveTo(cx, cy - s);
                    pyr.lineTo(cx - s, cy + s);
                    pyr.lineTo(cx + s, cy + s);
                    pyr.close();
                    c.drawPath(pyr, paint);
                    paint.setColor(0xFF555555);
                    Path side = new Path();
                    side.moveTo(cx, cy - s);
                    side.lineTo(cx + s * 0.3f, cy);
                    side.lineTo(cx + s, cy + s);
                    side.close();
                    c.drawPath(side, paint);
                    break;
            }
        }
    }
}
