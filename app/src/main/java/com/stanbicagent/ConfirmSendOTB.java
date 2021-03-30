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

import model.GetFee;
import okhttp3.OkHttpClient;
import rest.ApiClient;
import rest.ApiInterface;
import rest.ApiSecurityClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import security.SecurityLayer;

import static com.stanbicagent.ApplicationConstants.CHANNEL_ID;

public class ConfirmSendOTB extends Fragment  implements View.OnClickListener{
    TextView recacno,recname,recamo,recnarr,recsendnum,recsendnam,recbnknm,txtfee,acbal;
    Button btnsub;
    String recanno, amou ,narra, ednamee,ednumbb,txtname,bankname,bankcode,agbalance;
    ProgressDialog prgDialog,prgDialog2;
    EditText etpin;
    TextView step1,step2;
    public ConfirmSendOTB() {
        // Required empty public constructor
    }
  /*  private static Fragment newInstance(Context context) {
        LayoutOne f = new LayoutOne();

        return f;
    }
*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.confirmsendotb, null);
        recacno = (TextView) root.findViewById(R.id.textViewnb2);
        recname = (TextView) root.findViewById(R.id.textViewcvv);
        acbal = (TextView) root.findViewById(R.id.txtacbal);
        recbnknm = (TextView) root.findViewById(R.id.textViewweryu);
        recamo = (TextView) root.findViewById(R.id.textViewrrs);
        recnarr = (TextView) root.findViewById(R.id.textViewrr);
        etpin = (EditText) root.findViewById(R.id.pin);
        recsendnam = (TextView) root.findViewById(R.id.sendnammm);
        recsendnum = (TextView) root.findViewById(R.id.sendno);
        prgDialog2 = new ProgressDialog(getActivity());
        prgDialog2.setMessage("Loading....");
        prgDialog2.setCancelable(false);
        txtfee = (TextView) root.findViewById(R.id.txtfee);
        step1 = (TextView) root.findViewById(R.id.tv);
        step1.setOnClickListener(this);
        step2 = (TextView) root.findViewById(R.id.tv2);
        step2.setOnClickListener(this);

        btnsub = (Button) root.findViewById(R.id.button2);
        btnsub.setOnClickListener(this);

        Bundle bundle = this.getArguments();
        if (bundle != null) {

            recanno = bundle.getString("recanno");
            amou = bundle.getString("amou");
            narra = bundle.getString("narra");
            ednamee = bundle.getString("ednamee");
            ednumbb = bundle.getString("ednumbb");
            txtname = bundle.getString("txtname");
            bankname = bundle.getString("bankname");
            bankcode = bundle.getString("bankcode");

            recacno.setText(recanno);
            recname.setText(txtname);
            recbnknm.setText(bankname);
            recamo.setText(ApplicationConstants.KEY_NAIRA+amou);
            recnarr.setText(narra);

            recsendnam.setText(ednamee);
            recsendnum.setText(ednumbb);
            amou = Utility.convertProperNumber(amou);
            getFeeSec();
        }
        return root;
    }



    public void StartChartAct(int i){


    }
    public void getFee(){
        if(!(prgDialog2 == null)) {
            prgDialog2.show();
        }
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        String usid = Utility.gettUtilUserId(getActivity());
        String agentid = Utility.gettUtilAgentId(getActivity());
        Call<GetFee> call = apiService.getFee("1", usid, agentid, "FTINTERBANK",  amou);
        call.enqueue(new Callback<GetFee>() {
            @Override
            public void onResponse(Call<GetFee> call, Response<GetFee> response) {
                String responsemessage = response.body().getMessage();
                String respfee = response.body().getFee();
                SecurityLayer.Log("Response Message", responsemessage);
                if(respfee == null || respfee.equals("")){
                    txtfee.setText("N/A");
                }else{
                    respfee = Utility.returnNumberFormat(respfee);
                    txtfee.setText(ApplicationConstants.KEY_NAIRA+respfee);
                }
                if((!(getActivity() == null)) && !(prgDialog2 == null) && prgDialog2.isShowing()) {
                    prgDialog2.dismiss();
                }
            }

            @Override
            public void onFailure(Call<GetFee> call, Throwable t) {
                // Log error here since request failed
                SecurityLayer.Log("Throwable error",t.toString());
                Toast.makeText(
                        getActivity(),
                        "There was an error processing your request",
                        Toast.LENGTH_LONG).show();
                if((!(getActivity() == null)) && !(prgDialog2 == null) && prgDialog2.isShowing()) {
                    prgDialog2.dismiss();
                }
            }
        });
    }
    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.button2) {
            if (Utility.checkInternetConnection(getActivity())) {
                amou = Utility.convertProperNumber(amou);
                String agpin  = etpin.getText().toString();
                if (Utility.isNotNull(recanno)) {
                    if (Utility.isNotNull(amou)) {
                        if (Utility.isNotNull(narra)) {
                            if (Utility.isNotNull(ednamee)) {
                                if (Utility.isNotNull(ednumbb)) {
                                    if (Utility.isNotNull(agpin)) {
                                        double dbamo = Double.parseDouble(amou);
                                        Double dbagbal = Double.parseDouble(agbalance);
                                        if(dbamo <= dbagbal){
                                    String encrypted = null;

                                    OkHttpClient client = new OkHttpClient();

                                        String usid = Utility.gettUtilUserId(getActivity());
                                        String agentid = Utility.gettUtilAgentId(getActivity());
                                        String mobnoo = Utility.gettUtilMobno(getActivity());
                                     //   Call<InterBank> call = apiService.getInterBankResp("1",usid,agentid,"0000","1",amou,bankcode,recanno,txtname,narra,encrypted);
String params = CHANNEL_ID+usid+"/"+agentid+"/"+mobnoo+"/1/"+amou+"/"+bankcode+"/"+recanno+"/"+txtname+"/"+narra;

                                        Bundle b = new Bundle();
                                        b.putString("recanno", recanno);
                                        b.putString("amou", amou);
                                        b.putString("narra", narra);
                                        b.putString("ednamee", ednamee);
                                        b.putString("ednumbb", ednumbb);
                                        b.putString("txtname", txtname);
                                        b.putString("bankname", bankname);
                                        b.putString("bankcode", bankcode);

                                        b.putString("params",params);
                                        b.putString("txpin", encrypted);
                                        b.putString("serv","SENDOTB");
                                        Fragment fragment = new TransactingProcessing();

                                        fragment.setArguments(b);
                                        FragmentManager fragmentManager = getFragmentManager();
                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                        //  String tag = Integer.toString(title);
                                        fragmentTransaction.replace(R.id.container_body, fragment, "Confirm Other Bank");
                                        fragmentTransaction.addToBackStack("Confirm Other Bank");
                                        ((FMobActivity) getActivity())
                                                .setActionBarTitle("Confirm Other Bank");
                                        fragmentTransaction.commit();
                                //  InterBankResp(params);
                                     /*   ApiInterface apiService =
                                            ApiClient.getClient().create(ApiInterface.class);
                                    String usid = Utility.gettUtilUserId(getActivity());
                                    String agentid = Utility.gettUtilAgentId(getActivity());
                                    String mobnoo = Utility.gettUtilMobno(getActivity());
                                    Call<InterBank> call = apiService.getInterBankResp("1",usid,agentid,"0000","1",amou,bankcode,recanno,txtname,narra,encrypted);
                                    //  SecurityLayer.Log("Request",CHANNEL_ID+"CEVA/"+"BATA0000000001"+"9493818389/"+CHANNEL_ID+amou+"/"+bankcode+"/"+recanno+"/"+acname+"/"+narra+"/"+"2016-08-1517:17:41.670");
                                    call.enqueue(new Callback<InterBank>() {
                                        @Override
                                        public void onResponse(Call<InterBank>call, Response<InterBank> response) {
                                       //     SecurityLayer.Log("Response",response.body().toString());
                                            if(!(response.body() == null)) {
                                                String responsemessage = response.body().getMessage();
                                                String respcode = response.body().getRespCode();
                                                String agcmsn = response.body().getFee();
                                                SecurityLayer.Log("Response Message", responsemessage);
                                                InterBankData datas = response.body().getResults();
                                                if (Utility.isNotNull(respcode) && Utility.isNotNull(respcode)) {
                                                    if (!(Utility.checkUserLocked(respcode))) {
                                                    if (respcode.equals("00")) {
                                                        String totfee = "0.00";
                                                        if(!(datas == null)){
                                                            totfee = datas.getfee();
                                                        }
                                                        Bundle b = new Bundle();
                                                        b.putString("recanno", recanno);
                                                        b.putString("amou", amou);
                                                        b.putString("narra", narra);
                                                        b.putString("ednamee", ednamee);
                                                        b.putString("ednumbb", ednumbb);
                                                        b.putString("txtname", txtname);
                                                        b.putString("bankname", bankname);
                                                        b.putString("bankcode", bankcode);
                                                        b.putString("agcmsn",agcmsn);
                                                        b.putString("fee",totfee);
                                                        Fragment fragment = new FinalConfSendOTB();

                                                        fragment.setArguments(b);
                                                        FragmentManager fragmentManager = getFragmentManager();
                                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                                        //  String tag = Integer.toString(title);
                                                        fragmentTransaction.replace(R.id.container_body, fragment, "Confirm Other Bank");
                                                        fragmentTransaction.addToBackStack("Confirm Other Bank");
                                                        ((FMobActivity) getActivity())
                                                                .setActionBarTitle("Confirm Other Bank");
                                                        fragmentTransaction.commit();
                                                    } else {
                                                        Toast.makeText(
                                                                getActivity(),
                                                                " " + responsemessage,
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
                                            }else{
                                                Toast.makeText(
                                                        getActivity(),
                                                        "There was an error on your request",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                            prgDialog2.dismiss();
                                        }

                                        @Override
                                        public void onFailure(Call<InterBank>call, Throwable t) {
                                            // Log error here since request failed  SecurityLayer.Log("throwable error",t.toString());


                                            Toast.makeText(
                                                    getActivity(),
                                                    "There was an error on your request",
                                                    Toast.LENGTH_LONG).show();

                                            prgDialog2.dismiss();
                                        }
                                    });*/
                                                  /*  sDialog.dismissWithAnimation();
                                                }
                                            })
                                            .show();*/
                                        ClearPin();
                                        }  else {
                                            Toast.makeText(
                                                    getActivity(),
                                                    "The amount set is higher than your agent balance",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    }  else {
                                        Toast.makeText(
                                                getActivity(),
                                                "Please enter a valid value for Agent PIN",
                                                Toast.LENGTH_LONG).show();
                                    }
                                }  else {
                                    Toast.makeText(
                                            getActivity(),
                                            "Please enter a valid value for Depositor Number",
                                            Toast.LENGTH_LONG).show();
                                }
                            }else {
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
                            "Please enter a value for Account Number",
                            Toast.LENGTH_LONG).show();
                }
            }
        }

        if (view.getId() == R.id.tv) {
            Fragment  fragment = new SendOTB();
            String title = "Other Bank";


            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment,title);
            fragmentTransaction.addToBackStack(title);
            ((FMobActivity)getActivity())
                    .setActionBarTitle(title);
            fragmentTransaction.commit();
        }
        if (view.getId() == R.id.tv2) {
            Fragment  fragment = new FTMenu();


            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment,"Confirm Transfer");
            fragmentTransaction.addToBackStack("Confirm Transfer");
            ((FMobActivity)getActivity())
                    .setActionBarTitle("Confirm Transfer");
            fragmentTransaction.commit();
        }
    }


    @Override
    public void onResume(){
        super.onResume();
        // put your code here...

    }
    public void ClearPin(){
        etpin.setText("");
    }

    private void getFeeSec() {
        if(!(prgDialog2 == null) ) {
            prgDialog2.show();
        }
        String endpoint= "fee/getfee.action";


        String usid = Utility.gettUtilUserId(getActivity());
        String agentid = Utility.gettUtilAgentId(getActivity());

        String params = CHANNEL_ID+usid+"/"+agentid+"/FTINTERBANK/"+amou;
        String urlparams = "";
        try {
            urlparams = SecurityLayer.genURLCBC(params,endpoint,getActivity());
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
                                    Fragment fragment = new SendOTB();
                                    String title = "Send Other Bank";

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
                    if(!(getActivity() == null)) {
                        Toast.makeText(getActivity(), getActivity().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                        // SecurityLayer.Log(e.toString());
                        ((FMobActivity) getActivity()).SetForceOutDialog(getString(R.string.forceout),getString(R.string.forceouterr),getActivity());

                    }

                } catch (Exception e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    if(!(getActivity() == null)) {
                        ((FMobActivity) getActivity()).SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getActivity());
                    }
                    // SecurityLayer.Log(e.toString());
                }
                if((!(getActivity() == null)) && !(prgDialog2 == null) && prgDialog2.isShowing()) {
                    prgDialog2.dismiss();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                SecurityLayer.Log("Throwable error",t.toString());
                if(!(getActivity() == null)) {
                    Toast.makeText(
                            getActivity(),
                            "There was an error processing your request",
                            Toast.LENGTH_LONG).show();
                    if((!(getActivity() == null)) && !(prgDialog2 == null) && prgDialog2.isShowing()) {
                        prgDialog2.dismiss();
                    }
                    ((FMobActivity) getActivity()).SetForceOutDialog(getString(R.string.forceout),getString(R.string.forceouterr),getActivity());

                }
            }
        });

    }



    private void InterBankResp(String params) {

        String endpoint= "transfer/interbank.action";


        String usid = Utility.gettUtilUserId(getActivity());
        String agentid = Utility.gettUtilAgentId(getActivity());




        String urlparams = "";
        try {
            urlparams = SecurityLayer.genURLCBC(params,endpoint,getActivity());
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
                    // JSON Object


                    SecurityLayer.Log("Inter Bank Resp", response.body());
                    SecurityLayer.Log("response..:", response.body());
                    JSONObject obj = new JSONObject(response.body());
                    //obj = Utility.onresp(obj,getActivity());
                    obj = SecurityLayer.decryptTransaction(obj, getActivity());
                    SecurityLayer.Log("decrypted_response", obj.toString());




                    JSONObject datas = obj.optJSONObject("data");
                    //session.setString(SecurityLayer.KEY_APP_ID,appid);
                    if(!(response.body() == null)) {
                        String respcode = obj.optString("responseCode");
                        String responsemessage = obj.optString("message");
                        String agcmsn = obj.optString("fee");
                        SecurityLayer.Log("Response Message", responsemessage);


                        if (Utility.isNotNull(respcode) && Utility.isNotNull(respcode)) {
                            if (!(Utility.checkUserLocked(respcode))) {
                                if (respcode.equals("00")) {
                                    String totfee = "0.00";
                                    if(!(datas == null)){
                                        totfee = datas.optString("fee");
                                    }
                                    Bundle b = new Bundle();
                                    b.putString("recanno", recanno);
                                    b.putString("amou", amou);
                                    b.putString("narra", narra);
                                    b.putString("ednamee", ednamee);
                                    b.putString("ednumbb", ednumbb);
                                    b.putString("txtname", txtname);
                                    b.putString("bankname", bankname);
                                    b.putString("bankcode", bankcode);
                                    String refcodee = datas.optString("referenceCode");
                                    b.putString("refcode",refcodee);
                                    b.putString("agcmsn",agcmsn);
                                    b.putString("fee",totfee);
                                    Fragment fragment = new FinalConfSendOTB();

                                    fragment.setArguments(b);
                                    FragmentManager fragmentManager = getFragmentManager();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    //  String tag = Integer.toString(title);
                                    fragmentTransaction.replace(R.id.container_body, fragment, "Confirm Other Bank");
                                    fragmentTransaction.addToBackStack("Confirm Other Bank");
                                    ((FMobActivity) getActivity())
                                            .setActionBarTitle("Confirm Other Bank");
                                    fragmentTransaction.commit();
                                } else {
                                    Toast.makeText(
                                            getActivity(),
                                            " " + responsemessage,
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
                    }else{
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
                if((!(getActivity() == null)) && !(prgDialog2 == null) && prgDialog2.isShowing()) {
                    prgDialog2.dismiss();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                SecurityLayer.Log("throwable error",t.toString());





                if((!(getActivity() == null)) && !(prgDialog2 == null) && prgDialog2.isShowing()) {
                    prgDialog2.dismiss();
                    Toast.makeText(
                            getActivity(),
                            "There was an error on your request",
                            Toast.LENGTH_LONG).show();
                    ((FMobActivity) getActivity()).SetForceOutDialog(getString(R.string.forceout),getString(R.string.forceouterr),getActivity());



                }

            }
        });

    }
}
