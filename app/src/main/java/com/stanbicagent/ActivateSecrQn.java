package com.stanbicagent;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;


import com.stanbicagent.R;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ActivateSecrQn extends AppCompatActivity implements View.OnClickListener {
    Button btnnext;
    String agid,agpin,agphn;

    //Context applicationContext;
  /*  String regId = "";*/
    SessionManagement session;
    ProgressDialog prgDialog,prgDialog2;
    ProgressDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activate_secr_qn);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        agid = getIntent().getStringExtra("agid");
        agphn = getIntent().getStringExtra("agphnno");
        agpin = getIntent().getStringExtra("agpin");
        session = new SessionManagement(getApplicationContext());

        prgDialog2 = new ProgressDialog(this);
        prgDialog2.setMessage("Loading....");
        prgDialog2.setCancelable(false);

        btnnext = (Button) findViewById(R.id.button1);
        btnnext.setOnClickListener(this);

         pDialog = new ProgressDialog(this);
        pDialog.setTitle("Loading");
        pDialog.setCancelable(false);

    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }




    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button1) {


        }
    }

}
