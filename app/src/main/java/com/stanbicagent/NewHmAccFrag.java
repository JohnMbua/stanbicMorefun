package com.stanbicagent;

import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.cardview.widget.CardView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.stanbicagent.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import adapter.adapter.Dashboard;
import adapter.adapter.DashboardAdapter;
import model.BalInquiryData;
import model.BalanceInquiry;
import model.GetAgentIdData;
import rest.ApiClient;
import rest.ApiInterface;
import rest.ApiSecurityClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import security.SecurityLayer;

import static com.stanbicagent.ApplicationConstants.CHANNEL_ID;


public class NewHmAccFrag extends Fragment implements View.OnClickListener {
    ImageView imageView1;
    ListView lv;
    TextView tv,tvacco,tvcomm;
    List<Dashboard> planetsList = new ArrayList<Dashboard>();
    DashboardAdapter aAdpt;
    Button btn1,btn2,btn3;
    RelativeLayout rlbuttons,rlagbal,rlcomm;
    TextView curbal,lastl,greet,commamo;
    SessionManagement session;
    ProgressDialog prgDialog;
    ProgressBar prgbar;
    ImageView iv;
    ProgressDialog prgDialog2;
    GridView gridView;
    Button btncm,btnag;
    CardView cv3;
    Button mngacc;
//
// ProgressBar pDialog;
    List<GetAgentIdData> plan = new ArrayList<GetAgentIdData>();
    String agid;
    public NewHmAccFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.nwhmaccfrag, null);
        btn1 = (Button) root.findViewById(R.id.btn1);
        curbal = (TextView) root.findViewById(R.id.currentbal);
        tvacco = (TextView) root.findViewById(R.id.hfjdj);
        lastl = (TextView) root.findViewById(R.id.txt2);
        mngacc = (Button) root.findViewById(R.id.signinn);
        mngacc.setOnClickListener(this);
        greet = (TextView) root.findViewById(R.id.greet);
        tvcomm = (TextView) root.findViewById(R.id.comm);
        commamo = (TextView) root.findViewById(R.id.accountbal);
        cv3 = (CardView) root.findViewById(R.id.card_view3);
        btnag = (Button) root.findViewById(R.id.btn2);
        btncm = (Button) root.findViewById(R.id.btn1);
        prgDialog2 = new ProgressDialog(getActivity());
        prgDialog2.setMessage("Loading....");
        gridView = (GridView) root.findViewById(R.id.gridView1);
        prgDialog2.setCancelable(false);
        iv = (ImageView) root.findViewById(R.id.img);
        prgDialog = new ProgressDialog(getActivity());
        prgbar = (ProgressBar) root.findViewById(R.id.prgbar);
        prgbar.getIndeterminateDrawable().setColorFilter(getActivity().getResources().getColor(R.color.nbkyellow), android.graphics.PorterDuff.Mode.MULTIPLY);
        prgDialog.setMessage("Loading....");
        prgDialog.setCancelable(false);
//pDialog = new ProgressBar(getActivity());
        btn2 = (Button) root.findViewById(R.id.btn2);
        btn3 = (Button) root.findViewById(R.id.btn3);

        rlbuttons = (RelativeLayout) root.findViewById(R.id.balances);
        rlagbal = (RelativeLayout) root.findViewById(R.id.rlagentaccount);
        rlcomm = (RelativeLayout) root.findViewById(R.id.rlcommisionaccount);
        rlagbal.setOnClickListener(this);
//        rlcomm.setOnClickListener(this);
        session = new SessionManagement(getActivity());
     //   pDialog.setBackgroundColor(getActivity().getResources().getColor(R.color.nbkyellow));
        boolean  checktpref = session.checkShwBal();
        SecurityLayer.Log("Boolean checkpref", String.valueOf(checktpref));
        if(checktpref == false){
setBalInquSec();
        }else{
cv3.setVisibility(View.GONE);
        }

        String strlastl = Utility.getLastl(getActivity());
        strlastl = Utility.convertDate(strlastl);
        lastl.setText("Last Login: "+strlastl);
        String accnoo = Utility.getAcountno(getActivity());
        tvacco.setText("AGENT ACCOUNT");
        Calendar cal = Calendar.getInstance();
        String time = "";
        int v =  cal.getTime().getHours();
        if(v < 12){
            time = "Morning";

        }
        if(v >= 12 && v < 18){
            time = "Afternoon";


        }
        if(v >= 18 && v <24){
            time = "Evening";

        }
        String custname = Utility.gettUtilCustname(getActivity());
        String grt = "Good "+time+" "+Utility.returnCustname(custname);
        greet.setText(grt.toUpperCase());
        String agentid = Utility.gettUtilAgentId(getActivity());
        if(Utility.isNotNull(agentid)){
            tvcomm.setText("COMMISSION BALANCE");
        }
        PopulateCardLoginGrid();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String p = planetsList.get(i).getName();

                if( p.equals("Transfer")){
                    Fragment  fragment = new FTMenu();
                    String title = "Funds Transfer";
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    //  String tag = Integer.toString(title);
                    fragmentTransaction.replace(R.id.container_body, fragment,title);
                    fragmentTransaction.addToBackStack(title);
                    fragmentTransaction.commit();
                    ((FMobActivity) getActivity())
                            .setActionBarTitle(title);

                }
                if( p.equals("Deposit")){
                    Fragment  fragment = new CashDepo();
                    String title = "Cash Deposit";
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    //  String tag = Integer.toString(title);
                    fragmentTransaction.replace(R.id.container_body, fragment,title);
                    fragmentTransaction.addToBackStack(title);
                    fragmentTransaction.commit();
                    ((FMobActivity) getActivity())
                            .setActionBarTitle(title);

                }
                else if( p.equals("Withdraw")){
                    Fragment  fragment = new Withdraw();
                    String title = "Withdraw";
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    //  String tag = Integer.toString(title);
                    fragmentTransaction.replace(R.id.container_body, fragment,title);
                    fragmentTransaction.addToBackStack(title);
                    fragmentTransaction.commit();
                    ((FMobActivity) getActivity())
                            .setActionBarTitle(title);

                }
                else  if( p.equals("Pay Bill/Merchant") ){
                    Fragment  fragment = new BillMenu();
                    String title = "Pay Bill";
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    //  String tag = Integer.toString(title);
                    fragmentTransaction.replace(R.id.container_body, fragment,title);
                    fragmentTransaction.addToBackStack(title);
                    fragmentTransaction.commit();
                    ((FMobActivity) getActivity())
                            .setActionBarTitle(title);

                }
                else if( p.equals("Buy Airtime")){
                    Fragment  fragment = new AirtimeTransf();
                    String title = "Airtime";
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    //  String tag = Integer.toString(title);
                    fragmentTransaction.replace(R.id.container_body, fragment,title);
                    fragmentTransaction.addToBackStack(title);
                    fragmentTransaction.commit();
                    ((FMobActivity) getActivity())
                            .setActionBarTitle(title);


                }
                else if( p.equals("Manage Account")){
                    Fragment  fragment = new DashboardFrag();
                    String title = "";
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    //  String tag = Integer.toString(title);
                    fragmentTransaction.replace(R.id.container_body, fragment,title);
                    fragmentTransaction.addToBackStack(title);
                    fragmentTransaction.commit();
                    ((FMobActivity) getActivity())
                            .setActionBarTitle(title);


                }
            }
        });
        return root;
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn1) {
            //   SetDialog("Select Bank");

            Fragment  fragment = new CashDepo();
            String title = "Deposit";
            ((FMobActivity)getActivity()).addFragment(fragment,title);

          /*  FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment,"Deposit");

            ((FMobActivity)getActivity())
                    .setActionBarTitle("Deposit");
            fragmentTransaction.commit();*/

        }
        if (view.getId() == R.id.signinn) {
            //   SetDialog("Select Bank");

            Fragment  fragment = new DashboardFrag();
            String title = "Dashboard";
            ((FMobActivity)getActivity()).addFragment(fragment,title);


        }
        if (view.getId() == R.id.btn2) {
            //   SetDialog("Select Bank");

            Fragment  fragment = new FTMenu();
String title = "Transfer";
            ((FMobActivity)getActivity()).addFragment(fragment,title);

         /*   FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment,"Transfer");

            ((FMobActivity)getActivity())
                    .setActionBarTitle("Transfer");
            fragmentTransaction.commit();*/

        }
        if (view.getId() == R.id.btn3) {
            //   SetDialog("Select Bank");

            Fragment  fragment = new Withdraw();
String title = "Withdraw";
            ((FMobActivity)getActivity()).addFragment(fragment,title);
          /*  FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment,"Withdraw");

            ((FMobActivity)getActivity())
                    .setActionBarTitle("Withdraw");
            fragmentTransaction.commit();*/

        }
        if (view.getId() == R.id.rlagentaccount) {
            //   SetDialog("Select Bank");



           /* FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment,"Mini Statement");

            ((FMobActivity)getActivity())
                    .setActionBarTitle("Mini Statement");
            fragmentTransaction.commit();*/

        }
        if (view.getId() == R.id.rlcommisionaccount) {
            //   SetDialog("Select Bank");

         /*   Fragment  fragment = new CommReport();


            String title = "Commissions Report";
            ((FMobActivity)getActivity()).addFragment(fragment,title);*/
          /*  FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment,"Commissions Report");

            ((FMobActivity)getActivity())
                    .setActionBarTitle("Commisions Report");
            fragmentTransaction.commit();*/

        }
    }
    public void StartChartAct(int i){


    }
    @Override
    public void onResume(){
        super.onResume();
        // put your code here...

    }

    public void PopulateCardLoginGrid(){
        planetsList.clear();

       /* planetsList.add(new Dashboard("My Profile",R.drawable.icons40));*/
        planetsList.add(new Dashboard("Deposit",R.drawable.deposit));
        planetsList.add(new Dashboard("Transfer",R.drawable.transfer));
        planetsList.add(new Dashboard("Withdraw",R.drawable.cwithdraw));

        planetsList.add(new Dashboard("Pay Bill/Merchant",R.drawable.paybill));



        planetsList.add(new Dashboard("Open Account",R.drawable.openacc));
        aAdpt = new DashboardAdapter(planetsList, getActivity());

        gridView.setAdapter(aAdpt);
    }
public void setBalInqu(){
    if (Utility.checkInternetConnection(getActivity())) {
        prgDialog.show();
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        String usid = Utility.gettUtilUserId(getActivity());
        String agentid = Utility.gettUtilAgentId(getActivity());
        String mobnoo = Utility.gettUtilMobno(getActivity());
        Call<BalanceInquiry> call = apiService.getBalInq("1", usid, agentid, "0000");
        call.enqueue(new Callback<BalanceInquiry>() {
            @Override
            public void onResponse(Call<BalanceInquiry> call, Response<BalanceInquiry> response) {
                if(!(response.body() == null)) {
                    String responsemessage = response.body().getMessage();
                    BalInquiryData baldata = response.body().getData();
                    SecurityLayer.Log("Response Message", responsemessage);

//                                    SecurityLayer.Log("Respnse getResults",datas.toString());
                    if (!(baldata == null)) {
                        String balamo = baldata.getbalance();
                        String comamo =baldata.getcommision();


                        String fbal = Utility.returnNumberFormat(balamo);
                      //  curbal.setText(Html.fromHtml("&#8358") + " " + fbal);

                        startCountAnimation(0,Double.parseDouble(fbal));

                        String cmbal = Utility.returnNumberFormat(comamo);
                        commamo.setText(Html.fromHtml("&#8358") + " " + cmbal);
                    } else {
                        Toast.makeText(
                                getActivity(),
                                "There was an error retrieving your balance ",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(
                            getActivity(),
                            "There was an error retrieving your balance ",
                            Toast.LENGTH_LONG).show();
                }  try {
                    if ((prgDialog != null) && prgDialog.isShowing()) {
                        prgDialog.dismiss();
                    }
                } catch (final IllegalArgumentException e) {
                    // Handle or log or ignore
                } catch (final Exception e) {
                    // Handle or log or ignore
                } finally {
                    prgDialog = null;
                }

              //  prgDialog.dismiss();
            }

            @Override
            public void onFailure(Call<BalanceInquiry> call, Throwable t) {
                // Log error here since request failed
                SecurityLayer.Log("Throwable error",t.toString());
                Toast.makeText(
                        getActivity(),
                        "There was an error retrieving your balance ",
                        Toast.LENGTH_LONG).show();
                prgDialog.dismiss();
            }
        });
    }
}
    private void dismissProgressDialog() {
        if (prgDialog != null && prgDialog.isShowing()) {
            prgDialog.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        dismissProgressDialog();
        super.onDestroy();
    }
    private void fetchAds() {


        String endpoint= "adverts/ads.action";


        String usid = Utility.gettUtilUserId(getActivity());
        String agentid = Utility.gettUtilAgentId(getActivity());
        String params = CHANNEL_ID+usid+"/"+agentid+"9493818389";
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


                    JSONArray plan = obj.optJSONArray("data");
                    //session.setString(SecurityLayer.KEY_APP_ID,appid);





                    SecurityLayer.Log("Response Message", responsemessage);

                        if (Utility.isNotNull(respcode) && Utility.isNotNull(responsemessage)) {
                            SecurityLayer.Log("Response Message", responsemessage);

                            if (respcode.equals("00")) {
                                JSONObject json_data = null;
                                Picasso.with(getActivity()).load(R.drawable.fbankdebitcard).into(iv);
                                prgbar.setVisibility(View.GONE);


                            } else {

                                Toast.makeText(
                                        getActivity(),
                                        "There was an error on your request",
                                        Toast.LENGTH_LONG).show();


                            }
                        }

                    try {
                        if ((prgDialog2 != null) && prgDialog2.isShowing()) {
                            prgDialog2.dismiss();
                        }
                    } catch (final IllegalArgumentException e) {
                        // Handle or log or ignore
                    } catch (final Exception e) {
                        // Handle or log or ignore
                    } finally {
                        prgDialog2 = null;
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

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                SecurityLayer.Log("Throwable error",t.toString());
                Toast.makeText(
                        getActivity(),
                        "There was an error processing your request",
                        Toast.LENGTH_LONG).show();
                //   pDialog.dismiss();
                prgbar.setVisibility(View.GONE);
                try {
                    if ((prgDialog2 != null) && prgDialog2.isShowing()) {
                        prgDialog2.dismiss();
                    }
                } catch (final IllegalArgumentException e) {
                    // Handle or log or ignore
                } catch (final Exception e) {
                    // Handle or log or ignore
                } finally {
                    prgDialog2 = null;
                }

            }
        });
    }




    private void setBalInquSec() {

prgDialog2.show();
        String endpoint= "core/balenquirey.action";


        String usid = Utility.gettUtilUserId(getActivity());
        String agentid = Utility.gettUtilAgentId(getActivity());
        String params = CHANNEL_ID+usid+"/"+agentid+"9493818389";
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
                               String balamo = plan.optString("balance");
                               String comamo = plan.optString("commision");


                               String fbal = Utility.returnNumberFormat(balamo);
                             btnag.setText(Html.fromHtml("&#8358") + " " + fbal);
                             //   startCountAnimation(0,Double.parseDouble(fbal));
                               String cmbal = Utility.returnNumberFormat(comamo);
                               btncm.setText(Html.fromHtml("&#8358") + " " + cmbal);
                             //  startCountAnimation2(0,Double.parseDouble(cmbal));
                           } else {
                               Toast.makeText(
                                       getActivity(),
                                       "There was an error retrieving your balance ",
                                       Toast.LENGTH_LONG).show();
                           }
                       }else{
                           Toast.makeText(
                                   getActivity(),
                                   "There was an error retrieving your balance ",
                                   Toast.LENGTH_LONG).show();
                       }
                    } else {
                        Toast.makeText(
                                getActivity(),
                                "There was an error retrieving your balance ",
                                Toast.LENGTH_LONG).show();
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
                if(!(getActivity() == null)) {
                    if (Utility.checkInternetConnection(getActivity())) {
                        fetchAds();
                    }
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
                //   pDialog.dismiss();
                prgbar.setVisibility(View.GONE);
                try {
                    if ((prgDialog2 != null) && prgDialog2.isShowing()) {
                        prgDialog2.dismiss();
                    }
                } catch (final IllegalArgumentException e) {
                    // Handle or log or ignore
                } catch (final Exception e) {
                    // Handle or log or ignore
                } finally {
                    prgDialog2 = null;
                }
                if(Utility.checkInternetConnection(getActivity())){
                    fetchAds();
                }
            }
        });

    }


    private void startCountAnimation(double from,double end) {
        ValueAnimator animator = new ValueAnimator();
        animator.setObjectValues(from, end);
        animator.setDuration(3000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                curbal.setText(ApplicationConstants.KEY_NAIRA + (double) animation.getAnimatedValue());
            }
        });
        animator.start();
    }
    private void startCountAnimation2(double from,double end) {
        ValueAnimator animator = new ValueAnimator();
        animator.setObjectValues(from, end);
        animator.setDuration(3000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                commamo.setText(ApplicationConstants.KEY_NAIRA + (double) animation.getAnimatedValue());
            }
        });
        animator.start();
    }
}
