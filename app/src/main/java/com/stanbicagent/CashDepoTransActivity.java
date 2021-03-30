package com.stanbicagent;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.ActionBar;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

import static com.stanbicagent.ApplicationConstants.CHANNEL_ID;

public class CashDepoTransActivity extends BaseActivity implements View.OnClickListener {
    ImageView imageView1;

    EditText amon, edacc, pno, txtamount, txtnarr, edname, ednumber;
    Button btnsub;
    SessionManagement session;
    ProgressDialog prgDialog;
    EditText accountoname;
    String depositid;
    RelativeLayout rlid;
    String acname;
    TextView step2;

    private static int SPLASH_TIME_OUT = 2500;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_depo_trans2);
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

        rlid = (RelativeLayout) findViewById(R.id.rlid);

        step2 = (TextView)findViewById(R.id.tv2);
        step2.setOnClickListener(this);
        edacc = (EditText) findViewById(R.id.input_payacc);
        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Loading Account Details....");
        prgDialog.setCancelable(false);

        pno = (EditText) findViewById(R.id.scodepnam);

        accountoname = (EditText) findViewById(R.id.cname);


        txtamount = (EditText)findViewById(R.id.amount);
        txtnarr = (EditText) findViewById(R.id.ednarr);
        edname = (EditText) findViewById(R.id.sendname);
        ednumber = (EditText) findViewById(R.id.sendnumber);

        View.OnFocusChangeListener ofcListener = new MyFocusChangeListener();
        txtamount.setOnFocusChangeListener(ofcListener);
        txtnarr.setOnFocusChangeListener(ofcListener);
        edacc.setOnFocusChangeListener(ofcListener);
        edname.setOnFocusChangeListener(ofcListener);
        ednumber.setOnFocusChangeListener(ofcListener);

        btnsub = (Button) findViewById(R.id.button2);
        btnsub.setOnClickListener(this);
        edacc.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edacc.getText().toString().length() == 13) {
                    if (!(getApplicationContext() == null)) {
                        if (Utility.checkInternetConnection(getApplicationContext())) {

                            Utility.hideKeyboardFrom(getApplicationContext(), edacc);
                            if (!(prgDialog == null) &&  (!(CashDepoTransActivity.this.isFinishing())) && !(getApplicationContext() == null)) {
                                prgDialog.show();
                            }

                            String acno = edacc.getText().toString();
                            NameInquirySec(acno);
                       /* ApiInterface apiService =
                                ApiClient.getClient().create(ApiInterface.class);

                        Call<NameEnquiry> call = apiService.getAccountDetails("1", "suresh", "BATA0000000001", "0000", "0", acno);
                        call.enqueue(new Callback<NameEnquiry>() {
                            @Override
                            public void onResponse(Call<NameEnquiry> call, Response<NameEnquiry> response) {
                                String responsemessage = response.body().getMessage();

                                SecurityLayer.Log("Response Message", responsemessage);
                                NameEnquiryData datas = response.body().getResults();
//                                    SecurityLayer.Log("Respnse getResults",datas.toString());
                                if (!(datas == null)) {
                                    acname = datas.getAccountName();
                                    Toast.makeText(
                                            getApplicationContext(),
                                            "Account Name: " + acname,
                                            Toast.LENGTH_LONG).show();
                                    accountoname.setText(acname);
                                }
                                else{
                                    Toast.makeText(
                                            getApplicationContext(),
                                            "This is not a valid account number.Please check again",
                                            Toast.LENGTH_LONG).show();
                                }
                                prgDialog.dismiss();
                            }

                            @Override
                            public void onFailure(Call<NameEnquiry> call, Throwable t) {
                                // Log error here since request failed
                                SecurityLayer.Log("Throwable error",t.toString());
                                Toast.makeText(
                                        getApplicationContext(),
                                        "There was an error processing your request",
                                        Toast.LENGTH_LONG).show();
                                prgDialog.dismiss();
                            }
                        });*/
                        }
                    }

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
                String txt = txtamount.getText().toString();
                String fbal = Utility.returnNumberFormat(txt);
                txtamount.setText(fbal);

            }

            if (v.getId() == R.id.ednarr && !hasFocus) {
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);


            }
            if (v.getId() == R.id.sendname && !hasFocus) {
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);


            }
            if (v.getId() == R.id.sendnumber && !hasFocus) {
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);


            }
            if (v.getId() == R.id.input_payacc && !hasFocus) {
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);


            }
        }
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

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button4) {
            rlid.setVisibility(View.VISIBLE);
            //   checkInternetConnection2();

        }
        if (view.getId() == R.id.button2) {
            if (Utility.checkInternetConnection(getApplicationContext())) {
                final String recanno = edacc.getText().toString();
                final String amou = txtamount.getText().toString();
                final String narra = txtnarr.getText().toString();
                final String ednamee = edname.getText().toString();
                final String ednumbb = ednumber.getText().toString();
                if (Utility.isNotNull(recanno)) {
                    if (Utility.isNotNull(amou)) {
                        String nwamo = amou.replace(",", "");
                        SecurityLayer.Log("New Amount",nwamo);
                        double txamou = Double.parseDouble(nwamo);
                       /* if (txamou >= 100) {*/
                        if (Utility.isNotNull(narra)) {
                            if (Utility.isNotNull(ednamee)) {
                                if (Utility.isNotNull(ednumbb)) {

                                    if (Utility.isNotNull(acname)) {

                                        Intent intent  = new Intent(CashDepoTransActivity.this,ConfirmCashtransActivity.class);


                                        intent.putExtra("recanno", recanno);
                                        intent.putExtra("amou", amou);
                                        intent.putExtra("narra", narra);
                                        intent.putExtra("ednamee", ednamee);
                                        intent.putExtra("ednumbb", ednumbb);
                                        intent.putExtra("txtname", acname);
                                        intent.putExtra("trantype", "T");



                                        startActivity(intent);
                                      /*  Bundle b = new Bundle();
                                        b.putString("recanno", recanno);
                                        b.putString("amou", amou);
                                        b.putString("narra", narra);
                                        b.putString("ednamee", ednamee);
                                        b.putString("ednumbb", ednumbb);
                                        b.putString("txtname", acname);
                                        b.putString("trantype", "T");
                                        Fragment fragment = new ConfirmCashTransf();

                                        fragment.setArguments(b);
                                        FragmentManager fragmentManager = getFragmentManager();
                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                        //  String tag = Integer.toString(title);
                                        fragmentTransaction.replace(R.id.container_body, fragment, "Confirm Transfer");
                                        fragmentTransaction.addToBackStack("Confirm Transfer");
                                        ((FMobActivity) getApplicationContext())
                                                .setActionBarTitle("Confirm Transfer");
                                        fragmentTransaction.commit();*/
                                    } else {
                                        Toast.makeText(
                                                getApplicationContext(),
                                                "Please enter a valid account number",
                                                Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(
                                            getApplicationContext(),
                                            "Please enter a valid value for Sender Number",
                                            Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(
                                        getApplicationContext(),
                                        "Please enter a valid value for Sender Name",
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Please enter a valid value for Narration",
                                    Toast.LENGTH_LONG).show();

                        }
                       /* }else {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Please enter a valid amount more than 100 Naira",
                                    Toast.LENGTH_LONG).show();
                        }*/
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
        if (view.getId() == R.id.tv2) {
           /* Fragment fragment = new FTMenu();


            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment, "Confirm Transfer");
            fragmentTransaction.addToBackStack("Confirm Transfer");
            ((FMobActivity) getApplicationContext())
                    .setActionBarTitle("Confirm Transfer");
            fragmentTransaction.commit();*/

            finish();



            Intent intent  = new Intent(CashDepoTransActivity.this,FTMenuActivity.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);
        }
    }


    public void SetDialog(String msg, String title) {
        new MaterialDialog.Builder(getApplicationContext())
                .title(title)
                .content(msg)

                .negativeText("Close")
                .show();
    }

    private void NameInquirySec(String acno) {

        String endpoint = "transfer/nameenq.action";


        String usid = Utility.gettUtilUserId(getApplicationContext());
        String agentid = Utility.gettUtilAgentId(getApplicationContext());
        String mobileno = Utility.gettUtilAgentId(getApplicationContext());
        String params = CHANNEL_ID + usid + "/0/" + acno;
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
                    if (Utility.isNotNull(respcode) && Utility.isNotNull(respcode)) {
                        if ((Utility.checkUserLocked(respcode))) {
                           LogOut();
                        }
                        if (!(response.body() == null)) {
                            if (respcode.equals("00")) {

                                SecurityLayer.Log("Response Message", responsemessage);

//                                    SecurityLayer.Log("Respnse getResults",datas.toString());
                                if (!(plan == null)) {
                                    acname = plan.optString("accountName");

                                    Toast.makeText(
                                            getApplicationContext(),
                                            "Account Name: " + acname,
                                            Toast.LENGTH_LONG).show();
                                    accountoname.setText(acname);
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
                    }

                } catch (JSONException e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    // TODO Auto-generated catch block
                    if(!(getApplicationContext() == null)) {
                        Toast.makeText(getApplicationContext(), getApplicationContext().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                        SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getApplicationContext());
                    }
                    // SecurityLayer.Log(e.toString());

                } catch (Exception e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    if(!(getApplicationContext() == null)) {
                       SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getApplicationContext());
                    }
                    // SecurityLayer.Log(e.toString());
                }
                if ((prgDialog != null) && prgDialog.isShowing() && !(getApplicationContext() == null)) {
                    prgDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                SecurityLayer.Log("Throwable error", t.toString());

                if ((prgDialog != null) && prgDialog.isShowing() && !(getApplicationContext() == null)) {
                    Toast.makeText(
                            getApplicationContext(),
                            "There was an error processing your request",
                            Toast.LENGTH_LONG).show();

                    prgDialog.dismiss();
                   SetForceOutDialog(getString(R.string.forceout),getString(R.string.forceouterr),getApplicationContext());

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



    private void dismissProgressDialog() {
        if (prgDialog != null && prgDialog.isShowing()) {
            prgDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        dismissProgressDialog();
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
