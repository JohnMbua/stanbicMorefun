package com.stanbicagent;


import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.borax12.materialdaterangepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import adapter.InboxListAdapter;
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
import com.stanbicagent.R;


public class InboxActivity extends BaseActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener,AdapterView.OnItemClickListener, DateRangePickerFragment.OnDateRangeSelectedListener {
    Button signup;
    Spinner sp1;
    ArrayList<String> phoneContactList = new ArrayList<String>();
    ArrayList<GetCommPerfData> planetsList = new ArrayList<GetCommPerfData>();
    // private TextView emptyView;
    InboxListAdapter aAdpt;
    ListView lv;
    Button ok;
    ProgressDialog prgDialog2;
    TextView acno;
    EditText accno,mobno,fnam;
    Button  calendar;
    SessionManagement session;
    TextView txtitle,txfrom;
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
    private Toolbar mToolbar;
    SimpleDateFormat format1 = new SimpleDateFormat("" +
            "MMMM dd yyyy");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inboxact);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        //  mToolbar.setTitle("Inbox");
        setSupportActionBar(mToolbar);
        // Get the ActionBar here to configure the way it behaves.
        final ActionBar ab = getSupportActionBar();
        //ab.setHomeAsUpIndicator(R.drawable.ic_menu); // set a custom icon for the default home button
        ab.setDisplayShowHomeEnabled(true); // show or hide the default home button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
        ab.setDisplayShowTitleEnabled(false); // disable the default title element here (for centered title)




        lv = (ListView)findViewById(R.id.lv);
        //   emptyView = (TextView) rootView.findViewById(R.id.empty_view);
        prgDialog2 = new ProgressDialog(this);
        prgDialog2.setMessage("Loading Inbox....");
        // Set Cancelable as False
        session = new SessionManagement(this);
        calendar = (Button) findViewById(R.id.button4);
        calendar.setOnClickListener(this);
        prgDialog2.setCancelable(false);
        planetsList.clear();
        txtitle = (TextView) findViewById(R.id.bname);
        txfrom = (TextView) findViewById(R.id.from);
        Calendar cal = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH); // Note: zero based!
        int day = now.get(Calendar.DAY_OF_MONTH);

        sv=(SearchView) findViewById(R.id.searchView1);
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
        sv.setIconified(false);
        now.set(year,month,01);


        System.out.println(cal.getTime());
// Output "Wed Sep 26 14:23:28 EST 2012"

        String formattednow = format1.format(cal.getTime());
        String formattedstartdate = format1.format(now.getTime());
// Output "2012-09-26"
        txtitle.setText(formattednow);
        txfrom.setText(formattedstartdate);
        //  checkInternetConnection2();


        month = month+1;

        String frmdymonth = Integer.toString(day);
        if (day < 10) {
            frmdymonth = "0" + frmdymonth;
        }

        String frmdymonthplus = Integer.toString(month);
        if (month < 10) {
            frmdymonthplus = "0" + frmdymonthplus;
        }
        String frmyear = Integer.toString(year);

        String tdate = frmdymonth + "-" + (frmdymonthplus) + "-" + frmyear;
        String firdate = "01" + "-" + (frmdymonthplus) + "-" + frmyear;

        Calendar calfrom = Calendar.getInstance();
        calfrom.set(year,month,1);



        SetMinist(firdate,tdate);

        pro = new ProgressDialog(this);
        pro.setMessage("Loading...");
        pro.setTitle("");
        pro.setCancelable(false);


      /* lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Log.d("onContextItemSelected","Remove Pressed");

                Bundle b  = new Bundle();
                String txamo = planetsList.get(position).getAmount();
                String txacno = planetsList.get(position).gettoAcNum();
                String txrefno = planetsList.get(position).getrefNumber();
                String txtdate = planetsList.get(position).getTxndateTime();
                SecurityLayer.Log("Inbox inside click","");
                b.putString("txamo",txamo);
                b.putString("txaco",txacno);
                b.putString("txref",txrefno);
                b.putString("txdate",txtdate);
                String title = "Txn Status";
                Fragment fragment = new LogComplaint();

                fragment.setArguments(b);
                android.app.FragmentManager fragmentManager = getFragmentManager();
                android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                //  String tag = Integer.toString(title);
                fragmentTransaction.replace(R.id.container_body, fragment,title);
                fragmentTransaction.addTSessoBackStack(title);
                ((FMobActivity) getApplicationContext())
                        .setActionBarTitle(title);
                fragmentTransaction.commit();
            }
        });*/
        registerForContextMenu(lv);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                GetCommPerfData p = planetsList.get(position);
                String txncode = p.getTxnCode();
                String toAcnum = p.gettoAcNum();
                String fromacnum = p.getFromAcnum();
                String txtrfno = p.getrefNumber();

                String servtype = Utility.convertTxnCodetoServ(txncode);
                String txtdatetime = p.getTxndateTime();
                String statuss = p.getStatus();

                String narr =  fromacnum;
                String nartor = toAcnum;


                String amoo =  ApplicationConstants.KEY_NAIRA+Utility.returnNumberFormat(p.getAmount());;
                FragmentManager fm = getSupportFragmentManager();
                CommReceipt editNameDialog = new CommReceipt();
                Bundle bundle = new Bundle();
                bundle.putString("narr",narr);
                bundle.putString("refno",txtrfno);
                bundle.putString("datetime",txtdatetime);
                bundle.putString("amo",amoo);
                bundle.putString("servtype",servtype);
                bundle.putString("status",statuss);
                bundle.putString("narrtor",nartor);
                editNameDialog.setArguments(bundle);
                editNameDialog.show(fm, "fragment_edit_name");
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    public void StartChartAct(int i){
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        SecurityLayer.Log("Am i in","Yes");
        MenuInflater inflater = getMenuInflater();
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

             /*

                SecurityLayer.Log("Inbox inside click","");

                String title = "Txn Status";
                Fragment fragment = new LogComplaint();

                fragment.setArguments(b);
                android.app.FragmentManager fragmentManager = getFragmentManager();
                android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                //  String tag = Integer.toString(title);
                fragmentTransaction.replace(R.id.container_body, fragment,title);
                fragmentTransaction.addToBackStack(title);
                ((FMobActivity) getApplicationContext())
                        .setActionBarTitle(title);
                fragmentTransaction.commit();*/

                String txamo = planetsList.get(index).getAmount();
                String txacno = planetsList.get(index).gettoAcNum();
                String txrefno = planetsList.get(index).getrefNumber();
                String txtdate = planetsList.get(index).getTxndateTime();
                Bundle b  = new Bundle();
                b.putString("txamo",txamo);
                b.putString("txaco",txacno);
                b.putString("txref",txrefno);
                b.putString("txdate",txtdate);
                Intent pIntent = new Intent(this,LogCompActivity.class);


                pIntent.putExtras(b);
                startActivity(pIntent);
                return true;









            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        long viewId = view.getId();
        SecurityLayer.Log("Inbox outside click","");
        if (viewId == R.id.btnrec) {
          /*  Bundle b  = new Bundle();
            String txamo = planetsList.get(position).getAmount();
            SecurityLayer.Log("Inbox inside click","");
            b.putString("txamo",txamo);

            String title = "Txn Status";
            Fragment  fragment = new LogComplaint();

            fragment.setArguments(b);
            android.app.FragmentManager fragmentManager = getFragmentManager();
            android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment,title);
            fragmentTransaction.addToBackStack(title);
            ((FMobActivity) getApplicationContext())
                    .setActionBarTitle(title);
            fragmentTransaction.commit();*/
        }
    }
    public void SetDialog(String msg,String title){
        new MaterialDialog.Builder(getApplicationContext())
                .title(title)
                .content(msg)

                .negativeText("Close")
                .show();
    }

    public void SetMinist(String stdate,String enddate) {

        planetsList.clear();

        if ((prgDialog2 != null) && !(getApplicationContext() == null)){
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
            String usid = Utility.gettUtilUserId(getApplicationContext());
            String agentid = Utility.gettUtilAgentId(getApplicationContext());
            String mobnoo = Utility.gettUtilMobno(getApplicationContext());
            String params = ApplicationConstants.CHANNEL_ID + usid + "/INBOX/" + stdate + "/" + enddate;
            http://localhost:9090/agencyapi/app/report/inbox.action/1/112091011/INBOX/03-03-2019/12-03-2019
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
                getApplicationContext(),
                "There are no inbox records to display",
                Toast.LENGTH_LONG).show();

}
                    if(!(getApplicationContext() == null)) {
                        aAdpt = new InboxListAdapter(planetsList, getApplicationContext());


                        lv.setAdapter(aAdpt);
                    }
                }else{
                    Toast.makeText(
                            getApplicationContext(),
                            "There are no records to display",
                            Toast.LENGTH_LONG).show();
                }
                if(!(getApplicationContext() == null)) {
                    prgDialog2.dismiss();
                }
            }

            @Override
            public void onFailure(Call<GetPerf>call, Throwable t) {
                // Log error here since request failed
                SecurityLayer.Log("Throwable error",t.toString());
                Toast.makeText(
                        getApplicationContext(),
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
        String endpoint= "report/inbox.action";



        String usid = Utility.gettUtilUserId(this);
        String agentid = Utility.gettUtilAgentId(this);




        String urlparams = "";
        try {
            urlparams = SecurityLayer.genURLCBC(params,endpoint,this);
            //SecurityLayer.Log("cbcurl",url);
            SecurityLayer.Log("RefURL",urlparams);
            SecurityLayer.Log("refurl", urlparams);
            SecurityLayer.Log("params", params);
            SecurityLayer.Log("urlparam",params);
        } catch (Exception e) {
            SecurityLayer.Log("encryptionerror",e.toString());
        }





        ApiInterface apiService =
                ApiSecurityClient.getClient(getApplicationContext()).create(ApiInterface.class);


        Call<String> call = apiService.setGenericRequestRaw(urlparams);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    // JSON Object


                    SecurityLayer.Log("Cable TV Resp", response.body());
                    SecurityLayer.Log("response..:", response.body());
                    JSONObject obj = new JSONObject(response.body());
                    //obj = Utility.onresp(obj,this);
                    obj = SecurityLayer.decryptTransaction(obj, getApplicationContext());
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
                                    if(!(comperf == null)) {
                                        if (comperf.length() > 0) {


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
                                                String toAcNum = json_data.optString("custAccNum");
                                                String refNumber = json_data.optString("refNumber");
                                                String fromaccnum = json_data.optString("agentAccNum");
                                                SecurityLayer.Log("my amount", amount);
                                                double dbam = 0;
                                                if ((amount != null) && (!(amount.equals("null")))) {
                                                    SecurityLayer.Log("It is NOT null", amount);
                                                    dbam = Double.parseDouble(amount);
                                                } else {
                                                    dbam = 0;
                                                    SecurityLayer.Log("It is really null", amount);
                                                }
                                                if (txnCode.equals("CASHDEP") || txnCode.equals("FTINTRABANK") || txnCode.equals("CWDBYACT") || txnCode.equals("BILLPAYMENT") || txnCode.equals("MMO") || txnCode.equals("FTINTERBANK")) {
                                                    fintoacnum = fromaccnum;
                                                    finfromacnum = toAcNum;
                                                    toAcNum = fintoacnum;
                                                    fromaccnum = finfromacnum;
                                                }
                                                if ((dbam > 0)) {
                                                    planetsList.add(new GetCommPerfData(txnCode, txndateTime, agentCmsn, status, amount, toAcNum, refNumber, fromaccnum));

                                                }


                                            }
                                            if (!(this == null)) {
                                                //   planetsList.add(new GetCommPerfData("1334", "13 Sep 2012 9:12", 45.00, "N", "450.00", "3123442", "242244432","1239032"));

                                                aAdpt = new InboxListAdapter(planetsList, InboxActivity.this);


                                                lv.setAdapter(aAdpt);

                                                registerForContextMenu(lv);
                                            }


                                        }
                                    }

                                }else{
                                    Toast.makeText(
                                            getApplicationContext(),
                                            "" + responsemessage,
                                            Toast.LENGTH_LONG).show();
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




                } catch (JSONException e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    // TODO Auto-generated catch block
                    Toast.makeText(getApplicationContext(), getApplicationContext().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                    // SecurityLayer.Log(e.toString());
                    SetForceOutDialog(getString(R.string.forceout),getString(R.string.forceouterr),getApplicationContext());


                } catch (Exception e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    SetForceOutDialog(getString(R.string.forceout),getString(R.string.forceouterr),getApplicationContext());

                    // SecurityLayer.Log(e.toString());
                }
                if(!(getApplicationContext() == null) && !(prgDialog2 == null) && prgDialog2.isShowing()) {
                    prgDialog2.dismiss();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                // Log error here since request failed
                SecurityLayer.Log("throwable error",t.toString());


                Toast.makeText(
                        getApplicationContext(),
                        "There was an error on your request",
                        Toast.LENGTH_LONG).show();


                if(!(getApplicationContext() == null) && !(prgDialog2 == null) && prgDialog2.isShowing()) {
                    prgDialog2.dismiss();
                }
                SetForceOutDialog(getString(R.string.forceout),getString(R.string.forceouterr),getApplicationContext());

            }
        });

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button4) {
          /*  Calendar now = Calendar.getInstance();
            DatePickerDialog dpd = DatePickerDialog.newInstance(
                    InboxActivity.this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );
            dpd.setMaxDate(now);
            dpd.show(getFragmentManager(), "Datepickerdialog");*/



            DateRangePickerFragment dateRangePickerFragment= DateRangePickerFragment.newInstance(this,false);
            dateRangePickerFragment.show(getSupportFragmentManager(),"datePicker");
        }


    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth, int yearEnd, int monthOfYearEnd, int dayOfMonthEnd) {
        //   String date = "Inbox : From- " + dayOfMonth + "/" + (monthOfYear) + "/" + year + " To " + dayOfMonthEnd + "/" + (monthOfYearEnd) + "/" + yearEnd;

        Calendar clfrom = Calendar.getInstance();
        Calendar clto = Calendar.getInstance();
        clfrom.set(year,monthOfYear,dayOfMonth);
        clto.set(yearEnd,monthOfYearEnd,dayOfMonthEnd);

        String formattedfrom = format1.format(clfrom.getTime());
        String formattedto = format1.format(clto.getTime());
        txtitle.setText(formattedto);
        txfrom.setText(formattedfrom);
        ++monthOfYear;
        ++monthOfYearEnd;

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

            String frmdymonthplusyear = Integer.toString(monthOfYear);
            if (monthOfYearEnd < 10) {
                frmdymonthplusyear = "0" + frmdymonthplusyear;
            }

            String frmyear = Integer.toString(year);


            String frmenddymonth = Integer.toString(dayOfMonthEnd);
            if (dayOfMonthEnd < 10) {
                frmenddymonth = "0" + frmenddymonth;
            }
            String frmdymonthplus = Integer.toString(monthOfYearEnd);
            if (monthOfYearEnd < 10) {
                frmdymonthplus = "0" + frmdymonthplus;
            }
            String frmendyr = Integer.toString(yearEnd);

            String endd = frmenddymonth + "-" + (frmdymonthplus) + "-" + frmendyr;
            String  fromd = frmdymonth + "-" +(frmdymonthplusyear) + "-" + frmyear;
            SetMinist(fromd,endd);
        }else{
            Toast.makeText(
                    getApplicationContext(),
                    "Please ensure the from date is before the after date",
                    Toast.LENGTH_LONG).show();
        }
    }

/*
    // this will find a bluetooth printer device
    void findBT() {

        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if (mBluetoothAdapter == null) {
                Toast.makeText(
                        getApplicationContext(),
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

    *//*
     * after opening a connection to bluetooth printer device,
     * we have to listen and check if a data were sent to be printed.
     *//*
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
    }*/

    @Override
    public void onPause()
    {

        super.onPause();

        long secs = (new Date().getTime())/1000;
        SecurityLayer.Log("Seconds Loged",Long.toString(secs));
        session.putCurrTime(secs);
    }

    public void SetForceOutDialog(String msg, final String title, final Context c) {
        if (!(InboxActivity.this == null)) {
            new MaterialDialog.Builder(InboxActivity.this)
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
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            // Staring Login Activity
                            startActivity(i);

                        }
                    })
                    .show();
        }
    }

    @Override
    public void onDateRangeSelected(int dayOfMonth, int monthOfYear,int year ,int dayOfMonthEnd, int monthOfYearEnd, int yearEnd ) {
     /*   String date = " From- " + dayOfMonth + "/" + (++monthOfYear) + "/" + year + " To " + dayOfMonthEnd + "/" + (++monthOfYearEnd) + "/" + yearEnd;
   //     txtitle.setText(date);
*/
        Calendar clfrom = Calendar.getInstance();
        Calendar clto = Calendar.getInstance();
        clfrom.set(year,monthOfYear,dayOfMonth);
        clto.set(yearEnd,monthOfYearEnd,dayOfMonthEnd);

        String formattedfrom = format1.format(clfrom.getTime());
        String formattedto = format1.format(clto.getTime());
        txtitle.setText(formattedto);
        txfrom.setText(formattedfrom);
        ++monthOfYear;
        ++monthOfYearEnd;
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
            String frmdymonthplusyear = Integer.toString(monthOfYear);
            if (monthOfYearEnd < 10) {
                frmdymonthplusyear = "0" + frmdymonthplusyear;
            }
            String frmyear = Integer.toString(year);

            String  fromd = frmdymonth + "-" +(frmdymonthplusyear) + "-" + frmyear;
            String frmenddymonth = Integer.toString(dayOfMonthEnd);
            if (dayOfMonthEnd < 10) {
                frmenddymonth = "0" + frmenddymonth;
            }
            String frmdymonthplus = Integer.toString(monthOfYearEnd);
            if (monthOfYearEnd < 10) {
                frmdymonthplus = "0" + frmdymonthplus;
            }

            String frmendyr = Integer.toString(yearEnd);

            String endd = frmenddymonth + "-" + (frmdymonthplus) + "-" + frmendyr;
            SetMinist(fromd,endd);
        }else{
            Toast.makeText(
                    getApplicationContext(),
                    "Please ensure the from date is before the after date",
                    Toast.LENGTH_LONG).show();
        }
    }




    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub


        if(prgDialog2!=null && prgDialog2.isShowing()){

            prgDialog2.dismiss();
        }
        super.onDestroy();
    }


    public  void LogOut(){
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
