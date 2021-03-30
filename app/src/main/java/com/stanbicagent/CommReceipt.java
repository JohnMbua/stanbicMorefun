package com.stanbicagent;

import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stanbicagent.R;


public class CommReceipt extends DialogFragment  {
    public TextView txtname;
    public TextView txtmobno;
    public TextView txtamo;
    public TextView refnumber;
    public TextView statuss,txtto;
    public TextView txservtype;
    LinearLayout lystatus;
    SessionManagement session;
    String serv;
    public CommReceipt() {
        // Required empty public constructor
    }
 /*   @Override
    public void onStart()
    {
        super.onStart();

        // safety check
        if (getDialog() == null)
            return;

        int dialogWidth = 900; // specify a value here
        int dialogHeight = 1200; // specify a value here

        getDialog().getWindow().setLayout(dialogWidth, dialogHeight);

        // ... other stuff you want to do in your onStart() method
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.commreceipt, container, false);
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
          /*  getDialog().getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);*/
         //   getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        Rect displayRectangle = new Rect();
        Window window = getDialog().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

     /*   view.setMinimumWidth((int)(displayRectangle.width() * 0.9f));
        view.setMinimumHeight((int)(displayRectangle.height() * 0.9f));*/
        session = new SessionManagement(getActivity());

        lystatus = (LinearLayout) view.findViewById(R.id.lystatus);
      txtname = (TextView) view.findViewById(R.id.txt);
        txtto = (TextView) view.findViewById(R.id.txtto);
       txtmobno = (TextView) view.findViewById(R.id.txt2);
       txtamo = (TextView) view.findViewById(R.id.tamo);
     refnumber = (TextView) view.findViewById(R.id.txtt14);
        statuss = (TextView) view.findViewById(R.id.status);
        txservtype = (TextView) view.findViewById(R.id.txt9);
        Bundle bundle = getArguments();
        String recamo = bundle.getString("amo","");
        String recnarr = bundle.getString("narr","");
        String recnarrtor = bundle.getString("narrtor","");
        String recdatetime = bundle.getString("datetime","");
        String recservtime = bundle.getString("servtype","");
        String recrefno = bundle.getString("refno","");
        String recstatus = bundle.getString("status","");
        txtamo.setText(recamo);
        refnumber.setText(recrefno);
        txtname.setText(recnarr);
        txtmobno.setText(recdatetime);
        txservtype.setText(recservtime);
        txtto.setText(recnarrtor);


        if(recstatus.equals("FAILURE")){
            lystatus.setVisibility(View.VISIBLE);
            statuss.setTextColor(getActivity().getResources().getColor(R.color.fab_material_red_900));
            statuss.setText(recstatus);
        }
        if(recstatus.equals("SUCCESS")){
            lystatus.setVisibility(View.VISIBLE);
            statuss.setTextColor(getActivity().getResources().getColor(R.color.fab_material_light_green_900));
            statuss.setText(recstatus);
        }


        return view;
    }


}
