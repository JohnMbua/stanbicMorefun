package com.stanbicagent;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.stanbicagent.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import adapter.GetMyPerfServData;
import adapter.MyPerfServAdapt;
import adapter.TxnAdapter;
import adapter.TxnList;
import fragments.DateRangePickerFragment;
import model.GetCommPerfData;
import model.GetSummaryData;
import rest.ApiClient;
import rest.ApiInterface;
import rest.ApiSecurityClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import security.SecurityLayer;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MyPerfActivity extends BaseActivity implements View.OnClickListener, DateRangePickerFragment.OnDateRangeSelectedListener, OnChartValueSelectedListener {
    ViewPager pager;
    List<String> Titles = new ArrayList<String>();
    int Numboftabs = 2;
    private static int SPLASH_TIME_OUT = 3000;
    ViewPagerMyPerfAdapter adapter;
    CardView cv;
    Spinner sp1;
    String textdate;
    private BarChart mChart, mChart2;
    private LineChart mLineChart;
    private Typeface mTf;
    List<TxnList> planetsList = new ArrayList<TxnList>();
    TxnAdapter aAdpt;
    MyPerfServAdapt mypfadapt;
    ListView lv, lvpie, lvserv;
    Button ok, calendar;
    LinearLayout lyselcharttran, lyselchart, lymapsview, lyorderview;
    public String finalfx, finpfrom, finpto;
    ProgressDialog prgDialog2;
    RadioButton r1, r2, r3;
    LinearLayout layl;
    RelativeLayout ryl;
    ArrayList<PieEntry> finentry = new ArrayList<PieEntry>();
    ArrayList<BarEntry> barent = new ArrayList<BarEntry>();
    ArrayList<LegendEntry> legendent = new ArrayList<LegendEntry>();
    List<GetSummaryData> temp = new ArrayList<GetSummaryData>();
    List<GetCommPerfData> cperfdata = new ArrayList<GetCommPerfData>();
    ArrayList<Entry> listentry = new ArrayList<Entry>();
    SessionManagement session;
    private TextView emptyView, fromdate, endate, fromdatetran;
    TextView succtrans;
    String fromd, endd;
    PieDataSet dataSet;
    ArrayList<String> labels = new ArrayList<>();
    ArrayList<String> labelslnchart = new ArrayList<>();
    List<GetMyPerfServData> myperfdata = new ArrayList<GetMyPerfServData>();
    Legend l, l2;
    PieChart pieChart;
    ProgressDialog pro;
    Button calnd;
    View v1, v2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_newperf);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("My Performance");
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        //ab.setHomeAsUpIndicator(R.drawable.ic_menu); // set a custom icon for the default home button
        ab.setDisplayShowHomeEnabled(true); // show or hide the default home button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
        ab.setDisplayShowTitleEnabled(false); // disable the default title element here (for centered title)
        Titles.add("Transaction");
        Titles.add("Commission");
        fromdate = (TextView) findViewById(R.id.fromdate);
        fromdatetran = (TextView) findViewById(R.id.fromdatetran);
        pro = new ProgressDialog(this);
        pro.setMessage("Loading...");
        pro.setTitle("");
        pro.setCancelable(false);
        calendar = (Button) findViewById(R.id.button4);
        calendar.setOnClickListener(this);
        session = new SessionManagement(this);
        prgDialog2 = new ProgressDialog(this);
        prgDialog2.setMessage("Loading ....");
        // Set Cancelable as False
        session = new SessionManagement(this);
        prgDialog2.setCancelable(false);
        InitCharts();

        succtrans = (TextView) findViewById(R.id.succtrans);
        lymapsview = (LinearLayout) findViewById(R.id.lymapsvw);
        lyorderview = (LinearLayout) findViewById(R.id.lyorder);

        lyselcharttran = (LinearLayout) findViewById(R.id.lyselcharttran);
        lyselchart = (LinearLayout) findViewById(R.id.lyselchart);

        v1 = (View) findViewById(R.id.v1);
        v2 = (View) findViewById(R.id.v2);

        lyorderview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lyselchart.setVisibility(View.GONE);
                lyselcharttran.setVisibility(View.VISIBLE);


                v1.setVisibility(View.VISIBLE);
                v2.setVisibility(View.GONE);
            }
        });

        lymapsview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lyselchart.setVisibility(View.VISIBLE);
                lyselcharttran.setVisibility(View.GONE);


                v2.setVisibility(View.VISIBLE);
                v1.setVisibility(View.GONE);

            }
        });
        cv = (CardView) findViewById(R.id.card_view10);
        lvserv = (ListView) findViewById(R.id.lvserv);
        mChart2 = (BarChart) findViewById(R.id.chart2);
        mChart2.setOnChartValueSelectedListener(this);
        mChart2.setDrawBarShadow(false);
        Description ds2 = new Description();
        ds2.setText("Transaction amount in KES");
        mChart2.setDescription(ds2);
        mChart2.getDescription().setPosition(3f, 3f);
        mChart2.setPinchZoom(true);
        mChart2.setDrawGridBackground(false);
        XAxis xAxis2 = mChart2.getXAxis();
        xAxis2.setTypeface(mTf);
        xAxis2.setDrawGridLines(false);
        xAxis2.setGranularity(1f); // only intervals of 1 day
        xAxis2.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis2.setTextSize(7f);
        l2 = mChart2.getLegend();
        l2.setPosition(Legend.LegendPosition.ABOVE_CHART_CENTER);
        l2.setForm(Legend.LegendForm.DEFAULT);
        l2.setFormSize(6f);
        l2.setTextSize(6f);
        l2.setXOffset(2);
        l2.setXEntrySpace(4f); // set the space between the legend entries on the x-axis
        l2.setYEntrySpace(4f); // set the space between the legend entries on the y-axis
        mChart2.getAxisLeft().setDrawLabels(false);
        mChart2.getAxisRight().setDrawLabels(false);
        mChart2.getXAxis().setDrawLabels(false);
        pieChart = (PieChart) findViewById(R.id.piechart);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setCenterText("");
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);

        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);

        pieChart.setHoleRadius(40f);
        pieChart.setTransparentCircleRadius(40f);

        pieChart.setDrawCenterText(true);

        pieChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);

        pieChart.setOnChartValueSelectedListener(this);

        //     setPieData(4, 100);

        pieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        // mChart.spin(2000, 0, 360);


        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);

        l.setYOffset(0f);


        l.setFormSize(6f);
        l.setTextSize(6f);
        l.setXOffset(2);

        l.setXEntrySpace(4f); // set the space between the legend entries on the x-axis
        l.setYEntrySpace(4f); // set the space between the legend entries on the y-axis


        // entry label styling
        pieChart.setEntryLabelColor(Color.BLACK);

        pieChart.setEntryLabelTextSize(8f);
        //  SetLV();
     /*   new MaterialDialog.Builder(getActivity())
                .title("Enter PIN")
                .content("Please enter Login PIN")
                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)
                .input("Login PIN", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        // Do something
                        String pin =  input.toString();
                        SecurityLayer.Log("Login Pin",pin);
                        String encpin = Utility.getencpin(pin);
                        String usid = Utility.gettUtilUserId(getActivity());
                        String mobnoo = Utility.gettUtilMobno(getActivity());
                        String params = ApplicationConstants.CHANNEL_ID+usid+"/" + encpin  + "/"+mobnoo;
                         LogRetro(params,"COMM");
                    }
                }).show();*/
        Switch sw = (Switch) findViewById(R.id.switch1);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    cv.setVisibility(View.GONE);
                } else {
                    // The toggle is disabled
                    cv.setVisibility(View.VISIBLE);
                }
            }
        });


    }

    public String getToday() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        SecurityLayer.Log("Date Today is", sdf.format(date));
        String datetod = sdf.format(date);
        return datetod;
    }

    public String getWeek() {
        long DAY_IN_MS = 1000 * 60 * 60 * 24;

        Date date = new Date(System.currentTimeMillis() - (7 * DAY_IN_MS));
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        SecurityLayer.Log("Date Week is", sdf.format(date));
        String datetod = sdf.format(date);
        return datetod;
    }

    public String getMonth() {
        long DAY_IN_MS = 1000 * 60 * 60 * 24;

        Date date = new Date(System.currentTimeMillis() - (30 * DAY_IN_MS));
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        SecurityLayer.Log("Date Month is", sdf.format(date));
        String datetod = sdf.format(date);
        return datetod;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    public void loadDataset(String fromdt, String enddt) {
        if ((prgDialog2 != null) && !(getApplicationContext() == null) && (!(MyPerfActivity.this.isFinishing()))) {
            prgDialog2.show();
        }
        SecurityLayer.Log("From Date", fromdt);
        SecurityLayer.Log("End Date", enddt);
        final PieData[] data = {null};
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        String usid = Utility.gettUtilUserId(this);
        String agentid = Utility.gettUtilAgentId(this);
        String mobnoo = Utility.gettUtilMobno(this);
        String params = ApplicationConstants.CHANNEL_ID + usid + "/" + agentid + "/" + mobnoo + "/CMSNRPT/" + fromdt + "/" + enddt;
        Loadd(params);
    }


    private void Loadd(String params) {
        String endpoint = "report/genrpt.action";
        String usid = Utility.gettUtilUserId(this);
        String agentid = Utility.gettUtilAgentId(this);
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
                ApiSecurityClient.getClient(MyPerfActivity.this).create(ApiInterface.class);


        Call<String> call = apiService.setGenericRequestRaw(urlparams);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                int counter = 0;
                barent.clear();
                myperfdata.clear();
                try {
                    // JSON Object


                    SecurityLayer.Log("Cable TV Resp", response.body());
                    SecurityLayer.Log("response..:", response.body());

                    JSONObject obj = new JSONObject(response.body());
                    //obj = Utility.onresp(obj,getActivity());
                    obj = SecurityLayer.decryptTransaction(obj, getApplicationContext());
                    SecurityLayer.Log("decrypted_response", obj.toString());


                    JSONObject comdatas = obj.optJSONObject("data");
                    JSONArray comperf = comdatas.optJSONArray("transaction");
                    JSONArray summdata = comdatas.optJSONArray("summary");
                    boolean chktxncnt = false;
                    //session.setString(SecurityLayer.KEY_APP_ID,appid);

                    if (!(response.body() == null)) {
                        String respcode = obj.optString("responseCode");
                        String responsemessage = obj.optString("message");

                        SecurityLayer.Log("Response Message", responsemessage);
                        ArrayList<String> labelsstr = new ArrayList<>();

                        if (Utility.isNotNull(respcode) && Utility.isNotNull(respcode)) {
                            if (!(Utility.checkUserLocked(respcode))) {
                                SecurityLayer.Log("Response Message", responsemessage);
                                ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
                                ArrayList<IBarDataSet> comdataSets = new ArrayList<IBarDataSet>();
                                ArrayList<Integer> colors = new ArrayList<Integer>();
                                ArrayList<PieEntry> entries = new ArrayList<PieEntry>();


                                for (int c : ColorTemplate.VORDIPLOM_COLORS)
                                    colors.add(c);

                                for (int c : ColorTemplate.JOYFUL_COLORS)
                                    colors.add(c);

                                for (int c : ColorTemplate.COLORFUL_COLORS)
                                    colors.add(c);

                                for (int c : ColorTemplate.LIBERTY_COLORS)
                                    colors.add(c);

                                for (int c : ColorTemplate.PASTEL_COLORS)
                                    colors.add(c);

                                colors.add(ColorTemplate.getHoloBlue());
                                if (respcode.equals("00")) {
                                    session.setString(SessionManagement.COMMDATA, comperf.toString());
                                    session.setString(SessionManagement.SUMMDATA, summdata.toString());
                                    session.setString(SessionManagement.MYPERFTEXT, textdate);
                                    SecurityLayer.Log("Session In", "Session In");
                                    SetActivityLogic(summdata, comperf);

                                    SetTranActivityLogic(summdata, comperf);

                                   /* NewSelChartTran fragment = (NewSelChartTran)getFragmentManager().findFragmentById(R.id.example_fragment);
                                     fragment.SetActivityLogic(summdata,comperf);*/
                                   /* Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.container + ":" + pager.getCurrentItem());

                                    int pos = pager.getCurrentItem();
                                    Fragment activeFragment = adapter.getItem(pos);


                                    SecurityLayer.Log("NewSelChart Tran","Inside");
                                    NewSelChartTran fm2 = (NewSelChartTran) getSupportFragmentManager().findFragmentById(R.id.pager);
                                    fm2.SetActivityLogic(summdata,comperf);*/


                                  /*  Fragment fm =  getSupportFragmentManager().findFragmentById(R.id.pager);
                                    if(fm != null){
                                        SecurityLayer.Log("NewSelChart Ndani","Inside");
                                        fm = NewSelChart.newInstance("NewSelChsdf","Param 2");
                                    }*/


                                  /*  if(pos == 1) {
                                      //  ((NewSelChartTran) activeFragment).SetActivityLogic(summdata,comperf);
                                      //  ((NewSelChartTran) activeFragment).Test();

                                        SecurityLayer.Log("NewSelChart","Inside");
                                       *//* NewSelChart fm = (NewSelChart) getSupportFragmentManager().findFragmentById(R.id.pager);
                                        fm.SetActivityLogic(summdata,comperf);*//*

                                        Fragment fm =  getSupportFragmentManager().findFragmentById(R.id.pager);
                                        if(fm == null){
                                            fm = NewSelChart.newInstance("NewSelChsdf","Param 2");
                                        }

                                    }
                                    if(pos == 0) {
                                     //   ((NewSelChart) activeFragment).SetActivityLogic(summdata,comperf);
                                     //   ((NewSelChart) activeFragment).Test();
                                        SecurityLayer.Log("NewSelChart Tran","Inside");
                                        NewSelChartTran fm2 = (NewSelChartTran) getSupportFragmentManager().findFragmentById(R.id.pager);
                                        fm2.SetActivityLogic(summdata,comperf);


                                       *//* Fragment fm =  getSupportFragmentManager().findFragmentById(R.id.pager);
                                        if(fm == null){
                                            fm = NewSelChart.newInstance("NewSelChsdf","Param 2");
                                        }*//*
                                    }*/

                                   /* if (page != null) {
                                        switch (pager.getCurrentItem()) {
                                            case 0:
                                               *//* NewSelChart oneFragment = (NewSelChart) page;
                                                oneFragment.Test();*//*
                                               SecurityLayer.Log("NewSelChart","Inside");
                                                NewSelChart fm = (NewSelChart) getSupportFragmentManager().findFragmentById(R.id.pager);
                                                fm.Test();
                                                break;
                                            case 1:
                                               *//* NewSelChartTran twoFragment = (NewSelChartTran) page;
                                                twoFragment.Test();*//*
                                                SecurityLayer.Log("NewSelChart Tran","Inside");
                                                NewSelChartTran fm2 = (NewSelChartTran) getSupportFragmentManager().findFragmentById(R.id.pager);
                                                fm2.Test();
                                                break;
                                        }
                                    }*/

                                } else {
                                    Toast.makeText(
                                            getApplicationContext(),
                                            "" + responsemessage,
                                            Toast.LENGTH_LONG).show();
                                }
                            } else {
                                finish();
                                startActivity(new Intent(MyPerfActivity.this, SignInActivity.class));
                                Toast.makeText(
                                        getApplicationContext(),
                                        "You have been locked out of the app.Please call customer care for further details",
                                        Toast.LENGTH_LONG).show();

                            }
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
                    // prgDialog2.dismiss();


                } catch (NumberFormatException e) {
                    SecurityLayer.Log("numberformatexception", e.toString());
                    //  ((FMobActivity) getActivity()).SetForceOutDialog(getString(R.string.forceout),getString(R.string.forceouterr),getActivity());
                    // SecurityLayer.Log(e.toString());
                } catch (JSONException e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    // TODO Auto-generated catch block
                    Toast.makeText(getApplicationContext(), getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                    SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getApplicationContext());
                    // SecurityLayer.Log(e.toString());

                } catch (Exception e) {
                    SecurityLayer.Log("generalexception", e.toString());
                    SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getApplicationContext());
                    // SecurityLayer.Log(e.toString());
                }
                if ((prgDialog2 != null) && prgDialog2.isShowing() && !(getApplicationContext() == null)) {
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


                if ((prgDialog2 != null) && prgDialog2.isShowing() && !(getApplicationContext() == null)) {
                    prgDialog2.dismiss();
                    SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getApplicationContext());
                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button4) {
          /*  Calendar now = Calendar.getInstance();
            DatePickerDialog dpd = DatePickerDialog.newInstance(
                    this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );*/
/*dpd.setMaxDate(now);
            dpd.show(getFragmentManager(), "Datepickerdialog");*/

            DateRangePickerFragment dateRangePickerFragment = DateRangePickerFragment.newInstance(this, false);

            dateRangePickerFragment.show(getSupportFragmentManager(), "datePicker");
            SecurityLayer.Log("Changed manenoz", "It has");


        }
    }

    @Override
    public void onDateRangeSelected(int startDay, int startMonth, int startYear, int endDay, int endMonth, int endYear) {
        Log.d("range : ", "from: " + startDay + "-" + startMonth + "-" + startYear + " to : " + endDay + "-" + endMonth + "-" + endYear);

        Calendar start = Calendar.getInstance();
        start.set(startYear, startMonth, startDay);


        Calendar end = Calendar.getInstance();
        end.set(endYear, endMonth, endDay);

        Calendar now = Calendar.getInstance();

        if ((start.compareTo(now) > 0) || (end.compareTo(now) > 0)) {
            Toast.makeText(
                    getApplicationContext(),
                    "Please ensure either the From Date or the After Date is not after today",
                    Toast.LENGTH_LONG).show();
        } else {

            SimpleDateFormat format1 = new SimpleDateFormat("" +
                    "MMMM dd yyyy");

            String startdatestring = format1.format(start.getTime());

            String enddatestring = format1.format(end.getTime());
            //   textdate = "You picked the following date: From- " + startDay + "/" + (++startMonth) + "/" + startYear + " To " + endDay + "/" + (++endMonth) + "/" + endYear;
            textdate = " From- " + startdatestring + " To " + enddatestring;

            fromdate.setText(textdate);
            fromdatetran.setText(textdate);

            startMonth = startMonth + 1;
            endMonth = endMonth + 1;
            Calendar calfrom = Calendar.getInstance();
            Calendar calto = Calendar.getInstance();
            calto.set(endYear, endMonth, endDay);
            calfrom.set(startYear, startMonth, startDay);

            if (calfrom.before(calto)) {

                String frmdymonth = Integer.toString(startDay);
                if (startDay < 10) {
                    frmdymonth = "0" + frmdymonth;
                }
                String frmyear = Integer.toString(startYear);
                fromd = frmdymonth + "-" + (startMonth) + "-" + frmyear;
                String frmenddymonth = Integer.toString(endDay);
                if (endDay < 10) {
                    frmenddymonth = "0" + frmenddymonth;
                }

                String frmendyr = Integer.toString(endYear);
                endd = frmenddymonth + "-" + (endMonth) + "-" + frmendyr;

                if (Utility.checkInternetConnection(getApplicationContext())) {
                    if (Utility.isNotNull(fromd) || Utility.isNotNull(endd)) {
                        loadDataset(fromd, endd);
                    } else {
                        Toast.makeText(getApplicationContext(), "Please select appropriate from date and end date", Toast.LENGTH_LONG).show();
                    }
                }
            } else {
                Toast.makeText(
                        getApplicationContext(),
                        "Please ensure the from date is before the after date",
                        Toast.LENGTH_LONG).show();
            }
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
    public void onResume() {
        super.onResume();
        /* *//* resetDisconnectTimer();*/

        HashMap<String, Long> nurl = session.getCurrTime();
        long newurl = nurl.get(SessionManagement.KEY_TIMEST);
        boolean blfalse = true;
        if (newurl > 0) {
            long secs = (new Date().getTime()) / 1000;
            long diff = 0;
            if (secs >= newurl) {
                diff = secs - newurl;
                if (diff > 180) {
                    blfalse = false;
                    this.finish();
                    //    session.logoutUser();
                    // After logout redirect user to Loing Activity
                    Intent i = new Intent(MyPerfActivity.this, FMobActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    // Staring Login Activity
                    startActivity(i);
                    //   Toast.makeText(FMobActivity.ths, "Your session has expired. Please login again", Toast.LENGTH_LONG).show();
                }
            }
        }

        if (blfalse) {

        }
    }

    private void notifyViewPagerDataSetChanged() {
        Log.d("Has it changed", "\nnotifyDataSetChanged()");


        adapter.notifyDataSetChanged();

    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }


    public class ViewPagerMyPerfAdapter extends FragmentStatePagerAdapter {

        int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created
        List<String> Titles;


        // Build a Constructor and assign the passed Values to appropriate values in the class
        public ViewPagerMyPerfAdapter(FragmentManager fm, List<String> Titles, int mNumbOfTabsumb) {
            super(fm);

            this.Titles = Titles;
            this.NumbOfTabs = mNumbOfTabsumb;


        }

        //This method return the fragment for the every position in the View Pager
        @Override
        public Fragment getItem(int position) {

            Fragment fragmentt;
            if (position == 0) // if the position is 0 we are returning the First tab
            {
                fragmentt = new NewSelChartTran();

            } else         // As we are having 2 tabs if the position is now 0 it must be 1 so we are returning second tab
            {
                fragmentt = new NewSelChart();

            }
            return fragmentt;

        }


        // This method return the titles for the Tabs in the Tab Strip

        @Override
        public CharSequence getPageTitle(int position) {
            return Titles.get(position);
        }

        // This method return the Number of tabs for the tabs Strip

        @Override
        public int getCount() {
            return NumbOfTabs;
        }


        // To update fragment in ViewPager, we should override getItemPosition() method,
        // in this method, we call the fragment's public updating method.
        public int getItemPosition(Object object) {
           /* Log.d("NewTag", "getItemPosition(" + object.getClass().getSimpleName() + ")");
            if (object instanceof NewSelChart) {
                String tst = session.getString("TESTSTR");
                if (Utility.isNotNull(tst) && tst.equals("")) {
                    ((NewSelChart) object).Test(tst);
                }

            }
            return super.getItemPosition(object);*/
            return POSITION_NONE;
        }

    }

    @Override
    protected void onDestroy() {

        session.setString(SessionManagement.MYPERFTEXT, null);
        if ((prgDialog2 != null) && prgDialog2.isShowing() && !(getApplicationContext() == null)) {
            prgDialog2.dismiss();

        }
        super.onDestroy();
    }


    public void SetForceOutDialog(String msg, final String title, final Context c) {
        if (!(c == null)) {
            new MaterialDialog.Builder(MyPerfActivity.this)
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

    public void InitCharts() {
        String dated = getToday();
        String datew = getWeek();
        finpfrom = datew;
        finpto = dated;
        Calendar cal = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH); // Note: zero based!
        int day = now.get(Calendar.DAY_OF_MONTH);
        now.set(year, month, 01);

        SimpleDateFormat format1 = new SimpleDateFormat("" +
                "MMMM dd yyyy");
        System.out.println(cal.getTime());
        String formattednow = format1.format(cal.getTime());
        String formattedstartdate = format1.format(now.getTime());
        String date = "From- " + formattedstartdate + " To " + formattednow;

        fromdate.setText(date);
        fromdatetran.setText(date);
        month = month + 1;
        String frmdymonth = Integer.toString(day);
        if (day < 10) {
            frmdymonth = "0" + frmdymonth;
        }
        String frmyear = Integer.toString(year);
        String tdate = frmdymonth + "-" + (month) + "-" + frmyear;
        String firdate = "01" + "-" + (month) + "-" + frmyear;

        Calendar calfrom = Calendar.getInstance();
        calfrom.set(year, month, 1);

        loadDataset(firdate, tdate);
    }


    public void SetActivityLogic(JSONArray summdata, JSONArray comperf) {
        if (summdata.length() > 0) {

            try {
                boolean chktxncnt = false;
                ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
                ArrayList<IBarDataSet> comdataSets = new ArrayList<IBarDataSet>();
                ArrayList<Integer> colors = new ArrayList<Integer>();
                ArrayList<PieEntry> entries = new ArrayList<PieEntry>();
                ArrayList<String> labelsstr = new ArrayList<>();


                for (int c : ColorTemplate.VORDIPLOM_COLORS)
                    colors.add(c);

                for (int c : ColorTemplate.JOYFUL_COLORS)
                    colors.add(c);

                for (int c : ColorTemplate.COLORFUL_COLORS)
                    colors.add(c);

                for (int c : ColorTemplate.LIBERTY_COLORS)
                    colors.add(c);

                for (int c : ColorTemplate.PASTEL_COLORS)
                    colors.add(c);

                colors.add(ColorTemplate.getHoloBlue());
                JSONObject json_data = null;
                for (int i = 0; i < summdata.length(); i++) {
                    json_data = summdata.getJSONObject(i);
                    //String accid = json_data.getString("benacid");


                    String txncode = json_data.optString("txnCode");

                    String amon = json_data.optString("amount");
                    String status = json_data.optString("status");
                    String agcmsn = json_data.optString("agentCmsn");
                    String txnname = Utility.convertTxnCodetoServ(txncode);
                    if ((!(amon.equals(""))) && (!(agcmsn.equals("")))) {
                        float dbagcmsn = Float.parseFloat(amon);
                        float agamcmn = Float.parseFloat(agcmsn);
                        if (Utility.isNotNull(status)) {
                            if (((status.equals("SUCCESS"))) && (dbagcmsn > 0)) {
                                //   finentry.add(new PieEntry(dbagcmsn, txnname));
                                ArrayList<BarEntry> barnt = new ArrayList<BarEntry>();
                                SecurityLayer.Log("Log Amount", Double.toString(Double.parseDouble(agcmsn)));
                                barnt.add(new BarEntry(i, dbagcmsn, txnname));

                                BarDataSet set1;

                                set1 = new BarDataSet(barnt, txnname);

                                set1.setColor(colors.get(i));
                                //  }
                                dataSets.add(set1);
                                ArrayList<BarEntry> bcmnt = new ArrayList<BarEntry>();

                                bcmnt.add(new BarEntry(i, agamcmn, txnname));
                                labelsstr.add(txnname);

                                chktxncnt = true;
                                SecurityLayer.Log("Entries", dbagcmsn + " Code" + txnname);
                                BarDataSet set2;

                                set2 = new BarDataSet(bcmnt, txnname);

                                set2.setColor(colors.get(i));
                                //  }
                                comdataSets.add(set2);
                                entries.add(new PieEntry((float) agamcmn,
                                        txnname));

                            }
                        }
                    }
                }


                BarData databar = new BarData(dataSets);
                                        /*BarData data = new BarData(dataSets);

                                        mChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labelsstr));
                                        mChart.setData(data);
                                        mChart.invalidate();*/

                PieDataSet dataSet = new PieDataSet(entries, "");

                // dataSet.setDrawIcons(false);

                dataSet.setSliceSpace(3f);

                dataSet.setSelectionShift(5f);

                // add a lot of colors


                colors.add(ColorTemplate.getHoloBlue());

                dataSet.setColors(colors);
                //dataSet.setSelectionShift(0f);

                PieData data = new PieData(dataSet);
                data.setValueFormatter(new PercentFormatter());
                data.setValueTextSize(7f);
                data.setValueTextColor(Color.BLACK);

                pieChart.setData(data);

                // undo all highlights
                pieChart.highlightValues(null);

                pieChart.invalidate();


                BarData comdata = new BarData(comdataSets);

                mChart2.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labelsstr));
                mChart2.setData(databar);
                mChart2.invalidate();
                JSONObject json_cperfdata = null;
                int cntr = 0;
                for (int i = 0; i < comperf.length(); i++) {
                    json_cperfdata = comperf.getJSONObject(i);
                    //String accid = json_data.getString("benacid");


                    String txnCode = json_cperfdata.optString("txnCode");
                    double agentCmsn = json_cperfdata.optDouble("agentCmsn");
                    String txndateTime = json_cperfdata.optString("txndateTime");
                    String amount = json_cperfdata.optString("amount");
                    String status = json_cperfdata.optString("status");
                    String toAcNum = json_cperfdata.optString("toAcNum");
                    String refNumber = json_cperfdata.optString("refNumber");
                    txndateTime = Utility.convertPerfDate(txndateTime);
                    if (((status.equals("SUCCESS"))) && (agentCmsn > 0)) {
                        listentry.add(new Entry(cntr, Float.parseFloat(Double.toString(agentCmsn))));

                        labelslnchart.add(cntr, txndateTime);
                        cntr++;
                        SecurityLayer.Log("Counter", Integer.toString(cntr));
                    }


                }


                if (cntr > 0) {
                    //  setData(listentry);
                }
                SecurityLayer.Log("Transaction counter", Integer.toString(cntr));
                succtrans.setText(Integer.toString(cntr));
                if (chktxncnt == false) {
                    Toast.makeText(
                            getApplicationContext(),
                            "There are no transaction records to display",
                            Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(
                    getApplicationContext(),
                    "There are no records to display",
                    Toast.LENGTH_LONG).show();
        }


    }


    public void SetTranActivityLogic(JSONArray summdata, JSONArray comperf) {

        if (summdata.length() > 0) {
            try {

                ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
                ArrayList<IBarDataSet> comdataSets = new ArrayList<IBarDataSet>();
                ArrayList<Integer> colors = new ArrayList<Integer>();
                ArrayList<PieEntry> entries = new ArrayList<PieEntry>();
                ArrayList<String> labelsstr = new ArrayList<>();

                boolean chktxncnt = false;
                for (int c : ColorTemplate.VORDIPLOM_COLORS)
                    colors.add(c);

                for (int c : ColorTemplate.JOYFUL_COLORS)
                    colors.add(c);

                for (int c : ColorTemplate.COLORFUL_COLORS)
                    colors.add(c);

                for (int c : ColorTemplate.LIBERTY_COLORS)
                    colors.add(c);

                for (int c : ColorTemplate.PASTEL_COLORS)
                    colors.add(c);

                colors.add(ColorTemplate.getHoloBlue());
                JSONObject json_data = null;
                for (int i = 0; i < summdata.length(); i++) {

                    json_data = summdata.getJSONObject(i);

                    //String accid = json_data.getString("benacid");


                    String txncode = json_data.optString("txnCode");

                    String amon = json_data.optString("amount");
                    String status = json_data.optString("status");
                    String agcmsn = json_data.optString("agentCmsn");
                    String txnname = Utility.convertTxnCodetoServ(txncode);
                    if ((!(amon.equals(""))) && (!(agcmsn.equals("")))) {
                        float dbagcmsn = Float.parseFloat(amon);
                        float agamcmn = Float.parseFloat(agcmsn);
                        if (Utility.isNotNull(status)) {
                            if (((status.equals("SUCCESS"))) && (dbagcmsn > 0)) {
                                //   finentry.add(new PieEntry(dbagcmsn, txnname));
                                ArrayList<BarEntry> barnt = new ArrayList<BarEntry>();
                                SecurityLayer.Log("Log Amount", Double.toString(Double.parseDouble(agcmsn)));
                                myperfdata.add(new GetMyPerfServData(txnname, "", Utility.roundto2dp(Double.toString(agamcmn)) + ApplicationConstants.KEY_NAIRA));
                                barnt.add(new BarEntry(i, dbagcmsn, txnname));

                                BarDataSet set1;

                                set1 = new BarDataSet(barnt, txnname);

                                set1.setColor(colors.get(i));
                                //  }
                                dataSets.add(set1);
                                ArrayList<BarEntry> bcmnt = new ArrayList<BarEntry>();

                                bcmnt.add(new BarEntry(i, agamcmn, txnname));
                                labelsstr.add(txnname);

                                chktxncnt = true;
                                SecurityLayer.Log("Entries", dbagcmsn + " Code" + txnname);
                                BarDataSet set2;

                                set2 = new BarDataSet(bcmnt, txnname);

                                set2.setColor(colors.get(i));
                                //  }
                                comdataSets.add(set2);
                                entries.add(new PieEntry((float) agamcmn,
                                        txnname));

                            }
                        }
                    }
                }

                mypfadapt = new MyPerfServAdapt(myperfdata, this);
                lvserv.setAdapter(mypfadapt);
                BarData databar = new BarData(dataSets);
                                        /*BarData data = new BarData(dataSets);

                                        mChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labelsstr));
                                        mChart.setData(data);
                                        mChart.invalidate();*/

                PieDataSet dataSet = new PieDataSet(entries, "");

                // dataSet.setDrawIcons(false);

                dataSet.setSliceSpace(3f);

                dataSet.setSelectionShift(5f);

                // add a lot of colors


                colors.add(ColorTemplate.getHoloBlue());

                dataSet.setColors(colors);
                //dataSet.setSelectionShift(0f);

                PieData data = new PieData(dataSet);
                data.setValueFormatter(new PercentFormatter());
                data.setValueTextSize(7f);
                data.setValueTextColor(Color.BLACK);

                pieChart.setData(data);

                // undo all highlights
                pieChart.highlightValues(null);

                pieChart.invalidate();


                BarData comdata = new BarData(comdataSets);

                mChart2.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labelsstr));
                mChart2.setData(databar);
                mChart2.invalidate();
                JSONObject json_cperfdata = null;
                int cntr = 0;
                for (int i = 0; i < comperf.length(); i++) {
                    json_cperfdata = comperf.getJSONObject(i);
                    //String accid = json_data.getString("benacid");


                    String txnCode = json_cperfdata.optString("txnCode");
                    double agentCmsn = json_cperfdata.optDouble("agentCmsn");
                    String txndateTime = json_cperfdata.optString("txndateTime");
                    String amount = json_cperfdata.optString("amount");
                    String status = json_cperfdata.optString("status");
                    String toAcNum = json_cperfdata.optString("toAcNum");
                    String refNumber = json_cperfdata.optString("refNumber");
                    txndateTime = Utility.convertPerfDate(txndateTime);
                    if (((status.equals("SUCCESS"))) && (agentCmsn > 0)) {
                        listentry.add(new Entry(cntr, Float.parseFloat(Double.toString(agentCmsn))));

                        labelslnchart.add(cntr, txndateTime);
                        cntr++;
                        SecurityLayer.Log("Counter", Integer.toString(cntr));
                    }


                }


                if (cntr > 0) {
                    //  setData(listentry);
                }
                SecurityLayer.Log("Transaction counter", Integer.toString(cntr));
                succtrans.setText(Integer.toString(cntr));
                if (chktxncnt == false) {
                    Toast.makeText(
                            getApplicationContext(),
                            "There are no transaction records to display",
                            Toast.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

          /*  SecurityLayer.Log("NewSelChart","Inside");
            NewSelChart fm = (NewSelChart) getFragmentManager().findFragmentById(R.id.pager);
            fm.SetActivityLogic(summdata,comperf);*/
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    "There are no records to display",
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
