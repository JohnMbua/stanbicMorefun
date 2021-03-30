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

import com.stanbicagent.ApplicationConstants;
import com.stanbicagent.R;
import com.stanbicagent.Utility;

import java.util.List;

import model.GetCommPerfData;

public class NewCommListAdapter extends ArrayAdapter<GetCommPerfData> implements Filterable {

    private List<GetCommPerfData> planetList;
    private Context context;
    private Filter planetFilter;
    private List<GetCommPerfData> origPlanetList;

    public NewCommListAdapter(List<GetCommPerfData> planetList, Context ctx) {
        super(ctx, R.layout.min_xml, planetList);
        this.planetList = planetList;
        this.context = ctx;
        this.origPlanetList = planetList;
    }

    public int getCount() {
        return planetList.size();
    }

    public GetCommPerfData getItem(int position) {
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
            TextView refno = (TextView) v.findViewById(R.id.txtt14);


            holder.txtname = accid;
            holder.txtmobno = curr;
            holder.txtamo = amo;
            holder.refnumber = refno;


            v.setTag(holder);
        } else
            holder = (PlanetHolder) v.getTag();

        GetCommPerfData p = planetList.get(position);
        String txncode = p.getTxnCode();
        String toAcnum = p.gettoAcNum();
        String fromacnum = p.getterminal();
        String txtrfno = p.getrefNumber();
        holder.txtname.setText(Utility.convertTxnCodetoServ(txncode) + " \n Customer Account: " + toAcnum);
        holder.refnumber.setText("Ref Number: " + txtrfno);
        holder.txtmobno.setText(p.getTxndateTime());
        double dbagcsmn = p.getAgentCmsn();
        String agcsmn = Double.toString(dbagcsmn);
        String fbal = Utility.returnNumberFormat(agcsmn);
        holder.txtamo.setText(ApplicationConstants.KEY_NAIRA + fbal);


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
        public TextView refnumber;
    }


}



	



