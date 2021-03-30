package com.stanbicagent;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.ActionBar;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.stanbicagent.R;
import com.afollestad.materialdialogs.MaterialDialog;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import model.GetCitiesData;
import model.GetStatesData;
import rest.ApiInterface;
import rest.ApiSecurityClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import security.SecurityLayer;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.stanbicagent.ApplicationConstants.CHANNEL_ID;
import static java.util.Calendar.DATE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

public class OpenAccActivity extends BaseActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {


    Button sigin;
    TextView gendisp;
    SessionManagement session;
    EditText midname,mobno,fnam,lnam,yob;
    String strfname,strlname,stryob,stremail,strhmadd,strsalut,strmarstat;
String bvn = "NA";
    List<GetStatesData> planetsList = new ArrayList<GetStatesData>();
    List<GetStatesData> arrangelist = new ArrayList<GetStatesData>();
    List<GetCitiesData> citylist = new ArrayList<GetCitiesData>();
    ArrayAdapter<GetStatesData> mobadapt;
    ArrayAdapter<GetCitiesData> cityadapt;
    List<String> prodid = new ArrayList<String>();
    ArrayAdapter<String> mArrayAdapter;
    Spinner sp1,sp2,sp5,sp3,sp4;
    Button btn4;
    static Hashtable<String, String> data1;
    String paramdata = "",chosdate,fintdate;
    ProgressDialog prgDialog,prgDialog2,prgDialog7;
    TextView tnc;
    List<String> mobopname  = new ArrayList<String>();
    List<String> mobopid  = new ArrayList<String>();
    DatePickerDialog datePickerDialog;
    TextView tvdate;
    public static final String DATEPICKER_TAG = "datepicker";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_acc);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get the ActionBar here to configure the way it behaves.
        final ActionBar ab = getSupportActionBar();
        //ab.setHomeAsUpIndicator(R.drawable.ic_menu); // set a custom icon for the default home button
        ab.setDisplayShowHomeEnabled(true); // show or hide the default home button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
        ab.setDisplayShowTitleEnabled(false); // disable the default title element here (for centered title)

        sigin = (Button)findViewById(R.id.button1);
        sigin.setOnClickListener(this);
        btn4 = (Button)findViewById(R.id.button4);
        btn4.setOnClickListener(this);
        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Loading....");
        tvdate = (TextView) findViewById(R.id.bnameh);
        // Set Cancelable as False

        prgDialog.setCancelable(false);
        session = new SessionManagement(this);



        gendisp = (TextView) findViewById(R.id.tdispedit);
        gendisp.setOnClickListener(this);

        sp2 = (Spinner) findViewById(R.id.spin2);
        lnam = (EditText) findViewById(R.id.user_id);
        mobno = (EditText)findViewById(R.id.user_id45);
        fnam = (EditText) findViewById(R.id.user_id2);
        midname = (EditText) findViewById(R.id.user_id29);
        //   yob = (EditText) root.findViewById(R.id.user_id7);
        sigin.setOnClickListener(this);
        sp5 = (Spinner) findViewById(R.id.spin5);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                OpenAccActivity.this, R.array.gender, R.layout.my_spinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp5.setAdapter(adapter);

        prgDialog2 = new ProgressDialog(this);
        prgDialog2.setMessage("Loading Account Types....");
        // Set Cancelable as False

        prgDialog7 = new ProgressDialog(this);
        prgDialog7.setMessage("Loading....");
        // Set Cancelable as False

        prgDialog7.setCancelable(false);

        prgDialog2.setCancelable(false);

        sp1 = (Spinner)findViewById(R.id.spin2);
        sp3 = (Spinner)findViewById(R.id.spin3);




        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(
                OpenAccActivity.this, R.array.states, R.layout.my_spinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //   sp3.setAdapter(adapter3);



        ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(
                OpenAccActivity.this, R.array.lga, R.layout.my_spinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //  sp4.setAdapter(adapter4);
        final Calendar calendar = Calendar.getInstance();
        int maxyear = calendar.get(YEAR) - 18;
        datePickerDialog = DatePickerDialog.newInstance(this, maxyear, calendar.get(MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        Calendar calback = Calendar.getInstance();
        calback.set(maxyear,calendar.get(MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setMaxDate(calback);
        datePickerDialog.setYearRange(1903,maxyear);

        // checkInternetConnection2();

        Intent intent = getIntent();
        if (intent != null) {


            strfname = intent.getStringExtra("fname");
            strlname = intent.getStringExtra("lname");
            bvn = intent.getStringExtra("bvn");
            stryob = intent.getStringExtra("yob");




        //    Log.v("Gotten State",strstate);
            lnam.setText(strlname);
            fnam.setText(strfname);


        }

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
    public void onClick(View view) {
        if (view.getId() == R.id.button1) {
         /*   new SweetAlertDialog(getApplicationContext(), SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Open Account")
                    .setContentText("Are you sure you want to proceed with Open Account? \n \n" +
                            " First Name:  Test \n   Last Name: Customer \n Identification Number: 01010101 \n Network Operator: Airtel \n State: Lagos \n LGA: Eti-Osa \n Gender: Male \n DOB: 01/05/1980 \n Mobile Number: 0818888888   ")
                    .setConfirmText("Confirm")
                    .setCancelText("Cancel")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                        }
                    })
                    .show();*/
            Bundle bundle = new Bundle();

            String fname = fnam.getText().toString();
            String lname = lnam.getText().toString();
            String strgender = sp5.getSelectedItem().toString();

            if (sp5.getSelectedItemPosition() == 1) {
                strgender = "M";
            } else if (sp5.getSelectedItemPosition() == 2) {
                strgender = "F";
            }

            int dtdiff = 0;

            SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy");
            if (Utility.isNotNull(chosdate)) {
                String dateInString = chosdate;


                try {
                    Date datefrom = sdf.parse(dateInString);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date dateto = new Date();
                    System.out.println(dateFormat.format(dateto));
                    dtdiff = getDiffYears(datefrom, dateto);
                    //   txtdtdiff = Integer.toString(dtdiff);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(
                        getApplicationContext(),
                        "Please select a date of birth",
                        Toast.LENGTH_LONG).show();
            }


            if (sp5.getSelectedItemPosition() == 1) {
                strgender = "M";
            } else if (sp5.getSelectedItemPosition() == 2) {
                strgender = "F";
            }


                if (Utility.checkInternetConnection(getApplicationContext())) {
                    if (Utility.isNotNull(fname)) {
                        if (Utility.isNotNull(lname)) {


                            if (Utility.isNotNull(chosdate)) {


                                        if(dtdiff >= 18){



                                            Intent intent  = new Intent(OpenAccActivity.this,OpenAccStepTwoActivity.class);


                                            intent.putExtra("fname", fname);
                                            intent.putExtra("lname", lname);
                                            intent.putExtra("bvn", bvn);

                                            intent.putExtra("gender", strgender);

                                            intent.putExtra("yob", fintdate);


                                            startActivity(intent);

                                        } else {
                                            Toast.makeText(
                                                    getApplicationContext(),
                                                    "Date of Birth should be older than 18 years",
                                                    Toast.LENGTH_LONG).show();
                                        }


                            } else {
                                Toast.makeText(
                                        getApplicationContext(),
                                        "Please select a date of birth",
                                        Toast.LENGTH_LONG).show();
                            }

                        } else {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Please enter a valid value for Last Name",
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(
                                getApplicationContext(),
                                "Please enter a valid value for First Name",
                                Toast.LENGTH_LONG).show();
                    }
                }

        }




        if(view.getId()==  R.id.button4){

            datePickerDialog.show(getFragmentManager(), DATEPICKER_TAG);
        }
        if(view.getId() == R.id.tdispedit){

          /*Fragment fragment =  new NatWebProd();;
            String title = "Bank Info";
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment, title);
            fragmentTransaction.addToBackStack(title);
            fragmentTransaction.commit();
            Activity activity123 = getApplicationContext();
            if(activity123 instanceof MainActivity) {
                ((MainActivity)getApplicationContext())
                        .setActionBarTitle(title);
            }
            if(activity123 instanceof SignInActivity) {
                ((SignInActivity) getApplicationContext())
                        .setActionBarTitle(title);
            }*/
        }

        if(view.getId() == R.id.textView3){

        }
    }


    public void ClearOpenAcc(){
        //   sp1.setSelection(0);

        mobno.setText(" ");

        fnam.setText(" ");
        lnam.setText(" ");
        //   yob.setText(" ");
    }



    public static int getDiffYears(Date first, Date last) {
        Calendar a = getCalendar(first);
        Calendar b = getCalendar(last);
        int diff = b.get(YEAR) - a.get(YEAR);
        if (a.get(MONTH) > b.get(MONTH) ||
                (a.get(MONTH) == b.get(MONTH) && a.get(DATE) > b.get(DATE))) {
            diff--;
        }
        return diff;
    }
    public static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance(Locale.US);
        cal.setTime(date);
        return cal;
    }
    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        String finalmon = null;
        String finalday = null;
        int nwm = month + 1;
        finalmon = Integer.toString(nwm);
        finalday = Integer.toString(day);
        if (nwm < 10) {
            finalmon = "0" + nwm;
        }
        if (day < 10) {
            finalday = "0" + finalday;
        }
        String tdate = day+"-"+finalmon + "-" +year ;
        fintdate = finalday+finalmon+year;
//fintdate = tdate;
        chosdate = tdate;
        tvdate.setText(tdate);
    }



    public void SetDialog(String msg,String title){
        new MaterialDialog.Builder(getApplicationContext())
                .title(title)
                .content(msg)

                .negativeText("Close")
                .show();
    }

    public String setMobFormat(String mobno){
        String vb = mobno.substring(Math.max(0, mobno.length() - 9));
        SecurityLayer.Log("Logged Number is", vb);
        if(vb.length() == 9 && (vb.substring(0, Math.min(mobno.length(), 1)).equals("7"))){
            return "254"+vb;
        }else{
            return  "N";
        }
    }




    private void PopulateState() {
        planetsList.clear();
        String endpoint= "core/states.action";

        prgDialog.show();
        String usid = Utility.gettUtilUserId(getApplicationContext());
        String agentid = Utility.gettUtilAgentId(getApplicationContext());
        String mobnoo = Utility.gettUtilMobno(getApplicationContext());

        String params = CHANNEL_ID+usid+"/"+agentid+"/"+mobnoo;
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
                        if (respcode.equals("00")) {

                            SecurityLayer.Log("Response Message", responsemessage);

                            if(plan.length() > 0){


                                JSONObject json_data = null;
                                for (int i = 0; i < plan.length(); i++) {
                                    json_data = plan.getJSONObject(i);
                                    //String accid = json_data.getString("benacid");




                                    String statecode = json_data.optString("stateCode");
                                    String statename = json_data.optString("stateName");
                                    String citycode = json_data.optString("cityCode");
                                    String cityname = json_data.optString("cityName");


                                    planetsList.add(new GetStatesData(statecode,statename));
                                    citylist.add(new GetCitiesData(citycode,cityname));



                                }
                                if(!(planetsList == null)) {
                                    if(planetsList.size() > 0) {
                                        SecurityLayer.Log("Get State Data Name", planetsList.get(0).getstateName());
                                        Collections.sort(planetsList, new Comparator<GetStatesData>(){
                                            public int compare(GetStatesData d1, GetStatesData d2){
                                                return d1.getstateName().compareTo(d2.getstateName());
                                            }
                                        });
                                        mobadapt = new ArrayAdapter<GetStatesData>(OpenAccActivity.this, R.layout.my_spinner, planetsList);
                                        mobadapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        sp3.setAdapter(mobadapt);

                                        //   sp3.setSelection(planetsList.size() -1);
                                    }else{
                                        Toast.makeText(
                                                getApplicationContext(),
                                                "No states available  ",
                                                Toast.LENGTH_LONG).show();
                                    }
                                }

                                if(!(citylist == null)) {
                                    if(citylist.size() > 0) {

                                        List<GetCitiesData> fincitylist = new ArrayList<GetCitiesData>();
                                        fincitylist.add(0,citylist.get(0));
                                        SecurityLayer.Log("Get City Data Name", fincitylist.get(0).getcityname());
                                        Collections.sort(fincitylist, new Comparator<GetCitiesData>(){
                                            public int compare(GetCitiesData d1, GetCitiesData d2){
                                                return d1.getcityname().compareTo(d2.getcityname());
                                            }
                                        });
                                        cityadapt = new ArrayAdapter<GetCitiesData>(OpenAccActivity.this,R.layout.my_spinner, fincitylist);
                                        cityadapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        sp4.setAdapter(cityadapt);

                                        //   sp3.setSelection(planetsList.size() -1);
                                    }else{
                                        Toast.makeText(
                                                getApplicationContext(),
                                                "No cities  available  ",
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                                if((!(getApplicationContext() == null)) && prgDialog != null && prgDialog.isShowing()) {
                                    prgDialog.dismiss();
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

                } catch (Exception e) {

                    SecurityLayer.Log("encryptionJSONException", e.toString());
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
                }
            }
        });

    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
