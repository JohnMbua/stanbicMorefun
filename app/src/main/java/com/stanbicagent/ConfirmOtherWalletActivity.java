package com.stanbicagent;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.ActionBar;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.stanbicagent.R;
import com.afollestad.materialdialogs.MaterialDialog;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import rest.ApiInterface;
import rest.ApiSecurityClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import security.SecurityLayer;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.stanbicagent.ApplicationConstants.CHANNEL_ID;

public class ConfirmOtherWalletActivity extends BaseActivity implements View.OnClickListener {
    TextView wphoneno,recname,recamo,recnarr,recsendnum,recsendnam,recwalletname,txtfee;
    Button btnsub;
    String amou ,narra, ednamee,ednumbb,txtname,walphnno,walletname,walletcode;
    ProgressDialog prgDialog,prgDialog2;
    EditText etpin;
    TextView step1,step2,benefname;
    SessionManagement session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_other_wallet);

        wphoneno = (TextView) findViewById(R.id.textViewcvv);
        etpin = (EditText) findViewById(R.id.pin);
        recwalletname = (TextView) findViewById(R.id.otwallet);
        recamo = (TextView) findViewById(R.id.textViewrrs);
        recnarr = (TextView) findViewById(R.id.textViewrr);

        recsendnam = (TextView) findViewById(R.id.sendnammm);
        recsendnum = (TextView) findViewById(R.id.sendno);
        benefname = (TextView) findViewById(R.id.textViewcbyyname);
        prgDialog2 = new ProgressDialog(this);
        prgDialog2.setMessage("Loading....");
        prgDialog2.setCancelable(false);
        txtfee = (TextView) findViewById(R.id.txtfee);
        btnsub = (Button) findViewById(R.id.button2);
        btnsub.setOnClickListener(this);

        session = new SessionManagement(this);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        // Get the ActionBar here to configure the way it behaves.
        final ActionBar ab = getSupportActionBar();
        //ab.setHomeAsUpIndicator(R.drawable.ic_menu); // set a custom icon for the default home button
        ab.setDisplayShowHomeEnabled(true); // show or hide the default home button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
        ab.setDisplayShowTitleEnabled(false); // disable the default title element here (for centered title)

        Intent intent = getIntent();
        if (intent != null) {


            walletname = intent.getStringExtra("walletname");
            walletcode = intent.getStringExtra("walletcode");
            walphnno = intent.getStringExtra("wphoneno");
            amou = intent.getStringExtra("amou");
            narra = intent.getStringExtra("narra");
            ednamee = intent.getStringExtra("ednamee");
            ednumbb = intent.getStringExtra("ednumbb");
            txtname = intent.getStringExtra("txtname");

            wphoneno.setText(walphnno);
            recwalletname.setText(walletname);
            benefname.setText(txtname);
            recamo.setText(amou);
            recnarr.setText(narra);

            recsendnam.setText(ednamee);
            recsendnum.setText(ednumbb);
            amou = Utility.convertProperNumber(amou);
            getFeeSec();
        }

        step1 = (TextView)findViewById(R.id.tv);
        step1.setOnClickListener(this);
        step2 = (TextView) findViewById(R.id.tv2);
        step2.setOnClickListener(this);
    }



    private void getFeeSec() {
        prgDialog2.show();
        String endpoint= "fee/getfee.action";


        String usid = Utility.gettUtilUserId(getApplicationContext());
        String agentid = Utility.gettUtilAgentId(getApplicationContext());

        String params = CHANNEL_ID+usid+"/"+agentid+"/DEPWALLET/"+amou;
        String urlparams = "";
        try {
            urlparams = SecurityLayer.genURLCBC(params,endpoint,getApplicationContext());
            //SecurityLayer.Log("cbcurl",url);
            SecurityLayer.Log("RefURL",urlparams);
            SecurityLayer.Log("refurl", urlparams);
            SecurityLayer.Log("params", params);
        } catch (Exception e) {
            SecurityLayer.Log("encryptionerror",e.toString());
        }





        ApiInterface apiService =
                ApiSecurityClient.getClient(getApplicationContext()).create(ApiInterface.class);


        Call<String> call = apiService.setGenericRequestRaw(urlparams);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    // JSON Object

                    SecurityLayer.Log("response..:", response.body());
                    JSONObject obj = new JSONObject(response.body());
                    //obj = Utility.onresp(obj,getApplicationContext());
                    obj = SecurityLayer.decryptTransaction(obj, getApplicationContext());
                    SecurityLayer.Log("decrypted_response", obj.toString());

                    String respcode = obj.optString("responseCode");
                    String responsemessage = obj.optString("message");
                    String respfee = obj.optString("fee");


                    //session.setString(SecurityLayer.KEY_APP_ID,appid);


                    if(!(response.body() == null)) {
                        if (respcode.equals("00")) {

                            SecurityLayer.Log("Response Message", responsemessage);

//                                    SecurityLayer.Log("Respnse getResults",datas.toString());
                            if (respfee == null || respfee.equals("")) {
                                txtfee.setText("N/A");
                            } else {
                                respfee = Utility.returnNumberFormat(respfee);
                                txtfee.setText(ApplicationConstants.KEY_NAIRA + respfee);
                            }

                        } else  if (respcode.equals("93")) {
                            Toast.makeText(
                                    getApplicationContext(),
                                    responsemessage,
                                    Toast.LENGTH_LONG).show();
                            onBackPressed();
                           /* Fragment fragment = new SendOtherWallet();
                            String title = "Mobile Money Wallet";

                            FragmentManager fragmentManager = getFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            //  String tag = Integer.toString(title);
                            fragmentTransaction.replace(R.id.container_body, fragment,title);
                            fragmentTransaction.addToBackStack(title);
                            ((FMobActivity)getApplicationContext())
                                    .setActionBarTitle(title);
                            fragmentTransaction.commit();*/


                        }
                        else{
                            btnsub.setVisibility(View.GONE);
                            Toast.makeText(
                                    getApplicationContext(),
                                    responsemessage,
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        txtfee.setText("N/A");
                    }



                } catch (JSONException e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    // TODO Auto-generated catch block
                    Toast.makeText(getApplicationContext(), getApplicationContext().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                    // SecurityLayer.Log(e.toString());
                    SetForceOutDialog(getString(R.string.forceout),getString(R.string.forceouterr),getApplicationContext());


                } catch (Exception e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    SetForceOutDialog(getString(R.string.forceout),getString(R.string.forceouterr),getApplicationContext());

                    // SecurityLayer.Log(e.toString());
                }
                if(!(prgDialog2 == null) && prgDialog2.isShowing()) {
                    prgDialog2.dismiss();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                SecurityLayer.Log("Throwable error",t.toString());
                Toast.makeText(
                        getApplicationContext(),
                        "There was an error processing your request",
                        Toast.LENGTH_LONG).show();
                if(!(prgDialog2 == null) && prgDialog2.isShowing()) {
                    prgDialog2.dismiss();
                }
                SetForceOutDialog(getString(R.string.forceout),getString(R.string.forceouterr),getApplicationContext());

            }
        });

    }



    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.button2) {

            if (Utility.checkInternetConnection(getApplicationContext())) {
                amou = Utility.convertProperNumber(amou);
                String agpin  = etpin.getText().toString();
                if (Utility.isNotNull(walphnno)) {
                    if (Utility.isNotNull(amou)) {
                        if (Utility.isNotNull(narra)) {
                            if (Utility.isNotNull(ednamee)) {
                                if (Utility.isNotNull(ednumbb)) {
                                    if (Utility.isNotNull(agpin)) {

                                        String encrypted = null;
                                        encrypted = Utility.b64_sha256(agpin);
                                        OkHttpClient client = new OkHttpClient();
                                        prgDialog2.show();
                                        String usid = Utility.gettUtilUserId(getApplicationContext());
                                        String agentid = Utility.gettUtilAgentId(getApplicationContext());
                                        String mobnoo = Utility.gettUtilMobno(getApplicationContext());
                                        String params = CHANNEL_ID+usid+"/"+agentid+"/"+mobnoo+"/1/"+amou+"/"+walletcode+"/"+walphnno+"/"+txtname+"/"+narra+"/"+encrypted;
                                        InterBankResp(params);
                                       /* ApiInterface apiService =
                                                ApiClient.getClient().create(ApiInterface.class);
                                        String usid = Utility.gettUtilUserId(getApplicationContext());
                                        String agentid = Utility.gettUtilAgentId(getApplicationContext());
                                        String mobnoo = Utility.gettUtilMobno(getApplicationContext());

                                        Call<InterBank> call = apiService.getInterBankResp("1",usid,agentid,"0000","1",amou,walletcode,walphnno,txtname,narra,encrypted);
                                       //  SecurityLayer.Log("Request",CHANNEL_ID+usid+"/"+agentid+"9493818389/"+CHANNEL_ID+amou+"/"+walletcode+"/"+walphnno+"/"+txtname+"/"+narra+"/"+encrypted);
                                        call.enqueue(new Callback<InterBank>() {
                                            @Override
                                            public void onResponse(Call<InterBank>call, Response<InterBank> response) {
                                                SecurityLayer.Log("Response",response.body().toString());
                                                String responsemessage = response.body().getMessage();
                                                String respcode = response.body().getRespCode();
                                                SecurityLayer.Log("Response Message",responsemessage);

                                                if(Utility.isNotNull(responsemessage) && Utility.isNotNull(respcode)) {
                                                    if(respcode.equals("00")) {
                                                      *//*  Bundle b = new Bundle();
                                                        b.putString("recanno", walphnno);
                                                        b.putString("amou", amou);
                                                        b.putString("narra", narra);
                                                        b.putString("ednamee", ednamee);
                                                        b.putString("ednumbb", ednumbb);
                                                        b.putString("txtname", txtname);
                                                        b.putString("walletname", walletname);
                                                        b.putString("walletcode",walletcode );
                                                        Fragment fragment = new FinalConfSendOTB();

                                                        fragment.setArguments(b);
                                                        FragmentManager fragmentManager = getFragmentManager();
                                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                                        //  String tag = Integer.toString(title);
                                                        fragmentTransaction.replace(R.id.container_body, fragment, "Confirm Other Bank");
                                                        fragmentTransaction.addToBackStack("Confirm Other Bank");
                                                        ((FMobActivity) getApplicationContext())
                                                                .setActionBarTitle("Confirm Other Bank");
                                                        fragmentTransaction.commit();*//*
                                                    }else {
                                                        Toast.makeText(
                                                                getApplicationContext(),
                                                                " " + responsemessage,
                                                                Toast.LENGTH_LONG).show();
                                                    }

                                                }else{
                                                    Toast.makeText(
                                                            getApplicationContext(),
                                                            "There was an error on your request",
                                                            Toast.LENGTH_LONG).show();

                                                }
                                                prgDialog2.dismiss();
                                            }

                                            @Override
                                            public void onFailure(Call<InterBank>call, Throwable t) {
                                                // Log error here since request failed
                                                SecurityLayer.Log("throwable error",t.toString());


                                                Toast.makeText(
                                                        getApplicationContext(),
                                                        "There was an error on your request",
                                                        Toast.LENGTH_LONG).show();

                                                prgDialog2.dismiss();
                                            }
                                        });*/
                                                  /*  sDialog.dismissWithAnimation();
                                                }
                                            })
                                            .show();*/
                                    }  else {
                                        Toast.makeText(
                                                getApplicationContext(),
                                                "Please enter a valid value for Agent PIN",
                                                Toast.LENGTH_LONG).show();
                                    }
                                }  else {
                                    Toast.makeText(
                                            getApplicationContext(),
                                            "Please enter a valid value for Depositor Number",
                                            Toast.LENGTH_LONG).show();
                                }
                            }else {
                                Toast.makeText(
                                        getApplicationContext(),
                                        "Please enter a valid value for Depositor Name",
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Please enter a valid value for Narration",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                    else {
                        Toast.makeText(
                                getApplicationContext(),
                                "Please enter a valid value for Amount",
                                Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(
                            getApplicationContext(),
                            "Please enter a value for Account Number",
                            Toast.LENGTH_LONG).show();
                }
            }

        }
        if (view.getId() == R.id.tv) {
            /*Fragment  fragment = new SendOtherWallet();
            String title = "Send Wallet";


            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment,title);
            fragmentTransaction.addToBackStack(title);
            ((FMobActivity)getApplicationContext())
                    .setActionBarTitle(title);
            fragmentTransaction.commit();*/

            finish();



            Intent intent  = new Intent(ConfirmOtherWalletActivity.this,SendOtherWalletActivity.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);
        }
        if (view.getId() == R.id.tv2) {
          /*  Fragment  fragment = new FTMenu();


            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment,"Confirm Transfer");
            fragmentTransaction.addToBackStack("Confirm Transfer");
            ((FMobActivity)getApplicationContext())
                    .setActionBarTitle("Confirm Transfer");
            fragmentTransaction.commit();*/

            finish();



            Intent intent  = new Intent(ConfirmOtherWalletActivity.this,FTMenuActivity.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);
        }
    }


    @Override
    public void onResume(){
        super.onResume();
        // put your code here...

    }

    private void InterBankResp(String params) {

        String endpoint= "transfer/depwallet.action";


        String usid = Utility.gettUtilUserId(getApplicationContext());
        String agentid = Utility.gettUtilAgentId(getApplicationContext());




        String urlparams = "";
        try {
            urlparams = SecurityLayer.genURLCBC(params,endpoint,getApplicationContext());
            //SecurityLayer.Log("cbcurl",url);
            SecurityLayer.Log("RefURL",urlparams);
            SecurityLayer.Log("refurl", urlparams);
            SecurityLayer.Log("params", params);
        } catch (Exception e) {
            SecurityLayer.Log("encryptionerror",e.toString());
        }





        ApiInterface apiService =
                ApiSecurityClient.getClient(getApplicationContext()).create(ApiInterface.class);


        Call<String> call = apiService.setGenericRequestRaw(urlparams);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    // JSON Object


                    SecurityLayer.Log("Inter Bank Resp", response.body());
                    SecurityLayer.Log("response..:", response.body());
                    JSONObject obj = new JSONObject(response.body());
                    //obj = Utility.onresp(obj,getApplicationContext());
                    obj = SecurityLayer.decryptTransaction(obj, getApplicationContext());
                    SecurityLayer.Log("decrypted_response", obj.toString());




                    JSONObject datas = obj.optJSONObject("data");
                    //session.setString(SecurityLayer.KEY_APP_ID,appid);
                    if(!(response.body() == null)) {
                        String respcode = obj.optString("responseCode");
                        String responsemessage = obj.optString("message");
                        String agcmsn = obj.optString("fee");
                        SecurityLayer.Log("Response Message", responsemessage);


                        if (Utility.isNotNull(respcode) && Utility.isNotNull(respcode)) {
                            if (!(Utility.checkUserLocked(respcode))) {
                                if (respcode.equals("00")) {
                                    String totfee = "0.00";
                                    if(!(datas == null)){
                                        totfee = datas.optString("fee");
                                    }

                                /*    Bundle b = new Bundle();
                                    b.putString("walphno", walphnno);
                                    b.putString("amou", amou);
                                    b.putString("narra", narra);
                                    b.putString("ednamee", ednamee);
                                    b.putString("ednumbb", ednumbb);
                                    b.putString("txtname", txtname);
                                    String refcodee = datas.optString("referenceCode");
                                    b.putString("refcode",refcodee);
                                    b.putString("walletname", walletname);
                                    b.putString("walletcode", walletcode);
                                    b.putString("agcmsn",agcmsn);
                                    b.putString("fee",totfee);
                                    Fragment fragment = new FinalConfOtherWallets();

                                    fragment.setArguments(b);
                                    FragmentManager fragmentManager = getFragmentManager();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    //  String tag = Integer.toString(title);
                                    fragmentTransaction.replace(R.id.container_body, fragment, "Confirm Other Bank");
                                    fragmentTransaction.addToBackStack("Confirm Other Bank");
                                    ((FMobActivity) getApplicationContext())
                                            .setActionBarTitle("Confirm Other Bank");
                                    fragmentTransaction.commit();*/

                                    Intent intent  = new Intent(ConfirmOtherWalletActivity.this,FinalConfOtherWalletsActivity.class);



                                    intent.putExtra("walphno", walphnno);
                                    intent.putExtra("amou", amou);
                                    intent.putExtra("narra", narra);
                                    intent.putExtra("ednamee", ednamee);
                                    intent.putExtra("ednumbb", ednumbb);
                                    intent.putExtra("txtname", txtname);
                                    String refcodee = datas.optString("referenceCode");
                                    intent.putExtra("refcode",refcodee);
                                    intent.putExtra("walletname", walletname);
                                    intent.putExtra("walletcode", walletcode);
                                    intent.putExtra("agcmsn",agcmsn);
                                    intent.putExtra("fee",totfee);;
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(
                                            getApplicationContext(),
                                            " " + responsemessage,
                                            Toast.LENGTH_LONG).show();
                                }
                            } else {
                               finish();
                                startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                                Toast.makeText(
                                        getApplicationContext(),
                                        "You have been locked out of the app.Please call customer care for further details",
                                        Toast.LENGTH_LONG).show();

                            }
                        } else {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "There was an error on your request",
                                    Toast.LENGTH_LONG).show();

                        }
                    }else{
                        Toast.makeText(
                                getApplicationContext(),
                                "There was an error on your request",
                                Toast.LENGTH_LONG).show();
                    }

                    // prgDialog2.dismiss();




                } catch (JSONException e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    // TODO Auto-generated catch block
                    Toast.makeText(getApplicationContext(), getApplicationContext().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                    // SecurityLayer.Log(e.toString());
                   SetForceOutDialog(getString(R.string.forceout),getString(R.string.forceouterr),getApplicationContext());


                } catch (Exception e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    SetForceOutDialog(getString(R.string.forceout),getString(R.string.forceouterr),getApplicationContext());

                    // SecurityLayer.Log(e.toString());
                }
                prgDialog2.dismiss();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                SecurityLayer.Log("throwable error",t.toString());


                Toast.makeText(
                        getApplicationContext(),
                        "There was an error on your request",
                        Toast.LENGTH_LONG).show();

               SetForceOutDialog(getString(R.string.forceout),getString(R.string.forceouterr),getApplicationContext());


                prgDialog2.dismiss();
            }
        });

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();    //Call the back button's method
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public String setMobFormat(String mobno) {
        String vb = mobno.substring(Math.max(0, mobno.length() - 9));
        //SecurityLayer.Log("Logged Number is", vb);
        if (vb.length() == 9 && (vb.substring(0, Math.min(mobno.length(), 1)).equals("7"))) {
            return vb;
        } else {
            return "N";
        }
    }

    public void SetForceOutDialog(String msg, final String title, final Context c) {
        if (!(c == null)) {
            new MaterialDialog.Builder(this)
                    .title(title)
                    .content(msg)

                    .negativeText("CONTINUE")
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            dialog.dismiss();
                        }

                        @Override
                        public void onNegative(MaterialDialog dialog) {

                            dialog.dismiss();
                            finish();
                            session.logoutUser();

                            // After logout redirect user to Loing Activity
                            Intent i = new Intent(c, SignInActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            // Staring Login Activity
                            startActivity(i);

                        }
                    })
                    .show();
        }
    }




    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub


        if(prgDialog2!=null && prgDialog2.isShowing()){

            prgDialog2.dismiss();
        }

        super.onDestroy();
    }

    public  void LogOut(){
        session.logoutUser();

        // After logout redirect user to Loing Activity
        finish();
        Intent i = new Intent(getApplicationContext(), SignInActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // Staring Login Activity
        startActivity(i);
        Toast.makeText(
                getApplicationContext(),
                "You have been locked out of the app.Please call customer care for further details",
                Toast.LENGTH_LONG).show();
        // Toast.makeText(getApplicationContext(), "You have logged out successfully", Toast.LENGTH_LONG).show();

    }
}
