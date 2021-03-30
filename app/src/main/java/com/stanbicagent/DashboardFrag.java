package com.stanbicagent;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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

import java.util.ArrayList;
import java.util.List;

import adapter.adapter.Dashboard;
import adapter.adapter.DashboardAdapter;
import model.BalInquiryData;
import model.BalanceInquiry;
import model.GetAgentIdData;
import rest.ApiClient;
import rest.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import security.SecurityLayer;


public class DashboardFrag extends Fragment  {
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
//
// ProgressBar pDialog;
    List<GetAgentIdData> plan = new ArrayList<GetAgentIdData>();
    String agid;
    public DashboardFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.dashboard_frag, null);

        gridView = (GridView) root.findViewById(R.id.gridView1);

       PopulateCardLoginGrid();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String p = planetsList.get(i).getName();

                if( p.equals("My Performance")){
                   /* String title = "My Performance";
                    android.app.Fragment   fragmentt = new SelChart();
                    android.app.FragmentManager fragmentManager = getFragmentManager();
                    android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    //  String tag = Integer.toString(title);
                    fragmentTransaction.replace(R.id.container_body, fragmentt,title);

                    fragmentTransaction.addToBackStack(null);

                    fragmentTransaction.commit();*/




                }
                else if( p.equals("My Profile")){
                    Fragment  fragment = new ChangeACName();
                    String title = "My Profile";
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
        planetsList.add(new Dashboard("My Performance",R.drawable.ft));
        planetsList.add(new Dashboard("My Profile",R.drawable.account));

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
                        curbal.setText(Html.fromHtml("&#8358") + " " + fbal);

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



}
