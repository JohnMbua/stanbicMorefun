package com.stanbicagent;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.stanbicagent.R;
import com.afollestad.materialdialogs.MaterialDialog;
import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;

import org.json.JSONException;
import org.json.JSONObject;

import rest.ApiInterface;
import rest.ApiSecurityClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import security.SecurityLayer;

import static com.stanbicagent.ApplicationConstants.CHANNEL_ID;


public class DialogSignInFragment extends DialogFragment implements View.OnClickListener {
    private EditText mEditText;
    Button btnconfirm;
    ProgressDialog pro ;
    TextView txserv;
    String serv,encpin;
    private PinLockView mPinLockView;
    private IndicatorDots mIndicatorDots;
    ImageView imv;
    SessionManagement session;
String finpin;
    private PinLockListener mPinLockListener = new PinLockListener() {
        @Override
        public void onComplete(String pin) {

            //SecurityLayer.Log(TAG, "Pin complete: " + pin);
            finpin = pin;
        }

        @Override
        public void onEmpty() {
            SecurityLayer.Log("Pin Empty", "Pin empty");
        }

        @Override
        public void onPinChange(int pinLength, String intermediatePin) {
            //	SecurityLayer.Log(TAG, "Pin changed, new length " + pinLength + " with intermediate pin " + intermediatePin);
        }
    };
    public DialogSignInFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.signindialog, container, false);
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        pro = new ProgressDialog(getActivity());
        pro.setMessage("Loading...");
        pro.setTitle("");
        pro.setCancelable(false);
        session = new SessionManagement(getActivity());
        txserv = (TextView) view.findViewById(R.id.serv);

        imv = (ImageView) view.findViewById(R.id.imv);
        btnconfirm = (Button) view.findViewById(R.id.signinn);
        btnconfirm.setOnClickListener(this);
        imv.setOnClickListener(this);
       // getDialog().setTitle("Enter Attendant PIN");


        Bundle bundle = getArguments();
        serv = bundle.getString("SERV","");
        if(serv.equals("PROF")) {
            txserv.setText("My Profile");
        }
        if(serv.equals("MYPERF")) {
            txserv.setText("My Performance");
        }
        if(serv.equals("INBOX")) {
            txserv.setText("My Inbox");
        }

        if(serv.equals("MINIST")) {
            txserv.setText("Agent Account");
        }
        if(serv.equals("COMM")) {
            txserv.setText("Commission Wallet");
        }

        mPinLockView = (PinLockView) view.findViewById(R.id.pin_lock_view);
        mPinLockView.setPinLockListener(mPinLockListener);
        mIndicatorDots = (IndicatorDots) view.findViewById(R.id.indicator_dots);

        mPinLockView.attachIndicatorDots(mIndicatorDots);


        mPinLockView.setPinLength(5);
        mPinLockView.setTextColor(getResources().getColor(R.color.fab_material_blue_grey_900));

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
                                String cntopen = datas.optString("canOpenAccount");
                                session.setString(SessionManagement.KEY_SETCNTOPEN,cntopen);
                                if(status.equals("F")) {
                                    getActivity().finish();
                                    Intent mIntent = new Intent(getActivity(), ForceResetPin.class);
                                    mIntent.putExtra("pinna", encpin);
                                    startActivity(mIntent);
                                }else {
                                    if (service.equals("PROF")) {
//                                        Fragment fragment = new ChangeACName();
//                                        String title = "Mini Statement";
//                                        ((FMobActivity) getActivity()).addFragment(fragment, title);

                                        startActivity(new Intent(getActivity(), ChangeAcNameActivity.class));
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
                        }   else if(respcode.equals("002")){
                            dismiss();
                            Toast.makeText(
                                    getActivity(), responsemessage,
                                    Toast.LENGTH_LONG).show();

                            ((FMobActivity) getActivity()).showEditDialog(serv);
                        }
                        else {

                           /* Toast.makeText(
                                    getActivity(),
                                    responsemessage,
                                    Toast.LENGTH_LONG).show();*/
                           dismiss();
setDialog(responsemessage);

                        }

                    }
                    else {

                        Toast.makeText(
                                getActivity(),
                                "There was an error on your request",
                                Toast.LENGTH_LONG).show();

                        setDialog("There was an error on your request");


                    }

                } catch (JSONException e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    // TODO Auto-generated catch block
                    if (!(getActivity() == null)){
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

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                SecurityLayer.Log("Throwable error",t.toString());

                if ((pro != null) && pro.isShowing() && !(getActivity() == null)) {
                    pro.dismiss();
                    Toast.makeText(
                            getActivity(),
                            "There was an error processing your request",
                            Toast.LENGTH_LONG).show();
                    ((FMobActivity) getActivity()).SetForceOutDialog(getString(R.string.forceout),getString(R.string.forceouterr),getActivity());

                }


            }
        });

    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.signinn){

            if(Utility.isNotNull(finpin)) {
                String pubkey = "97206B46CE46376894703ECE161F31F2";
              encpin =  Utility.b64_sha256(finpin);
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


        if(view.getId() == R.id.imv){
dismiss();
        }
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
}
