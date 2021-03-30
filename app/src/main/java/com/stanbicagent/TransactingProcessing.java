package com.stanbicagent;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.stanbicagent.R;
import com.afollestad.materialdialogs.MaterialDialog;
import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.vipul.hp_hp.library.Layout_to_Image;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.net.SocketTimeoutException;

import rest.ApiInterface;
import rest.ApiSecurityClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import security.SecurityLayer;

public class TransactingProcessing extends Fragment implements View.OnClickListener{
    Button btnsub;
    String recanno, amou ,narra, ednamee,ednumbb,txtname,strfee,stragcms,bankname,bankcode,txpin,newparams,serv ;
    String txtcustid,serviceid,billid,txtfee,strtref,strlabel,strbillnm,fullname,telcoop;

    ProgressDialog prgDialog,prgDialog2;
    RelativeLayout rlsendname,rlsendno;
    EditText etpin;
    private FirebaseAnalytics mFirebaseAnalytics;
    String   txtrfc,txref;
    Layout_to_Image layout_to_image;  //Create Object of Layout_to_Image Class
TextView txstatus,txdesc;
    LinearLayout relativeLayout;   //Define Any Layout
Button shareImage,repissue;
    Bitmap bitmap;                  //Bitmap for holding Image of layout

    private PinLockView mPinLockView;
    private IndicatorDots mIndicatorDots;
    Button btnconfirm;
    ProgressDialog pro ;
    String finpin;
    private PinLockListener mPinLockListener = new PinLockListener() {
        @Override
        public void onComplete(String pin) {

            //SecurityLayer.Log(TAG, "Pin complete: " + pin);
            finpin = pin;
        }

        @Override
        public void onEmpty() {
            SecurityLayer.Log("Pin Empty", "Pin empty");
        }

        @Override
        public void onPinChange(int pinLength, String intermediatePin) {
            //	SecurityLayer.Log(TAG, "Pin changed, new length " + pinLength + " with intermediate pin " + intermediatePin);
        }
    };
    public TransactingProcessing() {
        // Required empty public constructor
    }
  /*  private static Fragment newInstance(Context context) {
        LayoutOne f = new LayoutOne();

        return f;
    }
*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.transactionproc, null);

        prgDialog2 = new ProgressDialog(getActivity());
        prgDialog2.setMessage("Loading....");
        prgDialog2.setCancelable(false);
        rlsendname = (RelativeLayout) root.findViewById(R.id.rlsendnam);
        rlsendno = (RelativeLayout) root.findViewById(R.id.rlsendnum);

        txstatus = (TextView) root.findViewById(R.id.txstatus);
        txdesc = (TextView) root.findViewById(R.id.txdesc);
        btnsub = (Button) root.findViewById(R.id.button2);
        btnsub.setOnClickListener(this);
        relativeLayout=(LinearLayout)root.findViewById(R.id.receipt);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
           serv = bundle.getString("serv");
            if(serv.equals("CASHDEPO")) {
                recanno = bundle.getString("recanno");
                amou = bundle.getString("amou");
                narra = bundle.getString("narra");
                ednamee = bundle.getString("ednamee");
                ednumbb = bundle.getString("ednumbb");
                txtname = bundle.getString("txtname");
                txtrfc = bundle.getString("refcode");
                String params  = bundle.getString("params");
                stragcms = Utility.returnNumberFormat(bundle.getString("agcmsn"));
                String trantype = bundle.getString("trantype");
                strfee = bundle.getString("fee");
                txpin = bundle.getString("txpin");
                newparams = params;
                Log.v("Params",newparams+"/"+txpin);
                IntraDepoBankResp(newparams+"/"+txpin);

            }
            if(serv.equals("CASHTRAN")) {
                recanno = bundle.getString("recanno");
                amou = bundle.getString("amou");
                narra = bundle.getString("narra");
                ednamee = bundle.getString("ednamee");
                ednumbb = bundle.getString("ednumbb");
                txtname = bundle.getString("txtname");
                txtrfc = bundle.getString("refcode");
                String params  = bundle.getString("params");
                stragcms = Utility.returnNumberFormat(bundle.getString("agcmsn"));
                String trantype = bundle.getString("trantype");
                strfee = bundle.getString("fee");
                txpin = bundle.getString("txpin");
 newparams = params;
                Log.v("Params",newparams+"/"+txpin);
                IntraTranBankResp(newparams+"/"+txpin);

            }
            if(serv.equals("WDRAW")) {
                recanno = bundle.getString("recanno");
                amou = bundle.getString("amou");
                strfee = bundle.getString("fee");
                txtname = bundle.getString("txtname");
                txref = bundle.getString("txref");
                  txtrfc = bundle.getString("refcode");

                String params  = bundle.getString("params");
                stragcms = Utility.returnNumberFormat(bundle.getString("agcmsn"));

                strfee = bundle.getString("fee");

                txpin = bundle.getString("txpin");
                newparams = params;
                Log.v("Params",newparams+"/"+txpin);



                WithdrawResp(newparams+"/"+txpin);

            }
            if(serv.equals("AIRT")) {

                strfee = bundle.getString("fee");



                stragcms = Utility.returnNumberFormat(bundle.getString("agcmsn"));

                strfee = bundle.getString("fee");

                txtcustid = bundle.getString("mobno");
                amou = bundle.getString("amou");
                telcoop = bundle.getString("telcoop");
                String params  = bundle.getString("params");
                String txtamou = Utility.returnNumberFormat(amou);
                if(txtamou.equals("0.00")){
                    amou = txtamou;
                }

                billid = bundle.getString("billid");
                serviceid = bundle.getString("serviceid");
                txpin = bundle.getString("txpin");
                newparams = params;
                Log.v("Params",newparams+"/"+txpin);

                AirtimeResp(newparams+"/"+txpin);

            }
            if(serv.equals("SENDOTB")) {
                recanno = bundle.getString("recanno");
                amou = bundle.getString("amou");
                narra = bundle.getString("narra");
                ednamee = bundle.getString("ednamee");
                ednumbb = bundle.getString("ednumbb");
                txtname = bundle.getString("txtname");
                bankname = bundle.getString("bankname");
                bankcode = bundle.getString("bankcode");
                strfee = bundle.getString("fee");
                txtrfc = bundle.getString("refcode");
                String params  = bundle.getString("params");
                txpin = bundle.getString("txpin");
                newparams = params;
                Log.v("Params",newparams+"/"+txpin);


                InterBankResp(newparams+"/"+txpin);

            }
            if(serv.equals("CABLETV")) {
                txtcustid = bundle.getString("custid");
                amou = bundle.getString("amou");
                narra = bundle.getString("narra");
                ednamee = bundle.getString("ednamee");
                ednumbb = bundle.getString("ednumbb");
                strlabel = bundle.getString("label");
                billid = bundle.getString("billid");
                strbillnm = bundle.getString("billname");
                serviceid = bundle.getString("serviceid");
                strfee = bundle.getString("fee");
                strtref = bundle.getString("tref");
                fullname = bundle.getString("fullname");
                String params  = bundle.getString("params");
                txpin = bundle.getString("txpin");
                newparams = params;
                Log.v("Params",newparams+"/"+txpin);




                PayBillResp(newparams+"/"+txpin);

            }
        }




        return root;
    }

    private void shareImage(Uri imagePath) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        sharingIntent.setType("image/*");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, imagePath);
        startActivity(Intent.createChooser(sharingIntent, "Share Image Using"));
    }
    public void StartChartAct(int i){


    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.button2) {
           getActivity().finish();
            startActivity(new Intent(getActivity(), FMobActivity.class));
        }

    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @Override
    public void onResume(){
        super.onResume();
        // put your code here...

    }












    private void IntraDepoBankResp(String params) {
        prgDialog2.show();
        String endpoint = "transfer/intrabank.action";


        String usid = Utility.gettUtilUserId(getActivity());
        String agentid = Utility.gettUtilAgentId(getActivity());


        String urlparams = "";
        try {
            urlparams = SecurityLayer.genURLCBC(params, endpoint, getActivity());
            //SecurityLayer.Log("cbcurl",url);
            SecurityLayer.Log("RefURL", urlparams);
            SecurityLayer.Log("refurl", urlparams);
            SecurityLayer.Log("params", params);
        } catch (Exception e) {
            SecurityLayer.Log("encryptionerror", e.toString());
        }


        ApiInterface apiService =
                ApiSecurityClient.getClient(getActivity()).create(ApiInterface.class);


        Call<String> call = apiService.setGenericRequestRaw(urlparams);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    // JSON Object


                    SecurityLayer.Log("Intra Bank Resp", response.body());
                    SecurityLayer.Log("response..:", response.body());
                    JSONObject obj = new JSONObject(response.body());
                    //obj = Utility.onresp(obj,getActivity());
                    obj = SecurityLayer.decryptTransaction(obj, getActivity());
                    SecurityLayer.Log("decrypted_response", obj.toString());


                    JSONObject datas = obj.optJSONObject("data");
                    //session.setString(SecurityLayer.KEY_APP_ID,appid);

                    if (!(response.body() == null)) {
                        String respcode = obj.optString("responseCode");
                        String responsemessage = obj.optString("message");
                        String agcmsn = obj.optString("fee");
                        String refcodee = obj.optString("commission");
                        SecurityLayer.Log("Response Message", responsemessage);

                        if (Utility.isNotNull(respcode) && Utility.isNotNull(respcode)) {
                            if (!(Utility.checkUserLocked(respcode))) {
                                if (respcode.equals("00")) {
                                    String totfee = "0.00";
                                    if (!(datas == null)) {
                                        totfee = datas.optString("fee");
                                    }

                                    Bundle b = new Bundle();
                                    b.putString("recanno", recanno);
                                    b.putString("amou", amou);
                              //      String refcodee = datas.optString("referenceCode");
                                    b.putString("refcode", refcodee);
                                    b.putString("narra", narra);
                                    b.putString("ednamee", ednamee);
                                    b.putString("ednumbb", ednumbb);
                                    b.putString("txtname", txtname);
                                    b.putString("agcmsn", agcmsn);
                                    b.putString("fee", totfee);
                                    b.putString("trantype", "D");
                                    Fragment fragment = new FinalConfDepoTrans();

                                    fragment.setArguments(b);
                                    FragmentManager fragmentManager = getFragmentManager();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    //  String tag = Integer.toString(title);
                                    fragmentTransaction.replace(R.id.container_body, fragment, "Confirm Transfer");
                                    fragmentTransaction.addToBackStack("Confirm Transfer");
                                    ((FMobActivity) getActivity())
                                            .setActionBarTitle("Confirm Transfer");
                                    fragmentTransaction.commit();
                                }

                                else if(respcode.equals("002")){
                                    Toast.makeText(
                                            getActivity(), responsemessage,
                                            Toast.LENGTH_LONG).show();


                                 setAlertDialog();

                                 //   ((FMobActivity)getActivity()).showWrongPinDialog(serv);
                                }else {
                                    new MaterialDialog.Builder(getActivity())
                                            .title("Error")
                                            .content(responsemessage)

                                            .negativeText("Dismiss")
                                            .callback(new MaterialDialog.ButtonCallback() {
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

                                    txstatus.setText("TRANSACTION FAILURE");
                                    txdesc.setText(responsemessage);
                                   /* Bundle params = new Bundle();
                                    params.putString("deposit_error", responsemessage);
                                    params.putString("response_code", respcode);
                                    mFirebaseAnalytics.logEvent("cash_deposit", params);

                                    Answers.getInstance().logCustom(new CustomEvent("cash_deposit error code")

                                            .putCustomAttribute("deposit_error", responsemessage)
                                            .putCustomAttribute("response_code", respcode)
                                    );*/

                                 /*   Toast.makeText(
                                            getActivity(),
                                            "" + responsemessage,
                                            Toast.LENGTH_LONG).show();*/
                                }
                            } else {
                              /*  getActivity().finish();
                                startActivity(new Intent(getActivity(), SignInActivity.class));
                                Toast.makeText(
                                        getActivity(),
                                        "You have been locked out of the app.Please call customer care for further details",
                                        Toast.LENGTH_LONG).show();*/

                                ((FMobActivity) getActivity()).LogOut();
                            }
                        } else {

                            Toast.makeText(
                                    getActivity(),
                                    "There was an error on your request",
                                    Toast.LENGTH_LONG).show();
                            txstatus.setText("TRANSACTION FAILURE");
                            txdesc.setText("There was an error on your request");

                        }
                    } else {

                        Toast.makeText(
                                getActivity(),
                                "There was an error on your request",
                                Toast.LENGTH_LONG).show();


                    }
                    // prgDialog2.dismiss();


                }

                catch (JSONException e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    // TODO Auto-generated catch block
                    Toast.makeText(getActivity(), getActivity().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                    ((FMobActivity) getActivity()).SetForceOutDialog(getString(R.string.forceout),getString(R.string.forceouterr),getActivity());
                    // SecurityLayer.Log(e.toString());

                } catch (Exception e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    ((FMobActivity) getActivity()).SetForceOutDialog(getString(R.string.forceout),getString(R.string.forceouterr),getActivity());
                    // SecurityLayer.Log(e.toString());
                }
                if ((!(getActivity() == null)) && !(prgDialog2 == null) && prgDialog2.isShowing()) {
                    prgDialog2.dismiss();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                // Log error here since request failed

                if (t instanceof SocketTimeoutException) {
                    //   message = "Socket Time out. Please try again.";

                    setDialog("Your request has been received.Please wait shortly for feedback");
                }
                SecurityLayer.Log("throwable error", t.toString());


                Toast.makeText(
                        getActivity(),
                        "There was an error on your request",
                        Toast.LENGTH_LONG).show();


                if ((!(getActivity() == null)) && !(prgDialog2 == null) && prgDialog2.isShowing()) {
                    prgDialog2.dismiss();
                }
                ((FMobActivity) getActivity()).SetForceOutDialog(getString(R.string.forceout),getString(R.string.forceouterr),getActivity());
            }
        });
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




    private void IntraTranBankResp(String params) {
prgDialog2.show();
        String endpoint= "transfer/intrabank.action";


        String usid = Utility.gettUtilUserId(getActivity());
        String agentid = Utility.gettUtilAgentId(getActivity());




        String urlparams = "";
        try {
            urlparams = SecurityLayer.genURLCBC(params,endpoint,getActivity());
            //SecurityLayer.Log("cbcurl",url);
            SecurityLayer.Log("RefURL",urlparams);
            SecurityLayer.Log("refurl", urlparams);
            SecurityLayer.Log("params", params);
        } catch (Exception e) {
            SecurityLayer.Log("encryptionerror",e.toString());
        }





        ApiInterface apiService =
                ApiSecurityClient.getClient(getActivity()).create(ApiInterface.class);


        Call<String> call = apiService.setGenericRequestRaw(urlparams);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    // JSON Object


                    SecurityLayer.Log("Intra Bank Resp", response.body());
                    SecurityLayer.Log("response..:", response.body());
                    JSONObject obj = new JSONObject(response.body());
                    //obj = Utility.onresp(obj,getActivity());
                    obj = SecurityLayer.decryptTransaction(obj, getActivity());
                    SecurityLayer.Log("decrypted_response", obj.toString());




                    JSONObject datas = obj.optJSONObject("data");
                    //session.setString(SecurityLayer.KEY_APP_ID,appid);

                    if(!(response.body() == null)) {
                        String respcode = obj.optString("responseCode");
                        String responsemessage = obj.optString("message");
                        String agcmsn = obj.optString("fee");
                        String refcodee = obj.optString("commission");
                        SecurityLayer.Log("Response Message", responsemessage);
                        SecurityLayer.Log("Response Code", respcode);
                        if (Utility.isNotNull(respcode) && Utility.isNotNull(respcode)) {
                            if (!(Utility.checkUserLocked(respcode))) {
                                if(respcode.equals("00")){
                                    String totfee = "0.00";
                                    if(!(datas == null)){
                                        totfee = datas.optString("fee");
                                    }
                                    Bundle b  = new Bundle();
                                    b.putString("recanno",recanno);
                                    b.putString("amou",amou);
                                    b.putString("narra",narra);
                                //    String refcodee = datas.optString("referenceCode");
                                    b.putString("refcode",refcodee);
                                    b.putString("ednamee",ednamee);
                                    b.putString("ednumbb",ednumbb);
                                    b.putString("txtname",txtname);
                                    b.putString("trantype","T");
                                    b.putString("agcmsn",agcmsn);
                                    b.putString("fee",totfee);
                                    Fragment  fragment = new FinalConfDepoTrans();

                                    fragment.setArguments(b);
                                    FragmentManager fragmentManager = getFragmentManager();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    //  String tag = Integer.toString(title);
                                    fragmentTransaction.replace(R.id.container_body, fragment,"Confirm Transfer");
                                    fragmentTransaction.addToBackStack("Confirm Transfer");
                                    ((FMobActivity)getActivity())
                                            .setActionBarTitle("Confirm Transfer");
                                    fragmentTransaction.commit();
                                }
                                else if(respcode.equals("002")){
                                    Toast.makeText(
                                            getActivity(), responsemessage,
                                            Toast.LENGTH_LONG).show();


                                setAlertDialog();
                                }
                                else {
                                    new MaterialDialog.Builder(getActivity())
                                            .title("Error")
                                            .content(responsemessage)

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
                                    txstatus.setText("TRANSACTION FAILURE");
                                    txdesc.setText(responsemessage);
                                    Bundle params = new Bundle();
                                    params.putString("transfer_error", responsemessage);
                                    params.putString("response_code", respcode);


                                    Answers.getInstance().logCustom(new CustomEvent("cash_deposit error code")

                                            .putCustomAttribute("deposit_error",responsemessage)
                                            .putCustomAttribute("response_code",respcode)
                                    );

                                /*    Toast.makeText(
                                            getActivity(),
                                            "" + responsemessage,
                                            Toast.LENGTH_LONG).show();*/
                                }
                            } else {
                               /* getActivity().finish();
                                startActivity(new Intent(getActivity(), SignInActivity.class));
                                Toast.makeText(
                                        getActivity(),
                                        "You have been locked out of the app.Please call customer care for further details",
                                        Toast.LENGTH_LONG).show();*/

                                ((FMobActivity) getActivity()).LogOut();
                            }
                        } else {

                            Toast.makeText(
                                    getActivity(),
                                    "There was an error on your request",
                                    Toast.LENGTH_LONG).show();

                            txstatus.setText("TRANSACTION FAILURE");
                            txdesc.setText("There was an error on your request");
                        }
                    } else {

                        Toast.makeText(
                                getActivity(),
                                "There was an error on your request",
                                Toast.LENGTH_LONG).show();

                        txstatus.setText("TRANSACTION FAILURE");
                        txdesc.setText("There was an error on your request");


                    }
                    // prgDialog2.dismiss();




                } catch (JSONException e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    // TODO Auto-generated catch block
                    Toast.makeText(getActivity(), getActivity().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                    ((FMobActivity) getActivity()).SetForceOutDialog(getString(R.string.forceout),getString(R.string.forceouterr),getActivity());
                    // SecurityLayer.Log(e.toString());

                } catch (Exception e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    ((FMobActivity) getActivity()).SetForceOutDialog(getString(R.string.forceout),getString(R.string.forceouterr),getActivity());
                    // SecurityLayer.Log(e.toString());
                }
                if(!(prgDialog2 == null) && prgDialog2.isShowing() && getActivity() != null) {
                    prgDialog2.dismiss();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                SecurityLayer.Log("throwable error",t.toString());


                Toast.makeText(
                        getActivity(),
                        "There was an error on your request",
                        Toast.LENGTH_LONG).show();


                if(!(prgDialog2 == null) && prgDialog2.isShowing() && getActivity() != null) {
                    prgDialog2.dismiss();
                }
                ((FMobActivity) getActivity()).SetForceOutDialog(getString(R.string.forceout),getString(R.string.forceouterr),getActivity());
            }
        });

    }



    private void WithdrawResp(String params) {
prgDialog2.show();
        String endpoint= "withdrawal/cashbyaccountconfirm.action";


        String usid = Utility.gettUtilUserId(getActivity());
        String agentid = Utility.gettUtilAgentId(getActivity());




        String urlparams = "";
        try {
            urlparams = SecurityLayer.genURLCBC(params,endpoint,getActivity());
            //SecurityLayer.Log("cbcurl",url);
            SecurityLayer.Log("RefURL",urlparams);
            SecurityLayer.Log("refurl", urlparams);
            SecurityLayer.Log("params", params);
        } catch (Exception e) {
            SecurityLayer.Log("encryptionerror",e.toString());
        }





        ApiInterface apiService =
                ApiSecurityClient.getClient(getActivity()).create(ApiInterface.class);


        Call<String> call = apiService.setGenericRequestRaw(urlparams);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    // JSON Object


                    SecurityLayer.Log("Intra Bank Resp", response.body());
                    SecurityLayer.Log("response..:", response.body());
                    JSONObject obj = new JSONObject(response.body());
                    //obj = Utility.onresp(obj,getActivity());
                    obj = SecurityLayer.decryptTransaction(obj, getActivity());
                    SecurityLayer.Log("decrypted_response", obj.toString());




                    JSONObject datas = obj.optJSONObject("data");
                    //session.setString(SecurityLayer.KEY_APP_ID,appid);

                    if(!(response.body() == null)) {
                        String respcode = obj.optString("responseCode");
                        String responsemessage = obj.optString("message");
                        String agcmsn = obj.optString("fee");
                        String refcodee = obj.optString("commission");
                        SecurityLayer.Log("Response Message", responsemessage);

                        if (Utility.isNotNull(respcode) && Utility.isNotNull(respcode)) {
                            if (!(Utility.checkUserLocked(respcode))) {
                                SecurityLayer.Log("Response Message", responsemessage);
                               /* Toast.makeText(
                                        getActivity(),
                                        "" + responsemessage,
                                        Toast.LENGTH_LONG).show();*/
                                if (respcode.equals("00")) {
                                    String totfee = "0.00";
                                    if (!(datas == null)) {
                                        totfee = datas.optString("fee");
                                    }
                                    Bundle b = new Bundle();
                                    b.putString("recanno", recanno);
                                    b.putString("amou", amou);


                                    b.putString("txtname", txtname);
                                    b.putString("txref", txref);
                                    b.putString("agcmsn", agcmsn);
                                //    String refcodee = datas.optString("referenceCode");
                                    b.putString("refcode",refcodee);
                                    b.putString("fee", totfee);
                                    Fragment fragment = new FinalConfWithdraw();

                                    fragment.setArguments(b);
                                    FragmentManager fragmentManager = getFragmentManager();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    //  String tag = Integer.toString(title);
                                    fragmentTransaction.replace(R.id.container_body, fragment, "Final Confirm Withdrawal");
                                    fragmentTransaction.addToBackStack("Final Confirm Withdrawal");
                                    ((FMobActivity) getActivity())
                                            .setActionBarTitle("Final Confirm Withdrawal");
                                    fragmentTransaction.commit();
                                }

                                else if(respcode.equals("002")){
                                    Toast.makeText(
                                            getActivity(), responsemessage,
                                            Toast.LENGTH_LONG).show();


                                 setAlertDialog();
                                }else{
                                    setDialog(responsemessage);
                                    txstatus.setText("TRANSACTION FAILURE");
                                    txdesc.setText(responsemessage);

                                    Answers.getInstance().logCustom(new CustomEvent("withdrawal error code")

                                            .putCustomAttribute("error_msg",responsemessage)
                                            .putCustomAttribute("response_code",respcode)
                                    );
                                }
                            } else {
                               /* getActivity().finish();
                                startActivity(new Intent(getActivity(), SignInActivity.class));
                                Toast.makeText(
                                        getActivity(),
                                        "You have been locked out of the app.Please call customer care for further details",
                                        Toast.LENGTH_LONG).show();*/

                                ((FMobActivity) getActivity()).LogOut();

                            }
                        } else {

                            Toast.makeText(
                                    getActivity(),
                                    "There was an error on your request",
                                    Toast.LENGTH_LONG).show();
                            txstatus.setText("TRANSACTION FAILURE");
                            txdesc.setText("There was an error on your request");


                        }
                    } else {

                        Toast.makeText(
                                getActivity(),
                                "There was an error on your request",
                                Toast.LENGTH_LONG).show();
                        txstatus.setText("TRANSACTION FAILURE");
                        txdesc.setText("There was an error on your request");


                    }
                    // prgDialog2.dismiss();




                } catch (JSONException e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    // TODO Auto-generated catch block
                    Toast.makeText(getActivity(), getActivity().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                    ((FMobActivity) getActivity()).SetForceOutDialog(getString(R.string.forceout),getString(R.string.forceouterr),getActivity());
                    // SecurityLayer.Log(e.toString());

                } catch (Exception e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    ((FMobActivity) getActivity()).SetForceOutDialog(getString(R.string.forceout),getString(R.string.forceouterr),getActivity());
                    // SecurityLayer.Log(e.toString());
                }
                if ((prgDialog2 != null) && prgDialog2.isShowing() && !(getActivity() == null)) {
                    prgDialog2.dismiss();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                // Log error here since request failed
                SecurityLayer.Log("throwable error",t.toString());


                Toast.makeText(
                        getActivity(),
                        "There was an error on your request",
                        Toast.LENGTH_LONG).show();


                if ((prgDialog2 != null) && prgDialog2.isShowing() && !(getActivity() == null)) {
                    prgDialog2.dismiss();
                }
                ((FMobActivity) getActivity()).SetForceOutDialog(getString(R.string.forceout),getString(R.string.forceouterr),getActivity());
            }
        });

    }




    private void InterBankResp(String params) {
prgDialog2.show();
        String endpoint= "transfer/interbank.action";


        String usid = Utility.gettUtilUserId(getActivity());
        String agentid = Utility.gettUtilAgentId(getActivity());




        String urlparams = "";
        try {
            urlparams = SecurityLayer.genURLCBC(params,endpoint,getActivity());
            //SecurityLayer.Log("cbcurl",url);
            SecurityLayer.Log("RefURL",urlparams);
            SecurityLayer.Log("refurl", urlparams);
            SecurityLayer.Log("params", params);
        } catch (Exception e) {
            SecurityLayer.Log("encryptionerror",e.toString());
        }





        ApiInterface apiService =
                ApiSecurityClient.getClient(getActivity()).create(ApiInterface.class);


        Call<String> call = apiService.setGenericRequestRaw(urlparams);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    // JSON Object


                    SecurityLayer.Log("Inter Bank Resp", response.body());
                    SecurityLayer.Log("response..:", response.body());
                    JSONObject obj = new JSONObject(response.body());
                    //obj = Utility.onresp(obj,getActivity());
                    obj = SecurityLayer.decryptTransaction(obj, getActivity());
                    SecurityLayer.Log("decrypted_response", obj.toString());




                    JSONObject datas = obj.optJSONObject("data");
                    //session.setString(SecurityLayer.KEY_APP_ID,appid);
                    if(!(response.body() == null)) {
                        String respcode = obj.optString("responseCode");
                        String responsemessage = obj.optString("message");
                        String agcmsn = obj.optString("fee");
                        String refcodee = obj.optString("commission");
                        SecurityLayer.Log("Response Message", responsemessage);


                        if (Utility.isNotNull(respcode) && Utility.isNotNull(respcode)) {
                            if (!(Utility.checkUserLocked(respcode))) {
                                if (respcode.equals("00")) {
                                    String totfee = "0.00";
                                    if(!(datas == null)){
                                        totfee = datas.optString("fee");
                                    }




                                    Bundle b = new Bundle();
                                    b.putString("recanno", recanno);
                                    b.putString("amou", amou);
                                    b.putString("narra", narra);
                                    b.putString("ednamee", ednamee);
                                    b.putString("ednumbb", ednumbb);
                                    b.putString("txtname", txtname);
                                    b.putString("bankname", bankname);
                                    b.putString("bankcode", bankcode);
                               //     String refcodee = datas.optString("referenceCode");
                                    b.putString("refcode",refcodee);
                                    b.putString("agcmsn",agcmsn);
                                    b.putString("fee",totfee);
                                    Fragment fragment = new FinalConfSendOTB();

                                    fragment.setArguments(b);
                                    FragmentManager fragmentManager = getFragmentManager();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    //  String tag = Integer.toString(title);
                                    fragmentTransaction.replace(R.id.container_body, fragment, "Confirm Other Bank");
                                    fragmentTransaction.addToBackStack("Confirm Other Bank");
                                    ((FMobActivity) getActivity())
                                            .setActionBarTitle("Confirm Other Bank");
                                    fragmentTransaction.commit();
                                }   else if(respcode.equals("002")){
                                    Toast.makeText(
                                            getActivity(), responsemessage,
                                            Toast.LENGTH_LONG).show();


                                   setAlertDialog();
                                }else {
                                   setDialog(responsemessage);
                                    txstatus.setText("TRANSACTION FAILURE");
                                    txdesc.setText(responsemessage);

                                    Answers.getInstance().logCustom(new CustomEvent("interbank error code")

                                            .putCustomAttribute("error_msg",responsemessage)
                                            .putCustomAttribute("response_code",respcode)
                                    );
                                }
                            } else {
                                /*getActivity().finish();
                                startActivity(new Intent(getActivity(), SignInActivity.class));
                                Toast.makeText(
                                        getActivity(),
                                        "You have been locked out of the app.Please call customer care for further details",
                                        Toast.LENGTH_LONG).show();*/
                                ((FMobActivity) getActivity()).LogOut();
                            }
                        } else {
                            Toast.makeText(
                                    getActivity(),
                                    "There was an error on your request",
                                    Toast.LENGTH_LONG).show();
                            txstatus.setText("TRANSACTION FAILURE");
                            txdesc.setText("There was an error on your request");

                        }
                    }else{
                        Toast.makeText(
                                getActivity(),
                                "There was an error on your request",
                                Toast.LENGTH_LONG).show();
                        txstatus.setText("TRANSACTION FAILURE");
                        txdesc.setText("There was an error on your request");
                    }

                    // prgDialog2.dismiss();




                } catch (JSONException e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    // TODO Auto-generated catch block
                    Toast.makeText(getActivity(), getActivity().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                    ((FMobActivity) getActivity()).SetForceOutDialog(getString(R.string.forceout),getString(R.string.forceouterr),getActivity());
                    // SecurityLayer.Log(e.toString());

                } catch (Exception e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    ((FMobActivity) getActivity()).SetForceOutDialog(getString(R.string.forceout),getString(R.string.forceouterr),getActivity());
                    // SecurityLayer.Log(e.toString());
                }
                if((!(getActivity() == null)) && !(prgDialog2 == null) && prgDialog2.isShowing()) {
                    prgDialog2.dismiss();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                SecurityLayer.Log("throwable error",t.toString());


                Toast.makeText(
                        getActivity(),
                        "There was an error on your request",
                        Toast.LENGTH_LONG).show();


                if((!(getActivity() == null)) && !(prgDialog2 == null) && prgDialog2.isShowing()) {
                    prgDialog2.dismiss();
                }
                ((FMobActivity) getActivity()).SetForceOutDialog(getString(R.string.forceout),getString(R.string.forceouterr),getActivity());
            }
        });

    }




    private void PayBillResp(String params) {
prgDialog2.show();
        String endpoint = "billpayment/dobillpayment.action";


        String usid = Utility.gettUtilUserId(getActivity());
        String agentid = Utility.gettUtilAgentId(getActivity());


        String urlparams = "";
        try {
            urlparams = SecurityLayer.genURLCBC(params, endpoint, getActivity());
            //SecurityLayer.Log("cbcurl",url);
            SecurityLayer.Log("RefURL", urlparams);
            SecurityLayer.Log("refurl", urlparams);
            SecurityLayer.Log("params", params);
        } catch (Exception e) {
            SecurityLayer.Log("encryptionerror", e.toString());
        }


        ApiInterface apiService =
                ApiSecurityClient.getClient(getActivity()).create(ApiInterface.class);


        Call<String> call = apiService.setGenericRequestRaw(urlparams);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    // JSON Object


                    SecurityLayer.Log("Cable TV Resp", response.body());
                    SecurityLayer.Log("response..:", response.body());
                    JSONObject obj = new JSONObject(response.body());
                    //obj = Utility.onresp(obj,getActivity());
                    obj = SecurityLayer.decryptTransaction(obj, getActivity());
                    SecurityLayer.Log("decrypted_response", obj.toString());


                    JSONObject datas = obj.optJSONObject("data");
                    //session.setString(SecurityLayer.KEY_APP_ID,appid);

                    if (!(response.body() == null)) {
                        String respcode = obj.optString("responseCode");
                        String responsemessage = obj.optString("message");
                        String agcmsn = obj.optString("fee");
                        String refcodee = obj.optString("commission");
                        SecurityLayer.Log("Response Message", responsemessage);

                        if (Utility.isNotNull(respcode) && Utility.isNotNull(respcode)) {
                            if (!(Utility.checkUserLocked(respcode))) {
                                SecurityLayer.Log("Response Message", responsemessage);
                                Toast.makeText(
                                        getActivity(),
                                        "" + responsemessage,
                                        Toast.LENGTH_LONG).show();
                                if (respcode.equals("00")) {

//                                    SecurityLayer.Log("Respnse getResults",datas.toString());
                                    String totfee = "0.00";
                                    String tref = "N/A";
                                    if (!(datas == null)) {


                                        String ttf = datas.optString("fee");
                                        if (ttf == null || ttf.equals("")) {

                                        } else {
                                            totfee = ttf;
                                        }


                                        tref = datas.optString("refNumber");
                                    }
                                    Bundle b = new Bundle();
                                    b.putString("custid", txtcustid);
                                    b.putString("amou", amou);
                                    b.putString("narra", narra);
                                    b.putString("billname", strbillnm);
                                    b.putString("ednamee", ednamee);
                                    b.putString("ednumbb", ednumbb);
                                    b.putString("billid", billid);
                                    b.putString("serviceid", serviceid);
                                    b.putString("label", strlabel);
                                    b.putString("fullname", fullname);
                                  //  String refcodee = datas.optString("refNumber");
                                    b.putString("refcode", refcodee);
                                    b.putString("tref", tref);
                                    b.putString("agcmsn", agcmsn);
                                    b.putString("fee", totfee);
                                    Fragment fragment = new FinalConfirmCableTV();

                                    fragment.setArguments(b);
                                    FragmentManager fragmentManager = getFragmentManager();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    //  String tag = Integer.toString(title);
                                    fragmentTransaction.replace(R.id.container_body, fragment, "Final Conf Cable");
                                    fragmentTransaction.addToBackStack("Final Conf Cable");
                                    ((FMobActivity) getActivity())
                                            .setActionBarTitle("Final Conf Cable");
                                    fragmentTransaction.commit();
                                }   else if(respcode.equals("002")){
                                    Toast.makeText(
                                            getActivity(), responsemessage,
                                            Toast.LENGTH_LONG).show();

                                   setAlertDialog();
                                }else {
                                    setDialog(responsemessage);
                                    txstatus.setText("TRANSACTION FAILURE");
                                    txdesc.setText(responsemessage);


                                    Answers.getInstance().logCustom(new CustomEvent("paybill error code")

                                            .putCustomAttribute("error_msg",responsemessage)
                                            .putCustomAttribute("response_code",respcode)
                                    );
                                }
                            } else {
                                /*getActivity().finish();
                                startActivity(new Intent(getActivity(), SignInActivity.class));
                                Toast.makeText(
                                        getActivity(),
                                        "You have been locked out of the app.Please call customer care for further details",
                                        Toast.LENGTH_LONG).show();*/

                                ((FMobActivity) getActivity()).LogOut();
                            }
                        } else {

                            Toast.makeText(
                                    getActivity(),
                                    "There was an error on your request",
                                    Toast.LENGTH_LONG).show();
                            txstatus.setText("TRANSACTION FAILURE");
                            txdesc.setText("There was an error on your request");


                        }
                    } else {

                        Toast.makeText(
                                getActivity(),
                                "There was an error on your request",
                                Toast.LENGTH_LONG).show();
                        txstatus.setText("TRANSACTION FAILURE");
                        txdesc.setText("There was an error on your request");


                    }
                    // prgDialog2.dismiss();


                } catch (JSONException e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());

                    // TODO Auto-generated catch block
                    Toast.makeText(getActivity(), getActivity().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                    // SecurityLayer.Log(e.toString());

                    txstatus.setText("TRANSACTION FAILURE");
                    txdesc.setText("There was an error on your request.Please retry");
                    ((FMobActivity) getActivity()).SetForceOutDialog(getString(R.string.forceout),getString(R.string.forceouterr),getActivity());

                } catch (Exception e) {

                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    ((FMobActivity) getActivity()).SetForceOutDialog(getString(R.string.forceout),getString(R.string.forceouterr),getActivity());
                    // SecurityLayer.Log(e.toString());
                }
                if ((prgDialog2 != null) && prgDialog2.isShowing() && !(getActivity() == null)) {
                    prgDialog2.dismiss();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                // Log error here since request failed
                SecurityLayer.Log("throwable error", t.toString());
                if (t instanceof SocketTimeoutException) {

                    SecurityLayer.Log("socket timeout error", t.toString());
                }


                Toast.makeText(
                        getActivity(),
                        "There was an error on your request",
                        Toast.LENGTH_LONG).show();

                if ((prgDialog2 != null) && prgDialog2.isShowing() && !(getActivity() == null)) {
                    prgDialog2.dismiss();
                }
                ((FMobActivity) getActivity()).SetForceOutDialog(getString(R.string.forceout),getString(R.string.forceouterr),getActivity());
            }
        });

    }





    private void AirtimeResp(String params) {

            prgDialog2.show();

        String endpoint = "billpayment/mobileRecharge.action";


        String usid = Utility.gettUtilUserId(getActivity());
        String agentid = Utility.gettUtilAgentId(getActivity());



        String urlparams = "";
        try {
            urlparams = SecurityLayer.genURLCBC(params, endpoint, getActivity());
            //SecurityLayer.Log("cbcurl",url);
            SecurityLayer.Log("RefURL", urlparams);
            SecurityLayer.Log("refurl", urlparams);
            SecurityLayer.Log("params", params);
        } catch (Exception e) {
            SecurityLayer.Log("encryptionerror", e.toString());
        }


        ApiInterface apiService =
                ApiSecurityClient.getClient(getActivity()).create(ApiInterface.class);


        Call<String> call = apiService.setGenericRequestRaw(urlparams);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    // JSON Object


                    SecurityLayer.Log("Intra Bank Resp", response.body());
                    SecurityLayer.Log("response..:", response.body());
                    JSONObject obj = new JSONObject(response.body());
                    //obj = Utility.onresp(obj,getActivity());
                    obj = SecurityLayer.decryptTransaction(obj, getActivity());
                    SecurityLayer.Log("decrypted_response", obj.toString());


                    //session.setString(SecurityLayer.KEY_APP_ID,appid);

                    if (!(response.body() == null)) {
                        String respcode = obj.optString("responseCode");
                        String responsemessage = obj.optString("message");
                        String agcmsn = obj.optString("fee");
                        String refcodee = obj.optString("commission");
                        JSONObject datas = obj.optJSONObject("data");
                        SecurityLayer.Log("Response Message", responsemessage);

                        if (Utility.isNotNull(respcode) && Utility.isNotNull(respcode)) {
                            if (!(Utility.checkUserLocked(respcode))) {
                                SecurityLayer.Log("Response Message", responsemessage);
                               /* Toast.makeText(
                                        getActivity(),
                                        "" + responsemessage,
                                        Toast.LENGTH_LONG).show();*/
                                if (respcode.equals("00")) {

//                                    SecurityLayer.Log("Respnse getResults",datas.toString());
                                    if (!(datas == null)) {

                                        String totfee = "0.00";

                                        String ttf = datas.optString("fee");
                                        if (ttf == null || ttf.equals("")) {

                                        } else {
                                            totfee = ttf;
                                        }

                                        String tref = datas.optString("refNumber");


                                        Bundle b = new Bundle();
                                        b.putString("mobno", txtcustid);
                                        b.putString("amou", amou);
                                        b.putString("telcoop", telcoop);
                                      //  String refcodee = datas.optString("refNumber");
                                        b.putString("refcode", refcodee);
                                        b.putString("billid", billid);
                                        b.putString("serviceid", serviceid);
                                        b.putString("agcmsn", agcmsn);
                                        b.putString("fee", totfee);
                                        b.putString("tref", tref);
                                        Fragment fragment = new FinalConfAirtime();

                                        fragment.setArguments(b);
                                        FragmentManager fragmentManager = getFragmentManager();
                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                        //  String tag = Integer.toString(title);
                                        fragmentTransaction.replace(R.id.container_body, fragment, "Final Conf Airtime");
                                        fragmentTransaction.addToBackStack("Final Conf");
                                        ((FMobActivity) getActivity())
                                                .setActionBarTitle("Final Conf Airtime");
                                        fragmentTransaction.commit();
                                    }
                                }    else if(respcode.equals("002")){
                                    Toast.makeText(
                                            getActivity(), responsemessage,
                                            Toast.LENGTH_LONG).show();

                                  setAlertDialog();
                                } else {
                                    setDialog(responsemessage);
                                    txstatus.setText("TRANSACTION FAILURE");
                                    txdesc.setText(responsemessage);
                                }
                            } else {
                               /* getActivity().finish();
                                startActivity(new Intent(getActivity(), SignInActivity.class));
                                Toast.makeText(
                                        getActivity(),
                                        "You have been locked out of the app.Please call customer care for further details",
                                        Toast.LENGTH_LONG).show();*/
                                ((FMobActivity) getActivity()).LogOut();
                            }
                        } else {

                            Toast.makeText(
                                    getActivity(),
                                    "There was an error on your request",
                                    Toast.LENGTH_LONG).show();

                            txstatus.setText("TRANSACTION FAILURE");
                            txdesc.setText("There was an error on your request");
                        }
                    } else {

                        Toast.makeText(
                                getActivity(),
                                "There was an error on your request",
                                Toast.LENGTH_LONG).show();
                        txstatus.setText("TRANSACTION FAILURE");
                        txdesc.setText("There was an error on your request");

                    }
                    // prgDialog2.dismiss();


                } catch (JSONException e) {

                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    // TODO Auto-generated catch block
                    Toast.makeText(getActivity(), getActivity().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                    txstatus.setText("TRANSACTION FAILURE");
                    txdesc.setText("There was an error on your request");
                    ((FMobActivity) getActivity()).SetForceOutDialog(getString(R.string.forceout),getString(R.string.forceouterr),getActivity());
                    // SecurityLayer.Log(e.toString());

                } catch (Exception e) {

                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    txstatus.setText("TRANSACTION FAILURE");
                    txdesc.setText("There was an error on your request");
                    ((FMobActivity) getActivity()).SetForceOutDialog(getString(R.string.forceout),getString(R.string.forceouterr),getActivity());
                    // SecurityLayer.Log(e.toString());
                }
                if ((prgDialog2 != null) && prgDialog2.isShowing()) {
                    prgDialog2.dismiss();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                // Log error here since request failed
                SecurityLayer.Log("throwable error", t.toString());

                if (t instanceof SocketTimeoutException) {

                    SecurityLayer.Log("socket timeout error", t.toString());
                    txstatus.setText("TRANSACTION FAILURE");
                    txdesc.setText("There was an error on your request");
                }
                Toast.makeText(
                        getActivity(),
                        "There was an error on your request",
                        Toast.LENGTH_LONG).show();

                txstatus.setText("TRANSACTION FAILURE");
                txdesc.setText("There was an error on your request");
                if ((prgDialog2 != null) && prgDialog2.isShowing()) {
                    prgDialog2.dismiss();
                }
                ((FMobActivity) getActivity()).SetForceOutDialog(getString(R.string.forceout),getString(R.string.forceouterr),getActivity());
            }
        });

    }
/*


    public void setOldAlertDialog(){
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_custom_dialog, null);
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        final EditText mEditTexta = (EditText) alertLayout.findViewById(R.id.txt_your_name);
        alert.setTitle("Incorrect PIN set.Please enter valid PIN");
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);
        alert.setNegativeButton("CLOSE", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().finish();
                startActivity(new Intent(getActivity(), FMobActivity.class));
            }
        });

        alert.setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String pin =  mEditTexta.getText().toString();
                Log.v("Login Pin",pin);
                if(Utility.isNotNull(pin)) {
                    String encrypted = null;


String finalparams = newparams+"/"+encrypted;
                    if(serv.equals("CASHDEPO")) {

                        IntraDepoBankResp(finalparams);

                    }
                    if(serv.equals("CASHTRAN")) {

                        IntraTranBankResp(finalparams);

                    }
                    if(serv.equals("WDRAW")) {


                        WithdrawResp(finalparams);

                    }
                    if(serv.equals("AIRT")) {


                        AirtimeResp(finalparams);

                    }
                    if(serv.equals("SENDOTB")) {

                        InterBankResp(finalparams);

                    }
                    if(serv.equals("CABLETV")) {



                        PayBillResp(finalparams);

                    }

                }else{
                    Toast.makeText(
                            getActivity(),
                            "Please enter a valid value for Attendant PIN",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }
*/

    public void setAlertDialog(){
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.signindialogfrag, null);
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        final EditText mEditTexta = (EditText) alertLayout.findViewById(R.id.txt_your_name);

        pro = new ProgressDialog(getActivity());
        pro.setMessage("Loading...");
        pro.setTitle("");
        pro.setCancelable(false);

        mPinLockView = (PinLockView) alertLayout.findViewById(R.id.pin_lock_view);
        mPinLockView.setPinLockListener(mPinLockListener);
        mIndicatorDots = (IndicatorDots) alertLayout.findViewById(R.id.indicator_dots);

        mPinLockView.attachIndicatorDots(mIndicatorDots);


        mPinLockView.setPinLength(5);
        mPinLockView.setTextColor(getResources().getColor(R.color.fab_material_blue_grey_900));
        alert.setTitle("Incorrect PIN set.Please enter valid PIN");
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);
        alert.setNegativeButton("CLOSE", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().finish();
                startActivity(new Intent(getActivity(), FMobActivity.class));
            }
        });

        alert.setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {


                if(Utility.isNotNull(finpin)) {
                    String encrypted = Utility.getencpin(finpin);
                    SecurityLayer.Log("Enc Pin",encrypted);


                    String finalparams = newparams+"/"+encrypted;
                    dialog.dismiss();
                    if(serv.equals("CASHDEPO")) {

                        IntraDepoBankResp(finalparams);

                    }
                    if(serv.equals("CASHTRAN")) {

                        IntraTranBankResp(finalparams);

                    }
                    if(serv.equals("WDRAW")) {


                        WithdrawResp(finalparams);

                    }
                    if(serv.equals("AIRT")) {


                        AirtimeResp(finalparams);

                    }
                    if(serv.equals("SENDOTB")) {

                        InterBankResp(finalparams);

                    }
                    if(serv.equals("CABLETV")) {



                        PayBillResp(finalparams);

                    }

                }else{
                    Toast.makeText(
                            getActivity(),
                            "Please enter a valid value for Attendant PIN",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }
}
