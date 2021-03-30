package com.stanbicagent;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.preference.PreferenceManager;

import androidx.fragment.app.Fragment;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.stanbicagent.BuildConfig;
import com.stanbicagent.R;
import com.afollestad.materialdialogs.MaterialDialog;
import com.morefun.yapi.ServiceResult;
import com.morefun.yapi.device.pinpad.PinPad;
import com.morefun.yapi.device.pinpad.WorkKeyType;
import com.morefun.yapi.device.reader.icc.ICCSearchResult;
import com.morefun.yapi.device.reader.icc.IccCardReader;
import com.morefun.yapi.device.reader.icc.IccCardType;
import com.morefun.yapi.device.reader.icc.IccReaderSlot;
import com.morefun.yapi.device.reader.icc.OnSearchIccCardListener;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import adapter.adapter.DepoMenuAdapt;
import adapter.adapter.OTBList;
import publib.BytesUtils;
import publib.ISO8583;
import publib.LoggerUtils;
import security.SecurityLayer;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import utils.mfdes;

import static com.stanbicagent.ApplicationConstants.CUPS8583;
import static com.stanbicagent.ApplicationConstants.SWITCH_IP;
import static com.stanbicagent.ApplicationConstants.SWITCH_PORT;
import static com.stanbicagent.ApplicationConstants.TDK;
import static com.stanbicagent.ApplicationConstants.TLK;
import static publib.Utils.string2byte;

public class NL_CardWdlInqCard extends BaseActivity {
    GridView gridView;
    List<OTBList> planetsList = new ArrayList<OTBList>();
    String ptype;
    ListView lv;
    DepoMenuAdapt aAdpt;
    ProgressDialog prgDialog, prgDialog2;
    SessionManagement session;
    String sbpam = "0", pramo = "0";
    boolean blsbp = false, blpr = false, blpf = false, bllr = false, blms = false, blmpesa = false, blcash = false;
    ArrayList<String> ds = new ArrayList<String>();
    DeviceServiceEngine mSDKManager;
    int check = 0;
    String tid, mid, mer_name, serial_no,ccid, requst, fld_120, fld_43, enc_tmk, enc_tpk, last_key_change, curr_date, tmk_tpk_combined, status,
            appvers, clr_tdk, clr_tmk, clr_tpk = "";
    ISO8583 iso8583;
    byte[] reqData = null;
    byte[] respData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nl_activity_wdlmenu);
        session = new SessionManagement(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        appvers = Utility.getAppVersion(getApplicationContext());
        SDKManager.getInstance().bindService(getApplicationContext());
        // Get the ActionBar here to configure the way it behaves.
        final ActionBar ab = getSupportActionBar();
        //ab.setHomeAsUpIndicator(R.drawable.ic_menu); // set a custom icon for the default home button
        ab.setDisplayShowHomeEnabled(true); // show or hide the default home button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
        ab.setDisplayShowTitleEnabled(false); // disable the default title element here (for centered title)
        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Please wait...");
        prgDialog2 = new ProgressDialog(this);
        prgDialog2.setMessage("Loading....");
        gridView = (GridView) findViewById(R.id.gridView1);
        prgDialog.setCancelable(false);
        lv = (ListView) findViewById(R.id.lv);
        SetPop();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Fragment fragment = null;
                String title = null;
                SharedPreferences pref1 = PreferenceManager
                        .getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor1 = pref1.edit();
                editor1.remove("cardNo");
                editor1.remove("cardSN");
                editor1.remove("expiredDate");
                editor1.remove("serviceCode");
                editor1.remove("card_ttype");
                editor1.remove("acc_ttype");
                editor1.remove("track_data");
                editor1.remove("ctyp");
                editor1.remove("aid");
                editor1.remove("tsi");
                editor1.remove("card_label");
                editor1.remove("data55");
                editor1.remove("data_send");
                editor1.commit();
                if (position == 0) {
                    SharedPreferences pref = PreferenceManager
                            .getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("card_ttype", "FWDL");
                    editor.putString("trans_type", "WITHD");
                    editor.commit();
                    if (Utility.checkInternetConnection(NL_CardWdlInqCard.this)) {
                        WDL_Check_Term_Status();
                    }
                } else if (position == 1) {
                    startActivity(new Intent(NL_CardWdlInqCard.this, WithdrawActivity.class));
                }
                if (fragment != null) {

                }
            }
        });

        session.setString("bankname", null);
        session.setString("bankcode", null);
        session.setString("recanno", null);

        ccid = ApplicationClass.get().getGlobalVals().getSimSerial();

    }

    private void WDL_Check_Term_Status() {
        mSDKManager = SDKManager.getInstance().getDeviceServiceEngine();
        if (mSDKManager == null) {
            SecurityLayer.Log("MF_SDKManager", "ServiceEngine is Null");
            return;
        }
        Check_Card();
    }

    public void Check_Card() {
        SecurityLayer.Log("Check_Card", "Check_Card");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final IccCardReader iccCardReader = mSDKManager.getIccCardReader(IccReaderSlot.ICSlOT1);
                    OnSearchIccCardListener.Stub listener = new OnSearchIccCardListener.Stub() {
                        @Override
                        public void onSearchResult(final int retCode, Bundle bundle) throws RemoteException {
                            String cardType = bundle.getString(ICCSearchResult.CARDTYPE);
                            SecurityLayer.Log("onSearchResult", "retCode= " + retCode + "," + cardType);
                            iccCardReader.stopSearch();
                            check = retCode;
                            After_Card_Check();
                            //mSDKManager.getBeeper().beep(retCode == ServiceResult.Success ? BeepModeConstrants.SUCCESS : BeepModeConstrants.FAIL);
                        }
                    };
                    iccCardReader.searchCard(listener, 1, new String[]{IccCardType.CPUCARD, IccCardType.AT24CXX, IccCardType.AT88SC102});
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void After_Card_Check() {
        if (check != 0) {
            SharedPreferences pref = PreferenceManager
                    .getDefaultSharedPreferences(getApplicationContext());
            tid = pref.getString("tid", "");
            mid = pref.getString("mid", "");
            mer_name = pref.getString("mer_name", "");
            fld_43 = pref.getString("fld_43", "");
            if (tid.equals("") && mid.equals("") && mer_name.equals("") && fld_43.equals("")) {
                Send_User_Download_Request user_download_client = new Send_User_Download_Request(SWITCH_IP, SWITCH_PORT);
                user_download_client.execute();
            } else {
                int cked = Check_Key_Exchange_Date();
                if (cked == 0) {
                    startActivity(new Intent(NL_CardWdlInqCard.this, NL_CardWdlTransAmountActivity.class));
                } else {
                    Send_Key_Exchange_Request key_exchange_client = new Send_Key_Exchange_Request(SWITCH_IP, SWITCH_PORT);
                    key_exchange_client.execute();
                }
            }
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //setDialog("Card is inserted in POS.\nPlease remove card before transacting.");
                    Toast.makeText(
                            getApplicationContext(),
                            "Please remove card before starting card transaction.",
                            Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public int Check_Key_Exchange_Date() {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat sdate_df = new SimpleDateFormat("yyyy-MM-dd");
        curr_date = sdate_df.format(c);
        last_key_change = pref.getString("last_kchange", "");
        if (last_key_change.equals("")) {
            return -1;
        }
        if (last_key_change.equals(curr_date)) {
            return 0;
        }
        return -1;
    }

    private class Send_User_Download_Request extends AsyncTask<Void, Void, Void> {
        String dstAddress;
        int dstPort;
        String response = "";
        SSLSocketFactory sslSocketFactory = null;
        SSLSocket sslSocket = null;
        SSLContext sslContext = null;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        Send_User_Download_Request(String addr, int port) {
            dstAddress = addr;
            dstPort = port;
        }

        @Override
        protected void onPreExecute() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    prgDialog.setMessage("Downloading terminal parameters....");
                    prgDialog.show();
                }
            });
            Set_User_Download_Data();
        }

//        @Override
//        protected Void doInBackground(Void... arg0) {
//            try {
//                final TrustManager[] trustAllCerts = new TrustManager[]{
//                        new X509TrustManager() {
//                            @Override
//                            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
//                            }
//
//                            @Override
//                            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
//                            }
//
//                            @Override
//                            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
//                                return new java.security.cert.X509Certificate[]{};
//                            }
//                        }
//                };
//
//                sslContext = SSLContext.getInstance("TLSv1.2");
//                sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
//                sslSocketFactory = sslContext.getSocketFactory();
//                sslSocket = (SSLSocket) sslSocketFactory.createSocket(dstAddress, dstPort);
//                sslSocket.setSoTimeout(65000);
//                bos = new BufferedOutputStream(sslSocket.getOutputStream());
//                byte[] reqLenBs = ISOUtils.packIntToBytes(reqData.length, 2, true);
//                bos.write(reqLenBs);
//                SecurityLayer.Log("Dump", "Dump --- " + Dump.getHexDump(reqLenBs) + "," + Dump.getHexDump(reqData));
//                System.out.println(Dump.getHexDump(reqLenBs) + "," + Dump.getHexDump(reqData));
//                bos.write(reqData);
//                bos.flush();
//
//                bis = new BufferedInputStream(sslSocket.getInputStream());
//                byte[] respLenbs = new byte[2];
//                bis.read(respLenbs);
//                int respLen = ISOUtils.unPackIntFromBytes(respLenbs, 0, 2, true);
//                System.out.println(respLen);
//                byte[] buffer = new byte[respLen];
//                bis.read(buffer);
//                System.out.println(Dump.getHexDump(buffer));
//                respData = buffer;
//                status = "1";
//            } catch (UnknownHostException e) {
//                status = "3";
//                response = "UnknownHostException: " + e.toString();
//            } catch (IOException e) {
//                status = "3";
//                e.printStackTrace();
//                //response = "IOException: " + e.toString();
//                response = "Unable to connect to server." + "\n" + "Please try again";
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            } finally {
//                if (sslSocket != null) {
//                    try {
//                        sslSocket.close();
//                        if (bos != null)
//                            bos.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    Log.v("socket", "user download socket is null");
//                }
//            }
//            return null;
//        }

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
            prgDialog.dismiss();
            if (status.equals("1")) {
                if (!(respData == null) && respData.length > 0) {
                    Unpack_User_Download_Data();
                }
            } else {
                Log.v("response", "user download response == " + response);
                setDialog("Error", response);
            }
        }
    }

    private class Send_Key_Exchange_Request extends AsyncTask<Void, Void, Void> {
        String dstAddress;
        int dstPort;
        String response = "";
        SSLSocketFactory sslSocketFactory = null;
        SSLSocket sslSocket = null;
        SSLContext sslContext = null;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        Send_Key_Exchange_Request(String addr, int port) {
            dstAddress = addr;
            dstPort = port;
        }

        @Override
        protected void onPreExecute() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    prgDialog.setMessage("Performing Key Exchange....");
                    prgDialog.show();
                }
            });
            Set_Key_Exchange_Data();
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
            prgDialog.dismiss();
            if (status.equals("1")) {
                if (!(respData == null) && respData.length > 0) {
                    Unpack_Key_Exchange_Data();
                }
            } else {
                SecurityLayer.Log("response", "user download response == " + response);
                setDialog("Error", response);
            }
        }
    }

    public void Set_Key_Exchange_Data() {
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
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("MMddhhmmss");
        String formattedDate = df.format(c);
        iso8583 = new ISO8583(getApplicationContext());
        try {
            iso8583.loadXmlFile(CUPS8583);
        } catch (Exception e) {
            e.printStackTrace();
        }
        iso8583.initPack();
        try {
            iso8583.setField(0, "0800");
            iso8583.setField(3, "990180");
            iso8583.setField(7, formattedDate);
            iso8583.setField(60, serial_no); //serial_no - N7NL00479115
            iso8583.setField(127, ccid);
        } catch (Exception e) {
            Log.v("iso", "initPack error");
            e.printStackTrace();
            return;
        }
        try {
            requst = iso8583.pack();
        } catch (Exception e) {
            LoggerUtils.e("Group 8583 package exception");
            e.printStackTrace();
            return;
        }
        Log.v("key exchange req iso", requst);
        reqData = BytesUtils.hexToBytes(requst);
    }

    public void Set_User_Download_Data() {
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
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("MMddhhmmss");
        SimpleDateFormat df_1 = new SimpleDateFormat("hhmmss");
        String formattedDate = df.format(c);
        String formattedDate_stan = df_1.format(c);
        iso8583 = new ISO8583(getApplicationContext());
        try {
            iso8583.loadXmlFile(CUPS8583);
        } catch (Exception e) {
            e.printStackTrace();
        }
        iso8583.initPack();
        try {
            iso8583.setField(0, "0800");
            iso8583.setField(3, "900280");
            iso8583.setField(7, formattedDate);
            iso8583.setField(11, formattedDate_stan);
            iso8583.setField(60, serial_no); //serial_no - N7NL00479115
            iso8583.setField(101, appvers);
            iso8583.setField(127, ccid);
        } catch (Exception e) {
            SecurityLayer.Log("iso", "initPack error");
            e.printStackTrace();
            return;
        }
        try {
            requst = iso8583.pack();
        } catch (Exception e) {
            LoggerUtils.e("Group 8583 package exception");
            e.printStackTrace();
            return;
        }
        Log.v("user download req iso", requst);
        reqData = BytesUtils.hexToBytes(requst);
    }

    public void Unpack_User_Download_Data() {
        iso8583 = new ISO8583(getApplicationContext());
        try {
            iso8583.loadXmlFile(CUPS8583);
        } catch (Exception e) {
            e.printStackTrace();
        }

        iso8583.initPack();
        try {
            iso8583.unpack(respData);
            String rpc = iso8583.getField(39);
            String fl_111 = iso8583.getField(111);
            SecurityLayer.Log("rpc", rpc);
            if (rpc.equals("00")) {
                fld_120 = iso8583.getField(120);
                Split_Fld_120();
            } else {
                SecurityLayer.Log("ud response", fl_111);
                setDialog("Failed", "Response Code: " + rpc + "\n" + fl_111);
            }
        } catch (Exception e) {
            SecurityLayer.Log("iso", "initunPack error");
            e.printStackTrace();
            return;
        }
    }

    public void Unpack_Key_Exchange_Data() {
        iso8583 = new ISO8583(getApplicationContext());
        try {
            iso8583.loadXmlFile(CUPS8583);
        } catch (Exception e) {
            e.printStackTrace();
        }
        iso8583.initPack();
        try {
            iso8583.unpack(respData);
            String rpc = iso8583.getField(39);
            SecurityLayer.Log("rpc", rpc);
            if (rpc.equals("00")) {
                SharedPreferences pref = PreferenceManager
                        .getDefaultSharedPreferences(getApplicationContext());
                tmk_tpk_combined = iso8583.getField(120);
                SecurityLayer.Log("tmk_tpk_combined", "tmk_tpk_combined >> " + tmk_tpk_combined);
                Load_Keys_KE();
                startActivity(new Intent(NL_CardWdlInqCard.this, NL_CardWdlTransAmountActivity.class));
            } else {
                SecurityLayer.Log("keyc res", iso8583.getField(111));
                setDialog("Failed", "Response Code: " + rpc + "\n" + iso8583.getField(111));
            }
        } catch (Exception e) {
            SecurityLayer.Log("iso", "initunPack error");
            e.printStackTrace();
            return;
        }
    }

    private void Split_Fld_120() throws RemoteException {
        String whole_120 = fld_120;
        SecurityLayer.Log("whole_120", whole_120);
        String[] firstsplit = whole_120.split(Pattern.quote("^"));
        String user_pass = firstsplit[0];
        String[] user_pass_arr = user_pass.split(Pattern.quote(","));
        String user_only = user_pass_arr[0];
        String[] secondsplit = firstsplit[1].split(Pattern.quote("@"));
        String terminal_details = secondsplit[0];
        String[] terminal_details_arr = terminal_details.split(Pattern.quote("#"));
        String tid = terminal_details_arr[1];
        String mid = terminal_details_arr[2];
        String name = terminal_details_arr[3];
        String loc = terminal_details_arr[4];
        loc = loc.replaceAll("\\s+", "");
        String ccode = terminal_details_arr[5];
        String admin_pass = secondsplit[1];
        String[] thirdsplit = secondsplit[2].split(Pattern.quote("|"));
        String tmk = thirdsplit[1];
        String tpk = thirdsplit[2];
        tmk_tpk_combined = tmk + "|" + tpk;

        String fld_43_name = String.format("%-25s", name).replace(' ', ' ');
        String fld_43_loc = String.format("%-13s", loc).replace(' ', ' ');
        fld_43 = fld_43_name + "" + fld_43_loc + "" + ccode;

        SecurityLayer.Log("arrays", user_pass + " ----- " + terminal_details + " ----- " + admin_pass + " ----- " + tmk + " ----- " + tpk);
        SecurityLayer.Log("tmk_tpk_combined", tmk_tpk_combined);
        SecurityLayer.Log("user", user_only);
        SecurityLayer.Log("tid", tid);
        SecurityLayer.Log("mid", mid);
        SecurityLayer.Log("name", name);
        SecurityLayer.Log("loc", loc);
        SecurityLayer.Log("ccode", ccode);

        String user_id = Utility.gettUtilUserId(getApplicationContext());
        boolean isContains = whole_120.contains(user_id);
        SecurityLayer.Log("user_id", user_id + " = " + isContains);
        //if (user_id.equals(user_only)) {
        if(isContains){
            SharedPreferences pref = PreferenceManager
                    .getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("user_only", user_only);
            editor.putString("tid", tid);
            editor.putString("mid", mid);
            editor.putString("mer_name", name);
            editor.putString("loc", loc);
            editor.putString("ccode", ccode);
            editor.putString("fld_43", fld_43);
            editor.putString("tmk", tmk);
            editor.putString("tpk", tpk);
            editor.commit();
            Load_Keys_KE();
            startActivity(new Intent(NL_CardWdlInqCard.this, NL_CardWdlTransAmountActivity.class));
        } else {
            setDialog("Error", "Please note that this terminal is not assigned to this customer attendant ID.");
        }
    }

    public void Load_Keys_KE() throws RemoteException {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat sdate_df = new SimpleDateFormat("yyyy-MM-dd");
        curr_date = sdate_df.format(c);

        String whole_120 = tmk_tpk_combined;
        String[] firstsplit = whole_120.split(Pattern.quote("|"));
        enc_tmk = firstsplit[0];
        enc_tpk = firstsplit[1];
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = pref.edit();
        //editor.putString("etmk", enc_tmk);
        //editor.putString("etpk", enc_tpk);
        editor.putString("last_kchange", curr_date);
        editor.commit();

        SecurityLayer.Log("enc_tmk", "enc_tmk >> " + enc_tmk);
        SecurityLayer.Log("enc_tpk", "enc_tpk >> " + enc_tpk);
        //iRet = Load_TLK(mSDKManager);
        //SecurityLayer.Log("Load_TLK", "Load_TLK >> " + (iRet == ServiceResult.Success ? getString(R.string.msg_succ) : getString(R.string.msg_fail)));
        Load_Other_Keys(mSDKManager);
    }

    public void Load_Other_Keys(DeviceServiceEngine engine) throws RemoteException {
        try {
            clr_tdk = mfdes.decrypt(TLK, TDK);
            SecurityLayer.Log("decrypt clr_tdk", "clr_tdk = " + clr_tdk);
            clr_tmk = mfdes.decrypt(TLK, enc_tmk);
            SecurityLayer.Log("decrypt clr_tmk", "clr_tmk = " + clr_tmk);
            clr_tpk = mfdes.decrypt(clr_tmk, enc_tpk);
            SecurityLayer.Log("decrypt clr_tpk", "clr_tpk = " + clr_tpk);
        } catch (Exception e) {
            e.printStackTrace();
        }
        PinPad pinPad = engine.getPinPad();
        int tdk_ret = pinPad.loadPlainWKey(4, WorkKeyType.TDKEY, string2byte(clr_tdk), 16);
        SecurityLayer.Log("loadPlainWKey", "loadPlainWKey TDK >> " + (tdk_ret == ServiceResult.Success ? "Success" : "Fail"));
        int tpk_ret = pinPad.loadPlainWKey(0, WorkKeyType.PINKEY, string2byte(clr_tpk), 16);
        SecurityLayer.Log("loadPlainWKey", "loadPlainWKey TDK >> " + (tpk_ret == ServiceResult.Success ? "Success" : "Fail"));
    }

    public void setDialog(String title, String message) {
        new MaterialDialog.Builder(NL_CardWdlInqCard.this)
                .title(title)
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

    public void SetPop() {
        planetsList.clear();
        //planetsList.add(new OTBList("Access Bank Card", "057"));
        planetsList.add(new OTBList("Card Withdrawal", "058"));
        planetsList.add(new OTBList("Cash Withdrawal By Account", "059"));
        aAdpt = new DepoMenuAdapt(planetsList, this);
        lv.setAdapter(aAdpt);
    }
}
