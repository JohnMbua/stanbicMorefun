package com.stanbicagent;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.Toast;

import com.stanbicagent.R;

import java.util.ArrayList;
import java.util.List;

import adapter.Dashboard;
import adapter.DashboardAdapter;


public class PServices extends Fragment implements View.OnClickListener {


    GridView gridView;
    List<Dashboard> planetsList = new ArrayList<Dashboard>();
    DashboardAdapter aAdpt;
    ProgressDialog prgDialog;
    CheckBox chk1,chk2,chk3,chk4,chk5,chk6,chk7,chk8,chk9,chk10,chk11,chk12,chk13,chk14,chk15,chk16;

    Button ok;
    SessionManagement session;


    public PServices() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.persmenu, container, false);
       chk1 = (CheckBox) rootView.findViewById(R.id.ftran);
        chk2 = (CheckBox) rootView.findViewById(R.id.mbmon);
        chk3 = (CheckBox) rootView.findViewById(R.id.minstat);

        chk4 = (CheckBox) rootView.findViewById(R.id.atopup);
        chk5 = (CheckBox) rootView.findViewById(R.id.fstat);
        chk6 = (CheckBox) rootView.findViewById(R.id.cheqboo);

        chk7 = (CheckBox) rootView.findViewById(R.id.schfees);
        chk8 = (CheckBox) rootView.findViewById(R.id.kpower);
        chk9 = (CheckBox) rootView.findViewById(R.id.nhif);

        chk10 = (CheckBox) rootView.findViewById(R.id.zuku);
        chk11 = (CheckBox) rootView.findViewById(R.id.dstv);
        chk12 = (CheckBox) rootView.findViewById(R.id.krai);

        chk13 = (CheckBox) rootView.findViewById(R.id.jambojet);

        chk14 = (CheckBox) rootView.findViewById(R.id.gotv);
        chk15 = (CheckBox) rootView.findViewById(R.id.nwater);
        chk16 = (CheckBox) rootView.findViewById(R.id.threeg);
        session = new SessionManagement(getActivity());
        ok = (Button) rootView.findViewById(R.id.button1);
        ok.setOnClickListener(this);
        CheckServicesBool();
        return rootView;
    }



    public void StartChartAct(int i){


    }
    @Override
    public void onResume(){
        super.onResume();
        // put your code here...

    }
    @Override
    public void onClick(View view) {
if(view.getId() == R.id.button1){
    if(chk1.isChecked() == true){
    session.SetPers("FTR");
    }else{
        session.SetPersFalse("FTR");


    }
    if(chk2.isChecked() == true){
        session.SetPers("MBM");
    }else{
        session.SetPersFalse("MBM");


    }
    if(chk3.isChecked() == true){
        session.SetPers("MST");
    }else{
        session.SetPersFalse("MST");


    }
    if(chk4.isChecked() == true){
        session.SetPers("ATP");
    }else{
        session.SetPersFalse("ATP");


    }
    if(chk5.isChecked() == true){
        session.SetPers("FST");
    }else{
        session.SetPersFalse("FST");


    }
    if(chk6.isChecked() == true){
        session.SetPers("CHQ");
    }else{
        session.SetPersFalse("CHQ");


    }



    if(chk7.isChecked() == true){
        session.SetPers("SCH");
    }else{
        session.SetPersFalse("SCH");


    }
    if(chk8.isChecked() == true){
        session.SetPers("KPO");
    }else{
        session.SetPersFalse("KPO");


    }
    if(chk9.isChecked() == true){
        session.SetPers("NHF");
    }else{
        session.SetPersFalse("NHF");


    }
    if(chk10.isChecked() == true){
        session.SetPers("ZUK");
    }else{
        session.SetPersFalse("ZUK");


    }
    if(chk11.isChecked() == true){
        session.SetPers("DST");
    }else{
        session.SetPersFalse("DST");


    }
    if(chk12.isChecked() == true){
        session.SetPers("KRA");
    }else{
        session.SetPersFalse("KRA");


    }


    if(chk13.isChecked() == true){
        session.SetPers("JBJ");
    }else{
        session.SetPersFalse("JBJ");


    }
    if(chk14.isChecked() == true){
        session.SetPers("GTV");
    }else{
        session.SetPersFalse("GTV");


    }
    if(chk15.isChecked() == true){
        session.SetPers("NWT");
    }else{
        session.SetPersFalse("NWT");


    }
    if(chk16.isChecked() == true){
        session.SetPers("3GD");
    }else{
        session.SetPersFalse("3GD");


    }
    Toast.makeText(getActivity(), "Settings Applied Successfully ", Toast.LENGTH_LONG).show();
 // SetHome();

    getActivity().finish();
    Intent i = new Intent(getActivity(), MainActivity.class);

    // Staring Login Activity
    startActivity(i);

 /*   getActivity().finish();
    startActivity(new Intent(getActivity(), MainActivity.class));*/
}

    }


    public void CheckServicesBool(){
        boolean chkftra = session.checkPers("FTR");
        boolean chkmbm = session.checkPers("MBM");
        boolean chkmstat = session.checkPers("MST");

        boolean atopup = session.checkPers("ATP");
        boolean fst = session.checkPers("FST");
        boolean chq = session.checkPers("CHQ");

        boolean sch = session.checkPers("SCH");
        boolean kpow = session.checkPers("KPO");
        boolean blnhif = session.checkPers("NHF");

        boolean blzuku = session.checkPers("ZUK");
        boolean blds = session.checkPers("DST");
        boolean blkra = session.checkPers("KRA");


        boolean bljbj = session.checkPers("JBJ");

        boolean blgtv = session.checkPers("GTV");
        boolean blnwt = session.checkPers("NWT");
        boolean bl3g = session.checkPers("3GD");
        if(chkftra == true){
            chk1.setChecked(true);
        }else{
            chk1.setChecked(false);
        }
        if(chkmbm == true){
            chk2.setChecked(true);
        }else{
            chk2.setChecked(false);
        }
        if(chkmstat == true){
            chk3.setChecked(true);
        }else{
            chk3.setChecked(false);
        }

        if(atopup == true){
            chk4.setChecked(true);
        }else{
            chk4.setChecked(false);
        }
        if(fst == true){
            chk5.setChecked(true);
        }else{
            chk5.setChecked(false);
        }
        if(chq == true){
            chk6.setChecked(true);
        }else{
            chk6.setChecked(false);
        }

        if(sch == true){
            chk7.setChecked(true);
        }else{
            chk7.setChecked(false);
        }
        if(kpow == true){
            chk8.setChecked(true);
        }else{
            chk8.setChecked(false);
        }
        if(blnhif == true){
            chk9.setChecked(true);
        }else{
            chk9.setChecked(false);
        }

        if(blzuku == true){
            chk10.setChecked(true);
        }else{
            chk10.setChecked(false);
        }
        if(blds == true){
            chk11.setChecked(true);
        }else{
            chk11.setChecked(false);
        }
        if(blkra == true){
            chk12.setChecked(true);
        }else{
            chk12.setChecked(false);
        }


        if(bljbj == true){
            chk13.setChecked(true);
        }else{
            chk13.setChecked(false);
        }

        if(blgtv == true){
            chk14.setChecked(true);
        }else{
            chk14.setChecked(false);
        }
        if(blnwt == true){
            chk15.setChecked(true);
        }else{
            chk15.setChecked(false);
        }
        if(bl3g == true){
            chk16.setChecked(true);
        }else{
            chk16.setChecked(false);
        }
    }


}
