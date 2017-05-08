package com.churchblaze.churchblazemessager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by John on 25-Apr-17.
 */
public class tab3profile extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab3profile, container, false);


        LinearLayout openSettings = (LinearLayout) v.findViewById(R.id.lin_settings);
        openSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
            }
        });

        LinearLayout openBlockUsers = (LinearLayout) v.findViewById(R.id.lin_block_user);
        openBlockUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), BlockedUsersActivity.class));
            }
        });

        LinearLayout openEditProfile = (LinearLayout) v.findViewById(R.id.lin_editprofile);
        openEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(new Intent(getActivity(), EditProfileActivity.class)));
            }
        });

        LinearLayout openNotification = (LinearLayout) v.findViewById(R.id.lin_notifications);
        openNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(new Intent(getActivity(), NotificationActivity.class)));
            }
        });

        LinearLayout openShare = (LinearLayout) v.findViewById(R.id.lin_share);
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

        return v;
    }
}