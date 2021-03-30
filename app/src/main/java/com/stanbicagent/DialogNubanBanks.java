package com.stanbicagent;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.stanbicagent.R;
import com.afollestad.materialdialogs.MaterialDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import adapter.adapter.OTBRetroAdapt;
import model.GetBanksData;
import security.SecurityLayer;


public class DialogNubanBanks extends DialogFragment  {
    ListView lv;
    OTBRetroAdapt aAdpt;
    String bankname,bankcode,recanno;
    ProgressDialog prgDialog;
    List<GetBanksData> planetsList = new ArrayList<GetBanksData>();
    SessionManagement session;
    public DialogNubanBanks() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.otherbankslist, container, false);
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        session = new SessionManagement(getActivity());
        lv = (ListView) root.findViewById(R.id.lv);
        prgDialog = new ProgressDialog(getActivity());
        prgDialog.setMessage("Loading....");
        prgDialog.setCancelable(false);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            recanno = bundle.getString("recanno");
            JSONArray newjs = Utility.getNubanAlgo(recanno);
            SecurityLayer.Log("Nuban Algo Result",newjs.toString());
            SetNubanBanksDaa(newjs);

        }
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
dismiss();
                String bankname = planetsList.get(position).getBankName();
                String bankcode = planetsList.get(position).getBankCode();

                session.setString("bankname",bankname);
                session.setString("bankcode",bankcode);
                session.setString("recanno",recanno);

                ((SendOTBActivity)getActivity()).RunNuban();
                /*Fragment  fragment = new SendOTB();

                fragment.setArguments(b);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                //  String tag = Integer.toString(title);
                fragmentTransaction.replace(R.id.container_body, fragment,"Other Banks");
                fragmentTransaction.addToBackStack("Other Banks");
                ((FMobActivity)getActivity())
                        .setActionBarTitle("Other Banks");
                fragmentTransaction.commit();*/

            }
        });
        return root;
    }

    public void SetNubanBanksDaa(JSONArray servdata){
        planetsList.clear();
        try{

            if(servdata.length() > 0){


                JSONObject json_data = null;
                for (int i = 0; i < servdata.length(); i++) {
                    json_data = servdata.getJSONObject(i);
                    //String accid = json_data.getString("benacid");


                    String instName = json_data.optString("name");

                    String bankCode = json_data.optString("sortcode");




                    planetsList.add( new GetBanksData(instName,bankCode) );




                }
                if(!(getActivity() == null)) {
                    SecurityLayer.Log("Get Banks Data Name",planetsList.get(0).getBankName());
                    Collections.sort(planetsList, new Comparator<GetBanksData>(){
                        public int compare(GetBanksData d1, GetBanksData d2){
                            return d1.getBankName().compareTo(d2.getBankName());
                        }
                    });
                    aAdpt = new OTBRetroAdapt(planetsList, getActivity());
                    lv.setAdapter(aAdpt);
                }


            }else{
                Toast.makeText(
                        getActivity(),
                        "No services available  ",
                        Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            SecurityLayer.Log("encryptionJSONException", e.toString());
            // TODO Auto-generated catch block
            Toast.makeText(getActivity(), getActivity().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
            // SecurityLayer.Log(e.toString());

        }
    }

    public void setDialog(String message){
        new MaterialDialog.Builder(getActivity())
                .title("Error")
                .content(message)

                .negativeText("Dismiss")
                .callback(new MaterialDialog.ButtonCallback()  {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        dialog.dismiss();
                    }
                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
}
