package com.churchblaze.churchblazemessager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.util.Date;

public class SendPhotoActivity extends AppCompatActivity {

    private String mPostKey = null;

    private ImageView mAddPhoto;
    private EditText mCaption;
    private RecyclerView mCommentList;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabasePostUser;
    private DatabaseReference mDatabaseComment;
    private DatabaseReference mDatabaseUser;
    private DatabaseReference mDatabasePostComments;
    private Query mQueryPostComment;
    private FirebaseUser mCurrentUser;
    private FirebaseAuth mAuth;
    private StorageReference mStorage;
    private ProgressDialog mProgress;

    private Uri mImageUri = null;
    private static int GALLERY_REQUEST =1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_photo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAddPhoto = (ImageView) findViewById(R.id.addPhoto);
        mAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPhoto();
            }
        });
        mCaption = (EditText) findViewById(R.id.captionInput);
        mPostKey = getIntent().getExtras().getString("heartraise_id");

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Chatrooms");
        mProgress = new ProgressDialog(this);


        mAuth = FirebaseAuth.getInstance();
        mPostKey = getIntent().getExtras().getString("heartraise_id");

        mDatabasePostComments = FirebaseDatabase.getInstance().getReference().child("Chatrooms");
        mQueryPostComment = mDatabasePostComments.orderByChild("post_key").equalTo(mPostKey);

        mCurrentUser = mAuth.getCurrentUser();
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());
        mCommentList = (RecyclerView) findViewById(R.id.comment_list);
        mDatabaseComment = FirebaseDatabase.getInstance().getReference().child("Chatrooms");
        mDatabaseComment.keepSynced(true);
        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabasePostUser = FirebaseDatabase.getInstance().getReference().child("Users");

        mDatabaseComment.keepSynced(true);
        mDatabase.keepSynced(true);



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startPosting();

            }

        });

        addPhoto();
    }

    private void startPosting() {

        mProgress.setMessage("Posting...");

        Date date = new Date();
        final String stringDate = DateFormat.getDateTimeInstance().format(date);

        final String caption_val = mCaption.getText().toString().trim();

        final String user_id = mAuth.getCurrentUser().getUid();
        final String uid = user_id.substring(0, Math.min(user_id.length(), 4));

        if (mImageUri != null) {

            mProgress.show();

            StorageReference filepath = mStorage.child("chats_images").child(mImageUri.getLastPathSegment());

            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    final Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    final DatabaseReference newPost = mDatabaseComment.child(mPostKey).child(mCurrentUser.getUid()).push();
                    final DatabaseReference newPost2 =mDatabaseComment.child(mCurrentUser.getUid()).child(mPostKey).push();

                    mDatabasePostUser.child(mPostKey).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            // getting user uid
                            final String reciever_uid = (String) dataSnapshot.child("uid").getValue();


                                    mDatabaseUser.addValueEventListener(new ValueEventListener() {

                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    newPost.child("message").setValue(caption_val);
                                    newPost.child("photo").setValue(downloadUrl.toString());
                                    newPost.child("uid").setValue(mAuth.getCurrentUser().getUid());
                                    newPost.child("name").setValue(dataSnapshot.child("name").getValue());
                                    newPost.child("image").setValue(dataSnapshot.child("image").getValue());
                                    newPost.child("reciever_uid").setValue(reciever_uid);
                                    newPost.child("date").setValue(stringDate);
                                    newPost.child("post_key").setValue(mPostKey);

                                    newPost2.child("message").setValue(caption_val);
                                    newPost2.child("photo").setValue(downloadUrl.toString());
                                    newPost2.child("uid").setValue(mAuth.getCurrentUser().getUid());
                                    newPost2.child("name").setValue(dataSnapshot.child("name").getValue());
                                    newPost2.child("image").setValue(dataSnapshot.child("image").getValue());
                                    newPost2.child("reciever_uid").setValue(reciever_uid);
                                    newPost2.child("date").setValue(stringDate);
                                    newPost2.child("post_key").setValue(mPostKey);

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    AlertDialog.Builder builder = new AlertDialog.Builder(SendPhotoActivity.this);
                    builder.setTitle("Post Alert!");
                    builder.setMessage("Your comment has been posted SUCCESSFULLY!")
                            .setCancelable(true)
                            .setPositiveButton("OK", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();

                    mProgress.dismiss();


                }


            });

        }
    }

    private void addPhoto() {

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_REQUEST);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (item.getItemId()) {

            case android.R.id.home:
                this.finish();
                return true;
            default:

                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {

            mImageUri = data.getData();
            mAddPhoto.setImageURI(mImageUri);

        }
    }


}
