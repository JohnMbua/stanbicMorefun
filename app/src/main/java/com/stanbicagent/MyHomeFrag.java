package com.stanbicagent;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.stanbicagent.R;

import java.util.ArrayList;
import java.util.List;

import adapter.Dashboard;
import adapter.DashboardAdapter;

import adapter.ImagesBean;
import tablayout.SlidingTabLayout;


public class MyHomeFrag extends Fragment {

    ProgressDialog prgDialog;

    SessionManagement session;
    ViewPager pager;

    SlidingTabLayout tabs;
    CharSequence Titles[] = {"HOME","ACTIVITIES","OFFERS","PRODUCTS"};
    int Numboftabs =4;
    List<Dashboard> planetsList = new ArrayList<Dashboard>();
    ListView lv;
    DashboardAdapter aAdpt;
    GridView gridView;
    private Runnable animateViewPager;
    private Handler handler;
    List<ImagesBean> products;
    private static final long ANIM_VIEWPAGER_DELAY = 5000;
    private static final long ANIM_VIEWPAGER_DELAY_USER_VIEW = 10000;

    boolean stopSliding = false;
    private ViewPager mViewPager;

    public MyHomeFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.myhomefrag, null);
        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.


     //   lv = (ListView) root.findViewById(R.id.lv);
        gridView = (GridView) root.findViewById(R.id.gridView1);
       SetPop();
      //  mViewPager = (ViewPager)root.findViewById(R.id.pager);
      /*  mIndicator = (CirclePageIndicator) root.findViewById(R.id.indicator);
        mViewPager.setAdapter(new TestFragmentAdapter(getChildFragmentManager(),getActivity()));

        mIndicator.setViewPager(mViewPager);
							*//*imgNameTxt.setText(""
									+ ((ProductBean) products.get(mViewPager
											.getCurrentItem())).getName());*//*
        runnable(3);
        handler.postDelayed(animateViewPager,
                ANIM_VIEWPAGER_DELAY);
        mViewPager.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                switch (event.getAction()) {

                    case MotionEvent.ACTION_CANCEL:
                        break;

                    case MotionEvent.ACTION_UP:
                        // calls when touch release on ViewPager

                        stopSliding = false;
                        runnable(6);
                        handler.postDelayed(animateViewPager,
                                ANIM_VIEWPAGER_DELAY_USER_VIEW);

                        break;

                    case MotionEvent.ACTION_MOVE:
                        // calls when ViewPager touch
                        if (handler != null && stopSliding == false) {
                            stopSliding = true;
                            handler.removeCallbacks(animateViewPager);
                        }
                        break;
                }
                return false;
            }
        });*/

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Fragment  fragment = null;
                String title = null;


                   if(position == 0) {
                    fragment = new OpenAcc();
                    title = "Open Account";
                }
                   else  if(position == 1) {
                    fragment = new FTMenu();
                    title = "Deposit";
                }
          else      if(position == 2) {
                    fragment = new WithdrawalMenu();
                    title = "Withdrawal";
                }
           else     if(position == 3) {
                    fragment = new BillerChoiceMenu();
                    title = "Billers";
                }
             else   if(position == 4) {
                    fragment = new AirtimeTransf();
                    title = "Airtime Transfer";
                }
             else   if(position == 5) {
                    fragment = new OpenAccMenu();
                    title = "Remittances";
                }

              else  if(position == 6) {
                  /*  fragment = new Minstat();
                    title = "Mini-statement";*/
                }

                if(fragment != null) {




                    FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
                    //  String tag = Integer.toString(title);
                    fragmentTransaction.replace(R.id.container_body, fragment, title);
                    fragmentTransaction.addToBackStack(title);

                    fragmentTransaction.commit();
                    ((MainActivity)getActivity()).count =  ((MainActivity)getActivity()).count++;
                    //  ((MainActivity)getActivity()).changeFragment(fragment,title);

                }


            }
        });

        return root;
    }
    public void runnable(final int size) {
        handler = new Handler();
        animateViewPager = new Runnable() {
            public void run() {
                if (!stopSliding) {
                    if (mViewPager.getCurrentItem() == size - 1) {
                        mViewPager.setCurrentItem(0);
                    } else {
                        mViewPager.setCurrentItem(
                                mViewPager.getCurrentItem() + 1, true);
                    }
                    handler.postDelayed(animateViewPager, ANIM_VIEWPAGER_DELAY);
                }
            }
        };
    }
    @Override
    public void onResume() {


        super.onResume();
    }

    public void StartChartAct(int i){


    }

    public void SetPop(){
        planetsList.clear();
        planetsList.add(new Dashboard("Open Account",R.drawable.openaccnew));
        planetsList.add(new Dashboard("Deposit", R.drawable.cashdepo));
        planetsList.add(new Dashboard("Withdraw", R.drawable.withdraw));
        planetsList.add(new Dashboard("Bill Payment",R.drawable.billpayment));
       // planetsList.add(new TxnList("Payments","",R.drawable.payments));

        planetsList.add(new Dashboard("Remittances",R.drawable.ft));
        planetsList.add(new Dashboard("Mini-Statement",R.drawable.bankministat));





       /* planetsList.add(new Dashboard("My Profile",R.drawable.icons40));*/

        aAdpt = new DashboardAdapter( planetsList,getActivity());
        gridView.setAdapter(aAdpt);
    }


}
