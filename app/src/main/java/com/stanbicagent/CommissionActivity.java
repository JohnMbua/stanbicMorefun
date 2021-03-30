package com.stanbicagent;

import com.stanbicagent.R;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import adapter.NewCommListAdapter;
import fragments.DateRangePickerFragment;
import model.GetCommPerfData;
import rest.ApiClient;
import rest.ApiInterface;
import rest.ApiSecurityClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import security.SecurityLayer;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CommissionActivity extends BaseActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener, DateRangePickerFragment.OnDateRangeSelectedListener {
    Button signup;
    // Spinner sp1;
    List<String> acc = new ArrayList<String>();
    List<GetCommPerfData> temp = new ArrayList<GetCommPerfData>();
    static Hashtable<String, String> data1;
    public static ArrayList<String> instidacc = new ArrayList<String>();
    String paramdata = "";
    ArrayAdapter<String> mArrayAdapter;
    ArrayList<String> phoneContactList = new ArrayList<String>();
    List<GetCommPerfData> planetsList = new ArrayList<GetCommPerfData>();
    private TextView emptyView;
    NewCommListAdapter aAdpt;
    ListView lv;
    Button ok;
    String selacc;
    ProgressDialog prgDialog2;
    TextView acno, txtitle, txcomrepo, txtcomrepo, commamo, txfrom;
    EditText accno, mobno, fnam;
    SessionManagement session;
    Button calendar;
    LinearLayout lstmt;
    private Toolbar mToolbar;

    SimpleDateFormat format1 = new SimpleDateFormat("" +
            "MMMM dd yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commission);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        final ActionBar ab = getSupportActionBar();
        //ab.setHomeAsUpIndicator(R.drawable.ic_menu); // set a custom icon for the default home button
        ab.setDisplayShowHomeEnabled(true); // show or hide the default home button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
        ab.setDisplayShowTitleEnabled(false); // disable the default title element here (for centered title)
        lv = (ListView) findViewById(R.id.lv);
        txtitle = (TextView) findViewById(R.id.bname);
        txcomrepo = (TextView) findViewById(R.id.textViewweryu);
        txtcomrepo = (TextView) findViewById(R.id.textViewwaq);
        commamo = (TextView) findViewById(R.id.txtagcomm);
        txtitle = (TextView) findViewById(R.id.bname);
        txfrom = (TextView) findViewById(R.id.from);
        //   sp1 = (Spinner) rootView.findViewById(R.id.accno);
        prgDialog2 = new ProgressDialog(this);
        prgDialog2.setMessage("Loading ....");
        lstmt = (LinearLayout) findViewById(R.id.stmtly);
        lstmt.setOnClickListener(this);
        // Set Cancelable as False
        session = new SessionManagement(this);
        calendar = (Button) findViewById(R.id.button4);
        calendar.setOnClickListener(this);
        prgDialog2.setCancelable(false);
        emptyView = (TextView) findViewById(R.id.empty_view);
        String usid = Utility.gettUtilUserId(this);
        txtcomrepo.setText("Commission By User " + usid + "");
        Calendar cal = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH); // Note: zero based!
        int day = now.get(Calendar.DAY_OF_MONTH);

        now.set(year, month, 01);

        System.out.println(cal.getTime());
// Output "Wed Sep 26 14:23:28 EST 2012"

        String formattednow = format1.format(cal.getTime());
        String formattedstartdate = format1.format(now.getTime());
// Output "2012-09-26"
        //   txtitle.setText("Commission Report for "+formattedstartdate+" to "+formattednow);

        txtitle.setText(formattednow);
        txfrom.setText(formattedstartdate);
        setBalInquSec();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                GetCommPerfData p = planetsList.get(position);
                String txncode = p.getTxnCode();
                String toAcnum = p.gettoAcNum();
                String fromacnum = p.getFromAcnum();
                String txtrfno = p.getrefNumber();
                String narr = fromacnum;
                String nartor = toAcnum;
                String servtype = Utility.convertTxnCodetoServ(txncode);
                String txtdatetime = p.getTxndateTime();

                double dbagcsmn = p.getAgentCmsn();
                String agcsmn = Double.toString(dbagcsmn);
                String fbal = Utility.returnNumberFormat(agcsmn);
                String amoo = ApplicationConstants.KEY_NAIRA + fbal;
                FragmentManager fm = getSupportFragmentManager();
                CommReceipt editNameDialog = new CommReceipt();
                Bundle bundle = new Bundle();
                bundle.putString("narr", narr);
                bundle.putString("narrtor", nartor);
                bundle.putString("refno", txtrfno);
                bundle.putString("datetime", txtdatetime);
                bundle.putString("servtype", servtype);
                bundle.putString("amo", amoo);
                editNameDialog.setArguments(bundle);
                editNameDialog.show(fm, "fragment_edit_name");
            }
        });


    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void setBalInquSec() {
        if ((prgDialog2 != null) && !(getApplicationContext() == null) && (!(CommissionActivity.this.isFinishing()))) {
            prgDialog2.show();
        }
        String endpoint = "core/balenquirey.action";
        String usid = Utility.gettUtilUserId(this);
        String agentid = Utility.gettUtilAgentId(this);
        String mobnoo = Utility.gettUtilMobno(this);
        String params = ApplicationConstants.CHANNEL_ID + usid + "/" + agentid + "/" + mobnoo;
        String urlparams = "";
        try {
            urlparams = SecurityLayer.genURLCBC(params, endpoint, this);
            //SecurityLayer.Log("cbcurl",url);
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
                    obj = SecurityLayer.decryptTransaction(obj, CommissionActivity.this);
                    SecurityLayer.Log("decrypted_response", obj.toString());
                    String respcode = obj.optString("responseCode");
                    String responsemessage = obj.optString("message");
                    JSONObject plan = obj.optJSONObject("data");
                    JSONArray balances = plan.optJSONArray("balances");
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
                                commamo.setText("Account Balance: KES" + " " + fbal);
                            } else {
                                Toast.makeText(
                                        getApplicationContext(),
                                        "There was an error retrieving your balance ",
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "There was an error retrieving your balance ",
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(
                                getApplicationContext(),
                                "There was an error retrieving your balance ",
                                Toast.LENGTH_LONG).show();
                    }


                } catch (JSONException e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    // TODO Auto-generated catch block
                    Toast.makeText(getApplicationContext(), getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                    // SecurityLayer.Log(e.toString());

                } catch (Exception e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    // SecurityLayer.Log(e.toString());
                }
                if (!(getApplicationContext() == null)) {
                    if (Utility.checkInternetConnection(getApplicationContext())) {

                        Calendar now = Calendar.getInstance();
                        int year = now.get(Calendar.YEAR);
                        int month = now.get(Calendar.MONTH); // Note: zero based!
                        int day = now.get(Calendar.DAY_OF_MONTH);

                        month = month + 1;

                        String frmdymonth = Integer.toString(day);
                        if (day < 10) {
                            frmdymonth = "0" + frmdymonth;
                        }

                        String frmdymonthyr = Integer.toString(month);
                        if (month < 10) {
                            frmdymonthyr = "0" + frmdymonthyr;
                        }
                        String frmyear = Integer.toString(year);
                        String tdate = frmdymonth + "-" + (frmdymonthyr) + "-" + frmyear;
                        String firdate = "01" + "-" + (frmdymonthyr) + "-" + frmyear;
                        Calendar calfrom = Calendar.getInstance();
                        calfrom.set(year, month, 1);
                        SetMinist(firdate, tdate);
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
                try {
                    if ((prgDialog2 != null) && prgDialog2.isShowing()) {
                        prgDialog2.dismiss();
                    }
                } catch (final IllegalArgumentException e) {
                    // Handle or log or ignore
                } catch (final Exception e) {
                    // Handle or log or ignore
                } finally {

                }
                SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getApplicationContext());
            }
        });
    }

    public void SetMinist(String stdate, String enddate) {
        planetsList.clear();
        if (!(prgDialog2 == null) && !(getApplicationContext() == null) && (!CommissionActivity.this.isFinishing())) {
            prgDialog2.show();
            ApiInterface apiService =
                    ApiClient.getClient().create(ApiInterface.class);
            String usid = Utility.gettUtilUserId(this);
            String agentid = Utility.gettUtilAgentId(this);
            String mobnoo = Utility.gettUtilMobno(this);
            String params = ApplicationConstants.CHANNEL_ID + usid + "/CMSNRPT/" + stdate + "/" + enddate;
            SecurityLayer.Log("Params", params);
            CommReport(params);
        }
    }

    private void CommReport(String params) {
        String endpoint = "report/inbox.action";
        String urlparams = "";
        try {
            urlparams = SecurityLayer.genURLCBC(params, endpoint, getApplicationContext());
            SecurityLayer.Log("RefURL", urlparams);
            //SecurityLayer.Log("refurl", urlparams);
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
                    SecurityLayer.Log("response..:", response.body());
                    JSONObject obj = new JSONObject(response.body());
                    obj = SecurityLayer.decryptTransaction(obj, getApplicationContext());
                    SecurityLayer.Log("decrypted_response", obj.toString());
                    JSONObject comdatas = obj.optJSONObject("data");
                    JSONArray comperf = comdatas.optJSONArray("transaction");
                    //session.setString(SecurityLayer.KEY_APP_ID,appid);
                    double tott = 0;
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
                                        JSONObject json_data = null;
                                        for (int i = 0; i < comperf.length(); i++) {
                                            json_data = comperf.getJSONObject(i);
                                            String fintoacnum = "";
                                            String finfromacnum = "";
                                            String txnCode = json_data.optString("txnCode");
                                            double agentCmsn = json_data.optDouble("agentCmsn");
                                            String txndateTime = json_data.optString("txndateTime");
                                            String amount = json_data.optString("amount");
                                            String status = json_data.optString("status");
                                            String toAcNum = json_data.optString("custAccNum");
                                            String refNumber = json_data.optString("refNumber");
                                            String fromaccnum = json_data.optString("agentAccNum");
                                            if (txnCode.equals("CASHDEP") || txnCode.equals("FTINTRABANK")) {
                                                fintoacnum = fromaccnum;
                                                finfromacnum = toAcNum;
                                                toAcNum = fintoacnum;
                                                fromaccnum = finfromacnum;
                                            }
                                            if (((status.equals("SUCCESS"))) && (agentCmsn > 0)) {
                                                tott += agentCmsn;
                                                planetsList.add(new GetCommPerfData(txnCode, txndateTime, agentCmsn, status, amount, toAcNum, refNumber, fromaccnum));
                                            }
                                        }
                                        if (!(getApplicationContext() == null)) {
                                            aAdpt = new NewCommListAdapter(planetsList, CommissionActivity.this);
                                            lv.setAdapter(aAdpt);
                                        }
                                    }
                                } else {
                                    if (!(getApplicationContext() == null)) {
                                        Toast.makeText(
                                                getApplicationContext(),
                                                "" + responsemessage,
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            } else {
                                finish();
                                startActivity(new Intent(CommissionActivity.this, SignInActivity.class));
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
                        String fincommrpt = Double.toString(tott);
                        fincommrpt = Utility.returnNumberFormat(fincommrpt);
                        txcomrepo.setText(ApplicationConstants.KEY_NAIRA + fincommrpt);
                    } else {
                        if (!(getApplicationContext() == null)) {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "There was an error on your request",
                                    Toast.LENGTH_LONG).show();
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
                if ((prgDialog2 != null) && prgDialog2.isShowing() && !(getApplicationContext() == null) && !(CommissionActivity.this.isFinishing())) {
                    prgDialog2.dismiss();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                SecurityLayer.Log("throwable error", t.toString());
                Toast.makeText(
                        getApplicationContext(),
                        "There was an error on your request",
                        Toast.LENGTH_LONG).show();
                if ((prgDialog2 != null) && prgDialog2.isShowing() && !(getApplicationContext() == null) && !(CommissionActivity.this.isFinishing())) {
                    prgDialog2.dismiss();
                }
                SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getApplicationContext());

            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button4) {
            DateRangePickerFragment dateRangePickerFragment = DateRangePickerFragment.newInstance(this, false);
            dateRangePickerFragment.show(getSupportFragmentManager(), "datePicker");
        }
        if (view.getId() == R.id.stmtly) {
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth, int yearEnd, int monthOfYearEnd, int dayOfMonthEnd) {
        Calendar clfrom = Calendar.getInstance();
        Calendar clto = Calendar.getInstance();
        clfrom.set(year, monthOfYear, dayOfMonth);
        clto.set(yearEnd, monthOfYearEnd, dayOfMonthEnd);
        String formattedfrom = format1.format(clfrom.getTime());
        String formattedto = format1.format(clto.getTime());
        txtitle.setText(formattedto);
        txfrom.setText(formattedfrom);
        ++monthOfYear;
        ++monthOfYearEnd;
        Calendar calfrom = Calendar.getInstance();
        Calendar calto = Calendar.getInstance();
        calto.set(yearEnd, monthOfYearEnd, dayOfMonthEnd);
        calfrom.set(year, monthOfYear, dayOfMonth);
        if (calfrom.before(calto)) {
            String frmdymonth = Integer.toString(dayOfMonth);
            if (dayOfMonth < 10) {
                frmdymonth = "0" + frmdymonth;
            }
            String frmyr = Integer.toString(monthOfYear);
            if (monthOfYear < 10) {
                frmyr = "0" + frmyr;
            }
            String frmyear = Integer.toString(year);
            String fromd = frmdymonth + "-" + (frmyr) + "-" + frmyear;
            String frmenddymonth = Integer.toString(dayOfMonthEnd);
            if (dayOfMonthEnd < 10) {
                frmenddymonth = "0" + frmenddymonth;
            }
            String frmendofendyr = Integer.toString(monthOfYearEnd);
            if (monthOfYearEnd < 10) {
                frmendofendyr = "0" + frmendofendyr;
            }
            String frmendyr = Integer.toString(yearEnd);
            String endd = frmenddymonth + "-" + (frmendofendyr) + "-" + frmendyr;
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
            new MaterialDialog.Builder(CommissionActivity.this)
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

    private void dismissProgressDialog() {
        if (prgDialog2 != null && prgDialog2.isShowing() && !(CommissionActivity.this.isFinishing())) {
            prgDialog2.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        dismissProgressDialog();
        super.onDestroy();

    }

    @Override
    public void onDateRangeSelected(int dayOfMonth, int monthOfYear, int year, int dayOfMonthEnd, int monthOfYearEnd, int yearEnd) {
        Calendar clfrom = Calendar.getInstance();
        Calendar clto = Calendar.getInstance();
        clfrom.set(year, monthOfYear, dayOfMonth);
        clto.set(yearEnd, monthOfYearEnd, dayOfMonthEnd);
        String formattedfrom = format1.format(clfrom.getTime());
        String formattedto = format1.format(clto.getTime());
        txtitle.setText(formattedto);
        txfrom.setText(formattedfrom);
        ++monthOfYear;
        ++monthOfYearEnd;
        Calendar calfrom = Calendar.getInstance();
        Calendar calto = Calendar.getInstance();
        calto.set(yearEnd, monthOfYearEnd, dayOfMonthEnd);
        calfrom.set(year, monthOfYear, dayOfMonth);
        if (calfrom.before(calto)) {
            String frmdymonth = Integer.toString(dayOfMonth);
            if (dayOfMonth < 10) {
                frmdymonth = "0" + frmdymonth;
            }
            String frmyear = Integer.toString(year);
            String fromd = frmdymonth + "-" + (monthOfYear) + "-" + frmyear;
            String frmenddymonth = Integer.toString(dayOfMonthEnd);
            if (dayOfMonthEnd < 10) {
                frmenddymonth = "0" + frmenddymonth;
            }
            String frmendyr = Integer.toString(yearEnd);
            String endd = frmenddymonth + "-" + (monthOfYearEnd) + "-" + frmendyr;
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
        finish();
        Intent i = new Intent(getApplicationContext(), SignInActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        Toast.makeText(
                getApplicationContext(),
                "You have been locked out of the app.Please call customer care for further details",
                Toast.LENGTH_LONG).show();
    }
}
