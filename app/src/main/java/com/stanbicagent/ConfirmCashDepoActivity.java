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
import com.google.firebase.analytics.FirebaseAnalytics;

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

public class ConfirmCashDepoActivity extends BaseActivity implements View.OnClickListener {
    TextView recacno, recname, recamo, recnarr, txtfee, acbal;
    Button btnsub;
    String recanno, amou, narra, ednamee, ednumbb, txtname, finalfee = null, agbalance;
    ProgressDialog prgDialog2;
    EditText etpin;
    private FirebaseAnalytics mFirebaseAnalytics;
    TextView step1;
    SessionManagement session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_cash_depo);
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
        etpin = (EditText) findViewById(R.id.pin);
        acbal = (TextView) findViewById(R.id.txtacbal);
        recamo = (TextView) findViewById(R.id.textViewrrs);
        recnarr = (TextView) findViewById(R.id.textViewrr);
        txtfee = (TextView) findViewById(R.id.txtfee);
        step1 = (TextView) findViewById(R.id.tv);
        step1.setOnClickListener(this);


        prgDialog2 = new ProgressDialog(this);
        prgDialog2.setMessage("Loading....");
        prgDialog2.setCancelable(false);

        btnsub = (Button) findViewById(R.id.button2);
        btnsub.setOnClickListener(this);

        Intent intent = getIntent();
        if (intent != null) {
            recanno = intent.getStringExtra("recanno");
            amou = intent.getStringExtra("amou");
            narra = intent.getStringExtra("narra");
            ednamee = intent.getStringExtra("ednamee");
            ednumbb = intent.getStringExtra("ednumbb");
            txtname = intent.getStringExtra("txtname");
            recacno.setText(recanno);
            recname.setText(txtname);
            recamo.setText(ApplicationConstants.KEY_NAIRA + amou);
            recnarr.setText(narra);
            amou = Utility.convertProperNumber(amou);
            getFeeSec();

        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void getFee() {
        if (!(prgDialog2 == null)) {
            prgDialog2.show();
        }
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        String usid = Utility.gettUtilUserId(getApplicationContext());
        String agentid = Utility.gettUtilAgentId(getApplicationContext());
        Call<GetFee> call = apiService.getFee("1", usid, agentid, "CASHDEP", amou);
        call.enqueue(new Callback<GetFee>() {
            @Override
            public void onResponse(Call<GetFee> call, Response<GetFee> response) {
                if (!(response.body() == null)) {
                    String responsemessage = response.body().getMessage();
                    String respfee = response.body().getFee();
                    SecurityLayer.Log("Response Message", responsemessage);
                    if (respfee == null || respfee.equals("")) {
                        txtfee.setText("N/A");
                    } else {
                        finalfee = respfee;
                        respfee = Utility.returnNumberFormat(respfee);
                        txtfee.setText(ApplicationConstants.KEY_NAIRA + respfee);
                    }

                } else {
                    txtfee.setText("N/A");
                }
                if ((!(getApplicationContext() == null)) && !(prgDialog2 == null) && prgDialog2.isShowing()) {
                    prgDialog2.dismiss();
                }
            }

            @Override
            public void onFailure(Call<GetFee> call, Throwable t) {
                // Log error here since request failed
                SecurityLayer.Log("Throwable error", t.toString());
                Toast.makeText(
                        getApplicationContext(),
                        "There was an error processing your request",
                        Toast.LENGTH_LONG).show();
                if ((!(getApplicationContext() == null)) && !(prgDialog2 == null) && prgDialog2.isShowing()) {
                    prgDialog2.dismiss();
                }
            }

        });
    }

    private void getFeeSec() {
        if (!(prgDialog2 == null)) {
            prgDialog2.show();
        }
        String endpoint = "fee/getfee.action";


        String usid = Utility.gettUtilUserId(getApplicationContext());
        String agentid = Utility.gettUtilAgentId(getApplicationContext());

        String params = CHANNEL_ID + usid + "/" + agentid + "/CASHDEP/" + amou;
        String urlparams = "";
        try {
            urlparams = SecurityLayer.genURLCBC(params, endpoint, getApplicationContext());
            //SecurityLayer.Log("cbcurl",url);
            SecurityLayer.Log("RefURL", urlparams);
            SecurityLayer.Log("refurl", urlparams);
            SecurityLayer.Log("params", params);
        } catch (Exception e) {
            SecurityLayer.Log("encryptionerror", e.toString());
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
                    if (Utility.isNotNull(agbalance)) {
                        acbal.setText(agbalance + ApplicationConstants.KEY_NAIRA);
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
                                        finalfee = respfee;
                                        respfee = Utility.returnNumberFormat(respfee);
                                        txtfee.setText(ApplicationConstants.KEY_NAIRA + respfee);
                                    }

                                } else if (respcode.equals("93")) {
                                    Toast.makeText(
                                            getApplicationContext(),
                                            responsemessage,
                                            Toast.LENGTH_LONG).show();

                                    onBackPressed();


                                } else {
                                    if (!(Utility.checkUserLocked(respcode))) {


                                        btnsub.setVisibility(View.GONE);
                                        Toast.makeText(
                                                getApplicationContext(),
                                                responsemessage,
                                                Toast.LENGTH_LONG).show();
                                    } else {
                                        LogOut();
                                    }
                                }
                            } else {
                                txtfee.setText("N/A");
                            }

                        } else {
                            LogOut();
                        }
                    }

                } catch (JSONException e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    // TODO Auto-generated catch block
                    if (!(getApplicationContext() == null)) {
                        Toast.makeText(getApplicationContext(), getApplicationContext().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                        // SecurityLayer.Log(e.toString());
                        SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getApplicationContext());
                    }

                } catch (Exception e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    if (!(getApplicationContext() == null)) {
                        SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getApplicationContext());
                    }
                    // SecurityLayer.Log(e.toString());
                }
                if ((!(getApplicationContext() == null)) && !(prgDialog2 == null) && prgDialog2.isShowing()) {
                    prgDialog2.dismiss();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                SecurityLayer.Log("Throwable error", t.toString());

                if ((!(getApplicationContext() == null)) && !(prgDialog2 == null) && prgDialog2.isShowing()) {

                    prgDialog2.dismiss();
                    Toast.makeText(
                            getApplicationContext(),
                            "There was an error processing your request",
                            Toast.LENGTH_LONG).show();
                    SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getApplicationContext());

                }

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();    //Call the back button's method
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.button2) {
            if (Utility.checkInternetConnection(getApplicationContext())) {
                String agpin = etpin.getText().toString();
                if (Utility.isNotNull(recanno)) {
                    if (Utility.isNotNull(amou)) {
                        if (Utility.isNotNull(narra)) {
                            if (Utility.isNotNull(ednamee)) {
                                if (Utility.isNotNull(ednumbb)) {
                                    if (Utility.isNotNull(agpin)) {
                                        if (!(finalfee == null)) {

                                            String encrypted = null;
                                            String pubkey = "97206B46CE46376894703ECE161F31F2";
                                            encrypted = Utility.b64_sha256(agpin);

                                            ApiInterface apiService =
                                                    ApiClient.getClient().create(ApiInterface.class);
                                            String usid = Utility.gettUtilUserId(getApplicationContext());
                                            String agentid = Utility.gettUtilAgentId(getApplicationContext());
                                            String mobnoo = Utility.gettUtilMobno(getApplicationContext());
                                            // "0000"
                                            String channelid = Long.toString(System.nanoTime());
                                            String params = CHANNEL_ID + usid + "/" + "2/" + amou + "/" + recanno + "/" + txtname + "/" + narra + "/NA/NA/" + channelid;

                                            Intent intent = new Intent(ConfirmCashDepoActivity.this, TransactionProcessingActivity.class);
                                            Bundle b = new Bundle();
                                            intent.putExtra("params", params);
                                            intent.putExtra("serv", "CASHDEPO");
                                            intent.putExtra("recanno", recanno);
                                            intent.putExtra("amou", amou);

                                            intent.putExtra("narra", narra);
                                            intent.putExtra("ednamee", ednamee);
                                            intent.putExtra("ednumbb", ednumbb);
                                            intent.putExtra("txtname", txtname);
                                            intent.putExtra("txpin", encrypted);
                                            startActivity(intent);
                                            //    Fragment  fragment = new TransactingProcessing();

                                               /* fragment.setArguments(b);
                                                FragmentManager fragmentManager = getFragmentManager();
                                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                                //  String tag = Integer.toString(title);
                                                fragmentTransaction.replace(R.id.container_body, fragment,"Confirm Transfer");
                                                fragmentTransaction.addToBackStack("Confirm Transfer");
                                                ((FMobActivity)getApplicationContext())
                                                        .setActionBarTitle("Confirm Transfer");
                                                fragmentTransaction.commit();*/
                                            //    IntraBankResp(params);
                                      /*              Call<IntraBank> call = apiService.getIntraBankResp("1",usid,agentid,"0000","2",amou,recanno,txtname,narra,encrypted);
                                                    call.enqueue(new Callback<IntraBank>() {
                                                        @Override
                                                        public void onResponse(Call<IntraBank>call, Response<IntraBank> response) {

                                                        if(!(response.body() == null)) {
                                                            String responsemessage = response.body().getMessage();
                                                            String respcode = response.body().getRespCode();
                                                            String agcmsn = response.body().getFee();
                                                            SecurityLayer.Log("Response Message", responsemessage);
                                                            IntraBankData datas = response.body().getResults();
                                                            if (Utility.isNotNull(respcode) && Utility.isNotNull(respcode)) {
                                                                if (!(Utility.checkUserLocked(respcode))) {
                                                            if(respcode.equals("00")){
                                                                String totfee = "0.00";
                                                                if(!(datas == null)){
                                                                    totfee = datas.getfee();
                                                                }
                                                                Bundle b  = new Bundle();
                                                                b.putString("recanno",recanno);
                                                                b.putString("amou",amou);
                                                                b.putString("narra",narra);
                                                                b.putString("ednamee",ednamee);
                                                                b.putString("ednumbb",ednumbb);
                                                                b.putString("txtname",txtname);
                                                                b.putString("agcmsn",agcmsn);
                                                                b.putString("fee",totfee);
                                                                b.putString("trantype","D");
                                                                Fragment  fragment = new FinalConfDepoTrans();

                                                                fragment.setArguments(b);
                                                                FragmentManager fragmentManager = getFragmentManager();
                                                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                                                //  String tag = Integer.toString(title);
                                                                fragmentTransaction.replace(R.id.container_body, fragment,"Confirm Transfer");
                                                                fragmentTransaction.addToBackStack("Confirm Transfer");
                                                                ((FMobActivity)getApplicationContext())
                                                                        .setActionBarTitle("Confirm Transfer");
                                                                fragmentTransaction.commit();
                                                            }else {


                                                                Toast.makeText(
                                                                        getApplicationContext(),
                                                                        "" + responsemessage,
                                                                        Toast.LENGTH_LONG).show();
                                                            }
                                                                } else {
                                                                    getApplicationContext().finish();
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
                                                        } else {

                                                            Toast.makeText(
                                                                    getApplicationContext(),
                                                                    "There was an error on your request",
                                                                    Toast.LENGTH_LONG).show();


                                                        }
                                                            prgDialog2.dismiss();
                                                        }

                                                        @Override
                                                        public void onFailure(Call<IntraBank> call, Throwable t) {
                                                            // Log error here since request failed
                                                            SecurityLayer.Log("throwable error",t.toString());


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
                                                    "Please ensure fee modules are set up appropiately",
                                                    Toast.LENGTH_LONG).show();
                                        }

                                    } else {
                                        Toast.makeText(
                                                getApplicationContext(),
                                                "Please enter a valid value for Agent PIN",
                                                Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(
                                            getApplicationContext(),
                                            "Please enter a valid value for Depositor Number",
                                            Toast.LENGTH_LONG).show();
                                }
                            } else {
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
                    } else {
                        Toast.makeText(
                                getApplicationContext(),
                                "Please enter a valid value for Amount",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(
                            getApplicationContext(),
                            "Please enter a value for Account Number",
                            Toast.LENGTH_LONG).show();
                }
            }
        }
        if (view.getId() == R.id.tv) {
         /*   Fragment  fragment = new CashDepo();


            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment,"Cash Depo");
            fragmentTransaction.addToBackStack("Cash Depo");
            ((FMobActivity)getApplicationContext())
                    .setActionBarTitle("Cash Depo");
            fragmentTransaction.commit();*/


            finish();


            Intent intent = new Intent(ConfirmCashDepoActivity.this, CashDepoActivity.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);
        }
        if (view.getId() == R.id.tv2) {
           /* Fragment  fragment = new FTMenu();


            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment,"Confirm Transfer");
            fragmentTransaction.addToBackStack("Confirm Transfer");
            ((FMobActivity)getApplicationContext())
                    .setActionBarTitle("Confirm Transfer");
            fragmentTransaction.commit();*/
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        // put your code here...


    }

    public void ClearPin() {
        etpin.setText("");
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

    public void setDialog(String message) {
        new MaterialDialog.Builder(getApplicationContext())
                .title("Error")
                .content(message)

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
    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub


        if (prgDialog2 != null && prgDialog2.isShowing()) {

            prgDialog2.dismiss();
        }

        super.onDestroy();
    }


    public void LogOut() {
        session.logoutUser();

        // After logout redirect user to Loing Activity
        finish();
        Intent i = new Intent(ConfirmCashDepoActivity.this, SignInActivity.class);
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
