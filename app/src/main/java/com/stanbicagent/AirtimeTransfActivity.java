package com.stanbicagent;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.ActionBar;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.stanbicagent.R;
import com.afollestad.materialdialogs.MaterialDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import model.GetAirtimeBillers;
import model.GetAirtimeBillersData;
import rest.ApiClient;
import rest.ApiInterface;
import rest.ApiSecurityClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import security.SecurityLayer;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.stanbicagent.ApplicationConstants.CHANNEL_ID;

public class AirtimeTransfActivity extends BaseActivity implements View.OnClickListener {
    SessionManagement session;

    List<GetAirtimeBillersData> planetsList = new ArrayList<GetAirtimeBillersData>();
    EditText amon, edacc,pno,txtamount,txtnarr,phonenumb;
    Spinner sp1, sp5, sp7;
    //  RecyclerView lvbann;
    ArrayAdapter<GetAirtimeBillersData> mobadapt;

    TextView tx;
    LinearLayoutManager layoutManager,layoutManager2;

    Button btn2;
    String telcochosen;
    ProgressDialog prgDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_airtime_transf);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        // Get the ActionBar here to configure the way it behaves.
        final ActionBar ab = getSupportActionBar();
        //ab.setHomeAsUpIndicator(R.drawable.ic_menu); // set a custom icon for the default home button
        ab.setDisplayShowHomeEnabled(true); // show or hide the default home button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
        ab.setDisplayShowTitleEnabled(false); // disable the default title element here (for centered title)




        layoutManager2 = new LinearLayoutManager(this);
        layoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        session = new SessionManagement(this);
        //layoutManager.scrollToPosition(currPos);

//        lvbann.setLayoutManager(layoutManager2);
        //  Pop();
        prgDialog = new ProgressDialog(this);

        prgDialog.setMessage("Please wait...");
        prgDialog.setCancelable(false);
        sp1 = (Spinner) findViewById(R.id.spin1);

        btn2 = (Button) findViewById(R.id.button2);
        btn2.setOnClickListener(this);



        phonenumb = (EditText) findViewById(R.id.phonenumb);
        txtamount = (EditText) findViewById(R.id.amount);


        View.OnFocusChangeListener ofcListener = new MyFocusChangeListener();
        phonenumb.setOnFocusChangeListener(ofcListener);
        txtamount.setOnFocusChangeListener(ofcListener);
        String strservdata = session.getString(SessionManagement.KEY_AIRTIME);
        if(!(strservdata == null)){
            JSONArray servdata = null;
            try {
                servdata = new JSONArray(strservdata);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(servdata.length() > 0) {
                SetAirtimStored();
            }else{
                if(Utility.checkInternetConnection(getApplicationContext())) {
                    PopulateAirtime();
                }
            }
        }else {
            if(Utility.checkInternetConnection(getApplicationContext())) {
                PopulateAirtime();
            }
        }
        sp1.setOnTouchListener(spinnerOnTouch);
        sp1.setOnKeyListener(spinnerOnKey);
        sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                SecurityLayer.Log("Good Adapter","Am I in");
                if(sp1.getAdapter() == null) {
                    if (Utility.checkInternetConnection(getApplicationContext())) {
                        PopulateAirtime();
                    }
                }else{
                    SecurityLayer.Log("Good Adapter","Good Adapter");
                }
            } // to close the onItemSelected
            public void onNothingSelected(AdapterView<?> parent)
            {
                SecurityLayer.Log("Good Adapter","Am I in");
                if(sp1.getAdapter() == null) {
                    if (Utility.checkInternetConnection(getApplicationContext())) {
                        PopulateAirtime();
                    }
                }else{
                    SecurityLayer.Log("Good Adapter","Good Adapter");
                }

            }
        });
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();    //Call the back button's method
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button2) {

            String phoneno = phonenumb.getText().toString();
            String amou = txtamount.getText().toString();

            amou = Utility.returnNumberFormat(amou);
            String mnop = sp1.getSelectedItem().toString();

            int spinpos = sp1.getSelectedItemPosition();
            String billid = planetsList.get(spinpos).getBillerId();
            String serviceid = planetsList.get(spinpos).getSId();

            if (Utility.checkInternetConnection(getApplicationContext())) {


                if (Utility.isNotNull(phoneno)) {
                    if (Utility.isNotNull(amou)) {
                        if(phoneno.length() >= 10){
                            String nwamo = amou.replace(",", "");
                            SecurityLayer.Log("New Amount",nwamo);
                            double txamou = Double.parseDouble(nwamo);
//                            if (txamou >= 1000000) {
                                // if ((planetsList.size() - 1) == sp1.getSelectedItemPosition()) {
                                if (!(serviceid.equals("0000"))) {

                                   /* Bundle b = new Bundle();
                                    b.putString("mobno", phoneno);
                                    b.putString("amou", amou);
                                    b.putString("telcoop", mnop);

                                    b.putString("billid", billid);
                                    b.putString("serviceid", serviceid);
                                    Fragment fragment = new ConfirmAirtime();

                                    fragment.setArguments(b);
                                    FragmentManager fragmentManager = getFragmentManager();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    //  String tag = Integer.toString(title);
                                    fragmentTransaction.replace(R.id.container_body, fragment, "Confirm Airtime");
                                    fragmentTransaction.addToBackStack("Confirm Airtime");
                                    ((FMobActivity) getApplicationContext())
                                            .setActionBarTitle("Confirm Airtime");
                                    fragmentTransaction.commit();*/

                                    Intent intent  = new Intent(AirtimeTransfActivity.this,ConfirmAirtimeActivity.class);



                                    intent.putExtra("mobno", phoneno);
                                    intent.putExtra("amou", amou);
                                    intent.putExtra("telcoop", mnop);

                                    intent.putExtra("billid", billid);
                                    intent.putExtra("serviceid", serviceid);

                                    startActivity(intent);

                                } else {
                                    Toast.makeText(
                                            getApplicationContext(),
                                            "Please select a valid mobile network operator",
                                            Toast.LENGTH_LONG).show();
                                }
//                            } else {
//                                Toast.makeText(
//                                        getApplicationContext(),
//                                        "Please enter an airtime value more than 100 Naira",
//                                        Toast.LENGTH_LONG).show();
//                            }
                        } else {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Please ensure valid mobile number has been set",
                                    Toast.LENGTH_LONG).show();
                        }
                    }else {
                        Toast.makeText(
                                getApplicationContext(),
                                "Please enter a valid value for Amount",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(
                            getApplicationContext(),
                            "Please enter a value for Phone Number",
                            Toast.LENGTH_LONG).show();
                }
            }



        }
    }

    private View.OnTouchListener spinnerOnTouch = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                //Your code
                Log.v("Spinner on click","Spinner on click");
                if(sp1.getAdapter() == null) {
                    if (Utility.checkInternetConnection(getApplicationContext())) {
                        PopulateAirtime();
                    }
                }else{
                    SecurityLayer.Log("Good Adapter","Good Adapter");
                }


            }
            return false;
        }
    };
    private  View.OnKeyListener spinnerOnKey = new View.OnKeyListener() {
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                //your code
                Log.v("Spinner on click","Spinner on click");
                if(sp1.getAdapter() == null) {
                    if (Utility.checkInternetConnection(getApplicationContext())) {
                        PopulateAirtime();
                    }
                }else{
                    SecurityLayer.Log("Good Adapter","Good Adapter");
                }


                return true;
            } else {
                return false;
            }
        }
    };

    public void StartChartAct(int i) {


    }

    @Override
    public void onResume() {
        super.onResume();
        // put your code here...

    }

    private class MyFocusChangeListener implements View.OnFocusChangeListener {

        public void onFocusChange(View v, boolean hasFocus){

            if(v.getId() == R.id.amount && !hasFocus) {
                InputMethodManager imm =  (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                String txt = txtamount.getText().toString();
                String fbal = Utility.returnNumberFormat(txt);
                //  txtamount.setText(fbal);

            }

            if(v.getId() == R.id.ednarr && !hasFocus) {
                InputMethodManager imm =  (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);


            }
            if(v.getId() == R.id.sendname && !hasFocus) {
                InputMethodManager imm =  (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);


            }
            if(v.getId() == R.id.sendnumber && !hasFocus) {
                InputMethodManager imm =  (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);


            }
            if(v.getId() == R.id.input_payacc && !hasFocus) {
                InputMethodManager imm =  (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);


            }
        }
    }

    public static interface ClickListener {
        public void onClick(View view, int position);

        public void onLongClick(View view, int position);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private AirtimeTransf.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final AirtimeTransf.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean b) {

        }
    }


    public void SetAirtimStored(){
        planetsList.clear();
        try{
            String strservdata = session.getString(SessionManagement.KEY_AIRTIME);
            JSONArray servdata = new JSONArray(strservdata);
            if(servdata.length() > 0){

                JSONObject json_data = null;
                for (int i = 0; i < servdata.length(); i++) {
                    json_data = servdata.getJSONObject(i);
                    //String accid = json_data.getString("benacid");


                    String sid = json_data.optString("sid");

                    String id = json_data.optString("id");
                    String billerName = json_data.optString("billerName");



                    planetsList.add(new GetAirtimeBillersData(sid,id,billerName));




                }
                if(!(planetsList == null)) {
                    if(planetsList.size() > 0) {
                        GetAirtimeBillersData sa = new GetAirtimeBillersData("0000","0000","Select Network Operator");
                        planetsList.add(sa);
                        SecurityLayer.Log("Get Biller Data Name", planetsList.get(0).getBillerName());
                        Collections.sort(planetsList, new Comparator<GetAirtimeBillersData>(){
                            public int compare(GetAirtimeBillersData d1, GetAirtimeBillersData d2){
                                return d1.getBillerName().compareTo(d2.getBillerName());
                            }
                        });
                        //  Collections.swap(planetsList,0,planetsList.size()-1);

                        mobadapt = new ArrayAdapter<GetAirtimeBillersData>(AirtimeTransfActivity.this, R.layout.my_spinner, planetsList);
                        mobadapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        sp1.setAdapter(mobadapt);
                        sp1.setSelection(planetsList.size() -1);
                    }else{
                        Toast.makeText(
                                getApplicationContext(),
                                "No airtime billers available  ",
                                Toast.LENGTH_LONG).show();
                    }
                }
                prgDialog.dismiss();

            }else{
                Toast.makeText(
                        getApplicationContext(),
                        "No services available  ",
                        Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            SecurityLayer.Log("encryptionJSONException", e.toString());
            // TODO Auto-generated catch block
            Toast.makeText(getApplicationContext(), getApplicationContext().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
            // SecurityLayer.Log(e.toString());

        }
    }


    public void SetPop(){
        planetsList.clear();



        prgDialog.show();

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<GetAirtimeBillers> call = apiService.getAirBillPay("1","CEVA","ERWE0000000001","0000","1");
        call.enqueue(new Callback<GetAirtimeBillers>() {
            @Override
            public void onResponse(Call<GetAirtimeBillers>call, Response<GetAirtimeBillers> response) {
                String responsemessage = response.body().getMessage();

                SecurityLayer.Log("Response Message",responsemessage);

                planetsList = response.body().getResults();
                GetAirtimeBillersData sa = new GetAirtimeBillersData("0000","0000","Select Network Operator");
                planetsList.add(sa);

                if(!(planetsList == null)) {
                    if(planetsList.size() > 0) {
                        SecurityLayer.Log("Get Biller Data Name", planetsList.get(0).getBillerName());
                        Collections.sort(planetsList, new Comparator<GetAirtimeBillersData>(){
                            public int compare(GetAirtimeBillersData d1, GetAirtimeBillersData d2){
                                return d1.getBillerName().compareTo(d2.getBillerName());
                            }
                        });
                        //  Collections.swap(planetsList,0,planetsList.size()-1);

                        mobadapt = new ArrayAdapter<GetAirtimeBillersData>(AirtimeTransfActivity.this, R.layout.my_spinner, planetsList);
                        mobadapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        sp1.setAdapter(mobadapt);
                        sp1.setSelection(planetsList.size() -1);
                    }else{
                        Toast.makeText(
                                getApplicationContext(),
                                "No airtime billers available  ",
                                Toast.LENGTH_LONG).show();
                    }
                }
                prgDialog.dismiss();
            }

            @Override
            public void onFailure(Call<GetAirtimeBillers>call, Throwable t) {
                // Log error here since request failed

                Toast.makeText(
                        getApplicationContext(),
                        "Throwable Error: "+t.toString(),
                        Toast.LENGTH_LONG).show();
                prgDialog.dismiss();
            }
        });
    }


    private void PopulateAirtime() {
        planetsList.clear();
        String endpoint= "billpayment/MMObillers.action";

        prgDialog.show();
        String usid = Utility.gettUtilUserId(getApplicationContext());
        String agentid = Utility.gettUtilAgentId(getApplicationContext());
        String mobnoo = Utility.gettUtilMobno(getApplicationContext());
        String params = CHANNEL_ID+usid+"/1";
        String urlparams = "";
        try {
            urlparams = SecurityLayer.genURLCBC(params,endpoint,getApplicationContext());
            //SecurityLayer.Log("cbcurl",url);
            SecurityLayer.Log("RefURL",urlparams);
            SecurityLayer.Log("refurl", urlparams);
            SecurityLayer.Log("params", params);
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

                    SecurityLayer.Log("response..:", response.body());
                    JSONObject obj = new JSONObject(response.body());
                    //obj = Utility.onresp(obj,getApplicationContext());
                    obj = SecurityLayer.decryptTransaction(obj, getApplicationContext());
                    SecurityLayer.Log("decrypted_response", obj.toString());

                    String respcode = obj.optString("responseCode");
                    String responsemessage = obj.optString("message");


                    JSONArray plan = obj.optJSONArray("data");
                    //session.setString(SecurityLayer.KEY_APP_ID,appid);


                    if(!(response.body() == null)) {
                        if ((Utility.checkUserLocked(respcode))) {
                           LogOut();
                        }
                        if (respcode.equals("00")) {

                            SecurityLayer.Log("Response Message", responsemessage);

                            if(plan.length() > 0){
                                String billerdata = plan.toString();
                                session.setString(SessionManagement.KEY_SETAIRTIME, "Y");
                                session.setString(SessionManagement.KEY_AIRTIME, billerdata);

                                JSONObject json_data = null;
                                for (int i = 0; i < plan.length(); i++) {

                                    json_data = plan.getJSONObject(i);
                                    //String accid = json_data.getString("benacid");


                                    String sid = json_data.optString("sid");

                                    String id = json_data.optString("id");
                                    String billerName = json_data.optString("billerName");



                                    planetsList.add(new GetAirtimeBillersData(sid,id,billerName));




                                }
                                if(!(planetsList == null)) {
                                    if(planetsList.size() > 0) {
                                        GetAirtimeBillersData sa = new GetAirtimeBillersData("0000","0000","Select Network Operator");
                                        planetsList.add(sa);
                                        SecurityLayer.Log("Get Biller Data Name", planetsList.get(0).getBillerName());
                                        Collections.sort(planetsList, new Comparator<GetAirtimeBillersData>(){
                                            public int compare(GetAirtimeBillersData d1, GetAirtimeBillersData d2){
                                                return d1.getBillerName().compareTo(d2.getBillerName());
                                            }
                                        });
                                        //  Collections.swap(planetsList,0,planetsList.size()-1);

                                        mobadapt = new ArrayAdapter<GetAirtimeBillersData>(AirtimeTransfActivity.this, R.layout.my_spinner, planetsList);
                                        mobadapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        sp1.setAdapter(mobadapt);
                                        sp1.setSelection(planetsList.size() -1);
                                    }else{
                                        Toast.makeText(
                                                getApplicationContext(),
                                                "No airtime billers available  ",
                                                Toast.LENGTH_LONG).show();
                                    }
                                }

                            }

                        }else{
                            Toast.makeText(
                                    getApplicationContext(),
                                    responsemessage,
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(
                                getApplicationContext(),
                                "There was an error processing your request ",
                                Toast.LENGTH_LONG).show();
                    }


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
                if((!(getApplicationContext() == null)) && !(prgDialog == null) && prgDialog.isShowing()) {
                    prgDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                SecurityLayer.Log("Throwable error",t.toString());

                Toast.makeText(
                        getApplicationContext(),
                        "There was an error processing your request",
                        Toast.LENGTH_LONG).show();
                if((!(getApplicationContext() == null)) && !(prgDialog == null) && prgDialog.isShowing()) {
                    prgDialog.dismiss();
                    SetForceOutDialog(getString(R.string.forceout),getString(R.string.forceouterr),getApplicationContext());

                }

            }
        });

    }



    public void SetForceOutDialog(String msg, final String title, final Context c) {
        if (!(c == null)) {
            new MaterialDialog.Builder(this)
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

    private void dismissProgressDialog() {
        if (prgDialog != null && prgDialog.isShowing()) {
            prgDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        dismissProgressDialog();
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
