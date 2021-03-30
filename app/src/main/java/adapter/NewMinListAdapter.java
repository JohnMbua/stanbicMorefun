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
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import model.MinistatData;
import com.stanbicagent.R;
import com.stanbicagent.ApplicationConstants;

public class NewMinListAdapter extends ArrayAdapter<MinistatData> implements Filterable {
    private List<MinistatData> planetList;
    private Context context;
    private Filter planetFilter;
    private List<MinistatData> origPlanetList;

    public NewMinListAdapter(List<MinistatData> planetList, Context ctx) {
        super(ctx, R.layout.minstmt_xml, planetList);
        this.planetList = planetList;
        this.context = ctx;
        this.origPlanetList = planetList;
    }

    public int getCount() {
        return planetList.size();
    }

    public MinistatData getItem(int position) {
        return planetList.get(position);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        PlanetHolder holder = new PlanetHolder();
        if (convertView == null) {
            // This a new view we inflate the new layout
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.minstmt_xml, null);
            // Now we can fill the layout with the right values
            TextView accid = (TextView) v.findViewById(R.id.txt);
            TextView curr = (TextView) v.findViewById(R.id.txt2);
            TextView amo = (TextView) v.findViewById(R.id.tamo);
            TextView txttim = (TextView) v.findViewById(R.id.txttime);
            TextView txtdyy = (TextView) v.findViewById(R.id.dayy);
            TextView txtmnthh = (TextView) v.findViewById(R.id.month);
            ImageView ivup = (ImageView) v.findViewById(R.id.uparrow);
            holder.txtname = accid;
            holder.txtmobno = curr;
            holder.txtamo = amo;
            holder.txtime = txttim;
            holder.txtdayy = txtdyy;
            holder.txtmnth = txtmnthh;
            holder.ivup = ivup;
            v.setTag(holder);
        }
        else
            holder = (PlanetHolder) v.getTag();

        MinistatData p = planetList.get(position);
        holder.txtname.setText(p.getTranRemark());
        holder.txtmobno.setText(p.getTranDate());
        // String timee = getTime(p.getTranDate());
        holder.txtime.setText("");
        String dyy = "";

        String month = "";

        month = getMonth(p.getTranDate());
        dyy = getDay(p.getTranDate());

        holder.txtdayy.setText(dyy);
		/*SecurityLayer.Log("Month",p.getTranDate());
		SecurityLayer.Log("Month",month);
		SecurityLayer.Log("Day",dyy);*/
        holder.txtmnth.setText(month);
        String amo = p.getTranAmount();

        holder.txtamo.setText(ApplicationConstants.KEY_NAIRA+amo);


        String creddeb = p.getCredDeb();
        if(creddeb.equals("Debit")){
            holder.ivup.setBackgroundResource(R.drawable.ic_down);
            holder.txtamo.setTextColor(context.getResources().getColor(R.color.fab_material_red_900));
        }

        if(creddeb.equals("Credit")){
            holder.ivup.setBackgroundResource(R.drawable.ic_uparrow);
            holder.txtamo.setTextColor(context.getResources().getColor(R.color.fab_material_green_900));
        }






        return v;
    }
    public static String getTime(String date){
        String fdate = date;
        SimpleDateFormat dt = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        Date datee = null;
        try {
            datee = dt.parse(date);
            SimpleDateFormat dt1 = new SimpleDateFormat("HH:mm");

            fdate = dt1.format(datee);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // *** same for the format String below
        return fdate;
    }

    public static String getDay(String date){
        String fdate = date;
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
        Date datee = null;
        try {
            datee = dt.parse(date);
            SimpleDateFormat dt1 = new SimpleDateFormat("dd");

            fdate = dt1.format(datee);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // *** same for the format String below
        return fdate;
    }
    public static String getMonth(String date){
        String fdate = date;
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
        Date datee = null;
        try {
            datee = dt.parse(date);
            SimpleDateFormat dt1 = new SimpleDateFormat("MMM");

            fdate = dt1.format(datee);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // *** same for the format String below
        return fdate;
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
        public TextView txtime;
        public TextView txtdayy;
        public TextView txtmnth;
        public ImageView ivup;

    }





}
