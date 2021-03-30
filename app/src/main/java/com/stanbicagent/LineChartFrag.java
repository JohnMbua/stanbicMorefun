package com.stanbicagent;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.stanbicagent.R;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import listviewitems.ChartItem;
import listviewitems.LineChartItem;
import security.SecurityLayer;


public class LineChartFrag extends Fragment {
ImageView imageView1;
    ListView lvpie;
    SessionManagement session;
    String serv,pfrom,pto;

    public LineChartFrag() {
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
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String type = bundle.getString("serv");
        pfrom = bundle.getString("pfrom");
           pto = bundle.getString("pto");
            serv = type;
            SecurityLayer.Log("Line Chart Service",serv);
        }
  lvpie  = (ListView) root.findViewById(R.id.lvpie);
        //checkInternetConnection();
      //*  checkPiConnect();
      //*
        session = new SessionManagement(getActivity());
        ArrayList<ChartItem> list = new ArrayList<ChartItem>();

        // 30 items

        list.add(new LineChartItem(generateDataLine(""), getActivity()));


        ChartDataAdapter cda = new ChartDataAdapter(getActivity(), list);
        lvpie.setAdapter(cda);
//checkConnStats();
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
    private LineData generateDataLine(String jso) {

        ArrayList<Entry> e1 = new ArrayList<Entry>();
        ArrayList<String> xVals = new ArrayList<String>();




                    e1.add(new Entry(0,Float.parseFloat("11000")));
xVals.add(getDateTimeStamp("2016-09-10"));
        e1.add(new Entry(1,Float.parseFloat("10309")));
        xVals.add(getDateTimeStamp("2016-09-11"));
                 /*   xVals.add(type);
                    yVals1.add(new Entry(Float.parseFloat(amon), i));*/




        int val = 10;
        for (int i = 0; i < 12; i++) {

        }

        LineDataSet d1 = new LineDataSet(e1, "Services");
       // new LineDataSet()
        d1.setLineWidth(2.5f);
        d1.setCircleSize(4.5f);
        d1.setHighLightColor(Color.rgb(244, 117, 117));
        d1.setDrawValues(true);





        ArrayList<ILineDataSet> sets = new ArrayList<ILineDataSet>();
        sets.add(d1);


        LineData cd =  new LineData(sets);
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

    private boolean checkConnStats() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().isAvailable()
                && cm.getActiveNetworkInfo().isConnected()) {
            //	new SendTask().execute();
            HashMap<String, String> nurl = session.getMobNo();
            String newurl = nurl.get(SessionManagement.KEY_MOBILE);
            invokeFX(newurl);
            //	RegTest();
            return true;
        } else {

            Toast.makeText(
                    getActivity(),
                    "No Internet Connection. Please check your internet setttings",
                    Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public void invokeFX( String mno){
        // Show Progress Dialog



        // Make RESTful webservice call using AsyncHttpClient object
        final AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(35000);
        HashMap<String, String> nurl = session.getNetURL();
        String newurl = nurl.get(SessionManagement.NETWORK_URL);

        String url = ApplicationConstants.NET_URL+ApplicationConstants.AND_ENPOINT+"servstats/"+mno+"/"+pfrom+"/"+pto+"/"+serv;

        SecurityLayer.Log("Line Chart URL",url);


        client.post(url,new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {
                // Hide Progress Dialog

                try {
                    // JSON Object
                    SecurityLayer.Log("response..:", response);
                    JSONObject obj = new JSONObject(response);
                    if(obj.optString("message").equals("SUCCESS")){
                        String total = obj.optString("total");
                        if(total == null || total.equals("")){
                            total = "0";
                        }


                        JSONArray js = obj.getJSONArray("chartdata");
                        SecurityLayer.Log("JSON Aray", js.toString());
                        if(js.length() > 0) {
                            ArrayList<ChartItem> list = new ArrayList<ChartItem>();

                            // 30 items

                            list.add(new LineChartItem(generateDataLine(js.toString()), getActivity()));


                            ChartDataAdapter cda = new ChartDataAdapter(getActivity(), list);
                            lvpie.setAdapter(cda);
                        }


                    }
                    // Else display error message
                    else{
                        Toast.makeText(getActivity(), " The device has not successfully connected to server. Please check your internet settings", Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    Toast.makeText(getActivity(), " The device has not successfully connected to server. Please check your internet settings", Toast.LENGTH_LONG).show();
                    e.printStackTrace();

                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Throwable error,
                                  String content) {

                // Hide Progress Dialog

                // When Http response code is '404'
                if(statusCode == 404){
                    Toast.makeText(getActivity(), "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if(statusCode == 500){
                    Toast.makeText(getActivity(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else{
                    Toast.makeText(getActivity(), " The device has not successfully connected to server. Please check your internet settings", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    public static String getDateTimeStamp(String tdate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd");
        Date convertedCurrentDate = null;

        try {
            convertedCurrentDate = sdf.parse(tdate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String strDate = sdf2.format(convertedCurrentDate);

        return strDate;
    }
}
