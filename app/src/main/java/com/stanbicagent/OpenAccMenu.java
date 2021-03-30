package com.stanbicagent;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;

import com.stanbicagent.R;

import java.util.ArrayList;
import java.util.List;

import adapter.Dashboard;
import adapter.DashboardAdapter;


public class OpenAccMenu extends Fragment {

    GridView gridView;
    String ptype;

    List<Dashboard> planetsList = new ArrayList<Dashboard>();
    ListView lv;
    DashboardAdapter aAdpt;
    ProgressDialog prgDialog,prgDialog2;
    SessionManagement session;
    String sbpam = "0",pramo = "0";
    boolean blsbp = false,blpr = false,blpf =false,bllr = false,blms = false,blmpesa = false,blcash = false;
    ArrayList <String> ds = new ArrayList<String>();
    public OpenAccMenu() {
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
        View rootView = inflater.inflate(R.layout.myhomefrag, container, false);
        session = new SessionManagement(getActivity());
        Bundle bundle = this.getArguments();
        if (bundle != null) {
          ptype = bundle.getString("prtype");

        }
        prgDialog = new ProgressDialog(getActivity());
        gridView = (GridView) rootView.findViewById(R.id.gridView1);
        prgDialog.setMessage("Please wait...");

        prgDialog2 = new ProgressDialog(getActivity());
        prgDialog2.setMessage("Loading....");

       // lv = (ListView) rootView.findViewById(R.id.lv);
        SetPop();

        prgDialog.setCancelable(false);

        return rootView;
    }



    public void StartChartAct(int i){


    }
    @Override
    public void onResume(){
        super.onResume();
        // put your code here...

    }

    public void SetPop(){
        planetsList.clear();
        planetsList.add(new Dashboard("Moneygram",R.drawable.moneygram));
        planetsList.add(new Dashboard("Western Union", R.drawable.westernunion));
        planetsList.add(new Dashboard("Ria", R.drawable.ria));






       /* planetsList.add(new Dashboard("My Profile",R.drawable.icons40));*/

        aAdpt = new DashboardAdapter( planetsList,getActivity());
        gridView.setAdapter(aAdpt);
    }
}
