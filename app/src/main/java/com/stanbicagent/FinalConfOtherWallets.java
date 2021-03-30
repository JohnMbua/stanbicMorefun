package com.stanbicagent;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.stanbicagent.R;

public class FinalConfOtherWallets extends Fragment  implements View.OnClickListener{
    TextView recacno,recname,recamo,recnarr,recsendnum,recsendnam,recbnknm,recfee,recagcmn,txtrfcd;
    Button btnsub;
    String recanno, amou ,narra, ednamee,ednumbb,txtname,bankname,bankcode,strfee,stragcms;
    ProgressDialog prgDialog,prgDialog2;
    EditText etpin;
    public FinalConfOtherWallets() {
        // Required empty public constructor
    }
  /*  private static Fragment newInstance(Context context) {
        LayoutOne f = new LayoutOne();

        return f;
    }
*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.finalconfotherwallets, null);

        recacno = (TextView) root.findViewById(R.id.textViewnb2);
        txtrfcd = (TextView) root.findViewById(R.id.txtrfcd);
        recname = (TextView) root.findViewById(R.id.textViewcvv);
        recbnknm = (TextView) root.findViewById(R.id.textViewweryu);
        recamo = (TextView) root.findViewById(R.id.textViewrrs);
        recnarr = (TextView) root.findViewById(R.id.textViewrr);
        etpin = (EditText) root.findViewById(R.id.pin);
        recfee = (TextView) root.findViewById(R.id.txtfee);
        recsendnam = (TextView) root.findViewById(R.id.sendnammm);
        recsendnum = (TextView) root.findViewById(R.id.sendno);
        recagcmn = (TextView) root.findViewById(R.id.txtaccom);
        prgDialog2 = new ProgressDialog(getActivity());
        prgDialog2.setMessage("Loading....");
        prgDialog2.setCancelable(false);

        btnsub = (Button) root.findViewById(R.id.button2);
        btnsub.setOnClickListener(this);

        Bundle bundle = this.getArguments();
        if (bundle != null) {

            recanno = bundle.getString("walphno");
            amou = bundle.getString("amou");
            narra = bundle.getString("narra");
            ednamee = bundle.getString("ednamee");
            ednumbb = bundle.getString("ednumbb");
            txtname = bundle.getString("txtname");
            bankname = bundle.getString("walletname");
            bankcode = bundle.getString("walletcode");
            strfee = bundle.getString("fee");
            String   txtrfc = bundle.getString("refcode");
            txtrfcd.setText(txtrfc);
            stragcms = Utility.returnNumberFormat(bundle.getString("agcmsn"));
            recacno.setText(recanno);
            recname.setText(txtname);
            recbnknm.setText(bankname);
            recamo.setText(ApplicationConstants.KEY_NAIRA+amou);
            recnarr.setText(narra);
            recfee.setText(ApplicationConstants.KEY_NAIRA+strfee);
            recsendnam.setText(ednamee);
            recsendnum.setText(ednumbb);
            recagcmn.setText(ApplicationConstants.KEY_NAIRA+stragcms);
        }
        return root;
    }



    public void StartChartAct(int i){


    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.button2) {
            getActivity().finish();
            startActivity(new Intent(getActivity(), FMobActivity.class));
        }
    }


    @Override
    public void onResume(){
        super.onResume();
        // put your code here...

    }

}
