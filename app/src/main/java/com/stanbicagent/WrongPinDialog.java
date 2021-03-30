package com.stanbicagent;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.stanbicagent.R;
import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketTimeoutException;

import rest.ApiInterface;
import rest.ApiSecurityClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import security.SecurityLayer;

import static com.stanbicagent.ApplicationConstants.CHANNEL_ID;


public class WrongPinDialog extends DialogFragment implements View.OnClickListener {
    private EditText mEditText;
    Button btnconfirm;
    ProgressDialog pro ;
    String serv;
    ProgressDialog prgDialog,prgDialog2;
    public WrongPinDialog() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dialog, container, false);
        pro = new ProgressDialog(getActivity());
        pro.setMessage("Loading...");
        pro.setTitle("");
        pro.setCancelable(false);

        prgDialog2 = new ProgressDialog(getActivity());
        prgDialog2.setMessage("Loading....");
        prgDialog2.setCancelable(false);
        mEditText = (EditText) view.findViewById(R.id.txt_your_name);
        btnconfirm = (Button) view.findViewById(R.id.button2);
        btnconfirm.setOnClickListener(this);
       // getDialog().setTitle("Enter Attendant PIN");


        Bundle bundle = getArguments();
        serv = bundle.getString("SERV","");
        return view;
    }

    private void IntraDepoBankResp(String params) {
        prgDialog2.show();
        String endpoint = "transfer/intrabank.action";


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


                    SecurityLayer.Log("Intra Bank Resp", response.body());
                    SecurityLayer.Log("response..:", response.body());
                    JSONObject obj = new JSONObject(response.body());
                    //obj = Utility.onresp(obj,getActivity());
                    obj = SecurityLayer.decryptTransaction(obj, getActivity());
                    SecurityLayer.Log("decrypted_response", obj.toString());


                    JSONObject datas = obj.optJSONObject("data");
                    //session.setString(SecurityLayer.KEY_APP_ID,appid);

                    if (!(response.body() == null)) {
                        String respcode = obj.optString("responseCode");
                        String responsemessage = obj.optString("message");
                        String agcmsn = obj.optString("fee");
                        SecurityLayer.Log("Response Message", responsemessage);

                        if (Utility.isNotNull(respcode) && Utility.isNotNull(respcode)) {
                            if (!(Utility.checkUserLocked(respcode))) {
                                if (respcode.equals("00")) {
                                    String totfee = "0.00";
                                    if (!(datas == null)) {
                                        totfee = datas.optString("fee");
                                    }

                                    Bundle b = new Bundle();
                                   /* b.putString("recanno", recanno);
                                    b.putString("amou", amou);
                                    String refcodee = datas.optString("referenceCode");
                                    b.putString("refcode", refcodee);
                                    b.putString("narra", narra);
                                    b.putString("ednamee", ednamee);
                                    b.putString("ednumbb", ednumbb);
                                    b.putString("txtname", txtname);
                                    b.putString("agcmsn", agcmsn);
                                    b.putString("fee", totfee);
                                    b.putString("trantype", "D");*/
                                    Fragment fragment = new FinalConfDepoTrans();

                                    fragment.setArguments(b);
                                    FragmentManager fragmentManager = getFragmentManager();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    //  String tag = Integer.toString(title);
                                    fragmentTransaction.replace(R.id.container_body, fragment, "Confirm Transfer");
                                    fragmentTransaction.addToBackStack("Confirm Transfer");
                                    ((FMobActivity) getActivity())
                                            .setActionBarTitle("Confirm Transfer");
                                    fragmentTransaction.commit();
                                }

                                else if(respcode.equals("002")){


                                    ((FMobActivity)getActivity()).showWrongPinDialog(serv);
                                }else {
                                    new MaterialDialog.Builder(getActivity())
                                            .title("Error")
                                            .content(responsemessage)

                                            .negativeText("Dismiss")
                                            .callback(new MaterialDialog.ButtonCallback() {
                                                @Override
                                                public void onPositive(MaterialDialog dialog) {
                                                    dialog.dismiss();
                                                }

                                                @Override
                                                public void onNegative(MaterialDialog dialog) {
                                                    dialog.dismiss();
                                                }
                                            })
                                            .show();

                              /*      txstatus.setText("TRANSACTION FAILURE");
                                    txdesc.setText(responsemessage);
                                    Bundle params = new Bundle();
                                    params.putString("deposit_error", responsemessage);
                                    params.putString("response_code", respcode);
                                    mFirebaseAnalytics.logEvent("cash_deposit", params);*/

                                    Answers.getInstance().logCustom(new CustomEvent("cash_deposit error code")

                                            .putCustomAttribute("deposit_error", responsemessage)
                                            .putCustomAttribute("response_code", respcode)
                                    );

                                 /*   Toast.makeText(
                                            getActivity(),
                                            "" + responsemessage,
                                            Toast.LENGTH_LONG).show();*/
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

                            Toast.makeText(
                                    getActivity(),
                                    "There was an error on your request",
                                    Toast.LENGTH_LONG).show();
                           /* txstatus.setText("TRANSACTION FAILURE");
                            txdesc.setText("There was an error on your request");*/

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
                    Toast.makeText(getActivity(), getActivity().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                    // SecurityLayer.Log(e.toString());

                } catch (Exception e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    // SecurityLayer.Log(e.toString());
                }
                if ((!(getActivity() == null)) && !(prgDialog2 == null) && prgDialog2.isShowing()) {
                    prgDialog2.dismiss();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                // Log error here since request failed

                if (t instanceof SocketTimeoutException) {
                    //   message = "Socket Time out. Please try again.";

                    setDialog("Your request has been received.Please wait shortly for feedback");
                }
                SecurityLayer.Log("throwable error", t.toString());


                Toast.makeText(
                        getActivity(),
                        "There was an error on your request",
                        Toast.LENGTH_LONG).show();


                if ((!(getActivity() == null)) && !(prgDialog2 == null) && prgDialog2.isShowing()) {
                    prgDialog2.dismiss();
                }
            }
        });
    }



    public void setDialog(String message){
        new MaterialDialog.Builder(getActivity())
                .title("Error")
                .content(message)

                .negativeText("Dismiss")
                .callback(new MaterialDialog.ButtonCallback()  {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        dialog.dismiss();
                    }
                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        dialog.dismiss();
                    }
                })
                .show();
    }



    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.button2){
            String pin =  mEditText.getText().toString();
            Log.v("Login Pin",pin);
            if(Utility.isNotNull(pin)) {
                String encpin = Utility.getencpin(pin);
                String usid = Utility.gettUtilUserId(getActivity());
                String mobnoo = Utility.gettUtilMobno(getActivity());
                String params = CHANNEL_ID + usid + "/" + encpin + "/" + mobnoo;
                //LogRetro(params, serv);
            }else{
                Toast.makeText(
                        getActivity(),
                        "Please enter a valid value for Attendant PIN",
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}
