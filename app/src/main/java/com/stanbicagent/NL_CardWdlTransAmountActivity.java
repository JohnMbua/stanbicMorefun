package com.stanbicagent;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.stanbicagent.R;
import com.afollestad.materialdialogs.MaterialDialog;
import com.morefun.yapi.engine.DeviceServiceEngine;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class NL_CardWdlTransAmountActivity extends BaseActivity implements View.OnClickListener {
    ImageView imageView1;
    EditText txtamount;
    Button btnsub;
    SessionManagement session;
    ProgressDialog prgDialog;
    String depositid;
    RelativeLayout rlid;
    TextView step2;
    boolean isLightLed=false;
    DeviceServiceEngine mSDKManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nl_activity_cardwldtrans);
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

        rlid = (RelativeLayout) findViewById(R.id.rlid);
        step2 = (TextView) findViewById(R.id.tv2);
        step2.setOnClickListener(this);
        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Loading Account Details....");
        prgDialog.setCancelable(false);
        txtamount = (EditText) findViewById(R.id.amount);
        View.OnFocusChangeListener ofcListener = new MyFocusChangeListener();
        txtamount.setOnFocusChangeListener(ofcListener);
        btnsub = (Button) findViewById(R.id.button2);
        btnsub.setOnClickListener(this);

        mSDKManager = SDKManager.getInstance().getDeviceServiceEngine();
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        String lbluse = pref.getString("card_ttype", "");
        ApplicationClass.get().getGlobalVals().setTranType("CARD WITHDRAWAL");
    }

    private class MyFocusChangeListener implements View.OnFocusChangeListener {
        public void onFocusChange(View v, boolean hasFocus) {
            if (v.getId() == R.id.amount && !hasFocus) {
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                String txt = txtamount.getText().toString();
                String fbal = Utility.returnNumberFormat(txt);
                txtamount.setText(fbal);
            }
            if (v.getId() == R.id.ednarr && !hasFocus) {
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
            if (v.getId() == R.id.sendname && !hasFocus) {
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
            if (v.getId() == R.id.sendnumber && !hasFocus) {
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
            if (v.getId() == R.id.input_payacc && !hasFocus) {
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
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

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button4) {
            rlid.setVisibility(View.VISIBLE);
            //   checkInternetConnection2();
        }
        if (view.getId() == R.id.button2) {
            if (Utility.checkInternetConnection(this)) {
                final String amou = txtamount.getText().toString();
                if (Utility.isNotNull(amou)) {
                    String nwamo = amou.replace(",", "");
                    SharedPreferences pref = PreferenceManager
                            .getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("txn_amount", nwamo);
                    editor.commit();
                    double txamou = Double.parseDouble(nwamo);

                    //hapa
                    /*isLightLed = true;
                    try {
                        mSDKManager.getLEDDriver().PowerLed(isLightLed, isLightLed, isLightLed, isLightLed);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }*/
                    Intent intent = new Intent(NL_CardWdlTransAmountActivity.this, NL_Card_Wdl_InsCard.class);
                    intent.putExtra("amou", amou);
                    intent.putExtra("trantype", "CW");
                    startActivity(intent);
                } else {
                    Toast.makeText(
                            getApplicationContext(),
                            "Please enter a valid value for Amount",
                            Toast.LENGTH_LONG).show();
                }
            }
        }
        if (view.getId() == R.id.tv2) {
            finish();
            Intent intent = new Intent(NL_CardWdlTransAmountActivity.this, FTMenuActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    public void SetDialog(String msg, String title) {
        new MaterialDialog.Builder(getApplicationContext())
                .title(title)
                .content(msg)
                .negativeText("Close")
                .show();
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
