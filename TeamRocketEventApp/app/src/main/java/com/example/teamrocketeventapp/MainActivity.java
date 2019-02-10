package com.example.teamrocketeventapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void logIn(View view){
        //go to log in page when log in button pressed
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

    }

    public void signUp(View view){
        //go to sign up page when sign up button pressed
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);

    }
}
