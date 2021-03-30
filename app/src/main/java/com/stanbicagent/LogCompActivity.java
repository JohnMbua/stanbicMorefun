package com.stanbicagent;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.ActionBar;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.stanbicagent.R;

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

public class LogCompActivity extends BaseActivity implements View.OnClickListener {
    ImageView imageView1;

    TextView tvdate;
    Button btn4,btnok;
    EditText edreason;
    ProgressDialog prgDialog, prgDialog2;
    public static final String DATEPICKER_TAG = "datepicker";
    String dateset = null;
    Spinner sp3;
    String stracno,stramo,strefno,strdatee;
    EditText edacno,edamo,edrefno,eddate;
    private Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_comp);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        //  mToolbar.setTitle("Inbox");
        setSupportActionBar(mToolbar);
        // Get the ActionBar here to configure the way it behaves.
        final ActionBar ab = getSupportActionBar();
        //ab.setHomeAsUpIndicator(R.drawable.ic_menu); // set a custom icon for the default home button
        ab.setDisplayShowHomeEnabled(true); // show or hide the default home button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
        ab.setDisplayShowTitleEnabled(false); // disable the default title element here (for centered title)


        sp3 = (Spinner) findViewById(R.id.spin3);

        prgDialog2 = new ProgressDialog(this);
        prgDialog2.setMessage("Loading....");
        prgDialog2.setCancelable(false);
        edacno = (EditText) findViewById(R.id.recacno);
        edamo = (EditText) findViewById(R.id.edamo);
        edrefno = (EditText) findViewById(R.id.edrefno);
        eddate = (EditText) findViewById(R.id.timestamp);
        edreason = (EditText) findViewById(R.id.edit_text);
        btn4 = (Button) findViewById(R.id.button2);
        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(
                getApplicationContext(), R.array.complaintty, android.R.layout.simple_spinner_item);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp3.setAdapter(adapter3);
        btn4.setOnClickListener(this);
        if(!(getIntent().getExtras() == null)) {
            stracno = getIntent().getExtras().getString("txaco");
            stramo = getIntent().getExtras().getString("txamo");
            strefno = getIntent().getExtras().getString("txref");
            strdatee = getIntent().getExtras().getString("txdate");
            edamo.setText(stramo);
            edacno.setText(stracno);
            edrefno.setText(strefno);
            eddate.setText(strdatee);

            eddate.setEnabled(false);
            edacno.setEnabled(false);
            edrefno.setEnabled(false);
            edamo.setEnabled(false);
        }
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.button2) {
            String reas = edreason.getText().toString();
            SecurityLayer.Log("Reason",reas);
            if(Utility.isNotNull(reas)){
                LogCompl(reas);
            }
        }
    }





    private void LogCompl(String desc) {

        if ((prgDialog2 != null)  && !(getApplicationContext() == null)) {
            prgDialog2.show();
// /visit/visitshop.action/{channel}/{userId}/{agentId}/{requestedVisitDate}/{reason} /visit/visitshop.action/1/SURESHD/123123213/05-02-2018/test visti "
            //   /complaint/log.action/{channel}/{userId}/{agentId}/{customerAccountNumber}/{amount}/{transactionDateTime}/{description}
            String endpoint = "complaint/log.action";


            String usid = Utility.gettUtilUserId(getApplicationContext());
            String agentid = Utility.gettUtilAgentId(getApplicationContext());
            String mobnoo = Utility.gettUtilMobno(getApplicationContext());
            String params = CHANNEL_ID + usid + "/" + agentid + "/" + stracno +"/"+stramo+"/"+strdatee+"/"+desc;
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
                        //obj = Utility.onresp(obj,getActivity());
                        obj = SecurityLayer.decryptTransaction(obj, getApplicationContext());
                        SecurityLayer.Log("decrypted_response", obj.toString());

                        String respcode = obj.optString("responseCode");
                        String responsemessage = obj.optString("message");




                        if (!(getApplicationContext() == null)) {
                            if (!(response.body() == null)) {
                                if (respcode.equals("00")) {

                                    SecurityLayer.Log("Response Message", responsemessage);
                                    if (!(getApplicationContext() == null)) {
                                        Toast.makeText(
                                                getApplicationContext(),
                                                "Complaint has been successfully logged ",
                                                Toast.LENGTH_LONG).show();
                                        prgDialog2.dismiss();

                                        finish();
                                        startActivity(new Intent(getApplicationContext(), FMobActivity.class));
                                       /* Fragment  fragment = new ViewAgentRequests();


                                        FragmentManager fragmentManager = getFragmentManager();
                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                        //  String tag = Integer.toString(title);
                                        fragmentTransaction.replace(R.id.container_body, fragment,"View Requests");
                                        fragmentTransaction.addToBackStack("View Requests");
                                        ((FMobActivity)getActivity())
                                                .setActionBarTitle("View Requests");
                                        fragmentTransaction.commit();*/
                                    }

                                } else {
                                    if (!(getApplicationContext() == null)) {
                                        Toast.makeText(
                                                getApplicationContext(),
                                                responsemessage,
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            } else {

                            }
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
                    try {
                        if((!(getApplicationContext() == null)) && !(prgDialog2 == null) && prgDialog2.isShowing()) {
                            prgDialog2.dismiss();
                        }
                    } catch (final IllegalArgumentException e) {
                        // Handle or log or ignore
                    } catch (final Exception e) {
                        // Handle or log or ignore
                    } finally {
                        //  prgDialog2 = null;
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
                    //   pDialog.dismiss();

                    try {
                        if((!(getApplicationContext() == null)) && !(prgDialog2 == null) && prgDialog2.isShowing()) {
                            prgDialog2.dismiss();
                        }
                    } catch (final IllegalArgumentException e) {
                        // Handle or log or ignore
                    } catch (final Exception e) {
                        // Handle or log or ignore
                    } finally {
                        //  prgDialog2 = null;
                    }

                }
            });
        }
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
