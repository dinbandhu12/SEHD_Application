package com.example.shed_application;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class SeeFullResult extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_full_result);

        Intent intent = getIntent();
        String msg = intent.getStringExtra(MainActivity.Result);

        // Set text inside textview to message
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView textView = findViewById(R.id.Full_Result_Text);
        textView.setText(msg);
    }
}