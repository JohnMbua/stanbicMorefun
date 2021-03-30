package com.stanbicagent;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.stanbicagent.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import adapter.BillerPayMenuAdapt;
import model.GetBillPayData;
import rest.ApiClient;
import rest.ApiInterface;
import rest.ApiSecurityClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import security.SecurityLayer;

import static com.stanbicagent.ApplicationConstants.CHANNEL_ID;


public class GetPaybillItems extends Fragment implements View.OnClickListener {

    GridView gridView;
    List<GetBillPayData> planetsList = new ArrayList<GetBillPayData>();
    String ptype;
    public static final String KEY_TOKEN = "token";
    ListView lv;
    TextView txtservice, step2;
    String serviceid, servicename, servlabel, billid, billname, label, idd, acnumber;
    BillerPayMenuAdapt aAdpt;
    ProgressDialog prgDialog, prgDialog2;
    SessionManagement session;
    String sbpam = "0", pramo = "0";
    boolean blsbp = false, blpr = false, blpf = false, bllr = false, blms = false, blmpesa = false, blcash = false;
    ArrayList<String> ds = new ArrayList<String>();

    public GetPaybillItems() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.specbillermenu, container, false);
        session = new SessionManagement(getActivity());

        prgDialog = new ProgressDialog(getActivity());

        prgDialog.setMessage("Please wait...");

        prgDialog2 = new ProgressDialog(getActivity());
        prgDialog2.setMessage("Loading....");

        gridView = (GridView) rootView.findViewById(R.id.gridView1);
        txtservice = (TextView) rootView.findViewById(R.id.textView1);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            serviceid = bundle.getString("serviceid");
            servicename = bundle.getString("servicename");
            billid = bundle.getString("billid");
            billname = bundle.getString("billname");
            label = bundle.getString("label");
            acnumber = bundle.getString("acnumber");
            idd = bundle.getString("idd");
            txtservice.setText(billname);
        }

        prgDialog.setCancelable(false);

        step2 = (TextView) rootView.findViewById(R.id.tv2);
        step2.setOnClickListener(this);

        //checkInternetConnection2();
        lv = (ListView) rootView.findViewById(R.id.lv);
        String bsid = session.getString("getbillpay" + idd);
        if (bsid == null) {
            SetPop();
        } else {
            SetBillersStored();
        }
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Fragment fragment = null;
                String title = null;
                String billid = planetsList.get(position).getBillerId();
                //    String billname = planetsList.get(position).getdisplayName();
                String packid = planetsList.get(position).getpackid();
                String paycode = planetsList.get(position).getpaymentCode();
                String charge = planetsList.get(position).getCharge();
                Bundle b = new Bundle();
                b.putString("serviceid", serviceid);
                b.putString("servicename", servicename);
                b.putString("billid", billid);
                b.putString("billname", billname);
                b.putString("label", label);
                b.putString("packid", packid);
                b.putString("charge", charge);
                b.putString("paymentCode", paycode);
                fragment = new CableTV();
                title = "Cable";

                if (fragment != null) {
                    fragment.setArguments(b);
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    //  String tag = Integer.toString(title);
                    fragmentTransaction.replace(R.id.container_body, fragment, title);
                    fragmentTransaction.addToBackStack(title);
                    ((FMobActivity) getActivity())
                            .setActionBarTitle(title);
                    fragmentTransaction.commit();
                }


            }
        });

        return rootView;
    }


    public void StartChartAct(int i) {


    }

    @Override
    public void onResume() {
        super.onResume();
        // put your code here...

    }


    public void SetPop() {
        planetsList.clear();


        prgDialog.show();

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        String usid = Utility.gettUtilUserId(getActivity());
        String agentid = Utility.gettUtilAgentId(getActivity());
        String mobnoo = Utility.gettUtilMobno(getActivity());


        String params = CHANNEL_ID + usid + "/" + agentid + "/" + mobnoo + "/" + idd;

        GetServv(params);
      /*  Call<GetBillers> call = apiService.getBillers("1",usid,agentid,"0000",serviceid);
        call.enqueue(new Callback<GetBillers>() {
            @Override
            public void onResponse(Call<GetBillers>call, Response<GetBillers> response) {
                String responsemessage = response.body().getMessage();

                SecurityLayer.Log("Response Message",responsemessage);
                planetsList = response.body().getResults();

                if(!(planetsList == null)) {
                    if(planetsList.size() > 0) {
                        SecurityLayer.Log("Get Biller Data Name", planetsList.get(0).getBillerName());
                        Collections.sort(planetsList, new Comparator<GetBillersData>() {
                            public int compare(GetBillersData d1, GetBillersData d2) {
                                return d1.getBillerName().compareTo(d2.getBillerName());
                            }
                        });
                        aAdpt = new BillerMenuAdapt(planetsList, getActivity());
                        lv.setAdapter(aAdpt);
                    }else{
                        Toast.makeText(
                                getActivity(),
                                "No billers available  ",
                                Toast.LENGTH_LONG).show();
                    }
                }
                prgDialog.dismiss();
            }

            @Override
            public void onFailure(Call<GetBillers>call, Throwable t) {
                // Log error here since request failed
                Toast.makeText(
                        getActivity(),
                        "Throwable Error: "+t.toString(),
                        Toast.LENGTH_LONG).show();
                prgDialog.dismiss();
            }
        });*/
    }


    @Override
    public void onClick(View view) {


        if (view.getId() == R.id.tv2) {
            Fragment fragment = new BillMenu();


            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment, "Biller Menu");
            fragmentTransaction.addToBackStack("Biller Menu");
            ((FMobActivity) getActivity())
                    .setActionBarTitle("Biller Menu");
            fragmentTransaction.commit();
        }
    }


    private void GetServv(String params) {

        String endpoint = "billpayment/billpaymentitems.action";


        String usid = Utility.gettUtilUserId(getActivity());
        String agentid = Utility.gettUtilAgentId(getActivity());


        SecurityLayer.Log("Before Req Tok", session.getString(KEY_TOKEN));

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


                    JSONArray servdata = obj.optJSONArray("data");
                    //session.setString(SecurityLayer.KEY_APP_ID,appid);

                    if (!(response.body() == null)) {
                        String respcode = obj.optString("responseCode");
                        String responsemessage = obj.optString("message");

                        SecurityLayer.Log("Response Message", responsemessage);

                        if (Utility.isNotNull(respcode) && Utility.isNotNull(respcode)) {
                            if (!(Utility.checkUserLocked(respcode))) {
                                SecurityLayer.Log("Response Message", responsemessage);

                                if (respcode.equals("00")) {
                                    SecurityLayer.Log("JSON Aray", servdata.toString());
                                    if (servdata.length() > 0) {


                                        JSONObject json_data = null;
                                        for (int i = 0; i < servdata.length(); i++) {
                                            json_data = servdata.getJSONObject(i);
                                            //String accid = json_data.getString("benacid");


                                            String id = json_data.optString("packId");

                                            String billerId = json_data.optString("billerId");

                                            String displayName = json_data.optString("displayName");
                                            String charge = json_data.optString("charge");
                                            charge = Double.toString(Double.parseDouble(charge));
                                            String paymentCode = json_data.optString("paymentCode");


                                            planetsList.add(new GetBillPayData(id, displayName, billerId, paymentCode, charge));


                                        }
                                        session.setString("getbillpay" + idd, servdata.toString());
                                        if (!(getActivity() == null)) {
                                            aAdpt = new BillerPayMenuAdapt(planetsList, getActivity());


                                            lv.setAdapter(aAdpt);
                                        }


                                    } else {
                                     /*   Toast.makeText(
                                                getActivity(),
                                                "No billers available  ",
                                                Toast.LENGTH_LONG).show();*/
                                        goNextPage();
                                    }

                                } else {
                                    Toast.makeText(
                                            getActivity(),
                                            "" + responsemessage,
                                            Toast.LENGTH_LONG).show();
                                }
                            } else {
                               /* getActivity().finish();
                                startActivity(new Intent(getActivity(), SignInActivity.class));
                                Toast.makeText(
                                        getActivity(),
                                        "You have been locked out of the app.Please call customer care for further details",
                                        Toast.LENGTH_LONG).show();*/

                                ((FMobActivity) getActivity()).LogOut();

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
                    if (!(getActivity() == null)) {
                        Toast.makeText(getActivity(), getActivity().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                        ((FMobActivity) getActivity()).SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getActivity());
                        // SecurityLayer.Log(e.toString());
                    }

                } catch (Exception e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());

                    if (!(getActivity() == null)) {
                        ((FMobActivity) getActivity()).SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getActivity());
                    }
                    // SecurityLayer.Log(e.toString());
                }
                SecurityLayer.Log("After Req Tok", session.getString(KEY_TOKEN));
                prgDialog.dismiss();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                // Log error here since request failed
                SecurityLayer.Log("throwable error", t.toString());

                if (!(getActivity() == null)) {
                    Toast.makeText(
                            getActivity(),
                            "There was an error on your request",
                            Toast.LENGTH_LONG).show();
                    ((FMobActivity) getActivity()).SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getActivity());

                }


                SecurityLayer.Log("After Req Tok", session.getString(KEY_TOKEN));
                prgDialog.dismiss();
            }
        });

    }

    public void SetBillersStored() {
        planetsList.clear();
        try {
            String bsid = session.getString("getbillpay" + idd);
            JSONArray servdata = new JSONArray(bsid);
            if (servdata.length() > 0) {


                JSONObject json_data = null;
                for (int i = 0; i < servdata.length(); i++) {
                    json_data = servdata.getJSONObject(i);
                    //String accid = json_data.getString("benacid");


                    String id = json_data.optString("packId");

                    String billerId = json_data.optString("billerId");

                    String displayName = json_data.optString("displayName");
                    String charge = json_data.optString("charge");
                    charge = Double.toString(Double.parseDouble(charge));
                    String paymentCode = json_data.optString("paymentCode");


                    planetsList.add(new GetBillPayData(id, displayName, billerId, paymentCode, charge));


                }

                if (!(getActivity() == null)) {
                    aAdpt = new BillerPayMenuAdapt(planetsList, getActivity());


                    lv.setAdapter(aAdpt);
                }


            } else {
                                     /*   Toast.makeText(
                                                getActivity(),
                                                "No billers available  ",
                                                Toast.LENGTH_LONG).show();*/
                goNextPage();
            }
        } catch (JSONException e) {
            SecurityLayer.Log("encryptionJSONException", e.toString());
            // TODO Auto-generated catch block
            Toast.makeText(getActivity(), getActivity().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
            // SecurityLayer.Log(e.toString());

        }
    }

    public void goNextPage() {
        Bundle b = new Bundle();
        b.putString("serviceid", serviceid);
        b.putString("servicename", servicename);
        b.putString("billid", billid);
        b.putString("billname", billname);
        b.putString("label", label);
        b.putString("packid", "0");
        b.putString("charge", "N");
        b.putString("paymentCode", acnumber);
        Fragment fragment = new CableTV();
        String title = "Cable";

        if (fragment != null) {
            fragment.setArguments(b);
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment, title);
            fragmentTransaction.addToBackStack(title);
            ((FMobActivity) getActivity())
                    .setActionBarTitle(title);
            fragmentTransaction.commit();
        }


    }
}