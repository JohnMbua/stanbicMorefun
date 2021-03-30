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
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

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

public class ConfirmCashtransActivity extends BaseActivity implements View.OnClickListener {
    TextView recacno,recname,recamo,recnarr,recsendnum,recsendnam,txtfee;
    Button btnsub;
    String recanno, amou ,narra, ednamee,ednumbb,txtname,agbalance,trantype;
    ProgressDialog prgDialog2;
    EditText etpin;
    RelativeLayout rlsendname,rlsendno;
    TextView step1,step2,acbal;
    SessionManagement session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_cashtrans);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        // Get the ActionBar here to configure the way it behaves.
        final ActionBar ab = getSupportActionBar();
        //ab.setHomeAsUpIndicator(R.drawable.ic_menu); // set a custom icon for the default home button
        ab.setDisplayShowHomeEnabled(true); // show or hide the default home button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
        ab.setDisplayShowTitleEnabled(false); // disable the default title element here (for centered title)
        recacno = (TextView)findViewById(R.id.textViewnb2);
        recname = (TextView)findViewById(R.id.textViewcvv);
        acbal = (TextView) findViewById(R.id.txtacbal);
        etpin = (EditText) findViewById(R.id.pin);
        session = new SessionManagement(this);
        recamo = (TextView)findViewById(R.id.textViewrrs);
        recnarr = (TextView) findViewById(R.id.textViewrr);
        txtfee = (TextView) findViewById(R.id.txtfee);
        step1 = (TextView) findViewById(R.id.tv);
        step1.setOnClickListener(this);
        step2 = (TextView) findViewById(R.id.tv2);
        step2.setOnClickListener(this);
        recsendnam = (TextView) findViewById(R.id.sendnammm);
        recsendnum = (TextView)findViewById(R.id.sendno);

        rlsendname = (RelativeLayout) findViewById(R.id.rlsendnam);
        rlsendno = (RelativeLayout) findViewById(R.id.rlsendnum);
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
            trantype =  intent.getStringExtra("trantype");
            if(trantype.equals("D")){
                rlsendname.setVisibility(View.GONE);
                rlsendno.setVisibility(View.GONE);
            }
            recacno.setText(recanno);
            recname.setText(txtname);

            recamo.setText(ApplicationConstants.KEY_NAIRA+amou);
            recnarr.setText(narra);

            recsendnam.setText(ednamee);
            recsendnum.setText(ednumbb);
            amou = Utility.convertProperNumber(amou);
            getFeeSec();
        }
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.button2) {
            if (Utility.checkInternetConnection(getApplicationContext())) {
                String agpin  = etpin.getText().toString();


                if (Utility.isNotNull(recanno)) {
                    if (Utility.isNotNull(amou)) {
                        if (Utility.isNotNull(narra)) {
                            if (Utility.isNotNull(ednamee)) {
                                if (Utility.isNotNull(ednumbb)) {
                                    if (Utility.isNotNull(agpin)) {


                                            String encrypted = null;
                                            String pubkey = "97206B46CE46376894703ECE161F31F2";
                                            encrypted = Utility.b64_sha256(agpin);
                                            if(!(prgDialog2 == null)) {
                                                //   prgDialog2.show();
                                            }

                                            String usid = Utility.gettUtilUserId(getApplicationContext());
                                            String agentid = Utility.gettUtilAgentId(getApplicationContext());
                                            String mobnoo = Utility.gettUtilMobno(getApplicationContext());
                                            String channelid = Long.toString(System.nanoTime());
//                                            String params = CHANNEL_ID+usid+"/"+CHANNEL_ID+amou+"/"+recanno+"/"+txtname+"/"+narra+"/"+ednamee+"/"+ednumbb+"/"+channelid;
                                          String params = CHANNEL_ID+usid+"/"+"2/"+amou+"/"+recanno+"/"+txtname+"/"+narra+"/"+ednamee+"/"+ednumbb+"/"+channelid;


                                            // IntraBankResp(params);

                                            Bundle b  = new Bundle();
                                            b.putString("params",params);
                                           if(trantype.equals("D")){
                                               b.putString("serv", "CASHTRAN");
                                           }else {
                                               b.putString("serv", "CASHDEPO");
                                           }
                                            b.putString("recanno", recanno);
                                            b.putString("amou", amou);

                                            b.putString("narra", narra);
                                            b.putString("ednamee", ednamee);
                                            b.putString("ednumbb", ednumbb);
                                            b.putString("txtname", txtname);
                                            b.putString("txpin", encrypted);
                                           /* Fragment fragment = new TransactingProcessing();

                                            fragment.setArguments(b);
                                            FragmentManager fragmentManager = getFragmentManager();
                                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                            //  String tag = Integer.toString(title);
                                            fragmentTransaction.replace(R.id.container_body, fragment,"Confirm Transfer");
                                            fragmentTransaction.addToBackStack("Confirm Transfer");
                                            ((FMobActivity)getApplicationContext())
                                                    .setActionBarTitle("Confirm Transfer");
                                            fragmentTransaction.commit();*/


                                            Intent intent  = new Intent(ConfirmCashtransActivity.this,TransactionProcessingActivity.class);
                                            intent.putExtra("params",params);
                                            intent.putExtra("serv","CASHTRAN");
                                            if(trantype.equals("D")){
                                                intent.putExtra("serv", "CASHTRAN");
                                            }else {
                                                intent.putExtra("serv", "CASHDEPO");
                                            }
                                            intent.putExtra("recanno", recanno);
                                            intent.putExtra("amou", amou);

                                            intent.putExtra("narra", narra);
                                            intent.putExtra("ednamee", ednamee);
                                            intent.putExtra("ednumbb", ednumbb);
                                            intent.putExtra("txtname", txtname);
                                            intent.putExtra("txpin", encrypted);
                                            startActivity(intent);
                                                /*    ApiInterface apiService =
                                                            ApiClient.getClient().create(ApiInterface.class);
                                    String usid = Utility.gettUtilUserId(getApplicationContext());
                                    String agentid = Utility.gettUtilAgentId(getApplicationContext());
                                    String mobnoo = Utility.gettUtilMobno(getApplicationContext());
                                        // "0000"
                                                    Call<IntraBank> call = apiService.getIntraBankResp("1",usid,agentid,"0000","1",amou,recanno,txtname,narra,encrypted);
                                                    call.enqueue(new Callback<IntraBank>() {
                                                        @Override
                                                        public void onResponse(Call<IntraBank>call, Response<IntraBank> response) {

                                                        if(!(response.body() == null)) {
                                                            String responsemessage = response.body().getMessage();
                                                            String respcode = response.body().getRespCode();

                                                            String agcmsn = response.body().getFee();
//                                                            SecurityLayer.Log("Response Message", responsemessage);
                                                            IntraBankData datas = response.body().getResults();
                                                            if (Utility.isNotNull(respcode) && Utility.isNotNull(responsemessage)) {
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
           /* Fragment  fragment = new CashDepoTrans();


            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment,"Cash Depo Trans");
            fragmentTransaction.addToBackStack("Cash Depo Trans");
            ((FMobActivity)getApplicationContext())
                    .setActionBarTitle("Cash Depo Trans");
            fragmentTransaction.commit();*/

            finish();



            Intent intent  = new Intent(ConfirmCashtransActivity.this,CashDepoTransActivity.class);

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

            finish();



            Intent intent  = new Intent(ConfirmCashtransActivity.this,FTMenuActivity.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);
        }
    }

    public void getFee(){
        if(!(prgDialog2 == null) ) {
            prgDialog2.show();
        }
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        String usid = Utility.gettUtilUserId(getApplicationContext());
        String agentid = Utility.gettUtilAgentId(getApplicationContext());
        Call<GetFee> call = apiService.getFee("1", usid, agentid, "FTINTRABANK",  amou);
        call.enqueue(new Callback<GetFee>() {
            @Override
            public void onResponse(Call<GetFee> call, Response<GetFee> response) {
                if (!(response.body() == null)) {
                    String responsemessage = response.body().getMessage();
                    String respfee = response.body().getFee();
                    SecurityLayer.Log("Response Message", responsemessage);
                    if(respfee == null || respfee.equals("")){
                        txtfee.setText("N/A");
                    }else{
                        respfee = Utility.returnNumberFormat(respfee);
                        txtfee.setText(ApplicationConstants.KEY_NAIRA+respfee);
                    }
                    if(!(prgDialog2 == null) && prgDialog2.isShowing() && getApplicationContext() != null) {
                        prgDialog2.dismiss();
                    }
                }else{
                    txtfee.setText("N/A");
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
                if(!(prgDialog2 == null) && prgDialog2.isShowing() && getApplicationContext() != null) {
                    prgDialog2.dismiss();
                }
            }
        });
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
        if(!(prgDialog2 == null) ) {
            prgDialog2.show();
        }
        String endpoint= "fee/getfee.action";


        String usid = Utility.gettUtilUserId(getApplicationContext());
        String agentid = Utility.gettUtilAgentId(getApplicationContext());

        String params = CHANNEL_ID+usid+"/"+agentid+"/FTINTRABANK/"+amou;
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



                                   /* Fragment fragment = new CashDepoTrans();
                                    String title = "Cash Transfer";

                                    FragmentManager fragmentManager = getFragmentManager();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    //  String tag = Integer.toString(title);
                                    fragmentTransaction.replace(R.id.container_body, fragment, title);
                                    fragmentTransaction.addToBackStack(title);
                                    ((FMobActivity) getApplicationContext())
                                            .setActionBarTitle(title);
                                    fragmentTransaction.commit();*/
                                    onBackPressed();
                                    Toast.makeText(
                                            getApplicationContext(),
                                            responsemessage,
                                            Toast.LENGTH_LONG).show();

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
                if(!(prgDialog2 == null) && prgDialog2.isShowing() && getApplicationContext() != null) {
                    prgDialog2.dismiss();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                SecurityLayer.Log("Throwable error",t.toString());

                if(!(prgDialog2 == null) && prgDialog2.isShowing() && getApplicationContext() != null) {
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();    //Call the back button's method
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void IntraBankResp(String params) {

        String endpoint= "transfer/intrabank.action";


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


                    SecurityLayer.Log("Intra Bank Resp", response.body());
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
                                if(respcode.equals("00")){
                                    String totfee = "0.00";
                                    if(!(datas == null)){
                                        totfee = datas.optString("fee");
                                    }
                                    Bundle b  = new Bundle();

                                    Intent intent  = new Intent(ConfirmCashtransActivity.this,TransactionProcessingActivity.class);



                                    intent.putExtra("recanno",recanno);
                                    intent.putExtra("amou",amou);
                                    intent.putExtra("narra",narra);
                                    String refcodee = datas.optString("referenceCode");
                                    intent.putExtra("refcode",refcodee);
                                    intent.putExtra("ednamee",ednamee);
                                    intent.putExtra("ednumbb",ednumbb);
                                    intent.putExtra("txtname",txtname);
                                    intent.putExtra("trantype","T");
                                    intent.putExtra("agcmsn",agcmsn);
                                    intent.putExtra("fee",totfee);

                                    startActivity(intent);
                                   /* Fragment  fragment = new FinalConfDepoTrans();

                                    fragment.setArguments(b);
                                    FragmentManager fragmentManager = getFragmentManager();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    //  String tag = Integer.toString(title);
                                    fragmentTransaction.replace(R.id.container_body, fragment,"Confirm Transfer");
                                    fragmentTransaction.addToBackStack("Confirm Transfer");
                                    ((FMobActivity)getApplicationContext())
                                            .setActionBarTitle("Confirm Transfer");
                                    fragmentTransaction.commit();*/
                                }else {
                                    new MaterialDialog.Builder(getApplicationContext())
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
                                    Bundle params = new Bundle();
                                    params.putString("transfer_error", responsemessage);
                                    params.putString("response_code", respcode);


                                    Answers.getInstance().logCustom(new CustomEvent("cash_deposit error code")

                                            .putCustomAttribute("deposit_error",responsemessage)
                                            .putCustomAttribute("response_code",respcode)
                                    );

                                /*    Toast.makeText(
                                            getApplicationContext(),
                                            "" + responsemessage,
                                            Toast.LENGTH_LONG).show();*/
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
                    }
                    // SecurityLayer.Log(e.toString());

                } catch (Exception e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    if(!(getApplicationContext() == null)) {
                        SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getApplicationContext());
                    }
                    // SecurityLayer.Log(e.toString());
                }
                if(!(prgDialog2 == null) && prgDialog2.isShowing() && getApplicationContext() != null) {
                    prgDialog2.dismiss();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                SecurityLayer.Log("throwable error",t.toString());





                if(!(prgDialog2 == null) && prgDialog2.isShowing() && getApplicationContext() != null) {
                    prgDialog2.dismiss();
                }
                if(!(getApplicationContext() == null)) {
                    Toast.makeText(
                            getApplicationContext(),
                            "There was an error on your request",
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
