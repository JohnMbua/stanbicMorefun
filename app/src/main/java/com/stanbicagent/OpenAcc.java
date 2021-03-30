package com.stanbicagent;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import static com.stanbicagent.ApplicationConstants.CHANNEL_ID;
import static java.util.Calendar.DATE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;


public class OpenAcc extends Fragment implements View.OnClickListener{


    Button sigin;
    TextView gendisp;
    SessionManagement session;
    EditText midname,mobno,fnam,lnam,yob;
    String strfname,strlname,strmidnm,stryob,strcity,strstate,strgender,stremail,strhmadd,strsalut,strmarstat;
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

    TextView tvdate;
    public static final String DATEPICKER_TAG = "datepicker";

    public OpenAcc() {
        // Required empty public constructor
    }
  /*  private static Fragment newInstance(Context context) {
        LayoutOne f = new LayoutOne();

        return f;
    }
*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.openacc, null);
        sigin = (Button) root.findViewById(R.id.button1);
        sigin.setOnClickListener(this);
        btn4 = (Button) root.findViewById(R.id.button4);
        btn4.setOnClickListener(this);
        prgDialog = new ProgressDialog(getActivity());
        prgDialog.setMessage("Loading....");
        tvdate = (TextView) root.findViewById(R.id.bnameh);
        // Set Cancelable as False

        prgDialog.setCancelable(false);
        session = new SessionManagement(getActivity());



        gendisp = (TextView) root.findViewById(R.id.tdispedit);
        gendisp.setOnClickListener(this);

        sp2 = (Spinner) root.findViewById(R.id.spin2);
        lnam = (EditText) root.findViewById(R.id.user_id);
        mobno = (EditText) root.findViewById(R.id.user_id45);
        fnam = (EditText) root.findViewById(R.id.user_id2);
        midname = (EditText) root.findViewById(R.id.user_id29);
     //   yob = (EditText) root.findViewById(R.id.user_id7);
        sigin.setOnClickListener(this);
        sp5 = (Spinner) root.findViewById(R.id.spin5);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getActivity(), R.array.gender, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp5.setAdapter(adapter);

        prgDialog2 = new ProgressDialog(getActivity());
        prgDialog2.setMessage("Loading Account Types....");
        // Set Cancelable as False

        prgDialog7 = new ProgressDialog(getActivity());
        prgDialog7.setMessage("Loading....");
        // Set Cancelable as False

        prgDialog7.setCancelable(false);

        prgDialog2.setCancelable(false);

        sp1 = (Spinner)root.findViewById(R.id.spin2);
        sp3 = (Spinner)root.findViewById(R.id.spin3);




        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(
                getActivity(), R.array.states, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
     //   sp3.setAdapter(adapter3);



        ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(
                getActivity(), R.array.lga, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
      //  sp4.setAdapter(adapter4);
        final Calendar calendar = Calendar.getInstance();

        // checkInternetConnection2();

        if(!(getArguments() == null)) {
            strfname = getArguments().getString("fname");
            strlname = getArguments().getString("lname");
            strmidnm = getArguments().getString("midname");
            stryob = getArguments().getString("yob");
            strcity = getArguments().getString("city");
            strstate = getArguments().getString("state");
            strgender = getArguments().getString("gender");

            Log.v("Gotten State",strstate);
            lnam.setText(strlname);
            fnam.setText(strfname);
            midname.setText(strmidnm);
            if ( strgender.equals("M")) {

                sp5.setSelection(1);
            } else if (strgender.equals("F")) {
                sp5.setSelection(2);
            }

        }

        PopStates();
        return root;
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
        if (view.getId() == R.id.button1) {
         /*   new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
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
            String midnamee = midname.getText().toString();
            String strgender = sp5.getSelectedItem().toString();
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
                        getActivity(),
                        "Please select a date of birth",
                        Toast.LENGTH_LONG).show();
            }
            if (sp5.getSelectedItemPosition() == 1) {
                strgender = "M";
            } else if (sp5.getSelectedItemPosition() == 2) {
                strgender = "F";
            }

            String strcity = "01";
            if (planetsList.size() > 0){
                String strstate = arrangelist.get(sp3.getSelectedItemPosition()).getstateCode();
                 strcity = citylist.get(sp3.getSelectedItemPosition()-1).getcitycode();
            if (Utility.checkInternetConnection(getActivity())) {
                if (Utility.isNotNull(fname)) {
                    if (Utility.isNotNull(lname)) {


                            if (Utility.isNotNull(chosdate)) {
                                if(!(sp5.getSelectedItemPosition() == 0)){
                                    if(!(strstate.equals("0000"))){
                                        if(dtdiff >= 18){
                                bundle.putString("fname", fname);
                                bundle.putString("lname", lname);
                                bundle.putString("midname", midnamee);
                                bundle.putString("gender", strgender);
                                bundle.putString("city", strcity);
                                bundle.putString("state", strstate);
                                bundle.putString("yob", fintdate);
                                Fragment fragment = new OpenAccStepTwo();
                                fragment.setArguments(bundle);


                                FragmentManager fragmentManager = getFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                //  String tag = Integer.toString(title);
                                fragmentTransaction.replace(R.id.container_body, fragment, "Step Two");
                                fragmentTransaction.addToBackStack("Step Two");
                                ((FMobActivity) getActivity())
                                        .setActionBarTitle("Step Two");
                                fragmentTransaction.commit();
                                        } else {
                                            Toast.makeText(
                                                    getActivity(),
                                                    "Date of Birth should be older than 18 years",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    } else {
                                        Toast.makeText(
                                                getActivity(),
                                                "Please choose a valid State",
                                                Toast.LENGTH_LONG).show();
                                    }

                            } else {
                                    Toast.makeText(
                                            getActivity(),
                                            "Please choose a valid Gender",
                                            Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(
                                        getActivity(),
                                        "Please select a date of birth",
                                        Toast.LENGTH_LONG).show();
                            }

                    } else {
                        Toast.makeText(
                                getActivity(),
                                "Please enter a valid value for Last Name",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(
                            getActivity(),
                            "Please enter a valid value for First Name",
                            Toast.LENGTH_LONG).show();
                }
            }
        }else{
                Toast.makeText(
                        getActivity(),
                        "Please ensure valid value for state has been shown",
                        Toast.LENGTH_LONG).show();
            }
     //    CallOTP();
        }




        if(view.getId()==  R.id.button4){

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
            Activity activity123 = getActivity();
            if(activity123 instanceof MainActivity) {
                ((MainActivity)getActivity())
                        .setActionBarTitle(title);
            }
            if(activity123 instanceof SignInActivity) {
                ((SignInActivity) getActivity())
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




    public void SetDialog(String msg,String title){
        new MaterialDialog.Builder(getActivity())
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
        String usid = Utility.gettUtilUserId(getActivity());
        String agentid = Utility.gettUtilAgentId(getActivity());
        String mobnoo = Utility.gettUtilMobno(getActivity());

        String params = CHANNEL_ID+usid+"/"+agentid+"/"+mobnoo;
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

                    SecurityLayer.Log("response..:", response.body());
                    JSONObject obj = new JSONObject(response.body());
                    //obj = Utility.onresp(obj,getActivity());
                    obj = SecurityLayer.decryptTransaction(obj, getActivity());
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
                                        mobadapt = new ArrayAdapter<GetStatesData>(getActivity(), android.R.layout.simple_spinner_item, planetsList);
                                        mobadapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        sp3.setAdapter(mobadapt);

                                     //   sp3.setSelection(planetsList.size() -1);
                                    }else{
                                        Toast.makeText(
                                                getActivity(),
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
                                        cityadapt = new ArrayAdapter<GetCitiesData>(getActivity(), android.R.layout.simple_spinner_item, fincitylist);
                                        cityadapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        sp4.setAdapter(cityadapt);

                                        //   sp3.setSelection(planetsList.size() -1);
                                    }else{
                                        Toast.makeText(
                                                getActivity(),
                                                "No cities  available  ",
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                                if((!(getActivity() == null)) && prgDialog != null && prgDialog.isShowing()) {
                                    prgDialog.dismiss();
                                }

                            }

                        }else{
                            Toast.makeText(
                                    getActivity(),
                                    responsemessage,
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(
                                getActivity(),
                                "There was an error processing your request ",
                                Toast.LENGTH_LONG).show();
                    }


                } catch (JSONException e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());

                    // TODO Auto-generated catch block
                    Toast.makeText(getActivity(), getActivity().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                    // SecurityLayer.Log(e.toString());

                } catch (Exception e) {

                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    // SecurityLayer.Log(e.toString());
                }

                if((!(getActivity() == null)) && !(prgDialog == null) && prgDialog.isShowing()) {
                    prgDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                SecurityLayer.Log("Throwable error",t.toString());

                Toast.makeText(
                        getActivity(),
                        "There was an error processing your request",
                        Toast.LENGTH_LONG).show();
                if((!(getActivity() == null)) && !(prgDialog == null) && prgDialog.isShowing()) {
                    prgDialog.dismiss();
                }
            }
        });

    }
private void PopStates(){
String jsarray = "[{'id':'1','stateName':'Abia','stateCode':'23','cityName':'Umuahia','cityCode':'158'},{'id':'2','stateName':'Adamawa','stateCode':'04','cityName':'Yola','cityCode':'166'},{'id':'3','stateName':'Akwa Ibom','stateCode':'01','cityName':'Uyo','cityCode':'161'},{'id':'4','stateName':'Anambra','stateCode':'02','cityName':'Awka','cityCode':'024'},{'id':'5','stateName':'Bauchi','stateCode':'03','cityName':'Bauchi','cityCode':'029'},{'id':'6','stateName':'Bayelsa','stateCode':'32','cityName':'Yenagoa','cityCode':'174'},{'id':'7','stateName':'Benue','stateCode':'05','cityName':'Makurdi','cityCode':'103'},{'id':'8','stateName':'Borno','stateCode':'06','cityName':'Maiduguri','cityCode':'102'},{'id':'9','stateName':'Cross River','stateCode':'07','cityName':'Calabar','cityCode':'033'},{'id':'10','stateName':'Delta','stateCode':'09','cityName':'Asaba','cityCode':'022'},{'id':'11','stateName':'Ebonyi','stateCode':'33','cityName':'Abakaliki','cityCode':'172'},{'id':'12','stateName':'Edo','stateCode':'EDO','cityName':'Benin City','cityCode':'030'},{'id':'13','stateName':'Ekiti','stateCode':'34','cityName':'Ado - Ekiti','cityCode':'008'},{'id':'14','stateName':'Enugu','stateCode':'25','cityName':'Enugu','cityCode':'048'},{'id':'15','stateName':'Gombe','stateCode':'35','cityName':'Gombe','cityCode':'057'},{'id':'16','stateName':'Imo','stateCode':'10','cityName':'Owerri','cityCode':'138'},{'id':'17','stateName':'Jigawa','stateCode':'26','cityName':'Dutse','cityCode':'038'},{'id':'18','stateName':'Kaduna','stateCode':'11','cityName':'Kaduna','cityCode':'087'},{'id':'19','stateName':'Kano','stateCode':'12','cityName':'Kano','cityCode':'090'},{'id':'20','stateName':'Katsina','stateCode':'13','cityName':'Katsina','cityCode':'092'},{'id':'21','stateName':'Kebbi','stateCode':'27','cityName':'Birnin Kebbi','cityCode':'032'},{'id':'22','stateName':'Kogi','stateCode':'28','cityName':'Lokoja','cityCode':'101'},{'id':'23','stateName':'Kwara','stateCode':'14','cityName':'Ilorin','cityCode':'077'},{'id':'24','stateName':'Lagos','stateCode':'15','cityName':'Ikeja','cityCode':'099'},{'id':'25','stateName':'Nasarawa','stateCode':'36','cityName':'Lafia','cityCode':'098'},{'id':'26','stateName':'Niger','stateCode':'16','cityName':'Minna','cityCode':'108'},{'id':'27','stateName':'Ogun','stateCode':'17','cityName':'Abeokuta','cityCode':'005'},{'id':'28','stateName':'Ondo','stateCode':'18','cityName':'Akure','cityCode':'016'},{'id':'29','stateName':'Osun','stateCode':'29','cityName':'Oshogbo','cityCode':'133'},{'id':'30','stateName':'Oyo','stateCode':'19','cityName':'Ibadan','cityCode':'060'},{'id':'31','stateName':'Plateau','stateCode':'20','cityName':'Jos','cityCode':'086'},{'id':'32','stateName':'Rivers','stateCode':'21','cityName':'Port Harcourt','cityCode':'142'},{'id':'33','stateName':'Sokoto','stateCode':'22','cityName':'Sokoto','cityCode':'150'},{'id':'34','stateName':'Taraba','stateCode':'30','cityName':'Jalingo','cityCode':'085'},{'id':'35','stateName':'Yobe','stateCode':'31','cityName':'Damaturu','cityCode':'035'},{'id':'36','stateName':'Zamfara','stateCode':'37','cityName':'Gusau','cityCode':'058'}]";
    JSONArray plan = null;
    try {
        plan = new JSONArray(jsarray);


    if(plan.length() > 0){


                JSONObject json_data = null;

                for (int i = 0; i < plan.length(); i++) {
                    json_data = plan.getJSONObject(i);
                    //String accid = json_data.getString("benacid");




                    String statecode = json_data.optString("stateCode");
                    String statename = json_data.optString("stateName");
                    String citycode = json_data.optString("cityCode");
                    String cityname = json_data.optString("cityName");

                    SecurityLayer.Log("State Name", statename);
                    planetsList.add(new GetStatesData(statecode,statename));
                    citylist.add(new GetCitiesData(citycode,cityname));



                }
                if(!(planetsList == null)) {
                    if(planetsList.size() > 0) {
                        int index = 0;
                        Collections.sort(planetsList, new Comparator<GetStatesData>(){
                            public int compare(GetStatesData d1, GetStatesData d2){
                                return d1.getstateName().compareTo(d2.getstateName());
                            }
                        });
                        GetStatesData sa = new GetStatesData("0000","Select State");

                        arrangelist.add(sa);
                        for(int sd = 0;sd < planetsList.size();sd++){
                            arrangelist.add(planetsList.get(sd));
                            if(Utility.isNotNull(strstate)) {
                                String strstt = planetsList.get(sd).getstateCode();
                                if(strstt.equals(strstate)){
                                    index = sd +1;
                                }

                            }
                        }
                        //  Collections.swap(planetsList,0,planetsList.size() -1);
                        mobadapt = new ArrayAdapter<GetStatesData>(getActivity(), android.R.layout.simple_spinner_item, arrangelist);
                        mobadapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        sp3.setAdapter(mobadapt);
                        if(Utility.isNotNull(strstate)) {
                            sp3.setSelection(index);
                            Log.v("I am in",strstate);
                            Log.v("State index",Integer.toString(index));

                        }
                        //   sp3.setSelection(planetsList.size() -1);
                    }else{
                        Toast.makeText(
                                getActivity(),
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
                        cityadapt = new ArrayAdapter<GetCitiesData>(getActivity(), android.R.layout.simple_spinner_item, fincitylist);
                        cityadapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        sp4.setAdapter(cityadapt);

                        //   sp3.setSelection(planetsList.size() -1);
                    }else{
                        Toast.makeText(
                                getActivity(),
                                "No cities  available  ",
                                Toast.LENGTH_LONG).show();
                    }
                }


            }

    } catch (JSONException e) {
        e.printStackTrace();
    }


}
    private void PopulateCities(String stateid) {

        String endpoint= "core/cities.action";
        planetsList.clear();

        prgDialog.show();
        String usid = Utility.gettUtilUserId(getActivity());
        String agentid = Utility.gettUtilAgentId(getActivity());
        String mobnoo = Utility.gettUtilMobno(getActivity());

        String params = CHANNEL_ID+usid+"/"+agentid+"/"+mobnoo+"/"+stateid;
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

                    SecurityLayer.Log("response..:", response.body());
                    JSONObject obj = new JSONObject(response.body());
                    //obj = Utility.onresp(obj,getActivity());
                    obj = SecurityLayer.decryptTransaction(obj, getActivity());
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

                                    SecurityLayer.Log("State Name", statename);
                                    planetsList.add(new GetStatesData(statecode,statename));
                                    citylist.add(new GetCitiesData(citycode,cityname));



                                }
                                if(!(planetsList == null)) {
                                    if(planetsList.size() > 0) {
                                        int index = 0;
                                        Collections.sort(planetsList, new Comparator<GetStatesData>(){
                                            public int compare(GetStatesData d1, GetStatesData d2){
                                                return d1.getstateName().compareTo(d2.getstateName());
                                            }
                                        });
                                        GetStatesData sa = new GetStatesData("0000","Select State");

                                        arrangelist.add(sa);
                                        for(int sd = 0;sd < planetsList.size();sd++){
                                            arrangelist.add(planetsList.get(sd));
                                            if(Utility.isNotNull(strstate)) {
                                              String strstt = planetsList.get(sd).getstateCode();
                                              if(strstt.equals(strstate)){
                                                  index = sd +1;
                                              }

                                            }
                                        }
                                      //  Collections.swap(planetsList,0,planetsList.size() -1);
                                        mobadapt = new ArrayAdapter<GetStatesData>(getActivity(), android.R.layout.simple_spinner_item, arrangelist);
                                        mobadapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        sp3.setAdapter(mobadapt);
                                        if(Utility.isNotNull(strstate)) {
                                            sp3.setSelection(index);
                                            Log.v("I am in",strstate);
                                            Log.v("State index",Integer.toString(index));

                                        }
                                        //   sp3.setSelection(planetsList.size() -1);
                                    }else{
                                        Toast.makeText(
                                                getActivity(),
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
                                        cityadapt = new ArrayAdapter<GetCitiesData>(getActivity(), android.R.layout.simple_spinner_item, fincitylist);
                                        cityadapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        sp4.setAdapter(cityadapt);

                                        //   sp3.setSelection(planetsList.size() -1);
                                    }else{
                                        Toast.makeText(
                                                getActivity(),
                                                "No cities  available  ",
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                                if((!(getActivity() == null)) && !(prgDialog == null) && prgDialog.isShowing()) {
                                    prgDialog.dismiss();
                                }

                            }

                        }else{
                            Toast.makeText(
                                    getActivity(),
                                    responsemessage,
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(
                                getActivity(),
                                "There was an error processing your request ",
                                Toast.LENGTH_LONG).show();
                    }


                } catch (JSONException e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());

                    // TODO Auto-generated catch block
                    Toast.makeText(getActivity(), getActivity().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                    // SecurityLayer.Log(e.toString());

                } catch (Exception e) {

                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    // SecurityLayer.Log(e.toString());
                }
//                prgDialog.dismiss();
                try {
                    if((!(getActivity() == null)) && !(prgDialog == null) && prgDialog.isShowing()) {
                        prgDialog.dismiss();
                    }
                } catch (final IllegalArgumentException e) {
                    // Handle or log or ignore
                } catch (final Exception e) {
                    // Handle or log or ignore
                } finally {

                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                SecurityLayer.Log("Throwable error",t.toString());

                Toast.makeText(
                        getActivity(),
                        "There was an error processing your request",
                        Toast.LENGTH_LONG).show();
                try {
                    if((!(getActivity() == null)) && !(prgDialog == null) && prgDialog.isShowing()) {
                        prgDialog.dismiss();
                    }
                } catch (final IllegalArgumentException e) {
                    // Handle or log or ignore
                } catch (final Exception e) {
                    // Handle or log or ignore
                } finally {

                }

            }
        });

    }




}
