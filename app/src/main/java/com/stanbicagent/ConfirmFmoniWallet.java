package com.stanbicagent;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.stanbicagent.R;

import model.FMoniWallet;
import rest.ApiClient;
import rest.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import security.SecurityLayer;

public class ConfirmFmoniWallet extends Fragment  implements View.OnClickListener{
    TextView wphoneno,recname,recamo,recnarr,recsendnum,recsendnam;
    Button btnsub;
    String recanno, amou ,narra, ednamee,ednumbb,txtname,walphnno ;
    ProgressDialog prgDialog,prgDialog2;
    TextView step1,step2;
    public ConfirmFmoniWallet() {
        // Required empty public constructor
    }
  /*  private static Fragment newInstance(Context context) {
        LayoutOne f = new LayoutOne();

        return f;
    }
*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.confirmfmoniwallet, null);
        wphoneno = (TextView) root.findViewById(R.id.textViewcvv);


        recamo = (TextView) root.findViewById(R.id.textViewrrs);
        recnarr = (TextView) root.findViewById(R.id.textViewrr);

        recsendnam = (TextView) root.findViewById(R.id.sendnammm);
        recsendnum = (TextView) root.findViewById(R.id.sendno);
        prgDialog2 = new ProgressDialog(getActivity());
        prgDialog2.setMessage("Loading....");
        prgDialog2.setCancelable(false);

        btnsub = (Button) root.findViewById(R.id.button2);
        btnsub.setOnClickListener(this);

        Bundle bundle = this.getArguments();
        if (bundle != null) {

            walphnno = bundle.getString("wphoneno");
            amou = bundle.getString("amou");
            narra = bundle.getString("narra");
            ednamee = bundle.getString("ednamee");
            ednumbb = bundle.getString("ednumbb");
            txtname = bundle.getString("txtname");

            wphoneno.setText(walphnno);


            recamo.setText(amou);
            recnarr.setText(narra);

            recsendnam.setText(ednamee);
            recsendnum.setText(ednumbb);
        }
        step1 = (TextView) root.findViewById(R.id.tv);
        step1.setOnClickListener(this);
        step2 = (TextView) root.findViewById(R.id.tv2);
        step2.setOnClickListener(this);
        return root;
    }



    public void StartChartAct(int i){


    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.button2) {
            if (Utility.checkInternetConnection(getActivity())) {

                if (Utility.isNotNull(walphnno)) {
                    if (Utility.isNotNull(amou)) {
                        if (Utility.isNotNull(narra)) {
                            if (Utility.isNotNull(ednamee)) {
                                if (Utility.isNotNull(ednumbb)) {
                                 /*   new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                                            .setTitleText("Confirm Transaction")
                                            .setContentText("Are you sure you want to proceed with this  transaction? \n \n" +
                                                    " Recipient  Account Number: " + recanno + " \n  Recipient Account Name: "+txtname+" \n  Amount: " + amou + " \n Narration: " + narra + " \n Sender Name: " + ednamee + " \n Sender Number: " + ednumbb)
                                            .setConfirmText("Confirm")
                                            .setCancelText("Cancel")
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sDialog) {*/

                                    prgDialog2.show();


                                    ApiInterface apiService =
                                            ApiClient.getClient().create(ApiInterface.class);

                                   // /agencyapi/app/wallet/depwallet.action/1/CEVA/JANE00000000019493818389/500/0/2348180010548/test_narration
                                    Call<FMoniWallet> call = apiService.getWalletInfo("1","CEVA","JANE0000000001","0000",amou,"0","2348180010548","test_narration");
                                    call.enqueue(new Callback<FMoniWallet>() {
                                        @Override
                                        public void onResponse(Call<FMoniWallet>call, Response<FMoniWallet> response) {
                                            if(!(response == null)) {
                                                String responsemessage = response.body().getMessage();
                                                String respcode = response.body().getRespCode();
                                                SecurityLayer.Log("Response Message", responsemessage);

                                                if (Utility.isNotNull(respcode) && Utility.isNotNull(respcode)) {
                                                    Toast.makeText(
                                                            getActivity(),
                                                            "" + responsemessage,
                                                            Toast.LENGTH_LONG).show();

                                                } else {

                                                    Toast.makeText(
                                                            getActivity(),
                                                            "There was an error on your request",
                                                            Toast.LENGTH_LONG).show();


                                                }
                                            }else {

                                                Toast.makeText(
                                                        getActivity(),
                                                        "There was an error on your request",
                                                        Toast.LENGTH_LONG).show();


                                            }
                                            prgDialog2.dismiss();
                                        }

                                        @Override
                                        public void onFailure(Call<FMoniWallet> call, Throwable t) {
                                            // Log error here since request failed
                                            SecurityLayer.Log("throwable error",t.toString());


                                            Toast.makeText(
                                                    getActivity(),
                                                    "There was an error on your request",
                                                    Toast.LENGTH_LONG).show();



                                            prgDialog2.dismiss();
                                        }
                                    });

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
                            "Please enter a value for Account Number",
                            Toast.LENGTH_LONG).show();
                }
            }
        }
        if (view.getId() == R.id.tv) {
            Fragment  fragment = new SendtoWallet();
            String title = "Send to Wallet";


            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment,title);
            fragmentTransaction.addToBackStack(title);
            ((FMobActivity)getActivity())
                    .setActionBarTitle(title);
            fragmentTransaction.commit();
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



    @Override
    public void onResume(){
        super.onResume();
        // put your code here...

    }

}
