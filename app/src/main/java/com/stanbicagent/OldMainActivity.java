package com.stanbicagent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.stanbicagent.R;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.Date;
import java.util.HashMap;

import security.SecurityLayer;

public class OldMainActivity extends AppCompatActivity implements FragmentDrawerNewUI.FragmentDrawerListener,View.OnClickListener {
    private Toolbar mToolbar;
int count = 1;
    private FragmentDrawerNewUI drawerFragment;
    SessionManagement session;


    private static int SPLASH_TIME_OUT = 2000;
    TextView greet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        session = new SessionManagement(this);

        super.onCreate(savedInstanceState);
       // session = new SessionManagement(this);
        setContentView(R.layout.oldactivity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final Vibrator vibe = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);



        drawerFragment = (FragmentDrawerNewUI)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        //   drawerFragment.setArguments(bundle);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

       /* FragmentManager fm = getSupportFragmentManager();
        fm.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if(getFragmentManager().getBackStackEntryCount() == 0) finish();
            }
        });*/

        // display the first navigation drawer view on app launch
       displayView(40);
    }
    private boolean returnBackStackImmediate(FragmentManager fm) {
      /*  List<Fragment> fragments = fm.getFragments();
        if (fragments != null && fragments.size() > 0) {
            for (Fragment fragment : fragments) {
                if (fragment.getChildFragmentManager().getBackStackEntryCount() > 0) {
                    if (fragment.getChildFragmentManager().popBackStackImmediate()) {
                        return true;
                    } else {
                        return returnBackStackImmediate(fragment.getChildFragmentManager());
                    }
                }
            }
        }*/
        return false;
    }

  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
      MenuInflater inflater = getMenuInflater();
      boolean chcklg = session.checkLogin();
      if(chcklg == true){
          inflater.inflate(R.menu.main, menu);
      }
      return true;
    }




  /*  public  void showSweet(){
        final SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                pDialog.dismiss();
                resideMenuClass.openMenu(ResideMenu.DIRECTION_LEFT);
            }
        }, SPLASH_TIME_OUT);
    }*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        /*else if(id == R.id.signout) {
            this.finish();
            session.logoutUser();
            Toast.makeText(getApplicationContext(), "You have logged out successfully", Toast.LENGTH_LONG).show();

            return true;
        }*/
    /*    else if(id == R.id.myben) {
            Fragment  fragment = new ContactMgmt();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment,"Beneficiaries");
            fragmentTransaction.addToBackStack("Beneficiaries");
            fragmentTransaction.commit();

                    setActionBarTitle("Beneficiaries");

            return true;
        }*/

        if(id == R.id.inbox) {
           /* android.app.Fragment  fragment = new Inbox();
            gaddAppFragment(fragment,"Inbox");*/
           /* Fragment  fragment = new Inbox();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment,"Inbox");
            fragmentTransaction.addToBackStack("Inbox");

            //setActionBarTitle("Inbox");
            fragmentTransaction.commit();*/

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    private void displayView(int position) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);
       boolean checklg = true;
        switch (position) {

           case 40:


                break;
            case 0:
                fragment = new OpenAcc();
                title = "Open Account";
                break;
            case 1:
                fragment = new Withdraw();
                title = "Cash Withdrawal";
                break;



            case 2:
                fragment = new FTMenu();
                title = "Fund Transfer";
                break;
            case 3:
                fragment = new AirtimeTransf();
                title = "Airtime Purchase";
                break;
            case 4:
            fragment = new BillersMenu();
                title = "Pay Bills";
                break;
            case 5:



                break;
            case 6:

                fragment = new ChangeACName();
                title = "My Profile";

                break;
            case 7:
            this.finish();
       //     session.logoutUser();
            // After logout redirect user to Loing Activity
            Intent i = new Intent(OldMainActivity.this, SignInActivity.class);

            // Staring Login Activity
            startActivity(i);
            Toast.makeText(OldMainActivity.this, "You have successfully signed out", Toast.LENGTH_LONG).show();
                  break;


            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment,title);
            if(!(title.equals("Welcome"))) {
                fragmentTransaction.addToBackStack(title);
            }
            fragmentTransaction.commit();

            // set the toolbar title
           getSupportActionBar().setTitle(title);
        }
    }
    public void setActionBarTitle(String title) {

        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onPause()
    {

        super.onPause();
        long secs = (new Date().getTime())/1000;
        SecurityLayer.Log("Seconds Loged",Long.toString(secs));
        session.putCurrTime(secs);
    }
   @Override
    protected void onResume()
    {

        super.onResume();
        boolean checklg = false;
        checklg = session.checkLogin();
        if (checklg == true) {
        HashMap<String, Long> nurl = session.getCurrTime();
        long newurl = nurl.get(SessionManagement.KEY_TIMEST);
            if(newurl > 0) {
                long secs = (new Date().getTime()) / 1000;
                long diff = 0;
                if (secs >= newurl) {
                    diff = secs - newurl;
                    if (diff > 180) {

                        this.finish();
                        session.logoutUser();
                        // After logout redirect user to Loing Activity
                        Intent i = new Intent(OldMainActivity.this, SignInActivity.class);

                        // Staring Login Activity
                        startActivity(i);
                        Toast.makeText(OldMainActivity.this, "Your session has expired. Please login again", Toast.LENGTH_LONG).show();
                    }
                }
            }
}


    }
    protected void onDestroy() {
        super.onDestroy();
       // session.logoutUser();
        // Put code here.

    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        FragmentManager fm = getSupportFragmentManager();
        int bentry = fm.getBackStackEntryCount();
        Log.i("TAG", "Frag Entry: " + bentry);
	  /* if(bentry >0){
	  FragmentManager.BackStackEntry backEntry=getSupportFragmentManager().getBackStackEntryAt(bentry-1);
	    String str=backEntry.getName();
	    SecurityLayer.Log("Fragment Tag", str);
		setTitle(navMenuTitles[Integer.parseInt(str)]);
	   }*/
        if(bentry > 0){
            String ide = null;
            for(int entry = 0; entry < fm.getBackStackEntryCount(); entry++){
                ide = fm.getBackStackEntryAt(entry).getName();
                Log.i("TAG", "Found fragment: " + ide);
            }

            setActionBarTitle(ide);
        }else{

            new MaterialDialog.Builder(OldMainActivity.this)
                    .title("Confirm Exit")
                    .content("Are you sure you want to exit ")
                    .positiveText("YES")
                    .negativeText("NO")

                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                          /*  finish();
                            session.logoutUser();*/
                            //  checkConnBio();
                            session.logoutUser();

                            // After logout redirect user to Loing Activity
                            Intent i = new Intent(getApplicationContext(), SignInActivity.class);

                            // Staring Login Activity
                            startActivity(i);
                            Toast.makeText(getApplicationContext(), "You have logged out successfully", Toast.LENGTH_LONG).show();


                        }

                        @Override
                        public void onNegative(MaterialDialog dialog)
                        {

                        }
                    })
                    .show();


        }
    }

    public void SetTheme(){
        HashMap<String, Integer> nmob = session.getCurrTheme();
        int position = nmob.get(SessionManagement.KEY_THEME);
        SecurityLayer.Log("Current Theme Pos ","Theme Pos is"+Integer.toString(position));
        //setTheme(R.style.MyMaterialTheme);
        switch (position) {


            case 1:

                setTheme(R.style.MyMaterialTheme);

                break;

            case 2:

                setTheme(R.style.BlueTheme);

                break;

            case 3:
                setTheme(R.style.RedTheme);
                break;


            case 4:
                setTheme(R.style.GreenTheme);
                break;
        }

            HashMap<String, String> nurl = session.getMobNo();
            String mno = nurl.get(SessionManagement.KEY_MOBILE);

            HashMap<String, String> nu = session.getFulln();
            String fname = nu.get(SessionManagement.KEY_FULLN);

            HashMap<String, String> ntxn = session.getTxnFlag();
            String txnflag = ntxn.get(SessionManagement.KEY_TXPIN);

            HashMap<String, String> nema = session.getEmail();
            String emal = nema.get(SessionManagement.KEY_EMAIL);

            HashMap<String, String> ninst = session.getInst();
            String insttyp = ninst.get(SessionManagement.KEY_INST);

            HashMap<String, String> nlstl = session.getLastl();
            String lastl = nlstl.get(SessionManagement.LASTL);

            HashMap<String, String> ndisp = session.getDisp();
            String dispn = ndisp.get(SessionManagement.KEY_DISP);

            session.createLoginSession();
            session.putMobNo(mno);
            session.putTxnFlag(txnflag);
            session.putInst(insttyp);
            session.putEmail(emal);
            session.putLastl(lastl);
            session.putFulln(fname);
            if(!(dispn == null)) {
                dispn = dispn.replace("_"," ");
                session.putDisp(dispn);
            }



    }



    @Override
    public void onClick(View v) {

    }
}
