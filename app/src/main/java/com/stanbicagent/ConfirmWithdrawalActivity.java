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

import model.GetFee;
import rest.ApiClient;
import rest.ApiInterface;
import rest.ApiSecurityClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import security.SecurityLayer;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.stanbicagent.ApplicationConstants.CHANNEL_ID;

public class ConfirmWithdrawalActivity extends BaseActivity implements View.OnClickListener {
    TextView recacno,recname,recamo,recnarr,recsendnum,recsendnam,step2,txtfee,acbal;
    Button btnsub;
    String recanno, amou ,txtname,txref,otp,agbalance;
    ProgressDialog prgDialog,prgDialog2;
    EditText etpin;
    SessionManagement session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_withdrawal);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        // Get the ActionBar here to configure the way it behaves.
        final ActionBar ab = getSupportActionBar();
        //ab.setHomeAsUpIndicator(R.drawable.ic_menu); // set a custom icon for the default home button
        ab.setDisplayShowHomeEnabled(true); // show or hide the default home button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
        ab.setDisplayShowTitleEnabled(false); // disable the default title element here (for centered title)
        session = new SessionManagement(this);
        recacno = (TextView) findViewById(R.id.textViewnb2);
        recname = (TextView) findViewById(R.id.textViewcvv);
        acbal = (TextView) findViewById(R.id.txtacbal);
        etpin = (EditText) findViewById(R.id.pin);
        recamo = (TextView) findViewById(R.id.textViewrrs);
        recnarr = (TextView) findViewById(R.id.textViewrr);
        txtfee = (TextView) findViewById(R.id.txtfee);
        recsendnam = (TextView)findViewById(R.id.sendnammm);
        recsendnum = (TextView) findViewById(R.id.sendno);
        prgDialog2 = new ProgressDialog(this);
        prgDialog2.setMessage("Loading....");
        prgDialog2.setCancelable(false);

        btnsub = (Button) findViewById(R.id.button2);
        btnsub.setOnClickListener(this);

        step2 = (TextView) findViewById(R.id.tv2);
        step2.setOnClickListener(this);

        Intent intent = getIntent();
        if (intent != null) {

            recanno = intent.getStringExtra("recanno");


            amou = intent.getStringExtra("amou");

            txtname = intent.getStringExtra("txtname");
            txref = intent.getStringExtra("txref");

            otp = intent.getStringExtra("otp");
            recacno.setText(recanno);
            recname.setText(txtname);

            recamo.setText(amou);
            amou = Utility.convertProperNumber(amou);
            getFeeSec();

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();    //Call the back button's method
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void getFee(){
        if ((prgDialog2 != null)  && !(getApplicationContext() == null)) {
            prgDialog2.show();
        }
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        String usid = Utility.gettUtilUserId(getApplicationContext());
        String agentid = Utility.gettUtilAgentId(getApplicationContext());
        Call<GetFee> call = apiService.getFee("1", usid, agentid, "CWDBYACT",  amou);
        call.enqueue(new Callback<GetFee>() {
            @Override
            public void onResponse(Call<GetFee> call, Response<GetFee> response) {
                String responsemessage = response.body().getMessage();
                String respfee = response.body().getFee();
                SecurityLayer.Log("Response Message", responsemessage);
                if(respfee == null || respfee.equals("")){
                    txtfee.setText("N/A");
                }else{
                    respfee = Utility.returnNumberFormat(respfee);
                    txtfee.setText(ApplicationConstants.KEY_NAIRA+respfee);
                }
                if ((prgDialog2 != null) && prgDialog2.isShowing() && !(getApplicationContext() == null)) {
                    prgDialog2.dismiss();
                }
            }

            @Override
            public void onFailure(Call<GetFee> call, Throwable t) {
                // Log error here since request failed
                SecurityLayer.Log("Throwable error",t.toString());
                Toast.makeText(
                        getApplicationContext(),
                        "There was an error processing your request",
                        Toast.LENGTH_LONG).show();
                if ((prgDialog2 != null) && prgDialog2.isShowing() && !(getApplicationContext() == null)) {
                    prgDialog2.dismiss();
                }
            }
        });
    }
    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.button2) {
            if (Utility.checkInternetConnection(getApplicationContext())) {
                String agpin  = etpin.getText().toString();
                if (Utility.isNotNull(recanno)) {
                    if (Utility.isNotNull(amou)) {
                        if (Utility.isNotNull(agpin)) {
                            if ((prgDialog2 != null)  && !(getApplicationContext() == null)) {
                                // prgDialog2.show();
                            }
                            String encrypted = null;
                            encrypted = Utility.b64_sha256(agpin);



                            String usid = Utility.gettUtilUserId(getApplicationContext());
                            String agentid = Utility.gettUtilAgentId(getApplicationContext());
                            String mobnoo = Utility.gettUtilMobno(getApplicationContext());
                            String params = CHANNEL_ID+ usid+"/"+amou+"/"+txref+"/"+recanno+"/"+txtname+"/Narr/"+otp;

                            Intent intent  = new Intent(ConfirmWithdrawalActivity.this,TransactionProcessingActivity.class);

                            intent.putExtra("recanno", recanno);

                            intent.putExtra("recanno", recanno);
                            intent.putExtra("amou", amou);
                            intent.putExtra("otp", otp);
                            intent.putExtra("txtname", txtname);
                            intent.putExtra("txref", txref);
                            intent.putExtra("params", params);
                            intent.putExtra("txpin", encrypted);
                            intent.putExtra("serv","WDRAW");

                            startActivity(intent);

                            //   WithdrawResp(params);
                          /*  ApiInterface apiService =
                                    ApiClient.getClient().create(ApiInterface.class);
                            //  /agencyapi/app/withdrawal/cashbyaccountconfirm.action/1/CEVA/BATA00000000019493818389/1000/1480430145451/2017812696/AMAGREEMOMINE/androticashcwithdrawal/168697/43211
                            Call<WithdrawalConfirmOTP> call2 = apiService.getConfirmOTP("1", usid, agentid, "0000", amou, txref, recanno, txtname, "Narr", otp, encrypted);
                            call2.enqueue(new Callback<WithdrawalConfirmOTP>() {
                                @Override
                                public void onResponse(Call<WithdrawalConfirmOTP> call, Response<WithdrawalConfirmOTP> response) {
                                    if (!(response.body() == null)) {
                                        String responsemessage = response.body().getMessage();
                                        String respcode = response.body().getRespCode();
                                        String agcmsn = response.body().getFee();
                                        WithdrawalConfirmOTPData datas = response.body().getResults();
                                        if (Utility.isNotNull(respcode) && Utility.isNotNull(responsemessage)) {
                                            if (!(Utility.checkUserLocked(respcode))) {
                                                SecurityLayer.Log("Response Message", responsemessage);
                                                Toast.makeText(
                                                        getApplicationContext(),
                                                        "" + responsemessage,
                                                        Toast.LENGTH_LONG).show();
                                                if (respcode.equals("00")) {
                                                    String totfee = "0.00";
                                                    if (!(datas == null)) {
                                                        totfee = datas.getfee();
                                                    }
                                                    Bundle b = new Bundle();
                                                    b.putString("recanno", recanno);
                                                    b.putString("amou", amou);
                                                    b.putString("otp", otp);
                                                    b.putString("txtname", txtname);
                                                    b.putString("txref", txref);
                                                    b.putString("agcmsn", agcmsn);
                                                    b.putString("fee", totfee);
                                                    Fragment fragment = new FinalConfWithdraw();

                                                    fragment.setArguments(b);
                                                    FragmentManager fragmentManager = getFragmentManager();
                                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                                    //  String tag = Integer.toString(title);
                                                    fragmentTransaction.replace(R.id.container_body, fragment, "Final Confirm Withdrawal");
                                                    fragmentTransaction.addToBackStack("Final Confirm Withdrawal");
                                                    ((FMobActivity) getApplicationContext())
                                                            .setActionBarTitle("Final Confirm Withdrawal");
                                                    fragmentTransaction.commit();
                                                }
                                            } else {
                                                getApplicationContext().finish();
                                                startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                                                Toast.makeText(
                                                        getApplicationContext(),
                                                        "You have been locked out of the app.Please call customer care for further details",
                                                        Toast.LENGTH_LONG).show();

                                            }
                                        }else {

                                            Toast.makeText(
                                                    getApplicationContext(),
                                                    "There was an error on your request",
                                                    Toast.LENGTH_LONG).show();


                                        }
                                    } else {

                                        Toast.makeText(
                                                getApplicationContext(),
                                                "There was an error on your request",
                                                Toast.LENGTH_LONG).show();


                                    }
                                    prgDialog2.dismiss();
                                }

                                @Override
                                public void onFailure(Call<WithdrawalConfirmOTP> call, Throwable t) {
                                    // Log error here since request failed
                                    SecurityLayer.Log("throwable error", t.toString());


                                    Toast.makeText(
                                            getApplicationContext(),
                                            "There was an error on your request",
                                            Toast.LENGTH_LONG).show();


                                    prgDialog2.dismiss();
                                }
                            });*/
                            ClearPin();
                        } else {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Please enter a valid value for Agent PIN",
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
        if (view.getId() == R.id.tv2) {
           /* Fragment  fragment = new Withdraw();


            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment,"Withdraw");
            fragmentTransaction.addToBackStack("Withdraw");
            ((FMobActivity)getApplicationContext())
                    .setActionBarTitle("Withdraw");
            fragmentTransaction.commit();*/

            finish();



            Intent intent  = new Intent(ConfirmWithdrawalActivity.this,WithdrawActivity.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);
        }
    }


    @Override
    public void onResume(){
        super.onResume();
        // put your code here...

    }
    public void ClearPin(){
        etpin.setText("");
    }


    private void getFeeSec() {
        if ((prgDialog2 != null)  && !(getApplicationContext() == null)) {
            prgDialog2.show();
        }
        String endpoint= "fee/getfee.action";


        String usid = Utility.gettUtilUserId(getApplicationContext());
        String agentid = Utility.gettUtilAgentId(getApplicationContext());

        String params = CHANNEL_ID+usid+"/"+agentid+"/CWDBYACT/"+amou;
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
                    agbalance = obj.optString("data");
                    if(Utility.isNotNull(agbalance)) {
                        acbal.setText(Utility.returnNumberFormat(agbalance)+ApplicationConstants.KEY_NAIRA);
                    }

                    //session.setString(SecurityLayer.KEY_APP_ID,appid);

                    if (Utility.isNotNull(respcode) && Utility.isNotNull(respcode)) {
                        if (!(Utility.checkUserLocked(respcode))) {
                            if (!(response.body() == null)) {
                                if (respcode.equals("00")) {

                                    SecurityLayer.Log("Response Message", responsemessage);

//                                    SecurityLayer.Log("Respnse getResults",datas.toString());
                                    if (respfee == null || respfee.equals("")) {
                                        txtfee.setText("N/A");
                                    } else {
                                        respfee = Utility.returnNumberFormat(respfee);
                                        txtfee.setText(ApplicationConstants.KEY_NAIRA + respfee);
                                    }

                                } else if (respcode.equals("93")) {
                                    Toast.makeText(
                                            getApplicationContext(),
                                            responsemessage,
                                            Toast.LENGTH_LONG).show();
                                    onBackPressed();
                                    /*Fragment fragment = new Withdraw();
                                    String title = "Withdraw";

                                    FragmentManager fragmentManager = getFragmentManager();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    //  String tag = Integer.toString(title);
                                    fragmentTransaction.replace(R.id.container_body, fragment, title);
                                    fragmentTransaction.addToBackStack(title);
                                    ((FMobActivity) getApplicationContext())
                                            .setActionBarTitle(title);
                                    fragmentTransaction.commit();*/


                                } else {
                                    btnsub.setVisibility(View.GONE);
                                    Toast.makeText(
                                            getApplicationContext(),
                                            responsemessage,
                                            Toast.LENGTH_LONG).show();
                                }
                            } else {
                                txtfee.setText("N/A");
                            }
                        }else{
                           LogOut();
                        }
                    }


                } catch (JSONException e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    // TODO Auto-generated catch block
                    if(!(getApplicationContext() == null)) {
                        Toast.makeText(getApplicationContext(), getApplicationContext().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                        // SecurityLayer.Log(e.toString());
                        SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getApplicationContext());
                    }

                } catch (Exception e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    if(!(getApplicationContext() == null)) {
                        SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getApplicationContext());
                    }
                    // SecurityLayer.Log(e.toString());
                }
                if ((prgDialog2 != null) && prgDialog2.isShowing() && !(getApplicationContext() == null)) {
                    prgDialog2.dismiss();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                SecurityLayer.Log("Throwable error",t.toString());

                if ((prgDialog2 != null) && prgDialog2.isShowing() && !(getApplicationContext() == null)) {
                    prgDialog2.dismiss();
                }
                if(!(getApplicationContext() == null)) {
                    Toast.makeText(
                            getApplicationContext(),
                            "There was an error processing your request",
                            Toast.LENGTH_LONG).show();
                    SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getApplicationContext());
                }
            }
        });

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
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
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
