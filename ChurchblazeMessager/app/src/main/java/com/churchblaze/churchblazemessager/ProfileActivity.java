package com.churchblaze.churchblazemessager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private ImageView mUserImg;
    private TextView mUserName, mStatus;
    private DatabaseReference mDatabaseUser;
    private CircleImageView mCIV;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUserImg = (ImageView) findViewById(R.id.post_image);
        mUserName = (TextView) findViewById(R.id.post_name);
        mStatus = (TextView) findViewById(R.id.post_status);
        mCIV = (CircleImageView) findViewById(R.id.post_image);

        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users");
        auth = FirebaseAuth.getInstance();

        LinearLayout openSettings = (LinearLayout) findViewById(R.id.lin_settings);
        openSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, SettingsActivity.class));
            }
        });

        LinearLayout openShare = (LinearLayout) findViewById(R.id.lin_share);
        openShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent myIntent = new Intent(Intent.ACTION_SEND);
                myIntent.setType("text/plain");
                String shareBody ="Download Churchblaze messenger on google play store today";
                String shareSub = "Dear ";
                myIntent.putExtra(Intent.EXTRA_SUBJECT,shareBody);
                myIntent.putExtra(Intent.EXTRA_TEXT,shareBody);
                startActivity(Intent.createChooser(myIntent,"Share app"));
            }
        });

        mDatabaseUser.child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String post_name = (String) dataSnapshot.child("name").getValue();
                String post_status = (String) dataSnapshot.child("status").getValue();
                String post_image = (String) dataSnapshot.child("image").getValue();

                mUserName.setText(post_name);
                mStatus.setText(post_status);

                Picasso.with(ProfileActivity.this).load(post_image).into(mCIV);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
}
