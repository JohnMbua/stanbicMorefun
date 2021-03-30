package com.stanbicagent;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.borax12.materialdaterangepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.stanbicagent.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import adapter.NewMinListAdapter;
import fragments.DateRangePickerFragment;
import model.MinistatData;
import rest.ApiClient;
import rest.ApiInterface;
import rest.ApiSecurityClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import security.SecurityLayer;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MinistatActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener, View.OnClickListener, DateRangePickerFragment.OnDateRangeSelectedListener {
    private Toolbar mToolbar;
    Button signup;
    // Spinner sp1;
    List<String> acc = new ArrayList<String>();
    static Hashtable<String, String> data1;
    public static ArrayList<String> instidacc = new ArrayList<String>();
    String paramdata = "";
    ArrayAdapter<String> mArrayAdapter;
    ArrayList<String> phoneContactList = new ArrayList<String>();
    List<MinistatData> planetsList = new ArrayList<MinistatData>();
    private TextView emptyView;
    NewMinListAdapter aAdpt;
    ListView lv;
    Button ok;
    String selacc;
    ProgressDialog prgDialog2;
    TextView acno, txaco, txaccbal;
    EditText accno, mobno, fnam;
    SessionManagement session;
    Button calendar;
    LinearLayout lstmt;

    TextView txtitle, txfrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ministat);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        //  mToolbar.setTitle("Inbox");
        setSupportActionBar(mToolbar);
        final ActionBar ab = getSupportActionBar();
        //ab.setHomeAsUpIndicator(R.drawable.ic_menu); // set a custom icon for the default home button
        ab.setDisplayShowHomeEnabled(true); // show or hide the default home button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
        ab.setDisplayShowTitleEnabled(false); // disable the default title element here (for centered title)

        txtitle = (TextView) findViewById(R.id.enddate);
        txfrom = (TextView) findViewById(R.id.from);
        lv = (ListView) findViewById(R.id.lv);
        txaco = (TextView) findViewById(R.id.bname);
        txaccbal = (TextView) findViewById(R.id.accountbalance);
        lstmt = (LinearLayout) findViewById(R.id.stmtrlyy);
        lstmt.setOnClickListener(this);
        //   sp1 = (Spinner) rootView.findViewById(R.id.accno);
        prgDialog2 = new ProgressDialog(this);
        prgDialog2.setMessage("Loading ....");
        // Set Cancelable as False
        session = new SessionManagement(this);
        calendar = (Button) findViewById(R.id.button4);
        calendar.setOnClickListener(this);
        prgDialog2.setCancelable(false);
        emptyView = (TextView) findViewById(R.id.empty_view);
        String accnoo = Utility.getAcountno(this);
        txaco.setText("Statement for Account Number -" + accnoo);
        setBalInquSec();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void setBalInquSec() {
        if ((prgDialog2 != null) && !(getApplicationContext() == null) && !(MinistatActivity.this.isFinishing())) {
            prgDialog2.show();
            String endpoint = "core/balenquirey.action";
            String usid = Utility.gettUtilUserId(getApplicationContext());
            String agentid = Utility.gettUtilAgentId(getApplicationContext());
            String mobnoo = Utility.gettUtilMobno(getApplicationContext());
            String params = ApplicationConstants.CHANNEL_ID + usid + "/" + agentid + "/" + mobnoo;
            String urlparams = "";
            try {
                urlparams = SecurityLayer.genURLCBC(params, endpoint, getApplicationContext());
                SecurityLayer.Log("RefURL", urlparams);
                SecurityLayer.Log("refurl", urlparams);
                SecurityLayer.Log("params", params);
            } catch (Exception e) {
                SecurityLayer.Log("encryptionerror", e.toString());
            }
            ApiInterface apiService =
                    ApiSecurityClient.getClient(this).create(ApiInterface.class);
            Call<String> call = apiService.setGenericRequestRaw(urlparams);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {
                        SecurityLayer.Log("response..:", response.body());
                        JSONObject obj = new JSONObject(response.body());
                        obj = SecurityLayer.decryptTransaction(obj, getApplicationContext());
                        SecurityLayer.Log("decrypted_response", obj.toString());
                        String respcode = obj.optString("responseCode");
                        String responsemessage = obj.optString("message");
                        JSONObject plan = obj.optJSONObject("data");
                        JSONArray balances = plan.optJSONArray("balances");
                        if (!(getApplicationContext() == null)) {
                            if (!(response.body() == null)) {
                                if (respcode.equals("00")) {
                                    SecurityLayer.Log("Response Message", responsemessage);
                                    if (!(balances == null)) {
                                        JSONObject jsacbal = balances.getJSONObject(0);
                                        JSONObject jscomm = balances.getJSONObject(1);
                                        String balamo = jsacbal.optString("Balance");
                                        String comamo = jscomm.optString("Balance");
                                        String cmbal = Utility.returnNumberFormat(comamo);
                                        cmbal = Utility.roundto2dp(cmbal);
                                        String fbal = Utility.returnNumberFormat(balamo);
                                        txaccbal.setText("Account Balance: " + "KES " + fbal);
                                    } else {
                                        if (!(getApplicationContext() == null)) {
                                            Toast.makeText(
                                                    getApplicationContext(),
                                                    "There was an error retrieving your balance ",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    }
                                } else {
                                    if (!(getApplicationContext() == null)) {
                                        Toast.makeText(
                                                getApplicationContext(),
                                                "There was an error retrieving your balance ",
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            } else {
                                if (!(getApplicationContext() == null)) {
                                    Toast.makeText(
                                            getApplicationContext(),
                                            "There was an error retrieving your balance",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    } catch (JSONException e) {
                        SecurityLayer.Log("encryptionJSONException", e.toString());
                        Toast.makeText(getApplicationContext(), getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                        SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getApplicationContext());
                    } catch (Exception e) {
                        SecurityLayer.Log("encryptionJSONException", e.toString());
                        SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getApplicationContext());
                    }
                    if (!(getApplicationContext() == null)) {
                        if (Utility.checkInternetConnection(getApplicationContext())) {
                            SimpleDateFormat format2 = new SimpleDateFormat("" +
                                    "MMMM dd yyyy");
                            Calendar cal = Calendar.getInstance();
                            SimpleDateFormat format1 = new SimpleDateFormat("" +
                                    "yyyy-MM-dd");
                            String formattednow = format1.format(cal.getTime());
                            Calendar now = Calendar.getInstance();
                            int year = now.get(Calendar.YEAR);
                            int month = now.get(Calendar.MONTH); // Note: zero based!
                            int day = now.get(Calendar.DAY_OF_MONTH);
                            now.set(year, month, 01);
                            System.out.println(cal.getTime());
                            String formattednowa = format1.format(cal.getTime());
                            String formattedstartdate = format1.format(now.getTime());
                            SecurityLayer.Log("Formatet Date", formattedstartdate);
                            String formatteduserend = format2.format(cal.getTime());
                            String formatteduserstart = format2.format(now.getTime());
                            txtitle.setText(formatteduserend);
                            txfrom.setText(formatteduserstart);
                            month = month + 1;
                            String frmdymonth = Integer.toString(day);
                            if (day < 10) {
                                frmdymonth = "0" + frmdymonth;
                            }
                            String frmyear = Integer.toString(year);
                            //frmyear = frmyear.substring(2, 4);
                            String tdate = frmdymonth + "-" + (month) + "-" + frmyear;
                            String firdate = "01" + "-" + (month) + "-" + frmyear;
                            Calendar calfrom = Calendar.getInstance();
                            calfrom.set(year, month, 1);
                            SetMinist(formattedstartdate, formattednow);
                        }
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
                        if ((!(getApplicationContext() == null)) && !(prgDialog2 == null) && prgDialog2.isShowing() && !(MinistatActivity.this.isFinishing())) {
                            prgDialog2.dismiss();
                        }
                    } catch (final IllegalArgumentException e) {
                        // Handle or log or ignore
                    } catch (final Exception e) {
                        // Handle or log or ignore
                    } finally {
                        //  prgDialog2 = null;
                    }
                    SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getApplicationContext());
                }
            });
        }
    }

    public void SetMinist(String firdate, String enddate) {
        if (!(aAdpt == null)) {
            aAdpt.clear();
            aAdpt.notifyDataSetChanged();
        }
        if ((prgDialog2 != null) && !(getApplicationContext() == null) && (!(MinistatActivity.this.isFinishing()))) {
            prgDialog2.show();
            ApiInterface apiService =
                    ApiClient.getClient().create(ApiInterface.class);
            String usid = Utility.gettUtilUserId(getApplicationContext());
            String params = ApplicationConstants.CHANNEL_ID + usid + "/5";
            MiniStmtt(params);
        }
    }

    private void MiniStmtt(String params) {
        if (!(getApplicationContext() == null)) {
            String endpoint = "core/agentStmt.action";
            String urlparams = "";
            try {
                urlparams = SecurityLayer.genURLCBC(params, endpoint, getApplicationContext());
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
                        SecurityLayer.Log("CMiniStmtt", response.body());
                        SecurityLayer.Log("response..:", response.body());
                        JSONObject obj = new JSONObject(response.body());
                        //obj = Utility.onresp(obj,getApplicationContext());
                        obj = SecurityLayer.decryptTransaction(obj, getApplicationContext());
                        SecurityLayer.Log("decrypted_response", obj.toString());
                        JSONArray comperf = obj.optJSONArray("data");
                        SecurityLayer.Log("data minist", comperf.getJSONObject(0).optString("date"));
                        //session.setString(SecurityLayer.KEY_APP_ID,appid);

                        if (!(response.body() == null)) {
                            String respcode = obj.optString("responseCode");
                            String responsemessage = obj.optString("message");

                            SecurityLayer.Log("Response Message", responsemessage);

                            if (Utility.isNotNull(respcode) && Utility.isNotNull(respcode)) {
                                if (!(Utility.checkUserLocked(respcode))) {
                                    SecurityLayer.Log("Response Message", responsemessage);

                                    if (respcode.equals("00")) {
                                        SecurityLayer.Log("JSON Aray", comperf.toString());
                                        if (comperf.length() > 0) {
                                            planetsList.clear();

                                            JSONObject json_data = null;
                                            for (int i = 0; i < comperf.length(); i++) {
                                                json_data = comperf.getJSONObject(i);
                                                //String accid = json_data.getString("benacid");


                                                String tran_date = json_data.optString("Datetime");

                                                String tran_remark = json_data.optString("Narration");
                                                String credit_debit = "Credit";
                                                String tran_amt = String.valueOf(Utility.returnOldNumberFormat(json_data.optString("Amount")));

                                                Double dbamo = Double.parseDouble(tran_amt);
                                                if (dbamo < 0) {
                                                    credit_debit = "Debit";
                                                }

                                                planetsList.add(new MinistatData(tran_date, tran_remark, credit_debit, tran_amt));


                                            }
                                            if (!(getApplicationContext() == null)) {
                                                aAdpt = new NewMinListAdapter(planetsList, MinistatActivity.this);


                                                lv.setAdapter(aAdpt);
                                            }


                                        } else {
                                            if (!(getApplicationContext() == null)) {
                                                Toast.makeText(
                                                        getApplicationContext(),
                                                        "There are no records to display",
                                                        Toast.LENGTH_LONG).show();
                                            }
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
                                    finish();
                                    startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                                    Toast.makeText(
                                            getApplicationContext(),
                                            "You have been locked out of the app.Please call customer care for further details",
                                            Toast.LENGTH_LONG).show();


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
                        SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getApplicationContext());


                    } catch (Exception e) {
                        SecurityLayer.Log("encryptionJSONException", e.toString());
                        SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getApplicationContext());

                        // SecurityLayer.Log(e.toString());
                    }
                    if ((prgDialog2 != null) && prgDialog2.isShowing() && !(getApplicationContext() == null) && !(MinistatActivity.this.isFinishing())) {
                        prgDialog2.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    // Log error here since request failed
                    // Log error here since request failed
                    SecurityLayer.Log("throwable error", t.toString());


                    Toast.makeText(
                            getApplicationContext(),
                            "There was an error on your request",
                            Toast.LENGTH_LONG).show();


                    if ((prgDialog2 != null) && prgDialog2.isShowing() && !(getApplicationContext() == null) && !(MinistatActivity.this.isFinishing())) {
                        prgDialog2.dismiss();
                    }
                    SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getApplicationContext());

                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button4) {
            /*Calendar now = Calendar.getInstance();
            DatePickerDialog dpd = com.borax12.materialdaterangepicker.date.DatePickerDialog.newInstance(
                    this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );
            dpd.setMaxDate(now);
            dpd.show(getFragmentManager(), "Datepickerdialog");*/


            DateRangePickerFragment dateRangePickerFragment = DateRangePickerFragment.newInstance(this, false);
            dateRangePickerFragment.show(getSupportFragmentManager(), "datePicker");
        }
        if (view.getId() == R.id.stmtrlyy) {

        }
    }

    private void dismissProgressDialog() {
        if (prgDialog2 != null && prgDialog2.isShowing()) {
            prgDialog2.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        dismissProgressDialog();
        super.onDestroy();

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth, int yearEnd, int monthOfYearEnd, int dayOfMonthEnd) {
        String date = "From- " + dayOfMonth + "/" + (++monthOfYear) + "/" + year + " To " + dayOfMonthEnd + "/" + (++monthOfYearEnd) + "/" + yearEnd;
        //txtitle.setText(date);
        Calendar calfrom = Calendar.getInstance();
        Calendar calto = Calendar.getInstance();
        calto.set(yearEnd, monthOfYearEnd, dayOfMonthEnd);
        calfrom.set(year, monthOfYear, dayOfMonth);
        String strmnthyearend = null;
        if (calfrom.before(calto)) {
            //   fromdate.setText(date);
            String frmdymonth = Integer.toString(dayOfMonth);
            String strmnthyear = Integer.toString(monthOfYear);
            strmnthyearend = Integer.toString(monthOfYearEnd);
            if (dayOfMonth < 10) {
                frmdymonth = "0" + frmdymonth;
            }
            if (monthOfYear < 10) {
                strmnthyear = "0" + strmnthyear;
            }
            if (monthOfYearEnd < 10) {
                strmnthyearend = "0" + strmnthyearend;
            }
            String frmyear = Integer.toString(year);
            frmyear = frmyear.substring(0, 4);

            String fromd = frmyear + "-" + strmnthyear + "-" + frmdymonth;
            String frmenddymonth = Integer.toString(dayOfMonthEnd);
            if (dayOfMonthEnd < 10) {
                frmenddymonth = "0" + frmenddymonth;
            }

            String frmendyr = Integer.toString(yearEnd);
            frmendyr = frmendyr.substring(0, 4);
            String endd = frmendyr + "-" + strmnthyearend + "-" + frmenddymonth;
            SetMinist(fromd, endd);
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    "Please ensure the from date is before the after date",
                    Toast.LENGTH_LONG).show();
        }
    }


    public void SetForceOutDialog(String msg, final String title, final Context c) {
        if (!(c == null)) {
            new MaterialDialog.Builder(MinistatActivity.this)
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
    public void onPause() {

        super.onPause();

        long secs = (new Date().getTime()) / 1000;
        SecurityLayer.Log("Seconds Loged", Long.toString(secs));
        session.putCurrTime(secs);
    }


    @Override
    public void onDateRangeSelected(int dayOfMonth, int monthOfYear, int year, int dayOfMonthEnd, int monthOfYearEnd, int yearEnd) {
        // String date = "From- " + dayOfMonth + "/" + (++monthOfYear) + "/" + year + " To " + dayOfMonthEnd + "/" + (++monthOfYearEnd) + "/" + yearEnd;
        //txtitle.setText(date);

        Calendar clfrom = Calendar.getInstance();
        Calendar clto = Calendar.getInstance();
        clfrom.set(year, monthOfYear, dayOfMonth);
        clto.set(yearEnd, monthOfYearEnd, dayOfMonthEnd);
        SimpleDateFormat format2 = new SimpleDateFormat("" +
                "MMMM dd yyyy");
        String formattedfrom = format2.format(clfrom.getTime());
        String formattedto = format2.format(clto.getTime());
        txtitle.setText(formattedto);
        txfrom.setText(formattedfrom);
        ++monthOfYear;
        ++monthOfYearEnd;
        Calendar calfrom = Calendar.getInstance();
        Calendar calto = Calendar.getInstance();
        calto.set(yearEnd, monthOfYearEnd, dayOfMonthEnd);
        calfrom.set(year, monthOfYear, dayOfMonth);
        String strmnthyearend = null;
        if (calfrom.before(calto)) {
            //   fromdate.setText(date);
            String frmdymonth = Integer.toString(dayOfMonth);
            String strmnthyear = Integer.toString(monthOfYear);
            strmnthyearend = Integer.toString(monthOfYearEnd);
            if (dayOfMonth < 10) {
                frmdymonth = "0" + frmdymonth;
            }
            if (monthOfYear < 10) {
                strmnthyear = "0" + strmnthyear;
            }
            if (monthOfYearEnd < 10) {
                strmnthyearend = "0" + strmnthyearend;
            }
            String frmyear = Integer.toString(year);
            frmyear = frmyear.substring(0, 4);

            String fromd = frmyear + "-" + strmnthyear + "-" + frmdymonth;
            String frmenddymonth = Integer.toString(dayOfMonthEnd);
            if (dayOfMonthEnd < 10) {
                frmenddymonth = "0" + frmenddymonth;
            }

            String frmendyr = Integer.toString(yearEnd);
            frmendyr = frmendyr.substring(0, 4);
            String endd = frmendyr + "-" + strmnthyearend + "-" + frmenddymonth;
            SetMinist(fromd, endd);
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    "Please ensure the from date is before the after date",
                    Toast.LENGTH_LONG).show();
        }
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
