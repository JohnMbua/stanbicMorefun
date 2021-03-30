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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.stanbicagent.R;
import com.afollestad.materialdialogs.MaterialDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import adapter.adapter.OTBAdapt;
import adapter.adapter.OTBList;
import rest.ApiInterface;
import rest.ApiSecurityClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import security.SecurityLayer;

import static com.stanbicagent.ApplicationConstants.CHANNEL_ID;


public class SendOTB extends Fragment implements View.OnClickListener {
ImageView imageView1;

    EditText amon, edacc,pno,txtamount,txtnarr,edname,ednumber;
    Button btnsub;
    String bankname,bankcode,recanno;
    SessionManagement session;
    ProgressDialog prgDialog,prgDialog2;
    private static int SPLASH_TIME_OUT = 2500;
    String depositid;
    OTBAdapt aAdpt;
    TextView bankchosen,bankselected;
    LinearLayout linearLayoutMine;
    List<OTBList> planetsList = new ArrayList<OTBList>();
    MaterialDialog builder;
    EditText accountoname;
    String acname;
    TextView step2;
    public SendOTB() {
        // Required empty public constructor
    }
  /*  private static Fragment newInstance(Context context) {
        LayoutOne f = new LayoutOne();

        return f;
    }
*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.sendotb, null);
        session = new SessionManagement(getActivity());
        edacc = (EditText) root.findViewById(R.id.input_payacc);
        txtamount = (EditText) root.findViewById(R.id.amount);
        txtnarr = (EditText) root.findViewById(R.id.ednarr);
        edname = (EditText) root.findViewById(R.id.sendname);
        ednumber = (EditText) root.findViewById(R.id.sendnumber);
        prgDialog = new ProgressDialog(getActivity());
        prgDialog.setMessage("Loading Account Details....");
        // Set Cancelable as False

        prgDialog.setCancelable(false);


        prgDialog2 = new ProgressDialog(getActivity());
        prgDialog2.setMessage("Loading....");
        prgDialog2.setCancelable(false);
        bankchosen = (TextView) root.findViewById(R.id.textVip);
        bankselected = (TextView) root.findViewById(R.id.textVipp);
        bankselected.setOnClickListener(this);

        btnsub = (Button) root.findViewById(R.id.button2);
        accountoname = (EditText) root.findViewById(R.id.cname);

        View.OnFocusChangeListener ofcListener = new MyFocusChangeListener();
        txtamount.setOnFocusChangeListener(ofcListener);
        txtnarr.setOnFocusChangeListener(ofcListener);
        edacc.setOnFocusChangeListener(ofcListener);
        edname.setOnFocusChangeListener(ofcListener);
        ednumber.setOnFocusChangeListener(ofcListener);
      btnsub.setOnClickListener(this);

        step2 = (TextView) root.findViewById(R.id.tv2);
        step2.setOnClickListener(this);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            bankname = bundle.getString("bankname");
            bankcode = bundle.getString("bankcode");
            recanno = bundle.getString("recanno");
            edacc.setText(recanno);
            bankselected.setText("Change Bank");


            String usid = Utility.gettUtilUserId(getActivity());
            String agentid = Utility.gettUtilAgentId(getActivity());
            String mobnoo = Utility.gettUtilMobno(getActivity());
            String params = CHANNEL_ID + usid + "/" + agentid + "/" + mobnoo + "/" + bankcode + "/" + recanno;
            NameInquirySec(params);
        }

if(Utility.isNotNull(bankname)){
    bankchosen.setText(bankname);
}

        edacc.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edacc.getText().toString().length() == 10) {
                    if (!(getActivity() == null)) {
                     /*   if (!(bankcode == null)) {*/

                            if (Utility.checkInternetConnection(getActivity())) {
                                Utility.hideKeyboardFrom(getActivity(), edacc);
                                if (!(prgDialog == null)) {
                                    String acno = edacc.getText().toString();
                                  /*  prgDialog.show();

                                    String acno = edacc.getText().toString();
                                    String usid = Utility.gettUtilUserId(getActivity());
                                    String agentid = Utility.gettUtilAgentId(getActivity());
                                    String mobnoo = Utility.gettUtilMobno(getActivity());
                                    String params = CHANNEL_ID + usid + "/" + agentid + "/" + mobnoo + "/" + bankcode + "/" + acno;
                                    NameInquirySec(params);
                                }*/((FMobActivity)getActivity()).showNubanDialog(acno);
                                }
                            }
                      /*  } else {
                            Toast.makeText(
                                    getActivity(),
                                    "Please select a bank ",
                                    Toast.LENGTH_LONG).show();
                        }*/
                        // TODO Auto-generated method stub
                    }
                }
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


        ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(
                getActivity(), R.array.banks, android.R.layout.simple_spinner_item);
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
       // spn2.setAdapter(adapter4);
        return root;
    }


    private class MyFocusChangeListener implements View.OnFocusChangeListener {

        public void onFocusChange(View v, boolean hasFocus){

            if(v.getId() == R.id.amount && !hasFocus) {
                InputMethodManager imm =  (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                String txt = txtamount.getText().toString();
                String fbal = Utility.returnNumberFormat(txt);
                txtamount.setText(fbal);

            }

            if(v.getId() == R.id.ednarr && !hasFocus) {
                InputMethodManager imm =  (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);


            }
            if(v.getId() == R.id.sendname && !hasFocus) {
                InputMethodManager imm =  (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);


            }
            if(v.getId() == R.id.sendnumber && !hasFocus) {
                InputMethodManager imm =  (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);


            }
            if(v.getId() == R.id.input_payacc && !hasFocus) {
                InputMethodManager imm =  (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);


            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button2) {
            if(Utility.isNotNull(bankname)) {
                final String recanno = edacc.getText().toString();
                final String amou = txtamount.getText().toString();
                final String narra = txtnarr.getText().toString();
                String ednamee = edname.getText().toString();
                String ednumbb = ednumber.getText().toString();
                if (Utility.isNotNull(recanno)) {
                    if (Utility.isNotNull(amou)) {
                        String nwamo = amou.replace(",", "");
                        SecurityLayer.Log("New Amount",nwamo);
                        double txamou = Double.parseDouble(nwamo);
                       /* if (txamou >= 100) {*/
                        if (Utility.isNotNull(narra)) {
                            if (Utility.isNotNull(ednamee)) {
                                if (Utility.isNotNull(ednumbb)) {
                                    if (Utility.isNotNull(bankcode)) {
                                        if (Utility.isNotNull(acname)) {

                                    Bundle b  = new Bundle();
                                    b.putString("recanno",recanno);
                                    b.putString("amou",amou);
                                    b.putString("narra",narra);
                                    b.putString("ednamee",ednamee);
                                    b.putString("ednumbb",ednumbb);
                                    b.putString("txtname",acname);
                                    b.putString("bankname",bankname);
                                    b.putString("bankcode",bankcode);
                                    Fragment  fragment = new ConfirmSendOTB();

                                    fragment.setArguments(b);
                                    FragmentManager fragmentManager = getFragmentManager();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    //  String tag = Integer.toString(title);
                                    fragmentTransaction.replace(R.id.container_body, fragment,"Confirm Other Bank");
                                    fragmentTransaction.addToBackStack("Confirm Other Bank");
                                    ((FMobActivity)getActivity())
                                            .setActionBarTitle("Confirm Other Bank");
                                    fragmentTransaction.commit();
                                        }  else {
                                            Toast.makeText(
                                                    getActivity(),
                                                    "Please enter a valid account number",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    } else {
                                        Toast.makeText(
                                                getActivity(),
                                                "Please select a bank",
                                                Toast.LENGTH_LONG).show();
                                    }
            } else {
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
                     /*   } else {
                            Toast.makeText(
                                    getActivity(),
                                    "Please enter a valid amount more than 100 Naira",
                                    Toast.LENGTH_LONG).show();
                        }*/
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



                                else{
                Toast.makeText(getActivity(), "Please select a bank", Toast.LENGTH_LONG).show();

            }

        }
        if (view.getId() == R.id.textVipp) {
         //   SetDialog("Select Bank");

            Fragment  fragment = new OtherBankPage();


            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment,"Select Bank");
            fragmentTransaction.addToBackStack("Select Bank");
            ((FMobActivity)getActivity())
                    .setActionBarTitle("Select Bank");
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
    public void SetDialog(String title){
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View stdView = factory.inflate(R.layout.dialoglistview, null);
        linearLayoutMine  = (LinearLayout) stdView.findViewById(R.id.lay);

     final ListView   lv = (ListView) stdView.findViewById(R.id.lv);

        planetsList.add(new OTBList("Access Bank PLC","057"));
        planetsList.add(new OTBList("Aso Savings","058"));
        planetsList.add(new OTBList("CitiBank Nigeria LTD","059"));
        planetsList.add(new OTBList("Diamond Bank Plc","057"));
        planetsList.add(new OTBList("Ecobank Nigeria Plc","058"));
        planetsList.add(new OTBList("Enterprise Bank","059"));
        planetsList.add(new OTBList("Fidelity Bank Plc","057"));
        planetsList.add(new OTBList("First City Monument Bank Plc","058"));
        planetsList.add(new OTBList("Guaranty Trust Bank Plc","059"));
        planetsList.add(new OTBList("Heritage Bank","057"));
        planetsList.add(new OTBList("JAIZ Bank","058"));
        planetsList.add(new OTBList("Keystone Bank","059"));
        planetsList.add(new OTBList("Mainstreet Bank","059"));
        planetsList.add(new OTBList("Parallex MFB","057"));
        planetsList.add(new OTBList("Skye Bank Plc","058"));
        planetsList.add(new OTBList("StanbicIBTC Bank Plc","059"));

        planetsList.add(new OTBList("Standard Chartered Bank of Nigeria","058"));
        planetsList.add(new OTBList("Sterling Bank Plc","059"));
        planetsList.add(new OTBList("Union Bank of Nigeria Plc","059"));
        planetsList.add(new OTBList("United Bank for Africa Plc","057"));
        planetsList.add(new OTBList("Unity Bank Plc","058"));
        planetsList.add(new OTBList("Wema Bank Plc","059"));
        planetsList.add(new OTBList("Access Bank","059"));
        aAdpt = new OTBAdapt(planetsList, getActivity());
lv.setAdapter(aAdpt);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                String benname = planetsList.get(position).getBenName();
bankchosen.setText(benname);
                builder.dismiss();

            }
        });
      builder =   new MaterialDialog.Builder(getActivity())
                .title(title)

                .customView(linearLayoutMine,true)

                .negativeText("Close")

                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                     /*   Toast.makeText(
                                getActivity(),
                                "You have successfully added an offer",
                                Toast.LENGTH_LONG).show();*/

                    }
                })
                .show();
    }



    public void SetDialog(String msg,String title){
        new MaterialDialog.Builder(getActivity())
                .title(title)
                .content(msg)

                .negativeText("Close")
                .show();
    }

    private void NameInquirySec(String params) {
prgDialog.show();
        String endpoint= "transfer/nameenq.action";


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

                    SecurityLayer.Log("response..:", response.body());
                    JSONObject obj = new JSONObject(response.body());
                    //obj = Utility.onresp(obj,getActivity());
                    obj = SecurityLayer.decryptTransaction(obj, getActivity());
                    SecurityLayer.Log("decrypted_response", obj.toString());

                    String respcode = obj.optString("responseCode");
                    String responsemessage = obj.optString("message");


                    JSONObject plan = obj.optJSONObject("data");
                    //session.setString(SecurityLayer.KEY_APP_ID,appid);


                    if(!(response.body() == null)) {
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

                        }else{
                            if (!(getActivity() == null)) {
                                Toast.makeText(
                                        getActivity(),
                                        responsemessage,
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    } else {
                        if (!(getActivity() == null)) {
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
                        ((FMobActivity) getActivity()).SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getActivity());
                        // SecurityLayer.Log(e.toString());
                    }

                } catch (Exception e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    if (!(getActivity() == null)) {
                        ((FMobActivity) getActivity()).SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getActivity());
                        // SecurityLayer.Log(e.toString());
                    }
                }
                if((!(getActivity() == null)) && !(prgDialog == null) && prgDialog.isShowing()) {
                    prgDialog.dismiss();

                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                SecurityLayer.Log("Throwable error",t.toString());

                if((!(getActivity() == null)) && !(prgDialog == null) && prgDialog.isShowing()) {
                    prgDialog.dismiss();
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
    public void onPause() {
        super.onPause();

        if ((getActivity() != null && prgDialog != null) && prgDialog.isShowing()) {
            prgDialog.dismiss();

        }
    }

}
