package com.stanbicagent;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.ActionBar;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.stanbicagent.R;
import com.afollestad.materialdialogs.MaterialDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import adapter.adapter.OTBAdapt;
import adapter.adapter.OTBList;
import rest.ApiInterface;
import rest.ApiSecurityClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import security.SecurityLayer;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.stanbicagent.ApplicationConstants.CHANNEL_ID;

public class SendOTBActivity extends BaseActivity implements View.OnClickListener {
    ImageView imageView1;

    EditText amon, edacc,pno,txtamount,txtnarr,edname,ednumber;
    Button btnsub;
    String bankname,bankcode,recanno;
    SessionManagement session;
    ProgressDialog prgDialog;
    private static int SPLASH_TIME_OUT = 2500;
    String depositid;
    OTBAdapt aAdpt;
    TextView bankchosen,bankselected;
    LinearLayout linearLayoutMine;
    List<OTBList> planetsList = new ArrayList<OTBList>();
    MaterialDialog builder;
    EditText accountoname;
    String acname;
    TextView step2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_otb);
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
        edacc = (EditText)findViewById(R.id.input_payacc);
        txtamount = (EditText) findViewById(R.id.amount);
        txtnarr = (EditText) findViewById(R.id.ednarr);
        edname = (EditText) findViewById(R.id.sendname);
        ednumber = (EditText) findViewById(R.id.sendnumber);
        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Loading Account Details....");
        // Set Cancelable as False

        prgDialog.setCancelable(false);



        bankchosen = (TextView) findViewById(R.id.textVip);
        bankselected = (TextView) findViewById(R.id.textVipp);
        bankselected.setOnClickListener(this);

        btnsub = (Button) findViewById(R.id.button2);
        accountoname = (EditText)findViewById(R.id.cname);

        View.OnFocusChangeListener ofcListener = new MyFocusChangeListener();
        txtamount.setOnFocusChangeListener(ofcListener);
        txtnarr.setOnFocusChangeListener(ofcListener);
        edacc.setOnFocusChangeListener(ofcListener);
        edname.setOnFocusChangeListener(ofcListener);
        ednumber.setOnFocusChangeListener(ofcListener);
        btnsub.setOnClickListener(this);

        step2 = (TextView) findViewById(R.id.tv2);
        step2.setOnClickListener(this);



        edacc.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edacc.getText().toString().length() == 10) {
                    if (!(getApplicationContext() == null)) {
                     /*   if (!(bankcode == null)) {*/

                        if (Utility.checkInternetConnection(getApplicationContext())) {
                            Utility.hideKeyboardFrom(getApplicationContext(), edacc);
                            if (!(prgDialog == null)) {
                                String acno = edacc.getText().toString();
                                  /*  prgDialog.show();

                                    String acno = edacc.getText().toString();
                                    String usid = Utility.gettUtilUserId(getApplicationContext());
                                    String agentid = Utility.gettUtilAgentId(getApplicationContext());
                                    String mobnoo = Utility.gettUtilMobno(getApplicationContext());
                                    String params = CHANNEL_ID + usid + "/" + agentid + "/" + mobnoo + "/" + bankcode + "/" + acno;
                                    NameInquirySec(params);
                                }*/showNubanDialog(acno);
                            }
                        }
                      /*  } else {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Please select a bank ",
                                    Toast.LENGTH_LONG).show();
                        }*/
                        // TODO Auto-generated method stub
                    }
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


        ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(
                this, R.array.banks, android.R.layout.simple_spinner_item);
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }


    public void showNubanDialog(String recanno) {
        FragmentManager fm = getSupportFragmentManager();
        DialogNubanBanks nubbanks = new DialogNubanBanks();
        Bundle bundle = new Bundle();
        bundle.putString("recanno",recanno);
        nubbanks.setArguments(bundle);
        nubbanks.show(fm, "fragment_edit_name");
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
            if(Utility.isNotNull(bankname)) {
                final String recanno = edacc.getText().toString();
                final String amou = txtamount.getText().toString();
                final String narra = txtnarr.getText().toString();
                String ednamee = edname.getText().toString();
                String ednumbb = ednumber.getText().toString();
                if (Utility.isNotNull(recanno)) {
                    if (Utility.isNotNull(amou)) {
                        String nwamo = amou.replace(",", "");
                        SecurityLayer.Log("New Amount",nwamo);
                        double txamou = Double.parseDouble(nwamo);
                       /* if (txamou >= 100) {*/
                        if (Utility.isNotNull(narra)) {
                            if (Utility.isNotNull(ednamee)) {
                                if (Utility.isNotNull(ednumbb)) {
                                    if (Utility.isNotNull(bankcode)) {
                                     //   if (Utility.isNotNull(acname)) {


                                            Intent intent  = new Intent(SendOTBActivity.this,ConfirmSendOTBActivity.class);

                                            intent.putExtra("recanno",recanno);
                                            intent.putExtra("amou",amou);
                                            intent.putExtra("narra",narra);
                                            intent.putExtra("ednamee",ednamee);
                                            intent.putExtra("ednumbb",ednumbb);
                                            intent.putExtra("txtname",acname);
                                            intent.putExtra("bankname",bankname);
                                            intent.putExtra("bankcode",bankcode);


                                            startActivity(intent);

                                          /*  Bundle b  = new Bundle();
                                            b.putString("recanno",recanno);
                                            b.putString("amou",amou);
                                            b.putString("narra",narra);
                                            b.putString("ednamee",ednamee);
                                            b.putString("ednumbb",ednumbb);
                                            b.putString("txtname",acname);
                                            b.putString("bankname",bankname);
                                            b.putString("bankcode",bankcode);
                                            Fragment fragment = new ConfirmSendOTB();

                                            fragment.setArguments(b);
                                            FragmentManager fragmentManager = getFragmentManager();
                                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                            //  String tag = Integer.toString(title);
                                            fragmentTransaction.replace(R.id.container_body, fragment,"Confirm Other Bank");
                                            fragmentTransaction.addToBackStack("Confirm Other Bank");
                                            ((FMobActivity)getApplicationContext())
                                                    .setActionBarTitle("Confirm Other Bank");
                                            fragmentTransaction.commit();*/
                                       /* }  else {
                                            Toast.makeText(
                                                    getApplicationContext(),
                                                    "Please enter a valid account number",
                                                    Toast.LENGTH_LONG).show();
                                        }*/
                                    } else {
                                        Toast.makeText(
                                                getApplicationContext(),
                                                "Please select a bank",
                                                Toast.LENGTH_LONG).show();
                                    }
                                } else {
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
                     /*   } else {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Please enter a valid amount more than 100 Naira",
                                    Toast.LENGTH_LONG).show();
                        }*/
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



            else{
                Toast.makeText(getApplicationContext(), "Please select a bank", Toast.LENGTH_LONG).show();

            }

        }
        if (view.getId() == R.id.textVipp) {
            //   SetDialog("Select Bank");

           /* Fragment  fragment = new OtherBankPage();


            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment,"Select Bank");
            fragmentTransaction.addToBackStack("Select Bank");
            ((FMobActivity)getApplicationContext())
                    .setActionBarTitle("Select Bank");
            fragmentTransaction.commit();*/

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



            Intent intent  = new Intent(SendOTBActivity.this,FTMenuActivity.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);
        }
    }
    public void SetDialog(String title){
        LayoutInflater factory = LayoutInflater.from(getApplicationContext());
        final View stdView = factory.inflate(R.layout.dialoglistview, null);
        linearLayoutMine  = (LinearLayout) stdView.findViewById(R.id.lay);

        final ListView lv = (ListView) stdView.findViewById(R.id.lv);

        planetsList.add(new OTBList("Access Bank PLC","057"));
        planetsList.add(new OTBList("Aso Savings","058"));
        planetsList.add(new OTBList("CitiBank Nigeria LTD","059"));
        planetsList.add(new OTBList("Diamond Bank Plc","057"));
        planetsList.add(new OTBList("Ecobank Nigeria Plc","058"));
        planetsList.add(new OTBList("Enterprise Bank","059"));
        planetsList.add(new OTBList("Fidelity Bank Plc","057"));
        planetsList.add(new OTBList("First City Monument Bank Plc","058"));
        planetsList.add(new OTBList("Guaranty Trust Bank Plc","059"));
        planetsList.add(new OTBList("Heritage Bank","057"));
        planetsList.add(new OTBList("JAIZ Bank","058"));
        planetsList.add(new OTBList("Keystone Bank","059"));
        planetsList.add(new OTBList("Mainstreet Bank","059"));
        planetsList.add(new OTBList("Parallex MFB","057"));
        planetsList.add(new OTBList("Skye Bank Plc","058"));
        planetsList.add(new OTBList("StanbicIBTC Bank Plc","059"));

        planetsList.add(new OTBList("Standard Chartered Bank of Nigeria","058"));
        planetsList.add(new OTBList("Sterling Bank Plc","059"));
        planetsList.add(new OTBList("Union Bank of Nigeria Plc","059"));
        planetsList.add(new OTBList("United Bank for Africa Plc","057"));
        planetsList.add(new OTBList("Unity Bank Plc","058"));
        planetsList.add(new OTBList("Wema Bank Plc","059"));
        planetsList.add(new OTBList("Access Bank","059"));
        aAdpt = new OTBAdapt(planetsList, getApplicationContext());
        lv.setAdapter(aAdpt);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                String benname = planetsList.get(position).getBenName();
                bankchosen.setText(benname);
                builder.dismiss();

            }
        });
        builder =   new MaterialDialog.Builder(getApplicationContext())
                .title(title)

                .customView(linearLayoutMine,true)

                .negativeText("Close")

                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                     /*   Toast.makeText(
                                getApplicationContext(),
                                "You have successfully added an offer",
                                Toast.LENGTH_LONG).show();*/

                    }
                })
                .show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();    //Call the back button's method
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void SetDialog(String msg,String title){
        new MaterialDialog.Builder(getApplicationContext())
                .title(title)
                .content(msg)

                .negativeText("Close")
                .show();
    }

    private void NameInquirySec(String params) {
        prgDialog.show();
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
                                        "Account Name: " + acname,
                                        Toast.LENGTH_LONG).show();
                                accountoname.setText(acname);
                            } else {

                                Toast.makeText(
                                        getApplicationContext(),
                                        "This is not a valid account number.Please check again",
                                        Toast.LENGTH_LONG).show();


                            }

                        }else{
                            if (!(getApplicationContext() == null)) {
                                Toast.makeText(
                                        getApplicationContext(),
                                        responsemessage,
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    } else {
                        if (!(getApplicationContext() == null)) {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "There was an error processing your request ",
                                    Toast.LENGTH_LONG).show();
                        }
                    }



                } catch (JSONException e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    // TODO Auto-generated catch block
                    if (!(getApplicationContext() == null)) {
                        Toast.makeText(getApplicationContext(), getApplicationContext().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                       SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getApplicationContext());
                        // SecurityLayer.Log(e.toString());
                    }

                } catch (Exception e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    if (!(getApplicationContext() == null)) {
                        SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getApplicationContext());
                        // SecurityLayer.Log(e.toString());
                    }
                }
                if((!(getApplicationContext() == null)) && !(prgDialog == null) && prgDialog.isShowing()) {
                    prgDialog.dismiss();

                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                SecurityLayer.Log("Throwable error",t.toString());

                if((!(getApplicationContext() == null)) && !(prgDialog == null) && prgDialog.isShowing()) {
                    prgDialog.dismiss();
                    Toast.makeText(
                            getApplicationContext(),
                            "There was an error processing your request",
                            Toast.LENGTH_LONG).show();
                   SetForceOutDialog(getString(R.string.forceout),getString(R.string.forceouterr),getApplicationContext());


                }
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();

        if ((getApplicationContext() != null && prgDialog != null) && prgDialog.isShowing()) {
            prgDialog.dismiss();

        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onResume() {
        super.onResume();

        bankname = session.getString("bankname");
        bankcode = session.getString("bankcode");
        recanno = session.getString("recanno");

        if (Utility.isNotNull(bankname) && Utility.isNotNull(bankcode) && Utility.isNotNull(recanno)) {
SecurityLayer.Log("Inside If","Inside If");
         //   edacc.setText(recanno);
            bankselected.setText("Change Bank");


            String usid = Utility.gettUtilUserId(getApplicationContext());
            String agentid = Utility.gettUtilAgentId(getApplicationContext());
            String mobnoo = Utility.gettUtilMobno(getApplicationContext());
            String params = CHANNEL_ID + usid + "/" + bankcode + "/" + recanno;
            NameInquirySec(params);
        }else{
            SecurityLayer.Log("Outside If","Outside If");
        }

        if(Utility.isNotNull(bankname)){
            bankchosen.setText(bankname);
        }
    }

    public void RunNuban(){
        bankname = session.getString("bankname");
        bankcode = session.getString("bankcode");
        recanno = session.getString("recanno");

        if (Utility.isNotNull(bankname) && Utility.isNotNull(bankcode) && Utility.isNotNull(recanno)) {
            SecurityLayer.Log("Inside If","Inside If");
       //     edacc.setText(recanno);
            bankselected.setText("Change Bank");


            String usid = Utility.gettUtilUserId(getApplicationContext());
            String agentid = Utility.gettUtilAgentId(getApplicationContext());
            String mobnoo = Utility.gettUtilMobno(getApplicationContext());
            String params = CHANNEL_ID + usid + "/" + bankcode + "/" + recanno;
            NameInquirySec(params);
        }else{
            SecurityLayer.Log("Outside If","Outside If");
        }

        if(Utility.isNotNull(bankname)){
            bankchosen.setText(bankname);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        session.setString("bankname",null);
        session.setString("bankcode",null);
        session.setString("recanno",null);

        if(prgDialog!=null && prgDialog.isShowing()){

            prgDialog.dismiss();
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



}
