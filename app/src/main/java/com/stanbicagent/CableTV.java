package com.stanbicagent;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.stanbicagent.R;
import com.afollestad.materialdialogs.MaterialDialog;

import security.SecurityLayer;


public class CableTV extends Fragment implements View.OnClickListener {
ImageView imageView1;

    Button btnsub;
    SessionManagement session;
    ProgressDialog prgDialog2;
    EditText amon, edacc,pno,txtamount,txtnarr,edname,ednumber;
    String billid,serviceid,servlabel,servicename,blname,packid,paycode,charge;
    TextView billname,smcno,step2,step1;
    public CableTV() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.cabletv, null);
        session = new SessionManagement(getActivity());
        amon = (EditText) root.findViewById(R.id.amount);
        edacc = (EditText) root.findViewById(R.id.acc);
        billname = (TextView) root.findViewById(R.id.textView1);
        smcno = (TextView) root.findViewById(R.id.smcno);
        prgDialog2 = new ProgressDialog(getActivity());
        prgDialog2.setMessage("Loading Request....");
        prgDialog2.setCancelable(false);

        btnsub = (Button) root.findViewById(R.id.button2);
      btnsub.setOnClickListener(this);

        step2 = (TextView) root.findViewById(R.id.tv2);
        step2.setOnClickListener(this);

        step1 = (TextView) root.findViewById(R.id.tv);
        step1.setOnClickListener(this);

        txtamount = (EditText) root.findViewById(R.id.amount);
        txtnarr = (EditText) root.findViewById(R.id.ednarr);
        edname = (EditText) root.findViewById(R.id.sendname);
        ednumber = (EditText) root.findViewById(R.id.sendnumber);


        View.OnFocusChangeListener ofcListener = new MyFocusChangeListener();
        txtamount.setOnFocusChangeListener(ofcListener);
        txtnarr.setOnFocusChangeListener(ofcListener);
        edname.setOnFocusChangeListener(ofcListener);
        ednumber.setOnFocusChangeListener(ofcListener);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            billid = bundle.getString("billid");
            serviceid = bundle.getString("serviceid");
            servicename = bundle.getString("servicename");
            servlabel = bundle.getString("label");
          blname = bundle.getString("billname");

            packid = bundle.getString("packid");
            paycode = bundle.getString("paymentCode");
            charge = bundle.getString("charge");
            if(Utility.isNotNull(charge)) {
                if (!(charge.equals("N"))) {
                    txtamount.setText(charge);
                }
            }
billname.setText(blname);
            smcno.setText(servlabel);
            edacc.setHint(servlabel);
        }

        return root;
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button2) {

            if (Utility.checkInternetConnection(getActivity())) {
                final String recanno = edacc.getText().toString();
                final String amou = txtamount.getText().toString();
                final String narra = txtnarr.getText().toString();
                final  String ednamee = edname.getText().toString();
                final  String ednumbb = ednumber.getText().toString();
                if (Utility.isNotNull(recanno)) {
                    if (Utility.isNotNull(amou)) {
                        String nwamo = amou.replace(",", "");
                        SecurityLayer.Log("New Amount",nwamo);
                        double txamou = Double.parseDouble(nwamo);
//                        if (txamou >= 100) {
                        if (Utility.isNotNull(narra)) {
                            if (Utility.isNotNull(ednamee)) {
                                if (Utility.isNotNull(ednumbb)) {
                                    Bundle b  = new Bundle();
                                    b.putString("custid",recanno);
                                    b.putString("amou",amou);
                                    b.putString("narra",narra);
                                    b.putString("ednamee",ednamee);
                                    b.putString("ednumbb",ednumbb);
                                    b.putString("billid",billid);
                                    b.putString("billname",blname);
                                    b.putString("serviceid",serviceid);
                                    b.putString("servicename",servicename);
                                    b.putString("label",servlabel);
                                    b.putString("packId",packid);
                                    b.putString("paymentCode",paycode);
                                    Fragment  fragment = new ConfirmCableTV();

                                    fragment.setArguments(b);
                                    FragmentManager fragmentManager = getFragmentManager();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    //  String tag = Integer.toString(title);
                                    fragmentTransaction.replace(R.id.container_body, fragment,"Confirm Cable");
                                    fragmentTransaction.addToBackStack("Confirm Cable");
                                    ((FMobActivity)getActivity())
                                            .setActionBarTitle("Confirm Cable");
                                    fragmentTransaction.commit();

                                }  else {
                                    Toast.makeText(
                                            getActivity(),
                                            "Please enter a valid value for Depositor Number",
                                            Toast.LENGTH_LONG).show();
                                }
                            }else {
                                Toast.makeText(
                                        getActivity(),
                                        "Please enter a valid value for Depositor Name",
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(
                                    getActivity(),
                                    "Please enter a valid value for Narration",
                                    Toast.LENGTH_LONG).show();
                        }
//                        } else {
//                            Toast.makeText(
//                                    getActivity(),
//                                    "Please enter an amount value more than 100 Naira",
//                                    Toast.LENGTH_LONG).show();
//                        }
                    }
                    else {
                        Toast.makeText(
                                getActivity(),
                                "Please enter a valid value for Amount",
                                Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(
                            getActivity(),
                            "Please enter a value for "+servlabel,
                            Toast.LENGTH_LONG).show();
                }
            }

            String amo = amon.getText().toString();
            String custid = edacc.getText().toString();

         //   checkInternetConnection2();

        }
        if (view.getId() == R.id.tv2) {
            Fragment  fragment = new BillMenu();


            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment,"Biller Menu");
            fragmentTransaction.addToBackStack("Biller Menu");
            ((FMobActivity)getActivity())
                    .setActionBarTitle("Biller Menu");
            fragmentTransaction.commit();
        }
        if (view.getId() == R.id.tv) {
            Bundle b  = new Bundle();
            b.putString("serviceid",serviceid);
            b.putString("servicename",servicename);


        Fragment    fragment = new SpecBillMenu();
          String  title = servicename;


            if (fragment != null) {
                fragment.setArguments(b);
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

        }


    public void SetDialog(String msg,String title){
        new MaterialDialog.Builder(getActivity())
                .title(title)
                .content(msg)

                .negativeText("Close")
                .show();
    }

    private class MyFocusChangeListener implements View.OnFocusChangeListener {

        public void onFocusChange(View v, boolean hasFocus){

            if(v.getId() == R.id.amount && !hasFocus) {
                InputMethodManager imm =  (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                String txt = txtamount.getText().toString();
                String fbal = Utility.returnNumberFormat(txt);
                txtamount.setText(fbal);

            }

            if(v.getId() == R.id.ednarr && !hasFocus) {
                InputMethodManager imm =  (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);


            }
            if(v.getId() == R.id.sendname && !hasFocus) {
                InputMethodManager imm =  (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);


            }
            if(v.getId() == R.id.sendnumber && !hasFocus) {
                InputMethodManager imm =  (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);


            }

        }
    }
}
