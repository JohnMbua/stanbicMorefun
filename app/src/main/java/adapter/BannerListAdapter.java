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
import android.graphics.Typeface;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.stanbicagent.R;

import java.util.List;




public class BannerListAdapter extends RecyclerView.Adapter<BannerListAdapter.MyViewHolder> {

	private List<BannList> planetList;
	private Context context;

    private LayoutInflater inflater;




    public BannerListAdapter(Context context, List<BannList> planetList) {
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
        View view = inflater.inflate(R.layout.banner_list, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
       BannList p = planetList.get(position);
        holder.subj.setText(p.getSubj());
        holder.bodyy.setText(p.getBodyy());
        Typeface font1 = Typeface.createFromAsset(context.getAssets(), "musleo.ttf");
        holder.subj.setTypeface(font1);
        holder.bodyy.setTypeface(font1);
        if(position == 0){

            holder.cv.setCardBackgroundColor(context.getResources().getColor(R.color.nbklightblue));
        }
        if(position == 1){

            holder.cv.setCardBackgroundColor(context.getResources().getColor(R.color.fab_material_brown_500));
        }
        if(position == 2){
            holder.cv.setCardBackgroundColor(context.getResources().getColor(R.color.fab_material_red_500));

        }

    }

    @Override
    public int getItemCount() {
        return planetList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView subj;
      public TextView bodyy;
        public CardView cv;
       public RelativeLayout rl;


        public MyViewHolder(View v) {
            super(v);
            subj = (TextView) v.findViewById(R.id.tvvb);
            bodyy = (TextView) v.findViewById(R.id.tvsd);
            cv = (CardView) v.findViewById(R.id.card_view6);
            rl = (RelativeLayout) v.findViewById(R.id.phn);

        }
    }


}
