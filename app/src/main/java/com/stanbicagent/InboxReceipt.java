package com.stanbicagent;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.stanbicagent.R;


public class InboxReceipt extends DialogFragment  {
    public TextView txtname;
    public TextView txtmobno;
    public TextView txtamo;
    public TextView refnumber;
    public TextView statuss;
    SessionManagement session;
    String serv;
    public InboxReceipt() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.inboxreceipt, container, false);
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        session = new SessionManagement(getActivity());


      txtname = (TextView) view.findViewById(R.id.txt);
       txtmobno = (TextView) view.findViewById(R.id.txt2);
       txtamo = (TextView) view.findViewById(R.id.tamo);
     refnumber = (TextView) view.findViewById(R.id.txtt14);
        statuss = (TextView) view.findViewById(R.id.status);

        Bundle bundle = getArguments();
        String recamo = bundle.getString("amo","");
        String recnarr = bundle.getString("narr","");
        String recdatetime = bundle.getString("datetime","");
        String recrefno = bundle.getString("refno","");
        String recstatus = bundle.getString("status","");
        txtamo.setText(recamo);
        refnumber.setText(recrefno);
        txtname.setText(recnarr);
        txtmobno.setText(recdatetime);


        if(recstatus.equals("FAILURE")){
            statuss.setTextColor(getActivity().getResources().getColor(R.color.fab_material_red_900));
            statuss.setText("STATUS: "+recstatus);
        }
        if(recstatus.equals("SUCCESS")){
            statuss.setTextColor(getActivity().getResources().getColor(R.color.fab_material_light_green_900));
            statuss.setText("STATUS: "+recstatus);
        }


        return view;
    }


}
