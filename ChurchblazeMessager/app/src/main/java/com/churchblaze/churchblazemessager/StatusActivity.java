package com.churchblaze.churchblazemessager;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import java.text.DateFormat;
import java.util.Date;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

public class StatusActivity extends AppCompatActivity {

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

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
            final DatabaseReference newPost = mDatabaseChatroom.push();

            // reciever chat screen
            //final DatabaseReference newPost2 = mDatabaseChatroom.child(mPostKey).child(mAuth.getCurrentUser().getUid()).push();


            mDatabaseUser.child(mPostKey).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    // getting user uid
                    final String reciever_uid = (String) dataSnapshot.child("uid").getValue();

                    mDatabaseUser.addValueEventListener(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            // reciever chat
                            newPost.child("message").setValue(message_val);
                            newPost.child("uid").setValue(mCurrentUser.getUid());
                            newPost.child("name").setValue(dataSnapshot.child("name").getValue());
                            newPost.child("image").setValue(dataSnapshot.child("image").getValue());
                            newPost.child("sender_uid").setValue(mCurrentUser.getUid());
                            newPost.child("reciever_uid").setValue(reciever_uid);
                            newPost.child("date").setValue(stringDate);
                            newPost.child("post_key").setValue(mPostKey);

/*
                            //current user (sender) chat
                            newPost2.child("message").setValue(message_val);
                            newPost2.child("uid").setValue(mCurrentUser.getUid());
                            newPost2.child("name").setValue(dataSnapshot.child("name").getValue());
                            newPost2.child("image").setValue(dataSnapshot.child("image").getValue());
                            newPost2.child("sender_uid").setValue(mCurrentUser.getUid());
                            newPost2.child("reciever_uid").setValue(reciever_uid);
                            newPost2.child("reciever_uid").setValue(mCurrentUser.getUid());
                            newPost2.child("date").setValue(stringDate);
                            newPost2.child("post_key").setValue(mPostKey);
*/
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
                    final String stringDate = DateFormat.getDateTimeInstance().format(date);

                    final String status = emojiconEditText.getText().toString().trim();
                    if (!TextUtils.isEmpty(status)) {

                        //mProgress.show();

                        //pushing chats into chatroom on the database inside my uid

                        //sender chat screen
                        final DatabaseReference newPost = mDatabaseUser;

                        // reciever chat screen
                        //final DatabaseReference newPost2 = mDatabaseChatroom.child(mPostKey).child(mAuth.getCurrentUser().getUid()).push();


                        mDatabaseUser.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                // getting user uid

                                newPost.child(mAuth.getCurrentUser().getUid()).child("status").setValue(status);


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
