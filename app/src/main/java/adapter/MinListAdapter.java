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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.stanbicagent.R;

import java.util.List;




public class MinListAdapter extends ArrayAdapter<MinstList> implements Filterable {

	private List<MinstList> planetList;
	private Context context;
	private Filter planetFilter;
	private List<MinstList> origPlanetList;

	public MinListAdapter(List<MinstList> planetList, Context ctx) {
		super(ctx, R.layout.min_xml, planetList);
		this.planetList = planetList;
		this.context = ctx;
		this.origPlanetList = planetList;
	}
	
	public int getCount() {
		return planetList.size();
	}

	public MinstList getItem(int position) {
		return planetList.get(position);
	}



	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		
		PlanetHolder holder = new PlanetHolder();
		
		// First let's verify the convertView is not null
		if (convertView == null) {
			// This a new view we inflate the new layout
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.min_xml, null);
			// Now we can fill the layout with the right values
			TextView accid = (TextView) v.findViewById(R.id.txt);
            TextView curr = (TextView) v.findViewById(R.id.txt2);
            TextView amo = (TextView) v.findViewById(R.id.tamo);



			

			
			holder.txtname = accid;
            holder.txtmobno = curr;
            holder.txtamo = amo;


		
			
			v.setTag(holder);
		}
		else 
			holder = (PlanetHolder) v.getTag();
		
		MinstList p = planetList.get(position);
		holder.txtname.setText(p.getBenName());
        holder.txtmobno.setText(p.getBenmob());
        holder.txtamo.setText(p.getMinDate());



		
		
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
		public TextView txtmobno;
        public TextView txtamo;

	}
	

	


}
