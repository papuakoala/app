package com.papuakoala.shapeapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnExplore = findViewById(R.id.btnExplore);
        Button btnNet = findViewById(R.id.btnNet);
        Button btnGame = findViewById(R.id.btnGame);

        btnExplore.setOnClickListener(v ->
            startActivity(new Intent(this, ExploreActivity.class)));
        btnNet.setOnClickListener(v ->
            startActivity(new Intent(this, NetActivity.class)));
        btnGame.setOnClickListener(v ->
            startActivity(new Intent(this, GameActivity.class)));
    }
}
