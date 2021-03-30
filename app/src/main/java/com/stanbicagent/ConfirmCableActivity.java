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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.stanbicagent.R;
import com.afollestad.materialdialogs.MaterialDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketTimeoutException;

import adapter.BillMenuParcelable;
import rest.ApiClient;
import rest.ApiInterface;
import rest.ApiSecurityClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import security.SecurityLayer;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.stanbicagent.ApplicationConstants.CHANNEL_ID;

public class ConfirmCableActivity extends BaseActivity implements View.OnClickListener {
    TextView reccustid, recamo, recnarr, recsendnum, recsendnam, txtfee, txtlabel,acbal,txtanarr;
    TextView step2, step1, step3, stt;
    Button btnsub;
    String txtcustid, amou, narra, ednamee, ednumbb, serviceid, billid, strlabl, servicename, billname, packid, paymentCode, bs,agbalance,marketnm;
    String finalrespfee;
    ProgressDialog  prgDialog2;
    RelativeLayout rlreceipt;
    EditText amon, edacc, pno, txtamount, txtnarr, edname, ednumber;
    EditText etpin;
    boolean chkfee = false;
    SessionManagement session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_cable);


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
        reccustid = (TextView) findViewById(R.id.textViewnb2);
        etpin = (EditText) findViewById(R.id.pin);
        acbal = (TextView) findViewById(R.id.txtacbal);
        stt = (TextView) findViewById(R.id.recip);
        recamo = (TextView) findViewById(R.id.textViewrrs);
        recnarr = (TextView)findViewById(R.id.textViewrr);

        txtanarr = (TextView)findViewById(R.id.textViewr);
        rlreceipt = (RelativeLayout) findViewById(R.id.rlreceipt);
        txtlabel = (TextView) findViewById(R.id.textViewnb);
        recsendnam = (TextView) findViewById(R.id.sendnammm);
        recsendnum = (TextView) findViewById(R.id.sendno);
        prgDialog2 = new ProgressDialog(this);
        prgDialog2.setMessage("Loading....");
        prgDialog2.setCancelable(false);
        txtfee = (TextView) findViewById(R.id.txtfee);
        chkfee = false;
        step2 = (TextView)findViewById(R.id.tv2);
        step2.setOnClickListener(this);

        step1 = (TextView) findViewById(R.id.tv);
        step1.setOnClickListener(this);

        step3 = (TextView) findViewById(R.id.tv3);
        step3.setOnClickListener(this);


        btnsub = (Button) findViewById(R.id.button2);
        btnsub.setOnClickListener(this);

        Intent intent = getIntent();
        if (intent != null) {

            txtcustid = intent.getStringExtra("custid");
            amou = intent.getStringExtra("amou");
            narra = intent.getStringExtra("narra");
            ednamee = intent.getStringExtra("ednamee");
            ednumbb = intent.getStringExtra("ednumbb");
            billname = intent.getStringExtra("billname");
            billid = intent.getStringExtra("billid");
            serviceid = intent.getStringExtra("serviceid");
            servicename = intent.getStringExtra("servicename");
            strlabl = intent.getStringExtra("label");
            packid = intent.getStringExtra("packId");
            paymentCode = intent.getStringExtra("paymentCode");
            reccustid.setText(txtcustid);


            recamo.setText(amou);


            recsendnam.setText(ednamee);
            recsendnum.setText(ednumbb);
          //  txtlabel.setText(strlabl);

            if(Utility.checkStateCollect(serviceid)){
                txtanarr.setText("Market");
                 marketnm = intent.getStringExtra("marketname");
                recnarr.setText(marketnm);
                stt.setText(ednamee);
                rlreceipt.setVisibility(View.GONE);
                chkfee = true;
            }else{
                recnarr.setText(narra);
                rlreceipt.setVisibility(View.VISIBLE);
            }
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



    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.button2) {
            if (Utility.checkInternetConnection(getApplicationContext())) {
                String agpin = etpin.getText().toString();
                if (Utility.isNotNull(txtcustid)) {
                    if (Utility.isNotNull(amou)) {
                        if (Utility.isNotNull(narra)) {
                            if (Utility.isNotNull(ednamee)) {
                                if (Utility.isNotNull(ednumbb)) {
                                    if (Utility.isNotNull(agpin)) {
                                     /*   if (chkfee) {

                                            boolean chkdb = true;
                                            try {
                                                double dbamo = Double.parseDouble(amou);
                                                Double dbagbal = 0.0;

                                                if (Utility.isNotNull(agbalance)) {
                                                    dbagbal = Double.parseDouble(agbalance);
                                                    if (dbamo > dbagbal) {
                                                        chkdb = false;
                                                    }
                                                }
                                            }catch (NumberFormatException e){
                                                e.printStackTrace();
                                            }
                                            if(chkdb){*/
                                                String encrypted = null;
                                                encrypted = Utility.b64_sha256(agpin);
                                                if (!(prgDialog2 == null)) {
                                                    //  prgDialog2.show();
                                                }

                                                ApiInterface apiService =
                                                        ApiClient.getClient().create(ApiInterface.class);
                                                String usid = Utility.gettUtilUserId(getApplicationContext());
                                                String agentid = Utility.gettUtilAgentId(getApplicationContext());
                                                String emaill = Utility.gettUtilEmail(getApplicationContext());
                                                final String mobnoo = "0" + Utility.gettUtilMobno(getApplicationContext());

                                                if ((!Utility.isNotNull(packid)) || packid.equals("")) {
                                                    packid = "01";
                                                }

                                               // /billpayment/dobillpayment.action/{channel}/{userId}/{id}/{amount}/{packId}/{customerMobile}/{customerId}/{pin}/{channelRef}
                                                String params = CHANNEL_ID + usid + "/" + billid + "/" + amou + "/" + packid + "/" + ednumbb + "/" + txtcustid;

                                                Intent intent  = new Intent(ConfirmCableActivity.this,TransactionProcessingActivity.class);



                                                //    PayBillResp(params);

                                                intent.putExtra("custid", txtcustid);
                                                intent.putExtra("amou", amou);
                                                intent.putExtra("narra", narra);
                                                intent.putExtra("billname", billname);
                                                intent.putExtra("ednamee", ednamee);
                                                intent.putExtra("ednumbb", ednumbb);
                                                intent.putExtra("billid", billid);
                                                intent.putExtra("serviceid", serviceid);
                                                intent.putExtra("label", strlabl);
                                                intent.putExtra("fullname", bs);

                                                intent.putExtra("params", params);
                                                intent.putExtra("fee", finalrespfee);
                                                intent.putExtra("txpin", encrypted);
                                                if(Utility.checkStateCollect(serviceid)) {
                                                    intent.putExtra("marketnm", marketnm);
                                                }
                                                intent.putExtra("serv", "CABLETV");
                                                startActivity(intent);
                                               /* Fragment fragment = new TransactingProcessing();

                                                fragment.setArguments(b);
                                                FragmentManager fragmentManager = getFragmentManager();
                                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                                //  String tag = Integer.toString(title);
                                                fragmentTransaction.replace(R.id.container_body, fragment, "Final Conf Cable");
                                                fragmentTransaction.addToBackStack("Final Conf Cable");
                                                ((FMobActivity) getApplicationContext())
                                                        .setActionBarTitle("Final Conf Cable");
                                                fragmentTransaction.commit();*/
                                                ClearPin();
                                          /*  }  else {
                                                Toast.makeText(
                                                        getApplicationContext(),
                                                        "The amount set is higher than your agent balance",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        } else {
                                            Toast.makeText(
                                                    getApplicationContext(), "Please ensure a valid customer has been chosen for the selected biller",
                                                    Toast.LENGTH_LONG).show();
                                        }*/
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
                            "Please enter a value for Customer ID",
                            Toast.LENGTH_LONG).show();
                }
            }
        }

        if (view.getId() == R.id.tv2) {
           /* Fragment fragment = new BillMenu();


            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment, "Biller Menu");
            fragmentTransaction.addToBackStack("Biller Menu");
            ((FMobActivity) getApplicationContext())
                    .setActionBarTitle("Biller Menu");
            fragmentTransaction.commit();*/

            finish();
            Intent intent  = new Intent(ConfirmCableActivity.this,BillMenuActivity.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);
        }
        if (view.getId() == R.id.tv) {

            finish();



            Intent intent  = new Intent(ConfirmCableActivity.this,SpecBillerMenuActivity.class);
            BillMenuParcelable bcp = new BillMenuParcelable(serviceid,servicename,null,null,null,null,null,null,null,null,null,null,null,null,null,null);



                /*intent.putExtra("serviceid",serviceid);
                intent.putExtra("servicename",servicename);*/
            intent.putExtra("bcp",bcp);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
          /*  Bundle b = new Bundle();
            b.putString("serviceid", serviceid);
            b.putString("servicename", servicename);


            Fragment fragment = new SpecBillMenu();
            String title = servicename;


            if (fragment != null) {
                fragment.setArguments(b);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                //  String tag = Integer.toString(title);
                fragmentTransaction.replace(R.id.container_body, fragment, title);
                fragmentTransaction.addToBackStack(title);
                ((FMobActivity) getApplicationContext())
                        .setActionBarTitle(title);
                fragmentTransaction.commit();
            }*/
        }
        if (view.getId() == R.id.tv3) {
onBackPressed();
           /* finish();
            Intent intent  = new Intent(ConfirmCableActivity.this,CableTVActivity.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);*/
           /* Bundle b = new Bundle();
            b.putString("serviceid", serviceid);
            b.putString("servicename", servicename);
            b.putString("billid", billid);
            b.putString("billname", billname);
            b.putString("label", strlabl);
            Fragment fragment = new CableTV();
            String title = "Cable";

            if (fragment != null) {
                fragment.setArguments(b);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                //  String tag = Integer.toString(title);
                fragmentTransaction.replace(R.id.container_body, fragment, title);
                fragmentTransaction.addToBackStack(title);
                ((FMobActivity) getApplicationContext())
                        .setActionBarTitle(title);
                fragmentTransaction.commit();
            }*/
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onResume() {
        super.onResume();
        // put your code here...

    }

    public void ClearPin() {
        etpin.setText("");
    }

    private void getFeeSec() {
        finalrespfee = "0.00";
        if (!(prgDialog2 == null)) {
            prgDialog2.show();
        }
        String endpoint = "fee/getfee.action";


        String usid = Utility.gettUtilUserId(getApplicationContext());
        String agentid = Utility.gettUtilAgentId(getApplicationContext());

        String params = CHANNEL_ID + usid + "/" + agentid + "/BILLPAYMENT/" + amou;
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
                    if(Utility.isNotNull(agbalance)) {
                        acbal.setText(agbalance+ApplicationConstants.KEY_NAIRA);
                    }


                    //session.setString(SecurityLayer.KEY_APP_ID,appid);


                    if (!(response.body() == null)) {
                        if (respcode.equals("00")) {

                            SecurityLayer.Log("Response Message", responsemessage);

//                                    SecurityLayer.Log("Respnse getResults",datas.toString());
                            if (respfee == null || respfee.equals("")) {
                                txtfee.setText("N/A");
                            } else {
                                respfee = Utility.returnNumberFormat(respfee);
                                finalrespfee = respfee;
                                txtfee.setText(ApplicationConstants.KEY_NAIRA + respfee);
                            }


                        } else if (respcode.equals("93")) {
                            Toast.makeText(
                                    getApplicationContext(),
                                    responsemessage,
                                    Toast.LENGTH_LONG).show();
                            onBackPressed();
                          /*  Bundle b = new Bundle();
                            b.putString("serviceid", serviceid);
                            b.putString("servicename", servicename);
                            b.putString("billid", billid);
                            b.putString("billname", billname);
                            b.putString("label", strlabl);

                            Fragment fragment = new CableTV();
                            String title = "Cable";

                            if (fragment != null) {
                                fragment.setArguments(b);
                                FragmentManager fragmentManager = getFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                //  String tag = Integer.toString(title);
                                fragmentTransaction.replace(R.id.container_body, fragment, title);
                                fragmentTransaction.addToBackStack(title);
                                ((FMobActivity) getApplicationContext())
                                        .setActionBarTitle(title);
                                fragmentTransaction.commit();
                            }*/
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Please ensure amount set is below the set limit",
                                    Toast.LENGTH_LONG).show();

                        } else {
                            //   btnsub.setVisibility(View.GONE);
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
                //    prgDialog2.dismiss();
                try {
                    if ((prgDialog2 != null) && prgDialog2.isShowing() && !(getApplicationContext() == null)) {
                        prgDialog2.dismiss();
                    }
                } catch (final IllegalArgumentException e) {
                    // Handle or log or ignore
                } catch (final Exception e) {
                    // Handle or log or ignore
                } finally {

                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                SecurityLayer.Log("Throwable error", t.toString());
                if (t instanceof SocketTimeoutException) {

                    SecurityLayer.Log("socket timeout error", t.toString());
                }

                // prgDialog2.dismiss();
                try {
                    if ((prgDialog2 != null) && prgDialog2.isShowing() && !(getApplicationContext() == null)) {
                        prgDialog2.dismiss();
                    }
                } catch (final IllegalArgumentException e) {
                    // Handle or log or ignore
                } catch (final Exception e) {
                    // Handle or log or ignore
                } finally {

                }
                // validatecust();
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

    private void validatecust() {
        if (!(prgDialog2 == null)) {
            prgDialog2.show();
        }
        //{channel}/{userId}/{merchantId}/{mobileNumber}/{billerId}/{customerId}/{pamentCode}
        String endpoint = "billpayment/validateCustomer.action";


        String usid = Utility.gettUtilUserId(getApplicationContext());
        String agentid = Utility.gettUtilAgentId(getApplicationContext());
        String mobnoo = Utility.gettUtilMobno(getApplicationContext());

        if (paymentCode == null || paymentCode.equals("")) {
            paymentCode = billid + "01";
        }
        if(paymentCode == null ||paymentCode.equals("null")){
            paymentCode = "0";
        }
        String params = CHANNEL_ID + usid + "/" + billid + "/" + txtcustid + "/" + paymentCode;
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
                    JSONObject respfee = obj.optJSONObject("data");


                    //session.setString(SecurityLayer.KEY_APP_ID,appid);


                    if (!(response.body() == null)) {
                        if (respcode.equals("00")) {

                            SecurityLayer.Log("Response Message", responsemessage);

//                                    SecurityLayer.Log("Respnse getResults",datas.toString());
                            if (respfee == null || respfee.equals("")) {

                            } else {
                                bs = respfee.optString("FullName");
                              /*  Toast.makeText(
                                        getApplicationContext(),
                                        bs,
                                        Toast.LENGTH_LONG).show();*/
                                stt.setText(bs);
                            }
                            chkfee = true;
                        } else if (respcode.equals("93")) {
                            Toast.makeText(
                                    getApplicationContext(),
                                    responsemessage,
                                    Toast.LENGTH_LONG).show();
                          /*  Bundle b = new Bundle();
                            b.putString("serviceid", serviceid);
                            b.putString("servicename", servicename);
                            b.putString("billid", billid);
                            b.putString("billname", billname);
                            b.putString("label", strlabl);
                            b.putString("fullname", strlabl);
                            Fragment fragment = new CableTV();
                            String title = "Cable";

                            if (fragment != null) {
                                fragment.setArguments(b);
                                FragmentManager fragmentManager = getFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                //  String tag = Integer.toString(title);
                                fragmentTransaction.replace(R.id.container_body, fragment, title);
                                fragmentTransaction.addToBackStack(title);
                                ((FMobActivity) getApplicationContext())
                                        .setActionBarTitle(title);
                                fragmentTransaction.commit();
                            }*/
                            Toast.makeText(
                                    getApplicationContext(),
                                    obj.optString("responsemessage"),
                                    Toast.LENGTH_LONG).show();

                        } else {
                            //  btnsub.setVisibility(View.GONE);
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
                try {
                    if ((prgDialog2 != null) && prgDialog2.isShowing() && !(getApplicationContext() == null)) {
                        prgDialog2.dismiss();
                    }
                } catch (final IllegalArgumentException e) {
                    // Handle or log or ignore
                } catch (final Exception e) {
                    // Handle or log or ignore
                } finally {

                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                SecurityLayer.Log("Throwable error", t.toString());


                try {
                    if ((prgDialog2 != null) && prgDialog2.isShowing() && !(getApplicationContext() == null)) {
                        prgDialog2.dismiss();
                    }
                } catch (final IllegalArgumentException e) {
                    // Handle or log or ignore
                } catch (final Exception e) {
                    // Handle or log or ignore
                } finally {

                } if(!(getApplicationContext() == null)) {
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
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
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

        if(prgDialog2!=null && prgDialog2.isShowing()){

            prgDialog2.dismiss();
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
