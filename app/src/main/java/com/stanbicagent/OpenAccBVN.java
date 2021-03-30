package com.stanbicagent;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.stanbicagent.R;
import com.afollestad.materialdialogs.MaterialDialog;

import org.json.JSONException;
import org.json.JSONObject;

import rest.ApiInterface;
import rest.ApiSecurityClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import security.SecurityLayer;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.stanbicagent.ApplicationConstants.CHANNEL_ID;

public class OpenAccBVN extends AppCompatActivity implements View.OnClickListener {
    ProgressDialog pro ;
    Button btnnext,btnopenacc;
    EditText agentid;
    SessionManagement session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_acc_bvn);
        session = new SessionManagement(this);  Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        // Get the ActionBar here to configure the way it behaves.
        final ActionBar ab = getSupportActionBar();
        //ab.setHomeAsUpIndicator(R.drawable.ic_menu); // set a custom icon for the default home button
        ab.setDisplayShowHomeEnabled(true); // show or hide the default home button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
        ab.setDisplayShowTitleEnabled(false); // disable the default title element here (for centered title)

        pro = new ProgressDialog(this);
        pro.setMessage("Loading...");
        pro.setTitle("");
        pro.setCancelable(false);


        agentid  = (EditText) findViewById(R.id.agentid);

        btnnext = (Button) findViewById(R.id.button2);
        btnnext.setOnClickListener(this);

        btnopenacc = (Button) findViewById(R.id.button5);
        btnopenacc.setOnClickListener(this);

    }


    private void GetBVN(final String bvn) {
        pro.show();
        String endpoint= "core/bvn.action";
        if(!(getApplicationContext() == null)) {
            String usid = Utility.gettUtilUserId(getApplicationContext());
            String appid = Utility.getFinAppid(getApplicationContext());
            String appvers = Utility.getAppVersion(getApplicationContext());
            String params = "1/CEVA/"+bvn;

            SecurityLayer.Log("params", params);
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


                        SecurityLayer.Log("Cable TV Resp", response.body());
                        SecurityLayer.Log("response..:", response.body());
                        JSONObject obj = new JSONObject(response.body());
                        //obj = Utility.onresp(obj,getApplicationContext());
                        obj = SecurityLayer.decryptTransaction(obj, getApplicationContext());
                        SecurityLayer.Log("decrypted_response", obj.toString());


                        JSONObject servdata = obj.optJSONObject("data");
                        //session.setString(SecurityLayer.KEY_APP_ID,appid);

                        if (!(response.body() == null)) {
                            String respcode = obj.optString("responseCode");
                            String responsemessage = obj.optString("message");

                            SecurityLayer.Log("Response Message", responsemessage);

                            if (Utility.isNotNull(respcode) && Utility.isNotNull(respcode)) {
                                if (!(Utility.checkUserLocked(respcode))) {
                                    SecurityLayer.Log("Response Message", responsemessage);
                                    if(respcode.equals("00")){

                                        String fname = servdata.optString("firstName");
                                        String lname = servdata.optString("lastname");

                                        String midname = servdata.optString("middleName");


                                        String yob = servdata.optString("dateOfBirth");


                                        String mobileNumber = servdata.optString("mobileNumber");


                                        Intent intent  = new Intent(OpenAccBVN.this,OpenAccBVNConfirm.class);



                                        intent.putExtra("fname", fname);
                                        intent.putExtra("lname", lname);
                                        intent.putExtra("midname", midname);
                                        intent.putExtra("yob", yob);
                                        intent.putExtra("bvn", bvn);

                                        intent.putExtra("city", "NA");


                                        intent.putExtra("hmadd", "NA");
                                        intent.putExtra("mobn", mobileNumber);



                                        startActivity(intent);
                                    }else{
                                        Toast.makeText(
                                                getApplicationContext(),
                                                "" + responsemessage,
                                                Toast.LENGTH_LONG).show();
                                    }


                                } else {
                                  /*  getApplicationContext().finish();
                                    startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                                    Toast.makeText(
                                            getApplicationContext(),
                                            "You have been locked out of the app.Please call customer care for further details",
                                            Toast.LENGTH_LONG).show();*/
                                    if(!(getApplicationContext() == null)) {
                                        ((FMobActivity) getApplicationContext()).LogOut();
                                    }
                                }
                            } else {

                               /* Toast.makeText(
                                        getApplicationContext(),
                                        "There was an error on your request",
                                        Toast.LENGTH_LONG).show();*/


                            }
                        } else {

                          /*  Toast.makeText(
                                    getApplicationContext(),
                                    "There was an error on your request",
                                    Toast.LENGTH_LONG).show();
*/

                        }
                         pro.dismiss();


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
                            Toast.makeText(getApplicationContext(), getApplicationContext().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                            // SecurityLayer.Log(e.toString());
                            SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getApplicationContext());
                        }
                        // SecurityLayer.Log(e.toString());
                    }
                    if(!(getApplicationContext() == null)){
                        pro.dismiss();
                    }

                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    // Log error here since request failed
                    // Log error here since request failed
                    SecurityLayer.Log("throwable error", t.toString());

                    
                    


                }
            });
        }
    }


    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.button2) {
            String agid = agentid.getText().toString();

            if (Utility.isNotNull(agid)) {

                if (Utility.checkInternetConnection(getApplicationContext())) {
                //    /core/bvn.action/1/CEVA/22141872609
                    String params = "1/CEVA/"+agid;
                    GetBVN(agid);

                }


            } else {
                Toast.makeText(
                        getApplicationContext(),
                        "Please enter a valid value for Agent ID",
                        Toast.LENGTH_LONG).show();
            }
        }

        if(view.getId() == R.id.button5) {
OpenAcc();
        }
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




    private void OpenAcc() {
        pro.show();

       String endpoint= "bvn/openaccount.action";
        if(!(getApplicationContext() == null)) {
            String usid = Utility.gettUtilUserId(getApplicationContext());
            String appid = Utility.getFinAppid(getApplicationContext());
            String appvers = Utility.getAppVersion(getApplicationContext());

            String params = CHANNEL_ID + usid + "/Mr/Ian/Kipkemboi/Midname/Married/2018-12-12/ian@cevaltd.com/M/03/04/ders/0718754578/12341421/22222222226/12344/12345";
 // /bvn/openaccount.action/{channel}/{userId}/{salutation}/{firstName}/{lastName}/{midName}/{maritalStatus}/{dob}/{email}/{gender}/{state}/{city}/{address}/{phone}/{mandateCard}/{bvn}/{otp}/{pin}

            SecurityLayer.Log("params", params);
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


                        SecurityLayer.Log("Cable TV Resp", response.body());
                        SecurityLayer.Log("response..:", response.body());
                        JSONObject obj = new JSONObject(response.body());
                        //obj = Utility.onresp(obj,getApplicationContext());
                        obj = SecurityLayer.decryptTransaction(obj, getApplicationContext());
                        SecurityLayer.Log("decrypted_response", obj.toString());


                        JSONObject servdata = obj.optJSONObject("data");
                        //session.setString(SecurityLayer.KEY_APP_ID,appid);

                        if (!(response.body() == null)) {
                            String respcode = obj.optString("responseCode");
                            String responsemessage = obj.optString("message");

                            SecurityLayer.Log("Response Message", responsemessage);

                            if (Utility.isNotNull(respcode) && Utility.isNotNull(respcode)) {
                                if (!(Utility.checkUserLocked(respcode))) {
                                    SecurityLayer.Log("Response Message", responsemessage);


                                } else {
                                  /*  getApplicationContext().finish();
                                    startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                                    Toast.makeText(
                                            getApplicationContext(),
                                            "You have been locked out of the app.Please call customer care for further details",
                                            Toast.LENGTH_LONG).show();*/
                                    if(!(getApplicationContext() == null)) {
                                        ((FMobActivity) getApplicationContext()).LogOut();
                                    }
                                }
                            } else {

                               /* Toast.makeText(
                                        getApplicationContext(),
                                        "There was an error on your request",
                                        Toast.LENGTH_LONG).show();*/


                            }
                        } else {

                          /*  Toast.makeText(
                                    getApplicationContext(),
                                    "There was an error on your request",
                                    Toast.LENGTH_LONG).show();
*/

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
                            Toast.makeText(getApplicationContext(), getApplicationContext().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                            // SecurityLayer.Log(e.toString());
                            SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getApplicationContext());
                        }
                        // SecurityLayer.Log(e.toString());
                    }
                    if(!(getApplicationContext() == null)){
                        pro.dismiss();
                    }

                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    // Log error here since request failed
                    // Log error here since request failed
                    SecurityLayer.Log("throwable error", t.toString());
pro.dismiss();




                }
            });
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
}
