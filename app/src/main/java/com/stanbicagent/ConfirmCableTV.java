package com.stanbicagent;

import android.app.ProgressDialog;
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

import java.net.SocketTimeoutException;

import rest.ApiClient;
import rest.ApiInterface;
import rest.ApiSecurityClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import security.SecurityLayer;

import static com.stanbicagent.ApplicationConstants.CHANNEL_ID;

public class ConfirmCableTV extends Fragment implements View.OnClickListener {
    TextView reccustid, recamo, recnarr, recsendnum, recsendnam, txtfee, txtlabel,acbal;
    TextView step2, step1, step3, stt;
    Button btnsub;
    String txtcustid, amou, narra, ednamee, ednumbb, serviceid, billid, strlabl, servicename, billname, packid, paymentCode, bs,agbalance;
    String respfee;
    ProgressDialog prgDialog, prgDialog2;
    EditText amon, edacc, pno, txtamount, txtnarr, edname, ednumber;
    EditText etpin;
    boolean chkfee = false;

    public ConfirmCableTV() {
        // Required empty public constructor
    }

    /*  private static Fragment newInstance(Context context) {
          LayoutOne f = new LayoutOne();

          return f;
      }
  */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.confirmcabletv, null);
        reccustid = (TextView) root.findViewById(R.id.textViewnb2);
        etpin = (EditText) root.findViewById(R.id.pin);
        acbal = (TextView) root.findViewById(R.id.txtacbal);
        stt = (TextView) root.findViewById(R.id.recip);
        recamo = (TextView) root.findViewById(R.id.textViewrrs);
        recnarr = (TextView) root.findViewById(R.id.textViewrr);
        txtlabel = (TextView) root.findViewById(R.id.textViewnb);
        recsendnam = (TextView) root.findViewById(R.id.sendnammm);
        recsendnum = (TextView) root.findViewById(R.id.sendno);
        prgDialog2 = new ProgressDialog(getActivity());
        prgDialog2.setMessage("Loading....");
        prgDialog2.setCancelable(false);
        txtfee = (TextView) root.findViewById(R.id.txtfee);
        chkfee = false;
        step2 = (TextView) root.findViewById(R.id.tv2);
        step2.setOnClickListener(this);

        step1 = (TextView) root.findViewById(R.id.tv);
        step1.setOnClickListener(this);

        step3 = (TextView) root.findViewById(R.id.tv3);
        step3.setOnClickListener(this);


        btnsub = (Button) root.findViewById(R.id.button2);
        btnsub.setOnClickListener(this);

        Bundle bundle = this.getArguments();
        if (bundle != null) {

            txtcustid = bundle.getString("custid");
            amou = bundle.getString("amou");
            narra = bundle.getString("narra");
            ednamee = bundle.getString("ednamee");
            ednumbb = bundle.getString("ednumbb");
            billname = bundle.getString("billname");
            billid = bundle.getString("billid");
            serviceid = bundle.getString("serviceid");
            servicename = bundle.getString("servicename");
            strlabl = bundle.getString("label");
            packid = bundle.getString("packId");
            paymentCode = bundle.getString("paymentCode");
            reccustid.setText(txtcustid);

            recamo.setText(amou);
            recnarr.setText(narra);

            recsendnam.setText(ednamee);
            recsendnum.setText(ednumbb);
            txtlabel.setText(strlabl);
            amou = Utility.convertProperNumber(amou);
            getFeeSec();
        }
        return root;
    }


    public void StartChartAct(int i) {


    }
/*
    public void getFee() {
        if (!(prgDialog2 == null)) {
            prgDialog2.show();
        }
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        String usid = Utility.gettUtilUserId(getActivity());
        String agentid = Utility.gettUtilAgentId(getActivity());
        Call<GetFee> call = apiService.getFee("1", usid, agentid, "BILLPAYMENT", amou);
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

                    prgDialog2.dismiss();
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
                prgDialog2.dismiss();
            }
        });
    }*/

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.button2) {
            if (Utility.checkInternetConnection(getActivity())) {
                String agpin = etpin.getText().toString();
                if (Utility.isNotNull(txtcustid)) {
                    if (Utility.isNotNull(amou)) {
                        if (Utility.isNotNull(narra)) {
                            if (Utility.isNotNull(ednamee)) {
                                if (Utility.isNotNull(ednumbb)) {
                                    if (Utility.isNotNull(agpin)) {
                                        if (chkfee) {
                                            boolean chkdb = true;
                                            try {
                                                double dbamo = Double.parseDouble(amou);
                                                Double dbagbal = 0.0;

                                                if (Utility.isNotNull(agbalance)) {
                                                    dbagbal = Double.parseDouble(agbalance);
                                                    if (dbamo > dbagbal) {
                                                        chkdb = false;
                                                    }
                                                }
                                            }catch (NumberFormatException e){
                                                e.printStackTrace();
                                            }
                                            if(chkdb){
                                            String encrypted = null;

                                            if (!(prgDialog2 == null)) {
                                              //  prgDialog2.show();
                                            }

                                            ApiInterface apiService =
                                                    ApiClient.getClient().create(ApiInterface.class);
                                            String usid = Utility.gettUtilUserId(getActivity());
                                            String agentid = Utility.gettUtilAgentId(getActivity());
                                            String emaill = Utility.gettUtilEmail(getActivity());
                                            final String mobnoo = "0" + Utility.gettUtilMobno(getActivity());
                                            if (Utility.isNotNull(paymentCode) || paymentCode.equals("")) {
                                                paymentCode = billid + "01";
                                            }
                                            if ((!Utility.isNotNull(packid)) || packid.equals("")) {
                                                packid = "01";
                                            }
                                            String params = CHANNEL_ID + usid + "/" + agentid + "/" + mobnoo + "/" + billid + "/" + serviceid + "/" + amou + "/" + packid + "/" + ednumbb + "/" + emaill + "/" + txtcustid + "/" + paymentCode;

                                        //    PayBillResp(params);
                                            Bundle b = new Bundle();
                                            b.putString("custid", txtcustid);
                                            b.putString("amou", amou);
                                            b.putString("narra", narra);
                                            b.putString("billname", billname);
                                            b.putString("ednamee", ednamee);
                                            b.putString("ednumbb", ednumbb);
                                            b.putString("billid", billid);
                                            b.putString("serviceid", serviceid);
                                            b.putString("label", strlabl);
                                            b.putString("fullname", bs);

                                            b.putString("params", params);
                                            b.putString("fee", respfee);
                                            b.putString("txpin", encrypted);
                                            b.putString("serv", "CABLETV");

                                            Fragment fragment = new TransactingProcessing();

                                            fragment.setArguments(b);
                                            FragmentManager fragmentManager = getFragmentManager();
                                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                            //  String tag = Integer.toString(title);
                                            fragmentTransaction.replace(R.id.container_body, fragment, "Final Conf Cable");
                                            fragmentTransaction.addToBackStack("Final Conf Cable");
                                            ((FMobActivity) getActivity())
                                                    .setActionBarTitle("Final Conf Cable");
                                            fragmentTransaction.commit();
                                            ClearPin();
                                            }  else {
                                                Toast.makeText(
                                                        getActivity(),
                                                        "The amount set is higher than your agent balance",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        } else {
                                            Toast.makeText(
                                                    getActivity(), "Please ensure a valid customer has been chosen for the selected biller",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    } else {
                                        Toast.makeText(
                                                getActivity(),
                                                "Please enter a valid value for Agent PIN",
                                                Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(
                                            getActivity(),
                                            "Please enter a valid value for Depositor Number",
                                            Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(
                                        getActivity(),
                                        "Please enter a valid value for Depositor Name",
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
                            "Please enter a value for Customer ID",
                            Toast.LENGTH_LONG).show();
                }
            }
        }

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
        if (view.getId() == R.id.tv) {
            Bundle b = new Bundle();
            b.putString("serviceid", serviceid);
            b.putString("servicename", servicename);


            Fragment fragment = new SpecBillMenu();
            String title = servicename;


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
        if (view.getId() == R.id.tv3) {
            Bundle b = new Bundle();
            b.putString("serviceid", serviceid);
            b.putString("servicename", servicename);
            b.putString("billid", billid);
            b.putString("billname", billname);
            b.putString("label", strlabl);
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


    @Override
    public void onResume() {
        super.onResume();
        // put your code here...

    }

    public void ClearPin() {
        etpin.setText("");
    }

    private void getFeeSec() {
        if (!(prgDialog2 == null)) {
            prgDialog2.show();
        }
        String endpoint = "fee/getfee.action";


        String usid = Utility.gettUtilUserId(getActivity());
        String agentid = Utility.gettUtilAgentId(getActivity());

        String params = CHANNEL_ID + usid + "/" + agentid + "/BILLPAYMENT/" + amou;
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
                    respfee = obj.optString("fee");
                    agbalance = obj.optString("data");
                    if(Utility.isNotNull(agbalance)) {
                        acbal.setText(agbalance+ApplicationConstants.KEY_NAIRA);
                    }


                    //session.setString(SecurityLayer.KEY_APP_ID,appid);


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
                            Bundle b = new Bundle();
                            b.putString("serviceid", serviceid);
                            b.putString("servicename", servicename);
                            b.putString("billid", billid);
                            b.putString("billname", billname);
                            b.putString("label", strlabl);

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
                            Toast.makeText(
                                    getActivity(),
                                    "Please ensure amount set is below the set limit",
                                    Toast.LENGTH_LONG).show();

                        } else {
                            //   btnsub.setVisibility(View.GONE);
                            Toast.makeText(
                                    getActivity(),
                                    responsemessage,
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        txtfee.setText("N/A");
                    }


                } catch (JSONException e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());

                    // TODO Auto-generated catch block
                    if(!(getActivity() == null)) {
                        Toast.makeText(getActivity(), getActivity().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                        ((FMobActivity) getActivity()).SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getActivity());
                    }
                    // SecurityLayer.Log(e.toString());

                } catch (Exception e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());

                    if(!(getActivity() == null)) {
                        ((FMobActivity) getActivity()).SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getActivity());
                    }
                    // SecurityLayer.Log(e.toString());
                }
                //    prgDialog2.dismiss();
                try {
                    if ((prgDialog2 != null) && prgDialog2.isShowing() && !(getActivity() == null)) {
                        prgDialog2.dismiss();
                    }
                } catch (final IllegalArgumentException e) {
                    // Handle or log or ignore
                } catch (final Exception e) {
                    // Handle or log or ignore
                } finally {

                }
                validatecust();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                SecurityLayer.Log("Throwable error", t.toString());
                if (t instanceof SocketTimeoutException) {

                    SecurityLayer.Log("socket timeout error", t.toString());
                }

                // prgDialog2.dismiss();
                try {
                    if ((prgDialog2 != null) && prgDialog2.isShowing() && !(getActivity() == null)) {
                        prgDialog2.dismiss();
                    }
                } catch (final IllegalArgumentException e) {
                    // Handle or log or ignore
                } catch (final Exception e) {
                    // Handle or log or ignore
                } finally {

                }
               // validatecust();
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

    private void validatecust() {
        if (!(prgDialog2 == null)) {
            prgDialog2.show();
        }
        //{channel}/{userId}/{merchantId}/{mobileNumber}/{billerId}/{customerId}/{pamentCode}
        String endpoint = "billpayment/validateCustomer.action";


        String usid = Utility.gettUtilUserId(getActivity());
        String agentid = Utility.gettUtilAgentId(getActivity());
        String mobnoo = Utility.gettUtilMobno(getActivity());

        if (paymentCode == null || paymentCode.equals("")) {
            paymentCode = billid + "01";
        }
        if(paymentCode == null ||paymentCode.equals("null")){
            paymentCode = "0";
        }
        String params = CHANNEL_ID + usid + "/" + agentid + "/" + mobnoo + "/" + billid + "/" + txtcustid + "/" + paymentCode;
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
                    JSONObject respfee = obj.optJSONObject("data");


                    //session.setString(SecurityLayer.KEY_APP_ID,appid);


                    if (!(response.body() == null)) {
                        if (respcode.equals("00")) {

                            SecurityLayer.Log("Response Message", responsemessage);

//                                    SecurityLayer.Log("Respnse getResults",datas.toString());
                            if (respfee == null || respfee.equals("")) {


                            } else {
                                bs = respfee.optString("FullName");
                              /*  Toast.makeText(
                                        getActivity(),
                                        bs,
                                        Toast.LENGTH_LONG).show();*/
                                stt.setText(bs);
                            }
                            chkfee = true;
                        } else if (respcode.equals("93")) {
                            Toast.makeText(
                                    getActivity(),
                                    responsemessage,
                                    Toast.LENGTH_LONG).show();
                            Bundle b = new Bundle();
                            b.putString("serviceid", serviceid);
                            b.putString("servicename", servicename);
                            b.putString("billid", billid);
                            b.putString("billname", billname);
                            b.putString("label", strlabl);
                            b.putString("fullname", strlabl);
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
                            Toast.makeText(
                                    getActivity(),
                                    obj.optString("responsemessage"),
                                    Toast.LENGTH_LONG).show();

                        } else {
                            //  btnsub.setVisibility(View.GONE);
                            Toast.makeText(
                                    getActivity(),
                                    responsemessage,
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        txtfee.setText("N/A");
                    }


                } catch (JSONException e) {

                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    // TODO Auto-generated catch block
                    if(!(getActivity() == null)) {
                        Toast.makeText(getActivity(), getActivity().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                        // SecurityLayer.Log(e.toString());
                        ((FMobActivity) getActivity()).SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getActivity());
                    }

                } catch (Exception e) {

                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    if(!(getActivity() == null)) {
                        ((FMobActivity) getActivity()).SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getActivity());
                    }
                    // SecurityLayer.Log(e.toString());
                }
                try {
                    if ((prgDialog2 != null) && prgDialog2.isShowing() && !(getActivity() == null)) {
                        prgDialog2.dismiss();
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
                SecurityLayer.Log("Throwable error", t.toString());


                try {
                    if ((prgDialog2 != null) && prgDialog2.isShowing() && !(getActivity() == null)) {
                        prgDialog2.dismiss();
                    }
                } catch (final IllegalArgumentException e) {
                    // Handle or log or ignore
                } catch (final Exception e) {
                    // Handle or log or ignore
                } finally {

                } if(!(getActivity() == null)) {
                    Toast.makeText(
                            getActivity(),
                            "There was an error processing your request",
                            Toast.LENGTH_LONG).show();
                    ((FMobActivity) getActivity()).SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getActivity());
                }
            }
        });

    }

    private void PayBillResp(String params) {

        String endpoint = "billpayment/dobillpayment.action";


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


                    JSONObject datas = obj.optJSONObject("data");
                    //session.setString(SecurityLayer.KEY_APP_ID,appid);

                    if (!(response.body() == null)) {
                        String respcode = obj.optString("responseCode");
                        String responsemessage = obj.optString("message");
                        String agcmsn = obj.optString("fee");
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
                                    String totfee = "0.00";
                                    String tref = "N/A";
                                    if (!(datas == null)) {


                                        String ttf = datas.optString("fee");
                                        if (ttf == null || ttf.equals("")) {

                                        } else {
                                            totfee = ttf;
                                        }


                                        tref = datas.optString("refNumber");
                                    }
                                    Bundle b = new Bundle();
                                    b.putString("custid", txtcustid);
                                    b.putString("amou", amou);
                                    b.putString("narra", narra);
                                    b.putString("billname", billname);
                                    b.putString("ednamee", ednamee);
                                    b.putString("ednumbb", ednumbb);
                                    b.putString("billid", billid);
                                    b.putString("serviceid", serviceid);
                                    b.putString("label", strlabl);
                                    b.putString("fullname", bs);
                                    String refcodee = datas.optString("refNumber");
                                    b.putString("refcode", refcodee);
                                    b.putString("tref", tref);
                                    b.putString("agcmsn", agcmsn);
                                    b.putString("fee", respfee);
                                    Fragment fragment = new FinalConfirmCableTV();

                                    fragment.setArguments(b);
                                    FragmentManager fragmentManager = getFragmentManager();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    //  String tag = Integer.toString(title);
                                    fragmentTransaction.replace(R.id.container_body, fragment, "Final Conf Cable");
                                    fragmentTransaction.addToBackStack("Final Conf Cable");
                                    ((FMobActivity) getActivity())
                                            .setActionBarTitle("Final Conf Cable");
                                    fragmentTransaction.commit();
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
                    if(!(getActivity() == null)) {
                        Toast.makeText(getActivity(), getActivity().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                        // SecurityLayer.Log(e.toString());

                        ((FMobActivity) getActivity()).SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getActivity());
                    }

                } catch (Exception e) {

                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    if(!(getActivity() == null)) {
                        ((FMobActivity) getActivity()).SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getActivity());
                    }
                    // SecurityLayer.Log(e.toString());
                }
                if ((prgDialog2 != null) && prgDialog2.isShowing() && !(getActivity() == null)) {
                    prgDialog2.dismiss();
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




                if ((prgDialog2 != null) && prgDialog2.isShowing() && !(getActivity() == null)) {
                    prgDialog2.dismiss();
                }
                if(!(getActivity() == null)) {
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
