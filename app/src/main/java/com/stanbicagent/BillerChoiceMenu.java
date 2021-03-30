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
import java.util.HashMap;
import java.util.List;

import adapter.Dashboard;
import adapter.DashboardAdapter;


public class BillerChoiceMenu extends Fragment {

    GridView gridView;
    List<Dashboard> planetsList = new ArrayList<Dashboard>();
    String ptype;
    DashboardAdapter aAdpt;
    ProgressDialog prgDialog,prgDialog2;
    SessionManagement session;
    String sbpam = "0",pramo = "0";
    boolean blsbp = false,blpr = false,blpf =false,bllr = false,blms = false,blmpesa = false,blcash = false;
    ArrayList <String> ds = new ArrayList<String>();
    public BillerChoiceMenu() {
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
        View rootView = inflater.inflate(R.layout.billerschoice, container, false);
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
       PopulateGrid();
       //checkInternetConnection2();

gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
String p = planetsList.get(i).getName();


        if( i == 0){
            Fragment  fragment = new CableTV();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment,"DSTV");
            fragmentTransaction.addToBackStack("DSTV");
            fragmentTransaction.commit();
            ((FMobActivity)getActivity())
                    .setActionBarTitle("DSTV");


        }else if( i == 1){
            Fragment  fragment = new CableTV();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment,"GOTV");
            fragmentTransaction.addToBackStack("GOTV");
            fragmentTransaction.commit();
            ((FMobActivity)getActivity())
                    .setActionBarTitle("GOTV");
        }
        if( i == 2){
            Fragment  fragment = new CableTV();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment,"StarTimes");
            fragmentTransaction.addToBackStack("StarTimes");
            fragmentTransaction.commit();
            ((FMobActivity)getActivity()).setActionBarTitle("StarTimes");


        }/*else if( i == 3){
            Bundle b  = new Bundle();
            String serv ="MyTV";
            b.putString("serv",serv);
            Fragment  fragment = new MyTV();
            fragment.setArguments(b);
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment,serv);
            fragmentTransaction.addToBackStack(serv);
            fragmentTransaction.commit();
            ((FMobActivity)getActivity())
                    .setActionBarTitle(serv);
        }*/
     else   if( i == 3){



        }else if( i == 4){

        }
     else   if( i == 5){



        }
     else   if( i == 7){



        }else if( i == 6){

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
        HashMap<String, String> ustype = session.getUsType();
        String fintype = ustype.get(SessionManagement.US_TYPE);


         //   planetsList.add(new Dashboard("Mvisa Cash In", R.drawable.icons42));
         //   planetsList.add(new Dashboard("Mvisa Cash Out", R.drawable.icons41));
         //   planetsList.add(new Dashboard("Withdraw",R.drawable.icons61));


    //   planetsList.add(new Dashboard("Funds Transfer",R.drawable.icons39));

       /* planetsList.add(new Dashboard("My Profile",R.drawable.icons40));*/

        planetsList.add(new Dashboard("StarTimes",R.drawable.startimes));
        planetsList.add(new Dashboard("Smile",R.drawable.smile));
        planetsList.add(new Dashboard("Spectranet",R.drawable.spectranet));
        planetsList.add(new Dashboard("Airtel Postpaid",R.drawable.airtel));
        planetsList.add(new Dashboard("Arik AirBook",R.drawable.arik));
        planetsList.add(new Dashboard("LCC",R.drawable.lcc));
      //  planetsList.add(new Dashboard("Mini Statement",R.drawable.icons55));
      ////  planetsList.add(new Dashboard("Full Statement",R.drawable.icons55));


    //    planetsList.add(new Dashboard("My Credit Card",R.drawable.icons53));

       // planetsList.add(new Dashboard("Cheques",R.drawable.cheq));


        aAdpt = new DashboardAdapter(planetsList, getActivity());

        gridView.setAdapter(aAdpt);
    }

}
