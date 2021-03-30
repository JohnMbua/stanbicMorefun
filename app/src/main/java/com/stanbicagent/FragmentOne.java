package com.stanbicagent;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.stanbicagent.R;

public class FragmentOne extends Fragment implements View.OnClickListener {
    Button sign_in, sign_up, proceed, link, open_nat;
	ImageView ivIcon;
	TextView tvItemName;



	public FragmentOne() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.main, container,
				false);
      /*  sign_in = (Button) view.findViewById(R.id.sign_in);
        sign_in.setOnClickListener(this);
        sign_up = (Button) view.findViewById(R.id.sign_up);
        sign_up.setOnClickListener(this);
        proceed = (Button) view.findViewById(R.id.proceed);
        proceed.setOnClickListener(this);
        link = (Button) view.findViewById(R.id.link);
        link.setOnClickListener(this);
        open_nat = (Button) view.findViewById(R.id.open_nat);
        open_nat.setOnClickListener(this);
		ivIcon = (ImageView) view.findViewById(R.id.frag1_icon);
		tvItemName = (TextView) view.findViewById(R.id.frag1_text);*/


		return view;
	}
    @Override
    public void onClick(View v) {


    }
}
