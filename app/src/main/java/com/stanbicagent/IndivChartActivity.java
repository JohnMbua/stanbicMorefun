package com.stanbicagent;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.stanbicagent.R;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;


public class IndivChartActivity extends BaseActivity implements View.OnClickListener  {
    RecyclerView lvpie;
    ChartFragAdapter mAdapter;
    private Toolbar mToolbar;

    private static final long ANIM_VIEWPAGER_DELAY = 5000;
    private static final long ANIM_VIEWPAGER_DELAY_USER_VIEW = 10000;
    boolean stopSliding = false;
    private Runnable animateViewPager;
    private Handler handler;
    ViewPager mPager;
    String serv,pfrom,pto,serv2;
    SessionManagement session;
    ProgressBar prgDialog2;
    LinearLayout layl;
    RelativeLayout ryl,rlcash;
    TextView ttot,cashtot;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.indivchart);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String type = bundle.getString("serv");
            serv2 = type;
            serv = type;
            pfrom = bundle.getString("pfrom");
            pto = bundle.getString("pto");
            if (type.equals("Airtime TopUp Self")) {
                type = "ARTISL";
            }
            if (type.equals("Airtime TopUp Other")) {
                type = "ARTIOT";
            }
            if (type.equals("Mobile Money")) {
                type = "MPESAB2C";
            }

            if (type.equals("Funds Transfer")) {
                type = "FTWIBK";
            }
            if (type.equals("School Fees")) {
                type = "SCHFEE";
            }
            if (type.equals("Billers")) {
                type = "CELBILL";
            }
            if (type.equals("Zuku")) {
                type = "CELLZUK";
            }
            if (type.equals("Airtel Money")) {
                type = "AIRTELC2B";
            }
            // serv = type;

        }
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(serv);
        setSupportActionBar(mToolbar);
        layl = (LinearLayout) findViewById(R.id.layl);
        ryl = (RelativeLayout) findViewById(R.id.rl2);
        rlcash = (RelativeLayout) findViewById(R.id.rlcash);
        ttot = (TextView) findViewById(R.id.trantot);
        cashtot = (TextView) findViewById(R.id.cashcoll);
        session = new SessionManagement(this);
        rlcash.setOnClickListener(this);
        prgDialog2 = (ProgressBar) findViewById(R.id.progressBar);
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setOffscreenPageLimit(3);

        mAdapter = new ChartFragAdapter(getSupportFragmentManager(),serv,pfrom,pto);

        if(mPager.getAdapter() == null) {

            mPager.setAdapter(mAdapter);

            runnable(2);
        }

        mPager.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                switch (event.getAction()) {

                    case MotionEvent.ACTION_CANCEL:
                        break;

                    case MotionEvent.ACTION_UP:
                        // calls when touch release on ViewPager

                        stopSliding = false;
                        runnable(2);
                        handler.postDelayed(animateViewPager,
                                ANIM_VIEWPAGER_DELAY_USER_VIEW);

                        break;

                    case MotionEvent.ACTION_MOVE:
                        // calls when ViewPager touch
                        if (handler != null && stopSliding == false) {
                            stopSliding = true;
                            handler.removeCallbacks(animateViewPager);
                        }
                        break;
                }
                return false;
            }
        });
        //     checkConnStats();
        cashtot.setText("21309 Naira");


        ttot.setText("2");
    }




    public void runnable(final int size) {
        handler = new Handler();
        animateViewPager = new Runnable() {
            public void run() {
                if (!stopSliding) {
                    if (mPager.getCurrentItem() == size - 1) {
                        mPager.setCurrentItem(0);
                    } else {
                        mPager.setCurrentItem(
                                mPager.getCurrentItem() + 1, true);
                    }
                    handler.postDelayed(animateViewPager, ANIM_VIEWPAGER_DELAY);
                }
            }
        };
    }

    public void StartChartAct(int i){


    }
    @Override
    public void onResume(){
        super.onResume();
        // put your code here...

    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.rlcash){

        }
    }


    private PieData generateDataPie(int cnt) {

        /*ArrayList<Entry> entries = new ArrayList<Entry>();

        for (int i = 0; i < 4; i++) {
            entries.add(new Entry((int) (Math.random() * 70) + 30, i));
        }

        PieDataSet d = new PieDataSet(entries, "");

        // space between slices
        d.setSliceSpace(2f);
        d.setColors(ColorTemplate.VORDIPLOM_COLORS);

        PieData cd = new PieData(getQuarters(), d);
        return cd;*/
        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<Entry> yVals1 = new ArrayList<Entry>();


        xVals.add("Mobile Money");
        xVals.add("Kenya Power");
        xVals.add("EFT");
        xVals.add("Airtime Top Up");

        int val = 10;
        for (int i = 0; i < 4; i++) {

            yVals1.add(new Entry(val, i));
            val+=10;
        }
        PieDataSet dataSet = null;
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<Integer>();

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

        dataSet.setColors(colors);

        PieData data = null;
        // data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);

        data.setValueTextColor(Color.WHITE);
        return  data;
    }

    private LineData generateDataLine(int cnt) {

        ArrayList<Entry> e1 = new ArrayList<Entry>();
        int val = 10;
        for (int i = 0; i < 12; i++) {
            e1.add(new Entry((int) (Math.random() * 70) + 30, i));
        }

        LineDataSet d1 = new LineDataSet(e1, "Services");
        d1.setLineWidth(2.5f);
        d1.setCircleSize(4.5f);
        d1.setHighLightColor(Color.rgb(244, 117, 117));
        d1.setDrawValues(false);

        ArrayList<Entry> e2 = new ArrayList<Entry>();

        for (int i = 0; i < 4; i++) {
            //  e2.add(new Entry(e1.get(i).getVal() - 30, i));
        }

        ArrayList<String> xVals = getMonths();


        ArrayList<LineDataSet> sets = new ArrayList<LineDataSet>();
        sets.add(d1);


        LineData cd = null;
        return cd;
    }
    private ArrayList<String> getMonths() {

        ArrayList<String> m = new ArrayList<String>();
        m.add("Jan");
        m.add("Feb");
        m.add("Mar");
        m.add("Apr");
        m.add("May");
        m.add("Jun");
        m.add("Jul");
        m.add("Aug");
        m.add("Sep");
        m.add("Okt");
        m.add("Nov");
        m.add("Dec");

        return m;
    }
}
