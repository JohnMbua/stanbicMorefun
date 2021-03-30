package adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.stanbicagent.NewSelChart;
import com.stanbicagent.NewSelChartTran;

import java.util.List;

/**
 * Created by hp1 on 21-01-2015.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created
    List<String> Titles;


    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapter(FragmentManager fm, List<String> Titles, int mNumbOfTabsumb) {
        super(fm);

        this.Titles = Titles;
        this.NumbOfTabs = mNumbOfTabsumb;


    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {

        Fragment fragmentt;
        if (position == 0) // if the position is 0 we are returning the First tab
        {
            fragmentt = new NewSelChart();

        } else         // As we are having 2 tabs if the position is now 0 it must be 1 so we are returning second tab
        {
            fragmentt = new NewSelChartTran();

        }
        return fragmentt;

    }


    // This method return the titles for the Tabs in the Tab Strip

    @Override
    public CharSequence getPageTitle(int position) {
        return Titles.get(position);
    }

    // This method return the Number of tabs for the tabs Strip

    @Override
    public int getCount() {
        return NumbOfTabs;
    }

    @Override
    // To update fragment in ViewPager, we should override getItemPosition() method,
    // in this method, we call the fragment's public updating method.
    public int getItemPosition(Object object) {
        Log.d("NewTag", "getItemPosition(" + object.getClass().getSimpleName() + ")");
        if (object instanceof NewSelChart) {

        }
        return super.getItemPosition(object);
    }
}