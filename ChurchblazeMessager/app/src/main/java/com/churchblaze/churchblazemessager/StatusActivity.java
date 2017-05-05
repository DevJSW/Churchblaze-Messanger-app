package com.churchblaze.churchblazemessager;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class StatusActivity extends AppCompatActivity {

    private EditText  inputStatus;
    private CircleImageView mPostUserimg;
    private DatabaseReference mDatabaseUsers;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private ProgressDialog mprogress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        auth = FirebaseAuth.getInstance();

        mprogress = new ProgressDialog(this);
        mPostUserimg = (CircleImageView) findViewById(R.id.post_userimg);
        inputStatus = (EditText) findViewById(R.id.status);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");

        mDatabaseUsers.child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                String post_status = (String) dataSnapshot.child("status").getValue();
                String post_image = (String) dataSnapshot.child("image").getValue();

                inputStatus.setText(post_status);

                Picasso.with(StatusActivity.this).load(post_image).into(mPostUserimg);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.status_menu, menu);
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
                if (id == R.id.action_sendBtn) {

                    Date date = new Date();

                    final String status = inputStatus.getText().toString().trim();
                    if (!TextUtils.isEmpty(status)) {

                        //mProgress.show();

                        //pushing chats into chatroom on the database inside my uid

                        //sender chat screen
                        final DatabaseReference newPost = mDatabaseUsers;

                        // reciever chat screen
                        //final DatabaseReference newPost2 = mDatabaseChatroom.child(mPostKey).child(mAuth.getCurrentUser().getUid()).push();


                        mDatabaseUsers.child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                // getting user uid

                                newPost.child(auth.getCurrentUser().getUid()).child("status").setValue(status);


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        // mProgress.dismiss();

                    }


                }

                return super.onOptionsItemSelected(item);
        }
    }

}
