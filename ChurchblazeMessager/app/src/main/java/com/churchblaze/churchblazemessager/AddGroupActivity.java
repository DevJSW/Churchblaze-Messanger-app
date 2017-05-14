package com.churchblaze.churchblazemessager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddGroupActivity extends AppCompatActivity {

    private String mPostKey = null;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private DatabaseReference mDatabaseUsers;
    private FirebaseAuth mAuth;
    private ImageView searchBtn, backBtn, groupIcon;
    private Button mSendBtn;
    private EditText searchInput, groupName;
    private Query mQueryMembers;
    private RecyclerView mMembersList;
    private ProgressDialog mprogress;
    private Boolean mProcessSelecting = false;
    private DatabaseReference mDatabaseCreatingGroup, mDatabaseChatroom;
    private StorageReference mStorage;
    private Uri mImageUri = null;
    private static int GALLERY_REQUEST =1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                refreshItems();
            }
        });

        // pushing group info to database
        mSendBtn = (Button) findViewById(R.id.sendBtn);
        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGroup();
            }
        });

        groupName = (EditText) findViewById(R.id.group_name);
        mprogress = new ProgressDialog(this);
        backBtn = (ImageView) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddGroupActivity.this.finish();

            }
        });

        //clean up add group database
       // mDatabaseCreatingGroup.child(mAuth.getCurrentUser().getUid()).removeValue();

        //adding image to icon
        groupIcon = (ImageView) findViewById(R.id.post_image);
        groupIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });

        mStorage = FirebaseStorage.getInstance().getReference().child("Profile_Images");
        mDatabaseChatroom = FirebaseDatabase.getInstance().getReference().child("Chatrooms");
        mDatabaseCreatingGroup = FirebaseDatabase.getInstance().getReference().child("CreatingGroup");
        searchInput = (EditText) findViewById(R.id.searchInput);
        searchBtn = (ImageView) findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String question = searchInput.getText().toString().trim();

                Intent cardonClick = new Intent(AddGroupActivity.this, MembersSearchResult.class);
                cardonClick.putExtra("heartraise_id", question );
                startActivity(cardonClick);

            }

        });

        String question = searchInput.getText().toString().trim();


        mAuth = FirebaseAuth.getInstance();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mMembersList = (RecyclerView) findViewById(R.id.Members_list);
        mMembersList.setLayoutManager(new LinearLayoutManager(this));
        mMembersList.setHasFixedSize(true);

        mDatabaseUsers.keepSynced(true);
    }

    // post data to database
    private void createGroup() {

        final String name = groupName.getText().toString().trim();

        Date date = new Date();
        final String stringDate = DateFormat.getDateInstance().format(date);

        //final String user_id = auth.getCurrentUser().getUid();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getApplicationContext(), "Enter group name!", Toast.LENGTH_SHORT).show();

        }


        if ( mImageUri == null) {

            Toast.makeText(getApplicationContext(), "Select group profile image!", Toast.LENGTH_SHORT).show();
        }


        //create group
                      mprogress.setMessage("Creating group, please wait...");
                            mprogress.show();

                            StorageReference filepath = mStorage.child("Profile_images").child(mImageUri.getLastPathSegment());


                            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    final Uri downloadUrl = taskSnapshot.getDownloadUrl();

                                    mDatabaseCreatingGroup.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            String post_uid = dataSnapshot.getChildren().toString();

                                            //push to group members chat fragment
                                            final DatabaseReference newPost2 = mDatabaseChatroom.child(post_uid).push();
                                            final DatabaseReference newPost = mDatabaseChatroom.child(mAuth.getCurrentUser().getUid()).child(mPostKey).push();

                                            //post to group members chat fragment
                                            newPost2.child("name").setValue(name);
                                            newPost2.child("image").setValue(downloadUrl.toString());
                                            newPost2.child("date").setValue(stringDate);
                                            newPost2.child("this_is_a_group").setValue("true");
                                            newPost2.child("sender_uid").setValue(post_uid);
                                            newPost2.child("uid").setValue(mAuth.getCurrentUser().getUid());
                                            newPost2.child("post_key").setValue(mPostKey);

                                            newPost.child("name").setValue(name);
                                            newPost.child("image").setValue(downloadUrl.toString());
                                            newPost.child("date").setValue(stringDate);
                                            newPost.child("this_is_a_group").setValue("true");
                                            newPost.child("sender_uid").setValue(post_uid);
                                            newPost.child("uid").setValue(mAuth.getCurrentUser().getUid());
                                            newPost2.child("post_key").setValue(mPostKey);

                                            // clean up
                                            mDatabaseCreatingGroup.child(mAuth.getCurrentUser().getUid()).removeValue();

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });



                                    Intent cardonClick = new Intent(AddGroupActivity.this, Main2Activity.class);
                                    cardonClick.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(cardonClick);

                                }
                            });



    }

    void refreshItems() {
        // Load items
        // ...

        // Load complete
        onItemsLoadComplete();
    }

    void onItemsLoadComplete() {
        // Update the adapter and notify data set changed
        // ...

        // Stop refresh animation
        mSwipeRefreshLayout.setRefreshing(false);
    }


    @Override
    protected void onStart() {
        super.onStart();



        FirebaseRecyclerAdapter<People, LetterViewHolder> firebaseRecyclerAdapter = new  FirebaseRecyclerAdapter<People, LetterViewHolder>(

                People.class,
                R.layout.member_checkbox_row,
                LetterViewHolder.class,
                mDatabaseUsers


        ) {
            @Override
            protected void populateViewHolder(final LetterViewHolder viewHolder, final People model, int position) {

                final String post_key = getRef(position).getKey();

                viewHolder.setName(model.getName());
                viewHolder.setStatus(model.getStatus());
                viewHolder.setImage(getApplicationContext(), model.getImage());

                // open chatroom activity

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mProcessSelecting = true;

                        mDatabaseCreatingGroup.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if(mProcessSelecting) {

                                    if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {

                                        mDatabaseCreatingGroup.child(mAuth.getCurrentUser().getUid()).child(post_key).removeValue();
                                        viewHolder.selectedIcon.setVisibility(View.GONE);
                                        mProcessSelecting = false;
                                    }else {

                                        mDatabaseCreatingGroup.child(mAuth.getCurrentUser().getUid()).child(post_key).setValue(mAuth.getCurrentUser().getUid());
                                        viewHolder.selectedIcon.setVisibility(View.VISIBLE);
                                        mProcessSelecting = false;

                                    }

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }
                });

            }

        };

        mMembersList.setAdapter(firebaseRecyclerAdapter);

    }



    public static class LetterViewHolder extends RecyclerView.ViewHolder {

        View mView;

        Button mChatBtn;

        ImageView selectedIcon;
        ProgressBar mProgressBar;

        public LetterViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            mChatBtn = (Button) mView.findViewById(R.id.chatBtn);
            mProgressBar = (ProgressBar) mView.findViewById(R.id.progressBar);
            selectedIcon = (ImageView) mView.findViewById(R.id.selected);

        }

        public void setName(String name) {

            TextView post_name = (TextView) mView.findViewById(R.id.post_name);
            post_name.setText(name);
        }

        public void setStatus(String status) {

            TextView post_status = (TextView) mView.findViewById(R.id.status);
            post_status.setText(status);
        }

        public void setImage(final Context ctx, final String image) {

            final CircleImageView civ = (CircleImageView) mView.findViewById(R.id.post_image);

            Picasso.with(ctx).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(civ, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {


                    Picasso.with(ctx).load(image).into(civ);
                }
            });
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {

            mImageUri = data.getData();
            groupIcon.setImageURI(mImageUri);

        }
    }

}