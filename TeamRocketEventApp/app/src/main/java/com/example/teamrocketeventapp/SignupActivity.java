package com.example.teamrocketeventapp;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.net.Uri;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;


    private static int PICK_IMAGE = 1;
    private ImageView profilePicture;
    private FloatingActionButton buttonLoadPicture;
    private Button signUpButton;
    private Button cancelButton;
    private EditText userText;
    private EditText emailText;
    private EditText bdayText;
    private EditText passwordText;
    private EditText passwordConfText;
    private EditText addressText;
    private FirebaseUser user;

    private ProgressDialog progressDialog;
    private DatePickerDialog dpd;
    private Calendar c;
    private String BirthDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        //Get parts of the layout
        profilePicture = (ImageView) findViewById (R.id.profilePicture);
        buttonLoadPicture = (FloatingActionButton) findViewById (R.id.buttonLoadPicture);
        signUpButton = (Button) findViewById(R.id.signUpButton);
        cancelButton = (Button) findViewById(R.id.cancelButton);
        userText = (EditText) findViewById(R.id.usernameEditText);
        emailText = (EditText) findViewById(R.id.emailEditText);
        bdayText = (EditText) findViewById(R.id.bdayEditText);
        passwordText = (EditText) findViewById(R.id.passwordEditText);
        passwordConfText = (EditText) findViewById(R.id.passwordConfEditText);
        addressText = (EditText) findViewById(R.id.addressEditText);

        progressDialog = new ProgressDialog(this);

        signUpButton.setOnClickListener(this);
        buttonLoadPicture.setOnClickListener(this);

        bdayText.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                c = Calendar.getInstance();
                int day = c.get(Calendar.DAY_OF_MONTH);
                int month = c.get(Calendar.MONTH);
                int year = c.get(Calendar.YEAR);

                dpd = new DatePickerDialog(SignupActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int mYear, int mMonth, int mDay) {

                        BirthDate = mDay + "/" + (mMonth+1) + "/" + mYear;
                        bdayText.setText(BirthDate);

                    }
                }, day, month, year);
                dpd.show();
            }
            });
    }

    private void registerUser() {
        //Get info from fields
        String username = userText.getText().toString().trim();
        String email = emailText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();
        String passwordConf = passwordConfText.getText().toString().trim();
        Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher (username);
        Matcher m2 = p.matcher(password);
        boolean b = m.find();
        boolean b2 = m2.find();

        //Error check user input here
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();  //Toast is popup msg at bottom
            return; //Return to stop registration
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(passwordConf)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "Passwords needs to be 6 characters or more", Toast.LENGTH_SHORT).show();
            return;
        }
        if (b) {
            Toast.makeText(this, "Username has special characters", Toast.LENGTH_SHORT).show();
            return;
        }

        if (b2) {
            Toast.makeText(this, "Password has special characters", Toast.LENGTH_SHORT).show();
            return;
        }

        //Progress bar (Since registering online might take a while)
        progressDialog.setMessage("Registering User");
        progressDialog.show();

        //Create user in database
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

        //Sign in then and save info to database
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            user = mAuth.getCurrentUser();
                            saveUserInfo(user.getUid());    //add properties to database
                            updateView(null);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignupActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }


    //Adds user properties to database
    private void saveUserInfo(String userId) {
        String username = userText.getText().toString().trim();
        String email = emailText.getText().toString().trim();
        String bday = BirthDate;
        String address = addressText.getText().toString().trim();

        //Create user object to pass into database call
        UserProperties userProperties = new UserProperties(username, email, bday, address, userId);
        userProperties.addEvent("");

        //add users/ to front of node name to keep database easily searchable
        String node = "users/" + userId;

        //Creates new node in database and saves data
        myRef.child(node).setValue(userProperties);
    }

    public void cancel(View view) {
        //go back to main page when cancel is pressed

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }

    //method id called upon sucessful registration
    public void updateView (View view){
        //go to event page after sucessful registration
        //TODO change MainActivity to the userprofile page
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),PICK_IMAGE);
    }
    @Override
    public void onClick(View view) {
        if (view == signUpButton) {
            registerUser();
            setContentView(R.layout.activity_signup_preferences);
        }
        if (view == cancelButton) {

        }
        if(view == buttonLoadPicture) {
            pickImage();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==PICK_IMAGE && resultCode==RESULT_OK) {
            Uri imageUri = data.getData();
            profilePicture.setImageURI(imageUri);
        }
    }
}
