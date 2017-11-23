package com.suchef.suchef;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Restaurante extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurante);

        String s = getIntent().getStringExtra("id");
        System.out.println(s);
    }
}
