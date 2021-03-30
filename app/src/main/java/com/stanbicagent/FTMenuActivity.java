package com.stanbicagent;
//

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;
import androidx.appcompat.app.ActionBar;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import adapter.adapter.DepoMenuAdapt;
import adapter.adapter.OTBList;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class FTMenuActivity extends BaseActivity {
    GridView gridView;
    List<OTBList> planetsList = new ArrayList<OTBList>();
    String ptype;
    ListView lv;
    DepoMenuAdapt aAdpt;
    ProgressDialog prgDialog, prgDialog2;
    SessionManagement session;
    String sbpam = "0", pramo = "0";
    boolean blsbp = false, blpr = false, blpf = false, bllr = false, blms = false, blmpesa = false, blcash = false;
    ArrayList<String> ds = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nl_activity_ftmenu);
        session = new SessionManagement(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayShowHomeEnabled(true); // show or hide the default home button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
        ab.setDisplayShowTitleEnabled(false); // disable the default title element here (for centered title)
        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Please wait...");
        prgDialog2 = new ProgressDialog(this);
        prgDialog2.setMessage("Loading....");
        gridView = (GridView) findViewById(R.id.gridView1);
        prgDialog.setCancelable(false);
        lv = (ListView) findViewById(R.id.lv);
        SetPop();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Fragment fragment = null;
                String title = null;
                if (position == 0) {
                    startActivity(new Intent(FTMenuActivity.this, CashDepoTransActivity.class));
                } else if (position == 1) {
                    startActivity(new Intent(FTMenuActivity.this, NL_CEVA_CardTransfer_Amount_Activity.class));
                }
                if (fragment != null) {
                }
            }
        });

        session.setString("bankname", null);
        session.setString("bankcode", null);
        session.setString("recanno", null);
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

    public void SetPop() {
        planetsList.clear();
        planetsList.add(new OTBList("Cash Transfer", "057"));
        planetsList.add(new OTBList("Card Transfer", "058"));
        aAdpt = new DepoMenuAdapt(planetsList, this);
        lv.setAdapter(aAdpt);
    }
}



//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.Intent;
//import androidx.fragment.app.Fragment;
//import androidx.appcompat.app.ActionBar;
//
//import android.os.Bundle;
//import androidx.appcompat.widget.Toolbar;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.GridView;
//import android.widget.ListView;
//
//import com.stanbicagent.R;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import adapter.adapter.DepoMenuAdapt;
//import adapter.adapter.OTBList;
//import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
//
//public class FTMenuActivity extends BaseActivity {
//    GridView gridView;
//    List<OTBList> planetsList = new ArrayList<OTBList>();
//    String ptype;
//    ListView lv;
//    DepoMenuAdapt aAdpt;
//    ProgressDialog prgDialog, prgDialog2;
//    SessionManagement session;
//    String sbpam = "0", pramo = "0";
//    boolean blsbp = false, blpr = false, blpf = false, bllr = false, blms = false, blmpesa = false, blcash = false;
//    ArrayList<String> ds = new ArrayList<String>();
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_ftmenu);
//        session = new SessionManagement(this);
//
//
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//
//        setSupportActionBar(toolbar);
//
//        // Get the ActionBar here to configure the way it behaves.
//        final ActionBar ab = getSupportActionBar();
//        //ab.setHomeAsUpIndicator(R.drawable.ic_menu); // set a custom icon for the default home button
//        ab.setDisplayShowHomeEnabled(true); // show or hide the default home button
//        ab.setDisplayHomeAsUpEnabled(true);
//        ab.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
//        ab.setDisplayShowTitleEnabled(false); // disable the default title element here (for centered title)
//        prgDialog = new ProgressDialog(this);
//
//        prgDialog.setMessage("Please wait...");
//
//        prgDialog2 = new ProgressDialog(this);
//        prgDialog2.setMessage("Loading....");
//
//        gridView = (GridView) findViewById(R.id.gridView1);
//
//        prgDialog.setCancelable(false);
//
//        //checkInternetConnection2();
//        lv = (ListView) findViewById(R.id.lv);
//        SetPop();
//        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    int position, long id) {
//                Fragment fragment = null;
//                String title = null;
//
//                if (position == 0) {
//                    /*fragment = new CashDepoTrans();
//                    title = "Cash Deposit";*/
//                    startActivity(new Intent(FTMenuActivity.this, CashDepoTransActivity.class));
//                } else if (position == 1) {
//                /*    fragment = new SendOTB();
//                    title = "Other Bank";*/
//
//                    startActivity(new Intent(FTMenuActivity.this, SendOTBActivity.class));
//                }/* else if (position == 2) {
//                    fragment = new SendtoWallet();
//                    title = "Send to Wallet";
//                } */
//                else if (position == 2) {
//                    startActivity(new Intent(FTMenuActivity.this, SendOtherWalletActivity.class));
//                }
//
//                if (fragment != null) {
//                   /* FragmentManager fragmentManager = getFragmentManager();
//                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                    //  String tag = Integer.toString(title);
//                    fragmentTransaction.replace(R.id.container_body, fragment, title);
//                    fragmentTransaction.addToBackStack(null);
//                    ((FMobActivity)getActivity())
//                            .setActionBarTitle(title);
//                    fragmentTransaction.commit();*/
//                }
//
//
//            }
//        });
//
//        session.setString("bankname",null);
//        session.setString("bankcode",null);
//        session.setString("recanno",null);
//    }
//    @Override
//    protected void attachBaseContext(Context newBase) {
//        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
//    }
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == android.R.id.home) {
//            onBackPressed();    //Call the back button's method
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
//
//    public void SetPop(){
//        planetsList.clear();
//
//
//        planetsList.add(new OTBList("AccessBank","057"));
//        planetsList.add(new OTBList("Other Banks","058"));
//        //    planetsList.add(new OTBList("FirstMonie","059"));
//     //   planetsList.add(new OTBList("Mobile Money Wallet","059"));
//        aAdpt = new DepoMenuAdapt(planetsList, this);
//        lv.setAdapter(aAdpt);
//    }
//}
