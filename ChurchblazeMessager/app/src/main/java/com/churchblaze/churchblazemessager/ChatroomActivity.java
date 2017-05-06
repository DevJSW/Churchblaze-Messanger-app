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
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

public class ChatroomActivity extends AppCompatActivity {

    private static final String TAG = ChatroomActivity.class.getSimpleName();
    private String mPostKey = null;
    private TextView mNoPostTxt;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressDialog mProgress;
    private RecyclerView mCommentList;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseComment, mDatabaseChatroom;
    private DatabaseReference mDatabaseUser;
    private DatabaseReference mDatabaseUser2;
    private DatabaseReference mDatabasePostChats;
    private Query mQueryPostChats;
    private FirebaseUser mCurrentUser;
    private FirebaseAuth mAuth;
    private ImageView mSendBtn;
    private EditText mCommentField;
    private Uri mImageUri = null;
    private static int GALLERY_REQUEST =1;
    private Menu menu;
    Context context = this;

    EmojiconEditText emojiconEditText;
    EmojiconTextView textView;
    ImageView emojiImageView;
    EmojIconActions emojIcon;
    View rootView;
    private Query mQueryChats;


    /** Called when the activity is first created. */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);
        Toolbar my_toolbar = (Toolbar) findViewById(R.id.mCustomToolbarChat);
        setSupportActionBar(my_toolbar);
        rootView = findViewById(R.id.root_view);
        emojiImageView = (ImageView) findViewById(R.id.emoji_btn);
        emojiconEditText = (EmojiconEditText) findViewById(R.id.emojicon_edit_text);
        emojIcon = new EmojIconActions(this, rootView, emojiconEditText, emojiImageView);
        emojIcon.ShowEmojIcon();
        emojIcon.setIconsIds(R.drawable.ic_action_keyboard, R.drawable.smiley);
        emojIcon.setKeyboardListener(new EmojIconActions.KeyboardListener() {
            @Override
            public void onKeyboardOpen() {
                Log.e(TAG, "Keyboard opened!");
            }

            @Override
            public void onKeyboardClose() {
                Log.e(TAG, "Keyboard closed");
            }
        });
        //mNoPostTxt = (TextView) findViewById(R.id.noPostTxt);
        final RelativeLayout hello = (RelativeLayout) findViewById(R.id.hello);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                refreshItems();
            }
        });


        mAuth = FirebaseAuth.getInstance();
        mPostKey = getIntent().getExtras().getString("heartraise_id");

        mDatabasePostChats = FirebaseDatabase.getInstance().getReference().child("Chatrooms");
        mQueryPostChats = mDatabasePostChats.orderByChild("post_key").equalTo(mPostKey);
        mQueryChats = mDatabasePostChats.orderByChild("uid").equalTo(mAuth.getCurrentUser().getUid());

        mCurrentUser = mAuth.getCurrentUser();
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());
        mDatabaseUser2 = FirebaseDatabase.getInstance().getReference().child("Users");
        mCommentList = (RecyclerView) findViewById(R.id.comment_list);
        mCommentList.setHasFixedSize(true);
        mCommentList.setLayoutManager(new LinearLayoutManager(this));
        mDatabaseComment = FirebaseDatabase.getInstance().getReference().child("Chatrooms");
        mDatabaseComment.keepSynced(true);
        mDatabaseChatroom = FirebaseDatabase.getInstance().getReference().child("Chatrooms");
        mDatabaseChatroom.keepSynced(true);
        mDatabaseUser.keepSynced(true);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Chatrooms").child(mPostKey).child(mAuth.getCurrentUser().getUid());

        mSendBtn = (ImageView) findViewById(R.id.sendBtn);
        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPosting();
            }
        });

        // toolbar back button
        ImageView toolbar_back = (ImageView) findViewById(R.id.toolbar_back);
        toolbar_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatroomActivity.this.finish();
            }
        });

        mDatabaseUser2.child(mPostKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final String userimg = (String) dataSnapshot.child("image").getValue();
                final String username = (String) dataSnapshot.child("name").getValue();
                final CircleImageView civ = (CircleImageView) findViewById(R.id.post_image);
                final TextView name = (TextView) findViewById(R.id.post_name);

                // load image on toolbar
                CircleImageView userImgToolbar = (CircleImageView) findViewById(R.id.toolbarImg);
                Picasso.with(ChatroomActivity.this).load(userimg).into(userImgToolbar);

                // set username on toolbar
                TextView toolbar_username = (TextView) findViewById(R.id.toolbar_username);
                toolbar_username.setText(username);


                mDatabaseUser2.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String username2 = (String) dataSnapshot.child("name").getValue();
                        final TextView name2 = (TextView) findViewById(R.id.post_name2);

                        Picasso.with(ChatroomActivity.this).load(userimg).into(civ);
                        name.setText(username);
                        name2.setText(username2);
                        hello.setVisibility(View.VISIBLE);




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

        mDatabaseComment.keepSynced(true);


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


    private void startPosting() {
       // mProgress.setMessage("Posting...");

        Date date = new Date();
        final String stringDate = DateFormat.getDateTimeInstance().format(date);

        final String message_val = emojiconEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(message_val)) {

            //mProgress.show();

            //pushing chats into chatroom on the database inside my uid

            //sender chat screen
            final DatabaseReference newPost = mDatabaseChatroom.child(mPostKey);
            final DatabaseReference newPost2 = mDatabaseChatroom.child(mAuth.getCurrentUser().getUid());


            mDatabaseUser.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    // getting user details
                    final String reciever_name = (String) dataSnapshot.child("name").getValue();
                    final String reciever_image = (String) dataSnapshot.child("image").getValue();

                    mDatabaseUser2.child(mPostKey).addValueEventListener(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            // reciever chat
                            newPost.child("message").setValue(message_val);
                            newPost.child("uid").setValue(mCurrentUser.getUid());
                            newPost.child("name").setValue(dataSnapshot.child("name").getValue());
                            newPost.child("image").setValue(dataSnapshot.child("image").getValue());
                            newPost.child("sender_uid").setValue(mCurrentUser.getUid());
                            newPost.child("date").setValue(stringDate);
                            newPost.child("post_key").setValue(mPostKey);

                            newPost2.child("message").setValue(message_val);
                            newPost2.child("uid").setValue(mCurrentUser.getUid());
                            newPost2.child("name").setValue(reciever_name);
                            newPost2.child("image").setValue(reciever_image);
                            newPost2.child("sender_uid").setValue(mPostKey);
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

           // mProgress.dismiss();

        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chatroom_menu, menu);
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
                if (id == R.id.action_settings) {

                    Intent cardonClick = new Intent(ChatroomActivity.this, SendPhotoActivity.class);
                    cardonClick.putExtra("heartraise_id", mPostKey );
                    startActivity(cardonClick);
                } else  if(id == R.id.action_call) {

                    Intent cardonClick = new Intent(ChatroomActivity.this, CallActivity.class);
                    cardonClick.putExtra("recipient_id", mPostKey );
                    cardonClick.putExtra("caller_id", mCurrentUser.getUid() );
                    startActivity(cardonClick);
                }


                return super.onOptionsItemSelected(item);
        }
    }

}
