package com.stanbicagent;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.stanbicagent.R;
import com.afollestad.materialdialogs.MaterialDialog;

import org.json.JSONException;
import org.json.JSONObject;

import rest.ApiInterface;
import rest.ApiSecurityClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import security.SecurityLayer;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class WithdrawActivity extends BaseActivity implements View.OnClickListener {
    SessionManagement session;
    EditText acno, amo;
    String accnum = null;
    ProgressDialog prgDialog, prgDialog2;
    Button sigin;
    TextView txtref;
    RadioButton r1, r2, r3;
    LinearLayout lywithdr, lybutt;
    RelativeLayout rlid;
    Spinner sp1, sp2, sp3;
    Button btnok;
    String acname;
    String txref;
    EditText accountoname, cotp;
    private static int SPLASH_TIME_OUT = 2500;
    EditText amon, edacc, edamo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        // Get the ActionBar here to configure the way it behaves.
        final ActionBar ab = getSupportActionBar();
        //ab.setHomeAsUpIndicator(R.drawable.ic_menu); // set a custom icon for the default home button
        ab.setDisplayShowHomeEnabled(true); // show or hide the default home button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
        ab.setDisplayShowTitleEnabled(false); // disable the default title element here (for centered title)
        btnok = (Button) findViewById(R.id.button5);
        session = new SessionManagement(this);
        lywithdr = (LinearLayout) findViewById(R.id.lywithdr);
        txtref = (TextView) findViewById(R.id.txref);
        lybutt = (LinearLayout) findViewById(R.id.lybutt);
        edacc = (EditText) findViewById(R.id.input_payacc);
        edamo = (EditText) findViewById(R.id.amount);
        cotp = (EditText) findViewById(R.id.cotp);
        accountoname = (EditText) findViewById(R.id.cname);
        btnok.setOnClickListener(this);


        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Loading Account Details....");

        prgDialog2 = new ProgressDialog(this);
        prgDialog2.setMessage("Loading....");
        prgDialog2.setCancelable(false);
        // Set Cancelable as False
        View.OnFocusChangeListener ofcListener = new MyFocusChangeListener();
        edamo.setOnFocusChangeListener(ofcListener);

        edacc.setOnFocusChangeListener(ofcListener);

        prgDialog.setCancelable(false);
        edacc.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edacc.getText().toString().length() == 13) {


                    if (Utility.checkInternetConnection(getApplicationContext())) {

                        Utility.hideKeyboardFrom(getApplicationContext(), edacc);
                        prgDialog.show();

                        String acno = edacc.getText().toString();

                        NameInquirySec(acno);

                    }
                    //   accountoname.setText("Test Customer");


                }
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

                // TODO Auto-generated method stub
            }
        });


    }


    private class MyFocusChangeListener implements View.OnFocusChangeListener {

        public void onFocusChange(View v, boolean hasFocus) {

            if (v.getId() == R.id.amount && !hasFocus) {
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                String txt = edamo.getText().toString();
                String fbal = Utility.returnNumberFormat(txt);
                edamo.setText(fbal);

            }


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

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.button5) {
            String recaccno = edacc.getText().toString();
            String editamo = edamo.getText().toString();
            String ottp = cotp.getText().toString();
            if (Utility.isNotNull(recaccno)) {
                if (Utility.isNotNull(editamo)) {
                    String nwamo = editamo.replace(",", "");
                    SecurityLayer.Log("New Amount", nwamo);
                    double txamou = Double.parseDouble(nwamo);
                    /*  if (txamou >= 100) {*/
                    if (Utility.isNotNull(ottp)) {
                        if (Utility.isNotNull(acname)) {
                            if (Utility.isNotNull(txref)) {
                               /* Bundle b = new Bundle();
                                b.putString("recanno", recaccno);
                                b.putString("amou", editamo);
                                b.putString("otp", ottp);
                                b.putString("txtname", acname);
                                b.putString("txref", txref);
                                Fragment fragment = new ConfirmWithdrawal();

                                fragment.setArguments(b);
                                FragmentManager fragmentManager = getFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                //  String tag = Integer.toString(title);
                                fragmentTransaction.replace(R.id.container_body, fragment, "Confirm Withdrawal");
                                fragmentTransaction.addToBackStack("Confirm Withdrawal");
                                ((FMobActivity) getApplicationContext())
                                        .setActionBarTitle("Confirm Withdrawal");
                                fragmentTransaction.commit();*/

                                Intent intent = new Intent(WithdrawActivity.this, ConfirmWithdrawalActivity.class);


                                intent.putExtra("recanno", recaccno);
                                intent.putExtra("amou", editamo);
                                intent.putExtra("otp", ottp);
                                intent.putExtra("txtname", acname);
                                intent.putExtra("txref", txref);


                                startActivity(intent);

                            } else {
                                Toast.makeText(
                                        getApplicationContext(),
                                        "Please ensure you have generated a withdrawal transaction for the customer",
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Please ensure you have checked the account's name ",
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(
                                getApplicationContext(),
                                "Please enter a valid value for one time pin",
                                Toast.LENGTH_LONG).show();
                    }
                   /* } else {
                        Toast.makeText(
                                getApplicationContext(),
                                "Please enter a valid amount more than 100 Naira",
                                Toast.LENGTH_LONG).show();
                    }*/
                } else {
                    Toast.makeText(
                            getApplicationContext(),
                            "Please enter a valid value for amount",
                            Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(
                        getApplicationContext(),
                        "Please enter a valid value for account number",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void NameInquirySec(String acno) {

        String endpoint = "transfer/nameenq.action";


        String usid = Utility.gettUtilUserId(getApplicationContext());
        String agentid = Utility.gettUtilAgentId(getApplicationContext());
        String mobileno = Utility.gettUtilAgentId(getApplicationContext());
        String params = ApplicationConstants.CHANNEL_ID+usid+"/"+agentid+"/"+mobileno+"/0/"+acno;
//        String params = CHANNEL_ID + usid + "/0/" + acno;
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


                    JSONObject plan = obj.optJSONObject("data");
                    //session.setString(SecurityLayer.KEY_APP_ID,appid);


                    if (!(response.body() == null)) {
                        if (respcode.equals("00")) {

                            SecurityLayer.Log("Response Message", responsemessage);

//                                    SecurityLayer.Log("Respnse getResults",datas.toString());
                            if (!(plan == null)) {
                                acname = plan.optString("accountName");

                                final String recaccno = edacc.getText().toString();

                                new MaterialDialog.Builder(WithdrawActivity.this)
                                        .title("Account Details")
                                        .content("The following are the recipient account details  \n \n" +
                                                " Account Number: " + recaccno + " \n   Account Name:" + acname + "   \n Do you wish to proceed with this transaction?")
                                        .positiveText("Confirm")
                                        .negativeText("Cancel")
                                        .callback(new MaterialDialog.ButtonCallback() {
                                            @Override
                                            public void onPositive(MaterialDialog dialog) {


                                                prgDialog2.show();

                                                String usid = Utility.gettUtilUserId(getApplicationContext());
                                                String agentid = Utility.gettUtilAgentId(getApplicationContext());
                                                String mobnoo = Utility.gettUtilMobno(getApplicationContext());
//                                                String params = CHANNEL_ID + usid + "/" + recaccno + "/" + acname;
                                                String params = ApplicationConstants.CHANNEL_ID+usid+"/"+agentid+"/"+mobnoo+"/"+recaccno+"/"+acname;

                                                getOTP(params);

                                            }

                                            @Override
                                            public void onNegative(MaterialDialog dialog) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .show();
                            } else {

                                Toast.makeText(
                                        getApplicationContext(),
                                        "This is not a valid account number.Please check again",
                                        Toast.LENGTH_LONG).show();


                            }

                        } else {
                            Toast.makeText(
                                    getApplicationContext(),
                                    responsemessage,
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(
                                getApplicationContext(),
                                "There was an error processing your request ",
                                Toast.LENGTH_LONG).show();
                    }
                    try {
                        if ((prgDialog != null) && prgDialog.isShowing() && getApplicationContext() != null) {
                            prgDialog.dismiss();
                        }
                    } catch (final IllegalArgumentException e) {
                        // Handle or log or ignore
                    } catch (final Exception e) {
                        // Handle or log or ignore
                    } finally {
                        //   prgDialog = null;
                    }


                } catch (JSONException e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    // TODO Auto-generated catch block
                    Toast.makeText(getApplicationContext(), getApplicationContext().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                    SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getApplicationContext());
                    // SecurityLayer.Log(e.toString());

                } catch (Exception e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getApplicationContext());
                    // SecurityLayer.Log(e.toString());
                }
                prgDialog.dismiss();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                SecurityLayer.Log("Throwable error", t.toString());
                Toast.makeText(
                        getApplicationContext(),
                        "There was an error processing your request",
                        Toast.LENGTH_LONG).show();
                if ((prgDialog != null) && prgDialog.isShowing() && getApplicationContext() != null) {
                    prgDialog.dismiss();
                }
                SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getApplicationContext());
            }
        });

    }


    private void getOTP(String params) {

        String endpoint = "withdrawal/cashbyaccountinit.action";


        String usid = Utility.gettUtilUserId(getApplicationContext());
        String agentid = Utility.gettUtilAgentId(getApplicationContext());
        // String params = "1/suresh/BATA00000000019493818389/0/"+acno;
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


                    String plan = obj.optString("data");
                    //session.setString(SecurityLayer.KEY_APP_ID,appid);


                    if (!(response.body() == null)) {
                        txref = plan;
                        if (Utility.isNotNull(txref)) {
                            txtref.setText(txref);
                        }
                        if (Utility.isNotNull(respcode) && Utility.isNotNull(responsemessage)) {
                            SecurityLayer.Log("Response Message", responsemessage);
                            if (respcode.equals("00")) {
                                Toast.makeText(
                                        getApplicationContext(),
                                        "Customer can proceed to input their OTP to complete transaction",
                                        Toast.LENGTH_LONG).show();

                                lybutt.setVisibility(View.VISIBLE);
                                lywithdr.setVisibility(View.VISIBLE);
                                accountoname.setText(acname);
                            } else {
                                Toast.makeText(
                                        getApplicationContext(),
                                        responsemessage,
                                        Toast.LENGTH_LONG).show();
                            }


                        } else {

                            Toast.makeText(
                                    getApplicationContext(),
                                    "There was an error on your request",
                                    Toast.LENGTH_LONG).show();


                        }
                        if ((prgDialog2 != null) && prgDialog2.isShowing() && getApplicationContext() != null) {
                            prgDialog2.dismiss();
                        }
                    } else {
                        Toast.makeText(
                                getApplicationContext(),
                                "There was an error processing your request ",
                                Toast.LENGTH_LONG).show();
                    }


                } catch (JSONException e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    // TODO Auto-generated catch block
                    Toast.makeText(getApplicationContext(), getApplicationContext().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                    SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getApplicationContext());
                    // SecurityLayer.Log(e.toString());

                } catch (Exception e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getApplicationContext());
                    // SecurityLayer.Log(e.toString());
                }
                if ((prgDialog2 != null) && prgDialog2.isShowing() && getApplicationContext() != null) {
                    prgDialog2.dismiss();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                SecurityLayer.Log("Throwable error", t.toString());
                Toast.makeText(
                        getApplicationContext(),
                        "There was an error processing your request",
                        Toast.LENGTH_LONG).show();
                if ((prgDialog2 != null) && prgDialog2.isShowing() && getApplicationContext() != null) {
                    prgDialog2.dismiss();
                }
                SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getApplicationContext());
            }
        });

    }


    public void SetForceOutDialog(String msg, final String title, final Context c) {
        if (!(c == null)) {
            new MaterialDialog.Builder(WithdrawActivity.this)
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
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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

        if (prgDialog2 != null && prgDialog2.isShowing()) {

            prgDialog2.dismiss();
        }
        if (prgDialog != null && prgDialog.isShowing()) {

            prgDialog.dismiss();
        }
        super.onDestroy();
    }
}
