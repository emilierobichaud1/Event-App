package com.example.teamrocketeventapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }
    public void cancel (View view){
        //go back to main page when cancel is pressed

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }
}
