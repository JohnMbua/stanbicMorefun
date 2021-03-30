package com.stanbicagent;

import android.graphics.Typeface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.stanbicagent.R;


public class RingLay extends Fragment implements View.OnClickListener {
ImageView imageView1;
    RelativeLayout rl;
    TextView txphn;

    public RingLay() {
        // Required empty public constructor
    }
  /*  private static Fragment newInstance(Context context) {
        LayoutOne f = new LayoutOne();

        return f;
    }
*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.layout_ring, null);
        rl = (RelativeLayout) root.findViewById(R.id.phn);

        txphn = (TextView) root.findViewById(R.id.tvvb);
        Typeface font1 = Typeface.createFromAsset(getActivity().getAssets(), "musleo.ttf");
        txphn.setTypeface(font1);
        rl.setOnClickListener(this);

        return root;
    }



    public void StartChartAct(int i){


    }
    @Override
    public void onResume(){
        super.onResume();
        // put your code here...

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.phn) {

        }
    }
}
