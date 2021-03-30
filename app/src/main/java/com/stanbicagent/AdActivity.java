package com.stanbicagent;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.stanbicagent.R;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.security.ProviderInstaller;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import rest.ApiInterface;
import rest.ApiSecurityClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import security.SecurityLayer;

import static com.stanbicagent.ApplicationConstants.CHANNEL_ID;

public class AdActivity extends AppCompatActivity implements View.OnClickListener {

   // List<GetAgentIdData> plan = new ArrayList<GetAgentIdData>();
    String agid;

    ImageView iv;
   // SweetAlertDialog pDialog;
   public static final String SHAREDPREFFILE = "temp";
    public static final String USERIDPREF = "uid";
    public static final String TOKENPREF = "tkn";
    ProgressBar prgbar;
    Button btncontinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        btncontinue = (Button) findViewById(R.id.btncontinue);
        setSupportActionBar(toolbar);
//mClient  = new MobileServiceClient("https://cevabank.azurewebsites.net");
     /*   try {
            mClient = new MobileServiceClient("https://auth21.azurewebsites.net", getApplicationContext());


            //SecurityLayer.Log(Constant.LOG_TAG, "tuko poa: ");
        } catch (MalformedURLException m) {
            m.printStackTrace();
            //SecurityLayer.Log(Constant.LOG_TAG, "maneno: " + m.toString());
        }*/
        Button btnc = (Button) findViewById(R.id.btncontinue);

        prgbar = (ProgressBar) findViewById(R.id.prgbar);
        prgbar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.nbkyellow), android.graphics.PorterDuff.Mode.MULTIPLY);

      /*  pDialog = new SweetAlertDialog(this,SweetAlertDialog.PROGRESS_TYPE);
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);*/

        iv = (ImageView) findViewById(R.id.adidimg);
        //  iv.setImageBitmap(null);

        updateAndroidSecurityProvider(getParent());

        btnc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "You have successfully logged in", Toast.LENGTH_LONG).show();
                finish();
                startActivity(new Intent(getApplicationContext(),FMobActivity.class));
            }
        });   OkHttpClient client = new OkHttpClient();


if(Utility.checkInternetConnection(getApplicationContext())){
  //  getAgentIDs();
  fetchAds();
}

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
    private void fetchAds() {


        String endpoint= "adverts/ads.action";


        String usid = Utility.gettUtilUserId(getApplicationContext());
        String agentid = Utility.gettUtilAgentId(getApplicationContext());
        String mobilenumber = Utility.gettUtilMobno(getApplicationContext());
        String params = CHANNEL_ID+usid+"/"+agentid+"/"+mobilenumber;
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
                    //obj = Utility.onresp(obj,getActivity());
                    obj = SecurityLayer.decryptTransaction(obj, getApplicationContext());
                    SecurityLayer.Log("decrypted_response", obj.toString());

                    String respcode = obj.optString("responseCode");
                    String responsemessage = obj.optString("message");


                    JSONArray plan = obj.optJSONArray("data");
                    //session.setString(SecurityLayer.KEY_APP_ID,appid);





                    SecurityLayer.Log("Response Message", responsemessage);
                    if (respcode.equals("00")) {
                        if (Utility.isNotNull(respcode) && Utility.isNotNull(responsemessage)) {
                            SecurityLayer.Log("Response Message", responsemessage);

                            if (respcode.equals("00")) {
                                JSONObject json_data = null;

                                prgbar.setVisibility(View.GONE);
                                Picasso.with(getApplicationContext()).load(R.drawable.adfbn).into(iv);
                                btncontinue.setVisibility(View.VISIBLE);

//                                    SecurityLayer.Log("Respnse getResults",datas.toString());
                               /* if (!(plan == null)) {

                                        for (int sw = 0; sw < plan.length(); sw++) {

                                                json_data = plan.getJSONObject(sw);
                                            if(!(json_data == null)) {
                                                String imgloc = json_data.optString("imgLoc");
                                                if (imgloc.equals("HOME")) {
                                                    agid = json_data.optString("id");
                                                    new DownloadImg().execute("");
                                                } else {
                                                    Picasso.with(getApplicationContext()).load(R.drawable.adfbn).into(iv);
                                                    prgbar.setVisibility(View.GONE);

                                                }
                                            }

                                        }

                                } else {
                                    Toast.makeText(
                                            getApplicationContext(),
                                            "There was an error processing your request",
                                            Toast.LENGTH_LONG).show();
                                    Picasso.with(getApplicationContext()).load(R.drawable.adfbn).into(iv);

                                    prgbar.setVisibility(View.GONE);
                                }*/
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
                   /* if (respcode.equals("00")) {
                        if (Utility.isNotNull(respcode) && Utility.isNotNull(responsemessage)) {
                            SecurityLayer.Log("Response Message", responsemessage);

                            if (respcode.equals("00")) {

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

                    }*/
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
                //   pDialog.dismiss();
                prgbar.setVisibility(View.GONE);

            }
        });


    }
/*public void getAgentIDs(){
 //   pDialog.show();



    ApiInterface apiService =
            ApiClient.getClient().create(ApiInterface.class);
    String usid = Utility.gettUtilUserId(getApplicationContext());
    String agentid = Utility.gettUtilAgentId(getApplicationContext());
    String mobnoo = Utility.gettUtilMobno(getApplicationContext());
    Call<GetAgentId> call = apiService.GetAgId("1", usid, agentid, "0000");
    call.enqueue(new Callback<GetAgentId>() {
        @Override
        public void onResponse(Call<GetAgentId> call, Response<GetAgentId> response) {
            String responsemessage = response.body().getMessage();

            SecurityLayer.Log("Response Message", responsemessage);
            plan.clear();
            plan = response.body().getData();
//                                    SecurityLayer.Log("Respnse getResults",datas.toString());
            if (!(plan == null)) {
if(!(plan.get(0) == null)) {
    for(int sw = 0;sw < plan.size();sw++) {
        if (!(plan.get(sw) == null)) {
            String imgloc = plan.get(sw).getimgLoc();
            if (imgloc.equals("HOME")) {
                agid = plan.get(sw).getId();
                new DownloadImg().execute("");
            }
        }else{
            Picasso.with(getApplicationContext()).load(R.drawable.adfbn).into(iv);
            prgbar.setVisibility(View.GONE);

        }
    }
}else{
    Picasso.with(getApplicationContext()).load(R.drawable.adfbn).into(iv);
    prgbar.setVisibility(View.GONE);
}
            }
            else{
                Toast.makeText(
                        getApplicationContext(),
                        "There was an error processing your request",
                        Toast.LENGTH_LONG).show();
                prgbar.setVisibility(View.GONE);
            }
          //  pDialog.dismiss();

        }

        @Override
        public void onFailure(Call<GetAgentId> call, Throwable t) {
            // Log error here since request failed
            SecurityLayer.Log("Throwable error",t.toString());
            Toast.makeText(
                    getApplicationContext(),
                    "There was an error processing your request",
                    Toast.LENGTH_LONG).show();
         //   pDialog.dismiss();
           prgbar.setVisibility(View.GONE);
        }
    });

}*/




    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.adidimg) {

        }

    }



    private void invokeAds() {
        final ProgressDialog pro = new ProgressDialog(this);
        pro.setMessage("Loading...");
        pro.setTitle("");
        pro.setCancelable(false);
        pro.show();
        final AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(35000);

        String endpoint= "adverts/ads.action";


        String usid = Utility.gettUtilUserId(getApplicationContext());
        String agentid = Utility.gettUtilAgentId(getApplicationContext());
        String mobnoo = Utility.gettUtilMobno(getApplicationContext());
        String params = CHANNEL_ID+usid+"/"+agentid+"/"+mobnoo;
        String url = "";
        try {
            url = SecurityLayer.genURLCBC(params,endpoint,getApplicationContext());
            //SecurityLayer.Log("cbcurl",url);
            SecurityLayer.Log("RefURL",url);
            SecurityLayer.Log("refurl", url);
            SecurityLayer.Log("params", params);
        } catch (Exception e) {
            SecurityLayer.Log("encryptionerror",e.toString());
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
                    obj = SecurityLayer.decryptTransaction(obj, getApplicationContext());
                    SecurityLayer.Log("decrypted_response", obj.toString());

                    String respcode = obj.optString("responseCode");
                    String responsemessage = obj.optString("message");


                    JSONObject datas = obj.optJSONObject("data");
                    //session.setString(SecurityLayer.KEY_APP_ID,appid);
                    if (respcode.equals("00")) {
                        if (Utility.isNotNull(respcode) && Utility.isNotNull(responsemessage)) {
                            SecurityLayer.Log("Response Message", responsemessage);

                            if (respcode.equals("00")) {

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

}



