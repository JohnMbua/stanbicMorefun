package com.stanbicagent;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.stanbicagent.R;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.security.ProviderInstaller;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import rest.ApiInterface;
import rest.ApiSecurityClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import security.SecurityLayer;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.stanbicagent.ApplicationConstants.CHANNEL_ID;

public class ForceChangePin extends AppCompatActivity implements View.OnClickListener {
    ProgressDialog pDialog;
    EditText et, et2, oldpin;
    Button btnok;
    SessionManagement session;
    String value;
    public static final String AGMOB = "agmobno";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_force_change_pin);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        oldpin = (EditText) findViewById(R.id.oldpin);
        et = (EditText) findViewById(R.id.pin);
        et2 = (EditText) findViewById(R.id.cpin);
        btnok = (Button) findViewById(R.id.button2);
        btnok.setOnClickListener(this);
        setSupportActionBar(toolbar);
        session = new SessionManagement(getApplicationContext());
        pDialog = new ProgressDialog(this);
        pDialog.setTitle("Loading");
        pDialog.setCancelable(false);
        updateAndroidSecurityProvider(getParent());
        if (!(getIntent() == null)) {
            value = getIntent().getExtras().getString("pinna");
        }


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

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.button2) {
            if (Utility.checkInternetConnection(getApplicationContext())) {
                final String npin = et.getText().toString();
                String confnpin = et2.getText().toString();
                String oldpinn = oldpin.getText().toString();
                if (Utility.isNotNull(oldpinn)) {
                    if (Utility.isNotNull(npin)) {
                        if (confnpin.equals(npin)) {
                            if (npin.length() == 5 && oldpinn.length() == 5) {
                                //   pDialog.show();
                                if (Utility.findweakPin(npin)) {


                                    String encrypted1 = null;
                                    String encrypted2 = null;


                                    String usid = Utility.gettUtilUserId(getApplicationContext());
                                    String agentid = Utility.gettUtilAgentId(getApplicationContext());
                                    String mobnoo = Utility.gettUtilMobno(getApplicationContext());

                                    encrypted1 = Utility.b64_sha256(oldpinn);
                                    encrypted2 = Utility.b64_sha256(npin);

                                    SecurityLayer.Log("Encrypted Pin", encrypted1);

                                    String params = CHANNEL_ID + usid + "/" + agentid + "/" + mobnoo + "/" + encrypted1 + "/";


                                    String lgparams = CHANNEL_ID + usid + "/" + value + "/" + mobnoo;
                                    LogRetro(lgparams, params, npin);


                                } else {
                                    Toast.makeText(
                                            getApplicationContext(),
                                            "You have set a weak PIN for New Pin Value.Please ensure you have selected a strong PIN",
                                            Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(
                                        getApplicationContext(),
                                        "Please ensure the Confirm New Pin and New Pin values are 5 digit length",
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Please ensure the Confirm New Pin and New Pin values are the same",
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(
                                getApplicationContext(),
                                "Please enter a valid value for New pin",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(
                            getApplicationContext(),
                            "Please enter a valid value for Current pin",
                            Toast.LENGTH_LONG).show();
                }
            }

        }
    }

    private void RetroDevReg(String params) {
        pDialog.show();
        SecurityLayer.Log("", "Inside Retro Dev Reg");

        String endpoint = "login/changepin.action";

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
                    // JSON Object
                    SecurityLayer.Log("response..:", response.body());


                    JSONObject obj = new JSONObject(response.body());
                 /*   JSONObject jsdatarsp = obj.optJSONObject("data");
                    SecurityLayer.Log("JSdata resp", jsdatarsp.toString());
                    //obj = Utility.onresp(obj,getActivity()); */
                    obj = SecurityLayer.decryptTransaction(obj, getApplicationContext());
                    SecurityLayer.Log("decrypted_response", obj.toString());

                    String respcode = obj.optString("responseCode");
                    String responsemessage = obj.optString("message");


                    //session.setString(SecurityLayer.KEY_APP_ID,appid);

                    if (Utility.isNotNull(respcode) && Utility.isNotNull(responsemessage)) {
                        SecurityLayer.Log("Response Message", responsemessage);

                        if (respcode.equals("00")) {
                            session.setString(SessionManagement.SESS_REG, "Y");
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Pin successfully changed.Please login",
                                    Toast.LENGTH_LONG).show();
                            finish();
                            Intent i = new Intent(getApplicationContext(), SignInActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            // Staring Login Activity
                            startActivity(i);
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


    public void ClearFields() {

        oldpin.setText("");
        et.setText("");
        et2.setText("");
    }


    private void invokeCheckRef(final String params) {

        pDialog.show();
        final AsyncHttpClient client = new AsyncHttpClient();
        String userid = Utility.gettUtilUserId(getApplicationContext());
        String appid = Utility.getFinAppid(getApplicationContext());
        client.addHeader("man", userid);

        client.addHeader("serial", appid);
        client.setTimeout(35000);

        String endpoint = "login/changepin.action";

        String url = "";
        SecurityLayer.Log("error:", "Inside Check Ref");
        try {
            url = ApplicationConstants.NET_URL + SecurityLayer.genURLCBC(params, endpoint, getApplicationContext());
            //SecurityLayer.Log("cbcurl",url);
            SecurityLayer.Log("RefURL", url);
            SecurityLayer.Log("refurl", url);
            SecurityLayer.Log("params", params);
        } catch (Exception e) {
            SecurityLayer.Log("encryptionerror", e.toString());
        }

        client.post(url, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {
                // Hide Progress Dialog

                try {
                    // JSON Object
                    SecurityLayer.Log("response..:", response);
                    JSONObject obj = new JSONObject(response);
                    //obj = Utility.onresp(obj,getActivity());
                    obj = SecurityLayer.decryptTransaction(obj, getApplicationContext());
                    SecurityLayer.Log("decrypted_response", obj.toString());

                    String respcode = obj.optString("responseCode");
                    String responsemessage = obj.optString("message");


                    //  JSONObject datas = obj.optJSONObject("data");
                    //session.setString(SecurityLayer.KEY_APP_ID,appid);

                    if (Utility.isNotNull(respcode) && Utility.isNotNull(responsemessage)) {
                        SecurityLayer.Log("Response Message", responsemessage);

                        if (respcode.equals("00")) {
                            session.setString(SessionManagement.SESS_REG, "Y");
                            finish();

                            Intent i = new Intent(getApplicationContext(), SignInActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
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
                    SecurityLayer.Log(e.toString());

                } catch (Exception e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    SecurityLayer.Log(e.toString());
                }
                pDialog.dismiss();
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


    private void invokeFchangepin(final String params) {
        final ProgressDialog pro = new ProgressDialog(this);
        pro.setMessage("Loading...");
        pro.setTitle("");
        pro.setCancelable(false);
        pro.show();
        final AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(35000);

        String sessid = UUID.randomUUID().toString();


        String session_id = UUID.randomUUID().toString();
        String endpoint = "login/login.action/";

        String url = "";
        try {
            url = SecurityLayer.beforeLogin(params, getApplicationContext(), endpoint);
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
                pro.dismiss();
                try {
                    // JSON Object
                    SecurityLayer.Log("response..:", response);
                    JSONObject obj = new JSONObject(response);
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

                            if (respcode.equals("00")) {
                                if (!(datas == null)) {
                                    String agentid = datas.optString("agent");
                                    String userid = datas.optString("userId");
                                    String username = datas.optString("userName");
                                    String email = datas.optString("email");
                                    String lastl = datas.optString("lastLoggedIn");
                                    String mobno = datas.optString("mobileNo");
                                    String accno = datas.optString("acountNumber");
                                    session.SetAgentID(agentid);
                                    session.SetUserID(userid);
                                    session.putCustName(username);
                                    session.putEmail(email);
                                    session.putLastl(lastl);
                                    session.putMobNo(mobno);
                                    session.putAccountno(accno);
                                    boolean checknewast = session.checkAst();
                                    if (checknewast == false) {
                                        Toast.makeText(getApplicationContext(), "Activation has been completed successfully.You have successfully logged in", Toast.LENGTH_LONG).show();
                                        finish();
                                        startActivity(new Intent(getApplicationContext(), AdActivity.class));
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Activation has been completed successfully.You have successfully logged in", Toast.LENGTH_LONG).show();
                                        finish();
                                        startActivity(new Intent(getApplicationContext(), FMobActivity.class));
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

            @Override
            public void onFailure(int statusCode, Throwable error,
                                  String content) {

                // Hide Progress Dialog
                pro.dismiss();
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


    private void LogRetro(final String params, final String chgpinparams, final String npin) {

        pDialog.show();
        String endpoint = "login/login.action/";

        String urlparams = "";
        try {
            urlparams = SecurityLayer.generalLogin(params, "23322", getApplicationContext(), endpoint);
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
                    obj = SecurityLayer.decryptGeneralLogin(obj, getApplicationContext());
                    SecurityLayer.Log("decrypted_response", obj.toString());

                    String respcode = obj.optString("responseCode");
                    String responsemessage = obj.optString("message");


                    JSONObject datas = obj.optJSONObject("data");

                    //session.setString(SecurityLayer.KEY_APP_ID,appid);

                    if (Utility.isNotNull(respcode) && Utility.isNotNull(responsemessage)) {
                        SecurityLayer.Log("Response Message", responsemessage);

                        if (respcode.equals("00")) {
                            if (!(datas == null)) {
                                session.setString(SessionManagement.CHKLOGIN, "Y");

                                String agentid = datas.optString("agent");
                                String userid = datas.optString("userId");
                                String username = datas.optString("userName");
                                String email = datas.optString("email");
                                String lastl = datas.optString("lastLoggedIn");
                                String mobno = datas.optString("mobileNo");
                                String accno = datas.optString("acountNumber");
                                String pubkey = datas.optString("publicKey");

                                session.SetAgentID(agentid);
                                session.SetUserID(userid);
                                session.putCustName(username);
                                session.putEmail(email);
                                session.putLastl(lastl);
                                session.setString(AGMOB, mobno);
                                session.putAccountno(accno);

                                session.setString("Base64image", "N");
                                session.setString(SessionManagement.KEY_SETBANKS, "N");
                                session.setString(SessionManagement.KEY_SETBILLERS, "N");
                                session.setString(SessionManagement.KEY_SETWALLETS, "N");
                                session.setString(SessionManagement.KEY_SETAIRTIME, "N");

                                String encryptednewpin = Utility.b64_sha256(npin);
                                Log.v("Public Key", pubkey);
                                boolean checknewast = session.checkAst();
                              /*  if (checknewast == false) {

                                    finish();
                                    Intent i = new Intent(getApplicationContext(), FMobActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    // Staring Login Activity
                                    startActivity(i);

                                } else {
                                    finish();
                                    startActivity(new Intent(getApplicationContext(), AdActivity.class));

                                }*/
                                String finalparams = chgpinparams + encryptednewpin;
                                RetroDevReg(finalparams);
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

    private void invokeLoginSec(final String params, final String chgpinparams) {

        pDialog.show();
        final AsyncHttpClient client = new AsyncHttpClient();
        String userid = Utility.gettUtilUserId(getApplicationContext());
        String appid = Utility.getFinAppid(getApplicationContext());
        client.addHeader("man", userid);

        client.addHeader("serial", appid);
        client.setTimeout(35000);

        String endpoint = "login/login.action/";

        String url = "";
        try {
            url = SecurityLayer.generalLogin(params, "23322", getApplicationContext(), endpoint);
            //SecurityLayer.Log("cbcurl",url);
            SecurityLayer.Log("RefURL", url);
            SecurityLayer.Log("refurl", url);
            SecurityLayer.Log("params", params);
        } catch (Exception e) {
            SecurityLayer.Log("encryptionerror", e.toString());
        }

        client.post(url, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {
                // Hide Progress Dialog

                try {
                    // JSON Object
                    SecurityLayer.Log("response..:", response);
                    JSONObject obj = new JSONObject(response);
                    //obj = Utility.onresp(obj,getActivity());
                    obj = SecurityLayer.decryptGeneralLogin(obj, getApplicationContext());
                    SecurityLayer.Log("decrypted_response", obj.toString());

                    String respcode = obj.optString("responseCode");
                    String responsemessage = obj.optString("message");


                    JSONObject datas = obj.optJSONObject("data");
                    //session.setString(SecurityLayer.KEY_APP_ID,appid);

                    if (Utility.isNotNull(respcode) && Utility.isNotNull(responsemessage)) {
                        SecurityLayer.Log("Response Message", responsemessage);
                        SecurityLayer.Log("Response Code", respcode);
                        if (respcode.equals("00")) {
                            if (!(datas == null)) {
                                RetroDevReg(chgpinparams);
                                SecurityLayer.Log("Inside Check Ref", "");
                                //   invokeAds();
                                   /* String agentid = datas.optString("agent");
                                    String userid = datas.optString("userId");
                                    String username = datas.optString("userName");
                                    String email = datas.optString("email");
                                    String lastl = datas.optString("lastLoggedIn");
                                    String mobno = datas.optString("mobileNo");
                                    String accno = datas.optString("acountNumber");
                                    session.SetAgentID(agentid);
                                    session.SetUserID(userid);
                                    session.putCustName(username);
                                    session.putEmail(email);
                                    session.putLastl(lastl);
                                    session.putMobNo(mobno);
                                    session.putAccountno(accno);
                                    boolean checknewast = session.checkAst();
                                    if (checknewast == false) {
                                        Toast.makeText(getApplicationContext(), "Activation has been completed successfully.You have successfully logged in", Toast.LENGTH_LONG).show();
                                        finish();
                                        startActivity(new Intent(getApplicationContext(), AdActivity.class));
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Activation has been completed successfully.You have successfully logged in", Toast.LENGTH_LONG).show();
                                        finish();
                                        startActivity(new Intent(getApplicationContext(), FMobActivity.class));
                                    }*/
                            } else {
                                Toast.makeText(getApplicationContext(), "Specific error in request", Toast.LENGTH_LONG).show();
                                SecurityLayer.Log("Data is Null", "");

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
                    SecurityLayer.Log(e.toString());

                } catch (Exception e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    SecurityLayer.Log(e.toString());
                }
                pDialog.dismiss();
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
