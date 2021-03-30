package com.stanbicagent;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.stanbicagent.R;

import java.util.ArrayList;
import java.util.List;

import adapter.adapter.AgentList;
import adapter.adapter.AgentListAdapter;


public class MyAgents extends Fragment {
    ListView lv;
    AgentListAdapter aAdpt;
    String bankname,bankcode;
    ProgressDialog prgDialog;
    List<AgentList> planetsList = new ArrayList<AgentList>();
    public MyAgents() {
        // Required empty public constructor
    }
  /*  private static Fragment newInstance(Context context) {
        LayoutOne f = new LayoutOne();

        return f;
    }
*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.myagents, null);
   lv = (ListView) root.findViewById(R.id.lv);
        prgDialog = new ProgressDialog(getActivity());
        prgDialog.setMessage("Loading....");
        prgDialog.setCancelable(false);

        //SetBanks();

/*        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                String bankname = planetsList.get(position).getBankName();
                String bankcode = planetsList.get(position).getBankCode();
                Bundle b  = new Bundle();
                b.putString("bankname",bankname);
                b.putString("bankcode",bankcode);
                Fragment  fragment = new SendOTB();

                fragment.setArguments(b);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                //  String tag = Integer.toString(title);
                fragmentTransaction.replace(R.id.container_body, fragment,"Other Banks");
                fragmentTransaction.addToBackStack("Other Banks");
                ((FMobActivity)getActivity())
                        .setActionBarTitle("Other Banks");
                fragmentTransaction.commit();

            }
        });*/
        return root;
    }



    public void StartChartAct(int i){


    }
    public void SetAgents(){
     /*   planetsList.add(new AgentList("Ikorodu Shop","0813333333",R.drawable.));
        planetsList.add(new OTBList("Ikeja City Mall ","081222222",R.drawable));
        planetsList.add(new OTBList("Cellulant","057"));
        planetsList.add(new OTBList("eTranzact","058"));
        planetsList.add(new OTBList("EcoMobile","059"));
        planetsList.add(new OTBList("GTMobile","057"));
        planetsList.add(new OTBList("TeasyMobile","058"));
        planetsList.add(new OTBList("VTNetworks","059"));
        planetsList.add(new OTBList("AccessMobile","059"));*/
    }
  /*  public void SetBanks(){

prgDialog.show();

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<GetBanks> call = apiService.getBanks();
        call.enqueue(new Callback<GetBanks>() {
            @Override
            public void onResponse(Call<GetBanks>call, Response<GetBanks> response) {
                String responsemessage = response.body().getMessage();

                SecurityLayer.Log("Response Message",responsemessage);
                planetsList = response.body().getResults();
//                                    SecurityLayer.Log("Respnse getResults",datas.toString());
                if(!(planetsList == null)) {
                    SecurityLayer.Log("Get Banks Data Name",planetsList.get(0).getBankName());
                    Collections.sort(planetsList, new Comparator<GetBanksData>(){
                        public int compare(GetBanksData d1, GetBanksData d2){
                            return d1.getBankName().compareTo(d2.getBankName());
                        }
                    });
                    aAdpt = new OTBRetroAdapt(planetsList, getActivity());
                    lv.setAdapter(aAdpt);
                }
                prgDialog.dismiss();
            }

            @Override
            public void onFailure(Call<GetBanks>call, Throwable t) {
                // Log error here since request failed
                Toast.makeText(
                        getActivity(),
                        "Throwable Error: "+t.toString(),
                        Toast.LENGTH_LONG).show();
                prgDialog.dismiss();
            }
        });



    }*/
    @Override
    public void onResume(){
        super.onResume();
        // put your code here...

    }

}
