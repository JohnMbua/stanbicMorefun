package com.stanbicagent;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.stanbicagent.R;

import java.util.ArrayList;
import java.util.List;

import model.GetCitiesData;
import model.GetStatesData;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class OpenAccBVNConfirm extends AppCompatActivity implements View.OnClickListener {
    String strfname, strlname, strmidnm, stryob, stremail, strhmdd, strmobn, strsalut, strmarst, strcity, strstate, strgender, straddr;
    String strcode,strcitycode = "N/A";
    TextView txtstrfname, txtstryob, txtstremail,  txtstrmobn,txtstrmarst, txtstrstate, txtstrgender, txtstraddr;
    List<GetStatesData> planetsList = new ArrayList<GetStatesData>();
    List<GetStatesData> arrangelist = new ArrayList<GetStatesData>();
    List<GetCitiesData> citylist = new ArrayList<GetCitiesData>();
    Button btnconfirm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_acc_bvnconfirm);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        btnconfirm = (Button) findViewById(R.id.button2);
        btnconfirm.setOnClickListener(this);
        setSupportActionBar(toolbar);

        // Get the ActionBar here to configure the way it behaves.
        final ActionBar ab = getSupportActionBar();
        //ab.setHomeAsUpIndicator(R.drawable.ic_menu); // set a custom icon for the default home button
        ab.setDisplayShowHomeEnabled(true); // show or hide the default home button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
        ab.setDisplayShowTitleEnabled(false); // disable the default title element here (for centered title)


        txtstrfname = (TextView)findViewById(R.id.fname);
        txtstryob = (TextView)findViewById(R.id.txtstryob);

        txtstrmobn = (TextView)findViewById(R.id.txtstrmobn);






        Intent intent = getIntent();
        if (intent != null) {
            strfname = intent.getStringExtra("fname");
            strlname = intent.getStringExtra("lname");

            stryob = intent.getStringExtra("yob");

            strhmdd = intent.getStringExtra("bvn");
            strmobn = intent.getStringExtra("mobn");



          //  Toast.makeText(getApplicationContext(),stryob,  Toast.LENGTH_LONG).show();

            txtstrfname.setText(strfname+" "+strlname);
            txtstryob.setText(stryob);

            txtstrmobn.setText(strmobn);






        }
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();    //Call the back button's method
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.button2) {
            Intent intent  = new Intent(OpenAccBVNConfirm.this,OpenAccActivity.class);

            intent.putExtra("bvn", strhmdd);






            startActivity(intent);
        }
    }



}
