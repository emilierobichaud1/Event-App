package com.example.teamrocketeventapp;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.codetroopers.betterpickers.datepicker.DatePickerBuilder;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {
    public static final String userId = "userId";
    public static final String MyPREFERENCES = "MyPrefs";
    private static final int PICK_IMAGE = 1;
    SharedPreferences sharedpreferences;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private StorageReference myStorageRef;
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
    private Uri imageUri;
    private UserProperties currentUser;
    private String node;
    private ProgressDialog progressDialog;
    private DatePickerDialog dpd;
    private Calendar c;
    private Calendar c2;
    private Calendar BirthDate;
    private String bdayString;
    private String userIdtemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();


        //Get parts of the layout
        profilePicture = (ImageView) findViewById(R.id.profilePicture);
        buttonLoadPicture = (FloatingActionButton) findViewById(R.id.buttonLoadPicture);
        signUpButton = (Button) findViewById(R.id.signUpButton);
        cancelButton = (Button) findViewById(R.id.cancelButton);
        userText = (EditText) findViewById(R.id.usernameEditText);
        emailText = (EditText) findViewById(R.id.emailEditText);
        bdayText = (EditText) findViewById(R.id.bdayEditText);
        passwordText = (EditText) findViewById(R.id.passwordEditText);
        passwordConfText = (EditText) findViewById(R.id.passwordConfEditText);
        addressText = (EditText) findViewById(R.id.addressEditText);

        progressDialog = new ProgressDialog(this);


        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();

                //setContentView(R.layout.activity_signup_preferences);

            }
        });

        buttonLoadPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage();

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setContentView(R.layout.activity_main);

            }
        });


        bdayText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                c = Calendar.getInstance();
                int day = c.get(Calendar.DAY_OF_MONTH);
                int month = c.get(Calendar.MONTH);
                int year = c.get(Calendar.YEAR);
                BirthDate = Calendar.getInstance();

                dpd = new DatePickerDialog(SignupActivity.this, android.R.style.Theme_Holo_Dialog, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int mYear, int mMonth, int mDay) {
                        bdayString = mDay + "/" + (mMonth + 1) + "/" + mYear;
                        bdayText.setText(bdayString);

                    }
                }, day, month, year);
                dpd.getDatePicker().updateDate(2001, 0, 1);
                c.set(1900, 0, 1);
                dpd.getDatePicker().setMinDate(c.getTimeInMillis());
                c.add(Calendar.YEAR, +119);
                dpd.getDatePicker().setMaxDate(c.getTimeInMillis());
                //dpd.getDatePicker().updateDate(2001, 0, 1);
                dpd.getDatePicker().setLayoutMode(1);
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
        Matcher m = p.matcher(username);
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
                    FirebaseUser user = mAuth.getCurrentUser();
                    saveUserInfo(user.getUid());    //add properties to database
                    updateView(null);

                } else {
                    Toast.makeText(SignupActivity.this, "Unsuccessful registration. Please try again.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //Sign in then and save info to database
/*        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            saveUserInfo(user.getUid());    //add properties to database
                            updateView(null);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignupActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });*/
    }


    //Adds user properties to database
    private void saveUserInfo(String userId) {
        String username = userText.getText().toString().trim();
        String email = emailText.getText().toString().trim();
        String bday = bdayString;
        String address = addressText.getText().toString().trim();

        //Create user object to pass into database call
        currentUser = new UserProperties(username, email, bday, address, userId);
        currentUser.addEvent("");
        currentUser.addPreferences("");
        currentUser.addPic(new Upload("", "default"));


        //add users/ to front of node name to keep database easily searchable
        node = "users/" + userId;

        //Creates new node in database and saves data
        myRef.child(node).setValue(currentUser);

        //Save username to username list (for login with username)
        String usernameList = "users/usernames/" + username;
        myRef.child(usernameList).setValue(email);

        uploadFile();
    }

    public void cancel(View view) {
        //go back to main page when cancel is pressed

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }

    //method id called upon sucessful registration
    public void updateView(View view) {
        //go to event page after sucessful registration
        //TODO change MainActivity to the userprofile page
        //setContentView(R.layout.activity_signup_preferences);
        Intent intent = new Intent(this, UserPreferencesActivity.class);
        startActivity(intent);
    }

    public void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            Picasso.with(this).load(imageUri).into(profilePicture);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    private void uploadFile() {
        myStorageRef = FirebaseStorage.getInstance().getReference(node);

        if (imageUri != null) {
            myStorageRef.putFile(imageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return myStorageRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        Log.e("logt", "then: " + downloadUri.toString());
                        Upload upload = new Upload(downloadUri.toString(), node);
                        myStorageRef = FirebaseStorage.getInstance().getReference(node);
                        currentUser.addPic(upload);
                        myRef.child(node).setValue(currentUser);
                    } else {
                        Toast.makeText(SignupActivity.this, "Upload Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
