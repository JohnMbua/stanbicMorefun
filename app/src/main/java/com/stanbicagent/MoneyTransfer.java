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

import adapter.TxnAdapter;
import adapter.TxnList;

public class MoneyTransfer extends Fragment {

    GridView gridView;
    List<TxnList> planetsList = new ArrayList<TxnList>();
    String ptype;
    ListView lv;
    TxnAdapter aAdpt;
    ProgressDialog prgDialog,prgDialog2;
    SessionManagement session;
    String sbpam = "0",pramo = "0";
    boolean blsbp = false,blpr = false,blpf =false,bllr = false,blms = false,blmpesa = false,blcash = false;
    ArrayList <String> ds = new ArrayList<String>();
    public MoneyTransfer() {
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
        View rootView = inflater.inflate(R.layout.listvmenu, container, false);
        session = new SessionManagement(getActivity());
        Bundle bundle = this.getArguments();
        if (bundle != null) {
          ptype = bundle.getString("prtype");

        }
        prgDialog = new ProgressDialog(getActivity());

        prgDialog.setMessage("Please wait...");

        prgDialog2 = new ProgressDialog(getActivity());
        prgDialog2.setMessage("Loading....");

      //  gridView = (GridView) rootView.findViewById(R.id.gridView1);
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
 /*   public void PopulateGrid(){
        planetsList.clear();
        HashMap<String, String> ustype = session.getUsType();
        String fintype = ustype.get(SessionManagement.US_TYPE);

        planetsList.add(new Dashboard("Moneygram", R.drawable.moneygram));
        planetsList.add(new Dashboard("Western Union", R.drawable.westernunion));
      //  planetsList.add(new Dashboard("Mini Statement",R.drawable.icons55));
      ////  planetsList.add(new Dashboard("Full Statement",R.drawable.icons55));


    //    planetsList.add(new Dashboard("My Credit Card",R.drawable.icons53));

       // planetsList.add(new Dashboard("Cheques",R.drawable.cheq));


        aAdpt = new DashboardAdapter(planetsList, getActivity());

        gridView.setAdapter(aAdpt);
    }*/
 public void SetPop(){
     planetsList.clear();
     planetsList.add(new TxnList("MoneyGram","", R.drawable.moneygram));
     planetsList.add(new TxnList("Western Union","", R.drawable.westernunion));

     //planetsList.add(new TxnList("Ria","", R.drawable.ria));


       /* planetsList.add(new Dashboard("My Profile",R.drawable.icons40));*/

     aAdpt = new TxnAdapter( planetsList,getActivity());
     lv.setAdapter(aAdpt);
 }
}
