/*
 * Copyright (C) 2012 jfrankie (http://www.survivingwithandroid.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package adapter;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.stanbicagent.ApplicationConstants;
import com.stanbicagent.FMobActivity;
import com.stanbicagent.LogComplaint;
import com.stanbicagent.R;
import com.stanbicagent.Utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import model.GetCommPerfData;



public class InboxListAdapter extends ArrayAdapter<GetCommPerfData> implements Filterable {

	private ArrayList<GetCommPerfData> planetList;
	private Context context;
	CustomFilter filter;
	private ArrayList<GetCommPerfData> origPlanetList;

	public InboxListAdapter(ArrayList<GetCommPerfData> planetLista, Context ctx) {
		super(ctx, R.layout.inbox_list, planetLista);
		this.planetList = planetLista;
		this.context = ctx;
		this.origPlanetList = planetLista;
	}

	public int getCount() {
		int cnt = 0;
		if(!(planetList == null)){
			cnt =  planetList.size();
		}
		return cnt;
	}

	public GetCommPerfData getItem(int position) {
		return planetList.get(position);
	}


	public View getView(final int position, View convertView, final ViewGroup parent) {
		View v = convertView;

		PlanetHolder holder = new PlanetHolder();

		// First let's verify the convertView is not null
		if (convertView == null) {
			// This a new view we inflate the new layout
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.inbox_list, null);
			// Now we can fill the layout with the right values
			TextView accid = (TextView) v.findViewById(R.id.txt);
			TextView txttoo = (TextView) v.findViewById(R.id.txtto);
			TextView curr = (TextView) v.findViewById(R.id.txt2);
			TextView taxref = (TextView) v.findViewById(R.id.txtref);
			TextView txtstt = (TextView) v.findViewById(R.id.txtstatus);
			Button btnrec = (Button) v.findViewById(R.id.btnrec);
			Button olbtnshare = (Button) v.findViewById(R.id.btnshr);
			Button olbmnshare = (Button) v.findViewById(R.id.btncontext);

			holder.txtname = accid;
			holder.txtmobno = curr;
			holder.txtstatus = txtstt;
			holder.txttref = taxref;
			holder.txtto = txttoo;
			holder.menubutton = olbmnshare;
			holder.btnreceipt = btnrec;
			holder.btnshare = olbtnshare;

			v.setTag(holder);
		}
		else
			holder = (PlanetHolder) v.getTag();

		GetCommPerfData p = planetList.get(position);
		final String tdate = p.getTxndateTime();
		final String amo = Utility.returnNumberFormat(p.getAmount());
		final String toAcnum = p.gettoAcNum();
		final String toref = p.getrefNumber();
		String statss = p.getStatus();
		String terminal = p.getterminal();

		// String convd = getDateTimeStamp(tdate);
		holder.txtname.setText(Utility.convertTxnCodetoServ(p.getTxnCode())+" transaction of "+ ApplicationConstants.KEY_NAIRA+amo);
		holder.txtto.setText(" \nCustomer account:  "+toAcnum);
		holder.txtmobno.setText(tdate);
		holder.txttref.setText("Ref Number:"+toref);
		if(statss.equals("FAILURE")){
			holder.txtstatus.setTextColor(context.getResources().getColor(R.color.fab_material_red_900));
		}
		if(statss.equals("SUCCESS")){
			holder.txtstatus.setTextColor(context.getResources().getColor(R.color.fab_material_light_green_900));
		}
		holder.txtstatus.setText("Status: "+statss);


		holder.btnreceipt.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//	((ListView) parent).performItemClick(v, position, 0); // Let the event be handled in onItemClick()
				Bundle b  = new Bundle();
				String txamo = amo;
				Log.v("Inbox inside click","");
				b.putString("txamo",txamo);
				b.putString("txaco",toAcnum);
				b.putString("txref",toref);
				b.putString("txdate",tdate);
				String title = "Txn Status";
				Fragment fragment = new LogComplaint();

				fragment.setArguments(b);
				android.app.FragmentManager fragmentManager = ((FMobActivity)context).getFragmentManager();
				android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
				//  String tag = Integer.toString(title);
				fragmentTransaction.replace(R.id.container_body, fragment,title);
				fragmentTransaction.addToBackStack(title);

				fragmentTransaction.commit();

			}
		});
		holder.btnshare.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {


			}
		});

		holder.menubutton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//context menu to be called here
				parent.showContextMenuForChild(v);
			}
		});

		return v;
	}

	public void resetData() {
		planetList = origPlanetList;
	}


	/* *********************************
	 * We use the holder pattern
	 * It makes the view faster and avoid finding the component
	 * **********************************/

	private static class PlanetHolder {
		public TextView txtname;
		public TextView txtto;
		public TextView txtmobno;
		public TextView txttref;
		public TextView txtstatus;
		public Button btnreceipt;
		public Button btnshare;
		public Button menubutton;
	}
	public static String getDateTimeStamp(String tdate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		SimpleDateFormat sdf2 = new SimpleDateFormat("dd MMM yyy HH:mm");
		Date convertedCurrentDate = null;

		try {
			convertedCurrentDate = sdf.parse(tdate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String strDate = sdf2.format(convertedCurrentDate);

		return strDate;
	}




	// Filter Class
	public  void filter(String charText) {
		charText = charText.toLowerCase(Locale.getDefault());
		planetList.clear();
		if (charText.length() == 0) {
			planetList.addAll(origPlanetList);

		} else {
			for (GetCommPerfData wp : origPlanetList) {
				if (wp.getrefNumber().toLowerCase(Locale.getDefault())
						.contains(charText)) {
					planetList.add(wp);
				}
			}
		}
		notifyDataSetChanged();
	}

	@Override
	public Filter getFilter() {
		// TODO Auto-generated method stub
		if(filter == null)
		{
			filter=new CustomFilter();
		}

		return filter;
	}

	class CustomFilter extends Filter
	{

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			// TODO Auto-generated method stub

			FilterResults results=new FilterResults();
			if (origPlanetList == null) {
				origPlanetList.addAll(planetList); // saves the original data in mOriginalValues
			}
			if(constraint != null && constraint.length()>0)
			{
				//CONSTARINT TO UPPER
				constraint=constraint.toString().toUpperCase();
				Log.v("String constraint", String.valueOf(constraint));
				ArrayList<GetCommPerfData> filters=new ArrayList<GetCommPerfData>();

			/*	//get specific items
				for (GetCommPerfData wp : origPlanetList) {
					if (wp.getrefNumber().toLowerCase(Locale.getDefault())
							.contains(constraint)) {
						Log.v("ref number in",wp.getrefNumber());
						origPlanetList.add(wp);
					}
				}*/
				for(int i=0;i<origPlanetList.size();i++)
				{
					if(origPlanetList.get(i).getrefNumber().toUpperCase().contains(constraint) || origPlanetList.get(i).getterminal().toUpperCase().contains(constraint) || origPlanetList.get(i).gettoAcNum().toUpperCase().contains(constraint))
					{

						filters.add(origPlanetList.get(i));
					}
				}

				results.count=filters.size();
				results.values=filters;

			}else
			{
				results.count=origPlanetList.size();
				results.values=origPlanetList;

			}

			return results;
		}

		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			// TODO Auto-generated method stub

			planetList=(ArrayList<GetCommPerfData>) results.values;
			notifyDataSetChanged();
		}

	}
}
