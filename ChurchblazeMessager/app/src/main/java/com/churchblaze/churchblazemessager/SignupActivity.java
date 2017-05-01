package com.churchblaze.churchblazemessager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.util.Date;

public class SignupActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword, inputName, inputStatus;
    private ImageView mUserImg;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private DatabaseReference mDatabaseUsers;
    private ProgressBar progressBar;
    private Menu menu;
    private FirebaseAuth auth;
    private ProgressDialog mprogress;
    private StorageReference mStorage;
    private Uri mImageUri = null;
    private static int GALLERY_REQUEST =1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        mUserImg = (ImageView) findViewById(R.id.userImgBtn);
        mUserImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });

        mprogress = new ProgressDialog(this);



        inputName = (EditText) findViewById(R.id.name);
        inputStatus = (EditText) findViewById(R.id.status);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mStorage = FirebaseStorage.getInstance().getReference().child("Profile_Images");



    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {

            mImageUri = data.getData();
            mUserImg.setImageURI(mImageUri);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.signup_menu, menu);
        this.menu = menu;
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (item.getItemId()) {

            case android.R.id.home:
                this.finish();
                return true;
            default:
                if (id == R.id.action_next) {

                    final String name = inputName.getText().toString().trim();
                    final String status = inputStatus.getText().toString().trim();
                    String email = inputEmail.getText().toString().trim();
                    String password = inputPassword.getText().toString().trim();

                    Date date = new Date();
                    final String stringDate = DateFormat.getDateInstance().format(date);

                    //final String user_id = auth.getCurrentUser().getUid();

                    if (TextUtils.isEmpty(name)) {
                        Toast.makeText(getApplicationContext(), "Enter name!", Toast.LENGTH_SHORT).show();

                    }

                    if (TextUtils.isEmpty(status)) {
                        Toast.makeText(getApplicationContext(), "Enter your status", Toast.LENGTH_SHORT).show();

                    }

                    if (TextUtils.isEmpty(email)) {
                        Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();

                    }

                    if (TextUtils.isEmpty(password)) {
                        Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    }

                    if (password.length() < 6) {
                        Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    }

                    if ( mImageUri == null) {

                        Toast.makeText(getApplicationContext(), "Select profile image!", Toast.LENGTH_SHORT).show();
                    }


                    progressBar.setVisibility(View.VISIBLE);
                    //create user
                    auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Toast.makeText(SignupActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.GONE);
                                    // If sign in fails, display a message to the user. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in user can be handled in the listener.
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(SignupActivity.this, "Authentication failed." + task.getException(),
                                                Toast.LENGTH_LONG).show();
                                    } else {

                                        mprogress.setMessage("Saving, please wait...");
                                        mprogress.show();

                                        StorageReference filepath = mStorage.child("Profile_images").child(mImageUri.getLastPathSegment());


                                        filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                                final Uri downloadUrl = taskSnapshot.getDownloadUrl();

                                                final DatabaseReference newPost = mDatabaseUsers;

                                                newPost.child(auth.getCurrentUser().getUid()).child("name").setValue(name);
                                                newPost.child(auth.getCurrentUser().getUid()).child("status").setValue(status);
                                                newPost.child(auth.getCurrentUser().getUid()).child("image").setValue(downloadUrl.toString());
                                                newPost.child(auth.getCurrentUser().getUid()).child("date").setValue(stringDate);
                                                newPost.child(auth.getCurrentUser().getUid()).child("uid").setValue(auth.getCurrentUser().getUid());

                                                Intent cardonClick = new Intent(SignupActivity.this, Main2Activity.class);
                                                cardonClick.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(cardonClick);

                                            }
                                        });

                                    }
                                }
                            });

                }
        }
        return super.onOptionsItemSelected(item);
    }
}
