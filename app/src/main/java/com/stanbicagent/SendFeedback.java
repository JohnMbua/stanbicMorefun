package com.stanbicagent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.stanbicagent.R;

import androidx.appcompat.widget.Toolbar;

public class SendFeedback extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_feedback);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }
}
