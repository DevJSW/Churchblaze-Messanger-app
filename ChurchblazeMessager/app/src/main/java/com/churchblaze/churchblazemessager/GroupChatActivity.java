package com.churchblaze.churchblazemessager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupChatActivity extends AppCompatActivity {

    String myCurrentChats = null;
    private Button mStartBtn;
    private TextView mNoPostTxt;
    private ImageView mNoPostImg;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private DatabaseReference mDatabaseUsers, mDatabaseBlockThisUser, mDatabaseUnread;
    private DatabaseReference mDatabaseChatroom,  mDatabaseGroupchat;
    private FirebaseAuth mAuth;
    private RecyclerView mMembersList;
    private Query mQueryPostChats;
    private ProgressBar mProgressBar;
    private Boolean mProcessLike = false;
    private DatabaseReference mDatabaseLike;
    private Query mQueryUnread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                refreshItems();
            }
        });

        mStartBtn = (Button) findViewById(R.id.startChat);

        mDatabaseBlockThisUser = FirebaseDatabase.getInstance().getReference().child("BlockThisUser");
        mDatabaseUnread = FirebaseDatabase.getInstance().getReference().child("Unread");
        mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");
        mNoPostImg = (ImageView) findViewById(R.id.noPostChat);
        mNoPostTxt = (TextView) findViewById(R.id.noPostTxt);
        mDatabaseChatroom = FirebaseDatabase.getInstance().getReference().child("Chatrooms");
        mDatabaseGroupchat = FirebaseDatabase.getInstance().getReference().child("Group_chat");
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar2);
        mAuth = FirebaseAuth.getInstance();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mQueryPostChats = mDatabaseChatroom.orderByChild("sender_uid").equalTo(mAuth.getCurrentUser().getUid());
        mMembersList = (RecyclerView) findViewById(R.id.Members_list);
        mMembersList.setLayoutManager(new LinearLayoutManager(GroupChatActivity.this));
        mMembersList.setHasFixedSize(true);
        mDatabaseChatroom.keepSynced(true);
        mDatabaseUsers.keepSynced(true);
        mDatabaseGroupchat.keepSynced(true);
        mDatabaseLike.keepSynced(true);
        mDatabaseUnread.keepSynced(true);


        mQueryPostChats.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null){

                    mNoPostImg.setVisibility(View.VISIBLE);
                    mNoPostTxt.setVisibility(View.VISIBLE);
                } else {
                    mNoPostImg.setVisibility(View.GONE);
                    mNoPostTxt.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //mDatabaseChatroomsShot = FirebaseDatabase.getInstance().getReference().child("Chatrooms").child(mAuth.getCurrentUser().getUid());

    }

    @Override
    public void onStart() {
        super.onStart();


        FirebaseRecyclerAdapter<People, LetterViewHolder> firebaseRecyclerAdapter = new  FirebaseRecyclerAdapter<People, LetterViewHolder>(

                People.class,
                R.layout.member3_row,
                LetterViewHolder.class,
                mDatabaseGroupchat


        ) {
            @Override
            protected void populateViewHolder(final LetterViewHolder viewHolder, final People model, int position) {

                final String post_key = getRef(position).getKey();
                final String PostKey = getRef(position).getKey();

                viewHolder.setName(model.getName());
                viewHolder.setDate(model.getDate());
                viewHolder.setMessage(model.getMessage());
                viewHolder.setImage(GroupChatActivity.this, model.getImage());
                //viewHolder.setLikeBtn(post_key);

                //CHECK IF POST Is A GROUP POST AND SET THE GROUP ICON VISIBLE
                mDatabaseChatroom.child(mAuth.getCurrentUser().getUid()).child(post_key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String group_icon = (String) dataSnapshot.child("this_is_a_group").getValue();
                        if (group_icon != null) {

                            viewHolder.groupIcon.setVisibility(View.VISIBLE);
                        } else {

                            viewHolder.groupIcon.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                //IF USER HAS NO UNREAD MESSAGE, MAKE COUNTER GONE
                mDatabaseUnread.child(post_key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild(mAuth.getCurrentUser().getUid())) {
                            viewHolder.counterTxt.setVisibility(View.VISIBLE);
                        } else {
                            viewHolder.counterTxt.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                //counting number of unread messages
                mDatabaseUnread.child(post_key).child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        viewHolder.mUnreadTxt.setText(dataSnapshot.getChildrenCount() + "");

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                mDatabaseChatroom.child(post_key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String PostKey = (String) dataSnapshot.child("uid").getValue();

                        // open chatroom activity
                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDatabaseUnread.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                Intent cardonClick = new Intent(GroupChatActivity.this, Chatroom2Activity.class);
                                cardonClick.putExtra("heartraise_id", post_key );
                                startActivity(cardonClick);
                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                // ON OPEN MESSAGE, CLEAR ALL UNREAD MESSAGE
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mDatabaseUnread.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                    }
                });

                mDatabaseUnread.child(post_key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String PostKey = (String) dataSnapshot.child("uid").getValue();

                        // open chatroom activity
                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDatabaseUnread.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                Intent cardonClick = new Intent(GroupChatActivity.this, Chatroom2Activity.class);
                                cardonClick.putExtra("heartraise_id", post_key );
                                startActivity(cardonClick);
                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                viewHolder.mPostImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mProcessLike = true;

                        mDatabaseLike.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if(mProcessLike) {

                                    if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {


                                        mDatabaseLike.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                        viewHolder.mLikeBtn.setVisibility(View.GONE);
                                        mProcessLike = false;
                                    }else {

                                        mDatabaseLike.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue(mAuth.getCurrentUser().getUid());
                                        viewHolder.mLikeBtn.setVisibility(View.VISIBLE);
                                        mProcessLike = false;

                                    }

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });



                viewHolder.mView.setOnLongClickListener(new View.OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View v) {


                        final Context context = GroupChatActivity.this;

                        // custom dialog
                        final Dialog dialog = new Dialog(context);
                        dialog.setContentView(R.layout.popup_dialog_new);
                        dialog.setTitle("Chat options");

                        // set the custom dialog components - text, image and button
                        // send current user uid to block user child
                        LinearLayout blockLiny = (LinearLayout) dialog.findViewById(R.id.blockLiny);
                        blockLiny.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                // push uid to block user child in database
                                final DatabaseReference newPost = mDatabaseBlockThisUser;
                                newPost.child(mAuth.getCurrentUser().getUid()).child(post_key).setValue("Block");
                                AlertDialog diaBox = AskOption2();
                                diaBox.show();
                                dialog.dismiss();
                            }
                        });

                        LinearLayout deleteLiny = (LinearLayout) dialog.findViewById(R.id.deleteLiny);
                        deleteLiny.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                AlertDialog diaBox = AskOption();
                                diaBox.show();
                                dialog.dismiss();
                            }
                        });


                        // if button is clicked, close the custom dialog

                        dialog.show();
                        return false;
                    }

                    private AlertDialog AskOption() {

                        AlertDialog myQuittingDialogBox =new AlertDialog.Builder(GroupChatActivity.this)
                                //set message, title, and icon
                                .setTitle("Delete Alert!")
                                .setMessage("Are you sure you want to remove this chat!")

                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        //your deleting code

                                        mDatabaseChatroom.child(post_key).removeValue();
                                        dialog.dismiss();
                                    }

                                })



                                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        dialog.dismiss();

                                    }
                                })
                                .create();
                        return myQuittingDialogBox;

                    }

                });

            }

            private AlertDialog AskOption2() {

                AlertDialog myQuittingDialogBox =new AlertDialog.Builder(GroupChatActivity.this)
                        //set message, title, and icon
                        .setTitle("Block Alert!")
                        .setMessage("You will no longer recieve messages from this user?")

                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {

                                dialog.dismiss();
                            }

                        })

                        .create();
                return myQuittingDialogBox;


            }



        };

        mMembersList.setAdapter(firebaseRecyclerAdapter);
        checkUserExists();

    }

    private void checkUserExists() {

        mProgressBar.setVisibility(View.VISIBLE);
        final String user_id = mAuth.getCurrentUser().getUid();

        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                if (!dataSnapshot.hasChild(user_id)) {

                    mProgressBar.setVisibility(View.GONE);

                }else {

                    mProgressBar.setVisibility(View.GONE);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                mProgressBar.setVisibility(View.GONE);
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



    public static class LetterViewHolder extends RecyclerView.ViewHolder {

        View mView;

        CircleImageView mPostImg;
        ImageView mLikeBtn, groupIcon;
        TextView mUnreadTxt;
        DatabaseReference mDatabaseLike;
        RelativeLayout counterTxt;
        FirebaseAuth mAuth;
        ProgressBar mProgressBar;

        public LetterViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            mProgressBar = (ProgressBar) mView.findViewById(R.id.progressBar);
            mPostImg = (CircleImageView) mView.findViewById(R.id.post_image);
            mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");
            mUnreadTxt = (TextView) mView.findViewById(R.id.unreadCounter);
            counterTxt = (RelativeLayout) mView.findViewById(R.id.counter);
            groupIcon = (ImageView) mView.findViewById(R.id.group_icon);
            mDatabaseLike.keepSynced(true);
            Query mQueryPostChats;

        }



        public void setName(String name) {

            TextView post_name = (TextView) mView.findViewById(R.id.post_name);
            post_name.setText(name);
        }

        public void setDate(String date) {

            TextView post_date = (TextView) mView.findViewById(R.id.post_date);
            post_date.setText(date);
        }

        public void setMessage(String message) {

            TextView post_message = (TextView) mView.findViewById(R.id.post_message);
            post_message.setText(message);
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
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
