package com.churchblaze.churchblazemessager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class CallActivity extends AppCompatActivity {

    String Recipient_id = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        Recipient_id =  getIntent().getExtras().getString("recipient_id");


    }
}
