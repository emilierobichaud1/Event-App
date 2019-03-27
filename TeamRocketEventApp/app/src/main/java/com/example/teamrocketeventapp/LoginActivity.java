package com.example.teamrocketeventapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.validator.routines.EmailValidator;

public class LoginActivity extends AppCompatActivity {

    public static final String userId = "userId";
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String EXTRA_MESSAGE = "com.example.teamrocketeventapp.MESSAGE";
    private static final String TAG = "LoginActivity";
    SharedPreferences sharedPreferences;
    private FirebaseAuth Auth;
    private EditText passwordText;
    private EditText emailText;
    private String uid;
    private FirebaseDatabase database;
    private DatabaseReference usernamesRef;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        emailText = (EditText) findViewById(R.id.enterEmail);
        passwordText = (EditText) findViewById(R.id.enterPassword);
        Auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        usernamesRef = database.getReference("users/usernames");


    }

    public void cancel(View view) {
        //go back to main page when cancel is pressed

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }

    public void checkValidInput(View view) {

        String pass = passwordText.getText().toString().trim();
        String userEmail = emailText.getText().toString().trim();

        if (TextUtils.isEmpty(userEmail)) {
            Toast.makeText(this, "Please enter email or username", Toast.LENGTH_SHORT).show();  //Toast is popup msg at bottom
            return; //Return to stop registration
        }
        if (TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!EmailValidator.getInstance().isValid(userEmail)) {
            usernamesRef.child(userEmail).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        email = dataSnapshot.getValue().toString();
                        login(email, pass);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            email = userEmail;
            login(email, pass);
        }


    }

    private void login(String email, String pass) {
        Auth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            editor.putString(userId, user.getUid());
                            editor.commit();

                            Toast.makeText(LoginActivity.this, "Login Success",
                                    Toast.LENGTH_SHORT).show();

                            updateUI(null);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //method id called upon sucessful login
    public void updateUI(View view) {
        //go to event page after sucessful login
        //TODO change MainActivity to the userprofile page
        Intent intent = new Intent(this, HomePageActivity.class); //temporary change for search testing
        startActivity(intent);
    }


}
