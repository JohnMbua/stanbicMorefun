package com.stanbicagent;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.stanbicagent.R;
import com.afollestad.materialdialogs.MaterialDialog;

import org.json.JSONException;
import org.json.JSONObject;

import rest.ApiInterface;
import rest.ApiSecurityClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import security.SecurityLayer;

import static com.stanbicagent.ApplicationConstants.CHANNEL_ID;


public class CashDepo extends Fragment implements View.OnClickListener {
    ImageView imageView1;
    EditText amon, edacc, pno, txtamount, txtnarr;
    Button btnsub;
    SessionManagement session;

    TextView accountoname;
    String depositid;
    String acname;
    RelativeLayout rlid;
    ProgressDialog prgDialog, prgDialog2;

    private static int SPLASH_TIME_OUT = 2500;

    public CashDepo() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.cashdepo, null);
        session = new SessionManagement(getActivity());


        rlid = (RelativeLayout) root.findViewById(R.id.rlid);
        accountoname = (TextView) root.findViewById(R.id.cname);


        prgDialog2 = new ProgressDialog(getActivity());
        prgDialog2.setMessage("Loading....");
        prgDialog2.setCancelable(false);

        edacc = (EditText) root.findViewById(R.id.input_payacc);
        prgDialog = new ProgressDialog(getActivity());
        prgDialog.setMessage("Loading Account Details....");
        prgDialog.setCancelable(false);
        txtamount = (EditText) root.findViewById(R.id.amount);
        txtnarr = (EditText) root.findViewById(R.id.ednarr);


        View.OnFocusChangeListener ofcListener = new MyFocusChangeListener();
        txtamount.setOnFocusChangeListener(ofcListener);
        txtnarr.setOnFocusChangeListener(ofcListener);
        edacc.setOnFocusChangeListener(ofcListener);


        btnsub = (Button) root.findViewById(R.id.button2);
        btnsub.setOnClickListener(this);

        edacc.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edacc.getText().toString().length() == 10) {
                    if (Utility.checkInternetConnection(getActivity())) {

                        Utility.hideKeyboardFrom(getActivity(), edacc);
                        if ((prgDialog != null) && !(getActivity() == null)) {
                            prgDialog.show();
                        }

                        String acno = edacc.getText().toString();
                        NameInquirySec(acno);

                       /* ApiInterface apiService =
                                ApiClient.getClient().create(ApiInterface.class);

                        Call<NameEnquiry> call = apiService.getAccountDetails("1", "suresh", "BATA0000000001", "0000", "0", acno);
                        call.enqueue(new Callback<NameEnquiry>() {
                            @Override
                            public void onResponse(Call<NameEnquiry> call, Response<NameEnquiry> response) {
                                String responsemessage = response.body().getMessage();

                                SecurityLayer.Log("Response Message", responsemessage);
                                NameEnquiryData datas = response.body().getResults();
//                                    SecurityLayer.Log("Respnse getResults",datas.toString());
                                if (!(datas == null)) {
                                    acname = datas.getAccountName();
                                    Toast.makeText(
                                            getActivity(),
                                            "Account Name: " + acname,
                                            Toast.LENGTH_LONG).show();
                                    accountoname.setText(acname);
                                }
                                else{
                                    Toast.makeText(
                                            getActivity(),
                                            "This is not a valid account number.Please check again",
                                            Toast.LENGTH_LONG).show();
                                }
                                prgDialog.dismiss();
                            }

                            @Override
                            public void onFailure(Call<NameEnquiry> call, Throwable t) {
                                // Log error here since request failed
                                SecurityLayer.Log("Throwable error",t.toString());
                                Toast.makeText(
                                        getActivity(),
                                        "There was an error processing your request",
                                        Toast.LENGTH_LONG).show();
                                prgDialog.dismiss();
                            }
                        });*/

                    }
                }
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

                // TODO Auto-generated method stub
            }
        });


        return root;
    }

    private class MyFocusChangeListener implements View.OnFocusChangeListener {

        public void onFocusChange(View v, boolean hasFocus) {

            if (v.getId() == R.id.amount && !hasFocus) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                String txt = txtamount.getText().toString();
                String fbal = Utility.returnNumberFormat(txt);
                txtamount.setText(fbal);

            }

            if (v.getId() == R.id.ednarr && !hasFocus) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);


            }
            if (v.getId() == R.id.sendname && !hasFocus) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);


            }
            if (v.getId() == R.id.sendnumber && !hasFocus) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);


            }
            if (v.getId() == R.id.input_payacc && !hasFocus) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);


            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button4) {
            rlid.setVisibility(View.VISIBLE);
            //   checkInternetConnection2();

        }
        if (view.getId() == R.id.button2) {

            if (Utility.checkInternetConnection(getActivity())) {
                final String recanno = edacc.getText().toString();
                final String amou = txtamount.getText().toString();
                final String narra = txtnarr.getText().toString();


                if (Utility.isNotNull(recanno)) {
                    if (Utility.isNotNull(amou)) {
                        String nwamo = amou.replace(",", "");
                        SecurityLayer.Log("New Amount", nwamo);
                        double txamou = Double.parseDouble(nwamo);

                        if (Utility.isNotNull(narra)) {

                            if (Utility.isNotNull(acname)) {


                                Bundle b = new Bundle();
                                b.putString("recanno", recanno);
                                b.putString("amou", amou);
                                b.putString("narra", narra);
                                b.putString("ednamee", "N/A");
                                b.putString("ednumbb", "N/A");
                                b.putString("trantype", "D");
                                b.putString("txtname", acname);
                                Fragment fragment = new ConfirmCashDepTransf();

                                fragment.setArguments(b);
                                FragmentManager fragmentManager = getFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                //  String tag = Integer.toString(title);
                                fragmentTransaction.replace(R.id.container_body, fragment, "Confirm Transfer");
                                fragmentTransaction.addToBackStack("Confirm Transfer");
                                ((FMobActivity) getActivity())
                                        .setActionBarTitle("Confirm Transfer");
                                fragmentTransaction.commit();

                            } else {
                                Toast.makeText(
                                        getActivity(),
                                        "Please enter a valid account number",
                                        Toast.LENGTH_LONG).show();
                            }

                        } else {
                            Toast.makeText(
                                    getActivity(),
                                    "Please enter a valid value for Narration",
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(
                                getActivity(),
                                "Please enter a valid value for Amount",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(
                            getActivity(),
                            "Please enter a value for Account Number",
                            Toast.LENGTH_LONG).show();
                }
            }
        }


    }


    public void SetDialog(String msg, String title) {
        new MaterialDialog.Builder(getActivity())
                .title(title)
                .content(msg)

                .negativeText("Close")
                .show();
    }


    private void NameInquirySec(String acno) {

        String endpoint = "transfer/nameenq.action";


        String usid = Utility.gettUtilUserId(getActivity());
        String agentid = Utility.gettUtilAgentId(getActivity());
        String mobileno = Utility.gettUtilAgentId(getActivity());
        String params = CHANNEL_ID + usid + "/" + agentid + "/" + mobileno + "/0/" + acno;

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
                    if (Utility.isNotNull(respcode) && Utility.isNotNull(respcode)) {
                        if ((Utility.checkUserLocked(respcode))) {
                            ((FMobActivity) getActivity()).LogOut();
                        }
                        if (!(response.body() == null)) {
                            if (respcode.equals("00")) {

                                SecurityLayer.Log("Response Message", responsemessage);

//                                    SecurityLayer.Log("Respnse getResults",datas.toString());
                                if (!(plan == null)) {
                                    acname = plan.optString("accountName");

                                    Toast.makeText(
                                            getActivity(),
                                            "Account Name: " + acname,
                                            Toast.LENGTH_LONG).show();
                                    accountoname.setText(acname);
                                } else {

                                    Toast.makeText(
                                            getActivity(),
                                            "This is not a valid account number.Please check again",
                                            Toast.LENGTH_LONG).show();


                                }

                            } else {
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
                    }

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
                    if ((prgDialog != null) && prgDialog.isShowing() && !(getActivity() == null)) {
                        prgDialog.dismiss();
                    }
                } catch (final IllegalArgumentException e) {
                    // Handle or log or ignore
                } catch (final Exception e) {
                    // Handle or log or ignore
                } finally {
                    //   prgDialog = null;
                }

                //   prgDialog.dismiss();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                SecurityLayer.Log("Throwable error", t.toString());


                if ((prgDialog != null) && prgDialog.isShowing() && !(getActivity() == null)) {
                    prgDialog.dismiss();
                }
                if (!(getActivity() == null)) {
                    Toast.makeText(
                            getActivity(),
                            "There was an error processing your request",
                            Toast.LENGTH_LONG).show();
                    ((FMobActivity) getActivity()).SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getActivity());
                }
            }
        });

    }

    /*
        public void SetForceOutDialog(String msg, final String title){
            new MaterialDialog.Builder(getActivity())
                    .title(title)
                    .content(msg)

                    .negativeText("CONTINUE")
                    .callback(new MaterialDialog.ButtonCallback()  {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            dialog.dismiss();
                        }
                        @Override
                        public void onNegative(MaterialDialog dialog) {

                            dialog.dismiss();
                            getActivity().finish();
                            session.logoutUser();

                            // After logout redirect user to Loing Activity
                            Intent i = new Intent(getActivity(), SignInActivity.class);

                            // Staring Login Activity
                            startActivity(i);

                        }
                    })
                    .show();
        }*/
    @Override
    public void onResume() {
        super.onResume();
        ((FMobActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

    }

    @Override
    public void onStop() {
        super.onStop();
        ((FMobActivity) getActivity()).getSupportActionBar().show();

    }

}
