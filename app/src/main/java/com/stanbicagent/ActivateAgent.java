package com.stanbicagent;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.stanbicagent.BuildConfig;
import com.stanbicagent.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.security.ProviderInstaller;
import com.morefun.yapi.engine.DeviceInfoConstrants;
import com.morefun.yapi.engine.DeviceServiceEngine;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import rest.ApiInterface;
import rest.ApiSecurityClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import security.SecurityLayer;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.stanbicagent.ApplicationConstants.CHANNEL_ID;

public class ActivateAgent extends AppCompatActivity implements View.OnClickListener {
    Button btnnext;
    TextView btnresp;
    EditText agentid, agentpin, phonenumber;
    //Context applicationContext;
    SessionManagement session;
    String regId;
    String encrypted, serial_no = "";
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    public static final String AGMOB = "agmobno";
    ProgressDialog pDialog;
    DeviceServiceEngine mSDKManager;
    String mlat = "NA";
    String mlongt = "NA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activate_agent);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SDKManager.getInstance().bindService(getApplicationContext());
        agentid = (EditText) findViewById(R.id.agentid);
        agentpin = (EditText) findViewById(R.id.agentpin);
        phonenumber = (EditText) findViewById(R.id.agentphon);
        btnnext = (Button) findViewById(R.id.button2);
        btnnext.setOnClickListener(this);
        btnresp = (TextView) findViewById(R.id.button5);
        btnresp.setOnClickListener(this);
        agentid.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        session = new SessionManagement(getApplicationContext());
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading ....");
        // Set Cancelable as False
        pDialog.setCancelable(false);
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
            registerInBackground();
        } else {
            registerInBackground();
        }
    }

    public void receivedSMS(String message) {
        try {
            phonenumber.setText(message);
        } catch (Exception e) {
            e.printStackTrace();
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
                        //   final   String agid = agentid.getText().toString();
                        String agid = Utility.gettUtilUserId(getApplicationContext());
                        String agpin = agentpin.getText().toString();
                        String phnnumb = phonenumber.getText().toString();
                        phnnumb = Utility.CheckNumberZero(phnnumb);
                        SecurityLayer.Log("Plain Pin", agpin);
                        String pubkey = "97206B46CE46376894703ECE161F31F2";
                        try {
                            encrypted = Utility.b64_sha256(agpin);
                            SecurityLayer.Log("Hex Pin", Utility.toHex(agpin));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        SecurityLayer.Log("Hashed Pin", Utility.b64_sha256(agpin));
                        SecurityLayer.Log("Encrypted Pin", encrypted);
                        String params = CHANNEL_ID + agid + "/" + phnnumb + "/" + encrypted + "/secans1/" + "secans2" + "/secans3/" + mac + "/" + ip + "/" + imei + "/" + serial + "/" + version + "/" + devtype + "/" + regId;
                        params = CHANNEL_ID + agid + "/" + phnnumb + "/" + encrypted + "/NA/NA/" + mac + "/" + ip + "/" + imei + "/" + serial_no + "/" + version + "/" + devtype + "/" + regId;
                        //String params = "1/MUNENEM/8121210402/309B3E7906BFF7C8/secans1/secans2/secans3/02:00:00:00:00:00/192.168.0.55/358812060537681/FA55BYN01623/23/HTC_M9pw/JKKS";
//                        String params = CHANNEL_ID + agid + "/" + phnnumb + "/" + encrypted + "/" + mlat + "/" + mlongt + "/" + mac + "/" + ip + "/" + imei + "/" + serial_no + "/" + version + "/" + devtype + "/" + regId; //from N910
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
            mSDKManager = SDKManager.getInstance().getDeviceServiceEngine();
            if (mSDKManager == null) {
                SecurityLayer.Log("MF_SDKManager", "ServiceEngine is Null");
                return;
            }
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
//            serial_no="01223125";
            else
                serial_no = serial_no.substring(serial_no.length() - 8);

            SecurityLayer.Log("serial_no", "serial_no >> " + serial_no);
            String agpin = agentpin.getText().toString();
            String phnnumb = phonenumber.getText().toString();
            if (Utility.isNotNull(agpin)) {
                if (Utility.isNotNull(phnnumb)) {
                    if (Utility.checkInternetConnection(getApplicationContext())) {
                        if (Build.VERSION.SDK_INT >= 23) {
                            insertDummyContactWrapper();
                        } else {
                            checkPlayServices();
                        }
                    }
                } else {
                    Toast.makeText(
                            getApplicationContext(),
                            "Please enter a valid value for OTP",
                            Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(
                        getApplicationContext(),
                        "Please enter a valid value for activation key",
                        Toast.LENGTH_LONG).show();
            }


        }


        if (v.getId() == R.id.button5) {
            String agid = Utility.gettUtilUserId(getApplicationContext());
            String params = CHANNEL_ID + agid;
            pDialog.show();
            GenerateOTPParams(params);
        }
    }

    private void GenerateOTPParams(String params) {
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
                    // JSON Object
                    SecurityLayer.Log("response..:", response.body());


                    JSONObject obj = new JSONObject(response.body());
                 /*   JSONObject jsdatarsp = obj.optJSONObject("data");
                    SecurityLayer.Log("JSdata resp", jsdatarsp.toString());
                    //obj = Utility.onresp(obj,getActivity()); */
                    obj = SecurityLayer.decryptFirstTimeLogin(obj, getApplicationContext());
                    SecurityLayer.Log("decrypted_response", obj.toString());

                    String respcode = obj.optString("responseCode");
                    String responsemessage = obj.optString("message");


                    //session.setString(SecurityLayer.KEY_APP_ID,appid);

                    if (Utility.isNotNull(respcode) && Utility.isNotNull(responsemessage)) {
                        SecurityLayer.Log("Response Message", responsemessage);

                        if (respcode.equals("00")) {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "OTP has been successfully resent",
                                    Toast.LENGTH_LONG).show();

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
                    // TODO Auto-generated catch block
                    Toast.makeText(getApplicationContext(), getApplicationContext().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                    // SecurityLayer.Log(e.toString());

                } catch (Exception e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    // SecurityLayer.Log(e.toString());
                }
                if ((pDialog != null) && pDialog.isShowing()) {
                    pDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                SecurityLayer.Log("Throwable error", t.toString());
                Toast.makeText(
                        getApplicationContext(),
                        "There was an error processing your request",
                        Toast.LENGTH_LONG).show();
                if ((pDialog != null) && pDialog.isShowing()) {
                    pDialog.dismiss();
                }
                //SetForceOutDialog(getString(R.string.forceout),getString(R.string.forceouterr));
            }
        });

    }

    @SuppressLint("NewApi")
    private void insertDummyContactWrapper() {
        List<String> permissionsNeeded = new ArrayList<String>();

        final List<String> permissionsList = new ArrayList<String>();


        if (!addPermission(permissionsList, Manifest.permission.READ_PHONE_STATE))
            permissionsNeeded.add("Read Phone State");

        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
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

        checkPlayServices();
    }

    @SuppressLint("NewApi")
    private boolean addPermission(List<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);


            // Check for Rationale Option
            if (!shouldShowRequestPermissionRationale(permission))
                return false;
        }
        return true;
    }


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(ActivateAgent.this)
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
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    checkPlayServices();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(
                            getApplicationContext(),
                            "Please note we need to allow this permission to activate the app",
                            Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
    }


    public void ClearFields() {

        agentid.setText("");
        agentpin.setText("");
        phonenumber.setText("");
    }

    private void RetroDevReg(String params) {

        SecurityLayer.Log("", "Inside Retro Dev Reg");

        String endpoint = "reg/devReg.action/";

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
                    // JSON Object
                    SecurityLayer.Log("response..:", response.body());
                    JSONObject obj = new JSONObject(response.body());
                    obj = SecurityLayer.decryptFirstTimeLogin(obj, getApplicationContext());
                    SecurityLayer.Log("decrypted_response", obj.toString());

                    String respcode = obj.optString("responseCode");
                    String responsemessage = obj.optString("message");


                    //session.setString(SecurityLayer.KEY_APP_ID,appid);

                    if (Utility.isNotNull(respcode) && Utility.isNotNull(responsemessage)) {
                        SecurityLayer.Log("Response Message", responsemessage);

                        if (respcode.equals("00")) {
                            JSONObject datas = obj.optJSONObject("data");
                            String agent = datas.optString("agent");
                            if (!(datas == null)) {
                                final String agid = agentid.getText().toString();
                                final String mobno = phonenumber.getText().toString();
                                String status = datas.optString("status");
                                //    session.SetUserID(agid);
                                session.SetAgentID(agent);
                                session.setString(AGMOB, mobno);
                                if (status.equals("F")) {
                                    finish();
                                    Intent mIntent = new Intent(getApplicationContext(), ForceChangePin.class);
                                    mIntent.putExtra("pinna", encrypted);
                                    startActivity(mIntent);
                                } else {
                                    session.setString(SessionManagement.SESS_REG, "Y");

                                    finish();
                                    Intent mIntent = new Intent(getApplicationContext(), SignInActivity.class);
                                    mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(mIntent);
                                }
                            }
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
                    // TODO Auto-generated catch block
                    Toast.makeText(getApplicationContext(), getApplicationContext().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                    // SecurityLayer.Log(e.toString());

                } catch (Exception e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    // SecurityLayer.Log(e.toString());
                }
                pDialog.dismiss();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                SecurityLayer.Log("Throwable error", t.toString());
                Toast.makeText(
                        getApplicationContext(),
                        "There was an error processing your request",
                        Toast.LENGTH_LONG).show();
                pDialog.dismiss();
            }
        });
    }
}
