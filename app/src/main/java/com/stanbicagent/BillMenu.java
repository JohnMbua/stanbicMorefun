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
import android.widget.Toast;

import com.stanbicagent.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import adapter.ServicesMenuAdapt;
import model.GetServicesData;
import rest.ApiClient;
import rest.ApiInterface;
import rest.ApiSecurityClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import security.SecurityLayer;

import static com.stanbicagent.ApplicationConstants.CHANNEL_ID;


public class BillMenu extends Fragment {

    GridView gridView;
    List<GetServicesData> planetsList = new ArrayList<GetServicesData>();
    String ptype;
    ListView lv;
    ServicesMenuAdapt aAdpt;
    ProgressDialog prgDialog, prgDialog2;
    SessionManagement session;
    String sbpam = "0", pramo = "0";
    boolean blsbp = false, blpr = false, blpf = false, bllr = false, blms = false, blmpesa = false, blcash = false;
    ArrayList<String> ds = new ArrayList<String>();

    public BillMenu() {
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
        View rootView = inflater.inflate(R.layout.billermenu, container, false);
        session = new SessionManagement(getActivity());
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            ptype = bundle.getString("prtype");

        }
        prgDialog = new ProgressDialog(getActivity());

        prgDialog.setMessage("Please wait...");

        prgDialog2 = new ProgressDialog(getActivity());
        prgDialog2.setMessage("Loading....");

        gridView = (GridView) rootView.findViewById(R.id.gridView1);

        prgDialog.setCancelable(false);

        //checkInternetConnection2();
        lv = (ListView) rootView.findViewById(R.id.lv);
        //   SetPop();
        //  SetBillersStored();
        String strservdata = session.getString(SessionManagement.KEY_BILLERS);
        if (!(strservdata == null)) {
            JSONArray servdata = null;
            try {
                servdata = new JSONArray(strservdata);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (servdata.length() > 0) {
                SetBillersStored();
            } else {
                SetPop();
            }
        } else {
            SetPop();
        }
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Fragment fragment = null;
                String title = null;
                String serviceid = planetsList.get(position).getId();
                String servicename = planetsList.get(position).getServiceName();
                String label = planetsList.get(position).getLabel();
                Bundle b = new Bundle();
                b.putString("serviceid", serviceid);
                b.putString("servicename", servicename);


                fragment = new SpecBillMenu();
                title = servicename;


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
        ArrayList<String> newarrlist = session.getSets("serviceid");
        if (!(newarrlist == null)) {
            for (int s = 0; s < newarrlist.size(); s++) {
                SecurityLayer.Log("Service Id", newarrlist.get(s));

            }

        }
        return rootView;
    }


    public void StartChartAct(int i) {


    }

    @Override
    public void onResume() {
        super.onResume();
        // put your code here...

    }

    public void SetBillersStored() {
        planetsList.clear();
        try {
            String strservdata = session.getString(SessionManagement.KEY_BILLERS);
            JSONArray servdata = new JSONArray(strservdata);
            if (servdata.length() > 0) {


                JSONObject json_data = null;
                for (int i = 0; i < servdata.length(); i++) {
                    json_data = servdata.getJSONObject(i);
                    //String accid = json_data.getString("benacid");


                    String id = json_data.optString("id");

                    String label = json_data.optString("label");
                    String serviceName = json_data.optString("serviceName");


                    planetsList.add(new GetServicesData(id, label, serviceName));


                }
                if (!(getActivity() == null)) {
                    Collections.sort(planetsList, new Comparator<GetServicesData>() {
                        public int compare(GetServicesData d1, GetServicesData d2) {
                            return d1.getServiceName().compareTo(d2.getServiceName());
                        }
                    });
                    aAdpt = new ServicesMenuAdapt(planetsList, getActivity());


                    lv.setAdapter(aAdpt);
                }


            } else {
                if (!(getActivity() == null)) {
                    Toast.makeText(
                            getActivity(),
                            "No services available  ",
                            Toast.LENGTH_LONG).show();
                }
            }
        } catch (JSONException e) {
            SecurityLayer.Log("encryptionJSONException", e.toString());
            // TODO Auto-generated catch block
            if (!(getActivity() == null)) {
                Toast.makeText(getActivity(), getActivity().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
            }
            // SecurityLayer.Log(e.toString());

        }
    }

    public void SetPop() {
        planetsList.clear();
        prgDialog.show();
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        String usid = Utility.gettUtilUserId(getActivity());
        String agentid = Utility.gettUtilAgentId(getActivity());
        String mobnoo = Utility.gettUtilMobno(getActivity());
        String params = CHANNEL_ID + usid + "/" + agentid + "/" + mobnoo;
        GetServv(params);
        /*Call<GetServices> call = apiService.getServices("1",usid,agentid,"0000");
        call.enqueue(new Callback<GetServices>() {
            @Override
            public void onResponse(Call<GetServices>call, Response<GetServices> response) {
                String responsemessage = response.body().getMessage();

                SecurityLayer.Log("Response Message",responsemessage);
                planetsList = response.body().getResults();

                if(!(planetsList == null)) {
                    if(planetsList.size() > 0) {

                        aAdpt = new ServicesMenuAdapt(planetsList, getActivity());
                        lv.setAdapter(aAdpt);
                    }else{
                        Toast.makeText(
                                getActivity(),
                                "No services available  ",
                                Toast.LENGTH_LONG).show();
                    }
                }
                prgDialog.dismiss();
            }

            @Override
            public void onFailure(Call<GetServices>call, Throwable t) {
                // Log error here since request failed
                Toast.makeText(
                        getActivity(),
                        "Throwable Error: "+t.toString(),
                        Toast.LENGTH_LONG).show();
                prgDialog.dismiss();
            }
        });*/
    }

    private void GetServv(String params) {

        String endpoint = "billpayment/services.action";


        String usid = Utility.gettUtilUserId(getActivity());
        String agentid = Utility.gettUtilAgentId(getActivity());


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
                                        session.setString(SessionManagement.KEY_SETBILLERS, "Y");
                                        session.setString(SessionManagement.KEY_BILLERS, servdata.toString());

                                        JSONObject json_data = null;
                                        for (int i = 0; i < servdata.length(); i++) {
                                            json_data = servdata.getJSONObject(i);
                                            //String accid = json_data.getString("benacid");


                                            String id = json_data.optString("id");

                                            String label = json_data.optString("label");
                                            String serviceName = json_data.optString("serviceName");


                                            planetsList.add(new GetServicesData(id, label, serviceName));


                                        }
                                        if (!(getActivity() == null)) {
                                            Collections.sort(planetsList, new Comparator<GetServicesData>() {
                                                public int compare(GetServicesData d1, GetServicesData d2) {
                                                    return d1.getServiceName().compareTo(d2.getServiceName());
                                                }
                                            });
                                            aAdpt = new ServicesMenuAdapt(planetsList, getActivity());


                                            lv.setAdapter(aAdpt);
                                        }


                                    } else {
                                        if (!(getActivity() == null)) {
                                            Toast.makeText(
                                                    getActivity(),
                                                    "No services available  ",
                                                    Toast.LENGTH_LONG).show();
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

                                ((FMobActivity) getActivity()).LogOut();


                            }
                        } else {
                            if (!(getActivity() == null)) {
                                Toast.makeText(
                                        getActivity(),
                                        "There was an error on your request",
                                        Toast.LENGTH_LONG).show();

                            }
                        }
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
                    if (!(getActivity() == null)) {
                        Toast.makeText(getActivity(), getActivity().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                        // SecurityLayer.Log(e.toString());
                        ((FMobActivity) getActivity()).SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getActivity());

                    }

                } catch (Exception e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    if (!(getActivity() == null)) {
                        ((FMobActivity) getActivity()).SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getActivity());
                    }
                    // SecurityLayer.Log(e.toString());
                }
                try {
                    if ((prgDialog != null) && prgDialog.isShowing()) {
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
                // Log error here since request failed
                SecurityLayer.Log("throwable error", t.toString());

                if (!(getActivity() == null)) {

                    try {
                        if ((prgDialog != null) && prgDialog.isShowing()) {
                            prgDialog.dismiss();
                        }
                    } catch (final IllegalArgumentException e) {
                        // Handle or log or ignore
                    } catch (final Exception e) {
                        // Handle or log or ignore
                    } finally {

                    }

                    Toast.makeText(
                            getActivity(),
                            "There was an error on your request",
                            Toast.LENGTH_LONG).show();

                    ((FMobActivity) getActivity()).SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getActivity());

                }

            }
        });

    }


}