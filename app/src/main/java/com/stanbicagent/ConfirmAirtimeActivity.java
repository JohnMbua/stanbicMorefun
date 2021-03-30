package com.stanbicagent;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.stanbicagent.R;
import com.afollestad.materialdialogs.MaterialDialog;

import java.io.InputStream;
import java.io.OutputStream;

import model.GetFee;
import rest.ApiClient;
import rest.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import security.SecurityLayer;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.stanbicagent.ApplicationConstants.CHANNEL_ID;

public class ConfirmAirtimeActivity extends BaseActivity implements View.OnClickListener {
    TextView reccustid, recamo, rectelco, step2, txtfee, acbal;
    Button btnsub;
    String txtcustid, amou, narra, ednamee, ednumbb, serviceid, billid, agbalance;
    ProgressDialog prgDialog, prgDialog2;
    String telcoop;
    EditText amon, edacc, pno, txtamount, txtnarr, edname, ednumber;
    EditText etpin;
    public static final String KEY_TOKEN = "token";
    SessionManagement session;

    String finalrespfee;
    // android built in classes for bluetooth operations
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;

    // needed for communication to bluetooth device / network
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;

    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_airtime);
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
        reccustid = (TextView) findViewById(R.id.textViewnb2);
        etpin = (EditText) findViewById(R.id.pin);
        acbal = (TextView) findViewById(R.id.txtacbal);
        recamo = (TextView) findViewById(R.id.textViewrrs);
        rectelco = (TextView) findViewById(R.id.textViewrr);
        txtfee = (TextView) findViewById(R.id.txtfee);

        prgDialog2 = new ProgressDialog(this);
        prgDialog2.setMessage("Loading....");
        prgDialog2.setCancelable(false);


        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Loading....");
        prgDialog.setCancelable(false);
        step2 = (TextView) findViewById(R.id.tv);
        step2.setOnClickListener(this);
        txtfee = (TextView) findViewById(R.id.txtfee);
        btnsub = (Button) findViewById(R.id.button2);
        btnsub.setOnClickListener(this);

        Intent intent = getIntent();
        if (intent != null) {


            txtcustid = intent.getStringExtra("mobno");
            amou = intent.getStringExtra("amou");
            telcoop = intent.getStringExtra("telcoop");
            String newamo = amou.replace(",", "");
            String txtamou = Utility.returnNumberFormat(newamo);
            if (txtamou.equals("0.00")) {
                txtamou = amou;
            }
            billid = intent.getStringExtra("billid");
            serviceid = intent.getStringExtra("serviceid");
            reccustid.setText(txtcustid);


            recamo.setText(ApplicationConstants.KEY_NAIRA + txtamou);
            rectelco.setText(telcoop);
            amou = Utility.convertProperNumber(amou);


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

    public void getFee() {
        if ((prgDialog2 != null)) {
            prgDialog2.show();
        }
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        String usid = Utility.gettUtilUserId(getApplicationContext());
        String agentid = Utility.gettUtilAgentId(getApplicationContext());
        Call<GetFee> call = apiService.getFee("1", usid, agentid, "MMO", amou);
        call.enqueue(new Callback<GetFee>() {
            @Override
            public void onResponse(Call<GetFee> call, Response<GetFee> response) {
                if (!(response.body() == null)) {
                    String responsemessage = response.body().getMessage();
                    String respfee = response.body().getFee();
                    SecurityLayer.Log("Response Message", responsemessage);
                    if (respfee == null || respfee.equals("")) {
                        txtfee.setText("N/A");
                    } else {
                        respfee = Utility.returnNumberFormat(respfee);
                        finalrespfee = ApplicationConstants.KEY_NAIRA + respfee;
                        txtfee.setText(ApplicationConstants.KEY_NAIRA + respfee);
                    }
                    if ((!(getApplicationContext() == null)) && (prgDialog2 != null) && prgDialog2.isShowing()) {
                        prgDialog2.dismiss();
                    }
                } else {
                    txtfee.setText("N/A");
                }
            }

            @Override
            public void onFailure(Call<GetFee> call, Throwable t) {
                // Log error here since request failed
                SecurityLayer.Log("Throwable error", t.toString());
                Toast.makeText(
                        getApplicationContext(),
                        "There was an error processing your request",
                        Toast.LENGTH_LONG).show();
                if (!(getApplicationContext() == null) && (prgDialog2 != null) && prgDialog2.isShowing()) {
                    prgDialog2.dismiss();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.button2) {
            //      txtcustid = Utility.convertMobNumber(txtcustid);
            if (Utility.checkInternetConnection(getApplicationContext())) {
                String agpin = etpin.getText().toString();
                if (Utility.isNotNull(txtcustid)) {
                    if (Utility.isNotNull(amou)) {
                        if (Utility.isNotNull(agpin)) {
                            /*double dbamo = Double.parseDouble(amou);
                            Double dbagbal = Double.parseDouble(agbalance);
                            if(dbamo <= dbagbal){*/
                            String encrypted = null;

                            encrypted = Utility.b64_sha256(agpin);


                            ApiInterface apiService =
                                    ApiClient.getClient().create(ApiInterface.class);

                            String usid = Utility.gettUtilUserId(getApplicationContext());
                            String agentid = Utility.gettUtilAgentId(getApplicationContext());
//                            final String mobnoo = "0" + Utility.gettUtilMobno(getApplicationContext());
                            String emaill = Utility.gettUtilEmail(getApplicationContext());
//                            String params = CHANNEL_ID + usid + "/" + billid + "/" + amou + "/" + txtcustid;

                            String mobnoo = "0" + Utility.gettUtilMobno(getApplicationContext());
                            String channelref = Long.toString(System.nanoTime());
                            String params = ApplicationConstants.CHANNEL_ID + usid + "/" + billid + "/" + amou + "/" + txtcustid + "/" + encrypted + "/" + channelref + "/" + mobnoo;

                            Intent intent = new Intent(ConfirmAirtimeActivity.this, TransactionProcessingActivity.class);


                            intent.putExtra("mobno", txtcustid);
                            intent.putExtra("amou", amou);
                            intent.putExtra("telcoop", telcoop);

                            intent.putExtra("res", billid);
                            intent.putExtra("billid", billid);
                            intent.putExtra("serviceid", serviceid);
                            intent.putExtra("txpin", encrypted);
                            intent.putExtra("serv", "AIRT");
                            intent.putExtra("params", params);


                            startActivity(intent);
                           /* Fragment fragment = new TransactingProcessing();

                            fragment.setArguments(b);
                            FragmentManager fragmentManager = getFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            //  String tag = Integer.toString(title);
                            fragmentTransaction.replace(R.id.container_body, fragment, "Final Conf Airtime");
                            fragmentTransaction.addToBackStack("Final Conf");
                            ((FMobActivity) getApplicationContext())
                                    .setActionBarTitle("Final Conf Airtime");
                            fragmentTransaction.commit();*/
                            //   AirtimeResp(params);


                            ClearPin();
                           /* }  else {
                                Toast.makeText(
                                        getApplicationContext(),
                                        "The amount set is higher than your agent balance",
                                        Toast.LENGTH_LONG).show();
                            }*/
                        } else {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Please enter a valid value for Agent PIN",
                                    Toast.LENGTH_LONG).show();

                        }
                    } else {
                        Toast.makeText(
                                getApplicationContext(),
                                "Please enter a valid value for Amount",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(
                            getApplicationContext(),
                            "Please enter a value for Customer ID",
                            Toast.LENGTH_LONG).show();
                }
            }
        }

        if (view.getId() == R.id.tv) {
           /* Fragment fragment = new AirtimeTransf();


            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment, "Airtime");
            fragmentTransaction.addToBackStack("Airtime");
            ((FMobActivity) getApplicationContext())
                    .setActionBarTitle("Airtime");
            fragmentTransaction.commit();*/

            finish();


            Intent intent = new Intent(ConfirmAirtimeActivity.this, AirtimeTransfActivity.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        // put your code here...

    }

    public void ClearPin() {
        etpin.setText("");
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


    private void dismissProgressDialog() {
        if (prgDialog != null && prgDialog.isShowing()) {
            prgDialog.dismiss();
        }
        if (prgDialog2 != null && prgDialog2.isShowing()) {

            prgDialog2.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        dismissProgressDialog();
        super.onDestroy();
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
