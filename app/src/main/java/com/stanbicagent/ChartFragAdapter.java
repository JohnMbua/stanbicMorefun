package com.stanbicagent;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

class ChartFragAdapter extends FragmentPagerAdapter  {
    protected static final String[] CONTENT = new String[] { "This", "Is", "A", "Test", };
    protected static final int[] ICONS = new int[] {

    };

    private int mCount = 2;
    String sevc = null;
    String pfrom = null;
    String pto = null;

    public ChartFragAdapter(FragmentManager fm,String servi,String  pffrom,String ptto)

    {
        super(fm);
        sevc = servi;
        pfrom = pffrom;
        pto = ptto;

    }

    @Override
    public Fragment getItem(int position) {

        if(position == 0) // if the position is 0 we are returning the First tab
        {
            Bundle b  = new Bundle();
            b.putString("serv",sevc);
            b.putString("pfrom",pfrom);
            b.putString("pto",pto);
            LineChartFrag tab1 = new LineChartFrag();
            tab1.setArguments(b);
            return tab1;
        }
        else  // if the position is 0 we are returning the First tab
        {
            Bundle b  = new Bundle();
            b.putString("serv",sevc);
            b.putString("pfrom",pfrom);
            b.putString("pto",pto);
            BarChartFrag tab2 = new BarChartFrag();
            tab2.setArguments(b);
            return tab2;
        }



      /*  return TestFragment.newInstance(CONTENT[position % CONTENT.length]);*/
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
      return ChartFragAdapter.CONTENT[position % CONTENT.length];
    }


    public void setCount(int count) {
        if (count > 0 && count <= 10) {
            mCount = count;
            notifyDataSetChanged();
        }
    }
}