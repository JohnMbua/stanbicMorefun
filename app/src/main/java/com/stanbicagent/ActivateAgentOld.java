package com.stanbicagent;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.stanbicagent.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;

import com.google.android.gms.security.ProviderInstaller;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


import rest.ApiInterface;
import rest.ApiSecurityClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import security.SecurityLayer;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.stanbicagent.ApplicationConstants.CHANNEL_ID;

public class ActivateAgentOld extends AppCompatActivity implements View.OnClickListener {
Button btnnext;
    EditText agentid,agentpin,phonenumber;
    ProgressDialog pDialog;

    //Context applicationContext;
    SessionManagement session;
    String regId ;
    String encrypted;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    public static final String AGMOB = "agmobno";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activate_agent);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       agentid  = (EditText) findViewById(R.id.agentid);
        agentpin  = (EditText) findViewById(R.id.agentpin);
        phonenumber  = (EditText) findViewById(R.id.agentphon);
        btnnext = (Button) findViewById(R.id.button2);
        btnnext.setOnClickListener(this);
        agentid.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        session = new SessionManagement(getApplicationContext());
        pDialog = new ProgressDialog(ActivateAgentOld.this);

        pDialog.setTitle("Logging in.");
        updateAndroidSecurityProvider(getParent());

        pDialog.setCancelable(false);
       // testResp();
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void updateAndroidSecurityProvider(Activity callingActivity) {
        try {
            ProviderInstaller.installIfNeeded(this);
        } catch (GooglePlayServicesRepairableException e) {
            // Thrown when Google Play Services is not installed, up-to-date, or enabled
            // Show dialog to allow users to install, update, or otherwise enable Google Play services.
            GooglePlayServicesUtil.getErrorDialog(e.getConnectionStatusCode(), callingActivity, 0);
        } catch (GooglePlayServicesNotAvailableException e) {
            SecurityLayer.Log("SecurityException", "Google Play Services not available.");
        }
    }
private void checkPlayServices(){
    GoogleApiAvailability api = GoogleApiAvailability.getInstance();
    int code = api.isGooglePlayServicesAvailable(getApplicationContext());
    if (code == ConnectionResult.SUCCESS) {
        // Do Your Stuff Here
        registerInBackground();
    } else {
        Toast.makeText(
                getApplicationContext(),
                "Please ensure you have installed Google Play Services"
                     , Toast.LENGTH_LONG).show();
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
                    if (Utility.checkInternetConnection(getApplicationContext())){
                        if (Utility.isNotNull(imei) && Utility.isNotNull(serial)) {
                            if(regId == null){
                                regId = "JKKS";
                            }



                            final   String agid = agentid.getText().toString();
                           String agpin = agentpin.getText().toString();
                            String phnnumb = phonenumber.getText().toString();
                            phnnumb = Utility.CheckNumberZero(phnnumb);
                            encrypted = "sdd";

                            String params = CHANNEL_ID+agid+"/"+phnnumb+"/"+encrypted+"/secans1/"+ "secans2"+"/secans3/"+mac+"/"+ip+"/"+imei+"/"+serial+"/"+version+"/"+devtype+"/"+regId;
                           invokeAgent(params);

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
        if(v.getId() == R.id.button2){

            String agid = agentid.getText().toString();
            String agpin = agentpin.getText().toString();
            String phnnumb = phonenumber.getText().toString();
            if (Utility.isNotNull(agid)) {
                if (Utility.isNotNull(agpin)) {
                    if (Utility.isNotNull(phnnumb)) {
                        if (Utility.checkInternetConnection(getApplicationContext())) {
                            if (Build.VERSION.SDK_INT >= 23) {
                                insertDummyContactWrapper();
                            } else {
                                // Pre-Marshmallow
                             //   registerInBackground();
                                checkPlayServices();
                            }



                        }

                    } else {
                        Toast.makeText(
                                getApplicationContext(),
                                "Please enter a valid value for mobile number",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(
                            getApplicationContext(),
                            "Please enter a valid value for phone number ",
                            Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(
                        getApplicationContext(),
                        "Please enter a valid value for Agent ID",
                        Toast.LENGTH_LONG).show();
            }

        }
    }


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

      //  registerInBackground();
        checkPlayServices();
    }

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
        new AlertDialog.Builder(ActivateAgentOld.this)
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


    public void ClearFields(){

        agentid.setText("");
        agentpin.setText("");
        phonenumber.setText("");
    }

    private void RetroDevReg(String params) {



        String endpoint= "reg/devReg.action";

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
                            JSONObject datas = obj.optJSONObject("data");
                            String agent = datas.optString("agent");
                            if (!(datas == null)) {
                                final   String agid = agentid.getText().toString();
                                session.SetUserID(agid);
                                session.SetAgentID(agent);
                                finish();
                                Intent mIntent = new Intent(getApplicationContext(), ForceChangePin.class);
                                mIntent.putExtra("pinna", encrypted);
                                startActivity(mIntent);
                            }
                        }
                        else {

                            Toast.makeText(
                                    getApplicationContext(),
                                    responsemessage,
                                    Toast.LENGTH_LONG).show();


                        }

                    }
                    else {

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

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                SecurityLayer.Log("Throwable error",t.toString());
                Toast.makeText(
                        getApplicationContext(),
                        "There was an error processing your request",
                        Toast.LENGTH_LONG).show();

            }
        });

    }
    private void invokeAgent(final String params) {

        final AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(35000);

        String sessid = UUID.randomUUID().toString();



        String endpoint= "reg/devReg.action/";

        String url = "";
        try {
            url = SecurityLayer.firstLogin(params,endpoint,getApplicationContext());
            //SecurityLayer.Log("cbcurl",url);
            SecurityLayer.Log("params", params);
            SecurityLayer.Log("refurl", url);
        } catch (Exception e) {
//SecurityLayer.Log("encryptionerror",e.toString());
        }


        client.post(url, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {
                // Hide Progress Dialog
                pDialog.dismiss();
                try {
                    // JSON Object
                    SecurityLayer.Log("response..:", response);


                    JSONObject obj = new JSONObject(response);
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
                                JSONObject datas = obj.optJSONObject("data");
                                String agent = datas.optString("agent");
                                if (!(datas == null)) {
                                    final   String agid = agentid.getText().toString();
                                    final   String mobno = phonenumber.getText().toString();
                                    String status = datas.optString("status");
                                    session.SetUserID(agid);
                                    session.SetAgentID(agent);
                                    session.setString(AGMOB,mobno);
                                    if(status.equals("F")) {
                                        finish();
                                        Intent mIntent = new Intent(getApplicationContext(), ForceChangePin.class);
                                        mIntent.putExtra("pinna", encrypted);
                                        startActivity(mIntent);
                                    }else{
                                        session.setReg();
                                        finish();
                                        Intent mIntent = new Intent(getApplicationContext(), SignInActivity.class);

                                        startActivity(mIntent);
                                    }
                                }
                            }
                            else {

                                Toast.makeText(
                                        getApplicationContext(),
                                        responsemessage,
                                        Toast.LENGTH_LONG).show();


                            }

                        }
                        else {

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
            }

            @Override
            public void onFailure(int statusCode, Throwable error,
                                  String content) {

                // Hide Progress Dialog
                pDialog.dismiss();
                SecurityLayer.Log("error:", error.toString());
                // When Http response code is '404'
                if (statusCode == 404) {
                    Toast.makeText(getApplicationContext(), "We are unable to process your request at the moment. Please try again later", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    Toast.makeText(getApplicationContext(), "We are unable to process your request at the moment. Please try again later", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    Toast.makeText(getApplicationContext(), getApplicationContext().getText(R.string.conn_error), Toast.LENGTH_LONG).show();

                }
            }
        });
    }

public void testResp(){
    try {
        // JSON Object

        JSONObject obj = new JSONObject();
        obj.put("DHASH","664cb9659759a8d343be772bed32898361d090821eebdb33b49aa53f6ba14e");
        obj.put("inp","74335a673147557466456a2b323549654c647747573245476d47306b75485a474a726e4f6944466f6d534e696b794c743250574b6671484333662b6d6e5a38522b4e5173503662585a522b770d0a58576b3341682b663759484a664e762f67745a6c464f42657575474e444e765a2b54673951754542583976492b7764474f496652585541574d79556437305674494f4f456b566662757843560d0a574e644f50455231334a6b65432f746e31552b343935737a503634474b2f47434730374e6b745551612b68767a6236365a36544348457a6b35754968585179476d6c6441456162304b7378480d0a306d354a6d6473466d346c73304c4c4a30316d4349306267754e6d6e7a776249566762634375725a4536754a4667482b525575426158597033396a434c612f61612b477567725865556c4c610d0a6f6a3566653061395a45496a4a5669460d0a");
        obj.put("pkey","394a7334724d665757396a3356324a62737831474657644b4336574a386f6a47663257574b50643958397754516a7338644c3170703043774d7a7743732b626e0d0a");
        obj.put("pvoke","6e425150522f3476585a6d556b46344e634d7a4c413033676d7a4d3461774749587a7547383953484173493d0d0a");
        obj.put("skey","475177463837624c34724f69474363434d626a574955714f6b5a3739466a656f6a49782b756a476b7939693133775472742b6f38626a2f6d33445a34556143300d0a");
        obj.put("svoke","3742387876343464346f476876764c55684b413449346e716a357444432f5348576173716a6c45636352733d0d0a");
        obj.put("status","S");



        //obj = Utility.onresp(obj,getActivity());
        obj = SecurityLayer.decryptFirstTimeLogin(obj, getApplicationContext());
        SecurityLayer.Log("decrypted_response", obj.toString());

        String respcode = obj.optString("responseCode");
        String responsemessage = obj.optString("message");


        JSONObject datas = obj.optJSONObject("data");
        //session.setString(SecurityLayer.KEY_APP_ID,appid);
        if (respcode.equals("00")) {
            if (Utility.isNotNull(respcode) && Utility.isNotNull(responsemessage)) {
                SecurityLayer.Log("Response Message", responsemessage);
                Toast.makeText(
                        getApplicationContext(),
                        "" + responsemessage,
                        Toast.LENGTH_LONG).show();

                if (respcode.equals("00")) {
                    if (!(datas == null)) {

                        finish();
                        startActivity(new Intent(getApplicationContext(), ForceChangePin.class));
                    }
                }
                else {

                    Toast.makeText(
                            getApplicationContext(),
                            responsemessage,
                            Toast.LENGTH_LONG).show();


                }

            }
            else {

                Toast.makeText(
                        getApplicationContext(),
                        "There was an error on your request",
                        Toast.LENGTH_LONG).show();


            }
        }

        // Else display error message
        else {
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
}

}
