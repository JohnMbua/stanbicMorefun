package adapter.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.stanbicagent.R;

import java.util.Collections;
import java.util.List;




/**
 * Created by Ravi Tamada on 12-03-2015.
 */
public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.MyViewHolder> {
    List<NavDrawerItem> data = Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;


    public NavigationDrawerAdapter(Context context, List<NavDrawerItem> data) {
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
        NavDrawerItem current = data.get(position);

        holder.icon.setImageResource(current.getIcon());
        holder.title.setText(current.getTitle());
        int datalength = data.size();

        if(current.getTitle().equals("Personalise Menu"))
              {
           // holder.title.setTextColor(context.getResources().getColor(R.color.nbklightblue));
        }
        else if (current.getTitle().equals("Personalise Widgets")){
          //  holder.title.setTextColor(context.getResources().getColor(R.color.nbklightblue));
        }
        else if (current.getTitle().equals("Personalise Top Three")){
           // holder.title.setTextColor(context.getResources().getColor(R.color.nbklightblue));
        }

        if(!(current.getIconPad() == 0)){
            holder.iconpad.setImageResource(current.getIconPad());
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView icon,iconpad;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            iconpad = (ImageView) itemView.findViewById(R.id.iconpad);
        }
    }
}
