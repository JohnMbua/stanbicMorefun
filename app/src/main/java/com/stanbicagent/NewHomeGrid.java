package com.stanbicagent;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.RemoteException;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.stanbicagent.R;
import com.afollestad.materialdialogs.MaterialDialog;
import com.morefun.yapi.ServiceResult;
import com.morefun.yapi.device.pinpad.DesAlgorithmType;
import com.morefun.yapi.device.pinpad.PinPad;
import com.morefun.yapi.device.pinpad.WorkKeyType;
import com.morefun.yapi.engine.DeviceInfoConstrants;
import com.morefun.yapi.engine.DeviceServiceEngine;
import com.newland.mtype.util.Dump;
import com.newland.mtype.util.ISOUtils;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
import publib.LoggerUtils;
import rest.ApiInterface;
import rest.ApiSecurityClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import security.SecurityLayer;
import utils.mfdes;

import static com.stanbicagent.ApplicationConstants.CHANNEL_ID;
import static com.stanbicagent.ApplicationConstants.CUPS8583;
import static com.stanbicagent.ApplicationConstants.FTP_IP;
import static com.stanbicagent.ApplicationConstants.FTP_PASSWORD;
import static com.stanbicagent.ApplicationConstants.FTP_USERNAME;
import static com.stanbicagent.ApplicationConstants.SWITCH_IP;
import static com.stanbicagent.ApplicationConstants.SWITCH_PORT;
import static com.stanbicagent.ApplicationConstants.TDK;
import static com.stanbicagent.ApplicationConstants.TLK;
import static com.stanbicagent.SessionManagement.KEY_ALL_USERS;
import static publib.Utils.byte2string;
import static publib.Utils.string2byte;

public class NewHomeGrid extends Fragment implements View.OnClickListener {
    ImageView imageView1;
    TextView txttogrid;
    RelativeLayout rldepo, rlwithdrw, rltransf, rlpybill, rlairtime, rlopenacc, rlagac, rlcommac, rlopenaccinside;
    Button rlagbal, rlcomm;
    SessionManagement session;
    ProgressDialog pro, prgDialog;
    Toolbar mToolbar;
    int sel_type, check, iRet = 0;
    String tid, mid, mer_name, serial_no, requst, fld_120, fld_43, enc_tmk, enc_tpk, last_key_change, curr_date, tmk_tpk_combined, status, rpc,
            appvers, clr_tdk, clr_tmk, clr_tpk = "";
    ISO8583 iso8583;
    byte[] reqData = null;
    byte[] respData = null;
    DeviceServiceEngine mSDKManager;

    public NewHomeGrid() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                SecurityLayer.Log("JELLY_BEAN_MR2", "JELLY_BEAN_MR2");
                builder.detectFileUriExposure();
            }
        }
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.newvershomegrid, null);
        session = new SessionManagement(getActivity());
        mToolbar = (Toolbar) root.findViewById(R.id.toolbar);

        SDKManager.getInstance().bindService(getActivity());
        appvers = Utility.getAppVersion(getActivity());
        prgDialog = new ProgressDialog(getActivity());
        prgDialog.setCancelable(false);
        pro = new ProgressDialog(getActivity());
        pro.setMessage("Loading...");
        pro.setTitle("");
        pro.setCancelable(false);
        rlagbal = (Button) root.findViewById(R.id.agacc);
        rlcomm = (Button) root.findViewById(R.id.comacc);
        rlairtime = (RelativeLayout) root.findViewById(R.id.rl1);
        rldepo = (RelativeLayout) root.findViewById(R.id.rl5);
        rltransf = (RelativeLayout) root.findViewById(R.id.rl2);
        rlpybill = (RelativeLayout) root.findViewById(R.id.rl6);
        rlopenacc = (RelativeLayout) root.findViewById(R.id.rl4);
        rlwithdrw = (RelativeLayout) root.findViewById(R.id.rl3);
        rlopenaccinside = (RelativeLayout) root.findViewById(R.id.rlopenaccinside);
        rlagbal.setOnClickListener(this);
        rlcomm.setOnClickListener(this);
        rlairtime.setOnClickListener(this);
        rltransf.setOnClickListener(this);
        rlpybill.setOnClickListener(this);
        rlopenaccinside.setOnClickListener(this);
        rlwithdrw.setOnClickListener(this);
        rldepo.setOnClickListener(this);

        rlagac = (RelativeLayout) root.findViewById(R.id.rlagac);
        rlcommac = (RelativeLayout) root.findViewById(R.id.rlcommwal);
        rlagac.setOnClickListener(this);
        rlcommac.setOnClickListener(this);
        boolean checktpref = session.checkShwBal();
        SecurityLayer.Log("Boolean checkpref", String.valueOf(checktpref));

        String cntopen = session.getString(SessionManagement.KEY_SETCNTOPEN);
        if (Utility.isNotNull(cntopen)) {
            Log.v("Security Can Open", cntopen);
        }
        if (checktpref == false) {
            if (session.getString(SessionManagement.KEY_SETBANKS).equals("N")) {
                //  GetServv();
            }
        } else {
        }
        ((FMobActivity) getActivity()).getSupportActionBar().setCustomView(R.layout.newtoolbar);
        checkAppvers();
        String chkappvs = session.getString("APPVERSBOOL");
        SecurityLayer.Log("chkappvs", chkappvs);
        if (chkappvs.equals("Y")) {
            GetAppversion();
        }
        return root;
    }

    public void StartChartAct(int i) {

    }

    @Override
    public void onClick(View view) {
        mSDKManager = SDKManager.getInstance().getDeviceServiceEngine();
        if (mSDKManager == null) {
            SecurityLayer.Log("MF_SDKManager", "ServiceEngine is Null");
            return;
        }

        if (view.getId() == R.id.rl2) {
            sel_type = 1;
            WDL_Check_Term_Status();
        }
        if (view.getId() == R.id.rl3) {
            sel_type = 2;
            WDL_Check_Term_Status();
            //startActivity(new Intent(getActivity(), WithdrawActivity.class));
        }
        if (view.getId() == R.id.rl6) {
            sel_type = 3;
            WDL_Check_Term_Status();
            //startActivity(new Intent(getActivity(), BillMenuActivity.class));
        }
        if (view.getId() == R.id.rl1) {
            sel_type = 4;
            WDL_Check_Term_Status();
            //startActivity(new Intent(getActivity(), AirtimeTransfActivity.class));
        }
        if (view.getId() == R.id.rl5) {
            sel_type = 5;
            WDL_Check_Term_Status();
            //startActivity(new Intent(getActivity(), CashDepoActivity.class));
        }
        if (view.getId() == R.id.rlopenaccinside) {
            sel_type = 6;
            WDL_Check_Term_Status();
            //startActivity(new Intent(getActivity(), OpenAccActivity.class));
        }
        if (view.getId() == R.id.rlagac) {
            ((FMobActivity) getActivity()).showEditDialog("MINIST");
        }
        if (view.getId() == R.id.rlcommwal) {
            ((FMobActivity) getActivity()).showEditDialog("COMM");
        }
    }

    private void WDL_Check_Term_Status() {
        Check_Printer();
    }

    public void Check_Printer() {
        try {
            com.morefun.yapi.device.printer.Printer printer = mSDKManager.getPrinter();
            if (printer.getStatus() != 0){
                check = -1;
            }else{
                check = 0;
            }
            After_Printer_Check();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void After_Printer_Check() {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        tid = pref.getString("tid", "");
        mid = pref.getString("mid", "");
        mer_name = pref.getString("mer_name", "");
        fld_43 = pref.getString("fld_43", "");
        if (check == 0) {
            if (Utility.checkInternetConnection(getActivity())) {
                if (tid.equals("") && mid.equals("") && mer_name.equals("") && fld_43.equals("")) {
                    Send_User_Download_Request user_download_client = new Send_User_Download_Request(SWITCH_IP, SWITCH_PORT);
                    user_download_client.execute();
                } else {
                    if (sel_type == 1) {
                        startActivity(new Intent(getActivity(),FTMenuActivity.class));// CashDepoTransActivity.class)); //todo John Change FTMenuActivity.class));
                    } else if (sel_type == 2) {
                        startActivity(new Intent(getActivity(), NL_WDLMenuActivity.class));
                    } else if (sel_type == 3) {
                        startActivity(new Intent(getActivity(), BillMenuActivity.class));
                    } else if (sel_type == 4) {
                        startActivity(new Intent(getActivity(), AirtimeTransfActivity.class));
                    } else if (sel_type == 5) {
                        startActivity(new Intent(getActivity(), CashDepoActivity.class));
                    } else if (sel_type == 6) {
                        startActivity(new Intent(getActivity(), OpenAccActivity.class));
                    }
                }
            }
        } else {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setDialog("Please insert paper receipt before transacting.");
                }
            });
        }
    }

    public static void CalTrack(DeviceServiceEngine engine) throws RemoteException {
        final byte[] block = new byte[8];
        byte[] tdKeyBytes = string2byte("12344FFFFFFFFFFF");
        final int ret = engine.getPinPad().desEncByWKey(4, WorkKeyType.TDKEY, tdKeyBytes, tdKeyBytes.length, DesAlgorithmType.TDES, block);
        if (ret == ServiceResult.Success) {
            SecurityLayer.Log("CalTrack", "CalTrack >> " + byte2string(block));
        } else {
            SecurityLayer.Log("CalTrack", "CalTrack >> FAILED!!");
        }
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
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    prgDialog.setMessage("Downloading terminal parameters....");
                    prgDialog.show();
                }
            });
            Set_User_Download_Data();
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
                System.out.println(Dump.getHexDump(reqLenBs) + "," + Dump.getHexDump(reqData));
                bos.write(reqData);
                bos.flush();

                bis = new BufferedInputStream(sslSocket.getInputStream());
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
                    Log.v("socket", "user download socket is null");
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
                SecurityLayer.Log("response", "user download response == " + response);
                setDialog("Error", response);
            }
        }
    }

    public void Set_User_Download_Data() {
        Bundle devInfo = null;
        try {
            devInfo = mSDKManager.getDevInfo();
            serial_no = devInfo.getString(DeviceInfoConstrants.COMMOM_SN);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("MMddhhmmss");
        SimpleDateFormat df_1 = new SimpleDateFormat("hhmmss");
        String formattedDate = df.format(c);
        String formattedDate_stan = df_1.format(c);
        //serial_no = "N7NL00479115";
        iso8583 = new ISO8583(getActivity());
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
        SecurityLayer.Log("user download req iso", requst);
        reqData = BytesUtils.hexToBytes(requst);
    }

    public void Unpack_User_Download_Data() {
        iso8583 = new ISO8583(getActivity());
        try {
            iso8583.loadXmlFile(CUPS8583);
        } catch (Exception e) {
            e.printStackTrace();
        }
        iso8583.initPack();
        try {
            iso8583.unpack(respData);
            rpc = iso8583.getField(39);
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

        String user_id = Utility.gettUtilUserId(getActivity());
        boolean isContains = whole_120.contains(user_id);
        SecurityLayer.Log("user_id", user_id + " = " + isContains);
        //if (user_id.equals(user_only)) {
        if(isContains){
            session.setString(KEY_ALL_USERS, user_pass);
            SharedPreferences pref = PreferenceManager
                    .getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("user_only", user_only);
            editor.putString("tid", tid);
            editor.putString("mid", mid);
            editor.putString("mer_name", name);
            editor.putString("loc", loc);
            editor.putString("ccode", ccode);
            editor.putString("fld_43", fld_43);
            //editor.putString("tmk", tmk);
            //editor.putString("tpk", tpk);
            editor.commit();
            session.setString("agmobno", "00" + tid);
            Load_Keys_KE();
            if (sel_type == 1) {
                startActivity(new Intent(getActivity(), FTMenuActivity.class));
            } else if (sel_type == 2) {
                startActivity(new Intent(getActivity(), NL_WDLMenuActivity.class));
            } else if (sel_type == 3) {
                startActivity(new Intent(getActivity(), BillMenuActivity.class));
            } else if (sel_type == 4) {
                startActivity(new Intent(getActivity(), AirtimeTransfActivity.class));
            } else if (sel_type == 5) {
                startActivity(new Intent(getActivity(), CashDepoActivity.class));
            } else if (sel_type == 6) {
                startActivity(new Intent(getActivity(), OpenAccActivity.class));
            }
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
                .getDefaultSharedPreferences(getActivity());
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

    /*public static int Load_TLK(DeviceServiceEngine engine) throws RemoteException {
        PinPad pinPad = engine.getPinPad();
        pinPad.format();
        return pinPad.loadPlainMKey(0, string2byte(TLK), 16, true);
    }*/

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
        CalTrack(mSDKManager);
    }

    private void showDialog() {
        final CharSequence[] items = {"BVN", "NO BVN",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("BVN")) {
                    startActivity(new Intent(getActivity(), OpenAccBVN.class));
                    session.setString("ISBVN", "Y");
                } else if (items[item].equals("NO BVN")) {
                    startActivity(new Intent(getActivity(), OpenAccActivity.class));
                    session.setString("ISBVN", "N");
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public void swithgrid() {
        Fragment fragment = new HomeAccountFragNewUI();
        String title = "New Grid";

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_down, R.anim.slide_out_down);
        //  String tag = Integer.toString(title);
        fragmentTransaction.replace(R.id.container_body, fragment, title);
        fragmentTransaction.addToBackStack(title);
        fragmentTransaction.commit();

        // ((FMobActivity)getActivity()).addFragment(fragment,title);
        //   getActivity().overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_up );
    }

    private void GetServv() {
        //  prgDialog2.show();
        String endpoint = "transfer/getbanks.action";

        if (!(getActivity() == null)) {
            String usid = Utility.gettUtilUserId(getActivity());
            String agentid = Utility.gettUtilAgentId(getActivity());


            String params = usid + "/" + agentid + "/93939393";

            String urlparams = "";
            try {
                urlparams = SecurityLayer.genURLCBC(params, endpoint, getActivity());
                //SecurityLayer.Log("cbcurl",url);
                SecurityLayer.Log("RefURL", urlparams);
                SecurityLayer.Log("refurl", urlparams);
                SecurityLayer.Log("params", params);
            } catch (Exception e) {
                SecurityLayer.Log("encryptionerror", e.toString());
            }


            ApiInterface apiService =
                    ApiSecurityClient.getClient(getActivity()).create(ApiInterface.class);


            Call<String> call = apiService.setGenericRequestRaw(urlparams);

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {
                        // JSON Object
                        SecurityLayer.Log("Get Banks Resp", response.body());
                        SecurityLayer.Log("response..:", response.body());
                        JSONObject obj = new JSONObject(response.body());
                        //obj = Utility.onresp(obj,getActivity());
                        obj = SecurityLayer.decryptTransaction(obj, getActivity());
                        SecurityLayer.Log("decrypted_response", obj.toString());
                        JSONArray servdata = obj.optJSONArray("data");
                        String strservdata = servdata.toString();
                        //session.setString(SecurityLayer.KEY_APP_ID,appid);
                        if (!(response.body() == null)) {
                            String respcode = obj.optString("responseCode");
                            String responsemessage = obj.optString("message");
                            SecurityLayer.Log("Response Message", responsemessage);
                            if (Utility.isNotNull(respcode) && Utility.isNotNull(respcode)) {
                                if (!(Utility.checkUserLocked(respcode))) {
                                    SecurityLayer.Log("Response Message", responsemessage);
                                    if (respcode.equals("00")) {
                                        SecurityLayer.Log("JSON Aray", servdata.toString());
                                        session.setString(SessionManagement.KEY_SETBANKS, "Y");
                                        session.setString(SessionManagement.KEY_BANKS, strservdata);
                                    } else {
                                        Toast.makeText(
                                                getActivity(),
                                                "" + responsemessage,
                                                Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    getActivity().finish();
                                    startActivity(new Intent(getActivity(), SignInActivity.class));
                                    Toast.makeText(
                                            getActivity(),
                                            "You have been locked out of the app.Please call customer care for further details",
                                            Toast.LENGTH_LONG).show();

                                }
                            } else {
                                Toast.makeText(
                                        getActivity(),
                                        "There was an error on your request",
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(
                                    getActivity(),
                                    "There was an error on your request",
                                    Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        SecurityLayer.Log("encryptionJSONException", e.toString());
                        // TODO Auto-generated catch block
                        Toast.makeText(getActivity(), getActivity().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                        // SecurityLayer.Log(e.toString());

                    } catch (Exception e) {
                        SecurityLayer.Log("encryptionJSONException", e.toString());
                        // SecurityLayer.Log(e.toString());
                    }
                    //    prgDialog2.dismiss();
                    if (session.getString(SessionManagement.KEY_SETBILLERS).equals("N")) {
                        if (!(getActivity() == null)) {
                            GetBillPay();
                        }
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    // Log error here since request failed
                    // Log error here since request failed
                    SecurityLayer.Log("throwable error", t.toString());


                    Toast.makeText(
                            getActivity(),
                            "There was an error on your request",
                            Toast.LENGTH_LONG).show();


                    //   prgDialog2.dismiss();
                    if (session.getString(SessionManagement.KEY_SETBILLERS).equals("N")) {
                        GetBillPay();
                    }
                }
            });
        }
    }

    private void GetBillPay() {
        //   prgDialog2.show();
        String endpoint = "billpayment/services.action";
        if (!(getActivity() == null)) {
            String usid = Utility.gettUtilUserId(getActivity());
            String agentid = Utility.gettUtilAgentId(getActivity());
            String mobnoo = Utility.gettUtilMobno(getActivity());
            String params = CHANNEL_ID + usid + "/" + agentid + "/9493818389";


            String urlparams = "";
            try {
                urlparams = SecurityLayer.genURLCBC(params, endpoint, getActivity());
                //SecurityLayer.Log("cbcurl",url);
                SecurityLayer.Log("RefURL", urlparams);
                SecurityLayer.Log("refurl", urlparams);
                SecurityLayer.Log("params", params);
            } catch (Exception e) {
                SecurityLayer.Log("encryptionerror", e.toString());
            }


            ApiInterface apiService =
                    ApiSecurityClient.getClient(getActivity()).create(ApiInterface.class);


            Call<String> call = apiService.setGenericRequestRaw(urlparams);

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {
                        // JSON Object


                        SecurityLayer.Log("Cable TV Resp", response.body());
                        SecurityLayer.Log("response..:", response.body());
                        JSONObject obj = new JSONObject(response.body());
                        //obj = Utility.onresp(obj,getActivity());
                        obj = SecurityLayer.decryptTransaction(obj, getActivity());
                        SecurityLayer.Log("decrypted_response", obj.toString());


                        JSONArray servdata = obj.optJSONArray("data");
                        //session.setString(SecurityLayer.KEY_APP_ID,appid);

                        if (!(response.body() == null)) {
                            String respcode = obj.optString("responseCode");
                            String responsemessage = obj.optString("message");

                            SecurityLayer.Log("Response Message", responsemessage);

                            if (Utility.isNotNull(respcode) && Utility.isNotNull(respcode)) {
                                if (!(Utility.checkUserLocked(respcode))) {
                                    SecurityLayer.Log("Response Message", responsemessage);

                                    if (respcode.equals("00")) {
                                        SecurityLayer.Log("JSON Aray", servdata.toString());
                                        String billerdata = servdata.toString();
                                        session.setString(SessionManagement.KEY_SETBILLERS, "Y");
                                        session.setString(SessionManagement.KEY_BILLERS, billerdata);
                                    } else {
                                        Toast.makeText(
                                                getActivity(),
                                                "" + responsemessage,
                                                Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    getActivity().finish();
                                    startActivity(new Intent(getActivity(), SignInActivity.class));
                                    Toast.makeText(
                                            getActivity(),
                                            "You have been locked out of the app.Please call customer care for further details",
                                            Toast.LENGTH_LONG).show();

                                }
                            } else {

                                Toast.makeText(
                                        getActivity(),
                                        "There was an error on your request",
                                        Toast.LENGTH_LONG).show();


                            }
                        } else {

                            Toast.makeText(
                                    getActivity(),
                                    "There was an error on your request",
                                    Toast.LENGTH_LONG).show();


                        }
                        // prgDialog2.dismiss();


                    } catch (JSONException e) {
                        SecurityLayer.Log("encryptionJSONException", e.toString());
                        // TODO Auto-generated catch block
                        Toast.makeText(getActivity(), getActivity().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                        // SecurityLayer.Log(e.toString());

                    } catch (Exception e) {
                        SecurityLayer.Log("encryptionJSONException", e.toString());
                        // SecurityLayer.Log(e.toString());
                    }
                    if (!(getActivity() == null)) {
                        if (session.getString(SessionManagement.KEY_SETWALLETS).equals("N")) {
                            GetWallets();
                        }

                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    // Log error here since request failed
                    // Log error here since request failed
                    SecurityLayer.Log("throwable error", t.toString());


                    Toast.makeText(
                            getActivity(),
                            "There was an error on your request",
                            Toast.LENGTH_LONG).show();

                    if (!(getActivity() == null)) {
                        if (session.getString(SessionManagement.KEY_SETWALLETS).equals("N")) {
                            GetWallets();
                        }

                    }
                }
            });
        }
    }

    private void PopulateAirtime() {
        //  planetsList.clear();
        String endpoint = "billpayment/MMObillers.action";

        //  prgDialog.show();
        if (!(getActivity() == null)) {
            String usid = Utility.gettUtilUserId(getActivity());
            String agentid = Utility.gettUtilAgentId(getActivity());
            String mobnoo = Utility.gettUtilMobno(getActivity());
            String params = CHANNEL_ID + usid + "/" + agentid + "/" + mobnoo + "/1";
            String urlparams = "";
            try {
                urlparams = SecurityLayer.genURLCBC(params, endpoint, getActivity());
                //SecurityLayer.Log("cbcurl",url);
                SecurityLayer.Log("RefURL", urlparams);
                SecurityLayer.Log("refurl", urlparams);
                SecurityLayer.Log("params", params);
            } catch (Exception e) {
                SecurityLayer.Log("encryptionerror", e.toString());
            }


            ApiInterface apiService =
                    ApiSecurityClient.getClient(getActivity()).create(ApiInterface.class);


            Call<String> call = apiService.setGenericRequestRaw(urlparams);

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {
                        // JSON Object

                        SecurityLayer.Log("response..:", response.body());
                        JSONObject obj = new JSONObject(response.body());
                        //obj = Utility.onresp(obj,getActivity());
                        obj = SecurityLayer.decryptTransaction(obj, getActivity());
                        SecurityLayer.Log("decrypted_response", obj.toString());

                        String respcode = obj.optString("responseCode");
                        String responsemessage = obj.optString("message");


                        JSONArray plan = obj.optJSONArray("data");
                        //session.setString(SecurityLayer.KEY_APP_ID,appid);


                        if (!(response.body() == null)) {
                            if (respcode.equals("00")) {

                                SecurityLayer.Log("Response Message", responsemessage);
                                SecurityLayer.Log("JSON Aray", plan.toString());
                                String billerdata = plan.toString();
                                session.setString(SessionManagement.KEY_SETAIRTIME, "Y");
                                session.setString(SessionManagement.KEY_AIRTIME, billerdata);


                            } else {
                                Toast.makeText(
                                        getActivity(),
                                        responsemessage,
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(
                                    getActivity(),
                                    "There was an error processing your request ",
                                    Toast.LENGTH_LONG).show();
                        }


                    } catch (JSONException e) {
                        SecurityLayer.Log("encryptionJSONException", e.toString());

                        // TODO Auto-generated catch block
                        Toast.makeText(getActivity(), getActivity().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                        // SecurityLayer.Log(e.toString());

                    } catch (Exception e) {

                        SecurityLayer.Log("encryptionJSONException", e.toString());
                        // SecurityLayer.Log(e.toString());
                    }
                    //        prgDialog.dismiss();

                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    // Log error here since request failed
                    SecurityLayer.Log("Throwable error", t.toString());

                    Toast.makeText(
                            getActivity(),
                            "There was an error processing your request",
                            Toast.LENGTH_LONG).show();
                    //   prgDialog.dismiss();

                }
            });
        }
    }

    private void GetWallets() {
        //   prgDialog2.show();
        String endpoint = "transfer/getwallets.action";
        String usid = Utility.gettUtilUserId(getActivity());
        String agentid = Utility.gettUtilAgentId(getActivity());
        String params = usid + "/" + agentid + "/93939393";
        String urlparams = "";
        try {
            urlparams = SecurityLayer.genURLCBC(params, endpoint, getActivity());
            //SecurityLayer.Log("cbcurl",url);
            SecurityLayer.Log("RefURL", urlparams);
            SecurityLayer.Log("refurl", urlparams);
            SecurityLayer.Log("params", params);
        } catch (Exception e) {
            SecurityLayer.Log("encryptionerror", e.toString());
        }

        ApiInterface apiService =
                ApiSecurityClient.getClient(getActivity()).create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(urlparams);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    SecurityLayer.Log("Get Wallets Resp", response.body());
                    SecurityLayer.Log("response..:", response.body());
                    JSONObject obj = new JSONObject(response.body());
                    //obj = Utility.onresp(obj,getActivity());
                    obj = SecurityLayer.decryptTransaction(obj, getActivity());
                    SecurityLayer.Log("decrypted_response", obj.toString());
                    JSONArray servdata = obj.optJSONArray("data");
                    //session.setString(SecurityLayer.KEY_APP_ID,appid);
                    if (!(response.body() == null)) {
                        String respcode = obj.optString("responseCode");
                        String responsemessage = obj.optString("message");
                        SecurityLayer.Log("Response Message", responsemessage);
                        if (Utility.isNotNull(respcode) && Utility.isNotNull(respcode)) {
                            if (!(Utility.checkUserLocked(respcode))) {
                                SecurityLayer.Log("Response Message", responsemessage);

                                if (respcode.equals("00")) {
                                    SecurityLayer.Log("JSON Aray", servdata.toString());
                                    if (servdata.length() > 0) {

                                        SecurityLayer.Log("JSON Aray", servdata.toString());
                                        String billerdata = servdata.toString();
                                        session.setString(SessionManagement.KEY_SETWALLETS, "Y");
                                        session.setString(SessionManagement.KEY_WALLETS, billerdata);

                                    } else {
                                        Toast.makeText(
                                                getActivity(),
                                                "No services available  ",
                                                Toast.LENGTH_LONG).show();
                                    }

                                } else {
                                    Toast.makeText(
                                            getActivity(),
                                            "" + responsemessage,
                                            Toast.LENGTH_LONG).show();
                                }
                            } else {
                                getActivity().finish();
                                startActivity(new Intent(getActivity(), SignInActivity.class));
                                Toast.makeText(
                                        getActivity(),
                                        "You have been locked out of the app.Please call customer care for further details",
                                        Toast.LENGTH_LONG).show();

                            }
                        } else {

                            Toast.makeText(
                                    getActivity(),
                                    "There was an error on your request",
                                    Toast.LENGTH_LONG).show();


                        }
                    } else {

                        Toast.makeText(
                                getActivity(),
                                "There was an error on your request",
                                Toast.LENGTH_LONG).show();


                    }
                    // prgDialog2.dismiss();


                } catch (JSONException e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    // TODO Auto-generated catch block
                    Toast.makeText(getActivity(), getActivity().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                    // SecurityLayer.Log(e.toString());

                } catch (Exception e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    // SecurityLayer.Log(e.toString());
                }
                if (!(getActivity() == null)) {
                    if (session.getString(SessionManagement.KEY_SETAIRTIME).equals("N")) {
                        PopulateAirtime();
                    }

                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                // Log error here since request failed
                SecurityLayer.Log("throwable error", t.toString());


                Toast.makeText(
                        getActivity(),
                        "There was an error on your request",
                        Toast.LENGTH_LONG).show();


                if (!(getActivity() == null)) {
                    if (session.getString(SessionManagement.KEY_SETAIRTIME).equals("N")) {
                        PopulateAirtime();
                    }

                }

            }
        });

    }

    private void LogRetro(String params, final String service) {
        pro.show();
        String endpoint = "login/login.action/";
        String urlparams = "";
        try {
            urlparams = SecurityLayer.generalLogin(params, "23322", getActivity(), endpoint);
            //SecurityLayer.Log("cbcurl",url);
            SecurityLayer.Log("RefURL", urlparams);
            SecurityLayer.Log("refurl", urlparams);
            SecurityLayer.Log("params", params);
        } catch (Exception e) {
            SecurityLayer.Log("encryptionerror", e.toString());
        }

        ApiInterface apiService =
                ApiSecurityClient.getClient(getActivity()).create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(urlparams);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    // JSON Object
                    SecurityLayer.Log("response..:", response.body());
                    JSONObject obj = new JSONObject(response.body());
                 /*   JSONObject jsdatarsp = obj.optJSONObject("data");
                    SecurityLayer.Log("JSdata resp", jsdatarsp.toString());
                    //obj = Utility.onresp(obj,getActivity()); */
                    obj = SecurityLayer.decryptGeneralLogin(obj, getActivity());
                    SecurityLayer.Log("decrypted_response", obj.toString());
                    String respcode = obj.optString("responseCode");
                    String responsemessage = obj.optString("message");
                    JSONObject datas = obj.optJSONObject("data");
                    //session.setString(SecurityLayer.KEY_APP_ID,appid);
                    if (Utility.isNotNull(respcode) && Utility.isNotNull(responsemessage)) {
                        SecurityLayer.Log("Response Message", responsemessage);

                        if (respcode.equals("00")) {
                            if (!(datas == null)) {
                                if (service.equals("MINIST")) {
                                    android.app.Fragment fragment = new Minstat();
                                    String title = "Mini Statement";
                                    ((FMobActivity) getActivity()).addAppFragment(fragment, title);
                                }
                                if (service.equals("COMM")) {
                                    android.app.Fragment fragment = new CommReport();


                                    String title = "Commissions Report";
                                    ((FMobActivity) getActivity()).addAppFragment(fragment, title);
                                }
                            }
                        } else {

                            Toast.makeText(
                                    getActivity(),
                                    responsemessage,
                                    Toast.LENGTH_LONG).show();


                        }

                    } else {

                        Toast.makeText(
                                getActivity(),
                                "There was an error on your request",
                                Toast.LENGTH_LONG).show();


                    }

                } catch (JSONException e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    // TODO Auto-generated catch block
                    Toast.makeText(getActivity(), getActivity().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                    // SecurityLayer.Log(e.toString());

                } catch (Exception e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    // SecurityLayer.Log(e.toString());
                }
                if ((pro != null) && pro.isShowing() && !(getActivity() == null)) {
                    pro.dismiss();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                SecurityLayer.Log("Throwable error", t.toString());
                Toast.makeText(
                        getActivity(),
                        "There was an error processing your request",
                        Toast.LENGTH_LONG).show();
                if ((pro != null) && pro.isShowing() && !(getActivity() == null)) {
                    pro.dismiss();
                }

            }
        });

    }

    private void GetAppversion() {
        pro.show();
        String endpoint = "reg/appVersion.action";
        if (!(getActivity() == null)) {
            String usid = Utility.gettUtilUserId(getActivity());
            String appid = Utility.getPlainAppid(getActivity());
            String appvers = Utility.getAppVersion(getActivity());
            String params = CHANNEL_ID + usid + "/" + appid + "/" + appvers;
            SecurityLayer.Log("params", params);
            String urlparams = "";
            try {
                urlparams = SecurityLayer.genURLCBC(params, endpoint, getActivity());
                SecurityLayer.Log("RefURL", urlparams);
                SecurityLayer.Log("refurl", urlparams);
                SecurityLayer.Log("params", params);
            } catch (Exception e) {
                SecurityLayer.Log("encryptionerror", e.toString());
            }
            ApiInterface apiService =
                    ApiSecurityClient.getClient(getActivity()).create(ApiInterface.class);
            Call<String> call = apiService.setGenericRequestRaw(urlparams);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {
                        long secs = (new Date().getTime()) / 1000;
                        session.setLong("CHECKTIME", secs);
                        SecurityLayer.Log("Cable TV Resp", response.body());
                        SecurityLayer.Log("response..:", response.body());
                        JSONObject obj = new JSONObject(response.body());
                        obj = SecurityLayer.decryptTransaction(obj, getActivity());
                        SecurityLayer.Log("decrypted_response", obj.toString());
                        JSONObject servdata = obj.optJSONObject("data");
                        if (!(response.body() == null)) {
                            String respcode = obj.optString("responseCode");
                            String responsemessage = obj.optString("message");
                            SecurityLayer.Log("Response Message", responsemessage);
                            if (Utility.isNotNull(respcode) && Utility.isNotNull(respcode)) {
                                if (!(Utility.checkUserLocked(respcode))) {
                                    SecurityLayer.Log("Response Message", responsemessage);
                                    if (respcode.equals("00")) {
                                        pro.dismiss();
                                        String reqversion = servdata.optString("minVersion");
                                        SecurityLayer.Log("Min version", reqversion);
                                        String currvers = Utility.getAppVersion(getActivity());
                                        SecurityLayer.Log("Curr version", currvers);
                                        SecurityLayer.Log("Curr version sorted", currvers.replace(".", ""));
                                        if (Utility.compareversionsupdatenew(reqversion, currvers)) {
                                            new MaterialDialog.Builder(getActivity())
                                                    .title(getActivity().getString(R.string.appupd_verstitle))
                                                    .content(getActivity().getString(R.string.appupd_vers))
                                                    .positiveText("Upgrade")
                                                    .negativeText("Not Now")
                                                    .callback(new MaterialDialog.ButtonCallback() {
                                                        @Override
                                                        public void onPositive(MaterialDialog dialog) {
                                                            dialog.dismiss();
                                                            prgDialog = ProgressDialog.show(getActivity(), "", "Downloading New Update.Please Wait..",
                                                                    true);
                                                            Handler handler = new Handler();
                                                            handler.postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    Update_Apk();
                                                                }
                                                            }, 1000);
                                                        }
                                                        @Override
                                                        public void onNegative(MaterialDialog dialog) {
                                                            dialog.dismiss();
                                                        }
                                                    })
                                                    .show();
                                        }
                                    } else {
                                        pro.dismiss();
                                    }
                                } else {
                                    pro.dismiss();
                                    if (!(getActivity() == null)) {
                                        ((FMobActivity) getActivity()).LogOut();
                                    }
                                }
                            } else {
                            }
                        } else {
                        }

                    } catch (JSONException e) {
                        SecurityLayer.Log("encryptionJSONException", e.toString());
                        if (!(getActivity() == null)) {
                            Toast.makeText(getActivity(), getActivity().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                            ((FMobActivity) getActivity()).SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getActivity());
                        }
                    } catch (Exception e) {
                        SecurityLayer.Log("encryptionJSONException", e.toString());
                        if (!(getActivity() == null)) {
                            Toast.makeText(getActivity(), getActivity().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                            ((FMobActivity) getActivity()).SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getActivity());
                        }
                    }
                    if (!(getActivity() == null)) {
                        pro.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    SecurityLayer.Log("throwable error", t.toString());
                    long secs = (new Date().getTime()) / 1000;
                    session.setLong("CHECKTIME", secs);
                    if (!(getActivity() == null)) {
                        pro.dismiss();
                        Toast.makeText(
                                getActivity(),
                                "There was an error on your request",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    public void Update_Apk() {
        String apk_name = "accessmf.apk";
        String pathFolder = Environment.getExternalStorageDirectory() + "/access";
        String pathFile = pathFolder + "/" + apk_name;
        File localFile = new File(pathFile);
        if (localFile.exists()) {
            SecurityLayer.Log("localFile.exists", "pathFolder > " + localFile);
            localFile.delete();
        }
        File localFile_pathFile = new File(pathFolder);
        if (!localFile_pathFile.exists()) {
            SecurityLayer.Log("localFile_pathFile", "Creating folder " + pathFolder);
            localFile_pathFile.mkdir();
        }
        try {
            if (downloadAndSaveFile(FTP_IP, 21, FTP_USERNAME, FTP_PASSWORD, "ftp/" + apk_name, localFile)) {
                SecurityLayer.Log("downloadAndSaveFile", "downloadAndSaveFile successfull");
            } else {
                prgDialog.cancel();
                SecurityLayer.Log("downloadAndSaveFile", "Unable to download app update!!");
            }
        } catch (IOException e) {
            prgDialog.cancel();
            e.printStackTrace();
        }
        if (localFile.exists()) {
            int file_size = Integer.parseInt(String.valueOf(localFile.length() / 1024));
            SecurityLayer.Log("ftp download", "ftp download successful = " + localFile + " Size = " + file_size);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(localFile), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this flag android returned a intent error!
            startActivity(intent);
            prgDialog.cancel();
        }
    }

    private Boolean downloadAndSaveFile(String server, int portNumber,
                                        String user, String password, String filename, File localFile)
            throws IOException {
        FTPClient ftp = null;
        try {
            ftp = new FTPClient();
            ftp.connect(server, portNumber);
            SecurityLayer.Log("FTPClient", "Connected. Reply: " + ftp.getReplyString());
            ftp.login(user, password);
            SecurityLayer.Log("FTPClient", "Logged in");
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            SecurityLayer.Log("FTPClient", "Downloading..");
            ftp.enterLocalPassiveMode();
            OutputStream outputStream = null;
            boolean success = false;
            try {
                outputStream = new BufferedOutputStream(new FileOutputStream(
                        localFile));
                success = ftp.retrieveFile(filename, outputStream);
            } finally {
                if (outputStream != null) {
                    outputStream.close();
                }
            }
            return success;
        } finally {
            if (ftp != null) {
                //ftp.logout();
                //ftp.disconnect();
            }
        }
    }

    public void checkAppvers() {
        session.setString("APPVERSBOOL", "Y");
        long checkcurrtime = session.getLong("CHECKTIME");
        SecurityLayer.Log("checkcurrtime", Long.toString(checkcurrtime));
        if (checkcurrtime == 0) {
            long secs = (new Date().getTime()) / 1000;
            session.setLong("CHECKTIME", secs);
        }
        long checknewcurrtime = session.getLong("CHECKTIME");
        SecurityLayer.Log("checknewcurrtime", Long.toString(checknewcurrtime));
        long currsecs = (new Date().getTime()) / 1000;
        long diffsecs = currsecs - checknewcurrtime;
        SecurityLayer.Log("diffsecs", Long.toString(diffsecs));
        if (diffsecs > 86400) {
            session.setString("APPVERSBOOL", "Y");
        } else {
            session.setString("APPVERSBOOL", "N");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((FMobActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));

    }

    @Override
    public void onStop() {
        super.onStop();
        ((FMobActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#f5f5f5")));
    }

    public void setDialog(String title, String message) {
        new MaterialDialog.Builder(getActivity())
                .title(title)
                .content(message)
                .canceledOnTouchOutside(false)
                .negativeText("Dismiss")
                .callback(new MaterialDialog.ButtonCallback() {
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
    }

    public void setDialog(String message) {
        new MaterialDialog.Builder(getActivity())
                .title("Error")
                .content(message)
                .negativeText("Dismiss")
                .callback(new MaterialDialog.ButtonCallback() {
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
    }
}
