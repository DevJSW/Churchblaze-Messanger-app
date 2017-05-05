package com.churchblaze.churchblazemessager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {
    private Menu menu;
    private CircleImageView mPostUserimg;
    private EditText mPostName, mPostStatus;
    private DatabaseReference mDatabaseUsers;
    private StorageReference mStorage;
    private Uri mImageUri = null;
    private FirebaseAuth auth;
    private ProgressDialog mprogress;
    private static final int GALLERY_REQUEST =1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);


        mprogress = new ProgressDialog(this);
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mStorage = FirebaseStorage.getInstance().getReference().child("Profile_Images");
        auth = FirebaseAuth.getInstance();

        mPostName = (EditText) findViewById(R.id.post_name);
        mPostStatus = (EditText) findViewById(R.id.post_status);

        // set user img when clicked by user from gallery
        mPostUserimg = (CircleImageView) findViewById(R.id.post_userimg);
        mPostUserimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });

        mDatabaseUsers.child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String post_name = (String) dataSnapshot.child("name").getValue();
                String post_image = (String) dataSnapshot.child("image").getValue();
                String post_status = (String) dataSnapshot.child("status").getValue();

                mPostName.setText(post_name);
                mPostStatus.setText(post_status);

                Picasso.with(EditProfileActivity.this).load(post_image).into(mPostUserimg);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.menu = menu;
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {

            mImageUri = data.getData();
            mPostUserimg.setImageURI(mImageUri);

        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (item.getItemId()) {

            case android.R.id.home:
                this.finish();
                return true;
            default:
                if (id == R.id.action_save) {


                    final String name_val = mPostName.getText().toString().trim();
                    final String status_val = mPostStatus.getText().toString().trim();

                    Date date = new Date();
                    final String stringDate = DateFormat.getDateInstance().format(date);

                    final String user_id = auth.getCurrentUser().getUid();

                    if (mImageUri == null) {
                        Toast.makeText(getApplicationContext(), "Add profile image!", Toast.LENGTH_SHORT).show();
                    }

                    if (mPostName == null) {
                        Toast.makeText(getApplicationContext(), "Enter Name!", Toast.LENGTH_SHORT).show();
                    }

                    if (mImageUri != null) {

                        mprogress.setMessage("Saving ...");
                        mprogress.show();

                        StorageReference filepath = mStorage.child("Profile_images").child(mImageUri.getLastPathSegment());


                        filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                final Uri downloadUrl = taskSnapshot.getDownloadUrl();

                                final DatabaseReference newPost = mDatabaseUsers;

                                newPost.child(user_id).child("name").setValue(name_val);
                                newPost.child(user_id).child("status").setValue(status_val);
                                newPost.child(user_id).child("image").setValue(downloadUrl.toString());
                                newPost.child(user_id).child("date").setValue(stringDate);
                                newPost.child(user_id).child("uid").setValue(user_id);

                                mprogress.dismiss();

                                Intent cardonClick = new Intent(EditProfileActivity.this, Main2Activity.class);
                                cardonClick.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(cardonClick);

                            }
                        });


                    }



                }
                return super.onOptionsItemSelected(item);
        }
    }
}
