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

import adapter.OtherWalletsAdapt;
import model.GetWalletsData;
import rest.ApiInterface;
import rest.ApiSecurityClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import security.SecurityLayer;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class OtherWalletsActivity extends BaseActivity {
    ListView lv;
    OtherWalletsAdapt aAdpt;
    String bankname,bankcode;
    ProgressDialog prgDialog;
    List<GetWalletsData> planetsList = new ArrayList<GetWalletsData>();
    SessionManagement session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_wallets);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        // Get the ActionBar here to configure the way it behaves.
        final ActionBar ab = getSupportActionBar();
        //ab.setHomeAsUpIndicator(R.drawable.ic_menu); // set a custom icon for the default home button
        ab.setDisplayShowHomeEnabled(true); // show or hide the default home button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
        ab.setDisplayShowTitleEnabled(false); // disable the default title element here (for centered title)


        lv = (ListView)findViewById(R.id.lv);
        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Loading....");
        prgDialog.setCancelable(false);
        //  SetBanks();
        session = new SessionManagement(this);
        //  SetWalletsStored();

        String strservdata = session.getString(SessionManagement.KEY_WALLETS);
        if(!(strservdata == null)){
            JSONArray servdata = null;
            try {
                servdata = new JSONArray(strservdata);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(servdata.length() > 0) {
                SetWalletsStored();
            }else{
                GetServv();
            }
        }else {
            GetServv();
        }
//GetServv();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                String walletname = planetsList.get(position).getBankName();
                String walletcode = planetsList.get(position).getBankCode();
                /*Bundle b  = new Bundle();
                b.putString("walletname",walletname);
                b.putString("wallcode",walletcode);
                Fragment fragment = new SendOtherWallet();

                fragment.setArguments(b);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                //  String tag = Integer.toString(title);
                fragmentTransaction.replace(R.id.container_body, fragment,"Mobile Money Wallet");
                fragmentTransaction.addToBackStack("Mobile Money Wallet");
                ((FMobActivity)getApplicationContext())
                        .setActionBarTitle("Mobile Money Wallet");
                fragmentTransaction.commit();*/


                Intent intent  = new Intent(OtherWalletsActivity.this,SendOtherWalletActivity.class);

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("walletname",walletname);
                intent.putExtra("wallcode",walletcode);

                startActivity(intent);

            }
        });
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


    private void GetServv() {
        if ((prgDialog != null)) {
            prgDialog.show();
        }
        String endpoint= "transfer/getwallets.action";


        String usid = Utility.gettUtilUserId(getApplicationContext());
        String agentid = Utility.gettUtilAgentId(getApplicationContext());


        String params = usid+"/"+agentid+"/93939393";

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


                    SecurityLayer.Log("Get Wallets Resp", response.body());
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
                                        session.setString(SessionManagement.KEY_SETWALLETS,"Y");
                                        session.setString(SessionManagement.KEY_WALLETS,servdata.toString());

                                        JSONObject json_data = null;
                                        for (int i = 0; i < servdata.length(); i++) {
                                            json_data = servdata.getJSONObject(i);
                                            //String accid = json_data.getString("benacid");


                                            String instName = json_data.optString("instName");

                                            String bankCode = json_data.optString("bankCode");




                                            planetsList.add( new GetWalletsData(instName,bankCode) );




                                        }
                                        if(!(getApplicationContext() == null)) {
                                            SecurityLayer.Log("Get Wallets Data Name",planetsList.get(0).getBankName());
                                            Collections.sort(planetsList, new Comparator<GetWalletsData>(){
                                                public int compare(GetWalletsData d1, GetWalletsData d2){
                                                    return d1.getBankName().compareTo(d2.getBankName());
                                                }
                                            });
                                            aAdpt = new OtherWalletsAdapt(planetsList, OtherWalletsActivity.this);
                                            lv.setAdapter(aAdpt);
                                        }


                                    }else{
                                        if (!(getApplicationContext() == null)) {
                                            Toast.makeText(
                                                    getApplicationContext(),
                                                    "No services available  ",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    }

                                }else{
                                    if (!(getApplicationContext() == null)) {
                                        Toast.makeText(
                                                getApplicationContext(),
                                                "" + responsemessage,
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            } else {

                                startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                                Toast.makeText(
                                        getApplicationContext(),
                                        "You have been locked out of the app.Please call customer care for further details",
                                        Toast.LENGTH_LONG).show();
                                finish();

                            }
                        } else {
                            if (!(getApplicationContext() == null)) {
                                Toast.makeText(
                                        getApplicationContext(),
                                        "There was an error on your request",
                                        Toast.LENGTH_LONG).show();
                            }

                        }
                    } else {
                        if (!(getApplicationContext() == null)) {
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
                    Toast.makeText(getApplicationContext(), getApplicationContext().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                    // SecurityLayer.Log(e.toString());
                   SetForceOutDialog(getString(R.string.forceout),getString(R.string.forceouterr),getApplicationContext());


                } catch (Exception e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    SetForceOutDialog(getString(R.string.forceout),getString(R.string.forceouterr),getApplicationContext());

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
                    //prgDialog2 = null;
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                // Log error here since request failed
                SecurityLayer.Log("throwable error",t.toString());


                Toast.makeText(
                        getApplicationContext(),
                        "There was an error on your request",
                        Toast.LENGTH_LONG).show();



                try {
                    if ((prgDialog != null) && prgDialog.isShowing()) {
                        prgDialog.dismiss();
                    }
                } catch (final IllegalArgumentException e) {
                    // Handle or log or ignore
                } catch (final Exception e) {
                    // Handle or log or ignore
                } finally {
                    //prgDialog2 = null;
                }
                SetForceOutDialog(getString(R.string.forceout),getString(R.string.forceouterr),getApplicationContext());

            }
        });

    }

    public void SetWalletsStored(){
        planetsList.clear();
        try{
            String strservdata = session.getString(SessionManagement.KEY_WALLETS);
            JSONArray servdata = new JSONArray(strservdata);
            if(servdata.length() > 0){


                JSONObject json_data = null;
                for (int i = 0; i < servdata.length(); i++) {
                    json_data = servdata.getJSONObject(i);
                    //String accid = json_data.getString("benacid");


                    String instName = json_data.optString("instName");

                    String bankCode = json_data.optString("bankCode");




                    planetsList.add( new GetWalletsData(instName,bankCode) );




                }
                if(!(getApplicationContext() == null)) {
                    SecurityLayer.Log("Get Wallets Data Name",planetsList.get(0).getBankName());
                    Collections.sort(planetsList, new Comparator<GetWalletsData>(){
                        public int compare(GetWalletsData d1, GetWalletsData d2){
                            return d1.getBankName().compareTo(d2.getBankName());
                        }
                    });
                    aAdpt = new OtherWalletsAdapt(planetsList, OtherWalletsActivity.this);
                    lv.setAdapter(aAdpt);
                }


            }else{
                Toast.makeText(
                        getApplicationContext(),
                        "No services available  ",
                        Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            SecurityLayer.Log("encryptionJSONException", e.toString());
            // TODO Auto-generated catch block
            Toast.makeText(getApplicationContext(), getApplicationContext().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
            // SecurityLayer.Log(e.toString());

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
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            // Staring Login Activity
                            startActivity(i);

                        }
                    })
                    .show();
        }
    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub

        if(prgDialog!=null && prgDialog.isShowing()){

            prgDialog.dismiss();
        }
        super.onDestroy();
    }
}
