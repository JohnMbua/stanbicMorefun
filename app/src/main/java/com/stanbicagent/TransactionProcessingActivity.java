package com.stanbicagent;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.stanbicagent.R;
import com.afollestad.materialdialogs.MaterialDialog;
import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.morefun.yapi.device.printer.FontFamily;
import com.morefun.yapi.device.printer.OnPrintListener;
import com.morefun.yapi.device.printer.PrinterConfig;
import com.morefun.yapi.engine.DeviceServiceEngine;
import com.vipul.hp_hp.library.Layout_to_Image;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import rest.ApiInterface;
import rest.ApiSecurityClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import security.SecurityLayer;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import utils.FileUtils;

public class TransactionProcessingActivity extends BaseActivity implements View.OnClickListener {
    Button btnsub;
    String recanno, amou, narra, ednamee, ednumbb, txtname, strfee, stragcms, bankname, bankcode, txpin, newparams, serv;
    String txtcustid, serviceid, billid, txtfee, strtref, strlabel, strbillnm, fullname, telcoop, marketnm;
    ProgressDialog prgDialog2;
    RelativeLayout rlsendname, rlsendno;
    EditText etpin;
    private FirebaseAnalytics mFirebaseAnalytics;
    String txtrfc, txref;
    Layout_to_Image layout_to_image;  //Create Object of Layout_to_Image Class
    TextView txstatus, txdesc;
    LinearLayout relativeLayout;   //Define Any Layout
    Button shareImage, repissue;
    Bitmap bitmap;                  //Bitmap for holding Image of layout
    private PinLockView mPinLockView;
    private IndicatorDots mIndicatorDots;
    Button btnconfirm;
    ProgressDialog pro;
    String finpin;
    SessionManagement session;
    SharedPreferences pref;
    String tid, mid, mer_name, loc, ccode, user_only, fld_43, rpc_code, rpc_message, txn_status, rrn, totfee, send_date, send_time, auth_id, batch,
            stan, appvers, mTypeFacePath = "";
    DeviceServiceEngine mSDKManager;

    private PinLockListener mPinLockListener = new PinLockListener() {
        @Override
        public void onComplete(String pin) {
            finpin = pin;
        }

        @Override
        public void onEmpty() {
            SecurityLayer.Log("Pin Empty", "Pin empty");
        }

        @Override
        public void onPinChange(int pinLength, String intermediatePin) {
            //	SecurityLayer.Log(TAG, "Pin changed, new length " + pinLength + " with intermediate pin " + intermediatePin);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_processing);
        SDKManager.getInstance().bindService(getApplicationContext());
        prgDialog2 = new ProgressDialog(this);
        prgDialog2.setMessage("Loading....");
        prgDialog2.setCancelable(false);
        rlsendname = (RelativeLayout) findViewById(R.id.rlsendnam);
        rlsendno = (RelativeLayout) findViewById(R.id.rlsendnum);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayShowHomeEnabled(true); // show or hide the default home button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
        ab.setDisplayShowTitleEnabled(false); // disable the default title element here (for centered title)

        appvers = Utility.getAppVersion(getApplicationContext());
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        user_only = pref.getString("user_only", "");
        tid = pref.getString("tid", "");
        mid = pref.getString("mid", "");
        mer_name = pref.getString("mer_name", "");
        loc = pref.getString("loc", "");
        ccode = pref.getString("ccode", "");
        fld_43 = pref.getString("fld_43", "");

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat sdate_df = new SimpleDateFormat("dd MMM yyyy");
        SimpleDateFormat stime_df = new SimpleDateFormat("HH:mm");
        SimpleDateFormat stan_df = new SimpleDateFormat("ddHHmm");
        send_date = sdate_df.format(c);
        send_time = stime_df.format(c);
        stan = stan_df.format(c);
        batch = stan_df.format(c);

        txstatus = (TextView) findViewById(R.id.txstatus);
        txdesc = (TextView) findViewById(R.id.txdesc);
        btnsub = (Button) findViewById(R.id.button2);
        btnsub.setOnClickListener(this);
        session = new SessionManagement(this);
        relativeLayout = (LinearLayout) findViewById(R.id.receipt);
        mTypeFacePath = initTypeFacePath();
        Intent intent = getIntent();
        if (intent != null) {
            mSDKManager = SDKManager.getInstance().getDeviceServiceEngine();
            if (mSDKManager == null) {
                SecurityLayer.Log("mSDKManager", "ServiceEngine is Null");
                return;
            }
            serv = intent.getStringExtra("serv");
            if (serv.equals("CASHDEPO")) {
                recanno = intent.getStringExtra("recanno");
                amou = intent.getStringExtra("amou");
                narra = intent.getStringExtra("narra");
                ednamee = intent.getStringExtra("ednamee");
                ednumbb = intent.getStringExtra("ednumbb");
                txtname = intent.getStringExtra("txtname");
                txtrfc = intent.getStringExtra("refcode");
                String params = intent.getStringExtra("params");
                stragcms = Utility.returnNumberFormat(intent.getStringExtra("agcmsn"));
                strfee = intent.getStringExtra("fee");
                txpin = intent.getStringExtra("txpin");
                newparams = params;
                SecurityLayer.Log("Params", newparams + "/" + txpin);
                Generic_Service_Calls("transfer/intrabank.action", newparams + "/" + txpin, FinalConfDepoActivity.class);
                //IntraDepoBankResp(newparams + "/" + txpin);
            }
            if (serv.equals("CASHTRAN")) {
                recanno = intent.getStringExtra("recanno");
                amou = intent.getStringExtra("amou");
                narra = intent.getStringExtra("narra");
                ednamee = intent.getStringExtra("ednamee");
                ednumbb = intent.getStringExtra("ednumbb");
                txtname = intent.getStringExtra("txtname");
                txtrfc = intent.getStringExtra("refcode");
                String params = intent.getStringExtra("params");
                stragcms = Utility.returnNumberFormat(intent.getStringExtra("agcmsn"));
                strfee = intent.getStringExtra("fee");
                txpin = intent.getStringExtra("txpin");
                newparams = params;
                SecurityLayer.Log("Params", newparams + "/" + txpin);
                Generic_Service_Calls("transfer/intrabank.action", newparams + "/" + txpin, FinalConfDepoActivity.class);
                //IntraTranBankResp(newparams + "/" + txpin);
            }
            if (serv.equals("WDRAW")) {
                recanno = intent.getStringExtra("recanno");
                amou = intent.getStringExtra("amou");
                strfee = intent.getStringExtra("fee");
                txtname = intent.getStringExtra("txtname");
                txref = intent.getStringExtra("txref");
                txtrfc = intent.getStringExtra("refcode");
                String params = intent.getStringExtra("params");
                stragcms = Utility.returnNumberFormat(intent.getStringExtra("agcmsn"));
                strfee = intent.getStringExtra("fee");
                txpin = intent.getStringExtra("txpin");
                newparams = params;
                SecurityLayer.Log("Params", newparams + "/" + txpin);
                Generic_Service_Calls("withdrawal/cashbyaccountconfirm.action", newparams + "/" + txpin, FinalConfWithdrawActivity.class);
                //WithdrawResp(newparams + "/" + txpin);
            }
            if (serv.equals("AIRT")) {
                strfee = intent.getStringExtra("fee");
                stragcms = Utility.returnNumberFormat(intent.getStringExtra("agcmsn"));
                strfee = intent.getStringExtra("fee");
                txtcustid = intent.getStringExtra("mobno");
                amou = intent.getStringExtra("amou");
                telcoop = intent.getStringExtra("telcoop");
                String params = intent.getStringExtra("params");
                String txtamou = Utility.returnNumberFormat(amou);
                if (txtamou.equals("0.00")) {
                    amou = txtamou;
                }
                billid = intent.getStringExtra("billid");
                serviceid = intent.getStringExtra("serviceid");
                txpin = intent.getStringExtra("txpin");
                newparams = params;
                SecurityLayer.Log("Params", newparams + "/" + txpin);
//                Generic_Service_Calls("billpayment/mobileRecharge.action", newparams + "/" + txpin, FinalConfAirtimeActivity.class);

                Generic_Service_Calls("billpayment/mobileRecharge.action", newparams, FinalConfAirtimeActivity.class);
                //AirtimeResp(newparams + "/" + txpin);
            }
            if (serv.equals("SENDOTB")) {
                recanno = intent.getStringExtra("recanno");
                amou = intent.getStringExtra("amou");
                narra = intent.getStringExtra("narra");
                ednamee = intent.getStringExtra("ednamee");
                ednumbb = intent.getStringExtra("ednumbb");
                txtname = intent.getStringExtra("txtname");
                bankname = intent.getStringExtra("bankname");
                bankcode = intent.getStringExtra("bankcode");
                strfee = intent.getStringExtra("fee");
                txtrfc = intent.getStringExtra("refcode");
                String params = intent.getStringExtra("params");
                txpin = intent.getStringExtra("txpin");
                newparams = params;
                SecurityLayer.Log("Params", newparams + "/" + txpin);
                Generic_Service_Calls("transfer/interbank.action", newparams + "/" + txpin, FinalConfSendOTBActivity.class);
                //InterBankResp(newparams + "/" + txpin);
            }
            if(serv.equals("CABLETV")) {
                txtcustid = intent.getStringExtra("custid");
                amou = intent.getStringExtra("amou");
                narra = intent.getStringExtra("narra");
                ednamee = intent.getStringExtra("ednamee");
                ednumbb = intent.getStringExtra("ednumbb");
                strlabel = intent.getStringExtra("label");
                billid = intent.getStringExtra("billid");
                strbillnm = intent.getStringExtra("billname");
                serviceid = intent.getStringExtra("serviceid");
                strfee = intent.getStringExtra("fee");
                strtref = intent.getStringExtra("tref");
                fullname = intent.getStringExtra("fullname");
                String params  = intent.getStringExtra("params");
                txpin = intent.getStringExtra("txpin");
                newparams = params;
                //SecurityLayer.Log("Params",newparams+"/"+txpin);
                String chaneelref = Long.toString(System.nanoTime());
                SecurityLayer.Log("Paybill Params",newparams+"/"+txpin+"/"+chaneelref);
                PayBillResp(newparams+"/"+txpin+"/"+chaneelref);
            }
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void Generic_Service_Calls(String endpoint, String params, final Class activity_name){
        prgDialog2.show();
        String urlparams = "";
        try {
            urlparams = SecurityLayer.genURLCBC(params, endpoint, getApplicationContext());
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
                    SecurityLayer.Log("response..:", response.body());
                    JSONObject obj = new JSONObject(response.body());
                    obj = SecurityLayer.decryptTransaction(obj, getApplicationContext());
                    SecurityLayer.Log("decrypted_response", obj.toString());
                    JSONObject datas = obj.optJSONObject("data");
                    if (!(response.body() == null)) {
                        String respcode = obj.optString("responseCode");
                        rpc_code = obj.optString("responseCode");
                        String responsemessage = obj.optString("message");
                        rpc_message = obj.optString("message");
                        String agcmsn = obj.optString("AgentCommission");//("commission");// from fee
                        String refcodee = "";
                        if (!(datas == null)) {
                            if(datas.has("ReferenceId")) {
                                rrn = datas.optString("ReferenceId");
                                SecurityLayer.Log("rrn", "rrn >> " + rrn);
                            }
                            if(datas.has("ReferenceNumber")) {
                                auth_id = datas.optString("ReferenceNumber");
                                SecurityLayer.Log("auth_id", "auth_id >> " + auth_id);
                            }
                        }
                        SecurityLayer.Log("Response Message", responsemessage);
                        if (Utility.isNotNull(respcode) && Utility.isNotNull(respcode)) {
                            SecurityLayer.Log("respcode", "respcode >> " + respcode);
                            if (!(Utility.checkUserLocked(respcode))) {
                                String datetimee = "";
                                if (respcode.equals("00")) {
                                    txn_status = "SUCCESS";
                                    if (!(datas == null)) {
                                        totfee = datas.optString("Fee");
                                        if(datas.has("datetime")) {
                                            datetimee = datas.optString("datetime"); //From dateTime
                                        }
                                        if(datas.has("ReferenceNumber")) {
                                            refcodee = datas.optString("ReferenceNumber");

                                        }
                                    }
                                } else {
                                    txn_status = "DECLINED";
                                }

                                Print_Receipt(respcode);
                                if (respcode.equals("00")) {
                                    Intent intent = new Intent(TransactionProcessingActivity.this, activity_name);
                                    intent.putExtra("recanno", recanno);
                                    intent.putExtra("amou", amou);
                                    intent.putExtra("narra", narra);
                                    intent.putExtra("refcode", refcodee);
                                    intent.putExtra("ednamee", ednamee);
                                    intent.putExtra("ednumbb", ednumbb);
                                    intent.putExtra("txtname", txtname);
                                    intent.putExtra("datetime", datetimee);
                                    intent.putExtra("trantype", "D");
                                    intent.putExtra("agcmsn", agcmsn);
                                    intent.putExtra("fee", totfee);
                                    intent.putExtra("rrn", rrn);
                                    intent.putExtra("auth_id", auth_id);
                                    if (serv.equals("SENDOTB")) {
                                        intent.putExtra("bankname", bankname);
                                        intent.putExtra("bankcode", bankcode);
                                    }
                                    if (serv.equals("AIRT")) {
                                        intent.putExtra("telcoop", telcoop);
                                        intent.putExtra("mobno", txtcustid);
                                        intent.putExtra("serviceid", serviceid);
                                        intent.putExtra("billid", billid);
                                    }
                                    startActivity(intent);
                                } else if (respcode.equals("002")) {
                                    Toast.makeText(
                                            getApplicationContext(), responsemessage,
                                            Toast.LENGTH_LONG).show();
                                    setAlertDialog();
                                } else {
                                    setDialog(responsemessage);
                                    txstatus.setText("TRANSACTION FAILURE");
                                    txdesc.setText(responsemessage);
                                    Answers.getInstance().logCustom(new CustomEvent("error code")
                                            .putCustomAttribute("error_msg", responsemessage)
                                            .putCustomAttribute("response_code", respcode)
                                    );
                                }
                            } else {
                                LogOut();
                            }
                        } else {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "There was an error on your request",
                                    Toast.LENGTH_LONG).show();
                            txstatus.setText("TRANSACTION FAILURE");
                            txdesc.setText("There was an error on your request");
                        }
                    } else {
                        Toast.makeText(
                                getApplicationContext(),
                                "There was an error on your request",
                                Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    Toast.makeText(getApplicationContext(), getApplicationContext().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                    SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getApplicationContext());
                } catch (Exception e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                }
                if ((!(getApplicationContext() == null)) && !(prgDialog2 == null) && prgDialog2.isShowing()) {
                    prgDialog2.dismiss();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    setDialog("Your request has been received.Please wait shortly for feedback");
                }
                SecurityLayer.Log("throwable error", t.toString());
                Toast.makeText(
                        getApplicationContext(),
                        "There was an error on your request",
                        Toast.LENGTH_LONG).show();
                if ((!(getApplicationContext() == null)) && !(prgDialog2 == null) && prgDialog2.isShowing()) {
                    prgDialog2.dismiss();
                }
            }
        });
    }

    private void Print_Receipt(String respcode) throws RemoteException {
        if (serv.equals("CASHDEPO")) {
            Print_Cash_Deposit_Receipt();
        }
        if (serv.equals("CASHTRAN")) {
            Print_Cash_Transfer_Receipt();
        }
        if (serv.equals("SENDOTB")) {
            Print_OTB_Cash_Transfer_Receipt();
        }
        if (serv.equals("AIRT")) {
            Print_Airtime_Receipt();
        }
        if (serv.equals("WDRAW")) {
            Print_Cash_Withdrawal_Receipt();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setDialog(String message) {
        new MaterialDialog.Builder(TransactionProcessingActivity.this)
                .title("Error")
                .content(message)
                .negativeText("Dismiss")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        dialog.dismiss();
                        finish();
                        Intent intent = new Intent(getApplicationContext(), FMobActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        dialog.dismiss();
                        finish();
                        Intent intent = new Intent(getApplicationContext(), FMobActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                })
                .show();
    }

    public void setBillDialog(String message) {
        new MaterialDialog.Builder(TransactionProcessingActivity.this)
                .title("Notice")
                .content(message)
                .negativeText("Dismiss")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        dialog.dismiss();
                        finish();
                        Intent intent = new Intent(getApplicationContext(), FMobActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        dialog.dismiss();
                        finish();
                        Intent intent = new Intent(getApplicationContext(), FMobActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                })
                .show();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button2) {
            finish();
            Intent intent = new Intent(getApplicationContext(), FMobActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    public void setAlertDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.signindialogfrag, null);
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText mEditTexta = (EditText) alertLayout.findViewById(R.id.txt_your_name);
        pro = new ProgressDialog(getApplicationContext());
        pro.setMessage("Loading...");
        pro.setTitle("");
        pro.setCancelable(false);
        mPinLockView = (PinLockView) alertLayout.findViewById(R.id.pin_lock_view);
        mPinLockView.setPinLockListener(mPinLockListener);
        mIndicatorDots = (IndicatorDots) alertLayout.findViewById(R.id.indicator_dots);
        mPinLockView.attachIndicatorDots(mIndicatorDots);
        mPinLockView.setPinLength(5);
        mPinLockView.setTextColor(getResources().getColor(R.color.fab_material_blue_grey_900));
        alert.setTitle("Incorrect PIN set.Please enter valid PIN");
        alert.setView(alertLayout);
        alert.setCancelable(false);
        alert.setNegativeButton("CLOSE", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                Intent intent = new Intent(getApplicationContext(), FMobActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        alert.setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Utility.isNotNull(finpin)) {
                    String encrypted = Utility.getencpin(finpin);
                    SecurityLayer.Log("Enc Pin", encrypted);
                    String finalparams = newparams + "/" + encrypted;
                    dialog.dismiss();
                    if (serv.equals("CASHDEPO")) {
                        Generic_Service_Calls("transfer/intrabank.action", finalparams, FinalConfDepoActivity.class);
                        //IntraDepoBankResp(finalparams);
                    }
                    if (serv.equals("CASHTRAN")) {
                        Generic_Service_Calls("transfer/intrabank.action", finalparams, FinalConfDepoActivity.class);
                        //IntraTranBankResp(finalparams);
                    }
                    if (serv.equals("WDRAW")) {
                        Generic_Service_Calls("transfer/intrabank.action", finalparams, FinalConfDepoActivity.class);
                        //WithdrawResp(finalparams);
                    }
                    if (serv.equals("AIRT")) {
                        Generic_Service_Calls("billpayment/mobileRecharge.action", finalparams, FinalConfAirtimeActivity.class);
                        //AirtimeResp(finalparams);
                    }
                    if (serv.equals("SENDOTB")) {
                        Generic_Service_Calls("transfer/interbank.action", finalparams, FinalConfSendOTBActivity.class);
                        //InterBankResp(finalparams);
                    }
                    if (serv.equals("CABLETV")) {
                        PayBillResp(finalparams);
                    }
                } else {
                    dialog.dismiss();
                    Toast.makeText(
                            getApplicationContext(),
                            "Please enter a valid value for Attendant PIN",
                            Toast.LENGTH_LONG).show();
                    setAlertDialog();

                }
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    public void SetForceOutDialog(String msg, final String title, final Context c) {
        if (!(c == null)) {
            new MaterialDialog.Builder(TransactionProcessingActivity.this)
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

    private void PayBillResp(String params) {
        prgDialog2.show();
        String endpoint = "billpayment/dobillpayment.action";
        String usid = Utility.gettUtilUserId(getApplicationContext());
        String agentid = Utility.gettUtilAgentId(getApplicationContext());
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
                    SecurityLayer.Log("Cable TV Resp", response.body());
                    SecurityLayer.Log("response..:", response.body());
                    JSONObject obj = new JSONObject(response.body());
                    obj = SecurityLayer.decryptTransaction(obj, getApplicationContext());
                    SecurityLayer.Log("decrypted_response", obj.toString());
                    JSONObject datas = obj.optJSONObject("data");
                    if (!(response.body() == null)) {
                        String respcode = obj.optString("responseCode");
                        rpc_code = obj.optString("responseCode");
                        String responsemessage = obj.optString("message");
                        rpc_message = obj.optString("message");
                        String agcmsn = obj.optString("fee");
                        SecurityLayer.Log("Response Message", responsemessage);
                        String refcodee = "";
                        if (!(datas == null)) {
                            rrn = datas.optString("ReferenceId");
                            auth_id = datas.optString("ReferenceNumber");
                        }
                        if (Utility.isNotNull(respcode) && Utility.isNotNull(respcode)) {
                            if (!(Utility.checkUserLocked(respcode))) {
                                SecurityLayer.Log("Response Message", responsemessage);
                                String totfee = "0.00";
                                String datetimee = "";
                                String tref = "";
                                if (respcode.equals("00")) {
                                    txn_status = "SUCCESS";
                                    if (!(datas == null)) {
                                        totfee = datas.optString("Fee");
                                        datetimee = datas.optString("datetime"); //From dateTime
                                        refcodee = datas.optString("ReferenceNumber");
                                    }
                                } else {
                                    txn_status = "DECLINED";
                                }
                                tref = datas.optString("refNumber");
                                Print_Paybill_Receipt();
                                if (respcode.equals("00")) {
                                    Intent intent = new Intent(TransactionProcessingActivity.this, FinalConfirmCableTVActivity.class);
                                    intent.putExtra("custid", txtcustid);
                                    intent.putExtra("amou", amou);
                                    intent.putExtra("narra", narra);
                                    intent.putExtra("billname", strbillnm);
                                    intent.putExtra("ednamee", ednamee);
                                    intent.putExtra("ednumbb", ednumbb);
                                    intent.putExtra("billid", billid);
                                    intent.putExtra("serviceid", serviceid);
                                    intent.putExtra("label", strlabel);
                                    intent.putExtra("fullname", fullname);
                                    intent.putExtra("datetime", datetimee);
                                    //  String refcodee = datas.optString("refNumber");
                                    intent.putExtra("refcode", refcodee);
                                    intent.putExtra("tref", tref);
                                    intent.putExtra("agcmsn", agcmsn);
                                    intent.putExtra("fee", strfee);
                                    startActivity(intent);
                                } else if (respcode.equals("002")) {
                                    Toast.makeText(
                                            getApplicationContext(), responsemessage,
                                            Toast.LENGTH_LONG).show();

                                    setAlertDialog();
                                } else {
                                    if (Utility.checkStateCollect(serviceid)) {
                                        setBillDialog(responsemessage);
                                    } else {
                                        setDialog(responsemessage);
                                    }
                                    txstatus.setText("TRANSACTION FAILURE");
                                    txdesc.setText(responsemessage);
                                    Answers.getInstance().logCustom(new CustomEvent("paybill error code")
                                            .putCustomAttribute("error_msg", responsemessage)
                                            .putCustomAttribute("response_code", respcode)
                                    );
                                }
                            } else {
                                LogOut();
                            }
                        } else {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "There was an error on your request",
                                    Toast.LENGTH_LONG).show();
                            txstatus.setText("TRANSACTION FAILURE");
                            txdesc.setText("There was an error on your request");
                        }
                    } else {
                        //Utility.errornexttoken();
                        Toast.makeText(
                                getApplicationContext(),
                                "There was an error on your request",
                                Toast.LENGTH_LONG).show();
                        txstatus.setText("TRANSACTION FAILURE");
                        txdesc.setText("There was an error on your request");
                    }
                } catch (JSONException e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    //Utility.errornexttoken();
                    Toast.makeText(getApplicationContext(), getApplicationContext().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                    txstatus.setText("TRANSACTION FAILURE");
                    txdesc.setText("There was an error on your request.Please retry");
                    SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getApplicationContext());
                } catch (Exception e) {
                    //Utility.errornexttoken();
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getApplicationContext());
                }
                if ((prgDialog2 != null) && prgDialog2.isShowing() && !(getApplicationContext() == null)) {
                    prgDialog2.dismiss();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                SecurityLayer.Log("throwable error", t.toString());
                if (t instanceof SocketTimeoutException) {
                    //Utility.errornexttoken();
                    SecurityLayer.Log("socket timeout error", t.toString());
                }
                //Utility.errornexttoken();
                Toast.makeText(
                        getApplicationContext(),
                        "There was an error on your request",
                        Toast.LENGTH_LONG).show();

                if ((prgDialog2 != null) && prgDialog2.isShowing() && !(getApplicationContext() == null)) {
                    prgDialog2.dismiss();
                }
                SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getApplicationContext());
            }
        });
    }

    public void Print_Cash_Deposit_Receipt() throws RemoteException {
        com.morefun.yapi.device.printer.Printer printer = mSDKManager.getPrinter();
        try {
            recanno = maskString(recanno, 0, 6, '*');
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (printer.getStatus() != 0){
                SecurityLayer.Log("getStatus", "getStatus = " + (R.string.msg_nopaper));
                return;
            }
            if (printer.initPrinter() != 0) {
                return ;
            }

            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(getApplicationContext().getAssets().open("receipt_logo.bmp"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            printer.appendImage(bitmap);
            int fontSize = FontFamily.MIDDLE;
            Bundle bundle = new Bundle();
            if (!TextUtils.isEmpty(mTypeFacePath)){
                bundle.putString(PrinterConfig.COMMON_TYPEFACE , mTypeFacePath);
            }
            bundle.putInt(PrinterConfig.COMMON_GRAYLEVEL, 15);
            printer.setConfig(bundle);

            printer.appendPrnStr("AGENT: " + mer_name, fontSize, false);
            printer.appendPrnStr("LOC: " + loc, fontSize, false);
            printer.appendPrnStr("ACC NO: " + recanno, fontSize, false);
            printer.appendPrnStr("NAME: " + txtname, fontSize, false);
            printer.appendPrnStr("TID: " + tid + "    MID: " + mid, fontSize, false);
            printer.appendPrnStr("DATE: " + send_date + "        TIME: " + send_time, fontSize, false);
            printer.appendPrnStr("-----------------------------------------------------", fontSize, false);
            if (rpc_code.equals("00")) {
                printer.appendPrnStr("CASH DEPOSIT", fontSize, false);
            } else {
                printer.appendPrnStr("CASH DEPOSIT(FAILED)", fontSize, false);
            }
            fontSize = FontFamily.BIG;
            printer.appendPrnStr("TOTAL: KES " + amou, fontSize, false);
            fontSize = FontFamily.MIDDLE;
            printer.appendPrnStr("Narration: " + narra, fontSize, false);
            printer.appendPrnStr("Sender Phone: " + ednumbb, fontSize, false);
            printer.appendPrnStr("Sender Name: " + ednamee, fontSize, false);
            if (rpc_code.equals("00")) {
                fontSize = FontFamily.BIG;
                printer.appendPrnStr("     APPROVED", fontSize, false);
            }
            fontSize = FontFamily.MIDDLE;

            printer.appendPrnStr("-----------------------------------------------------", fontSize, false);
            printer.appendPrnStr("Response Code: " + rpc_code, fontSize, false);
            if (rrn != null) {
                printer.appendPrnStr("Ref ID: " + rrn, fontSize, false);
            }
            if (!rpc_code.equals("00")) {
                printer.appendPrnStr("Reason: " + rpc_message, fontSize, false);
            }
            if (rpc_code.equals("00")) {
                printer.appendPrnStr("Auth: " + auth_id, fontSize, false);
            }
            printer.appendPrnStr("App Version: " + appvers , fontSize, false);
            printer.appendPrnStr("-----------------------------------------------------", fontSize, false);
            printer.appendPrnStr("SERVED BY: " + Utility.gettUtilUserId(getApplicationContext()), fontSize, false);
            printer.appendPrnStr("      **** MERCHANT COPY ****", fontSize, false);
            printer.appendPrnStr("\n\n", fontSize, false);
            printer.startPrint(new OnPrintListener.Stub() {
                @Override
                public void onPrintResult(int ret) throws RemoteException {
                    Log.d("onPrintResult", "onPrintResult = " + ret);
                    //listener.showMessage(ret == ServiceResult.Success ? context.getString(R.string.msg_succ) : context.getString(R.string.msg_fail));
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        ApplicationClass.get().getGlobalVals().setReprintData("CASH DEPOSIT" + "^" +
                recanno + "^" +
                amou + "^" +
                txtname + "^" +
                rrn + "^" +
                rpc_code + "^" +
                narra + "^"+
                auth_id+ "^" +
                rpc_message+"^"+
                loc+"^"+
                ednumbb+"^"+
                ednamee);

    }

    public void Print_Cash_Withdrawal_Receipt() throws RemoteException {
        com.morefun.yapi.device.printer.Printer printer = mSDKManager.getPrinter();
        try {
            recanno = maskString(recanno, 0, 6, '*');
        } catch (Exception e) {
            e.printStackTrace();
        }
        //mTypeFacePath = initTypeFacePath();
        try {
            if (printer.getStatus() != 0){
                SecurityLayer.Log("getStatus", "getStatus = " + (R.string.msg_nopaper));
                return;
            }
            if (printer.initPrinter() != 0) {
                return ;
            }

            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(getApplicationContext().getAssets().open("receipt_logo.bmp"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            printer.appendImage(bitmap);
            int fontSize = FontFamily.MIDDLE;
            Bundle bundle = new Bundle();
            if (!TextUtils.isEmpty(mTypeFacePath)){
                bundle.putString(PrinterConfig.COMMON_TYPEFACE , mTypeFacePath);
            }
            bundle.putInt(PrinterConfig.COMMON_GRAYLEVEL, 15);
            printer.setConfig(bundle);
            printer.appendPrnStr("AGENT: " + mer_name, fontSize, false);
            printer.appendPrnStr("LOC: " + loc, fontSize, false);
            printer.appendPrnStr("ACC NO: " + recanno, fontSize, false);
            printer.appendPrnStr("NAME: " + txtname, fontSize, false);
            printer.appendPrnStr("TID: " + tid + "    MID: " + mid, fontSize, false);
            printer.appendPrnStr("DATE: " + send_date + "        TIME: " + send_time, fontSize, false);
            printer.appendPrnStr("-----------------------------------------------------", fontSize, false);
            if (rpc_code.equals("00")) {
                printer.appendPrnStr("CASH WITHDRAWAL", fontSize, false);
            } else {
                printer.appendPrnStr("CASH WITHDRAWAL(FAILED)", fontSize, false);
            }
            fontSize = FontFamily.BIG;
            printer.appendPrnStr("TOTAL: KES " + amou, fontSize, false);
            fontSize = FontFamily.MIDDLE;
            printer.appendPrnStr("Narration: " + narra, fontSize, false);
            if (rpc_code.equals("00")) {
                fontSize = FontFamily.BIG;
                printer.appendPrnStr("     APPROVED", fontSize, false);
            }
            fontSize = FontFamily.MIDDLE;
            printer.appendPrnStr("-----------------------------------------------------", fontSize, false);
            printer.appendPrnStr("Response Code: " + rpc_code, fontSize, false);
            if (rrn != null) {
                printer.appendPrnStr("Ref ID: " + rrn, fontSize, false);
            }
            if (!rpc_code.equals("00")) {
                printer.appendPrnStr("*Reason: " + rpc_message, fontSize, false);
            }
            if (rpc_code.equals("00")) {
                printer.appendPrnStr("Auth: " + auth_id, fontSize, false);
            }
            printer.appendPrnStr("App Version: " + appvers , fontSize, false);
            printer.appendPrnStr("-----------------------------------------------------", fontSize, false);
            printer.appendPrnStr("SERVED BY: " + Utility.gettUtilUserId(getApplicationContext()), fontSize, false);
            printer.appendPrnStr("**** CUSTOMER COPY ****", fontSize, false);
            printer.appendPrnStr("\n\n", fontSize, false);
            printer.startPrint(new OnPrintListener.Stub() {
                @Override
                public void onPrintResult(int ret) throws RemoteException {
                    Log.d("onPrintResult", "onPrintResult = " + ret);
                    //listener.showMessage(ret == ServiceResult.Success ? context.getString(R.string.msg_succ) : context.getString(R.string.msg_fail));
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void Print_Cash_Transfer_Receipt() throws RemoteException {
        com.morefun.yapi.device.printer.Printer printer = mSDKManager.getPrinter();
        try {
            recanno = maskString(recanno, 0, 6, '*');
        } catch (Exception e) {
            e.printStackTrace();
        }
        //mTypeFacePath = initTypeFacePath();
        try {
            if (printer.getStatus() != 0){
                SecurityLayer.Log("getStatus", "getStatus = " + (R.string.msg_nopaper));
                return;
            }
            if (printer.initPrinter() != 0) {
                return ;
            }

            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(getApplicationContext().getAssets().open("receipt_logo.bmp"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            printer.appendImage(bitmap);
            int fontSize = FontFamily.MIDDLE;
            Bundle bundle = new Bundle();
            if (!TextUtils.isEmpty(mTypeFacePath)){
                bundle.putString(PrinterConfig.COMMON_TYPEFACE , mTypeFacePath);
            }
            bundle.putInt(PrinterConfig.COMMON_GRAYLEVEL, 15);
            printer.setConfig(bundle);

            printer.appendPrnStr("AGENT: " + mer_name, fontSize, false);
            printer.appendPrnStr("LOC: " + loc, fontSize, false);
            printer.appendPrnStr("ACC NO: " + recanno, fontSize, false);
            printer.appendPrnStr("NAME: " + txtname, fontSize, false);
            printer.appendPrnStr("TID: " + tid + "    MID: " + mid, fontSize, false);
            printer.appendPrnStr("DATE: " + send_date + "        TIME: " + send_time, fontSize, false);
            printer.appendPrnStr("-----------------------------------------------------", fontSize, false);
            if (rpc_code.equals("00")) {
                printer.appendPrnStr("FUNDS TRANSFER", fontSize, false);
            } else {
                printer.appendPrnStr("FUNDS TRANSFER(FAILED)", fontSize, false);
            }
            fontSize = FontFamily.BIG;
            printer.appendPrnStr("TOTAL: KES " + amou, fontSize, false);
            fontSize = FontFamily.MIDDLE;
            printer.appendPrnStr("Narration: " + narra, fontSize, false);
            printer.appendPrnStr("Sender Phone: " + ednumbb, fontSize, false);
            printer.appendPrnStr("Sender Name: " + ednamee, fontSize, false);
            if (rpc_code.equals("00")) {
                fontSize = FontFamily.BIG;
                printer.appendPrnStr("     APPROVED", fontSize, false);
            }
            fontSize = FontFamily.MIDDLE;

            printer.appendPrnStr("-----------------------------------------------------", fontSize, false);
            printer.appendPrnStr("Response Code: " + rpc_code, fontSize, false);
            if (rrn != null) {
                printer.appendPrnStr("Ref ID: " + rrn, fontSize, false);
            }
            if (!rpc_code.equals("00")) {
                printer.appendPrnStr("*Reason: " + rpc_message, fontSize, false);
            }
            if (rpc_code.equals("00")) {
                printer.appendPrnStr("Auth: " + auth_id, fontSize, false);
            }
            printer.appendPrnStr("App Version: " + appvers , fontSize, false);
            printer.appendPrnStr("-----------------------------------------------------", fontSize, false);
            printer.appendPrnStr("SERVED BY: " + Utility.gettUtilUserId(getApplicationContext()), fontSize, false);
            printer.appendPrnStr("**** CUSTOMER COPY ****", fontSize, false);
            printer.appendPrnStr("\n\n", fontSize, false);
            printer.startPrint(new OnPrintListener.Stub() {
                @Override
                public void onPrintResult(int ret) throws RemoteException {
                    Log.d("onPrintResult", "onPrintResult = " + ret);
                    //listener.showMessage(ret == ServiceResult.Success ? context.getString(R.string.msg_succ) : context.getString(R.string.msg_fail));
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void Print_Airtime_Receipt() throws RemoteException {
        com.morefun.yapi.device.printer.Printer printer = mSDKManager.getPrinter();
        try {
            recanno = maskString(recanno, 0, 6, '*');
        } catch (Exception e) {
            e.printStackTrace();
        }
       // mTypeFacePath = initTypeFacePath();
        try {
            if (printer.getStatus() != 0){
                SecurityLayer.Log("getStatus", "getStatus = " + (R.string.msg_nopaper));
                return;
            }
            if (printer.initPrinter() != 0) {
                return ;
            }

            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(getApplicationContext().getAssets().open("receipt_logo.bmp"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            printer.appendImage(bitmap);
            int fontSize = FontFamily.MIDDLE;
            Bundle bundle = new Bundle();
            if (!TextUtils.isEmpty(mTypeFacePath)){
                bundle.putString(PrinterConfig.COMMON_TYPEFACE , mTypeFacePath);
            }
            bundle.putInt(PrinterConfig.COMMON_GRAYLEVEL, 15);
            printer.setConfig(bundle);
            printer.appendPrnStr("AGENT: " + mer_name, fontSize, false);
            printer.appendPrnStr("LOC: " + loc, fontSize, false);
            printer.appendPrnStr("MOBILE NO: " + txtcustid, fontSize, false);
            printer.appendPrnStr("TELCO: " + telcoop, fontSize, false);
            printer.appendPrnStr("TID: " + tid + "    MID: " + mid, fontSize, false);
            printer.appendPrnStr("DATE: " + send_date + "        TIME: " + send_time, fontSize, false);
            printer.appendPrnStr("-----------------------------------------------------", fontSize, false);
            if (rpc_code.equals("00")) {
                printer.appendPrnStr("AIRTIME TOP-UP", fontSize, false);
            } else {
                printer.appendPrnStr("AIRTIME TOP-UP(FAILED)", fontSize, false);
            }
            fontSize = FontFamily.BIG;
            printer.appendPrnStr("TOTAL: KES " + amou, fontSize, false);
            fontSize = FontFamily.MIDDLE;
            printer.appendPrnStr("Narration: " + narra, fontSize, false);
            if (rpc_code.equals("00")) {
                fontSize = FontFamily.BIG;
                printer.appendPrnStr("     APPROVED", fontSize, false);
            }
            fontSize = FontFamily.MIDDLE;

            printer.appendPrnStr("-----------------------------------------------------", fontSize, false);
            printer.appendPrnStr("Response Code: " + rpc_code, fontSize, false);
            if (rrn != null) {
                printer.appendPrnStr("Ref ID: " + rrn, fontSize, false);
            }
            if (!rpc_code.equals("00")) {
                printer.appendPrnStr("*Reason: " + rpc_message, fontSize, false);
            }
            if (rpc_code.equals("00")) {
                printer.appendPrnStr("Auth: " + auth_id, fontSize, false);
            }
            printer.appendPrnStr("App Version: " + appvers , fontSize, false);
            printer.appendPrnStr("-----------------------------------------------------", fontSize, false);
            printer.appendPrnStr("SERVED BY: " + Utility.gettUtilUserId(getApplicationContext()), fontSize, false);
            printer.appendPrnStr("**** CUSTOMER COPY ****", fontSize, false);
            printer.appendPrnStr("\n\n", fontSize, false);
            printer.startPrint(new OnPrintListener.Stub() {
                @Override
                public void onPrintResult(int ret) throws RemoteException {
                    Log.d("onPrintResult", "Print_Airtime_Receipt = " + ret);
                    //listener.showMessage(ret == ServiceResult.Success ? context.getString(R.string.msg_succ) : context.getString(R.string.msg_fail));
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void Print_OTB_Cash_Transfer_Receipt() throws RemoteException {
        com.morefun.yapi.device.printer.Printer printer = mSDKManager.getPrinter();
        try {
            recanno = maskString(recanno, 0, 6, '*');
        } catch (Exception e) {
            e.printStackTrace();
        }
        //mTypeFacePath = initTypeFacePath();
        try {
            if (printer.getStatus() != 0){
                SecurityLayer.Log("getStatus", "getStatus = " + (R.string.msg_nopaper));
                return;
            }
            if (printer.initPrinter() != 0) {
                return ;
            }

            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(getApplicationContext().getAssets().open("receipt_logo.bmp"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            printer.appendImage(bitmap);
            int fontSize = FontFamily.MIDDLE;
            Bundle bundle = new Bundle();
            if (!TextUtils.isEmpty(mTypeFacePath)){
                bundle.putString(PrinterConfig.COMMON_TYPEFACE , mTypeFacePath);
            }
            bundle.putInt(PrinterConfig.COMMON_GRAYLEVEL, 15);
            printer.setConfig(bundle);
            printer.appendPrnStr("AGENT: " + mer_name, fontSize, false);
            printer.appendPrnStr("LOC: " + loc, fontSize, false);
            printer.appendPrnStr("BANK: " + bankname, fontSize, false);
            printer.appendPrnStr("ACC NO: " + recanno, fontSize, false);
            printer.appendPrnStr("NAME: " + txtname, fontSize, false);
            printer.appendPrnStr("TID: " + tid + "    MID: " + mid, fontSize, false);
            printer.appendPrnStr("DATE: " + send_date + "        TIME: " + send_time, fontSize, false);
            printer.appendPrnStr("-----------------------------------------------------", fontSize, false);
            printer.appendPrnStr("SENDER NAME: " + ednamee, fontSize, false);
            printer.appendPrnStr("SENDER PHONE: " + ednumbb, fontSize, false);
            printer.appendPrnStr("NARRATION: " + narra, fontSize, false);
            printer.appendPrnStr("-----------------------------------------------------", fontSize, false);
            if (rpc_code.equals("00")) {
                printer.appendPrnStr("FUNDS TRANSFER", fontSize, false);
            } else {
                printer.appendPrnStr("FUNDS TRANSFER(FAILED)", fontSize, false);
            }
            fontSize = FontFamily.BIG;
            printer.appendPrnStr("TOTAL: KES " + amou, fontSize, false);
            fontSize = FontFamily.MIDDLE;
            if (rpc_code.equals("00")) {
                fontSize = FontFamily.BIG;
                printer.appendPrnStr("     APPROVED", fontSize, false);
            }
            fontSize = FontFamily.MIDDLE;

            printer.appendPrnStr("-----------------------------------------------------", fontSize, false);
            printer.appendPrnStr("Response Code: " + rpc_code, fontSize, false);
            if (rrn != null) {
                printer.appendPrnStr("Ref ID: " + rrn, fontSize, false);
            }
            if (!rpc_code.equals("00")) {
                printer.appendPrnStr("Reason: " + rpc_message, fontSize, false);
            }
            if (rpc_code.equals("00")) {
                printer.appendPrnStr("Auth: " + auth_id, fontSize, false);
            }
            printer.appendPrnStr("App Version: " + appvers , fontSize, false);
            printer.appendPrnStr("-----------------------------------------------------", fontSize, false);
            printer.appendPrnStr("SERVED BY: " + Utility.gettUtilUserId(getApplicationContext()), fontSize, false);
            printer.appendPrnStr("**** CUSTOMER COPY ****", fontSize, false);
            printer.appendPrnStr("\n\n", fontSize, false);
            printer.startPrint(new OnPrintListener.Stub() {
                @Override
                public void onPrintResult(int ret) throws RemoteException {
                    Log.d("onPrintResult", "onPrintResult = " + ret);
                    //listener.showMessage(ret == ServiceResult.Success ? context.getString(R.string.msg_succ) : context.getString(R.string.msg_fail));
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void Print_Paybill_Receipt() throws RemoteException {
        com.morefun.yapi.device.printer.Printer printer = mSDKManager.getPrinter();
        try {
            recanno = maskString(recanno, 0, 6, '*');
        } catch (Exception e) {
            e.printStackTrace();
        }
        //mTypeFacePath = initTypeFacePath();
        try {
            if (printer.getStatus() != 0){
                SecurityLayer.Log("getStatus", "getStatus = " + (R.string.msg_nopaper));
                return;
            }
            if (printer.initPrinter() != 0) {
                return ;
            }

            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(getApplicationContext().getAssets().open("receipt_logo.bmp"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            printer.appendImage(bitmap);
            int fontSize = FontFamily.MIDDLE;
            Bundle bundle = new Bundle();
            if (!TextUtils.isEmpty(mTypeFacePath)){
                bundle.putString(PrinterConfig.COMMON_TYPEFACE , mTypeFacePath);
            }
            bundle.putInt(PrinterConfig.COMMON_GRAYLEVEL, 15);
            printer.setConfig(bundle);

            printer.appendPrnStr("AGENT: " + mer_name, fontSize, false);
            printer.appendPrnStr("LOC: " + loc, fontSize, false);
            printer.appendPrnStr(strbillnm + " : " + billid, fontSize, false);
            printer.appendPrnStr(strlabel + " : " + txtcustid, fontSize, false);
            printer.appendPrnStr("TID: " + tid + "    MID: " + mid, fontSize, false);
            printer.appendPrnStr("DATE: " + send_date + "        TIME: " + send_time, fontSize, false);
            printer.appendPrnStr("-----------------------------------------------------", fontSize, false);
            printer.appendPrnStr("PAYER NAME: " + ednamee, fontSize, false);
            printer.appendPrnStr("PAYER NUMBER: " + ednumbb, fontSize, false);
            printer.appendPrnStr("NARRATION: " + narra, fontSize, false);
            printer.appendPrnStr("-----------------------------------------------------", fontSize, false);
            if (rpc_code.equals("00")) {
                printer.appendPrnStr("PAYBILL", fontSize, false);
            } else {
                printer.appendPrnStr("PAYBILL(FAILED)", fontSize, false);
            }
            fontSize = FontFamily.BIG;
            printer.appendPrnStr("TOTAL: KES " + amou, fontSize, false);
            fontSize = FontFamily.MIDDLE;
            printer.appendPrnStr("Narration: " + narra, fontSize, false);
            if (rpc_code.equals("00")) {
                fontSize = FontFamily.BIG;
                printer.appendPrnStr("     APPROVED", fontSize, false);
            }
            fontSize = FontFamily.MIDDLE;

            printer.appendPrnStr("-----------------------------------------------------", fontSize, false);
            printer.appendPrnStr("Response Code: " + rpc_code, fontSize, false);
            if (rrn != null) {
                printer.appendPrnStr("Ref ID: " + rrn, fontSize, false);
            }
            if (!rpc_code.equals("00")) {
                printer.appendPrnStr("*Reason: " + rpc_message, fontSize, false);
            }
            if (rpc_code.equals("00")) {
                printer.appendPrnStr("Auth: " + auth_id, fontSize, false);
            }
            printer.appendPrnStr("App Version: " + appvers , fontSize, false);
            printer.appendPrnStr("-----------------------------------------------------", fontSize, false);
            printer.appendPrnStr("SERVED BY: " + Utility.gettUtilUserId(getApplicationContext()), fontSize, false);
            printer.appendPrnStr("**** CUSTOMER COPY ****", fontSize, false);
            printer.appendPrnStr("\n\n", fontSize, false);
            printer.startPrint(new OnPrintListener.Stub() {
                @Override
                public void onPrintResult(int ret) throws RemoteException {
                    Log.d("onPrintResult", "onPrintResult = " + ret);
                    //listener.showMessage(ret == ServiceResult.Success ? context.getString(R.string.msg_succ) : context.getString(R.string.msg_fail));
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public String initTypeFacePath() {
        String namePath = "arial";
        String mTypefacePath = null;
        if (Build.VERSION.SDK_INT >= 23) {
            mTypefacePath = FileUtils.getExternalCacheDir(TransactionProcessingActivity.this, namePath + ".ttf");
        }else {
            String filePath  = FileUtils.createTmpDir(TransactionProcessingActivity.this);
            SecurityLayer.Log("initTypeFacePath" ,"filePath = " + filePath);
            mTypefacePath = filePath + namePath +".ttf";
        }
        SecurityLayer.Log("initTypeFacePath" ,"mTypefacePath = " + mTypefacePath);
        try {
            FileUtils.copyFromAssets(TransactionProcessingActivity.this.getAssets(), namePath, mTypefacePath ,true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SecurityLayer.Log("initTypeFacePath" ,"mTypefacePath = " + mTypefacePath);
        return mTypefacePath ;
    }

    private static String maskString(String strText, int start, int end, char maskChar)
            throws Exception {
        if (strText == null || strText.equals(""))
            return "";

        if (start < 0)
            start = 0;

        if (end > strText.length())
            end = strText.length();

        if (start > end)
            throw new Exception("End index cannot be greater than start index");

        int maskLength = end - start;

        if (maskLength == 0)
            return strText;

        StringBuilder sbMaskString = new StringBuilder(maskLength);

        for (int i = 0; i < maskLength; i++) {
            sbMaskString.append(maskChar);
        }

        return strText.substring(0, start)
                + sbMaskString.toString()
                + strText.substring(start + maskLength);
    }

    @Override
    protected void onDestroy() {
        if (prgDialog2 != null && prgDialog2.isShowing()) {
            prgDialog2.dismiss();
        }
        super.onDestroy();
    }

    public void LogOut() {
        session.logoutUser();
        finish();
        Intent i = new Intent(getApplicationContext(), SignInActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // Staring Login Activity
        startActivity(i);
        Toast.makeText(
                getApplicationContext(),
                "You have been locked out of the app.Please call customer care for further details",
                Toast.LENGTH_LONG).show();
    }
}
