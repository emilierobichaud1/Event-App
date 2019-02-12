package com.example.teamrocketeventapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;

    private Button signupButton;
    private Button cancelButton;
    private EditText userText;
    private EditText emailText;
    private EditText bdayText;
    private EditText passwordText;
    private EditText passwordConfText;
    private EditText addressText;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        //Get parts of the layout
        signupButton = (Button) findViewById(R.id.signupButton);
        cancelButton = (Button) findViewById(R.id.cancelButton);
        userText = (EditText) findViewById(R.id.usernameEditText);
        emailText = (EditText) findViewById(R.id.emailEditText);
        bdayText = (EditText) findViewById(R.id.bdayEditText);
        passwordText = (EditText) findViewById(R.id.passwordEditText);
        passwordConfText = (EditText) findViewById(R.id.passwordConfEditText);
        addressText = (EditText) findViewById(R.id.addressEditText);

        progressDialog = new ProgressDialog(this);

        signupButton.setOnClickListener(this);
    }

    private void registerUser() {
        //Get info from fields
        String username = userText.getText().toString().trim();
        String email = emailText.getText().toString().trim();
        String bday = bdayText.getText().toString().trim();
        String address = addressText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();
        String passwordConf = passwordConfText.getText().toString().trim();

        //Error check user input here
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();  //Toast is popup msg at bottom
            return; //Return to stop registration
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(passwordConf)){
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        //Progress bar (Since registering online might take a while)
        progressDialog.setMessage("Registering User");
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(SignupActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SignupActivity.this, "Unsuccessful registration. Please try again.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //Add username to user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(username).build();

        user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                }
            }
        });

        //Add other properties to database
    }

    public void cancel(View view) {
        //go back to main page when cancel is pressed

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }

    @Override
    public void onClick(View view) {
        if (view == signupButton) {
            registerUser();
        }
        if (view == cancelButton) {

        }
    }
}
