package com.stanbicagent;


import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;


import android.app.Fragment;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.stanbicagent.R;
import com.afollestad.materialdialogs.MaterialDialog;
import com.borax12.materialdaterangepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import adapter.InboxListAdapter;
import model.GetCommPerfData;
import rest.ApiClient;
import rest.ApiInterface;
import rest.ApiSecurityClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import security.SecurityLayer;

import static com.stanbicagent.ApplicationConstants.CHANNEL_ID;


public class Inbox extends Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener,AdapterView.OnItemClickListener {
    Button signup;
    Spinner sp1;
    ArrayList<String> phoneContactList = new ArrayList<String>();
    ArrayList<GetCommPerfData> planetsList = new ArrayList<GetCommPerfData>();
   // private TextView emptyView;
    InboxListAdapter aAdpt;
    ListView lv;
    Button ok;
    ProgressDialog prgDialog,prgDialog2;
    TextView acno;
    EditText accno,mobno,fnam;
    Button  calendar;
    SessionManagement session;
    TextView txtitle;
    List<GetCommPerfData> temp  = new ArrayList<GetCommPerfData>();





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

    ProgressDialog pro ;
    SearchView sv;
    String tdate,firdate;
    EditText editsearch;
    public Inbox() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.inbox, container, false);
        lv = (ListView) rootView.findViewById(R.id.lv);
     //   emptyView = (TextView) rootView.findViewById(R.id.empty_view);
        prgDialog2 = new ProgressDialog(getActivity());
        prgDialog2.setMessage("Loading Inbox....");
        // Set Cancelable as False
        session = new SessionManagement(getActivity());
        calendar = (Button) rootView.findViewById(R.id.button4);
        calendar.setOnClickListener(this);
        prgDialog2.setCancelable(false);
        planetsList.clear();
        txtitle = (TextView) rootView.findViewById(R.id.bname);

        Calendar cal = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH); // Note: zero based!
        int day = now.get(Calendar.DAY_OF_MONTH);

        sv=(SearchView) rootView.findViewById(R.id.searchView1);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String arg0) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // TODO Auto-generated method stub
                if(!(aAdpt == null)) {
                    aAdpt.getFilter().filter(query);
                }
                return false;
            }
        });

        now.set(year,month,01);

        SimpleDateFormat format1 = new SimpleDateFormat("" +
                "MMMM dd yyyy");
        System.out.println(cal.getTime());
// Output "Wed Sep 26 14:23:28 EST 2012"

        String formattednow = format1.format(cal.getTime());
        String formattedstartdate = format1.format(now.getTime());
// Output "2012-09-26"
        txtitle.setText("Inbox for "+formattedstartdate+" to "+formattednow);
      //  checkInternetConnection2();


        month = month+1;

        String frmdymonth = Integer.toString(day);
        if (day < 10) {
            frmdymonth = "0" + frmdymonth;
        }
        String frmyear = Integer.toString(year);
        frmyear = frmyear.substring(2, 4);
        String tdate = frmdymonth + "-" + (month) + "-" + frmyear;
        String firdate = "01" + "-" + (month) + "-" + frmyear;

        Calendar calfrom = Calendar.getInstance();
        calfrom.set(year,month,1);



        SetMinist(firdate,tdate);

        pro = new ProgressDialog(getActivity());
        pro.setMessage("Loading...");
        pro.setTitle("");
        pro.setCancelable(false);


      /*  new MaterialDialog.Builder(getActivity())
                .title("Enter PIN")
                .content("Please enter Login PIN")
                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)
                .input("Login PIN", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        // Do something
                        String pin =  input.toString();
                        Log.v("Login Pin",pin);
                        String encpin = Utility.getencpin(pin);
                        String usid = Utility.gettUtilUserId(getActivity());
                        String mobnoo = Utility.gettUtilMobno(getActivity());
                        String params = CHANNEL_ID+usid+"/" + encpin  + "/"+mobnoo;
                        LogRetro(params,"COMM");
                    }
                }).show();*/
     /*   lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Bundle b  = new Bundle();
                String txnref = planetsList.get(position).getrefNumber();

                b.putString("txnref",txnref);
                SecurityLayer.Log("Txn Ref",txnref);
               String title = "Txn Status";
                Fragment  fragment = new TxnStatus();

                fragment.setArguments(b);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                //  String tag = Integer.toString(title);
                fragmentTransaction.replace(R.id.container_body, fragment,title);
                fragmentTransaction.addToBackStack(title);
                ((FMobActivity) getActivity())
                        .setActionBarTitle(title);
                fragmentTransaction.commit();

            }
        });*/


        return rootView;
    }

    public void StartChartAct(int i){
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.inboxlistmenu, menu);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int index = info.position; //Use this for getting the list item value
        View view = info.targetView;
        switch(item.getItemId()) {
           case R.id.logcomp:
                Log.d("onContextItemSelected","Remove Pressed");

               Bundle b  = new Bundle();
               String txamo = planetsList.get(index).getAmount();
               String txacno = planetsList.get(index).gettoAcNum();
               String txrefno = planetsList.get(index).getrefNumber();
               String txtdate = planetsList.get(index).getTxndateTime();
               Log.v("Inbox inside click","");
               b.putString("txamo",txamo);
               b.putString("txaco",txacno);
               b.putString("txref",txrefno);
               b.putString("txdate",txtdate);
               String title = "Txn Status";
               Fragment  fragment = new LogComplaint();

               fragment.setArguments(b);
               android.app.FragmentManager fragmentManager = getFragmentManager();
               android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
               //  String tag = Integer.toString(title);
               fragmentTransaction.replace(R.id.container_body, fragment,title);
               fragmentTransaction.addToBackStack(title);
               ((FMobActivity) getActivity())
                       .setActionBarTitle(title);
               fragmentTransaction.commit();
                return true;





            default:
                return super.onContextItemSelected(item);
        }
    }
    @Override
    public void onResume(){
        super.onResume();
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        long viewId = view.getId();
Log.v("Inbox outside click","");
        if (viewId == R.id.btnrec) {
            Bundle b  = new Bundle();
            String txamo = planetsList.get(position).getAmount();
            Log.v("Inbox inside click","");
            b.putString("txamo",txamo);

            String title = "Txn Status";
            Fragment  fragment = new LogComplaint();

            fragment.setArguments(b);
            android.app.FragmentManager fragmentManager = getFragmentManager();
            android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment,title);
            fragmentTransaction.addToBackStack(title);
            ((FMobActivity) getActivity())
                    .setActionBarTitle(title);
            fragmentTransaction.commit();
        }
    }
    public void SetDialog(String msg,String title){
        new MaterialDialog.Builder(getActivity())
                .title(title)
                .content(msg)

                .negativeText("Close")
                .show();
    }

    public void SetMinist(String stdate,String enddate) {

        planetsList.clear();

        if ((prgDialog2 != null) && !(getActivity() == null)){
            prgDialog2.show();
            sv.setQuery("", false);
            sv.clearFocus();
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH) + 1; // Note: zero based!
        int day = now.get(Calendar.DAY_OF_MONTH);


        String frmdymonth = Integer.toString(day);
        if (day < 10) {
            frmdymonth = "0" + frmdymonth;
        }
        String frmyear = Integer.toString(year);
        frmyear = frmyear.substring(2, 4);
        SimpleDateFormat format1 = new SimpleDateFormat("" +
                "MMMM dd yyyy");
        String fdate = frmdymonth + "-" + (month) + "-" + frmyear;
        Calendar noww = Calendar.getInstance();
        int yearr = now.get(Calendar.YEAR);
        int monthh = now.get(Calendar.MONTH); // Note: zero based!

        noww.set(yearr, monthh, 01);
        String formattedstartdate = "01" + "-" + (month) + "-" + frmyear;

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        String usid = Utility.gettUtilUserId(getActivity());
        String agentid = Utility.gettUtilAgentId(getActivity());
        String mobnoo = Utility.gettUtilMobno(getActivity());
        String params = CHANNEL_ID + usid + "/" + agentid + "/" + mobnoo + "/TXNRPT/" + stdate + "/" + enddate;
        SecurityLayer.Log("inbox params", params);
        Inbox(params);
        /*Call<GetPerf> call = apiService.getPerfData("1",usid,agentid,"0000","TXNRPT","09-09-16",fdate);
        call.enqueue(new Callback<GetPerf>() {
            @Override
            public void onResponse(Call<GetPerf>call, Response<GetPerf> response) {

                GetPerfData data = response.body().getResults();
//                                    SecurityLayer.Log("Respnse getResults",datas.toString());
                temp = data.getResults();
                if(!(temp == null)) {
                    for(int i =0; i<temp.size(); i++){
                        String status = temp.get(i).getStatus();
                        String amon = temp.get(i).getAmount();
                        Double dbagcmsn = Double.parseDouble(amon);
                        if(((status.equals("SUCCESS"))) && (dbagcmsn > 0)){
                            SecurityLayer.Log("Amount tt",amon);
                            planetsList.add(temp.get(i));

                        }
                    }

if(temp.size() == 0){
    Toast.makeText(
                getActivity(),
                "There are no inbox records to display",
                Toast.LENGTH_LONG).show();

}
                    if(!(getActivity() == null)) {
                        aAdpt = new InboxListAdapter(planetsList, getActivity());


                        lv.setAdapter(aAdpt);
                    }
                }else{
                    Toast.makeText(
                            getActivity(),
                            "There are no records to display",
                            Toast.LENGTH_LONG).show();
                }
                if(!(getActivity() == null)) {
                    prgDialog2.dismiss();
                }
            }

            @Override
            public void onFailure(Call<GetPerf>call, Throwable t) {
                // Log error here since request failed
                SecurityLayer.Log("Throwable error",t.toString());
                Toast.makeText(
                        getActivity(),
                        "There was an error processing your request",
                        Toast.LENGTH_LONG).show();
                prgDialog2.dismiss();
            }
        });*/
    }
    }


    private void Inbox(String params) {
planetsList.clear();
        if(!(aAdpt == null)){
            aAdpt.clear();
            aAdpt.notifyDataSetChanged();
        }
        String endpoint= "report/genrpt.action";



        String usid = Utility.gettUtilUserId(getActivity());
        String agentid = Utility.gettUtilAgentId(getActivity());




        String urlparams = "";
        try {
            urlparams = SecurityLayer.genURLCBC(params,endpoint,getActivity());
            //SecurityLayer.Log("cbcurl",url);
            SecurityLayer.Log("RefURL",urlparams);
            SecurityLayer.Log("refurl", urlparams);
            SecurityLayer.Log("params", params);
        } catch (Exception e) {
            SecurityLayer.Log("encryptionerror",e.toString());
        }





        ApiInterface apiService =
                ApiSecurityClient.getClient(getActivity()).create(ApiInterface.class);


        Call<String> call = apiService.setGenericRequestRaw(urlparams);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    // JSON Object


                    SecurityLayer.Log("Cable TV Resp", response.body());
                    SecurityLayer.Log("response..:", response.body());
                    JSONObject obj = new JSONObject(response.body());
                    //obj = Utility.onresp(obj,getActivity());
                    obj = SecurityLayer.decryptTransaction(obj, getActivity());
                    SecurityLayer.Log("decrypted_response", obj.toString());




                    JSONObject comdatas = obj.optJSONObject("data");
                    JSONArray comperf = comdatas.optJSONArray("transaction");
                    //session.setString(SecurityLayer.KEY_APP_ID,appid);

                    if(!(response.body() == null)) {
                        String respcode = obj.optString("responseCode");
                        String responsemessage = obj.optString("message");

                        SecurityLayer.Log("Response Message", responsemessage);

                        if (Utility.isNotNull(respcode) && Utility.isNotNull(respcode)) {
                            if (!(Utility.checkUserLocked(respcode))) {
                                SecurityLayer.Log("Response Message", responsemessage);

                                if (respcode.equals("00")){
                                    SecurityLayer.Log("JSON Aray", comperf.toString());
                                    if(comperf.length() > 0){


                                        JSONObject json_data = null;
                                        for (int i = 0; i < comperf.length(); i++) {
                                            json_data = comperf.getJSONObject(i);
                                            //String accid = json_data.getString("benacid");
                                            String fintoacnum = "";
                                            String finfromacnum = "";

                                            String txnCode = json_data.optString("txnCode");
                                            double agentCmsn = json_data.optDouble("agentCmsn");
                                            String txndateTime = json_data.optString("txndateTime");
                                            String amount = json_data.optString("amount");
                                            String status = json_data.optString("status");
                                            String toAcNum = json_data.optString("toAcNum");
                                            String refNumber = json_data.optString("refNumber");
                                            String fromaccnum = json_data.optString("fromAccountNum");
                                            Log.v("my amount",amount);
                                            double dbam =0;
                                            if((amount != null) && (!(amount.equals("null"))) ) {
                                                Log.v("It is NOT null",amount);
   dbam = Double.parseDouble(amount);
}else{
                                                dbam = 0;
                                                Log.v("It is really null",amount);
                                            }
                                            if(txnCode.equals("CASHDEP") || txnCode.equals("FTINTRABANK") || txnCode.equals("CWDBYACT")  || txnCode.equals("BILLPAYMENT") ||  txnCode.equals("MMO")  ||  txnCode.equals("FTINTERBANK") ){
                                                fintoacnum = fromaccnum;
                                                finfromacnum  = toAcNum;
                                                toAcNum = fintoacnum;
                                                fromaccnum = finfromacnum;
                                            }
                                            if( (dbam > 0)) {
                                                planetsList.add(new GetCommPerfData(txnCode, txndateTime, agentCmsn, status, amount, toAcNum, refNumber,fromaccnum));

                                            }


                                        }
                                        if(!(getActivity() == null)) {
                                         //   planetsList.add(new GetCommPerfData("1334", "13 Sep 2012 9:12", 45.00, "N", "450.00", "3123442", "242244432","1239032"));

                                            aAdpt = new InboxListAdapter(planetsList, getActivity());


                                            lv.setAdapter(aAdpt);

                                            registerForContextMenu(lv);
                                        }


                                    }

                                }else{
                                    Toast.makeText(
                                            getActivity(),
                                            "" + responsemessage,
                                            Toast.LENGTH_LONG).show();
                                }
                            } else {
                                getActivity().finish();
                                startActivity(new Intent(getActivity(), SignInActivity.class));
                                Toast.makeText(
                                        getActivity(),
                                        "You have been locked out of the app.Please call customer care for further details",
                                        Toast.LENGTH_LONG).show();

                            }
                        } else {

                            Toast.makeText(
                                    getActivity(),
                                    "There was an error on your request",
                                    Toast.LENGTH_LONG).show();


                        }
                    } else {

                        Toast.makeText(
                                getActivity(),
                                "There was an error on your request",
                                Toast.LENGTH_LONG).show();


                    }
                    // prgDialog2.dismiss();




                } catch (JSONException e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    // TODO Auto-generated catch block
                    Toast.makeText(getActivity(), getActivity().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                    // SecurityLayer.Log(e.toString());
                    ((FMobActivity) getActivity()).SetForceOutDialog(getString(R.string.forceout),getString(R.string.forceouterr),getActivity());


                } catch (Exception e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    ((FMobActivity) getActivity()).SetForceOutDialog(getString(R.string.forceout),getString(R.string.forceouterr),getActivity());

                    // SecurityLayer.Log(e.toString());
                }
                if(!(getActivity() == null) && !(prgDialog2 == null) && prgDialog2.isShowing()) {
                    prgDialog2.dismiss();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                // Log error here since request failed
                SecurityLayer.Log("throwable error",t.toString());


                Toast.makeText(
                        getActivity(),
                        "There was an error on your request",
                        Toast.LENGTH_LONG).show();


                if(!(getActivity() == null) && !(prgDialog2 == null) && prgDialog2.isShowing()) {
                    prgDialog2.dismiss();
                }
                ((FMobActivity) getActivity()).SetForceOutDialog(getString(R.string.forceout),getString(R.string.forceouterr),getActivity());

            }
        });

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button4) {
            Calendar now = Calendar.getInstance();
            DatePickerDialog dpd = com.borax12.materialdaterangepicker.date.DatePickerDialog.newInstance(
                    this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );
            dpd.setMaxDate(now);
            dpd.show(getFragmentManager(), "Datepickerdialog");


        }


    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth, int yearEnd, int monthOfYearEnd, int dayOfMonthEnd) {
        String date = "Inbox : From- " + dayOfMonth + "/" + (++monthOfYear) + "/" + year + " To " + dayOfMonthEnd + "/" + (++monthOfYearEnd) + "/" + yearEnd;
        txtitle.setText(date);
        Calendar calfrom = Calendar.getInstance();
        Calendar calto = Calendar.getInstance();
        calto.set(yearEnd,monthOfYearEnd,dayOfMonthEnd);
        calfrom.set(year,monthOfYear,dayOfMonth);

        if(calfrom.before(calto)) {
            //   fromdate.setText(date);
            String frmdymonth = Integer.toString(dayOfMonth);
            if (dayOfMonth < 10) {
                frmdymonth = "0" + frmdymonth;
            }
            String frmyear = Integer.toString(year);
            frmyear = frmyear.substring(2, 4);
            String  fromd = frmdymonth + "-" +(monthOfYear) + "-" + frmyear;
            String frmenddymonth = Integer.toString(dayOfMonthEnd);
            if (dayOfMonthEnd < 10) {
                frmenddymonth = "0" + frmenddymonth;
            }

            String frmendyr = Integer.toString(yearEnd);
            frmendyr = frmendyr.substring(2, 4);
            String endd = frmenddymonth + "-" + (monthOfYearEnd) + "-" + frmendyr;
            SetMinist(fromd,endd);
        }else{
            Toast.makeText(
                    getActivity(),
                    "Please ensure the from date is before the after date",
                    Toast.LENGTH_LONG).show();
        }
    }


    // this will find a bluetooth printer device
    void findBT() {

        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if (mBluetoothAdapter == null) {
                Toast.makeText(
                        getActivity(),
                        "No bluetooth available",
                        Toast.LENGTH_LONG).show();
            }

            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, 0);
            }

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {

                    // RPP300 is the name of the bluetooth printer device
                    // we got this name from the list of paired devices
                    if (device.getName().equals("BlueTooth Printer")) {
                        mmDevice = device;
                        break;
                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // tries to open a connection to the bluetooth printer device
    void openBT() throws IOException {
        try {

            // Standard SerialPortService ID
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();

            beginListenForData();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // this will send text data to be printed by the bluetooth printer
    void sendData(String msg) throws IOException {
        try {


            mmOutputStream = mmSocket.getOutputStream();
            mmOutputStream.write(msg.getBytes());
            //  sendData(bytes);
            //   mmOutputStream.write(bytes);


            //  ImageIO.write(mmOutputStream, "PNG", myNewPNGFile);


        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            closeBT();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    // close the connection to bluetooth printer.
    void closeBT() throws IOException {
        try {
            stopWorker = true;
            mmOutputStream.close();
            mmInputStream.close();
            mmSocket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
 * after opening a connection to bluetooth printer device,
 * we have to listen and check if a data were sent to be printed.
 */
    void beginListenForData() {
        try {
            final Handler handler = new Handler();

            // this is the ASCII code for a newline character
            final byte delimiter = 10;

            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            workerThread = new Thread(new Runnable() {
                public void run() {

                    while (!Thread.currentThread().isInterrupted() && !stopWorker) {

                        try {

                            int bytesAvailable = mmInputStream.available();

                            if (bytesAvailable > 0) {

                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);

                                for (int i = 0; i < bytesAvailable; i++) {

                                    byte b = packetBytes[i];
                                    if (b == delimiter) {

                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(
                                                readBuffer, 0,
                                                encodedBytes, 0,
                                                encodedBytes.length
                                        );

                                        // specify US-ASCII encoding
                                        final String data = new String(encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;

                                        // tell the user data were sent to bluetooth printer device
                                        handler.post(new Runnable() {
                                            public void run() {
                                                //   myLabel.setText(data);
                                            }
                                        });

                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }

                        } catch (IOException ex) {
                            stopWorker = true;
                        }

                    }
                }
            });

            workerThread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
