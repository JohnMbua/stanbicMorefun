package com.stanbicagent;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.stanbicagent.R;
import com.borax12.materialdaterangepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

import adapter.NewCommListAdapter;
import model.GetCommPerfData;
import rest.ApiClient;
import rest.ApiInterface;
import rest.ApiSecurityClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import security.SecurityLayer;

import static com.stanbicagent.ApplicationConstants.CHANNEL_ID;


public class CommReport extends Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener {
    Button signup;
    // Spinner sp1;
    List<String> acc = new ArrayList<String>();
    List<GetCommPerfData> temp = new ArrayList<GetCommPerfData>();
    static Hashtable<String, String> data1;
    public static ArrayList<String> instidacc = new ArrayList<String>();
    String paramdata = "";
    ArrayAdapter<String> mArrayAdapter;
    ArrayList<String> phoneContactList = new ArrayList<String>();
    List<GetCommPerfData> planetsList = new ArrayList<GetCommPerfData>();
    private TextView emptyView;
    NewCommListAdapter aAdpt;
    ListView lv;
    Button ok;
    String selacc;
    ProgressDialog prgDialog2;
    TextView acno, txtitle, txcomrepo, txtcomrepo, commamo;
    EditText accno, mobno, fnam;
    SessionManagement session;
    Button calendar;
    LinearLayout lstmt;

    public CommReport() {
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
        View rootView = inflater.inflate(R.layout.commreport, container, false);
        lv = (ListView) rootView.findViewById(R.id.lv);
        txtitle = (TextView) rootView.findViewById(R.id.bname);
        txcomrepo = (TextView) rootView.findViewById(R.id.textViewweryu);
        txtcomrepo = (TextView) rootView.findViewById(R.id.textViewwaq);
        commamo = (TextView) rootView.findViewById(R.id.txtagcomm);
        //   sp1 = (Spinner) rootView.findViewById(R.id.accno);
        prgDialog2 = new ProgressDialog(getActivity());
        prgDialog2.setMessage("Loading ....");
        lstmt = (LinearLayout) rootView.findViewById(R.id.stmtly);
        lstmt.setOnClickListener(this);
        // Set Cancelable as False
        session = new SessionManagement(getActivity());
        calendar = (Button) rootView.findViewById(R.id.button4);
        calendar.setOnClickListener(this);
        prgDialog2.setCancelable(false);
        emptyView = (TextView) rootView.findViewById(R.id.empty_view);
        String usid = Utility.gettUtilUserId(getActivity());
        txtcomrepo.setText("Commission for user:" + usid + " is");
        Calendar cal = Calendar.getInstance();

        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH); // Note: zero based!
        int day = now.get(Calendar.DAY_OF_MONTH);

        now.set(year, month, 01);

        SimpleDateFormat format1 = new SimpleDateFormat("" +
                "MMMM dd yyyy");
        System.out.println(cal.getTime());
// Output "Wed Sep 26 14:23:28 EST 2012"

        String formattednow = format1.format(cal.getTime());
        String formattedstartdate = format1.format(now.getTime());
// Output "2012-09-26"
        txtitle.setText("Commission Report for " + formattedstartdate + " to " + formattednow);

        if (!(getActivity() == null)) {
            setBalInquSec();
        }

        return rootView;
    }

    public void StartChartAct(int i) {
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setBalInquSec() {
        if ((prgDialog2 != null) && !(getActivity() == null)) {
            prgDialog2.show();
        }
        String endpoint = "core/balenquirey.action";


        String usid = Utility.gettUtilUserId(getActivity());
        String agentid = Utility.gettUtilAgentId(getActivity());
        String mobnoo = Utility.gettUtilMobno(getActivity());
        String params = CHANNEL_ID + usid + "/" + agentid + "/" + mobnoo;
        String urlparams = "";
        try {
            urlparams = SecurityLayer.genURLCBC(params, endpoint, getActivity());
            //SecurityLayer.Log("cbcurl",url);
            SecurityLayer.Log("RefURL", urlparams);
            SecurityLayer.Log("refurl", urlparams);
            SecurityLayer.Log("params", params);
        } catch (Exception e) {
            SecurityLayer.Log("encryptionerror", e.toString());
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


                    JSONObject plan = obj.optJSONObject("data");
                    //session.setString(SecurityLayer.KEY_APP_ID,appid);


                    if (!(response.body() == null)) {
                        if (respcode.equals("00")) {

                            SecurityLayer.Log("Response Message", responsemessage);

//                                     SecurityLayer.Log("Respnse getResults",datas.toString());
                            if (!(plan == null)) {
                                String balamo = plan.optString("balance");
                                String comamo = plan.optString("commission");


                                String cmbal = Utility.returnNumberFormat(comamo);

                                //   cmbal = Utility.roundto2dp(cmbal);
                                commamo.setText(ApplicationConstants.KEY_NAIRA + cmbal);
                            } else {
                                Toast.makeText(
                                        getActivity(),
                                        "There was an error retrieving your balance ",
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(
                                    getActivity(),
                                    "There was an error retrieving your balance ",
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(
                                getActivity(),
                                "There was an error retrieving your balance ",
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
                if (!(getActivity() == null)) {
                    if (Utility.checkInternetConnection(getActivity())) {

                        Calendar now = Calendar.getInstance();
                        int year = now.get(Calendar.YEAR);
                        int month = now.get(Calendar.MONTH); // Note: zero based!
                        int day = now.get(Calendar.DAY_OF_MONTH);

                        month = month + 1;

                        String frmdymonth = Integer.toString(day);
                        if (day < 10) {
                            frmdymonth = "0" + frmdymonth;
                        }
                        String frmyear = Integer.toString(year);
                        frmyear = frmyear.substring(2, 4);
                        String tdate = frmdymonth + "-" + (month) + "-" + frmyear;
                        String firdate = "01" + "-" + (month) + "-" + frmyear;

                        Calendar calfrom = Calendar.getInstance();
                        calfrom.set(year, month, 1);
                        SetMinist(firdate, tdate);
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                SecurityLayer.Log("Throwable error", t.toString());
                Toast.makeText(
                        getActivity(),
                        "There was an error processing your request",
                        Toast.LENGTH_LONG).show();
                //   pDialog.dismiss();

                try {
                    if ((prgDialog2 != null) && prgDialog2.isShowing()) {
                        prgDialog2.dismiss();
                    }
                } catch (final IllegalArgumentException e) {
                    // Handle or log or ignore
                } catch (final Exception e) {
                    // Handle or log or ignore
                } finally {

                }
             /*   if(Utility.checkInternetConnection(getActivity())){

                    Calendar now = Calendar.getInstance();
                    int year = now.get(Calendar.YEAR);
                    int month = now.get(Calendar.MONTH); // Note: zero based!
                    int day = now.get(Calendar.DAY_OF_MONTH);

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
                }*/
                ((FMobActivity) getActivity()).SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getActivity());

            }
        });

    }

    public void SetMinist(String stdate, String enddate) {

        planetsList.clear();
        if (!(prgDialog2 == null)) {
            prgDialog2.show();


            ApiInterface apiService =
                    ApiClient.getClient().create(ApiInterface.class);
            String usid = Utility.gettUtilUserId(getActivity());
            String agentid = Utility.gettUtilAgentId(getActivity());
            String mobnoo = Utility.gettUtilMobno(getActivity());
            String params = CHANNEL_ID + usid + "/CMSNRPT/" + stdate + "/" + enddate;
            CommReport(params);
        }

    }

    private void CommReport(String params) {

        String endpoint = "report/genrpt.action";

        String urlparams = "";
        try {
            urlparams = SecurityLayer.genURLCBC(params, endpoint, getActivity());
            //SecurityLayer.Log("cbcurl",url);
            SecurityLayer.Log("RefURL", urlparams);
            SecurityLayer.Log("refurl", urlparams);
            SecurityLayer.Log("params", params);
        } catch (Exception e) {
            SecurityLayer.Log("encryptionerror", e.toString());
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
                    double tott = 0;
                    if (!(response.body() == null)) {
                        String respcode = obj.optString("responseCode");
                        String responsemessage = obj.optString("message");

                        SecurityLayer.Log("Response Message", responsemessage);

                        if (Utility.isNotNull(respcode) && Utility.isNotNull(respcode)) {
                            if (!(Utility.checkUserLocked(respcode))) {
                                SecurityLayer.Log("Response Message", responsemessage);

                                if (respcode.equals("00")) {
                                    SecurityLayer.Log("JSON Aray", comperf.toString());
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
                                            String toAcNum = json_data.optString("toAcNum");
                                            String refNumber = json_data.optString("refNumber");
                                            String fromaccnum = json_data.optString("fromAccountNum");
                                            if (txnCode.equals("CASHDEP") || txnCode.equals("FTINTRABANK")) {
                                                fintoacnum = fromaccnum;
                                                finfromacnum = toAcNum;
                                                toAcNum = fintoacnum;
                                                fromaccnum = finfromacnum;
                                            }
                                            if (((status.equals("SUCCESS"))) && (agentCmsn > 0)) {
                                                tott += agentCmsn;
                                                planetsList.add(new GetCommPerfData(txnCode, txndateTime, agentCmsn, status, amount, toAcNum, refNumber, fromaccnum));

                                            }


                                        }
                                        if (!(getActivity() == null)) {
                                            aAdpt = new NewCommListAdapter(planetsList, getActivity());


                                            lv.setAdapter(aAdpt);
                                        }


                                    }

                                } else {
                                    if (!(getActivity() == null)) {
                                        Toast.makeText(
                                                getActivity(),
                                                "" + responsemessage,
                                                Toast.LENGTH_LONG).show();
                                    }
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
                            if (!(getActivity() == null)) {
                                Toast.makeText(
                                        getActivity(),
                                        "There was an error on your request",
                                        Toast.LENGTH_LONG).show();
                            }

                        }
                        String fincommrpt = Double.toString(tott);

                        fincommrpt = Utility.returnNumberFormat(fincommrpt);
                        txcomrepo.setText(ApplicationConstants.KEY_NAIRA + fincommrpt);
                    } else {
                        if (!(getActivity() == null)) {
                            Toast.makeText(
                                    getActivity(),
                                    "There was an error on your request",
                                    Toast.LENGTH_LONG).show();
                        }

                    }
                    // prgDialog2.dismiss();


                } catch (JSONException e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    // TODO Auto-generated catch block
                    Toast.makeText(getActivity(), getActivity().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                    ((FMobActivity) getActivity()).SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getActivity());

                    // SecurityLayer.Log(e.toString());

                } catch (Exception e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    ((FMobActivity) getActivity()).SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getActivity());

                    // SecurityLayer.Log(e.toString());
                }
                if ((prgDialog2 != null) && prgDialog2.isShowing() && !(getActivity() == null)) {
                    prgDialog2.dismiss();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                // Log error here since request failed
                SecurityLayer.Log("throwable error", t.toString());


                Toast.makeText(
                        getActivity(),
                        "There was an error on your request",
                        Toast.LENGTH_LONG).show();


                if ((prgDialog2 != null) && prgDialog2.isShowing() && !(getActivity() == null)) {
                    prgDialog2.dismiss();
                }
                ((FMobActivity) getActivity()).SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getActivity());

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
        if (view.getId() == R.id.stmtly) {

        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth, int yearEnd, int monthOfYearEnd, int dayOfMonthEnd) {
        String date = "Commission Report : From- " + dayOfMonth + "/" + (++monthOfYear) + "/" + year + " To " + dayOfMonthEnd + "/" + (++monthOfYearEnd) + "/" + yearEnd;
        txtitle.setText(date);
        Calendar calfrom = Calendar.getInstance();
        Calendar calto = Calendar.getInstance();
        calto.set(yearEnd, monthOfYearEnd, dayOfMonthEnd);
        calfrom.set(year, monthOfYear, dayOfMonth);

        if (calfrom.before(calto)) {
            //   fromdate.setText(date);
            String frmdymonth = Integer.toString(dayOfMonth);
            if (dayOfMonth < 10) {
                frmdymonth = "0" + frmdymonth;
            }
            String frmyear = Integer.toString(year);
            frmyear = frmyear.substring(2, 4);
            String fromd = frmdymonth + "-" + (monthOfYear) + "-" + frmyear;
            String frmenddymonth = Integer.toString(dayOfMonthEnd);
            if (dayOfMonthEnd < 10) {
                frmenddymonth = "0" + frmenddymonth;
            }

            String frmendyr = Integer.toString(yearEnd);
            frmendyr = frmendyr.substring(2, 4);
            String endd = frmenddymonth + "-" + (monthOfYearEnd) + "-" + frmendyr;
            SetMinist(fromd, endd);
        } else {
            Toast.makeText(
                    getActivity(),
                    "Please ensure the from date is before the after date",
                    Toast.LENGTH_LONG).show();
        }
    }
}
