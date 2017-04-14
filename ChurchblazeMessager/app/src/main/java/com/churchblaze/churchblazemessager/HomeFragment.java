package com.churchblaze.churchblazemessager;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    View v;
    RecyclerView mMembersList;
    ProgressDialog mProgress;

    private List<People> dataset=new ArrayList<>();
    private List<String> mDatakey=new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view = inflater.inflate(R.layout.fragment_home,container,false);
        return view;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.v=view;

        mProgress = new ProgressDialog(getActivity());
        mProgress.setTitle("Loading");
        mProgress.setMessage("Syncing");
        mProgress.setCancelable(false);
        mProgress.show();

    }


}
