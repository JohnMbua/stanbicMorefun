package com.stanbicagent;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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

import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketTimeoutException;

import model.GetFee;
import rest.ApiClient;
import rest.ApiInterface;
import rest.ApiSecurityClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import security.SecurityLayer;

import static com.stanbicagent.ApplicationConstants.CHANNEL_ID;


public class ConfirmAirtime extends Fragment  implements View.OnClickListener {
    TextView reccustid, recamo, rectelco, step2, txtfee,acbal;
    Button btnsub;
    String txtcustid, amou, narra, ednamee, ednumbb, serviceid, billid,agbalance;
    ProgressDialog prgDialog, prgDialog2;
    String telcoop;
    EditText amon, edacc, pno, txtamount, txtnarr, edname, ednumber;
    EditText etpin;
    public static final String KEY_TOKEN = "token";
    SessionManagement session;


    // android built in classes for bluetooth operations
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;

    // needed for communication to bluetooth device / network
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;

    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;

    public ConfirmAirtime() {
        // Required empty public constructor
    }

    /*  private static Fragment newInstance(Context context) {
          LayoutOne f = new LayoutOne();

          return f;
      }
  */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.confirmairtime, null);
        session = new SessionManagement(getActivity());
        reccustid = (TextView) root.findViewById(R.id.textViewnb2);
        etpin = (EditText) root.findViewById(R.id.pin);
        acbal = (TextView) root.findViewById(R.id.txtacbal);
        recamo = (TextView) root.findViewById(R.id.textViewrrs);
        rectelco = (TextView) root.findViewById(R.id.textViewrr);
        txtfee = (TextView) root.findViewById(R.id.txtfee);

        prgDialog2 = new ProgressDialog(getActivity());
        prgDialog2.setMessage("Loading....");
        prgDialog2.setCancelable(false);


        prgDialog = new ProgressDialog(getActivity());
        prgDialog.setMessage("Loading....");
        prgDialog.setCancelable(false);
        step2 = (TextView) root.findViewById(R.id.tv);
        step2.setOnClickListener(this);
        txtfee = (TextView) root.findViewById(R.id.txtfee);
        btnsub = (Button) root.findViewById(R.id.button2);
        btnsub.setOnClickListener(this);

        Bundle bundle = this.getArguments();
        if (bundle != null) {

            txtcustid = bundle.getString("mobno");
            amou = bundle.getString("amou");
            telcoop = bundle.getString("telcoop");
String newamo = amou.replace(",","");
            String txtamou = Utility.returnNumberFormat(newamo);
            if (txtamou.equals("0.00")) {
                txtamou = amou;
            }
            billid = bundle.getString("billid");
            serviceid = bundle.getString("serviceid");
            reccustid.setText(txtcustid);


            recamo.setText(ApplicationConstants.KEY_NAIRA + txtamou);
            rectelco.setText(telcoop);
            amou = Utility.convertProperNumber(amou);
            getFeeSec();

        }
        return root;
    }


    public void StartChartAct(int i) {


    }

    public void getFee() {
        if ((prgDialog2 != null)) {
            prgDialog2.show();
        }
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        String usid = Utility.gettUtilUserId(getActivity());
        String agentid = Utility.gettUtilAgentId(getActivity());
        Call<GetFee> call = apiService.getFee("1", usid, agentid, "MMO", amou);
        call.enqueue(new Callback<GetFee>() {
            @Override
            public void onResponse(Call<GetFee> call, Response<GetFee> response) {
                if (!(response.body() == null)) {
                    String responsemessage = response.body().getMessage();
                    String respfee = response.body().getFee();
                    SecurityLayer.Log("Response Message", responsemessage);
                    if (respfee == null || respfee.equals("")) {
                        txtfee.setText("N/A");
                    } else {
                        respfee = Utility.returnNumberFormat(respfee);
                        txtfee.setText(ApplicationConstants.KEY_NAIRA + respfee);
                    }
                    if ((!(getActivity() == null))&&(prgDialog2 != null) && prgDialog2.isShowing()) {
                        prgDialog2.dismiss();
                    }
                } else {
                    txtfee.setText("N/A");
                }
            }

            @Override
            public void onFailure(Call<GetFee> call, Throwable t) {
                // Log error here since request failed
                SecurityLayer.Log("Throwable error", t.toString());
                Toast.makeText(
                        getActivity(),
                        "There was an error processing your request",
                        Toast.LENGTH_LONG).show();
                if (!(getActivity() == null)&& (prgDialog2 != null) && prgDialog2.isShowing()) {
                    prgDialog2.dismiss();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.button2) {
                          txtcustid = Utility.convertMobNumber(txtcustid);
           if (Utility.checkInternetConnection(getActivity())) {
                String agpin  = etpin.getText().toString();
                if (Utility.isNotNull(txtcustid)) {
                    if (Utility.isNotNull(amou)) {
                        if (Utility.isNotNull(agpin)) {
                            /*double dbamo = Double.parseDouble(amou);
                            Double dbagbal = Double.parseDouble(agbalance);
                            if(dbamo <= dbagbal){*/
                            String encrypted = null;



                                    ApiInterface apiService =
                                            ApiClient.getClient().create(ApiInterface.class);

                       String usid = Utility.gettUtilUserId(getActivity());
                        String agentid = Utility.gettUtilAgentId(getActivity());
                        final String mobnoo = "0"+Utility.gettUtilMobno(getActivity());
                            String emaill = Utility.gettUtilEmail(getActivity());

                            String params = CHANNEL_ID+usid+"/"+agentid+"/"+mobnoo+"/"+billid+"/"+serviceid+"/"+amou+"/01/"+txtcustid+"/"+emaill+"/"+txtcustid+"/"+billid+"01";

                            Bundle b = new Bundle();
                            b.putString("mobno", txtcustid);
                            b.putString("amou", amou);
                            b.putString("telcoop", telcoop);


                            b.putString("billid", billid);
                            b.putString("serviceid", serviceid);
                            b.putString("txpin", encrypted);
                            b.putString("serv", "AIRT");
                            b.putString("params", params);
                            Fragment fragment = new TransactingProcessing();

                            fragment.setArguments(b);
                            FragmentManager fragmentManager = getFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            //  String tag = Integer.toString(title);
                            fragmentTransaction.replace(R.id.container_body, fragment, "Final Conf Airtime");
                            fragmentTransaction.addToBackStack("Final Conf");
                            ((FMobActivity) getActivity())
                                    .setActionBarTitle("Final Conf Airtime");
                            fragmentTransaction.commit();
                           //   AirtimeResp(params);


                            ClearPin();
                           /* }  else {
                                Toast.makeText(
                                        getActivity(),
                                        "The amount set is higher than your agent balance",
                                        Toast.LENGTH_LONG).show();
                            }*/
                        }

                        else {
                            Toast.makeText(
                                    getActivity(),
                                    "Please enter a valid value for Agent PIN",
                                    Toast.LENGTH_LONG).show();

                        }
                    }
                    else {
                        Toast.makeText(
                                getActivity(),
                                "Please enter a valid value for Amount",
                                Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(
                            getActivity(),
                            "Please enter a value for Customer ID",
                            Toast.LENGTH_LONG).show();
                }
            }
        }

        if (view.getId() == R.id.tv) {
            Fragment fragment = new AirtimeTransf();


            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment, "Airtime");
            fragmentTransaction.addToBackStack("Airtime");
            ((FMobActivity) getActivity())
                    .setActionBarTitle("Airtime");
            fragmentTransaction.commit();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        // put your code here...

    }

    public void ClearPin() {
        etpin.setText("");
    }

    private void getFeeSec() {
        if ((prgDialog2 != null)) {
            prgDialog2.show();
        }
        String endpoint = "fee/getfee.action";


        String usid = Utility.gettUtilUserId(getActivity());
        String agentid = Utility.gettUtilAgentId(getActivity());

        String params = CHANNEL_ID + usid + "/" + agentid + "/MMO/" + amou;
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
                    String respfee = obj.optString("fee");
                    agbalance = obj.optString("data");
                    if(Utility.isNotNull(agbalance)) {
                        acbal.setText(agbalance+ApplicationConstants.KEY_NAIRA);
                    }


                    //session.setString(SecurityLayer.KEY_APP_ID,appid);

                    if (Utility.isNotNull(respcode) && Utility.isNotNull(respcode)) {
                        if (!(Utility.checkUserLocked(respcode))) {
                            if (!(response.body() == null)) {
                                if (respcode.equals("00")) {

                                    SecurityLayer.Log("Response Message", responsemessage);

//                                    SecurityLayer.Log("Respnse getResults",datas.toString());
                                    if (respfee == null || respfee.equals("")) {
                                        txtfee.setText("N/A");
                                    } else {
                                        respfee = Utility.returnNumberFormat(respfee);
                                        txtfee.setText(ApplicationConstants.KEY_NAIRA + respfee);
                                    }

                                } else if (respcode.equals("93")) {
                                    Toast.makeText(
                                            getActivity(),
                                            responsemessage,
                                            Toast.LENGTH_LONG).show();
                                    Fragment fragment = new AirtimeTransf();
                                    String title = "Airtime";

                                    FragmentManager fragmentManager = getFragmentManager();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    //  String tag = Integer.toString(title);
                                    fragmentTransaction.replace(R.id.container_body, fragment, title);
                                    fragmentTransaction.addToBackStack(title);
                                    ((FMobActivity) getActivity())
                                            .setActionBarTitle(title);
                                    fragmentTransaction.commit();
                                    Toast.makeText(
                                            getActivity(),
                                            "Please ensure amount set is below the set limit",
                                            Toast.LENGTH_LONG).show();

                                } else {
                                    btnsub.setVisibility(View.GONE);
                                    Toast.makeText(
                                            getActivity(),
                                            responsemessage,
                                            Toast.LENGTH_LONG).show();
                                }
                            } else {
                                txtfee.setText("N/A");
                            }
                        }else{
                            ((FMobActivity) getActivity()).LogOut();
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
                    if ((!(getActivity() == null))&&(prgDialog2 != null) && prgDialog2.isShowing()) {
                        prgDialog2.dismiss();
                    }
                } catch (final IllegalArgumentException e) {
                    // Handle or log or ignore
                } catch (final Exception e) {
                    // Handle or log or ignore
                } finally {
                    // prgDialog2 = null;
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed

                SecurityLayer.Log("Throwable error", t.toString());

                try {
                    if ((!(getActivity() == null))&&(prgDialog2 != null) && prgDialog2.isShowing()) {
                        prgDialog2.dismiss();
                    }
                } catch (final IllegalArgumentException e) {
                    // Handle or log or ignore
                } catch (final Exception e) {
                    // Handle or log or ignore
                } finally {
                    //prgDialog2 = null;
                }
                if(!(getActivity() == null)) {
                    Toast.makeText(
                            getActivity(),
                            "There was an error processing your request",
                            Toast.LENGTH_LONG).show();
                    ((FMobActivity) getActivity()).SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getActivity());
                }
            }
        });

    }


    private void AirtimeResp(String params) {
        if ((prgDialog != null)) {
            prgDialog.show();
        }
        String endpoint = "billpayment/mobileRecharge.action";


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


                    SecurityLayer.Log("Intra Bank Resp", response.body());
                    SecurityLayer.Log("response..:", response.body());
                    JSONObject obj = new JSONObject(response.body());
                    //obj = Utility.onresp(obj,getActivity());
                    obj = SecurityLayer.decryptTransaction(obj, getActivity());
                    SecurityLayer.Log("decrypted_response", obj.toString());


                    //session.setString(SecurityLayer.KEY_APP_ID,appid);

                    if (!(response.body() == null)) {
                        String respcode = obj.optString("responseCode");
                        String responsemessage = obj.optString("message");
                        String agcmsn = obj.optString("fee");
                        JSONObject datas = obj.optJSONObject("data");
                        SecurityLayer.Log("Response Message", responsemessage);

                        if (Utility.isNotNull(respcode) && Utility.isNotNull(respcode)) {
                            if (!(Utility.checkUserLocked(respcode))) {
                                SecurityLayer.Log("Response Message", responsemessage);
                                Toast.makeText(
                                        getActivity(),
                                        "" + responsemessage,
                                        Toast.LENGTH_LONG).show();
                                if (respcode.equals("00")) {

//                                    SecurityLayer.Log("Respnse getResults",datas.toString());
                                    if (!(datas == null)) {

                                        String totfee = "0.00";

                                        String ttf = datas.optString("fee");
                                        if (ttf == null || ttf.equals("")) {

                                        } else {
                                            totfee = ttf;
                                        }

                                        String tref = datas.optString("refNumber");


                                        Bundle b = new Bundle();
                                        b.putString("mobno", txtcustid);
                                        b.putString("amou", amou);
                                        b.putString("telcoop", telcoop);
                                        String refcodee = datas.optString("refNumber");
                                        b.putString("refcode", refcodee);
                                        b.putString("billid", billid);
                                        b.putString("serviceid", serviceid);
                                        b.putString("agcmsn", agcmsn);
                                        b.putString("fee", totfee);
                                        b.putString("tref", tref);
                                        Fragment fragment = new FinalConfAirtime();

                                        fragment.setArguments(b);
                                        FragmentManager fragmentManager = getFragmentManager();
                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                        //  String tag = Integer.toString(title);
                                        fragmentTransaction.replace(R.id.container_body, fragment, "Final Conf Airtime");
                                        fragmentTransaction.addToBackStack("Final Conf");
                                        ((FMobActivity) getActivity())
                                                .setActionBarTitle("Final Conf Airtime");
                                        fragmentTransaction.commit();
                                    }
                                } else {
                                    Toast.makeText(
                                            getActivity(),
                                            "" + responsemessage,
                                            Toast.LENGTH_LONG).show();
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
                if ((!(getActivity() == null))&&(prgDialog != null) && prgDialog.isShowing()) {
                    prgDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                // Log error here since request failed
                SecurityLayer.Log("throwable error", t.toString());

                if (t instanceof SocketTimeoutException) {

                    SecurityLayer.Log("socket timeout error", t.toString());
                }
                SecurityLayer.Log("After Req Tok", session.getString(KEY_TOKEN));




                if(!(getActivity() == null)) {
                    if ((prgDialog != null) && prgDialog.isShowing()) {
                        prgDialog.dismiss();
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