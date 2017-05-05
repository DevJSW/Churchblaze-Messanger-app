package com.churchblaze.churchblazemessager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
