package com.stanbicagent;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.ActionBar;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.stanbicagent.R;
import com.afollestad.materialdialogs.MaterialDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import adapter.BillMenuParcelable;
import adapter.ServicesMenuAdapt;
import model.GetServicesData;
import rest.ApiClient;
import rest.ApiInterface;
import rest.ApiSecurityClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import security.SecurityLayer;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.stanbicagent.ApplicationConstants.CHANNEL_ID;

public class BillMenuActivity extends BaseActivity {

    GridView gridView;
    List<GetServicesData> planetsList = new ArrayList<GetServicesData>();
    String ptype;
    ListView lv;
    ServicesMenuAdapt aAdpt;
    ProgressDialog prgDialog, prgDialog2;
    SessionManagement session;
    String sbpam = "0", pramo = "0";
    boolean blsbp = false, blpr = false, blpf = false, bllr = false, blms = false, blmpesa = false, blcash = false;
    ArrayList<String> ds = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_menu);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        // Get the ActionBar here to configure the way it behaves.
        final ActionBar ab = getSupportActionBar();
        //ab.setHomeAsUpIndicator(R.drawable.ic_menu); // set a custom icon for the default home button
        ab.setDisplayShowHomeEnabled(true); // show or hide the default home button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
        ab.setDisplayShowTitleEnabled(false); // disable the default title element here (for centered title)

        session = new SessionManagement(this);
        
        prgDialog = new ProgressDialog(this);

        prgDialog.setMessage("Please wait...");

        prgDialog2 = new ProgressDialog(this);
        prgDialog2.setMessage("Loading....");

        gridView = (GridView) findViewById(R.id.gridView1);

        prgDialog.setCancelable(false);

        //checkInternetConnection2();
        lv = (ListView) findViewById(R.id.lv);
        //   SetPop();
        //  SetBillersStored();
        String strservdata = session.getString(SessionManagement.KEY_BILLERS);
        if(!(strservdata == null)){
            JSONArray servdata = null;
            try {
                servdata = new JSONArray(strservdata);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(servdata.length() > 0) {
                SetBillersStored();
            }else{
                SetPop();
            }
        }else {
            SetPop();
        }
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                String title = null;
                String serviceid = planetsList.get(position).getId();
                String servicename = planetsList.get(position).getServiceName();
                String label = planetsList.get(position).getLabel();




                title = servicename;

                Intent intent  = new Intent(BillMenuActivity.this,SpecBillerMenuActivity.class);
                BillMenuParcelable bcp = new BillMenuParcelable(serviceid,servicename,null,null,null,null,null,null,null,null,null,null,null,null,null,null);



                /*intent.putExtra("serviceid",serviceid);
                intent.putExtra("servicename",servicename);*/
                intent.putExtra("bcp",bcp);
                startActivity(intent);

            }
        });
        ArrayList<String> newarrlist = session.getSets("serviceid");
        if(!(newarrlist == null)) {
            for (int s = 0; s < newarrlist.size();s++){
                SecurityLayer.Log("Service Id",newarrlist.get(s));

            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();    //Call the back button's method
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    public void SetBillersStored(){
        planetsList.clear();
        try{
            String strservdata = session.getString(SessionManagement.KEY_BILLERS);
            JSONArray servdata = new JSONArray(strservdata);
            if(servdata.length() > 0){


                JSONObject json_data = null;
                for (int i = 0; i < servdata.length(); i++) {
                    json_data = servdata.getJSONObject(i);
                    //String accid = json_data.getString("benacid");


                    String id = json_data.optString("id");

                    String label = json_data.optString("label");
                    String serviceName = json_data.optString("serviceName");



                    planetsList.add( new GetServicesData(id,label,serviceName) );




                }
                if(!(getApplicationContext() == null)) {
                    Collections.sort(planetsList, new Comparator<GetServicesData>() {
                        public int compare(GetServicesData d1, GetServicesData d2) {
                            return d1.getServiceName().compareTo(d2.getServiceName());
                        }
                    });
                    aAdpt = new ServicesMenuAdapt(planetsList, BillMenuActivity.this);


                    lv.setAdapter(aAdpt);
                }


            }else{
                if(!(getApplicationContext() == null)) {
                    Toast.makeText(
                            getApplicationContext(),
                            "No services available  ",
                            Toast.LENGTH_LONG).show();
                }
            }
        } catch (JSONException e) {
            SecurityLayer.Log("encryptionJSONException", e.toString());
            // TODO Auto-generated catch block
            if(!(getApplicationContext() == null)) {
                Toast.makeText(getApplicationContext(), getApplicationContext().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
            }
            // SecurityLayer.Log(e.toString());

        }
    }


    public void SetPop(){
        planetsList.clear();
        prgDialog.show();
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        String usid = Utility.gettUtilUserId(getApplicationContext());
        String agentid = Utility.gettUtilAgentId(getApplicationContext());
        String mobnoo = Utility.gettUtilMobno(getApplicationContext());
        String params = CHANNEL_ID+usid;
        GetServv(params);

    }

    private void GetServv(String params) {

        String endpoint= "billpayment/services.action";


        String usid = Utility.gettUtilUserId(getApplicationContext());
        String agentid = Utility.gettUtilAgentId(getApplicationContext());




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


                    SecurityLayer.Log("Cable TV Resp", response.body());
                    SecurityLayer.Log("response..:", response.body());
                    JSONObject obj = new JSONObject(response.body());
                    //obj = Utility.onresp(obj,getApplicationContext());
                    obj = SecurityLayer.decryptTransaction(obj, getApplicationContext());
                    SecurityLayer.Log("decrypted_response", obj.toString());





                    JSONArray servdata = obj.optJSONArray("data");
                    //session.setString(SecurityLayer.KEY_APP_ID,appid);

                    if(!(response.body() == null)) {
                        String respcode = obj.optString("responseCode");
                        String responsemessage = obj.optString("message");

                        SecurityLayer.Log("Response Message", responsemessage);

                        if (Utility.isNotNull(respcode) && Utility.isNotNull(respcode)) {
                            if (!(Utility.checkUserLocked(respcode))) {
                                SecurityLayer.Log("Response Message", responsemessage);

                                if (respcode.equals("00")){
                                    SecurityLayer.Log("JSON Aray", servdata.toString());

                                    if(servdata.length() > 0){
                                        session.setString(SessionManagement.KEY_SETBILLERS, "Y");
                                        session.setString(SessionManagement.KEY_BILLERS, servdata.toString());

                                        JSONObject json_data = null;
                                        for (int i = 0; i < servdata.length(); i++) {
                                            json_data = servdata.getJSONObject(i);
                                            //String accid = json_data.getString("benacid");


                                            String id = json_data.optString("id");

                                            String label = json_data.optString("label");
                                            String serviceName = json_data.optString("serviceName");



                                            planetsList.add( new GetServicesData(id,label,serviceName) );




                                        }
                                        if(!(getApplicationContext() == null)) {
                                            Collections.sort(planetsList, new Comparator<GetServicesData>() {
                                                public int compare(GetServicesData d1, GetServicesData d2) {
                                                    return d1.getServiceName().compareTo(d2.getServiceName());
                                                }
                                            });
                                            aAdpt = new ServicesMenuAdapt(planetsList, BillMenuActivity.this);


                                            lv.setAdapter(aAdpt);
                                        }


                                    }else{
                                        if(!(getApplicationContext() == null)) {
                                            Toast.makeText(
                                                    getApplicationContext(),
                                                    "No services available  ",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    }

                                }else{
                                    if(!(getApplicationContext() == null)) {
                                        Toast.makeText(
                                                getApplicationContext(),
                                                "" + responsemessage,
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            } else {

                                LogOut();


                            }
                        } else {
                            if(!(getApplicationContext() == null)) {
                                Toast.makeText(
                                        getApplicationContext(),
                                        "There was an error on your request",
                                        Toast.LENGTH_LONG).show();

                            }
                        }
                    } else {
                        if(!(getApplicationContext() == null)) {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "There was an error on your request",
                                    Toast.LENGTH_LONG).show();
                        }

                    }
                    // prgDialog2.dismiss();




                } catch (JSONException e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    // TODO Auto-generated catch block
                    if(!(getApplicationContext() == null)) {
                        Toast.makeText(getApplicationContext(), getApplicationContext().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                        // SecurityLayer.Log(e.toString());
                       SetForceOutDialog(getString(R.string.forceout),getString(R.string.forceouterr),getApplicationContext());

                    }

                } catch (Exception e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    if(!(getApplicationContext() == null)) {
                       SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getApplicationContext());
                    }
                    // SecurityLayer.Log(e.toString());
                }
                try {
                    if ((prgDialog != null) && prgDialog.isShowing()) {
                        prgDialog.dismiss();
                    }
                } catch (final IllegalArgumentException e) {
                    // Handle or log or ignore
                } catch (final Exception e) {
                    // Handle or log or ignore
                } finally {

                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                // Log error here since request failed
                SecurityLayer.Log("throwable error",t.toString());

                if(!(getApplicationContext() == null)) {

                    try {
                        if ((prgDialog != null) && prgDialog.isShowing()) {
                            prgDialog.dismiss();
                        }
                    } catch (final IllegalArgumentException e) {
                        // Handle or log or ignore
                    } catch (final Exception e) {
                        // Handle or log or ignore
                    } finally {

                    }

                    Toast.makeText(
                            getApplicationContext(),
                            "There was an error on your request",
                            Toast.LENGTH_LONG).show();

                    SetForceOutDialog(getString(R.string.forceout),getString(R.string.forceouterr),getApplicationContext());

                }

            }
        });

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

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
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
}
