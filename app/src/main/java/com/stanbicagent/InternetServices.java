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

import com.stanbicagent.R;

import java.util.ArrayList;
import java.util.List;

import adapter.Dashboard;
import adapter.DashboardAdapter;


public class InternetServices extends Fragment {

    GridView gridView;
    List<Dashboard> planetsList = new ArrayList<Dashboard>();
    DashboardAdapter aAdpt;
    ProgressDialog prgDialog;

    String sbpam = "0",pramo = "0";
    boolean blsbp = false,blpr = false,blpf =false,bllr = false,blms = false,blmpesa = false,blcash = false;
    ArrayList <String> ds = new ArrayList<String>();
    public InternetServices() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.dashboard_frag, container, false);

        prgDialog = new ProgressDialog(getActivity());

        prgDialog.setMessage("Please wait...");

        gridView = (GridView) rootView.findViewById(R.id.gridView1);

        prgDialog.setCancelable(false);
       PopulateGrid();

gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


        if( i == 0){


        }else if( i == 1){

        }
        if( i == 2){



        }else if( i == 3){

        }
        if( i == 4){
            Bundle b  = new Bundle();
            String serv ="DSTV";
            b.putString("serv",serv);
            Fragment  fragment = new CableTV();
            fragment.setArguments(b);
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment,"DSTV");
            fragmentTransaction.addToBackStack("DSTV");
            fragmentTransaction.commit();
            ((MainActivity)getActivity())
                    .setActionBarTitle("DSTV");

        }
        if( i == 7){


        }
        if( i == 6){


        }
        if( i == 8){


        }
        if( i == 9){


        }
    }
});
        return rootView;
    }



    public void StartChartAct(int i){


    }
    @Override
    public void onResume(){
        super.onResume();
        // put your code here...

    }
    public void PopulateGrid(){
        planetsList.clear();
        planetsList.add(new Dashboard("Smile",R.drawable.smile));
        planetsList.add(new Dashboard("Spectranet",R.drawable.spectranet));
        planetsList.add(new Dashboard("Airtel Postpaid",R.drawable.airtel));




        aAdpt = new DashboardAdapter(planetsList, getActivity());

        gridView.setAdapter(aAdpt);
    }
}
