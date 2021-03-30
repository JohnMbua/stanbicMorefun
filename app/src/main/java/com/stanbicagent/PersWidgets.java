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


public class PersWidgets extends Fragment implements View.OnClickListener {


    GridView gridView;
    List<Dashboard> planetsList = new ArrayList<Dashboard>();
    DashboardAdapter aAdpt;
    ProgressDialog prgDialog;
    CheckBox chk1,chk2,chk4;

    Button ok;
    SessionManagement session;


    public PersWidgets() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.perswidg, container, false);
       chk1 = (CheckBox) rootView.findViewById(R.id.curcalc);
        chk2 = (CheckBox) rootView.findViewById(R.id.forex);
 chk4 = (CheckBox) rootView.findViewById(R.id.lcalc);


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
    session.SetPersWidg("CURC");
    }else{
        session.SetPersWidgFalse("CURC");


    }
    if(chk2.isChecked() == true){
        session.SetPersWidg("FRX");
    }else{
        session.SetPersWidgFalse("FRX");


    }
   /* if(chk3.isChecked() == true){
        session.setPersWidg("PAS");
    }else{
        session.setPersWidgFalse("PAS");


    }*/
    if(chk4.isChecked() == true){
        session.SetPersWidg("LOC");
    }else{
        session.SetPersWidgFalse("LOC");


    }
 /*   if(chk5.isChecked() == true){
        session.SetPersWidg("STC");
    }else{
        session.SetPersWidgFalse("STC");


    }*/

    Toast.makeText(getActivity(), "Settings Applied Successfully", Toast.LENGTH_LONG).show();
    getActivity().finish();
    Intent i = new Intent(getActivity(), MainActivity.class);

    // Staring Login Activity
    startActivity(i);
  //SetHome();
}

    }


    public void CheckServicesBool(){
        boolean chkftra = session.checkPersWidg("CURC");
        boolean chkmbm = session.checkPersWidg("FRX");
        boolean chkpas = session.checkPersWidg("LOC");

        boolean atopup = session.checkPersWidg("STC");

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
      if(chkpas == true){
            chk4.setChecked(true);
        }else{
            chk4.setChecked(false);
        }

       /* if(atopup == true){
            chk5.setChecked(true);
        }else{
            chk5.setChecked(false);
        }*/

    }


}
