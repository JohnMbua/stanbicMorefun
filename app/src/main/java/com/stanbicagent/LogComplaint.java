package com.stanbicagent;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.stanbicagent.R;

import org.json.JSONException;
import org.json.JSONObject;

import rest.ApiInterface;
import rest.ApiSecurityClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import security.SecurityLayer;

import static com.stanbicagent.ApplicationConstants.CHANNEL_ID;


public class LogComplaint extends Fragment implements View.OnClickListener{
    ImageView imageView1;

    TextView tvdate;
    Button btn4,btnok;
    EditText edreason;
    ProgressDialog prgDialog, prgDialog2;
    public static final String DATEPICKER_TAG = "datepicker";
    String dateset = null;
    Spinner sp3;
    String stracno,stramo,strefno,strdatee;
    EditText edacno,edamo,edrefno,eddate;

    public LogComplaint() {
        // Required empty public constructor
    }

    /*  private static Fragment newInstance(Context context) {
          LayoutOne f = new LayoutOne();

          return f;
      }
  */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.logcomplaint, null);
        sp3 = (Spinner) root.findViewById(R.id.spin3);

        prgDialog2 = new ProgressDialog(getActivity());
        prgDialog2.setMessage("Loading....");
        prgDialog2.setCancelable(false);
        edacno = (EditText) root.findViewById(R.id.recacno);
        edamo = (EditText) root.findViewById(R.id.edamo);
        edrefno = (EditText) root.findViewById(R.id.edrefno);
        eddate = (EditText) root.findViewById(R.id.timestamp);
        edreason = (EditText) root.findViewById(R.id.edit_text);
        btn4 = (Button) root.findViewById(R.id.button2);
        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(
                getActivity(), R.array.complaintty, android.R.layout.simple_spinner_item);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
          sp3.setAdapter(adapter3);
          btn4.setOnClickListener(this);
        if(!(getArguments() == null)) {
            stracno = getArguments().getString("txaco");
            stramo = getArguments().getString("txamo");
            strefno = getArguments().getString("txref");
            strdatee = getArguments().getString("txdate");
            edamo.setText(stramo);
            edacno.setText(stracno);
            edrefno.setText(strefno);
            eddate.setText(strdatee);

            eddate.setEnabled(false);
            edacno.setEnabled(false);
            edrefno.setEnabled(false);
            edamo.setEnabled(false);
        }
        return root;
    }


    public void StartChartAct(int i) {


    }

    @Override
    public void onResume() {
        super.onResume();
        // put your code here...

    }


    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.button2) {
String reas = edreason.getText().toString();
           SecurityLayer.Log("Reason",reas);
if(Utility.isNotNull(reas)){
    LogCompl(reas);
}
        }
    }



    private void LogCompl(String desc) {

        if ((prgDialog2 != null)  && !(getActivity() == null)) {
            prgDialog2.show();
// /visit/visitshop.action/{channel}/{userId}/{agentId}/{requestedVisitDate}/{reason} /visit/visitshop.action/1/SURESHD/123123213/05-02-2018/test visti "
 //   /complaint/log.action/{channel}/{userId}/{agentId}/{customerAccountNumber}/{amount}/{transactionDateTime}/{description}
            String endpoint = "complaint/log.action";


            String usid = Utility.gettUtilUserId(getActivity());
            String agentid = Utility.gettUtilAgentId(getActivity());
            String mobnoo = Utility.gettUtilMobno(getActivity());
            String params = CHANNEL_ID + usid + "/" + agentid + "/" + stracno +"/"+stramo+"/"+strdatee+"/"+desc;
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
                                                "Complaint has been successfully logged ",
                                                Toast.LENGTH_LONG).show();
                                        prgDialog2.dismiss();
                                       /* Fragment  fragment = new ViewAgentRequests();


                                        FragmentManager fragmentManager = getFragmentManager();
                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                        //  String tag = Integer.toString(title);
                                        fragmentTransaction.replace(R.id.container_body, fragment,"View Requests");
                                        fragmentTransaction.addToBackStack("View Requests");
                                        ((FMobActivity)getActivity())
                                                .setActionBarTitle("View Requests");
                                        fragmentTransaction.commit();*/
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
                        if((!(getActivity() == null)) && !(prgDialog2 == null) && prgDialog2.isShowing()) {
                            prgDialog2.dismiss();
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
                        if((!(getActivity() == null)) && !(prgDialog2 == null) && prgDialog2.isShowing()) {
                            prgDialog2.dismiss();
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
