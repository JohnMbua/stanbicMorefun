package com.stanbicagent;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.ActionBar;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import static com.stanbicagent.ApplicationConstants.CHANNEL_ID;

public class SendOtherWalletActivity extends BaseActivity implements View.OnClickListener {
    ImageView imageView1;
    EditText amon, phonenumb,pno,txtamount,txtnarr,edname,ednumber;
    Button btnsub;
    SessionManagement session;
    ProgressDialog prgDialog;
    RecyclerView lvbann;

    LinearLayoutManager layoutManager,layoutManager2;
    String depositid,walletname,walletcode;

    Spinner sp1;
    TextView walletchosen,walletselected;
    TextView step2;
    String acname;
    EditText accountoname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_other_wallet);

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
        amon = (EditText) findViewById(R.id.phone);
        sp1 = (Spinner) findViewById(R.id.spin1);

        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Loading Request....");
        pno = (EditText)findViewById(R.id.scodepnam);

        phonenumb = (EditText) findViewById(R.id.phonenumb);

        txtamount = (EditText) findViewById(R.id.amount);
        txtnarr = (EditText) findViewById(R.id.ednarr);
        edname = (EditText) findViewById(R.id.sendname);
        ednumber = (EditText) findViewById(R.id.sendnumber);

        btnsub = (Button) findViewById(R.id.button2);
        btnsub.setOnClickListener(this);
        accountoname = (EditText) findViewById(R.id.cname);
        walletchosen = (TextView)findViewById(R.id.textmmo);
        walletselected = (TextView) findViewById(R.id.textVipp);
        walletselected.setOnClickListener(this);
        //    lvbann = (RecyclerView) root.findViewById(R.id.listView2);
        layoutManager2 = new LinearLayoutManager(this);
        layoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);


        View.OnFocusChangeListener ofcListener = new MyFocusChangeListener();
        txtamount.setOnFocusChangeListener(ofcListener);
        txtnarr.setOnFocusChangeListener(ofcListener);
        phonenumb.setOnFocusChangeListener(ofcListener);
        edname.setOnFocusChangeListener(ofcListener);
        ednumber.setOnFocusChangeListener(ofcListener);



        Intent intent = getIntent();
        if (intent != null) {


            walletname = intent.getStringExtra("walletname");
            walletcode = intent.getStringExtra("wallcode");
        }

        if(Utility.isNotNull(walletname)){
            walletchosen.setText(walletname);
        }

        step2 = (TextView) findViewById(R.id.tv2);
        step2.setOnClickListener(this);


        phonenumb.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (phonenumb.getText().toString().length() == 11) {

                    if (!(walletcode == null)) {
                        if (Utility.checkInternetConnection(getApplicationContext())) {
                            Utility.hideKeyboardFrom(getApplicationContext(), phonenumb);
                            prgDialog.show();
                            String mobno = phonenumb.getText().toString();
                            mobno = "0"+mobno.substring(mobno.length() - 10);
                            String usid = Utility.gettUtilUserId(getApplicationContext());
                            String agentid = Utility.gettUtilAgentId(getApplicationContext());
                            String mobnoo = Utility.gettUtilMobno(getApplicationContext());
                            String mobileno = Utility.gettUtilAgentId(getApplicationContext());

                            String params = CHANNEL_ID+usid+"/"+agentid+"/"+mobileno+"/"+walletcode+"/"+mobno;
                            NameInquirySec(params);

                        }
                    }
                    else {
                        Toast.makeText(
                                getApplicationContext(),
                                "Please select a wallet ",
                                Toast.LENGTH_LONG).show();
                    }
                    // TODO Auto-generated method stub
                }
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


    private class MyFocusChangeListener implements View.OnFocusChangeListener {

        public void onFocusChange(View v, boolean hasFocus){

            if(v.getId() == R.id.amount && !hasFocus) {
                InputMethodManager imm =  (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                String txt = txtamount.getText().toString();
                String fbal = Utility.returnNumberFormat(txt);
                txtamount.setText(fbal);

            }

            if(v.getId() == R.id.ednarr && !hasFocus) {
                InputMethodManager imm =  (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);


            }
            if(v.getId() == R.id.sendname && !hasFocus) {
                InputMethodManager imm =  (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);


            }
            if(v.getId() == R.id.sendnumber && !hasFocus) {
                InputMethodManager imm =  (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);


            }
            if(v.getId() == R.id.input_payacc && !hasFocus) {
                InputMethodManager imm =  (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);


            }
        }
    }
    @Override
    public void onClick(View view) {


        if (view.getId() == R.id.button2) {

            String phoneno = phonenumb.getText().toString();
            if(phoneno.length() >= 10) {
                phoneno = "0" + phoneno.substring(phoneno.length() - 10);
            }else{
                phoneno = null;
            }
            String amou = txtamount.getText().toString();
            String narra = txtnarr.getText().toString();
            String ednamee = edname.getText().toString();
            String ednumbb = ednumber.getText().toString();

            if (Utility.isNotNull(walletname)) {
                if (Utility.isNotNull(phoneno)) {
                    if (Utility.isNotNull(amou)) {
                        if (Utility.isNotNull(narra)) {
                            if (Utility.isNotNull(ednamee)) {
                                if (Utility.isNotNull(ednumbb)) {
                                    if (Utility.isNotNull(acname)) {


                                       /* Bundle b = new Bundle();
                                        b.putString("walletname", walletname);
                                        b.putString("walletcode", walletcode);
                                        b.putString("wphoneno", phoneno);
                                        b.putString("txtname", acname);
                                        b.putString("amou", amou);
                                        b.putString("narra", narra);
                                        b.putString("ednamee", ednamee);
                                        b.putString("ednumbb", ednumbb);

                                        Fragment fragment = new ConfirmOtherWallet();

                                        fragment.setArguments(b);
                                        FragmentManager fragmentManager = getFragmentManager();
                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                        //  String tag = Integer.toString(title);
                                        fragmentTransaction.replace(R.id.container_body, fragment, "Confirm Other Wallet");
                                        fragmentTransaction.addToBackStack("Confirm Other Wallet");
                                        ((FMobActivity) getApplicationContext())
                                                .setActionBarTitle("Confirm Other Wallet");
                                        fragmentTransaction.commit();*/

                                        Intent intent  = new Intent(SendOtherWalletActivity.this,ConfirmOtherWalletActivity.class);

                                        intent.putExtra("walletname", walletname);
                                        intent.putExtra("walletcode", walletcode);
                                        intent.putExtra("wphoneno", phoneno);
                                        intent.putExtra("txtname", acname);
                                        intent.putExtra("amou", amou);
                                        intent.putExtra("narra", narra);
                                        intent.putExtra("ednamee", ednamee);
                                        intent.putExtra("ednumbb", ednumbb);


                                        startActivity(intent);
                                    }  else {
                                        Toast.makeText(
                                                getApplicationContext(),
                                                "Please enter a valid phone number",
                                                Toast.LENGTH_LONG).show();
                                    }
                                }else {
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
                            "Please enter a value for Wallet Phone Number",
                            Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(
                        getApplicationContext(),
                        "Please select a mobile money operator",
                        Toast.LENGTH_LONG).show();
            }
        }
        if (view.getId() == R.id.textVipp) {
            //   SetDialog("Select Bank");

            /*Fragment  fragment = new OtherWalletsPage();


            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment,"Select Wallets");
            fragmentTransaction.addToBackStack("Select Wallets");
            ((FMobActivity)getApplicationContext())
                    .setActionBarTitle("Select Wallets");
            fragmentTransaction.commit();*/

            Intent intent  = new Intent(SendOtherWalletActivity.this,OtherWalletsActivity.class);



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


            finish();



            Intent intent  = new Intent(SendOtherWalletActivity.this,FTMenuActivity.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);
        }
    }


    public void SetDialog(String msg,String title){
        new MaterialDialog.Builder(getApplicationContext())
                .title(title)
                .content(msg)

                .negativeText("Close")
                .show();
    }

    private void NameInquirySec(String params) {

        String endpoint= "transfer/nameenq.action";


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

                    SecurityLayer.Log("response..:", response.body());
                    JSONObject obj = new JSONObject(response.body());
                    //obj = Utility.onresp(obj,getApplicationContext());
                    obj = SecurityLayer.decryptTransaction(obj, getApplicationContext());
                    SecurityLayer.Log("decrypted_response", obj.toString());

                    String respcode = obj.optString("responseCode");
                    String responsemessage = obj.optString("message");


                    JSONObject plan = obj.optJSONObject("data");
                    //session.setString(SecurityLayer.KEY_APP_ID,appid);


                    if(!(response.body() == null)) {
                        if (respcode.equals("00")) {

                            SecurityLayer.Log("Response Message", responsemessage);

//                                    SecurityLayer.Log("Respnse getResults",datas.toString());
                            if (!(plan == null)) {
                                acname = plan.optString("accountName");

                                Toast.makeText(
                                        getApplicationContext(),
                                        " Name: " + acname,
                                        Toast.LENGTH_LONG).show();
                                accountoname.setText(acname);
                            } else {

                                Toast.makeText(
                                        getApplicationContext(),
                                        "This is not a valid account number.Please check again",
                                        Toast.LENGTH_LONG).show();


                            }

                        }else{
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



                } catch (JSONException e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    // TODO Auto-generated catch block
                    Toast.makeText(getApplicationContext(), getApplicationContext().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                    if(!(getApplicationContext() == null)) {
                       SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getApplicationContext());
                    }
                    // SecurityLayer.Log(e.toString());

                } catch (Exception e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                   SetForceOutDialog(getString(R.string.forceout),getString(R.string.forceouterr),getApplicationContext());
                    // SecurityLayer.Log(e.toString());
                }
                try {
                    if ((prgDialog != null) && prgDialog.isShowing()) {
                        prgDialog.dismiss();
                    }
                } catch (final IllegalArgumentException e) {
                    // Handle or log or ignore
                } catch (final Exception e) {
                    // Handle or log or ignore
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
                try {
                    if ((prgDialog != null) && prgDialog.isShowing()) {
                        prgDialog.dismiss();
                    }
                } catch (final IllegalArgumentException e) {
                    // Handle or log or ignore
                } catch (final Exception e) {
                    // Handle or log or ignore
                }
                SetForceOutDialog(getString(R.string.forceout),getString(R.string.forceouterr),getApplicationContext());
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
    protected void onDestroy() {
        // TODO Auto-generated method stub

        if(prgDialog!=null && prgDialog.isShowing()){

            prgDialog.dismiss();
        }
        super.onDestroy();
    }
}
