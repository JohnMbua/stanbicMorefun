package com.stanbicagent;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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


public class DialogFragmentta extends DialogFragment implements View.OnClickListener {
    private EditText mEditText;
    Button btnconfirm;
    ProgressDialog pro ;
    TextView txserv;
    String serv,encpin;
    public DialogFragmentta() {
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
        mEditText = (EditText) view.findViewById(R.id.txt_your_name);
        txserv = (TextView) view.findViewById(R.id.serv);
        btnconfirm = (Button) view.findViewById(R.id.button2);
        btnconfirm.setOnClickListener(this);
       // getDialog().setTitle("Enter Attendant PIN");


        Bundle bundle = getArguments();
        serv = bundle.getString("SERV","");
        if(serv.equals("PROF")) {
            txserv.setText("(My Profile Page)");
        }
        if(serv.equals("MYPERF")) {
            txserv.setText("(My Performance Page)");
        }
        if(serv.equals("INBOX")) {
            txserv.setText("(My Inbox Page)");
        }

        if(serv.equals("MINIST")) {
            txserv.setText("(Mini-Statement Page)");
        }
        if(serv.equals("COMM")) {
            txserv.setText("(Commission Page)");
        }
        return view;
    }


    private void LogRetro(String params, final String service) {


        pro.show();
        String endpoint= "login/login.action/";

        String urlparams = "";
        try {
            urlparams = SecurityLayer.generalLogin(params,"23322",getActivity(),endpoint);
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
                    if ((pro != null) && pro.isShowing() && !(getActivity() == null)) {
                        pro.dismiss();
                    }
                    // JSON Object
                    SecurityLayer.Log("response..:", response.body());


                    JSONObject obj = new JSONObject(response.body());
                 /*   JSONObject jsdatarsp = obj.optJSONObject("data");
                    SecurityLayer.Log("JSdata resp", jsdatarsp.toString());
                    //obj = Utility.onresp(obj,getActivity()); */
                    obj = SecurityLayer.decryptGeneralLogin(obj, getActivity());
                    SecurityLayer.Log("decrypted_response", obj.toString());

                    String respcode = obj.optString("responseCode");
                    String responsemessage = obj.optString("message");




                    JSONObject datas = obj.optJSONObject("data");

                    //session.setString(SecurityLayer.KEY_APP_ID,appid);

                    if (Utility.isNotNull(respcode) && Utility.isNotNull(responsemessage)) {
                        if ((Utility.checkUserLocked(respcode))) {
                            ((FMobActivity) getActivity()).LogOut();
                        }
                        SecurityLayer.Log("Response Message", responsemessage);

                        if (respcode.equals("00")) {
                            if (!(datas == null)) {
                                dismiss();
                                String status = datas.optString("status");
                                if(status.equals("F")) {
                                    getActivity().finish();
                                    Intent mIntent = new Intent(getActivity(), ForceResetPin.class);
                                    mIntent.putExtra("pinna", encpin);
                                    startActivity(mIntent);
                                }else {
                                    if (service.equals("PROF")) {
                                        Fragment fragment = new ChangeACName();
                                        String title = "Mini Statement";
                                        ((FMobActivity) getActivity()).addFragment(fragment, title);
                                    }
                                    if (service.equals("MYPERF")) {
                                   /* android.app.Fragment  fragmennt = new SelChartNewVers();
                                    String titlee = "My Performance";
                                    ((FMobActivity) getActivity()).addAppFragment(fragmennt,titlee);*/

                                        startActivity(new Intent(getActivity(), MyPerfActivity.class));
                                    }
                                    if (service.equals("INBOX")) {
                                 /*   android.app.Fragment  fragmennt = new Inbox();
                                    String titlee = "Inbox";
                                    ((FMobActivity) getActivity()).addAppFragment(fragmennt,titlee);*/


                                        startActivity(new Intent(getActivity(), InboxActivity.class));
                                    }

                                    if (service.equals("MINIST")) {
                                      /*  android.app.Fragment fragment = new Minstat();
                                        String title = "Mini Statement";
                                        ((FMobActivity) getActivity()).addAppFragment(fragment, title);*/

                                        startActivity(new Intent(getActivity(), MinistatActivity.class));
                                    }
                                    if (service.equals("COMM")) {
                                  /*  android.app.Fragment  fragment = new CommReport();


                                    String title = "Commissions Report";
                                    ((FMobActivity)getActivity()).addAppFragment(fragment,title);*/

                                        startActivity(new Intent(getActivity(), CommissionActivity.class));
                                    }
                                }
                            }
                        }
                        else {

                            Toast.makeText(
                                    getActivity(),
                                    responsemessage,
                                    Toast.LENGTH_LONG).show();


                        }

                    }
                    else {

                        Toast.makeText(
                                getActivity(),
                                "There was an error on your request",
                                Toast.LENGTH_LONG).show();


                    }

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

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                SecurityLayer.Log("Throwable error",t.toString());
                Toast.makeText(
                        getActivity(),
                        "There was an error processing your request",
                        Toast.LENGTH_LONG).show();
                if ((pro != null) && pro.isShowing() && !(getActivity() == null)) {
                    pro.dismiss();
                }
                ((FMobActivity) getActivity()).SetForceOutDialog(getString(R.string.forceout),getString(R.string.forceouterr),getActivity());


            }
        });

    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.button2){
            String pin =  mEditText.getText().toString();
            Log.v("Login Pin",pin);
            if(Utility.isNotNull(pin)) {
              encpin = Utility.getencpin(pin);
                SecurityLayer.Log("Enc Pin",encpin);
               // encpin = Utility.b64_sha256(encpin);
                String usid = Utility.gettUtilUserId(getActivity());
                String mobnoo = Utility.gettUtilMobno(getActivity());
                SecurityLayer.Log("Base64 Pin",encpin);
                String params = CHANNEL_ID + usid + "/" + encpin + "/" + mobnoo;
                LogRetro(params, serv);
            }else{
                Toast.makeText(
                        getActivity(),
                        "Please enter a valid value for Attendant PIN",
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}
