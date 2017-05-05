package com.churchblaze.churchblazemessager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by John on 25-Apr-17.
 */
public class tab2members extends Fragment {

    String post_key = null;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private DatabaseReference mDatabaseUsers;
    private DatabaseReference mDatabaseChats;
    private FirebaseAuth mAuth;
    private RecyclerView mMembersList;
    private Query mQueryPostChats;
    private ProgressBar mProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab2members, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                refreshItems();
            }
        });

        mDatabaseChats = FirebaseDatabase.getInstance().getReference().child("Chats");
        mProgressBar = (ProgressBar) v.findViewById(R.id.progressBar2);
        mAuth = FirebaseAuth.getInstance();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        //mQueryPostChats = mDatabaseChats.orderByChild("post_key").equalTo(post_key);
        mMembersList = (RecyclerView) v.findViewById(R.id.Members_list);
        mMembersList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mMembersList.setHasFixedSize(true);
        mDatabaseChats.keepSynced(true);
        mDatabaseUsers.keepSynced(true);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();



        FirebaseRecyclerAdapter<People, LetterViewHolder> firebaseRecyclerAdapter = new  FirebaseRecyclerAdapter<People, LetterViewHolder>(

                People.class,
                R.layout.member2_row,
                LetterViewHolder.class,
                mDatabaseUsers


        ) {
            @Override
            protected void populateViewHolder(final LetterViewHolder viewHolder, final People model, int position) {

                final String post_key = getRef(position).getKey();
                final String PostKey = getRef(position).getKey();

                viewHolder.setName(model.getName());
                viewHolder.setStatus(model.getStatus());
                viewHolder.setImage(getContext(), model.getImage());

                // open chatroom activity
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent cardonClick = new Intent(getActivity(), ChatroomActivity.class);
                        cardonClick.putExtra("heartraise_id", PostKey );
                        startActivity(cardonClick);
                    }
                });

                viewHolder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        final Context context = getActivity();
                        // custom dialog
                        final Dialog dialog = new Dialog(context);
                        dialog.setContentView(R.layout.popup_dialog);

                        // set the custom dialog components - text, image and button

                         final TextView delete = (TextView) dialog.findViewById(R.id.delete);
                        delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog diaBox = AskOption();
                                diaBox.show();
                                dialog.dismiss();
                            }
                        });

                        // final EditText explanation_input = (EditText) dialog.findViewById(R.id.exInput);


                        // if button is clicked, close the custom dialog

                        dialog.show();
                        return false;
                    }

                });


            }



                    private AlertDialog AskOption() {

                        AlertDialog myQuittingDialogBox =new AlertDialog.Builder(getActivity())
                                //set message, title, and icon
                                .setTitle("Delete")
                                .setMessage("Do you want to Delete this post?")

                                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        //your deleting code

                                         mDatabaseChats.child(post_key).removeValue();

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






        };

        mMembersList.setAdapter(firebaseRecyclerAdapter);


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

        ImageView mConnected;
        Button mChatBtn;

        ProgressBar mProgressBar;

        public LetterViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            mChatBtn = (Button) mView.findViewById(R.id.chatBtn);
            mProgressBar = (ProgressBar) mView.findViewById(R.id.progressBar);
            Query mQueryPostChats;

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

}