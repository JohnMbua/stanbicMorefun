package com.stanbicagent;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.stanbicagent.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import adapter.TxnAdapter;
import adapter.TxnList;
import listviewitems.ChartItem;
import security.SecurityLayer;


public class SelChartActivity extends BaseActivity implements View.OnClickListener,OnChartValueSelectedListener {
    Spinner sp1;
    private PieChart mChart;
    private Typeface mTf;
    List<TxnList> planetsList = new ArrayList<TxnList>();
    TxnAdapter aAdpt;
    ListView lv,lvpie;
    Button ok;
    public String finalfx,finpfrom,finpto;
    ProgressBar prgDialog2;
    RadioButton r1,r2,r3;
    LinearLayout layl;
    RelativeLayout ryl;
    SessionManagement session;
    private TextView emptyView;
    private Toolbar mToolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selchart);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("Stats");
        setSupportActionBar(mToolbar);
        mTf = Typeface.createFromAsset(getApplicationContext().getAssets(), "musleo.ttf");
        layl = (LinearLayout) findViewById(R.id.layl);
        ryl = (RelativeLayout) findViewById(R.id.rl2);

        lv = (ListView) findViewById(R.id.lv);
        emptyView = (TextView) findViewById(R.id.empty_view);
        ok = (Button) findViewById(R.id.button2);
        ok.setOnClickListener(this);
        session = new SessionManagement(this);

      /*  lv.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });*/
    /*    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {


            }
        });*/
        lvpie  = (ListView) findViewById(R.id.lvpie);

        prgDialog2 = (ProgressBar) findViewById(R.id.progressBar);


        r2.setChecked(true);
        String dated = getToday();
        String datew = getWeek();
        finpfrom = datew;
        finpto = dated;

        mChart = (PieChart) findViewById(R.id.chart1);
        mChart.setUsePercentValues(true);



        mChart.setDragDecelerationFrictionCoef(0.95f);

        mChart.setCenterTextTypeface(mTf);


        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColor(Color.WHITE);

        mChart.setTransparentCircleColor(Color.WHITE);
        mChart.setTransparentCircleAlpha(110);



        mChart.setHoleRadius(52f);
        mChart.setTransparentCircleRadius(57f);
        mChart.setDrawCenterText(true);

        mChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        mChart.setRotationEnabled(true);
        mChart.setHighlightPerTapEnabled(true);

        // mChart.setUnit(" €");
        // mChart.setDrawUnitsInChart(true);

        // add a selection listener
        mChart.setOnChartValueSelectedListener(this);

       mChart.setData(generateDataPie());

        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        // mChart.spin(2000, 0, 360);



        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.ABOVE_CHART_RIGHT);
        l.setXEntrySpace(5f);
        l.setYEntrySpace(0f);
        l.setYOffset(1f);

        // entry label styling
        mChart.setEntryLabelColor(Color.BLUE);
        mChart.setEntryLabelTypeface(mTf);
        mChart.setEntryLabelTextSize(12f);
        mChart.setDrawEntryLabels(false);
    }
    public interface OnChartValueSelectedListener {
        /**
         * Called when a value has been selected inside the chart.
         *
         * @param e The selected Entry.
         * @param h The corresponding highlight object that contains information
         * about the highlighted position
         */
        public void onValueSelected(Entry e, Highlight h);
        /**
         * Called when nothing has been selected or an "un-select" has been made.
         */
        public void onNothingSelected();
    }
    private class ChartDataAdapter extends ArrayAdapter<ChartItem> {

        public ChartDataAdapter(Context context, List<ChartItem> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getItem(position).getView(position, convertView, getContext());
        }

        @Override
        public int getItemViewType(int position) {
            // return the views type
            return getItem(position).getItemType();
        }

        @Override
        public int getViewTypeCount() {
            return 3; // we have 3 different item-types
        }
    }

    public void StartChartAct(int i){
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    public void setActionBarTitle(String title) {

      getSupportActionBar().setTitle(title);
    }
    public void SetPie(){
        planetsList.add(new TxnList("Cash Deposit ","32,812.12 Naira",R.drawable.cashdepo));
        planetsList.add(new TxnList("Cash Withdrawal","15,312.00 Naira",R.drawable.withdraw));
        aAdpt = new TxnAdapter( planetsList,this);
      //  lv.setAdapter(aAdpt);
    }
    @Override
    public void onValueSelected(Entry e, Highlight h) {

        if (e == null)
            return;
        Log.i("VAL SELECTED",
                "Value: " + e.getY() + ", index: " + h.getX()
                        + ", DataSet index: " + h.getDataSetIndex());

        float posn = h.getX();
        String benname = null;
        if(posn == 0.0){
             benname = "Cash Deposit";

        }
        if(posn == 1.0){
            benname = "Cash Withdrawal";

        }
        Bundle b = new Bundle();
      //  Fragment fragment = new IndivChart();
        b.putString("serv",benname);
        b.putString("pfrom",finpfrom);
        b.putString("pto",finpto);

        Intent i = new Intent(this, IndivChartActivity.class);
        i.putExtras(b);

        //Fire the second activity
        startActivity(i);
      /*  fragment.setArguments(b);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //  String tag = Integer.toString(title);
        fragmentTransaction.replace(R.id.container_body, fragment,benname);
        fragmentTransaction.addToBackStack(benname);
        setActionBarTitle(benname);
        fragmentTransaction.commit();*/

    }



    @Override
    public void onNothingSelected() {
        Log.i("PieChart", "nothing selected");
        Toast.makeText(getApplicationContext(),   "Nothing Selected", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.button2){

            if(r1.isChecked()) {
       String dated = getToday();
        finpfrom = dated;
        finpto = dated;
       // checkConnStats(dated,dated);
            }
            if(r2.isChecked()) {
        String dated = getToday();
        String datew = getWeek();
        finpfrom = datew;
        finpto = dated;
      //  checkConnStats(datew,dated);
            }
            if(r3.isChecked()) {
        String dated = getToday();
        String datem = getMonth();
        finpfrom = datem;
        finpto = dated;
       // checkConnStats(datem,dated);
            }
        }
    }


    private PieData generateDataPie() {

        float vallu = 21309;
        float vallu2 = 12309;
        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.

            entries.add(new PieEntry(vallu, "Cash Deposit"));
        entries.add(new PieEntry(vallu2, "Cash Withdrawal"));
        PieDataSet dataSet = new PieDataSet(entries, "");
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
        dataSet.setValueTextColor(Color.BLUE);

        PieData data = new PieData( dataSet);

     //   new PieData(dataSet);

        // data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);


        data.setValueTextColor(Color.BLUE);
        return  data;
    }

    private ArrayList<String> getQuarters() {

        ArrayList<String> q = new ArrayList<String>();
        q.add("1st Quarter");
        q.add("2nd Quarter");
        q.add("3rd Quarter");
        q.add("4th Quarter");

        return q;
    }


    public String getToday(){
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        SecurityLayer.Log("Date Today is",sdf.format(date));
        String datetod = sdf.format(date);
        return  datetod;
    }

    public String getWeek(){
        long DAY_IN_MS = 1000 * 60 * 60 * 24;

        Date date = new Date(System.currentTimeMillis() - (7 * DAY_IN_MS));
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        SecurityLayer.Log("Date Week is",sdf.format(date));
        String datetod = sdf.format(date);
return  datetod;
    }
    public String getMonth(){
        long DAY_IN_MS = 1000 * 60 * 60 * 24;

        Date date = new Date(System.currentTimeMillis() - (30 * DAY_IN_MS));
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        SecurityLayer.Log("Date Month is",sdf.format(date));
        String datetod = sdf.format(date);
        return  datetod;
    }
    public void CheckAdaptEmpty(){

        SecurityLayer.Log("Planet List Size is",Integer.toString(planetsList.size()));

        if (planetsList.size() == 0) {
            layl.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            layl.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }

    }


}
