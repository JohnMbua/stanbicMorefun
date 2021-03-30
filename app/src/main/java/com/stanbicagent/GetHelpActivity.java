package com.stanbicagent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.stanbicagent.R;

import java.util.ArrayList;
import java.util.List;

import adapter.adapter.GetHelpAdapt;
import adapter.adapter.GetHelpList;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class GetHelpActivity extends AppCompatActivity {
    List<GetHelpList> planetsList = new ArrayList<GetHelpList>();
    GetHelpAdapt aAdpt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_help);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        final ListView lv = (ListView) findViewById(R.id.lv);
        planetsList.add(new GetHelpList("Quick Guide",R.drawable.benef));


        planetsList.add(new GetHelpList("FAQ",R.drawable.benef));

        planetsList.add(new GetHelpList("Contact Info",R.drawable.benef));
        planetsList.add(new GetHelpList("Send Feedback",R.drawable.benef));

        aAdpt = new GetHelpAdapt(planetsList, getApplicationContext());
        lv.setAdapter(aAdpt);


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (position == 0) {
                    startActivity(new Intent(getApplicationContext(),QuickGuide.class));
                }


                if (position == 1) {
                    startActivity(new Intent(getApplicationContext(),FAQAct.class));
                } else if (position == 2) {
                    startActivity(new Intent(getApplicationContext(),ContactInfo.class));
                } else if (position == 3) {
                    startActivity(new Intent(getApplicationContext(),SendFeedback.class));
                }



            }
        });

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
