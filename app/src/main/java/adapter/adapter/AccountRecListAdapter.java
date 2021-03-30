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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stanbicagent.R;

import java.util.List;

public class AccountRecListAdapter extends RecyclerView.Adapter<AccountRecListAdapter.MyViewHolder> {

	private List<AccountList> planetList;
	private Context context;

    private LayoutInflater inflater;




    public AccountRecListAdapter(Context context, List<AccountList> planetList) {
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
        View view = inflater.inflate(R.layout.acc_list, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        AccountList p = planetList.get(position);
        holder.accid.setText(p.getAccId());
        holder.accamo.setText(p.getAmo());
        holder.curr.setText(p.getAcctype());


    }

    @Override
    public int getItemCount() {
        return planetList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView accid;
      public TextView curr;
        public TextView accamo;
        LinearLayout pf;

        public MyViewHolder(View v) {
            super(v);
            accid = (TextView) v.findViewById(R.id.bname);
            curr = (TextView) v.findViewById(R.id.curr);
             accamo = (TextView) v.findViewById(R.id.vv);
            pf = (LinearLayout) v.findViewById(R.id.ll);
        }
    }


}
