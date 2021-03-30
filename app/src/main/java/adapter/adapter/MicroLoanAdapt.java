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
package adapter.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stanbicagent.R;

import java.util.List;

public class MicroLoanAdapt extends RecyclerView.Adapter<MicroLoanAdapt.MyViewHolder> {

	private List<MicroLoanList> planetList;
	private Context context;

    private LayoutInflater inflater;
    public MicroLoanAdapt(Context context, List<MicroLoanList> planetList) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.planetList = planetList;
    }

    public void delete(int position) {
        planetList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.microloan_list, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
       MicroLoanList p = planetList.get(position);
        holder.ldes.setText(p.getLdesc());
        holder.lacno.setText(p.getAcno());
        holder.lbal.setText(p.getLbal());
        holder.instp.setText(p.getIrate());
        holder.prcloan.setText(p.getPrinc());
        holder.disbam.setText(p.getDisburs());
        holder.pen.setText(p.getPen());

    }

    @Override
    public int getItemCount() {
        return planetList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView accid;
      public TextView curr;

      public  TextView lacno,lbal,prcloan,pen,disbam,ldes,instp;

        public MyViewHolder(View v) {
            super(v);
            lacno = (TextView) v.findViewById(R.id.sbpid);
            lbal = (TextView)  v.findViewById(R.id.vv2);
            prcloan = (TextView)  v.findViewById(R.id.vv);
            instp = (TextView)  v.findViewById(R.id.sbpidk);
            pen = (TextView)  v.findViewById(R.id.vvas);

            disbam = (TextView)  v.findViewById(R.id.lsd);
            ldes = (TextView)  v.findViewById(R.id.lodsc);

        }
    }


}
