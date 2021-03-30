package com.stanbicagent;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.stanbicagent.R;

import java.util.ArrayList;
import java.util.List;

import adapter.adapter.DepoMenuAdapt;
import adapter.adapter.OTBList;


public class FTMenu extends Fragment {

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

    public FTMenu() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.ftmenu, container, false);
        session = new SessionManagement(getActivity());
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            ptype = bundle.getString("prtype");

        }
        prgDialog = new ProgressDialog(getActivity());

        prgDialog.setMessage("Please wait...");

        prgDialog2 = new ProgressDialog(getActivity());
        prgDialog2.setMessage("Loading....");

        gridView = (GridView) rootView.findViewById(R.id.gridView1);

        prgDialog.setCancelable(false);

        //checkInternetConnection2();
        lv = (ListView) rootView.findViewById(R.id.lv);
        SetPop();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Fragment fragment = null;
                String title = null;

                if (position == 0) {
                    fragment = new CashDepoTrans();
                    title = "Cash Deposit";
                } else if (position == 1) {
                    fragment = new SendOTB();
                    title = "Other Bank";
                }/* else if (position == 2) {
                    fragment = new SendtoWallet();
                    title = "Send to Wallet";
                } */
                 else if (position == 2) {
                    fragment = new SendOtherWallet();
                    title = "Mobile Money Wallet";
                }

                if (fragment != null) {
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    //  String tag = Integer.toString(title);
                    fragmentTransaction.replace(R.id.container_body, fragment, title);
                    fragmentTransaction.addToBackStack(null);
                    ((FMobActivity)getActivity())
                            .setActionBarTitle(title);
                    fragmentTransaction.commit();
                }


            }
        });

        return rootView;
    }


    public void StartChartAct(int i) {


    }

    @Override
    public void onResume() {
        super.onResume();
        // put your code here...

    }


    public void SetPop(){
        planetsList.clear();
       /* planetsList.add(new Dashboard("FirstBank", R.drawable.ic_ftblue));
        planetsList.add(new Dashboard("Other Banks", R.drawable.ic_ftblue));
        planetsList.add(new Dashboard("FirstMonie",R.drawable.ic_ftblue));


        planetsList.add(new Dashboard("Other Wallets",R.drawable.ic_ftblue));



       *//* planetsList.add(new Dashboard("My Profile",R.drawable.icons40));*//*

        aAdpt = new DashboardAdapter( planetsList,getActivity());
        gridView.setAdapter(aAdpt);*/

        planetsList.add(new OTBList("FirstBank","057"));
        planetsList.add(new OTBList("Other Banks","058"));
    //    planetsList.add(new OTBList("FirstMonie","059"));
        planetsList.add(new OTBList("Mobile Money Wallet","059"));
        aAdpt = new DepoMenuAdapt(planetsList, getActivity());
        lv.setAdapter(aAdpt);
    }
}