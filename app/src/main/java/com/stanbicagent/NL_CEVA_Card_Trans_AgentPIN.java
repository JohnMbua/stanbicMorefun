package com.stanbicagent;

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
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.afollestad.materialdialogs.MaterialDialog;
import com.morefun.yapi.ServiceResult;
import com.morefun.yapi.device.pinpad.DesAlgorithmType;
import com.morefun.yapi.device.pinpad.WorkKeyType;
import com.morefun.yapi.device.printer.FontFamily;
import com.morefun.yapi.device.printer.OnPrintListener;
import com.morefun.yapi.device.printer.PrinterConfig;
import com.morefun.yapi.engine.DeviceInfoConstrants;
import com.morefun.yapi.engine.DeviceServiceEngine;
import com.newland.mtype.tlv.TLVPackage;
import com.newland.mtype.util.Dump;
import com.newland.mtype.util.ISOUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import publib.BytesUtils;
import publib.ISO8583;
import security.SecurityLayer;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import utils.FileUtils;

import static com.stanbicagent.ApplicationConstants.CUPS8583;
import static com.stanbicagent.ApplicationConstants.SWITCH_IP;
import static com.stanbicagent.ApplicationConstants.SWITCH_PORT;
import static com.stanbicagent.ApplicationConstants.bank_id_name;
import static com.stanbicagent.ApplicationConstants.currency_code;
import static publib.Utils.byte2string;
import static publib.Utils.string2byte;

public class NL_CEVA_Card_Trans_AgentPIN extends BaseActivity implements View.OnClickListener {
    ImageView imageView1;
    EditText agt_pin;
    Button btnsub;
    SessionManagement session;
    ProgressDialog prgDialog;
    String depositid;
    RelativeLayout rlid;
    TextView step2, txtcard_name, txtcard_no, txtamount_disp;
    TextView textViewnb2, txt_ben_name, textViewrr, txt_sendnammm, txt_sendno, acbal, txtcard_holder;
    private static int SPLASH_TIME_OUT = 2500;
    SharedPreferences pref;
    ISO8583 iso8583;
    String requst, fld_120, cardNo, cardSN, expiredDate, serviceCode, track2, data55, data_send, serial_no, txn_amount, card_ttype, acc_ttype = "";
    String tid, mid, mer_name, loc, ccode, user_only, pcode, fld_43, rpc, stan, rrn, send_date, send_time, tvr, aid, cholder_name, reprint_data = "",ccid;
    String narration, txn_status, card_label, status, recanno, amou, narra, accname, sender_name, sender_number, appvers, reason, auth, mask_card_no, mTypeFacePath = "";
    byte[] reqData = null;
    byte[] respData = null;
    byte[] result;
    int check = 0;
    DeviceServiceEngine mSDKManager;
    int  card_no_len = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nl_activity_card_transfer_agtpin);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        mSDKManager = SDKManager.getInstance().getDeviceServiceEngine();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        serial_no = Utility.getSerialNo();
        appvers = Utility.getAppVersion(getApplicationContext());
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayShowHomeEnabled(true); // show or hide the default home button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
        ab.setDisplayShowTitleEnabled(false); // disable the default title element here (for centered title)
        session = new SessionManagement(this);
        rlid = (RelativeLayout) findViewById(R.id.rlid);
        step2 = (TextView) findViewById(R.id.tv2);
        step2.setOnClickListener(this);
        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Processing transaction" + "\n" + "Please wait.....");
        prgDialog.setCancelable(false);
        agt_pin = (EditText) findViewById(R.id.agt_pin);
        txtcard_no = (TextView) findViewById(R.id.txtcard_no);
        btnsub = (Button) findViewById(R.id.button2);
        btnsub.setOnClickListener(this);
        mTypeFacePath = initTypeFacePath();

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        user_only = Utility.gettUtilUserId(getApplicationContext());
        tid = pref.getString("tid", "");
        mid = pref.getString("mid", "");
        mer_name = pref.getString("mer_name", "");
        loc = pref.getString("loc", "");
        ccode = pref.getString("ccode", "");
        fld_43 = pref.getString("fld_43", "");

        cardNo = pref.getString("cardNo", "");
        String cardno_full = String.valueOf(cardNo.charAt(cardNo.length() - 1));
        if (cardno_full.equals("F")) {
            cardNo = cardNo.substring(0, cardNo.length() - 1);
        }
        card_no_len = cardNo.length();
        try {
            if(card_no_len > 16){
                mask_card_no = maskString(cardNo, 6, 15, '*');
            }else {
                mask_card_no = maskString(cardNo, 6, 12, '*');
            }
            txtcard_no.setText(mask_card_no);
        } catch (Exception e) {
            e.printStackTrace();
        }
        cardSN = pref.getString("cardSN", "");
        expiredDate = pref.getString("expiredDate", "");
        tvr = pref.getString("tvr", "");
        aid = pref.getString("aid", "");
        track2 = pref.getString("track2", "");
        String clabel, cholder = "";
        clabel = pref.getString("card_label", "");
        card_label = new String(ISOUtils.hex2byte(clabel));
        cholder = pref.getString("cholder_name", "");
        card_ttype = pref.getString("card_ttype", "");
        acc_ttype = pref.getString("acc_ttype", "");
        cholder_name = new String(ISOUtils.hex2byte(cholder));

        ccid = ApplicationClass.get().getGlobalVals().getSimSerial();

        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("cholder_name", cholder_name);
        editor.putString("card_label", card_label);
        editor.putString("tvr", tvr);
        editor.putString("aid", aid);
        editor.commit();

        data55 = pref.getString("data55", "");
        if (cardSN == null) {
            cardSN = "000";
        } else {
            cardSN = ISOUtils.padleft(cardSN, 3, '0');
        }
        if (expiredDate.length() == 6) {
            expiredDate = expiredDate.substring(0, expiredDate.length() - 2);
        }
        //track2 = pref.getString("track_data", "");
        SecurityLayer.Log("cardSN", "3 - cardSN > " + cardSN);
        SecurityLayer.Log("data55", "3 - data55 > " + data55);
        String track2_full = String.valueOf(track2.charAt(track2.length() - 1));
        if (track2_full.equals("F")) {
            track2 = track2.substring(0, track2.length() - 1);
        }
        data_send = pref.getString("data_send", "");
        pcode = "510000";
        textViewnb2 = (TextView) findViewById(R.id.textViewnb2);
        txt_ben_name = (TextView) findViewById(R.id.txt_ben_name);
        acbal = (TextView) findViewById(R.id.txtacbal);
        txtamount_disp = (TextView) findViewById(R.id.txtamount_disp);
        textViewrr = (TextView) findViewById(R.id.textViewrr);
        txt_sendnammm = (TextView) findViewById(R.id.txt_sendnammm);
        txt_sendno = (TextView) findViewById(R.id.txt_sendno);
        txtcard_holder = (TextView) findViewById(R.id.txtcard_holder);
        Intent intent = getIntent();
        if (intent != null) {
            recanno = pref.getString("recanno", "");
            accname = pref.getString("txtname", "");
            amou = pref.getString("txn_amount", "");
            sender_name = pref.getString("ednamee", "");
            sender_number = pref.getString("ednumbb", "");
            narra = pref.getString("narra", "");
            txn_amount = amou + "00";
            textViewnb2.setText(recanno);
            txt_ben_name.setText(accname);
            String amt_disp = Utility.returnNumberFormat(amou);
            txtamount_disp.setText(amt_disp);
            textViewrr.setText(narra);
            txt_sendnammm.setText(sender_name);
            txt_sendno.setText(sender_number);
            txtcard_holder.setText(cholder_name);
            SecurityLayer.Log("acc details", "acc details = " + recanno + " > " + accname + " > " + txn_amount + " > " + sender_name + " > " + sender_number);
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
            if (Utility.checkInternetConnection(this)) {
                final String pin = agt_pin.getText().toString();
                if (Utility.isNotNull(pin) && (pin.length() == 5)) {
                    //Check_Printer();
                    After_Printer_Check();
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
            Intent intent = new Intent(NL_CEVA_Card_Trans_AgentPIN.this, HomeAccountFragNewUI.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    /*public void Check_Printer() {
        if (n900Device.isDeviceAlive()) {
            final Printer printer;
            printer = n900Device.getPrinter();
            printer.init();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (printer.getStatus() == PrinterStatus.NORMAL) {
                        check = 0;
                    } else {
                        check = -1;
                    }
                    After_Printer_Check();
                }
            }).start();
        } else {
            Log.v("devmanager", "device manager is not alive");
        }
    }*/

    public void After_Printer_Check() {
        //if (check == 0) {
        Send_Card_Wdl_Txn_Request card_wdl_txn_client = new Send_Card_Wdl_Txn_Request(SWITCH_IP, SWITCH_PORT);
        card_wdl_txn_client.execute();
        /*} else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ClearPin();
                    Toast.makeText(
                            getApplicationContext(),
                            "Please insert paper receipt before transacting.",
                            Toast.LENGTH_LONG).show();
                }
            });
        }*/
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

    private void Set_Card_Transfer_Data() throws RemoteException {
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
            iso8583.setField(47, accname);
            iso8583.setField(49, currency_code);
            if (Utility.isNotNull(data_send)) {
                iso8583.setField(52, data_send);
            }
            iso8583.setField(55, data55);
            iso8583.setField(57, byte2string(block));
            iso8583.setField(58, narra);
            iso8583.setField(59, sender_name);
            iso8583.setField(60, serial_no);
            iso8583.setField(61, sender_number);
            iso8583.setField(101, appvers);
            iso8583.setField(103, recanno);
            //iso8583.setField(121, bank_id_name);
            iso8583.setField(127, ccid);
        } catch (Exception e) {
            SecurityLayer.Log("iso", "initPack error");
            e.printStackTrace();
            return;
        }
        try {
            requst = iso8583.pack();
        } catch (Exception e) {
            SecurityLayer.Log("iso", "pack card wdl >> Group 8583 package exception >> " + e.toString());
            e.printStackTrace();
            return;
        }
        SecurityLayer.Log("req iso", "req iso >> " + requst);
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
                Set_Card_Transfer_Data();
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
                    Unpack_Card_Transfer_Response();
                    reprint_data = cardNo + "*" + txn_amount + "*" + cholder_name + "*" + "CARD TRANSFER" + "*" + auth + "*" + rpc + "*" + rrn + "*" + reason + "*" + tvr + "*" + aid;
                    session.setString(SecurityLayer.KEY_REPRINT, reprint_data);
                    try {
                        Print_Card_Wdl_Txn();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    if (rpc.equals("00")) {
                        Intent intent = new Intent(NL_CEVA_Card_Trans_AgentPIN.this, NL_CEVA_Card_Transfer_Conf_Activity.class); //NL_CEVA_Card_Transfer_Conf_Activity
                        intent.putExtra("recanno", recanno);
                        intent.putExtra("amou", amou);
                        intent.putExtra("narra", narra);
                        intent.putExtra("ednamee", sender_name);
                        intent.putExtra("ednumbb", sender_number);
                        intent.putExtra("txtname", accname);
                        startActivity(intent);
                    } else {
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
                SecurityLayer.Log("response", "card transfer response == " + response);
                //setDialog("Error", "Unable to send card transaction. Please try again later.");
                setDialog("Error", response);
            }
        }
    }

    /*private class Send_Card_Wdl_Txn_Request extends AsyncTask<Void, Void, Void> {
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
                Set_Card_Transfer_Data();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                final TrustManager[] trustAllCerts = new TrustManager[]{
                        new X509TrustManager() {
                            @Override
                            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                            }

                            @Override
                            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                            }

                            @Override
                            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                                return new java.security.cert.X509Certificate[]{};
                            }
                        }
                };

                sslContext = SSLContext.getInstance("TLSv1.2");
                sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
                sslSocketFactory = sslContext.getSocketFactory();
                sslSocket = (SSLSocket) sslSocketFactory.createSocket(dstAddress, dstPort);
                sslSocket.setSoTimeout(65000);
                bos = new BufferedOutputStream(sslSocket.getOutputStream());
                byte[] reqLenBs = ISOUtils.packIntToBytes(reqData.length, 2, true);
                bos.write(reqLenBs);
                SecurityLayer.Log("Dump", "Dump --- " + Dump.getHexDump(reqLenBs) + "," + Dump.getHexDump(reqData));
                //System.out.println(Dump.getHexDump(reqLenBs) + "," + Dump.getHexDump(reqData));
                bos.write(reqData);
                bos.flush();

                bis = new BufferedInputStream(sslSocket.getInputStream());
                byte[] respLenbs = new byte[2];
                bis.read(respLenbs);
                int respLen = ISOUtils.unPackIntFromBytes(respLenbs, 0, 2, true);
                //System.out.println(respLen);
                byte[] buffer = new byte[respLen];
                bis.read(buffer);
                respData = buffer;
                SecurityLayer.Log("respData Dump", "respData Dump --- " + Dump.getHexDump(respLenbs) + "," + Dump.getHexDump(respData));
                status = "1";
            } catch (UnknownHostException e) {
                status = "3";
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                status = "3";
                e.printStackTrace();
                //response = "IOException: " + e.toString();
                response = "Unable to connect to server." + "\n" + "Please try again";
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                if (sslSocket != null) {
                    try {
                        sslSocket.close();
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
                    Unpack_Card_Transfer_Response();
                    reprint_data = cardNo + "*" + txn_amount + "*" + cholder_name + "*" + "CARD TRANSFER" + "*" + auth + "*" + rpc + "*" + rrn + "*" + reason + "*" + tvr + "*" + aid;
                    session.setString(SecurityLayer.KEY_REPRINT, reprint_data);
                    try {
                        Print_Card_Wdl_Txn();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    if (rpc.equals("00")) {
                        Intent intent = new Intent(NL_CEVA_Card_Trans_AgentPIN.this, HomeAccountFragNewUI.class); //NL_CEVA_Card_Transfer_Conf_Activity
                        intent.putExtra("recanno", recanno);
                        intent.putExtra("amou", amou);
                        intent.putExtra("narra", narra);
                        intent.putExtra("ednamee", sender_name);
                        intent.putExtra("ednumbb", sender_number);
                        intent.putExtra("txtname", accname);
                        startActivity(intent);
                    } else {
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
                SecurityLayer.Log("response", "card transfer response == " + response);
                //setDialog("Error", "Unable to send card transaction. Please try again later.");
                setDialog("Error", response);
            }
        }
    }*/

    public void Unpack_Card_Transfer_Response() {
        SecurityLayer.Log("Unpack_Card_Transfer_Response", "Unpack_Card_Transfer_Response >> " + status);
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
            String[] firstsplit = narration.split(Pattern.quote(","));
            reason = firstsplit[0];
            auth = firstsplit[1];
            SecurityLayer.Log("rpc", rpc);
            if (rpc.equals("00")) {
                txn_status = "SUCCESS";
            } else if (rpc.equals("09")) {
                txn_status = "PENDING";
            } else {
                txn_status = "DECLINED";
                SecurityLayer.Log("card trand", "trans declined - " + rpc + " >> " + narration);
            }

            SharedPreferences pref = PreferenceManager
                    .getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("rrn", rrn);
            editor.putString("stan", stan);
            editor.putString("auth", auth);
            editor.commit();

        } catch (Exception e) {
            SecurityLayer.Log("iso", "initunPack error");
            e.printStackTrace();
            return;
        }
    }

    public void Print_Card_Wdl_Txn() throws RemoteException {
        com.morefun.yapi.device.printer.Printer printer = mSDKManager.getPrinter();
        try {
            if(card_no_len > 16){
                cardNo = maskString(cardNo, 6, 15, '*');
            }else {
                cardNo = maskString(cardNo, 6, 12, '*');
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        txn_amount = txn_amount.substring(0, txn_amount.length() - 2);
        final String prn_amount = Utility.returnNumberFormat(txn_amount);
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
            bundle.putInt(PrinterConfig.COMMON_GRAYLEVEL,  ApplicationConstants.printer_gray_scale_level);
            printer.setConfig(bundle);

            printer.appendPrnStr("AGENT: " + mer_name, fontSize, false);
            printer.appendPrnStr("LOC: " + loc, fontSize, false);
            printer.appendPrnStr("CARD NUMBER: " + cardNo, fontSize, false);
            printer.appendPrnStr("HOLDER: " + cholder_name, fontSize, false);
            printer.appendPrnStr("TID: " + tid + "  MID: " + mid, fontSize, false);
            printer.appendPrnStr("DATE: " + send_date + "        TIME: " + send_time, fontSize, false);
            printer.appendPrnStr("-----------------------------------------------------", fontSize, false);
            printer.appendPrnStr("TO ACC: " + recanno, fontSize, false);
            printer.appendPrnStr("ACC NAME: " + accname, fontSize, false);
            printer.appendPrnStr("SENDER NAME: " + sender_name, fontSize, false);
            printer.appendPrnStr("SENDER NUMBER: " + sender_number, fontSize, false);
            printer.appendPrnStr("NARRATION: " + narra, fontSize, false);
            printer.appendPrnStr("-----------------------------------------------------", fontSize, false);
            printer.appendPrnStr("CARD: " + card_label, fontSize, false);
            printer.appendPrnStr("AID: " + aid, fontSize, false);
            printer.appendPrnStr("TVR: " + tvr, fontSize, false);
            printer.appendPrnStr("-----------------------------------------------------", fontSize, false);
            if (rpc.equals("00")) {
                printer.appendPrnStr("CARD TRANSFER", fontSize, false);
            } else {
                printer.appendPrnStr("CARD TRANSFER(FAILED)", fontSize, false);
            }
            fontSize = FontFamily.BIG;
            printer.appendPrnStr("TOTAL: KES " + txn_amount, fontSize, false);
            if (rpc.equals("00")) {
                fontSize = FontFamily.BIG;
                printer.appendPrnStr("     APPROVED", fontSize, false);
            }
            fontSize = FontFamily.MIDDLE;

            printer.appendPrnStr("-----------------------------------------------------", fontSize, false);
            printer.appendPrnStr("Response Code: " + rpc, fontSize, false);
            printer.appendPrnStr("Trace No: " + stan, fontSize, false);
            if (rrn != null) {
                printer.appendPrnStr("Ref ID: " + rrn, fontSize, false);
            }
            if (!rpc.equals("00")) {
                printer.appendPrnStr("Reason: " + reason, fontSize, false);
            }
            if (rpc.equals("00")) {
                printer.appendPrnStr("Auth: " + auth, fontSize, false);
            }
            printer.appendPrnStr("App Version: " + appvers , fontSize, false);
            printer.appendPrnStr("-----------------------------------------------------", fontSize, false);
            printer.appendPrnStr("SERVED BY: " + Utility.gettUtilUserId(getApplicationContext()), fontSize, false);
            printer.appendPrnStr("      **** CUSTOMER COPY ****", fontSize, false);
            printer.appendPrnStr("\n\n", fontSize, false);
            printer.startPrint(new OnPrintListener.Stub() {
                @Override
                public void onPrintResult(int ret) throws RemoteException {
                    SecurityLayer.Log("onPrintResult", "onPrintResult = " + ret);
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
            mTypefacePath = FileUtils.getExternalCacheDir(NL_CEVA_Card_Trans_AgentPIN.this, namePath + ".ttf");
        }else {
            String filePath  = FileUtils.createTmpDir(NL_CEVA_Card_Trans_AgentPIN.this);
            SecurityLayer.Log("initTypeFacePath" ,"filePath = " + filePath);
            mTypefacePath = filePath + namePath +".ttf";
        }
        SecurityLayer.Log("initTypeFacePath" ,"mTypefacePath = " + mTypefacePath);
        try {
            FileUtils.copyFromAssets(NL_CEVA_Card_Trans_AgentPIN.this.getAssets(), namePath, mTypefacePath ,true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SecurityLayer.Log("initTypeFacePath" ,"mTypefacePath = " + mTypefacePath);
        return mTypefacePath ;
    }

    public void setDialog(String title, String message) {
        new MaterialDialog.Builder(NL_CEVA_Card_Trans_AgentPIN.this)
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
}
