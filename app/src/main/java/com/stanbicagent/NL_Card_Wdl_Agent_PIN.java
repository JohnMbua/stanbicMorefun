package com.stanbicagent;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.stanbicagent.BuildConfig;
import com.stanbicagent.R;
import com.afollestad.materialdialogs.MaterialDialog;
import com.morefun.yapi.ServiceResult;
import com.morefun.yapi.device.pinpad.DesAlgorithmType;
import com.morefun.yapi.device.pinpad.WorkKeyType;
import com.morefun.yapi.device.printer.FontFamily;
import com.morefun.yapi.device.printer.OnPrintListener;
import com.morefun.yapi.device.printer.PrinterConfig;
import com.morefun.yapi.engine.DeviceInfoConstrants;
import com.morefun.yapi.engine.DeviceServiceEngine;
import com.newland.mtype.util.Dump;
import com.newland.mtype.util.ISOUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import publib.BytesUtils;
import publib.ISO8583;
import security.SecurityLayer;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import utils.FileUtils;

import static com.stanbicagent.ApplicationConstants.CUPS8583;
import static com.stanbicagent.ApplicationConstants.SWITCH_IP;
import static com.stanbicagent.ApplicationConstants.SWITCH_PORT;
import static com.stanbicagent.ApplicationConstants.currency_code;
import static publib.Utils.byte2string;
import static publib.Utils.string2byte;

public class NL_Card_Wdl_Agent_PIN extends BaseActivity implements View.OnClickListener {
    ImageView imageView1;
    EditText agt_pin;
    Button btnsub;
    TextView rle,amtT,amtD;
    SessionManagement session;
    ProgressDialog prgDialog;
    String depositid;
    RelativeLayout rlid;
    TextView step2, txtcard_name, txtcard_no, txtamount_disp,txnType;
    private static int SPLASH_TIME_OUT = 2500;
    SharedPreferences pref;
    ISO8583 iso8583;
    String requst, fld_120, cardNo, cardSN, expiredDate, serviceCode, track2, data55, data_send, serial_no, txn_amount, card_ttype, acc_ttype = "";
    String tid, mid, mer_name, loc, ccode, user_only, pcode, fld_43, rpc, stan, available_balance,book_balance, rrn,fld_112,prn_fee_amount,str_formtted_fee,fee,balance, send_date, send_time, ctyp, aid,tsi, ccid,cholder_name, etmk, etpk = "";
    String narration, txn_status, card_label, status, appvers, reason, auth = "";
    byte[] reqData = null;
    byte[] respData = null;
    byte[] result;
    int check = 0;
    DeviceServiceEngine mSDKManager;
    String mTypeFacePath,txtname,trantype,ednumbb,ednamee,auth_id,recanno,respCd;
    String tntp,amou,narra,crdN,txn;
    EditText edacc, pno, txtamount, txtnarr;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nl_activity_cardwldagtpin);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayShowHomeEnabled(true); // show or hide the default home button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
        ab.setDisplayShowTitleEnabled(false); // disable the default title element here (for centered title)
        session = new SessionManagement(this);
        SDKManager.getInstance().bindService(getApplicationContext());
        appvers = Utility.getAppVersion(getApplicationContext());
        rlid = (RelativeLayout) findViewById(R.id.rlid);
        step2 = (TextView) findViewById(R.id.tv2);
        step2.setOnClickListener(this);
        txtcard_name = (TextView) findViewById(R.id.txtcard_name);
        txnType = (TextView) findViewById(R.id.label_use);
        txtcard_no = (TextView) findViewById(R.id.txtcard_no);
        txtamount_disp = (TextView) findViewById(R.id.txtamount_disp);
        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Processing transaction" + "\n" + "Please wait.....");
        prgDialog.setCancelable(false);
        agt_pin = (EditText) findViewById(R.id.agt_pin);
        btnsub = (Button) findViewById(R.id.button2);
        btnsub.setOnClickListener(this);
        rle=(TextView) findViewById(R.id.attID) ;
        amtT=(TextView) findViewById(R.id.textViewrd) ;
        amtD=(TextView) findViewById(R.id.txtamount_disp) ;
        mTypeFacePath = initTypeFacePath();

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        //user_only = pref.getString("user_only", "");
        user_only = Utility.gettUtilUserId(getApplicationContext());
        tid = pref.getString("tid", "");
        mid = pref.getString("mid", "");
        mer_name = pref.getString("mer_name", "");
        loc = pref.getString("loc", "");
        ccode = pref.getString("ccode", "");
        fld_43 = pref.getString("fld_43", "");
        etmk = pref.getString("etmk", "");
        etpk = pref.getString("etpk", "");
        cardNo = pref.getString("cardNo", "");
        try {
            String mask_card_no = maskString(cardNo, 4, 12, '*');
            txtcard_no.setText(mask_card_no);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String cardno_full = String.valueOf(cardNo.charAt(cardNo.length() - 1));
        if (cardno_full.equals("F")) {
            cardNo = cardNo.substring(0, cardNo.length() - 1);
        }
        cardSN = pref.getString("cardSN", "");
        expiredDate = pref.getString("expiredDate", "");
        ctyp = pref.getString("ctyp", "");
        aid = pref.getString("aid", "");
        tsi=pref.getString("tsi","");
        track2 = pref.getString("track2", "");
        String clabel, cholder = "";


        clabel = pref.getString("card_label", "");
        card_label = new String(ISOUtils.hex2byte(clabel));
        cholder = pref.getString("cholder_name", "");
        data55 = pref.getString("data55", "");
        //serviceCode = pref.getString("serviceCode", "");
        card_ttype = pref.getString("card_ttype", "");
        acc_ttype = pref.getString("acc_ttype", "");
        cholder_name = new String(ISOUtils.hex2byte(cholder));
        txtcard_name.setText(cholder_name);
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("cholder_name", cholder_name);
        editor.putString("card_label", card_label);
        editor.putString("tvr", ctyp);
        editor.putString("aid", aid);
        editor.putString("tsi",tsi);

        editor.commit();

        auth_id=ApplicationClass.get().getGlobalVals().getSessAuth();
        recanno=ApplicationClass.get().getGlobalVals().getSessAccNo();

        if (cardSN == null) {
            cardSN = "000";
        } else {
            cardSN = ISOUtils.padleft(cardSN, 3, '0');
        }
        if (expiredDate.length() == 6) {
            expiredDate = expiredDate.substring(0, expiredDate.length() - 2);
        }
        if (ctyp != null){
            ctyp=hexToAscii(ctyp);
        }

        String track2_full = String.valueOf(track2.charAt(track2.length() - 1));
        if (track2_full.equals("F")) {
            track2 = track2.substring(0, track2.length() - 1);
        }

        txn_amount = pref.getString("txn_amount", "");
        String txtamou = Utility.returnNumberFormat(txn_amount);
        txtamount_disp.setText(ApplicationConstants.KEY_NAIRA + txtamou);
        txn_amount = txn_amount + "00";

        //data55 = pref.getString("data55", "");
        data_send = pref.getString("data_send", "");
        SecurityLayer.Log("data_send", "3 - data_send > " + data_send);


        tntp=ApplicationClass.get().getGlobalVals().getTranType();
        txnType.setText(tntp);
        if(tntp.equals("CARD DEPOSIT")){
            pcode = "512000";
        }
        if(tntp.equals("CARD WITHDRAWAL")){
            pcode = "510000";
        }
        if(tntp.equals("BALANCE INQUIRY")){
            pcode = "310000";
            agt_pin.setText("68997");
            agt_pin.setVisibility(View.GONE);
            rle.setVisibility(View.GONE);
            amtD.setVisibility(View.GONE);
            amtT.setVisibility(View.GONE);
            btnsub.setVisibility(View.GONE);
            btnsub.performClick();
        }
        if(tntp.equals("MINI STATEMENT")){
            pcode = "400000";
            agt_pin.setText("68997");
            agt_pin.setVisibility(View.GONE);
            rle.setVisibility(View.GONE);
            amtD.setVisibility(View.GONE);
            amtT.setVisibility(View.GONE);
            btnsub.setVisibility(View.GONE);
            btnsub.performClick();
        }else{

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

    private static String hexToAscii(String hexStr) {
        StringBuilder output = new StringBuilder("");

        for (int i = 0; i < hexStr.length(); i += 2) {
            String str = hexStr.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }

        return output.toString();
    }
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button2) {
            if (Utility.checkInternetConnection(this)) {
                mSDKManager = SDKManager.getInstance().getDeviceServiceEngine();
                if (mSDKManager == null) {
                    SecurityLayer.Log("MF_SDKManager", "ServiceEngine is Null");
                    return;
                }
                final String pin = agt_pin.getText().toString();
                if (Utility.isNotNull(pin) && (pin.length() == 5)) {
                    Check_Printer();
//                    After_Printer_Check();
                } else {
                    Toast.makeText(
                            getApplicationContext(),
                            "Please enter a valid value for PIN",
                            Toast.LENGTH_LONG).show();
                }
            }
        }
        if (view.getId() == R.id.tv2) {
            finish();
            Intent intent = new Intent(NL_Card_Wdl_Agent_PIN.this, NL_WDLMenuActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    public void Check_Printer() {
        try {
            com.morefun.yapi.device.printer.Printer printer = mSDKManager.getPrinter();
            if (printer.getStatus() != 0) {
                check = -1;
            } else {
                check = 0;
            }
            After_Printer_Check();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void After_Printer_Check() throws RemoteException {
        if (check == 0) {
            Send_Card_Wdl_Txn_Request card_wdl_txn_client = new Send_Card_Wdl_Txn_Request(SWITCH_IP, SWITCH_PORT);
            card_wdl_txn_client.execute();
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(
                            getApplicationContext(),
                            "Please insert paper receipt in order to print.",
                            Toast.LENGTH_LONG).show();
                }
            });
        }
    }
    public void printDeclineReceipt() throws RemoteException {{
        com.morefun.yapi.device.printer.Printer printer = mSDKManager.getPrinter();


        amou= ApplicationClass.get().getGlobalVals().getSessAmt();
        if (amou != null) {
            amou = Utility.returnNumberFormat(amou.substring(0, amou.length() - 2));
        }
        crdN=ApplicationClass.get().getGlobalVals().getSessPan();
        if (crdN != null) {
            crdN=Utility.MaskPan(ApplicationClass.get().getGlobalVals().getSessPan());
            try {
                recanno = maskString(recanno, 0, 6, '*');
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            if (printer.getStatus() != 0) {
                SecurityLayer.Log("getStatus", "getStatus = " + (R.string.msg_nopaper));
                return;
            }
            if (printer.initPrinter() != 0) {
                return;
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
            if (!TextUtils.isEmpty(mTypeFacePath)) {
                bundle.putString(PrinterConfig.COMMON_TYPEFACE, mTypeFacePath);
            }
            bundle.putInt(PrinterConfig.COMMON_GRAYLEVEL, 15);
            printer.setConfig(bundle);

            printer.appendPrnStr("AGENT: " + mer_name, fontSize, false);
            printer.appendPrnStr("LOC: " + loc, fontSize, false);
            printer.appendPrnStr("TID: " + tid + "  MID: " + mid, fontSize, false);
            printer.appendPrnStr("DATE: " + send_date + "    TIME: " + send_time, fontSize, false);
            printer.appendPrnStr("-----------------------------------------------------", fontSize, false);
            printer.appendPrnStr("CARD #: " + crdN, fontSize, false);
            printer.appendPrnStr(cholder_name, fontSize, false);
            fontSize = FontFamily.BIG;

            printer.appendPrnStr(tntp, fontSize, false);
            if (tntp.equals("BALANCE INQUIRY")) {
                // printer.appendPrnStr("TOTAL: KES " + amou, fontSize, false); //get balance enquiry details
            } else if (tntp.equals("MINI STATEMENT")) {
                // printer.appendPrnStr("TOTAL: KES " + amou, fontSize, false); //get ministatement data
            } else{
                printer.appendPrnStr("TOTAL: KES " + amou, fontSize, false);
            }

            fontSize = FontFamily.BIG;
            printer.appendPrnStr("          "+txn_status, fontSize, false);
            fontSize = FontFamily.SMALL;
            printer.appendPrnStr("Card: "+ctyp, fontSize, false);
            printer.appendPrnStr("Aid: "+aid, fontSize, false);
            printer.appendPrnStr("Tsi: "+tsi, fontSize, false);
            fontSize = FontFamily.MIDDLE;
            printer.appendPrnStr("-----------------------------------------------------", fontSize, false);
            printer.appendPrnStr("Response Code: " + rpc, fontSize, false);
            printer.appendPrnStr("Ref ID: " + rrn, fontSize, false);
            printer.appendPrnStr("Reason: " + narration, fontSize, false);
            printer.appendPrnStr("App Version: " + appvers, fontSize, false);
            printer.appendPrnStr("-----------------------------------------------------", fontSize, false);
            printer.appendPrnStr("SERVED BY: " + Utility.gettUtilUserId(getApplicationContext()), fontSize, false);
            printer.appendPrnStr("   **** MERCHANT COPY ****", fontSize, false);
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
        ApplicationClass.get().getGlobalVals().setReprintData(tntp + "^" + crdN + "^" + amou + "^" +  recanno + "^" + rrn + "^" + rpc + "^" + narra + "^"+cholder_name+ "^" + auth_id + "^" + narration+"^"+""+ctyp+"^"+aid+"^"+tsi+"^"+loc);


    }
    public void Print_Card_Receipt() throws RemoteException {
        com.morefun.yapi.device.printer.Printer printer = mSDKManager.getPrinter();

        trantype="D";
        auth_id = ApplicationClass.get().getGlobalVals().getSessAuth();
        amou= ApplicationClass.get().getGlobalVals().getSessAmt();
        if (amou != null) {
            amou = Utility.returnNumberFormat(amou.substring(0, amou.length() - 2));
        }
        crdN=ApplicationClass.get().getGlobalVals().getSessPan();
        if (crdN != null) {
            crdN=Utility.MaskPan(ApplicationClass.get().getGlobalVals().getSessPan());
            try {
                recanno = maskString(recanno, 0, 6, '*');
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        str_formtted_fee = fee.substring(2);
        SecurityLayer.Log("str_formtted_fee", "str_formtted_fee >> " + str_formtted_fee);
        while (str_formtted_fee.length() > 0 && str_formtted_fee.charAt(0) == '0') {
            str_formtted_fee = str_formtted_fee.substring(1);
        }
        str_formtted_fee = str_formtted_fee.substring(0, str_formtted_fee.length() - 2);
        SecurityLayer.Log("str_formtted_fee_2", "str_formtted_fee_2 >> " + str_formtted_fee);
        prn_fee_amount = Utility.returnNumberFormat(str_formtted_fee);
        SecurityLayer.Log("prn_fee_amount", "prn_fee_amount >> " + prn_fee_amount);


        try {
            if (printer.getStatus() != 0) {
                SecurityLayer.Log("getStatus", "getStatus = " + (R.string.msg_nopaper));
                return;
            }
            if (printer.initPrinter() != 0) {
                return;
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
            if (!TextUtils.isEmpty(mTypeFacePath)) {
                bundle.putString(PrinterConfig.COMMON_TYPEFACE, mTypeFacePath);
            }
            bundle.putInt(PrinterConfig.COMMON_GRAYLEVEL, 15);
            printer.setConfig(bundle);

            printer.appendPrnStr("AGENT: " + mer_name, fontSize, false);
            printer.appendPrnStr("LOC: " + loc, fontSize, false);
            printer.appendPrnStr("TID: " + tid + "  MID: " + mid, fontSize, false);
            printer.appendPrnStr("DATE: " + send_date + "    TIME: " + send_time, fontSize, false);
            printer.appendPrnStr("-----------------------------------------------------", fontSize, false);
            printer.appendPrnStr("CARD #: " + crdN, fontSize, false);
            printer.appendPrnStr(cholder_name, fontSize, false);
            fontSize = FontFamily.BIG;

            printer.appendPrnStr(tntp, fontSize, false);
            if (tntp.equals("BALANCE INQUIRY")) {
               // printer.appendPrnStr("TOTAL: KES " + amou, fontSize, false); //get balance enquiry details
            } else if (tntp.equals("MINI STATEMENT")) {
               // printer.appendPrnStr("TOTAL: KES " + amou, fontSize, false); //get ministatement data
            } else{
                printer.appendPrnStr("TOTAL: KES " + amou, fontSize, false);
            }
            fontSize = FontFamily.MIDDLE;
           // printer.appendPrnStr("Narration: " + narra, fontSize, false);
            printer.appendPrnStr("Fee: KES " + prn_fee_amount, fontSize, false);
            if (trantype.equals("T")) {
                printer.appendPrnStr("Sender Phone: " + ednumbb, fontSize, false);
                printer.appendPrnStr("Sender Name: " + ednamee, fontSize, false);
            }
            fontSize = FontFamily.BIG;
            printer.appendPrnStr("          "+txn_status, fontSize, false);
            fontSize = FontFamily.SMALL;
            printer.appendPrnStr("Card: "+ctyp, fontSize, false);
            printer.appendPrnStr("Aid: "+aid, fontSize, false);
            printer.appendPrnStr("Tsi: "+tsi, fontSize, false);
            fontSize = FontFamily.MIDDLE;
            printer.appendPrnStr("-----------------------------------------------------", fontSize, false);
            printer.appendPrnStr("Response Code: " + rpc, fontSize, false);
            printer.appendPrnStr("Ref ID: " + rrn, fontSize, false);
            //if(tntp.equals("CARD WITHDRAWAL")) {
            if (rpc.equals("00")) {
                printer.appendPrnStr("Auth: " + auth_id, fontSize, false);
            }else{
                printer.appendPrnStr("Reason: " + narration, fontSize, false);
            }
           // }
            printer.appendPrnStr("App Version: " + appvers, fontSize, false);
            printer.appendPrnStr("-----------------------------------------------------", fontSize, false);
            printer.appendPrnStr("SERVED BY: " + Utility.gettUtilUserId(getApplicationContext()), fontSize, false);
            printer.appendPrnStr("   **** MERCHANT COPY ****", fontSize, false);
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
        ApplicationClass.get().getGlobalVals().setReprintData(tntp + "^" + crdN + "^" + amou + "^" +  recanno + "^" + rrn + "^" + rpc + "^" + narra + "^"+cholder_name+ "^" + auth_id + "^" + narration+"^"+""+ctyp+"^"+aid+"^"+tsi+"^"+loc+"^"+prn_fee_amount);

    }
    public void Print_Card_BEQ_Txn() throws RemoteException {
        com.morefun.yapi.device.printer.Printer printer = mSDKManager.getPrinter();

        cardNo=ApplicationClass.get().getGlobalVals().getSessPan();
        if(cardNo != null) {
            try {
                cardNo = Utility.MaskPan(ApplicationClass.get().getGlobalVals().getSessPan());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

            str_formtted_fee = fee.substring(2);
            SecurityLayer.Log("str_formtted_fee", "str_formtted_fee >> " + str_formtted_fee);
            while (str_formtted_fee.length() > 0 && str_formtted_fee.charAt(0) == '0') {
                str_formtted_fee = str_formtted_fee.substring(1);
            }
            str_formtted_fee = str_formtted_fee.substring(0, str_formtted_fee.length() - 2);
            SecurityLayer.Log("str_formtted_fee_2", "str_formtted_fee_2 >> " + str_formtted_fee);
            prn_fee_amount = Utility.returnNumberFormat(str_formtted_fee);
            SecurityLayer.Log("prn_fee_amount", "prn_fee_amount >> " + prn_fee_amount);

                try {
                    if (printer.getStatus() != 0) {
                        SecurityLayer.Log("getStatus", "getStatus = " + (R.string.msg_nopaper));
                        return;
                    }
                    if (printer.initPrinter() != 0) {
                        return;
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
                    if (!TextUtils.isEmpty(mTypeFacePath)) {
                        bundle.putString(PrinterConfig.COMMON_TYPEFACE, mTypeFacePath);
                    }
                    bundle.putInt(PrinterConfig.COMMON_GRAYLEVEL, 15);
                    printer.setConfig(bundle);

                    printer.appendPrnStr("AGENT: " + mer_name, fontSize, false);
                    printer.appendPrnStr("LOC: " + loc, fontSize, false);
                    printer.appendPrnStr("TID: " + tid + "  MID: " + mid, fontSize, false);
                    printer.appendPrnStr("DATE: " + send_date + "    TIME: " + send_time, fontSize, false);
                    printer.appendPrnStr("-----------------------------------------------------", fontSize, false);
                    printer.appendPrnStr("CARD #: " + cardNo, fontSize, false);
                    printer.appendPrnStr(cholder_name, fontSize, false);
                    fontSize = FontFamily.BIG;

                    printer.appendPrnStr(tntp, fontSize, false);
                    fontSize = FontFamily.MIDDLE;
                    printer.appendPrnStr("Available Balance: KES " + available_balance, fontSize, false);
                    printer.appendPrnStr("Book Balance: KES " + book_balance, fontSize, false);

//                    fontSize = FontFamily.MIDDLE;
                        printer.appendPrnStr("Fee: KES " + prn_fee_amount, fontSize, false);
//                    fontSize = FontFamily.BIG;
//                    printer.appendPrnStr("          "+txn_status, fontSize, false);
//                    fontSize = FontFamily.SMALL;
//                    printer.appendPrnStr("Card: "+ctyp, fontSize, false);
//                    printer.appendPrnStr("Aid: "+aid, fontSize, false);
//                    printer.appendPrnStr("Tsi: "+tsi, fontSize, false);
//                    fontSize = FontFamily.MIDDLE;
                    printer.appendPrnStr("-----------------------------------------------------", fontSize, false);
                    printer.appendPrnStr("Response Code: " + rpc, fontSize, false);
                    printer.appendPrnStr("Ref ID: " + rrn, fontSize, false);
                    printer.appendPrnStr("App Version: " + appvers, fontSize, false);
                    printer.appendPrnStr("-----------------------------------------------------", fontSize, false);
                    printer.appendPrnStr("SERVED BY: " + Utility.gettUtilUserId(getApplicationContext()), fontSize, false);
                    printer.appendPrnStr("   **** CUSTOMER COPY ****", fontSize, false);
                    printer.appendPrnStr("\n\n", fontSize, false);
                    printer.startPrint(new OnPrintListener.Stub() {
                        @Override
                        public void onPrintResult(int ret) throws RemoteException {
                            Log.d("onPrintResult", "onPrintResult = " + ret);
                        }
                    });
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            SecurityLayer.Log("devmanager", "device manager is not alive");
    }

    public void Print_Card_MiniStatement_Txn() throws RemoteException {
        com.morefun.yapi.device.printer.Printer printer = mSDKManager.getPrinter();
        final String[] temp;
        temp = fld_112.split("\\|");

        crdN=ApplicationClass.get().getGlobalVals().getSessPan();
        if (crdN != null) {
            crdN=Utility.MaskPan(ApplicationClass.get().getGlobalVals().getSessPan());
            try {
                recanno = maskString(recanno, 0, 6, '*');
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        cardNo=ApplicationClass.get().getGlobalVals().getSessPan();
        if (cardNo != null) {
            try {
//                cardNo = maskString(cardNo, 6, 12, '*');
                cardNo=Utility.MaskPan(ApplicationClass.get().getGlobalVals().getSessPan());
                recanno = maskString(recanno, 0, 6, '*');
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

            str_formtted_fee = fee.substring(2);
            SecurityLayer.Log("str_formtted_fee", "str_formtted_fee >> " + str_formtted_fee);
            while (str_formtted_fee.length() > 0 && str_formtted_fee.charAt(0) == '0') {
                str_formtted_fee = str_formtted_fee.substring(1);
            }
            str_formtted_fee = str_formtted_fee.substring(0, str_formtted_fee.length() - 2);
            SecurityLayer.Log("str_formtted_fee_2", "str_formtted_fee_2 >> " + str_formtted_fee);
            prn_fee_amount = Utility.returnNumberFormat(str_formtted_fee);
            SecurityLayer.Log("prn_fee_amount", "prn_fee_amount >> " + prn_fee_amount);


        try {
            if (printer.getStatus() != 0) {
                SecurityLayer.Log("getStatus", "getStatus = " + (R.string.msg_nopaper));
                return;
            }
            if (printer.initPrinter() != 0) {
                return;
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
            if (!TextUtils.isEmpty(mTypeFacePath)) {
                bundle.putString(PrinterConfig.COMMON_TYPEFACE, mTypeFacePath);
            }
            bundle.putInt(PrinterConfig.COMMON_GRAYLEVEL, 15);
            printer.setConfig(bundle);

            printer.appendPrnStr("AGENT: " + mer_name, fontSize, false);
            printer.appendPrnStr("LOC: " + loc, fontSize, false);
            printer.appendPrnStr("TID: " + tid + "  MID: " + mid, fontSize, false);
            printer.appendPrnStr("DATE: " + send_date + "    TIME: " + send_time, fontSize, false);
            printer.appendPrnStr("-----------------------------------------------------", fontSize, false);
            printer.appendPrnStr("CARD #: " + cardNo, fontSize, false);
            printer.appendPrnStr(cholder_name, fontSize, false);
            fontSize = FontFamily.BIG;

            printer.appendPrnStr(tntp, fontSize, false);
            fontSize = FontFamily.MIDDLE;
            printer.appendPrnStr("DATE   TXN TYPE  AMOUNT", fontSize, false);
            for(int i =0; i < temp.length ; i++) {
                printer.appendPrnStr( temp[i] , fontSize, false);
            }

//            fontSize = FontFamily.MIDDLE;
            printer.appendPrnStr("Fee: KES " + prn_fee_amount, fontSize, false);
//            fontSize = FontFamily.BIG;
//            printer.appendPrnStr("          "+txn_status, fontSize, false);
//            fontSize = FontFamily.SMALL;
//            printer.appendPrnStr("Card: "+ctyp, fontSize, false);
//            printer.appendPrnStr("Aid: "+aid, fontSize, false);
//            printer.appendPrnStr("Tsi: "+tsi, fontSize, false);
//            fontSize = FontFamily.MIDDLE;
            printer.appendPrnStr("-----------------------------------------------------", fontSize, false);
            printer.appendPrnStr("Response Code: " + rpc, fontSize, false);
            printer.appendPrnStr("Ref ID: " + rrn, fontSize, false);
            printer.appendPrnStr("App Version: " + appvers, fontSize, false);
            printer.appendPrnStr("-----------------------------------------------------", fontSize, false);
            printer.appendPrnStr("SERVED BY: " + Utility.gettUtilUserId(getApplicationContext()), fontSize, false);
            printer.appendPrnStr("   **** CUSTOMER COPY ****", fontSize, false);
            printer.appendPrnStr("\n\n", fontSize, false);
            printer.startPrint(new OnPrintListener.Stub() {
                @Override
                public void onPrintResult(int ret) throws RemoteException {
                    Log.d("onPrintResult", "onPrintResult = " + ret);
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        SecurityLayer.Log("devmanager", "device manager is not alive");
    }

        public void SetDialog(String msg, String title) {
        new MaterialDialog.Builder(getApplicationContext())
                .title(title)
                .content(msg)
                .negativeText("Close")
                .show();
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

    public void ClearPin() {
        agt_pin.setText("");
    }

    public void Set_Card_Wdl_Data() throws RemoteException {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("MMddhhmmss");
        SimpleDateFormat df_1 = new SimpleDateFormat("HHmmss");
        SimpleDateFormat sdate_df = new SimpleDateFormat("dd MMM yyyy");
        SimpleDateFormat stime_df = new SimpleDateFormat("HH:mm");
        String formattedDate = df.format(c);
        String formattedDate_stan = df_1.format(c);
        send_date = sdate_df.format(c);
        send_time = stime_df.format(c);

        Calendar calendar = Calendar.getInstance();
        int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
        String day_y = String.format("%3s", String.valueOf(dayOfYear)).replace(' ', '0');
        String yr_num = String.valueOf(send_date.charAt(send_date.length() - 1));
        String hr_num = send_time.substring(0, 2);
        String rrn = yr_num + day_y + hr_num + formattedDate_stan;

        ccid = ApplicationClass.get().getGlobalVals().getSimSerial();

        SecurityLayer.Log("ccid >> ",ccid);


        Bundle devInfo = null;
        try {
            devInfo = mSDKManager.getDevInfo();
            serial_no = devInfo.getString(DeviceInfoConstrants.COMMOM_SN);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        //serial_no = "N7NL00479115";
        if(BuildConfig.DEBUG)
            serial_no = "19990004";
//        serial_no="01223125";
        else
        serial_no = serial_no.substring(serial_no.length() - 8);
        String agt_pin_format = agt_pin.getText().toString() + "0" + formattedDate_stan;
        agt_pin_format = String.format("%-16s", agt_pin_format).replace(' ', 'F');
        SecurityLayer.Log("agt_pin_format", "agt_pin_format >>> " + agt_pin_format);

        final byte[] block = new byte[8];
        byte[] tdKeyBytes = string2byte(agt_pin_format);
        final int ret = mSDKManager.getPinPad().desEncByWKey(4, WorkKeyType.TDKEY, tdKeyBytes, tdKeyBytes.length, DesAlgorithmType.TDES, block);
        if (ret == ServiceResult.Success) {
            SecurityLayer.Log("CalTrack", "CalTrack >> " + byte2string(block));
        } else {
            SecurityLayer.Log("CalTrack", "CalTrack >> FAILED!!");
        }
        iso8583 = new ISO8583(getApplicationContext());
        try {
            iso8583.loadXmlFile(CUPS8583);
        } catch (Exception e) {
            e.printStackTrace();
        }
        iso8583.initPack();
        SecurityLayer.Log("iso", "Initialization of iso8583 successful");

        if(tntp.equals("BALANCE INQUIRY")){
            try {
                iso8583.setField(0, "0100");
                iso8583.setField(2, cardNo);
                iso8583.setField(3, pcode);
                iso8583.setField(4, txn_amount);
                iso8583.setField(7, formattedDate);
                iso8583.setField(11, formattedDate_stan);
//                iso8583.setField(14, expiredDate);//expiredDate
//                iso8583.setField(23, cardSN);
                iso8583.setField(19, currency_code);
                iso8583.setField(35, track2); //"5399232099999911D1704221012131697"
                iso8583.setField(37, rrn);
                iso8583.setField(41, tid);
                iso8583.setField(42, mid);
                iso8583.setField(43, fld_43);
                iso8583.setField(46, user_only);
                iso8583.setField(49, currency_code);
                if (!(data_send.equals("")) && (data_send != null) && !(data_send.equals("9000000000000000"))) {
                    iso8583.setField(52, data_send);
                }
                iso8583.setField(57, byte2string(block));
                iso8583.setField(55, data55);
                iso8583.setField(60, serial_no); //serial_no - N7NL00479115
                iso8583.setField(101, appvers);
                iso8583.setField(127, ccid);


            } catch (Exception e) {
                SecurityLayer.Log("iso", "initPack error");
                e.printStackTrace();
                return;
            }

        }else if (tntp.equals("MINI STATEMENT")){
            try {
                iso8583.setField(0, "0100");
                iso8583.setField(2, cardNo);
                iso8583.setField(3, pcode);
                iso8583.setField(4, txn_amount);
                iso8583.setField(7, formattedDate);
                iso8583.setField(11, formattedDate_stan);
//                iso8583.setField(14, expiredDate);//expiredDate
//                iso8583.setField(23, cardSN);
                iso8583.setField(19, currency_code);
                iso8583.setField(35, track2); //"5399232099999911D1704221012131697"
                iso8583.setField(37, rrn);
                iso8583.setField(41, tid);
                iso8583.setField(42, mid);
                iso8583.setField(43, fld_43);
                iso8583.setField(46, user_only);
                iso8583.setField(49, currency_code);
                if (!(data_send.equals("")) && (data_send != null) && !(data_send.equals("9000000000000000"))) {
                    iso8583.setField(52, data_send);
                }
                iso8583.setField(57, byte2string(block));
                iso8583.setField(55, data55);
                iso8583.setField(60, serial_no); //serial_no - N7NL00479115
                iso8583.setField(101, appvers);
                iso8583.setField(127, ccid);


            } catch (Exception e) {
                SecurityLayer.Log("iso", "initPack error");
                e.printStackTrace();
                return;
            }

        }else {
            try {
                iso8583.setField(0, "0200");
                iso8583.setField(2, cardNo);
                iso8583.setField(3, pcode);
                iso8583.setField(4, txn_amount);
                iso8583.setField(7, formattedDate);
                iso8583.setField(11, formattedDate_stan);
                iso8583.setField(14, expiredDate);//expiredDate
                iso8583.setField(23, cardSN);
                iso8583.setField(35, track2); //"5399232099999911D1704221012131697"
                iso8583.setField(37, rrn);
                iso8583.setField(41, tid);
                iso8583.setField(42, mid);
                iso8583.setField(43, fld_43);
                iso8583.setField(46, user_only);
                iso8583.setField(49, currency_code);
                if (!(data_send.equals("")) && (data_send != null) && !(data_send.equals("9000000000000000"))) {
                    iso8583.setField(52, data_send);
                }
                iso8583.setField(55, data55);
                iso8583.setField(57, byte2string(block));
                iso8583.setField(60, serial_no); //serial_no - N7NL00479115
                iso8583.setField(101, appvers);
                iso8583.setField(127, ccid);


            } catch (Exception e) {
                SecurityLayer.Log("iso", "initPack error");
                e.printStackTrace();
                return;
            }
        }
        try {
            requst = iso8583.pack();
            Log.v("iso", "pack card wdl >> Group 8583 package exception >> " + requst);
        } catch (Exception e) {
            Log.v("iso", "pack card wdl >> Group 8583 package exception >> " + e.toString());
            e.printStackTrace();
            return;
        }
        Log.v("req iso", "req iso >> " + requst);
        reqData = BytesUtils.hexToBytes(requst);
    }

    private class Send_Card_Wdl_Txn_Request extends AsyncTask<Void, Void, Void> {
        String dstAddress;
        int dstPort;
        String response = "";
        SSLSocketFactory sslSocketFactory = null;
        SSLSocket sslSocket = null;
        SSLContext sslContext = null;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        Send_Card_Wdl_Txn_Request(String addr, int port) {
            dstAddress = addr;
            dstPort = port;
        }

        @Override
        protected void onPreExecute() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    prgDialog.show();
                }
            });
            try {
                Set_Card_Wdl_Data();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            Socket socket = new Socket();
            BufferedInputStream bis = null;
            BufferedOutputStream bos = null;
            try {
                socket.connect(new InetSocketAddress(dstAddress, dstPort), 15000);
                socket.setSoTimeout(60000);
                bos = new BufferedOutputStream(socket.getOutputStream());
                byte[] reqLenBs = ISOUtils.packIntToBytes(reqData.length, 2, true);
                bos.write(reqLenBs);
                //SecurityLayer.Log("Dump", "Dump --- " + Dump.getHexDump(reqLenBs) + "," + Dump.getHexDump(req));
                System.out.println(Dump.getHexDump(reqLenBs) + "," + Dump.getHexDump(reqData));
                bos.write(reqData);
                bos.flush();

                bis = new BufferedInputStream(socket.getInputStream());
                byte[] respLenbs = new byte[2];
                bis.read(respLenbs);
                int respLen = ISOUtils.unPackIntFromBytes(respLenbs, 0, 2, true);
                System.out.println(respLen);
                byte[] buffer = new byte[respLen];
                bis.read(buffer);
                System.out.println(Dump.getHexDump(buffer));
                respData = buffer;
                status = "1";
            } catch (UnknownHostException e) {
                status = "3";
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                status = "3";
                response = "No response received." + "\n" + "Please try again";
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                        if (bos != null)
                            bos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    SecurityLayer.Log("socket", "user download socket is null");
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            ClearPin();
            prgDialog.dismiss();


            if (status.equals("1")) {
                    if (!(respData == null) && respData.length > 0) {

                        if (pcode.equals("310000")) {
                            Unpack_Card_Beq_Response();
                        } else if (pcode.equals("400000")) {
                            Unpack_Card_MiniStatement_Response();
                        }else {
                            Unpack_Card_Wdl_Response();
                        }

                    if (rpc.equals("00")) {
                        try {
                            if (pcode.equals("310000")) {
                                Print_Card_BEQ_Txn();
                                finish();
                                Intent i = new Intent(NL_Card_Wdl_Agent_PIN.this, FMobActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                // Staring Login Activity
                                startActivity(i);
                            }
                            else if (pcode.equals("400000")) {
                                Print_Card_MiniStatement_Txn();
                                finish();
                                Intent i = new Intent(NL_Card_Wdl_Agent_PIN.this, FMobActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                // Staring Login Activity
                                startActivity(i);
                            }else {
                                Print_Card_Receipt();

                                Intent intent = new Intent(NL_Card_Wdl_Agent_PIN.this, NL_Card_Wdl_Conf_Activity.class);
                                startActivity(intent);
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }


                        } else {
                        try {
                            printDeclineReceipt();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        SecurityLayer.Log("response", "narration == " + narration);
                            if(rpc.equals("96") && narration.equals("Declined,0")){
                                SharedPreferences pref1 = PreferenceManager
                                        .getDefaultSharedPreferences(getApplicationContext());
                                SharedPreferences.Editor editor1 = pref1.edit();
                                editor1.remove("last_kchange");
                                editor1.commit();
                            }
                            setDialog(txn_status + " - " + rpc, narration);
                        }
                    }
            } else {
                SecurityLayer.Log("response", "card withdrawal response == " + response);
                //setDialog("Error", "Unable to send card transaction. Please try again later.");
                setDialog("Error", response);
            }
        }
    }

    public void Unpack_Card_Beq_Response() {
        iso8583 = new ISO8583(getApplicationContext());
        try {
            iso8583.loadXmlFile(CUPS8583);
        } catch (Exception e) {
            e.printStackTrace();
        }
        iso8583.initPack();
        try {
            iso8583.unpack(respData);
            stan = iso8583.getField(11);
            fee = iso8583.getField(28);
            rrn = iso8583.getField(37);
            rpc = iso8583.getField(39);
            balance = iso8583.getField(54);
            auth_id = iso8583.getField(104);
            narration = iso8583.getField(111);
            SecurityLayer.Log("fee", "fee >> " + fee);
            //if(Integer.valueOf(rpc) == 0 || Integer.valueOf(rpc) == 00){
            if (rpc.equals("00")) {
                ApplicationClass.get().getGlobalVals().setSessAuth(iso8583.getField(38));
                ApplicationClass.get().getGlobalVals().setSessAuth(iso8583.getField(104)); //Auth Code
                txn_status = "SUCCESS";
            } else {
                txn_status = "DECLINED";
                SecurityLayer.Log("card wdl", "wdl declined - " + rpc + " >> " + narration);
            }

            ApplicationClass.get().getGlobalVals().setSessPan(cardNo);  //Pan
            ApplicationClass.get().getGlobalVals().setSessAmt(iso8583.getField(4));

            SharedPreferences pref = PreferenceManager
                    .getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("rrn", rrn);
            editor.putString("stan", stan);
            editor.putString("fee", fee);
            editor.putString("auth_id", auth_id);
            editor.commit();
            Get_Balance(balance);
        } catch (Exception e) {
            SecurityLayer.Log("iso", "initunPack error");
            e.printStackTrace();
            return;
        }
    }

    public void Unpack_Card_MiniStatement_Response() {
        iso8583 = new ISO8583(getApplicationContext());
        try {
            iso8583.loadXmlFile(CUPS8583);
        } catch (Exception e) {
            e.printStackTrace();
        }
        iso8583.initPack();
        try {
            iso8583.unpack(respData);
            stan = iso8583.getField(11);
            fee = iso8583.getField(28);
            rrn = iso8583.getField(37);
            rpc = iso8583.getField(39);
            auth_id = iso8583.getField(104);
            narration = iso8583.getField(111);
            fld_112 = iso8583.getField(112);
            if (rpc.equals("00")) {
                ApplicationClass.get().getGlobalVals().setSessAuth(iso8583.getField(38));
                ApplicationClass.get().getGlobalVals().setSessAuth(iso8583.getField(104)); //Auth Code
                txn_status = "SUCCESS";
            } else {
                txn_status = "DECLINED";
                SecurityLayer.Log("card ministatement", "ministatement declined - " + rpc + " >> " + narration);
            }

            ApplicationClass.get().getGlobalVals().setSessPan(cardNo);  //Pan
            ApplicationClass.get().getGlobalVals().setSessAmt(iso8583.getField(4));

            SharedPreferences pref = PreferenceManager
                    .getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("rrn", rrn);
            editor.putString("stan", stan);
            editor.putString("fee", fee);
            editor.putString("auth_id", auth_id);
            editor.commit();
        } catch (Exception e) {
            SecurityLayer.Log("iso", "initunPack error");
            e.printStackTrace();
            return;
        }
    }
    private void Get_Balance(String balance) {
        String full_balance = balance;
        String avail_last_12, fmamo = "";
        String bbamo = "";
        String avail_17 = full_balance.substring(0, 17);
        avail_last_12 = avail_17.substring(avail_17.length() - 12);
        while (avail_last_12.length() > 0 && avail_last_12.charAt(0) == '0') {
            avail_last_12 = avail_last_12.substring(1);
        }
        avail_last_12 = new StringBuilder(avail_last_12).insert(avail_last_12.length() - 2, ".").toString();
        Double amou = Double.parseDouble(avail_last_12);
        if (amou != null) {
            DecimalFormat df = new DecimalFormat("#,###.##");
            fmamo = df.format(amou);
        }
        available_balance = fmamo;
        SecurityLayer.Log("available_balance", "available_balance >> " + available_balance);

        String book_bal_17 = full_balance.substring(full_balance.length() - 17);
        String book_bal_last_12 = book_bal_17.substring(book_bal_17.length() - 12);
        while (book_bal_last_12.length() > 0 && book_bal_last_12.charAt(0) == '0') {
            book_bal_last_12 = book_bal_last_12.substring(1);
        }
        book_bal_last_12 = new StringBuilder(book_bal_last_12).insert(book_bal_last_12.length() - 2, ".").toString();
        Double book_bal_amou = Double.parseDouble(book_bal_last_12);
        if (book_bal_amou != null) {
            DecimalFormat df = new DecimalFormat("#,###.##");
            bbamo = df.format(book_bal_amou);
        }
        book_balance = bbamo;
        SecurityLayer.Log("book_balance", "book_balance >> " + book_balance);
    }
    public void Unpack_Card_Wdl_Response() {
        iso8583 = new ISO8583(getApplicationContext());
        try {
            iso8583.loadXmlFile(CUPS8583);
        } catch (Exception e) {
            e.printStackTrace();
        }
        iso8583.initPack();
        try {
            iso8583.unpack(respData);
            rpc = iso8583.getField(39);
            stan = iso8583.getField(11);
            rrn = iso8583.getField(37);
            narration = iso8583.getField(111);
            fee = iso8583.getField(28);
            if (rpc.equals("00")) {
                ApplicationClass.get().getGlobalVals().setSessAuth(iso8583.getField(38));
                ApplicationClass.get().getGlobalVals().setSessAuth(iso8583.getField(104)); //Auth Code
                ApplicationClass.get().getGlobalVals().setFeep(iso8583.getField(28)); //fee
            }
            ApplicationClass.get().getGlobalVals().setSessPan(cardNo);  //Pan
            ApplicationClass.get().getGlobalVals().setSessAmt(iso8583.getField(4));
            ApplicationClass.get().getGlobalVals().setResp(rpc);
            ApplicationClass.get().getGlobalVals().setNarr(narration);

            String[] firstsplit = narration.split(Pattern.quote(","));
            reason = firstsplit[0];
            auth = firstsplit[1];
            Log.v("rpc", rpc);
            if (rpc.equals("00")) {
                txn_status = "APPROVED";
            } else if (rpc.equals("09")) {
                txn_status = "PENDING";
            } else {
                txn_status = "DECLINED";
                Log.v("card wdl", "wdl declined - " + rpc + " >> " + narration);
            }
            ApplicationClass.get().getGlobalVals().setTxnSta(txn_status);
            SharedPreferences pref = PreferenceManager
                    .getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("rrn", rrn);
            editor.putString("stan", stan);
            editor.putString("auth", auth);
            editor.commit();

        } catch (Exception e) {
            Log.v("iso", "initunPack error");
            e.printStackTrace();
            return;
        }
    }

    /*public void Print_Card_Wdl_Txn(){
        try {
            cardNo = maskString(cardNo, 4, 12, '*');
        } catch (Exception e) {
            e.printStackTrace();
        }
        txn_amount = txn_amount.substring(0, txn_amount.length() - 2);
        final String prn_amount = Utility.returnNumberFormat(txn_amount);

        if (n900Device.isDeviceAlive()) {
            final Printer printer;
            printer = n900Device.getPrinter();
            printer.init();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.v("Running ", "running Print_Attempt_SMS thread");
                    if (printer.getStatus() == PrinterStatus.NORMAL) {
                        try {
                            StringBuffer scriptBuffer = new StringBuffer();
                            String fontsPath = printer.getFontsPath(NL_FBNCardWdlAgentPIN.this, "arial.ttf", true);
                            if(fontsPath != null) {
                                scriptBuffer.append("!font "+fontsPath+"\n");//set ttf font path
                                Log.e("Font Success", "Font Success");
                                Map<String, Bitmap> map = new HashMap<String, Bitmap>();
                                Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.mipmap.receipt_logo);
                                String bmp1 = "logo";
                                map.put(bmp1, bitmap1);
                                scriptBuffer.append("*image c 300*100 path:" + bmp1 + "\n");//set image large threshold and  align left
                                scriptBuffer.append("!asc n\n !gray 4\n");//   Set  body content font as medium.
                                scriptBuffer.append("*text l AGENT: " + mer_name + "\n");
                                scriptBuffer.append("*text l LOC: " + loc + "\n");
                                scriptBuffer.append("*text l TID: " + tid + "  MID: " + mid + "\n");
                                scriptBuffer.append("*text l CARD NUMBER: " + cardNo + "\n");
                                scriptBuffer.append("*text l HOLDER: " + cholder_name + "\n");
                                scriptBuffer.append("*text l DATE: " + send_date + "    TIME: " + send_time + "\n");
                                scriptBuffer.append("*line" + "\n");// Print a dotted line
                                scriptBuffer.append("*text l CARD: " + card_label.toUpperCase() + "\n");
                                scriptBuffer.append("*text l AID: " + aid + "\n");
                                scriptBuffer.append("*text l TVR: " + tvr + "\n");
                                scriptBuffer.append("*line" + "\n");// Print a dotted line
                                if(rpc.equals("00")) {
                                    scriptBuffer.append("*text c CARD WITHDRAWAL\n");
                                }else{
                                    scriptBuffer.append("*text c CARD WITHDRAWAL(FAILED)\n");
                                }
                                scriptBuffer.append("!asc nl\n !gray 7\n");// Set  font as large.
                                scriptBuffer.append("*text l TOTAL: " + String.format("%22s", "KES " + prn_amount).replace(' ', ' ') + "\n");
                                scriptBuffer.append("!asc n\n !gray 4\n !yspace 20\n");//   Set  body content font as medium.
                                scriptBuffer.append("!asc n\n !gray 4\n !yspace 6\n");
                                if(rpc.equals("00")) {
                                    scriptBuffer.append("!asc nl\n !gray 7\n");// Set  font as large.
                                    scriptBuffer.append("*text c APPROVED\n");
                                }
                                scriptBuffer.append("!asc n\n !gray 4\n");//   Set  body content font as medium.
                                scriptBuffer.append("*line" + "\n");// Print a dotted line
                                scriptBuffer.append("*text l Response Code: " + rpc + "\n");
                                scriptBuffer.append("*text l Trace No: " + stan + "\n");
                                if(rrn != null) {
                                    scriptBuffer.append("*text l rrn: " + rrn + "\n");
                                }
                                if(!rpc.equals("00")) {
                                    scriptBuffer.append("*text l Reason: " + reason + "\n");
                                }
                                if(rpc.equals("00")) {
                                    scriptBuffer.append("*text l Auth: " + auth + "\n");
                                }
                                scriptBuffer.append("*text l App Version: " + appvers + "\n");
                                scriptBuffer.append("*line" + "\n");// Print a dotted line
                                scriptBuffer.append("*text l Served By: " + Utility.gettUtilUserId(getApplicationContext())+ "\n");
                                scriptBuffer.append("!asc n\n !gray 4\n !yspace 30\n");//   Set  body content font as medium.
                                scriptBuffer.append("*text c **** CUSTOMER COPY ****\n");
                                scriptBuffer.append("*feedline 2\n");
                                PrinterResult printerResult = printer.printByScript(PrintContext.defaultContext(),
                                        scriptBuffer.toString(), map, 60, TimeUnit.SECONDS);
                            }else{
                                Log.e("Font Error", "Font Error");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("Printer error", "Printer Exception" + e);
                        }
                    }
                }
            }).start();
        }else{
            Log.v("devmanager", "device manager is not alive");
        }
    }*/

    public void setDialog(String title, String message) {
        new MaterialDialog.Builder(NL_Card_Wdl_Agent_PIN.this)
                .title(title)
                .content(message)
                .negativeText("Dismiss")
                .cancelable(false)
                .canceledOnTouchOutside(false)
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

    public static String maskString(String strText, int start, int end, char maskChar)
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

    public void LogOut() {
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

    public String initTypeFacePath() {
        String namePath = "arial";
        String mTypefacePath = null;
        if (Build.VERSION.SDK_INT >= 23) {
            mTypefacePath = FileUtils.getExternalCacheDir(NL_Card_Wdl_Agent_PIN.this, namePath + ".ttf");
        } else {
            String filePath = FileUtils.createTmpDir(NL_Card_Wdl_Agent_PIN.this);
            SecurityLayer.Log("initTypeFacePath", "filePath = " + filePath);
            mTypefacePath = filePath + namePath + ".ttf";
        }
        SecurityLayer.Log("initTypeFacePath", "mTypefacePath = " + mTypefacePath);
        try {
            FileUtils.copyFromAssets(NL_Card_Wdl_Agent_PIN.this.getAssets(), namePath, mTypefacePath, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SecurityLayer.Log("initTypeFacePath", "mTypefacePath = " + mTypefacePath);
        return mTypefacePath;
    }
}
