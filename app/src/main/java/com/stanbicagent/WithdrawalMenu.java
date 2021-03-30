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

import adapter.Dashboard;
import adapter.DashboardAdapter;


public class WithdrawalMenu extends Fragment {

    GridView gridView;
    List<Dashboard> planetsList = new ArrayList<Dashboard>();
    String ptype;
    ListView lv;
    DashboardAdapter aAdpt;
    ProgressDialog prgDialog,prgDialog2;
    SessionManagement session;
    String sbpam = "0",pramo = "0";
    boolean blsbp = false,blpr = false,blpf =false,bllr = false,blms = false,blmpesa = false,blcash = false;
    ArrayList <String> ds = new ArrayList<String>();
    public WithdrawalMenu() {
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
        gridView = (GridView) rootView.findViewById(R.id.gridView1);

        prgDialog.setCancelable(false);

       //checkInternetConnection2();
      //  lv = (ListView) rootView.findViewById(R.id.lv);
        SetPop();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Fragment fragment = null;
                String title = null;

                if (position == 0) {
                    fragment = new Withdraw();
                    title = "Withdraw";
                }
               else if (position == 1) {

                }
                if (fragment != null) {
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    //  String tag = Integer.toString(title);
                    fragmentTransaction.replace(R.id.container_body, fragment, title);
                    fragmentTransaction.addToBackStack(title);
                    ((FMobActivity)getActivity())
                            .setActionBarTitle(title);
                    fragmentTransaction.commit();
                }


            }
        });
/*gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
String p = planetsList.get(i).getName();

        planetsList.add(new Dashboard("Token", R.drawable.withdraw));
        planetsList.add(new Dashboard("Card", R.drawable.withdraw));


        if( p.equals("Token")){
            Fragment  fragment = new Withdraw();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment,"Token");
            fragmentTransaction.addToBackStack("Token");
            fragmentTransaction.commit();
            ((MainActivity)getActivity())
                    .setActionBarTitle("Token");

        }
        if( p.equals("Other Banks")){
            Fragment  fragment = new SendOTB();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment,"Other Banks");
            fragmentTransaction.addToBackStack("Other Banks");
            fragmentTransaction.commit();
            ((MainActivity)getActivity())
                    .setActionBarTitle("Other Banks");

        }
        if( p.equals("FirstMonie")){
            Fragment  fragment = new SendtoWallet();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment,"FirstMonie");
            fragmentTransaction.addToBackStack("FirstMonie");
            fragmentTransaction.commit();
            ((MainActivity)getActivity())
                    .setActionBarTitle("FirstMonie");

        }

        if( p.equals("Other Wallets")){
            Fragment  fragment = new SendOtherWallet();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment,"Other Wallets");
            fragmentTransaction.addToBackStack("Other Wallets");
            fragmentTransaction.commit();
            ((MainActivity)getActivity())
                    .setActionBarTitle("Other Wallets");

        }*//*else if( i == 3){
            Fragment  fragment = new ProductsFrag();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment,"Products");
            fragmentTransaction.addToBackStack("Products");
            ((MainActivity)getActivity())
                    .setActionBarTitle("Products");
            fragmentTransaction.commit();
        }*//*
        *//*//*if( i == 4){
            StartChartAct(i);

        }*//*
       else if( p.equals("Mobile Money")){
            Fragment  fragment = new MobileMoney();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment,"Mobile Money");
            fragmentTransaction.addToBackStack("Mobile Money");
            fragmentTransaction.commit();
            ((MainActivity)getActivity())
                    .setActionBarTitle("Mobile Money");


        }
        else if( p.equals("Card Payment")){
            Fragment  fragment = new AgencyCardPay();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment,"Card Pay");
            fragmentTransaction.addToBackStack("Card Pay");
            fragmentTransaction.commit();
            ((MainActivity)getActivity())
                    .setActionBarTitle("Card Pay");


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

      *//*  if( i == 4){
            Fragment  fragment = new MyProfile();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment,"My Profile");
            fragmentTransaction.addToBackStack("My Profile");
            ((MainActivity)getActivity())
                    .setActionBarTitle("My Profile");
            fragmentTransaction.commit();
        }*//*
        if( p.equals("Full Statement")){
            Fragment  fragment = new MiniStatementFrag();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment,"Full Statement");
            fragmentTransaction.addToBackStack(" Full Statement");
            fragmentTransaction.commit();
            ((MainActivity)getActivity())
                    .setActionBarTitle("Full Statement");


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

    *//*   else if( i == 6){
            Fragment  fragment = new BankRequest();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment,"Bank Request");
            fragmentTransaction.addToBackStack("Bank Request");
            fragmentTransaction.commit();
            ((MainActivity)getActivity())
                    .setActionBarTitle("Bank Request");


        }*//* *//*else if( i == 8){
            Fragment  fragment = new CreditCard();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment,"Credit Card");
            fragmentTransaction.addToBackStack("Credit Card");
            fragmentTransaction.commit();
            ((MainActivity)getActivity())
                    .setActionBarTitle("Credit Card");
        }*//*
   else  if( p.equals("Cheques") ){
            Fragment  fragment = new Cheques();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment,"Cheques");
            fragmentTransaction.addToBackStack("Cheques");
            fragmentTransaction.commit();
            ((MainActivity)getActivity())
                    .setActionBarTitle("Cheques");

        }

    }
});*/
        return rootView;
    }



    public void StartChartAct(int i){


    }
    @Override
    public void onResume(){
        super.onResume();
        // put your code here...

    }
   /* public void PopulateGrid(){
        planetsList.clear();
        HashMap<String, String> ustype = session.getUsType();
        String fintype = ustype.get(SessionManagement.US_TYPE);

        planetsList.add(new Dashboard("Token", R.drawable.withdraw));
        planetsList.add(new Dashboard("Card", R.drawable.withdraw));

    //    planetsList.add(new Dashboard("My Credit Card",R.drawable.icons53));

       // planetsList.add(new Dashboard("Cheques",R.drawable.cheq));


        aAdpt = new DashboardAdapter(planetsList, getActivity());

        gridView.setAdapter(aAdpt);
    }*/
   public void SetPop(){
       planetsList.clear();
       planetsList.add(new Dashboard("Token",R.drawable.withdraw));
       planetsList.add(new Dashboard("Biometric", R.drawable.withdraw));




       /* planetsList.add(new Dashboard("My Profile",R.drawable.icons40));*/

       aAdpt = new DashboardAdapter( planetsList,getActivity());
       gridView.setAdapter(aAdpt);
   }
}
