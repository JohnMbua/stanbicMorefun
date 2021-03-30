package com.stanbicagent;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.ActionBar;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.stanbicagent.R;
import com.afollestad.materialdialogs.MaterialDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import adapter.BenList;
import adapter.BillMenuParcelable;
import rest.ApiInterface;
import rest.ApiSecurityClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import security.SecurityLayer;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.stanbicagent.ApplicationConstants.CHANNEL_ID;

public class StateCollectActivity extends BaseActivity implements View.OnClickListener {
    ImageView imageView1;
    List<BenList> marketslist = new ArrayList<BenList>();
    Button btnsub;
    SessionManagement session;
    ProgressDialog prgDialog2;
    EditText amon, edacc,pno,txtamount,edname,ednumber;
    String billid,serviceid,servlabel,servicename,blname,packid,paycode,charge;
    TextView billname,smcno,step2,step1;

    ArrayAdapter<BenList> mobadapt;

    Spinner sp3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_state_collect);

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
        amon = (EditText) findViewById(R.id.amount);
        edacc = (EditText) findViewById(R.id.acc);
        billname = (TextView) findViewById(R.id.textView1);
        smcno = (TextView) findViewById(R.id.smcno);
        prgDialog2 = new ProgressDialog(this);
        prgDialog2.setMessage("Loading Request....");
        prgDialog2.setCancelable(false);


        sp3 = (Spinner)findViewById(R.id.spin3);
        btnsub = (Button)findViewById(R.id.button2);
        btnsub.setOnClickListener(this);

        step2 = (TextView) findViewById(R.id.tv2);
        step2.setOnClickListener(this);

        step1 = (TextView) findViewById(R.id.tv);
        step1.setOnClickListener(this);

        txtamount = (EditText) findViewById(R.id.amount);

        edname = (EditText) findViewById(R.id.sendname);
        ednumber = (EditText) findViewById(R.id.sendnumber);


        View.OnFocusChangeListener ofcListener = new MyFocusChangeListener();
        txtamount.setOnFocusChangeListener(ofcListener);

        edname.setOnFocusChangeListener(ofcListener);
        ednumber.setOnFocusChangeListener(ofcListener);


        Intent intent = getIntent();
        if (intent != null) {

            BillMenuParcelable bcp = intent.getParcelableExtra("bcp");


           /* billid = intent.getStringExtra("billid");
            serviceid = intent.getStringExtra("serviceid");
            servicename = intent.getStringExtra("servicename");
            servlabel = intent.getStringExtra("label");
            blname = intent.getStringExtra("billname");

            packid = intent.getStringExtra("packid");
            paycode = intent.getStringExtra("paymentCode");
            charge = intent.getStringExtra("charge");*/


            billid = bcp.getbillid();
            serviceid = bcp.getserviceid();
            servicename = bcp.getservicename();
            servlabel = bcp.getservlabel();
            blname = bcp.getbillname();

            packid = bcp.getpackid();
            paycode = bcp.getpaymentCode();
            charge = bcp.getcharge();

            String idd = bcp.getidd();

            String dispname = bcp.getdispname();
            if(Utility.isNotNull(charge)) {
                if (!(charge.equals("N"))) {
                    Log.v("Charge",charge);
                    if (!(charge.equals("0.0"))) {
                        txtamount.setText(charge);
                    }
                }
            }
            billname.setText(dispname);
            smcno.setText(servlabel);
            edacc.setHint(servlabel);


            String usid = Utility.gettUtilUserId(getApplicationContext());
            String agentid = Utility.gettUtilAgentId(getApplicationContext());
            String mobnoo = Utility.gettUtilMobno(getApplicationContext());


            String params = CHANNEL_ID+usid+"/"+agentid+"/"+mobnoo+"/"+idd;

               GetServv(params);
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
        if (view.getId() == R.id.button2) {

            if (Utility.checkInternetConnection(getApplicationContext())) {
                final String recanno = edacc.getText().toString();
                final String amou = txtamount.getText().toString();
                int mktindex = sp3.getSelectedItemPosition();
                if(mktindex >=0){
                final String narra = marketslist.get(sp3.getSelectedItemPosition()).getBenName();
                final String ednamee = edname.getText().toString();
                final String ednumbb = ednumber.getText().toString();
                if (Utility.isNotNull(recanno)) {
                    if (Utility.isNotNull(amou)) {
                        String nwamo = amou.replace(",", "");
                        SecurityLayer.Log("New Amount", nwamo);
                        double txamou = Double.parseDouble(nwamo);
                        if (txamou >= 100) {
                            if (Utility.isNotNull(narra)) {
                                if (Utility.isNotNull(ednamee)) {
                                    if (Utility.isNotNull(ednumbb)) {
                                        if (!(narra.equals("0000"))) {

                                            Intent intent = new Intent(StateCollectActivity.this, ConfirmCableActivity.class);


                                            //    PayBillResp(params);

                                            String marketname = marketslist.get(sp3.getSelectedItemPosition()).getBenmob();
                                            intent.putExtra("custid", recanno);
                                            intent.putExtra("amou", amou);
                                            intent.putExtra("narra", narra);
                                            intent.putExtra("ednamee", ednamee);
                                            intent.putExtra("ednumbb", ednumbb);
                                            intent.putExtra("billid", billid);
                                            intent.putExtra("billname", blname);
                                            intent.putExtra("serviceid", serviceid);
                                            intent.putExtra("servicename", servicename);
                                            intent.putExtra("label", servlabel);
                                            intent.putExtra("packId", narra);
                                            intent.putExtra("paymentCode", paycode);
                                            intent.putExtra("marketname", marketname);

                                            Log.v("new packid",narra);
                                            startActivity(intent);

                                     /*   BillMenuParcelable bcp = new BillMenuParcelable(serviceid,servicename,billid,blname,null,servlabel,null,paycode,packid,charge,null,recanno,amou,narra,ednamee,ednumbb);
                                        intent.putExtra("bcp",bcp);


                                        startActivity(intent);*/
                                      /*  Bundle b  = new Bundle();
                                        b.putString("custid",recanno);
                                        b.putString("amou",amou);
                                        b.putString("narra",narra);
                                        b.putString("ednamee",ednamee);
                                        b.putString("ednumbb",ednumbb);
                                        b.putString("billid",billid);
                                        b.putString("billname",blname);
                                        b.putString("serviceid",serviceid);
                                        b.putString("servicename",servicename);
                                        b.putString("label",servlabel);
                                        b.putString("packId",packid);
                                        b.putString("paymentCode",paycode);
                                        Fragment fragment = new ConfirmCableTV();

                                        fragment.setArguments(b);
                                        FragmentManager fragmentManager = getFragmentManager();
                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                        //  String tag = Integer.toString(title);
                                        fragmentTransaction.replace(R.id.container_body, fragment,"Confirm Cable");
                                        fragmentTransaction.addToBackStack("Confirm Cable");
                                        ((FMobActivity)getApplicationContext())
                                                .setActionBarTitle("Confirm Cable");
                                        fragmentTransaction.commit();*/
                                        } else {
                                            Toast.makeText(
                                                    getApplicationContext(),
                                                    "Please enter a valid value for Market",
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
                                    "Please enter an amount value more than 100 Naira",
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
                            "Please enter a value for " + servlabel,
                            Toast.LENGTH_LONG).show();
                }
            }else{
                    Toast.makeText(
                            getApplicationContext(),
                            "Please enter a valid value for Market",
                            Toast.LENGTH_LONG).show();
                }
            }

            String amo = amon.getText().toString();
            String custid = edacc.getText().toString();

            //   checkInternetConnection2();

        }
        if (view.getId() == R.id.tv2) {
          /*  Fragment  fragment = new BillMenu();


            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment,"Biller Menu");
            fragmentTransaction.addToBackStack("Biller Menu");
            ((FMobActivity)getApplicationContext())
                    .setActionBarTitle("Biller Menu");
            fragmentTransaction.commit();*/


            finish();
            Intent intent  = new Intent(StateCollectActivity.this,BillMenuActivity.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);
        }
        if (view.getId() == R.id.tv) {

            onBackPressed();
          /*  finish();
            Intent intent  = new Intent(CableTVActivity.this,SpecBillerMenuActivity.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("serviceid",serviceid);
            intent.putExtra("servicename",servicename);
            startActivity(intent);*/
           /* Bundle b  = new Bundle();
            b.putString("serviceid",serviceid);
            b.putString("servicename",servicename);


            Fragment    fragment = new SpecBillMenu();
            String  title = servicename;


            if (fragment != null) {
                fragment.setArguments(b);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                //  String tag = Integer.toString(title);
                fragmentTransaction.replace(R.id.container_body, fragment, title);
                fragmentTransaction.addToBackStack(title);
                ((FMobActivity)getApplicationContext())
                        .setActionBarTitle(title);
                fragmentTransaction.commit();
            }*/
        }

    }


    public void SetDialog(String msg,String title){
        new MaterialDialog.Builder(this)
                .title(title)
                .content(msg)

                .negativeText("Close")
                .show();
    }

    private class MyFocusChangeListener implements View.OnFocusChangeListener {

        public void onFocusChange(View v, boolean hasFocus){

            if(v.getId() == R.id.amount && !hasFocus) {
                InputMethodManager imm =  (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                String txt = txtamount.getText().toString();
                String fbal = Utility.returnNumberFormat(txt);
                txtamount.setText(fbal);

            }

            if(v.getId() == R.id.ednarr && !hasFocus) {
                InputMethodManager imm =  (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);


            }
            if(v.getId() == R.id.sendname && !hasFocus) {
                InputMethodManager imm =  (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);


            }
            if(v.getId() == R.id.sendnumber && !hasFocus) {
                InputMethodManager imm =  (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);


            }

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



    private void GetServv(String params) {
        prgDialog2.show();
        String endpoint= "billpayment/lpam.action";


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


                    SecurityLayer.Log("Cable TV Resp", response.body());
                    SecurityLayer.Log("response..:", response.body());
                    JSONObject obj = new JSONObject(response.body());
                    //obj = Utility.onresp(obj,getApplicationContext());
                    obj = SecurityLayer.decryptTransaction(obj, getApplicationContext());
                    SecurityLayer.Log("decrypted_response", obj.toString());



                    JSONObject newobj = obj.getJSONObject("data");

                    JSONArray servdata = newobj.optJSONArray("makets");
                    //session.setString(SecurityLayer.KEY_APP_ID,appid);

                    if(!(response.body() == null)) {
                        String respcode = obj.optString("responseCode");
                        String responsemessage = obj.optString("message");

                        SecurityLayer.Log("Response Message", responsemessage);

                        if (Utility.isNotNull(respcode) && Utility.isNotNull(respcode)) {
                            if (!(Utility.checkUserLocked(respcode))) {
                                SecurityLayer.Log("Response Message", responsemessage);

                                if (respcode.equals("00")){
                                    SecurityLayer.Log("JSON Aray", servdata.toString());
                                    if(servdata.length() > 0){


                                        JSONObject json_data = null;
                                        for (int i = 0; i < servdata.length(); i++) {
                                            json_data = servdata.getJSONObject(i);
                                            //String accid = json_data.getString("benacid");


                                            String marketName = json_data.optString("marketName");

                                            String id = json_data.optString("id");

                                            marketslist.add(new BenList(id,marketName));




                                        }

                                        if(!(marketslist == null)) {
                                            if(marketslist.size() > 0) {
                                                Collections.sort(marketslist, new Comparator<BenList>(){
                                                    public int compare(BenList d1, BenList d2){
                                                        return d1.getBenmob().compareTo(d2.getBenmob());
                                                    }
                                                });
                                                BenList sa = new BenList("0000","Select Market");

                                                marketslist.add(sa);
                                                mobadapt = new ArrayAdapter<BenList>(StateCollectActivity.this, R.layout.my_spinner, marketslist);
                                                mobadapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                sp3.setAdapter(mobadapt);

                                                  sp3.setSelection(marketslist.size() -1);
                                            }else{
                                                Toast.makeText(
                                                        getApplicationContext(),
                                                        "No states available  ",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        }else{

                                        }

                                    }else{

                                    }

                                }else{
                                    Toast.makeText(
                                            getApplicationContext(),
                                            "" + responsemessage,
                                            Toast.LENGTH_LONG).show();
                                }
                            } else {
                               /* getApplicationContext().finish();
                                startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                                Toast.makeText(
                                        getApplicationContext(),
                                        "You have been locked out of the app.Please call customer care for further details",
                                        Toast.LENGTH_LONG).show();*/

                                LogOut();

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
                    // prgDialog2.dismiss();




                } catch (JSONException e) {

                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    // TODO Auto-generated catch block
                    if(!(getApplicationContext() == null)) {
                        Toast.makeText(getApplicationContext(), getApplicationContext().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                        SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getApplicationContext());
                        // SecurityLayer.Log(e.toString());
                    }

                } catch (Exception e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());

                    if(!(getApplicationContext() == null)) {
                        SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getApplicationContext());
                    }
                    // SecurityLayer.Log(e.toString());
                }

                prgDialog2.dismiss();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                // Log error here since request failed
                SecurityLayer.Log("throwable error",t.toString());

                if(!(getApplicationContext() == null)) {
                    Toast.makeText(
                            getApplicationContext(),
                            "There was an error on your request",
                            Toast.LENGTH_LONG).show();
                    SetForceOutDialog(getString(R.string.forceout),getString(R.string.forceouterr),getApplicationContext());

                }



                prgDialog2.dismiss();
            }
        });

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
