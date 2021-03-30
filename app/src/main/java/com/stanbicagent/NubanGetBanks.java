package com.stanbicagent;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

import adapter.adapter.OTBRetroAdapt;
import model.GetBanks;
import model.GetBanksData;
import rest.ApiClient;
import rest.ApiInterface;
import rest.ApiSecurityClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import security.SecurityLayer;


public class NubanGetBanks extends Fragment {
    ListView lv;
    OTBRetroAdapt aAdpt;
    String bankname,bankcode,recanno;
    ProgressDialog prgDialog;
    List<GetBanksData> planetsList = new ArrayList<GetBanksData>();
    SessionManagement session;
    public NubanGetBanks() {
        // Required empty public constructor
    }
  /*  private static Fragment newInstance(Context context) {
        LayoutOne f = new LayoutOne();

        return f;
    }
*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.otherbankslist, null);
        session = new SessionManagement(getActivity());
   lv = (ListView) root.findViewById(R.id.lv);
        prgDialog = new ProgressDialog(getActivity());
        prgDialog.setMessage("Loading....");
        prgDialog.setCancelable(false);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            recanno = bundle.getString("recanno");
            JSONArray newjs = Utility.getNubanAlgo(recanno);
            SecurityLayer.Log("Nuban Algo Result",newjs.toString());
            SetNubanBanksDaa(newjs);

        }
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                String bankname = planetsList.get(position).getBankName();
                String bankcode = planetsList.get(position).getBankCode();
                Bundle b  = new Bundle();
                b.putString("bankname",bankname);
                b.putString("bankcode",bankcode);
                b.putString("recanno",recanno);
                Fragment  fragment = new SendOTB();

                fragment.setArguments(b);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                //  String tag = Integer.toString(title);
                fragmentTransaction.replace(R.id.container_body, fragment,"Other Banks");
                fragmentTransaction.addToBackStack("Other Banks");
                ((FMobActivity)getActivity())
                        .setActionBarTitle("Other Banks");
                fragmentTransaction.commit();

            }
        });
        return root;
    }



    public void StartChartAct(int i){


    }
    public void SetNubanBanksDaa(JSONArray servdata){
        planetsList.clear();
        try{

            if(servdata.length() > 0){


                JSONObject json_data = null;
                for (int i = 0; i < servdata.length(); i++) {
                    json_data = servdata.getJSONObject(i);
                    //String accid = json_data.getString("benacid");


                    String instName = json_data.optString("name");

                    String bankCode = json_data.optString("sortcode");




                    planetsList.add( new GetBanksData(instName,bankCode) );




                }
                if(!(getActivity() == null)) {
                    SecurityLayer.Log("Get Banks Data Name",planetsList.get(0).getBankName());
                    Collections.sort(planetsList, new Comparator<GetBanksData>(){
                        public int compare(GetBanksData d1, GetBanksData d2){
                            return d1.getBankName().compareTo(d2.getBankName());
                        }
                    });
                    aAdpt = new OTBRetroAdapt(planetsList, getActivity());
                    lv.setAdapter(aAdpt);
                }


            }else{
                Toast.makeText(
                        getActivity(),
                        "No services available  ",
                        Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            SecurityLayer.Log("encryptionJSONException", e.toString());
            // TODO Auto-generated catch block
            Toast.makeText(getActivity(), getActivity().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
            // SecurityLayer.Log(e.toString());

        }
    }
    public void SetBanks(){



        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<GetBanks> call = apiService.getBanks();

        call.enqueue(new Callback<GetBanks>() {
            @Override
            public void onResponse(Call<GetBanks>call, Response<GetBanks> response) {
                String responsemessage = response.body().getMessage();

                SecurityLayer.Log("Response Message",responsemessage);
                planetsList = response.body().getResults();
//                                    SecurityLayer.Log("Respnse getResults",datas.toString());
                if(!(planetsList == null)) {
                    SecurityLayer.Log("Get Banks Data Name",planetsList.get(0).getBankName());
                    Collections.sort(planetsList, new Comparator<GetBanksData>(){
                        public int compare(GetBanksData d1, GetBanksData d2){
                            return d1.getBankName().compareTo(d2.getBankName());
                        }
                    });
                    aAdpt = new OTBRetroAdapt(planetsList, getActivity());
                    lv.setAdapter(aAdpt);
                }
                prgDialog.dismiss();
            }

            @Override
            public void onFailure(Call<GetBanks>call, Throwable t) {
                // Log error here since request failed
                Toast.makeText(
                        getActivity(),
                        "Throwable Error: "+t.toString(),
                        Toast.LENGTH_LONG).show();
                prgDialog.dismiss();
            }
        });



    }
    @Override
    public void onResume(){
        super.onResume();
        // put your code here...

    }
    private void GetServv() {
        if ((prgDialog != null)) {
            prgDialog.show();

            String endpoint = "transfer/getbanks.action";


            String usid = Utility.gettUtilUserId(getActivity());
            String agentid = Utility.gettUtilAgentId(getActivity());


            String params = usid + "/" + agentid + "/93939393";

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


                        SecurityLayer.Log("Get Banks Resp", response.body());
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
                                        session.setString(SessionManagement.KEY_SETBANKS, "Y");
                                        session.setString(SessionManagement.KEY_BANKS, servdata.toString());
                                        if (servdata.length() > 0) {


                                            JSONObject json_data = null;
                                            for (int i = 0; i < servdata.length(); i++) {
                                                json_data = servdata.getJSONObject(i);
                                                //String accid = json_data.getString("benacid");


                                                String instName = json_data.optString("instName");

                                                String bankCode = json_data.optString("bankCode");


                                                planetsList.add(new GetBanksData(instName, bankCode));


                                            }
                                            if (!(getActivity() == null)) {
                                                SecurityLayer.Log("Get Banks Data Name", planetsList.get(0).getBankName());
                                                Collections.sort(planetsList, new Comparator<GetBanksData>() {
                                                    public int compare(GetBanksData d1, GetBanksData d2) {
                                                        return d1.getBankName().compareTo(d2.getBankName());
                                                    }
                                                });
                                                aAdpt = new OTBRetroAdapt(planetsList, getActivity());
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
                                    if (!(getActivity() == null)) {
                                        Toast.makeText(
                                                getActivity(),
                                                "You have been locked out of the app.Please call customer care for further details",
                                                Toast.LENGTH_LONG).show();
                                        getActivity().finish();
                                        startActivity(new Intent(getActivity(), SignInActivity.class));

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
                        // SecurityLayer.Log(e.toString());
                        ((FMobActivity) getActivity()).SetForceOutDialog(getString(R.string.forceout),getString(R.string.forceouterr),getActivity());


                    } catch (Exception e) {
                        SecurityLayer.Log("encryptionJSONException", e.toString());
                        ((FMobActivity) getActivity()).SetForceOutDialog(getString(R.string.forceout),getString(R.string.forceouterr),getActivity());

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
                        //prgDialog2 = null;
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

                    try {
                        if ((prgDialog != null) && prgDialog.isShowing()) {
                            prgDialog.dismiss();
                        }
                    } catch (final IllegalArgumentException e) {
                        // Handle or log or ignore
                    } catch (final Exception e) {
                        // Handle or log or ignore
                    } finally {
                        //prgDialog2 = null;
                    }
                    ((FMobActivity) getActivity()).SetForceOutDialog(getString(R.string.forceout),getString(R.string.forceouterr),getActivity());

                }
            });
        }
    }
}
