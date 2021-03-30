package com.stanbicagent;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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


public class SendtoWallet extends Fragment implements View.OnClickListener {
ImageView imageView1;

    Button btnsub;
    SessionManagement session;
    ProgressDialog prgDialog;
    RecyclerView lvbann;

    EditText amon, phonenumb,pno,txtamount,txtnarr,edname,ednumber;
    LinearLayoutManager layoutManager,layoutManager2;
    String depositid;

    TextView step2;
    public SendtoWallet() {
        // Required empty public constructor
    }
  /*  private static Fragment newInstance(Context context) {
        LayoutOne f = new LayoutOne();

        return f;
    }
*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.sendtowallet, null);
        session = new SessionManagement(getActivity());

        phonenumb = (EditText) root.findViewById(R.id.phonenumb);
        prgDialog = new ProgressDialog(getActivity());
        prgDialog.setMessage("Loading Request....");
        txtamount = (EditText) root.findViewById(R.id.amount);
        txtnarr = (EditText) root.findViewById(R.id.ednarr);
        edname = (EditText) root.findViewById(R.id.sendname);
        ednumber = (EditText) root.findViewById(R.id.sendnumber);
        btnsub = (Button) root.findViewById(R.id.button2);
      btnsub.setOnClickListener(this);

        lvbann = (RecyclerView) root.findViewById(R.id.listView2);
        layoutManager2 = new LinearLayoutManager(getActivity());
        layoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);


        View.OnFocusChangeListener ofcListener = new MyFocusChangeListener();
        txtamount.setOnFocusChangeListener(ofcListener);
        txtnarr.setOnFocusChangeListener(ofcListener);
        phonenumb.setOnFocusChangeListener(ofcListener);
        edname.setOnFocusChangeListener(ofcListener);
        ednumber.setOnFocusChangeListener(ofcListener);

        step2 = (TextView) root.findViewById(R.id.tv2);
        step2.setOnClickListener(this);
        return root;
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
            if(v.getId() == R.id.phonenumb && !hasFocus) {
                InputMethodManager imm =  (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);


            }
        }
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.button2) {
            String phoneno = phonenumb.getText().toString();
            String amou = txtamount.getText().toString();
            String narra = txtnarr.getText().toString();
            String ednamee = edname.getText().toString();
            String ednumbb = ednumber.getText().toString();

            if (Utility.isNotNull(phoneno)) {
                if (Utility.isNotNull(amou)) {
                    if (Utility.isNotNull(narra)) {
                        if (Utility.isNotNull(ednamee)) {
                            if (Utility.isNotNull(ednumbb)) {
        /*    new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Firstmonie Wallet")
                    .setContentText("Are you sure you want to proceed with this Transaction? \n \n" +
                            "   Phone Number : "+phoneno+"  \n Amount: "+amou+" Naira \n Narration:  "+narra)
                    .setConfirmText("Confirm")
                    .setCancelText("Cancel")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                        }
                    })
                    .show();*/

                                Bundle b = new Bundle();
                                b.putString("wphoneno", phoneno);
                                b.putString("amou", amou);
                                b.putString("narra", narra);
                                b.putString("ednamee", ednamee);
                                b.putString("ednumbb", ednumbb);

                                Fragment fragment = new ConfirmFmoniWallet();

                                fragment.setArguments(b);
                                FragmentManager fragmentManager = getFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                //  String tag = Integer.toString(title);
                                fragmentTransaction.replace(R.id.container_body, fragment, "Confirm Firstmonie");
                                fragmentTransaction.addToBackStack("Confirm Firstmonie");
                                ((FMobActivity) getActivity())
                                        .setActionBarTitle("Confirm Firstmonie");
                                fragmentTransaction.commit();
                            } else {
                                Toast.makeText(
                                        getActivity(),
                                        "Please enter a valid value for Depositor Number",
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
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
                } else {
                    Toast.makeText(
                            getActivity(),
                            "Please enter a valid value for Amount",
                            Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(
                        getActivity(),
                        "Please enter a value for Wallet Phone Number",
                        Toast.LENGTH_LONG).show();
            }
        }

        if (view.getId() == R.id.tv2) {
            Fragment  fragment = new FTMenu();


            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment,"Confirm Transfer");
            fragmentTransaction.addToBackStack("Confirm Transfer");
            ((FMobActivity)getActivity())
                    .setActionBarTitle("Confirm Transfer");
            fragmentTransaction.commit();
        }
    }




    public void SetDialog(String msg,String title){
        new MaterialDialog.Builder(getActivity())
                .title(title)
                .content(msg)

                .negativeText("Close")
                .show();
    }
}
