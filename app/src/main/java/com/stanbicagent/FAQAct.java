package com.stanbicagent;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.ExpandableListView;

import com.stanbicagent.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import adapter.ExpandableListAdapter;
import adapter.adapter.OTBAdapt;
import adapter.adapter.OTBList;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class FAQAct extends AppCompatActivity {
    List<OTBList> planetsList = new ArrayList<OTBList>();
    OTBAdapt aAdpt;
    ExpandableListView lv;

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      //  lv = (ListView)findViewById(R.id.lv);
      //  SetFAQ();




        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lv);

        // preparing list data
        prepareListData();

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);

        // Listview Group click listener
        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                // Toast.makeText(getApplicationContext(),
                // "Group Clicked " + listDataHeader.get(groupPosition),
                // Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        // Listview Group expanded listener
        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
       /*         Toast.makeText(getApplicationContext(),
                        listDataHeader.get(groupPosition) + " Expanded",
                        Toast.LENGTH_SHORT).show();*/
            }
        });

        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
       /*         Toast.makeText(getApplicationContext(),
                        listDataHeader.get(groupPosition) + " Collapsed",
                        Toast.LENGTH_SHORT).show();*/

            }
        });

        // Listview on child click listener
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                // TODO Auto-generated method stub
             /*   Toast.makeText(
                        getApplicationContext(),
                        listDataHeader.get(groupPosition)
                                + " : "
                                + listDataChild.get(
                                listDataHeader.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT)
                        .show();*/
                return false;
            }
        });
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add("What do I need to get started?");
        listDataHeader.add("Will I have 24/7 access to the app?");
        listDataHeader.add("I have forgotten my PIN,what should I do?");
        listDataHeader.add("What can I do to the make app secure?");
        listDataHeader.add("Do I need a Stanbic Bank account to enjoy this service?");
        listDataHeader.add("Who do I contact for support?");
        // Adding child data
        List<String> top250 = new ArrayList<String>();
        top250.add(" a) Download and install from the stores b) Receive Activation PIN from Stanbic Bank of Kenya through your SMS or email address(Please do not share this PIN with anyone else). c) Proceed to open app and will be taken to activation page d) Proceed to fill the activation details and change the activation PIN ");



        List<String> fpin = new ArrayList<String>();
        fpin.add("Please Contact Customer Care on +254(020)32680000 and will assist in the procedure to set up the PIN ");

        List<String> appsec = new ArrayList<String>();
        appsec.add("Keep your agent PIN secure and dont give it to anyone else");



        List<String> comingSoon = new ArrayList<String>();
        comingSoon.add("Contact Customer Care on +254(020)32680000");


    }


    public void SetFAQ(){

        planetsList.add(new OTBList("What do i need to get started?","057"));
        planetsList.add(new OTBList("Will i have 24/7 access to the app?","058"));
        planetsList.add(new OTBList("I have forgotten my PIN,what should i do?","059"));
        planetsList.add(new OTBList("What can i do to the make app secure?","057"));
        planetsList.add(new OTBList("Do i need a Stanbic Bank account to enjoy this service?","058"));
        planetsList.add(new OTBList("Who do i contact for support?","059"));
        planetsList.add(new OTBList("What accounts can i access on the app?","057"));

        aAdpt = new OTBAdapt(planetsList, getApplicationContext());
        lv.setAdapter(aAdpt);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
