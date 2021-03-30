
package com.stanbicagent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import android.os.RemoteException;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.stanbicagent.BuildConfig;
import com.stanbicagent.R;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.security.ProviderInstaller;
import com.morefun.yapi.ServiceResult;
import com.morefun.yapi.device.pinpad.DesAlgorithmType;
import com.morefun.yapi.device.pinpad.PinPad;
import com.morefun.yapi.device.pinpad.WorkKeyType;
import com.morefun.yapi.engine.DeviceInfoConstrants;
import com.morefun.yapi.engine.DeviceServiceEngine;
import com.newland.mtype.util.Dump;
import com.newland.mtype.util.ISOUtils;

import org.json.JSONException;
import org.json.JSONObject;

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

import model.GetAirtimeBillersData;
import publib.BytesUtils;
import publib.ISO8583;
import publib.LoggerUtils;
import rest.ApiInterface;
import rest.ApiSecurityClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import security.SecurityLayer;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import utils.mfdes;

import static android.Manifest.permission.READ_PHONE_STATE;
import static com.stanbicagent.ApplicationConstants.CHANNEL_ID;
import static com.stanbicagent.ApplicationConstants.CUPS8583;
import static com.stanbicagent.ApplicationConstants.SWITCH_IP;
import static com.stanbicagent.ApplicationConstants.SWITCH_PORT;
import static com.stanbicagent.ApplicationConstants.TDK;
import static com.stanbicagent.ApplicationConstants.TLK;
import static com.stanbicagent.SessionManagement.KEY_ALL_USERS;
import static publib.Utils.byte2string;
import static publib.Utils.string2byte;

public class ActivateAgentBefore extends AppCompatActivity implements View.OnClickListener {
    Button btnnext;
    EditText agentid;
    ProgressDialog pDialog, prgDialog;
    SessionManagement session;
    String regId, clr_tdk, clr_tmk, clr_tpk = "";
    String encrypted, serial_no,ccid, all_users, status, requst, rpc, fld_120, tmk_tpk_combined, fld_43, curr_date, enc_tmk, enc_tpk, pack_message = "";
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    public static final String AGMOB = "agmobno";
    RelativeLayout rl_multiusers;
    LinearLayout ll_att_id;
    Spinner sp_att_id;
    ISO8583 iso8583;
    byte[] reqData = null;
    byte[] respData = null;
    byte[] result;
    byte[] data = null;
    int users_count = 0;
    List<GetAirtimeBillersData> planetsList = new ArrayList<GetAirtimeBillersData>();
    ArrayAdapter<GetAirtimeBillersData> mobadapt;
    DeviceServiceEngine mSDKManager;
    int entry = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activate_agentbefore);
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        session = new SessionManagement(getApplicationContext());
        SDKManager.getInstance().bindService(getApplicationContext());
        agentid = (EditText) findViewById(R.id.agentid);
        btnnext = (Button) findViewById(R.id.button2);
        btnnext.setOnClickListener(this);
        agentid.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading ....");
        pDialog.setCancelable(false);
        prgDialog = new ProgressDialog(this);
        prgDialog.setCancelable(false);
        updateAndroidSecurityProvider(this);
        session.SetEntryPoint("1");
        ll_att_id = (LinearLayout) findViewById(R.id.ll_att_id);
        rl_multiusers = (RelativeLayout) findViewById(R.id.rl_multiusers);
        sp_att_id = (Spinner) findViewById(R.id.sp_att_id);
        all_users = session.getString(KEY_ALL_USERS);
        if (all_users != null) {
            String[] splitString = all_users.split("\\*");
            users_count = splitString.length;
            if (users_count > 1) {
                rl_multiusers.setVisibility(View.VISIBLE);
                ll_att_id.setVisibility(View.GONE);
                Set_Users();
            } else {
                rl_multiusers.setVisibility(View.GONE);
            }
        } else {
            if (Utility.checkInternetConnection(this)) {
                entry = 1;
                insertDummyContactWrapper();
                //Perform_User_Download();
            } else {
                setDialog("Connection Error", "No Internet Connection. Please check your internet settings");
            }
        }
        ccid = ApplicationClass.get().getGlobalVals().getSimSerial();

    }

    private void Perform_User_Download() {
//        int iret_pack = Set_User_Download_Data();
//        if (iret_pack >= 0) {

        System.out.println(SWITCH_IP);
        System.out.println(SWITCH_PORT);
            Send_User_Download_Request user_download_client = new Send_User_Download_Request(SWITCH_IP, SWITCH_PORT);
            user_download_client.execute();
//        }else {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    setDialog("Error", pack_message);
//                }
//            });
//        }
    }

    public void Set_Users() {
        planetsList.clear();
        String[] splitString = all_users.split("\\*");
        int count = splitString.length;
        SecurityLayer.Log("count", "count == " + count);
        for (String user_n_pass : splitString) {
            String[] split_user_n_pass = user_n_pass.split(",");
            for (int i = 1; i < split_user_n_pass.length; i++) {
                String uid = split_user_n_pass[0];
                SecurityLayer.Log("uid", "uid >> " + uid);
                if (!planetsList.contains(uid)) {
                    planetsList.add(new GetAirtimeBillersData(uid, uid, uid));
                }
            }
        }
        mobadapt = new ArrayAdapter<GetAirtimeBillersData>(ActivateAgentBefore.this, R.layout.my_spinner, planetsList);
        mobadapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_att_id.setAdapter(mobadapt);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void updateAndroidSecurityProvider(Activity callingActivity) {
        try {
            ProviderInstaller.installIfNeeded(this);
        } catch (GooglePlayServicesRepairableException e) {
            GooglePlayServicesUtil.getErrorDialog(e.getConnectionStatusCode(), callingActivity, 0);
        } catch (GooglePlayServicesNotAvailableException e) {
            SecurityLayer.Log("SecurityException", "Google Play Services not available.");
        }
    }

    private void checkPlayServices() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int code = api.isGooglePlayServicesAvailable(getApplicationContext());
        if (code == ConnectionResult.SUCCESS) {
            // Do Your Stuff Here
            registerInBackground();
        } else {
            /*Toast.makeText(
                    getApplicationContext(),
                    "Please ensure you have installed Google Play Services"
                    , Toast.LENGTH_LONG).show();*/
            registerInBackground();
        }
    }

    private void registerInBackground() {
        pDialog.show();
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                /*    if (!TextUtils.isEmpty(regId)) {*/
                String ip = Utility.getIP(getApplicationContext());
                String mac = Utility.getMacAddress(getApplicationContext());
                String serial = Utility.getSerial();
                String version = Utility.getDevVersion();
                String devtype = Utility.getDevModel();
                String imei = Utility.getDevImei(getApplicationContext());
                if (Utility.checkInternetConnection(getApplicationContext())) {
                    if (Utility.isNotNull(imei) && Utility.isNotNull(serial)) {
                        if (regId == null) {
                            regId = "JKKS";
                        }
                        //final String agid = agentid.getText().toString();
                        String agid = "";
                        if (users_count > 1) {
                            agid = sp_att_id.getSelectedItem().toString();
                        } else {
                            agid = agentid.getText().toString();
                        }
                        String params = CHANNEL_ID + agid;
                        RetroDevReg(params);
                    }
                } else {
                    Toast.makeText(
                            getApplicationContext(),
                            "Please ensure this device has an IMEI number",
                            Toast.LENGTH_LONG).show();
                    pDialog.hide();
                }
            }
        }.execute(null, null, null);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button2) {
            entry = 0;
            String agid = agentid.getText().toString();
            if (Utility.isNotNull(agid)) {
                if (Utility.checkInternetConnection(this)) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        insertDummyContactWrapper();
                    } else {
                        checkPlayServices();
                    }
                }
            } else {
                Toast.makeText(
                        getApplicationContext(),
                        "Please enter a valid value for Agent ID",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    @SuppressLint("NewApi")
    private void insertDummyContactWrapper() {
        List<String> permissionsNeeded = new ArrayList<String>();
        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, READ_PHONE_STATE))
            permissionsNeeded.add("Read Phone State");

        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                String message = "You need to grant access to " + permissionsNeeded.get(0);
                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message = message + ", " + permissionsNeeded.get(i);
                showMessageOKCancel(message,
                        new DialogInterface.OnClickListener() {
                            @SuppressLint("NewApi")
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                            }
                        });
                return;
            }

            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return;
        }
        if(entry == 1){
            Perform_User_Download();
        }else {
            checkPlayServices();
        }
    }

    @SuppressLint("NewApi")
    private boolean addPermission(List<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            if (!shouldShowRequestPermissionRationale(permission))
                return false;
        }
        return true;
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(ActivateAgentBefore.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    //checkPlayServices();
                    if( entry == 1){
                        Perform_User_Download();
                    }else {
                        checkPlayServices();
                    }
                } else {
                    Toast.makeText(
                            getApplicationContext(),
                            "Please note we need to allow the Read Phone permission to activate the app",
                            Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    public void ClearFields() {
        agentid.setText("");
    }

    private void RetroDevReg(String params) {
        String endpoint = "otp/generateotp.action/";
        String urlparams = "";
        try {
            urlparams = SecurityLayer.firstLogin(params, endpoint, getApplicationContext());
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
                    SecurityLayer.Log("response..:", response.body());
                    JSONObject obj = new JSONObject(response.body());
                    obj = SecurityLayer.decryptFirstTimeLogin(obj, getApplicationContext());
                    SecurityLayer.Log("decrypted_response", obj.toString());
                    String respcode = obj.optString("responseCode");
                    String responsemessage = obj.optString("message");
                    if (Utility.isNotNull(respcode) && Utility.isNotNull(responsemessage)) {
                        SecurityLayer.Log("Response Message", responsemessage);
                        if (respcode.equals("00")) {
                            JSONObject datas = obj.optJSONObject("data");
                            if(users_count > 1) {
                                session.SetUserID(sp_att_id.getSelectedItem().toString());
                            }else {
                                session.SetUserID(agentid.getText().toString());
                            }
                            session.SetEntryPoint("1");
                            String status = datas.optString("status");
                            Intent mIntent = new Intent(getApplicationContext(), ActivateAgent.class);
                            startActivity(mIntent);
                            /*final String agid = agentid.getText().toString();
                            session.SetUserID(agid);
                            String status = datas.optString("status");
                            Intent mIntent = new Intent(getApplicationContext(), ActivateAgent.class);
                            startActivity(mIntent);*/
                        } else {
                            Toast.makeText(
                                    getApplicationContext(),
                                    responsemessage,
                                    Toast.LENGTH_LONG).show();
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
                } catch (Exception e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                }
                if ((pDialog != null) && pDialog.isShowing()) {
                    pDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                SecurityLayer.Log("Throwable error", t.toString());
                Toast.makeText(
                        getApplicationContext(),
                        "There was an error processing your request",
                        Toast.LENGTH_LONG).show();
                if ((pDialog != null) && pDialog.isShowing()) {
                    pDialog.dismiss();
                }
            }
        });

    }

    public void setDialog(String title, String message) {
        new MaterialDialog.Builder(ActivateAgentBefore.this)
                .title(title)
                .content(message)
                .canceledOnTouchOutside(false)
                .negativeText("Dismiss")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        dialog.dismiss();
                        finish();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .show();
    }
    private class Send_User_Download_Request extends AsyncTask<Void, Void, Void> {
        String dstAddress;
        int dstPort;
        String response = "";

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
            int iret_pack = Set_User_Download_Data();
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
                //System.out.println(Dump.getHexDump(reqLenBs) + "," + Dump.getHexDump(reqData));
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
                //e.printStackTrace();
                //response = "IOException: " + e.toString();
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
                SecurityLayer.Log("response", "user download response == " + response);
                setDialog("Error", response);
                //setDialog("Error", "Unable to download terminal parameters. Please try again later");
            }
        }
    }
    private class Send_User_Download_Request_SSL extends AsyncTask<Void, Void, Void> {
        String dstAddress;
        int dstPort;
        String response = "";
        SSLSocketFactory sslSocketFactory = null;
        SSLSocket sslSocket = null;
        SSLContext sslContext = null;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        Send_User_Download_Request_SSL(String addr, int port) {
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
        }

        @Override
        protected Void doInBackground(Void... arg0){
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
//       protected Void doInBackground(Void... arg0) {
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
//                //System.out.println(Dump.getHexDump(reqLenBs) + "," + Dump.getHexDump(reqData));
//                bos.write(reqData);
//                bos.flush();
//
//                bis = new BufferedInputStream(sslSocket.getInputStream());
//                byte[] respLenbs = new byte[2];
//                bis.read(respLenbs);
//                int respLen = ISOUtils.unPackIntFromBytes(respLenbs, 0, 2, true);
//                //System.out.println(respLen);
//                byte[] buffer = new byte[respLen];
//                bis.read(buffer);
//                //System.out.println(Dump.getHexDump(buffer));
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
//                    SecurityLayer.Log("socket", "user download socket is null");
//                }
//            }
//            return null;
//        }

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

    private int Set_User_Download_Data() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("MMddhhmmss");
        SimpleDateFormat df_1 = new SimpleDateFormat("hhmmss");
        String formattedDate = df.format(c);
        String formattedDate_stan = df_1.format(c);

        if(ccid==null){
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                if ((ContextCompat.checkSelfPermission(this, READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)) {
                    /* Ask for Permission */
                    ActivityCompat.requestPermissions(this,
                            new String[]{READ_PHONE_STATE},
                            1);
                } else {

                    ccid = telephonyManager.getSimSerialNumber();
                }
                ApplicationClass.get().getGlobalVals().setSimSerial(ccid);
                ccid = ApplicationClass.get().getGlobalVals().getSimSerial();

        }
//        if(ccid==null){
//            SecurityLayer.Log("CCID", "Sim Card not reached");
//            ccid="89882390000043429722"; //todo John comment
//                return -1;
//        }
                mSDKManager = SDKManager.getInstance().getDeviceServiceEngine();
        if (mSDKManager == null) {
            SecurityLayer.Log("MF_SDKManager", "ServiceEngine is Null");
            pack_message = "Code -1: ";
            return -1;
        }
        Bundle devInfo = null;
        try {
            devInfo = mSDKManager.getDevInfo();
            serial_no = devInfo.getString(DeviceInfoConstrants.COMMOM_SN);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        //serial_no = "N7NL00479115";
        //serial_no = "981912059990840";
        if(BuildConfig.DEBUG)
            serial_no = "19990004";
//        serial_no="01223125";
        else
            serial_no = serial_no.substring(serial_no.length() - 8);

        SecurityLayer.Log("serial no", serial_no);
        iso8583 = new ISO8583(getApplicationContext());
        try {
            iso8583.loadXmlFile(CUPS8583);
        } catch (Exception e) {
            e.printStackTrace();
            pack_message = "Code -2: " + e.toString();
            return -1;
        }
        iso8583.initPack();
        try {
            iso8583.setField(0, "0800");
            iso8583.setField(3, "900280");
            iso8583.setField(7, formattedDate);
            iso8583.setField(11, formattedDate_stan);
            iso8583.setField(60, serial_no); //serial_no - N7NL00479115
            iso8583.setField(101, Utility.getAppVersion(getApplicationContext()));

//            System.out.println("check ccid " + ccid);
            iso8583.setField(127, ccid);
        } catch (Exception e) {
            SecurityLayer.Log("iso", "initPack error");
            e.printStackTrace();
            pack_message = "Code -3: " + e.toString();
            return -1;
        }
        try {
            requst = iso8583.pack();
        } catch (Exception e) {
            Toast.makeText(
                    getApplicationContext(),
                    "Please check if Sim Card is well inserted",
                    Toast.LENGTH_LONG).show();
            /*Toast.makeText(
                    getApplicationContext(),
                    "Package exception",
                    Toast.LENGTH_LONG).show();*/
            LoggerUtils.e("Group 8583 package exception");
            e.printStackTrace();
            pack_message = "Code -4: " + e.toString();
            return -1;
        }
        SecurityLayer.Log("user download req iso", requst);
        reqData = BytesUtils.hexToBytes(requst);
        /*Toast.makeText(
                getApplicationContext(),
                "iso === " + requst,
                Toast.LENGTH_LONG).show();*/
        return 1;
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

    private void Split_Fld_120() {
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

        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("tid", tid);
        editor.putString("mid", mid);
        editor.putString("mer_name", name);
        editor.putString("loc", loc);
        editor.putString("ccode", ccode);
        editor.putString("fld_43", fld_43);
        editor.putString("tmk", tmk);
        editor.putString("tpk", tpk);
        editor.commit();
        session.setString("agmobno", tid);
        session.setString(KEY_ALL_USERS, user_pass);
        try {
            Load_Keys_KE();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        all_users = session.getString(KEY_ALL_USERS);
        String[] splitString = all_users.split("\\*");
        users_count = splitString.length;
        if (users_count > 1) {
            rl_multiusers.setVisibility(View.VISIBLE);
            ll_att_id.setVisibility(View.GONE);
            Set_Users();
        } else {
            rl_multiusers.setVisibility(View.GONE);
            agentid.setText(user_only);
            agentid.setEnabled(false);
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
        editor.putString("last_kchange", curr_date);
        editor.commit();

        SecurityLayer.Log("enc_tmk", "enc_tmk >> " + enc_tmk);
        SecurityLayer.Log("enc_tpk", "enc_tpk >> " + enc_tpk);
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
        CalTrack(mSDKManager);
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
}




