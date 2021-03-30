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


public class BillersMenu extends Fragment {

    GridView gridView;
    List<Dashboard> planetsList = new ArrayList<Dashboard>();
    String ptype;
    DashboardAdapter aAdpt;
    ProgressDialog prgDialog,prgDialog2;
    SessionManagement session;
    String sbpam = "0",pramo = "0";
    boolean blsbp = false,blpr = false,blpf =false,bllr = false,blms = false,blmpesa = false,blcash = false;
    ArrayList <String> ds = new ArrayList<String>();
    public BillersMenu() {
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
        View rootView = inflater.inflate(R.layout.dashboard_frag, container, false);
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



        if( p.equals("Cable TV")){
            Fragment  fragment = new PayServices();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment,"Cable TV");
            fragmentTransaction.addToBackStack("Cable TV");
            fragmentTransaction.commit();
            ((MainActivity)getActivity())
                    .setActionBarTitle("Cable TV");

        }
        if( p.equals("Internet Subscription")){
            Fragment  fragment = new InternetServices();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment,"Internet");
            fragmentTransaction.addToBackStack("Internet");
            fragmentTransaction.commit();
            ((MainActivity)getActivity())
                    .setActionBarTitle("Internet");

        }
        if( p.equals("Transport")){
            Fragment  fragment = new TransportServ();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment,"Transport");
            fragmentTransaction.addToBackStack("Transport");
            fragmentTransaction.commit();
            ((MainActivity)getActivity())
                    .setActionBarTitle("Transport");

        }

        if( p.equals("Mobile Money Wallet")){
            Fragment  fragment = new SendOtherWallet();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment,"Mobile Money Wallet");
            fragmentTransaction.addToBackStack("Mobile Money Wallet");
            fragmentTransaction.commit();
            ((MainActivity)getActivity())
                    .setActionBarTitle("Mobile Money Wallet");

        }/*else if( i == 3){
            Fragment  fragment = new ProductsFrag();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment,"Products");
            fragmentTransaction.addToBackStack("Products");
            ((MainActivity)getActivity())
                    .setActionBarTitle("Products");
            fragmentTransaction.commit();
        }*/
        /*/if( i == 4){
            StartChartAct(i);

        }*/
       else if( p.equals("Mobile Money")){


        }

        else if( p.equals("Airtime TopUp")){
            Fragment  fragment = new AirtimeTransf();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment,"Airtime TopUp");
            fragmentTransaction.addToBackStack("Airtime TopUp");
            fragmentTransaction.commit();
            ((MainActivity)getActivity())
                    .setActionBarTitle("Airtime TopUp");


        }
        if( p.equals("Bill Payment")){
            Fragment  fragment = new PayServices();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment,"Payment Services");
            fragmentTransaction.addToBackStack("Payment Services");
            ((MainActivity)getActivity())
                    .setActionBarTitle("Payment Services");
            fragmentTransaction.commit();

        }

      /*  if( i == 4){
            Fragment  fragment = new MyProfile();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment,"My Profile");
            fragmentTransaction.addToBackStack("My Profile");
            ((MainActivity)getActivity())
                    .setActionBarTitle("My Profile");
            fragmentTransaction.commit();
        }*/
        if( p.equals("Full Statement")){



        }else if( p.equals("Open Account")){
          //  checkInternetConnection();
            Fragment  fragment = new OpenAcc();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment,"Open Account");
            fragmentTransaction.addToBackStack("Open Account");
            fragmentTransaction.commit();
            ((MainActivity)getActivity())
                    .setActionBarTitle("Open Account");
        }
        else if( p.equals("Withdraw")) {
            Fragment fragment = new Withdraw();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment, "Withdraw");
            fragmentTransaction.addToBackStack("Withdraw");
            fragmentTransaction.commit();
            ((MainActivity)getActivity())
                    .setActionBarTitle("Withdraw");
        }

    /*   else if( i == 6){
            Fragment  fragment = new BankRequest();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment,"Bank Request");
            fragmentTransaction.addToBackStack("Bank Request");
            fragmentTransaction.commit();
            ((MainActivity)getActivity())
                    .setActionBarTitle("Bank Request");


        }*/ /*else if( i == 8){
            Fragment  fragment = new CreditCard();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment,"Credit Card");
            fragmentTransaction.addToBackStack("Credit Card");
            fragmentTransaction.commit();
            ((MainActivity)getActivity())
                    .setActionBarTitle("Credit Card");
        }*/
   else  if( p.equals("Cheques") ){


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
        planetsList.add(new Dashboard("Cable TV", R.drawable.icons39));
        planetsList.add(new Dashboard("Utility Bills", R.drawable.icons39));
        planetsList.add(new Dashboard("Internet Subscription", R.drawable.icons39));
        planetsList.add(new Dashboard("Transport", R.drawable.icons39));
      //  planetsList.add(new Dashboard("Mini Statement",R.drawable.icons55));
      ////  planetsList.add(new Dashboard("Full Statement",R.drawable.icons55));


    //    planetsList.add(new Dashboard("My Credit Card",R.drawable.icons53));

       // planetsList.add(new Dashboard("Cheques",R.drawable.cheq));


        aAdpt = new DashboardAdapter(planetsList, getActivity());

        gridView.setAdapter(aAdpt);
    }


}
