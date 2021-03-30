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
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import adapter.ComplainsAdapt;
import model.GetComplaints;
import rest.ApiInterface;
import rest.ApiSecurityClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import security.SecurityLayer;

import static com.stanbicagent.ApplicationConstants.CHANNEL_ID;


public class ViewComplaints extends Fragment implements View.OnClickListener {
    ListView lv;
    ComplainsAdapt aAdpt;
    String bankname, bankcode;
    ProgressDialog prgDialog;
    List<GetComplaints> requestslist = new ArrayList<GetComplaints>();
    SessionManagement session;
    RelativeLayout rl;
    private RatingBar ratingBar;

    public ViewComplaints() {
        // Required empty public constructor
    }

    /*  private static Fragment newInstance(Context context) {
          LayoutOne f = new LayoutOne();

          return f;
      }
  */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.complainslist, null);
        session = new SessionManagement(getActivity());
        lv = (ListView) root.findViewById(R.id.lv);
        rl = (RelativeLayout) root.findViewById(R.id.rlmakereq);
        lv = (ListView) root.findViewById(R.id.lv);
        prgDialog = new ProgressDialog(getActivity());
        prgDialog.setMessage("Loading....");
        prgDialog.setCancelable(false);
        rl.setOnClickListener(this);

        GetServv();
//GetComplaints();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
             /*   final String rqid = requestslist.get(position).getIdd();
                final String txtstatus = requestslist.get(position).getstatus();
            //    if(txtstatus.equals("2")) {
                    final Context c = getActivity();
                    LayoutInflater layoutInflaterAndroid = LayoutInflater.from(c);
                    View mView = layoutInflaterAndroid.inflate(R.layout.user_input_dialog_box, null);
                    AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(c);
                    alertDialogBuilderUserInput.setView(mView);

                    final EditText userInputDialogEditText = (EditText) mView.findViewById(R.id.userInputDialog);
                    ratingBar = (RatingBar) mView.findViewById(R.id.ratingBar);
                    alertDialogBuilderUserInput
                            .setCancelable(false)
                            .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogBox, int id) {
                                    // ToDo get user input here
                                    String css = userInputDialogEditText.getText().toString();
                                    int   ratedValue = (int)ratingBar.getRating();
                                 String sd =   Integer.toString(ratedValue);
                                    SendFeedback(sd, rqid);
                                }
                            })

                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialogBox, int id) {
                                            dialogBox.cancel();
                                        }
                                    });

                    AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                    alertDialogAndroid.show();*/
                //   }
            }
        });

        return root;
    }


    public void StartChartAct(int i) {


    }

    @Override
    public void onResume() {
        super.onResume();
        // put your code here...

    }

    private void GetComplaints() {
        requestslist.clear();
        requestslist.add(new GetComplaints("17 Feb 2018 12:34 am", "Faulty POS Terminal Charger", "0", "01", "POS"));
        requestslist.add(new GetComplaints("18 Feb 2018 15:14 am", "No Data in SIM", "0", "01", "POS"));
        aAdpt = new ComplainsAdapt(requestslist, getActivity());
        lv.setAdapter(aAdpt);
    }

    private void GetServv() {
        if ((prgDialog != null)) {
            prgDialog.show();
// /visit/getAll.action/{channel}/{userId}/{agentId} /visit/getAll.action/1/SURESHD/123123213 "
            String endpoint = "complaint/getAllComplaints.action";
            String usid = Utility.gettUtilUserId(getActivity());
            String agentid = Utility.gettUtilAgentId(getActivity());
            String params = CHANNEL_ID + usid + "/" + agentid;
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


                                        JSONObject json_data = null;
                                        for (int i = 0; i < servdata.length(); i++) {
                                            json_data = servdata.getJSONObject(i);
                                            //String accid = json_data.getString("benacid");


                                            String txndatetime = json_data.optString("transactionDateTime");

                                            String description = json_data.optString("description");
                                            String status = json_data.optString("status");
                                            String idd = json_data.optString("id");
                                            //   requestslist.add(new GetAgentRequests(requestedVisitDate, reason,status,idd));

                                            requestslist.add(new GetComplaints(txndatetime, description, "0", idd, "POS"));

                                        }
                                        if (!(getActivity() == null)) {
                                            SecurityLayer.Log("Get Banks Data Name", requestslist.get(0).getrequestedVisitDate());

                                            aAdpt = new ComplainsAdapt(requestslist, getActivity());
                                            lv.setAdapter(aAdpt);
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

                    } catch (Exception e) {
                        SecurityLayer.Log("encryptionJSONException", e.toString());
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

                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.rlmakereq) {
           /* Fragment  fragment = new LogComplaint();


            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment,"Request Visit");
            fragmentTransaction.addToBackStack("Request Visit");
            ((FMobActivity)getActivity())
                    .setActionBarTitle("Request Visit");
            fragmentTransaction.commit();*/
        }
    }


    private void SendFeedback(String agfeedback, String reqid) {

        if ((prgDialog != null) && !(getActivity() == null)) {
            prgDialog.show();
            //    /visit/visitshopfeedback.action/{channel}/{userId}/{agentId}/{agentFeedBack}/{id}
            String endpoint = "visit/visitshopfeedback.action";


            String usid = Utility.gettUtilUserId(getActivity());
            String agentid = Utility.gettUtilAgentId(getActivity());
            String mobnoo = Utility.gettUtilMobno(getActivity());
            String params = CHANNEL_ID + usid + "/" + agentid + "/" + agfeedback + "/" + reqid;
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


                        if (!(getActivity() == null)) {
                            if (!(response.body() == null)) {
                                if (respcode.equals("00")) {

                                    SecurityLayer.Log("Response Message", responsemessage);
                                    if (!(getActivity() == null)) {
                                        Toast.makeText(
                                                getActivity(),
                                                "Your feedback has been warmly received ",
                                                Toast.LENGTH_LONG).show();
                                        prgDialog.dismiss();
                                        Fragment fragment = new ViewComplaints();


                                        FragmentManager fragmentManager = getFragmentManager();
                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                        //  String tag = Integer.toString(title);
                                        fragmentTransaction.replace(R.id.container_body, fragment, "View Requests");
                                        fragmentTransaction.addToBackStack("View Requests");
                                        ((FMobActivity) getActivity())
                                                .setActionBarTitle("View Requests");
                                        fragmentTransaction.commit();
                                    }

                                } else {
                                    if (!(getActivity() == null)) {
                                        Toast.makeText(
                                                getActivity(),
                                                responsemessage,
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            } else {

                            }
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
                    try {
                        if ((!(getActivity() == null)) && !(prgDialog == null) && prgDialog.isShowing()) {
                            prgDialog.dismiss();
                        }
                    } catch (final IllegalArgumentException e) {
                        // Handle or log or ignore
                    } catch (final Exception e) {
                        // Handle or log or ignore
                    } finally {
                        //  prgDialog2 = null;
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
                        if ((!(getActivity() == null)) && !(prgDialog == null) && prgDialog.isShowing()) {
                            prgDialog.dismiss();
                        }
                    } catch (final IllegalArgumentException e) {
                        // Handle or log or ignore
                    } catch (final Exception e) {
                        // Handle or log or ignore
                    } finally {
                        //  prgDialog2 = null;
                    }

                }
            });
        }
    }
}
