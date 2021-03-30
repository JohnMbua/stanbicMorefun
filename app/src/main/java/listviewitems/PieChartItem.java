
package listviewitems;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.stanbicagent.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;


public class PieChartItem extends ChartItem implements OnChartValueSelectedListener {

    private Typeface mTf;
    Context cont;

    public PieChartItem(ChartData<?> cd, Context c) {
        super(cd);
        this.cont = c;
        mTf = Typeface.createFromAsset(c.getAssets(), "musleo.ttf");
    }

    @Override
    public int getItemType() {
        return TYPE_PIECHART;
    }

    @Override
    public View getView(int position, View convertView, Context c) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();

            convertView = LayoutInflater.from(c).inflate(
                    R.layout.list_item_piechart, null);
            holder.chart = (PieChart) convertView.findViewById(R.id.chart);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // apply styling
        holder.chart.getLegend().setEnabled(false);

        holder.chart.setHoleRadius(52f);
        holder.chart.setTransparentCircleRadius(57f);
        holder.chart.setCenterText("");
        holder.chart.setCenterTextTypeface(mTf);
        holder.chart.setCenterTextSize(18f);
        holder.chart.setUsePercentValues(true);
        holder.chart.setEntryLabelColor(R.color.blue);

        holder.chart.setOnChartValueSelectedListener(this);
        if (!(mChartData == null)) {
            mChartData.setValueFormatter(new PercentFormatter());
            mChartData.setValueTypeface(mTf);
            mChartData.setValueTextSize(11f);
            mChartData.setValueTextColor(this.cont.getResources().getColor(R.color.nbkdarkbrown));
            // set data
            holder.chart.setData((PieData) mChartData);

/*    Legend l = holder.chart.getLegend();
    l.setPosition(LegendPosition.BELOW_CHART_CENTER);*/

            // do not forget to refresh the chart
            // holder.chart.invalidate();
            holder.chart.animateXY(900, 900);
        }
        return convertView;
    }

    private static class ViewHolder {
        PieChart chart;
    }


    @Override
    public void onValueSelected(Entry e, Highlight h) {

        if (e == null)
            return;
        Log.i("VAL SELECTED",
                "Value: " + e.getY() + ", index: " + h.getX()
                        + ", DataSet index: " + h.getDataSetIndex());
        Toast.makeText(cont, "Value: " + e.getY() + ", index: " + h.getX()
                + ", DataSet index: " + h.getDataSetIndex(), Toast.LENGTH_LONG).show();

    }


    @Override
    public void onNothingSelected() {
        Log.i("PieChart", "nothing selected");
        Toast.makeText(cont, "Nothing Selected", Toast.LENGTH_LONG).show();
    }


}
