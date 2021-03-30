package adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.stanbicagent.R;

import java.util.List;

import adapter.adapter.NavDrawerItemSignIn;



/**
 * Created by Ravi Tamada on 12-03-2015.
 */
public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.MyViewHolder> {
    List<NavDrawerItemSignIn> data ;
    private LayoutInflater inflater;
    private Context context;


    public NavigationDrawerAdapter(Context context, List<NavDrawerItemSignIn> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;


    }



    public void delete(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.nav_drawer_row, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        NavDrawerItemSignIn current = data.get(position);

        holder.icon.setImageResource(current.getIcon());
        holder.title.setText(current.getTitle());
        int datalength = data.size();


        if(!(current.getIconPad() == 0)){
            holder.iconpad.setImageResource(current.getIconPad());
        }
       /* if(current.getIcon() == 0){
            holder.title.setTextColor(context.getResources().getColor(R.color.fab_material_blue_900));
        }*/

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView icon,iconpad;
        RelativeLayout container;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            iconpad = (ImageView) itemView.findViewById(R.id.iconpad);
            container = (RelativeLayout) itemView.findViewById(R.id.rlContainer);

        }
    }

    class NewViewHolder2 extends RecyclerView.ViewHolder {
       Button btn;


        public NewViewHolder2(View itemView) {
            super(itemView);
           btn = (Button) itemView.findViewById(R.id.button9);

        }
    }
}
