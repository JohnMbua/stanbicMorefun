package com.stanbicagent;

import android.content.Context;
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
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
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

import listviewitems.BarChartItem;
import listviewitems.ChartItem;
import security.SecurityLayer;


public class BarChartFrag extends Fragment {
ImageView imageView1;
    ListView lvpie;
    SessionManagement session;
    String serv,pfrom,pto;

    public BarChartFrag() {
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
            serv = type;
            pfrom = bundle.getString("pfrom");
            pto = bundle.getString("pto");
            SecurityLayer.Log("Bar Chart Service",serv);
        }
  lvpie  = (ListView) root.findViewById(R.id.lvpie);
        //checkInternetConnection();
      //*  checkPiConnect();
      //*
        session = new SessionManagement(getActivity());
//checkConnStats();
        ArrayList<ChartItem> list = new ArrayList<ChartItem>();

        // 30 items

        list.add(new BarChartItem(generateBar(""),getActivity()));


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
    private BarData generateBar(String jso) {
        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();



                    xVals.add(getDateTimeStamp("2016-09-10"));

                    yVals1.add(new BarEntry(0, Integer.parseInt("11000")));

        xVals.add(getDateTimeStamp("2016-09-11"));
        yVals1.add(new BarEntry(1,Integer.parseInt("10309")));

                BarDataSet set1 = new BarDataSet(yVals1, "Services");
              //  set1.setBarSpacePercent(35f);

                ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
                dataSets.add(set1);

               // BarData data = new BarData(xVals, dataSets);
        BarData data = new BarData(dataSets);
return  data;

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

        SecurityLayer.Log("Bar Chart URL",url);

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

                            list.add(new BarChartItem(generateBar(js.toString()),getActivity()));


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
