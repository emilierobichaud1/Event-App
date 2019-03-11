package com.example.teamrocketeventapp;

import android.Manifest;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.FirebaseApp;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);

        getPermissions();
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

    @AfterPermissionGranted(123)//use constant for requestCode
    private void getPermissions() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
        if (EasyPermissions.hasPermissions(this, perms)) {
        } else {
            EasyPermissions.requestPermissions(this, "Location permissions required to use app", 123, perms);
        }
    }
}
