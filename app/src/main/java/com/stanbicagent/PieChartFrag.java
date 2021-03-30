package com.stanbicagent;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.stanbicagent.R;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import listviewitems.ChartItem;
import listviewitems.PieChartItem;


public class PieChartFrag extends Fragment {
ImageView imageView1;
    ListView lvpie;

    public PieChartFrag() {
        // Required empty public constructor
    }
  /*  private static Fragment newInstance(Context context) {
        LayoutOne f = new LayoutOne();

        return f;
    }
*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.linefrag, null);
  lvpie  = (ListView) root.findViewById(R.id.lvpie);
        //checkInternetConnection();
      //*  checkPiConnect();
      //*
        ArrayList<ChartItem> list = new ArrayList<ChartItem>();

        // 30 items
        list.add(new PieChartItem(generateDataPie(1), getActivity()));


        ChartDataAdapter cda = new ChartDataAdapter(getActivity(), list);
        lvpie.setAdapter(cda);
        return root;
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

            yVals1.add(new Entry((int) (Math.random() * 70) + 30, i));
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
    @Override
    public void onResume(){
        super.onResume();
        // put your code here...

    }

}
