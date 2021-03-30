package com.stanbicagent;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.stanbicagent.R;

import model.ChangePinModel;
import rest.ApiClient;
import rest.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import security.SecurityLayer;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ForgotPIN extends AppCompatActivity implements View.OnClickListener{
    ProgressDialog pDialog;
    EditText et,et2;
    Button btnok;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pin);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        et = (EditText) findViewById(R.id.pin);
        et2 = (EditText) findViewById(R.id.cpin);
        btnok = (Button) findViewById(R.id.button2);
        btnok.setOnClickListener(this);
        setSupportActionBar(toolbar);

        pDialog = new ProgressDialog(this);
        pDialog.setTitle("Loading");
        pDialog.setCancelable(false);
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.button2) {
            if (Utility.checkInternetConnection(getApplicationContext())) {
              String oldpin = et.getText().toString();
                String newpin = et2.getText().toString();
                if (Utility.isNotNull(oldpin)) {
                    if (Utility.isNotNull(newpin)) {
                        pDialog.show();


                        String encrypted1 = null;
                        String encrypted2 = null;


                        ApiInterface apiService =
                                ApiClient.getClient().create(ApiInterface.class);
                        String usid = Utility.gettUtilUserId(getApplicationContext());
                        String agentid = Utility.gettUtilAgentId(getApplicationContext());
                        String mobnoo = Utility.gettUtilMobno(getApplicationContext());
                        Call<ChangePinModel> call = apiService.getChngPin("1",usid,agentid,"0000",encrypted1,encrypted2);
                        call.enqueue(new Callback<ChangePinModel>() {
                            @Override
                            public void onResponse(Call<ChangePinModel>call, Response<ChangePinModel> response) {

                                if(!(response.body() == null)) {
                                    String responsemessage = response.body().getMessage();
                                    String respcode = response.body().getRespCode();
                                    SecurityLayer.Log("Response Message", responsemessage);

                                    if (Utility.isNotNull(respcode) && Utility.isNotNull(respcode)) {
                                        Toast.makeText(
                                                getApplicationContext(),
                                                "" + responsemessage,
                                                Toast.LENGTH_LONG).show();

                                    } else {

                                        Toast.makeText(
                                                getApplicationContext(),
                                                "There was an error on your request",
                                                Toast.LENGTH_LONG).show();


                                    }
                                } else {

                                    Toast.makeText(
                                            getApplicationContext(),
                                            "There was an error on your request",
                                            Toast.LENGTH_LONG).show();


                                }
                                pDialog.dismiss();
                            }

                            @Override
                            public void onFailure(Call<ChangePinModel> call, Throwable t) {
                                // Log error here since request failed
                                SecurityLayer.Log("throwable error",t.toString());


                                Toast.makeText(
                                        getApplicationContext(),
                                        "There was an error on your request",
                                        Toast.LENGTH_LONG).show();



                                pDialog.dismiss();
                            }
                        });
                    }  else {
                        Toast.makeText(
                                getApplicationContext(),
                                "Please enter a valid value for Confirm New pin",
                                Toast.LENGTH_LONG).show();
                    }
                }  else {
                    Toast.makeText(
                            getApplicationContext(),
                            "Please enter a valid value for New pin",
                            Toast.LENGTH_LONG).show();
                }
                    }

            }
        }

    public void forgotpin(){
        pDialog.show();


        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

       Call<ChangePinModel> call = apiService.getChngPin("1","CEVA","PAND0000000001","0000","","9B7D106A26E2884F");
        call.enqueue(new Callback<ChangePinModel>() {
            @Override
            public void onResponse(Call<ChangePinModel>call, Response<ChangePinModel> response) {

                if(!(response.body() == null)) {
                    String responsemessage = response.body().getMessage();
                    String respcode = response.body().getRespCode();
                    SecurityLayer.Log("Response Message", responsemessage);

                    if (Utility.isNotNull(respcode) && Utility.isNotNull(respcode)) {
                        Toast.makeText(
                                getApplicationContext(),
                                "" + responsemessage,
                                Toast.LENGTH_LONG).show();

                    } else {

                        Toast.makeText(
                                getApplicationContext(),
                                "There was an error on your request",
                                Toast.LENGTH_LONG).show();


                    }
                } else {

                    Toast.makeText(
                            getApplicationContext(),
                            "There was an error on your request",
                            Toast.LENGTH_LONG).show();


                }
                pDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ChangePinModel> call, Throwable t) {
                // Log error here since request failed
                SecurityLayer.Log("throwable error",t.toString());


                Toast.makeText(
                        getApplicationContext(),
                        "There was an error on your request",
                        Toast.LENGTH_LONG).show();



                pDialog.dismiss();
            }
        });
    }
}
