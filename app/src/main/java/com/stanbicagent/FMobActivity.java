package com.stanbicagent;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;

import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.stanbicagent.BuildConfig;
import com.stanbicagent.R;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.security.ProviderInstaller;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import rest.ApiInterface;
import rest.ApiSecurityClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import security.SecurityLayer;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.Manifest.permission.READ_PHONE_STATE;

public class FMobActivity extends BaseActivity implements FragmentDrawer.FragmentDrawerListener,View.OnClickListener {
    private Toolbar mToolbar;
    int count = 1;
    private FragmentDrawer drawerFragment;
    private static int SPLASH_TIME_OUT = 2000;
    TextView greet;
       SessionManagement session;
    ProgressDialog prgDialog;
    ProgressDialog pro ;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle mDrawerToggle;
    String ccid;
    public static final long DISCONNECT_TIMEOUT = 180000; // 5 min = 5 * 60 * 1000 ms


    @Override
    public void onUserInteraction(){
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
       // session = new SessionManagement(this);
        pro = new ProgressDialog(getApplicationContext());
        pro.setMessage("Loading...");
        pro.setTitle("");
        pro.setCancelable(false);
        session = new SessionManagement(this);
        setContentView(R.layout.oldactivity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

       drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        //   drawerFragment.setArguments(bundle);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, drawerLayout, mToolbar);
        drawerFragment.setDrawerListener(this);

     //   mDrawerToggle = new ActionBarDrawerToggle (this, drawerLayout, mToolbar, R.string.open, R.string.close);

        prgDialog = new ProgressDialog(getApplicationContext());
        prgDialog.setMessage("Loading....");
        prgDialog.setCancelable(false);
        updateAndroidSecurityProvider(this);

       displayView(40);

        setGlValues();



     }

    /* public  void setNewToolbar(){
         mToolbar = (Toolbar) findViewById(R.id.newtoolbar);
         setSupportActionBar(mToolbar);
         getSupportActionBar().setDisplayShowHomeEnabled(true);
     }*/
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

  /*  private void enableViews(boolean enable) {

        // To keep states of ActionBar and ActionBarDrawerToggle synchronized,
        // when you enable on one, you disable on the other.
        // And as you may notice, the order for this operation is disable first, then enable - VERY VERY IMPORTANT.
        if(enable) {
//You may not want to open the drawer on swipe from the left in this case
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
// Remove hamburger
            dra.setDrawerIndicatorEnabled(false);
            // Show back button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            // when DrawerToggle is disabled i.e. setDrawerIndicatorEnabled(false), navigation icon
            // clicks are disabled i.e. the UP button will not work.
            // We need to add a listener, as in below, so DrawerToggle will forward
            // click events to this listener.
            if(!mToolBarNavigationListenerIsRegistered) {
                mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Doesn't have to be onBackPressed
                        onBackPressed();
                    }
                });

                mToolBarNavigationListenerIsRegistered = true;
            }

        } else {
//You must regain the power of swipe for the drawer.
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

// Remove back button
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            // Show hamburger
            .setDrawerIndicatorEnabled(true);
            // Remove the/any drawer toggle listener
            mDrawerToggle.setToolbarNavigationClickListener(null);
            mToolBarNavigationListenerIsRegistered = false;
        }

        // So, one may think "Hmm why not simplify to:
        // .....
        // getSupportActionBar().setDisplayHomeAsUpEnabled(enable);
        // mDrawer.setDrawerIndicatorEnabled(!enable);
        // ......
        // To re-iterate, the order in which you enable and disable views IS important #dontSimplify.
    }*/
  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
      MenuInflater inflater = getMenuInflater();

          inflater.inflate(R.menu.main, menu);

      return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
         if(id == R.id.inbox) {

             showEditDialog("INBOX");

           /* android.app.Fragment  fragment = new Inbox();
             addAppFragment(fragment,"Inbox");*/
            /*FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment,"Inbox");
            fragmentTransaction.addToBackStack("Inbox");
       //   getSupportActionBar().setTitle("Notifications");
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
    private void updateAndroidSecurityProvider(Activity callingActivity) {
        try {
            ProviderInstaller.installIfNeeded(this);
        } catch (GooglePlayServicesRepairableException e) {
            // Thrown when Google Play Services is not installed, up-to-date, or enabled
            // Show dialog to allow users to install, update, or otherwise enable Google Play services.
            GooglePlayServicesUtil.getErrorDialog(e.getConnectionStatusCode(), callingActivity, 0);
        } catch (GooglePlayServicesNotAvailableException e) {
            SecurityLayer.Log("SecurityException", "Google Play Services not available.");
        }
    }
    private void displayView(int position) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);
        String list = session.getString("VTYPE");

       boolean checklg = true;
        switch (position) {
           case 40:
               if( list == null){
                   fragment = new HomeAccountFragNewUI();
               }
               else{
                   SecurityLayer.Log("View type chosen",list);

if(list.equals("N") || list.equals("grid")) {
    fragment = new HomeAccountFragNewUI();
}
               if(list.equals("list")) {
                   fragment = new HomeAccountFragNewUI();
               }}
                title = "Welcome";

                break;
            case 0:

                if( list == null){
                    fragment = new HomeAccountFragNewUI();
                }else {
                    if (list.equals("N") || list.equals("grid")) {
                        fragment = new HomeAccountFragNewUI();
                    }
                    if (list.equals("list")) {
                        fragment = new HomeAccountFragNewUI();
                    }
                }
                title = "Welcome";

                break;

            case 1:


               /* android.app.Fragment  fragmennt = new SelChartNewVers();
                String titlee = "My Performance";
               addAppFragment(fragmennt,titlee);*/
showEditDialog("MYPERF");
                break;


            case 2:

              /*  fragment = new ChangeACName();
                title = "My Profile";*/
                showEditDialog("PROF");
                break;
            case 3:

                //     session.logoutUser();
                // After logout redirect user to Loing Activity
            /*  Intent ps = new Intent(FMobActivity.this, ChatActivity.class);


                // Staring Login Activity
                startActivity(ps);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);*/

                fragment = new ViewComplaints();
                title = "My Profile";

               /* fragment = new ViewComplaints();
                title = "My Profile";*/
                break;
            case 4:



               /*fragment = new ViewAgentRequests();
                title = "Coming Soon";*/
                fragment = new ComingSoon();
                title = "My Profile";
                break;
            case 5:
                this.finish();
                    session.logoutUser();
                // After logout redirect user to Loing Activity
                Intent i = new Intent(FMobActivity.this, SignInActivity.class);


                // Staring Login Activity
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                Toast.makeText(FMobActivity.this, "You have successfully signed out", Toast.LENGTH_LONG).show();
                break;


         /*   case 40:

                fragment = new HomeAccountFragNewUI();
                title = "Welcome";

                break;
            case 0:

                fragment = new HomeAccountFragNewUI();
                title = "Welcome";

                break;

            case 1:
                fragment = new CashDepo();
                title = "Deposit";

                break;


            case 2:

                fragment = new FTMenu();
                title = "Transfer";
                break;
            case 3:
                fragment = new Withdraw();
                title = "Withdrawal";
                break;
            case 4:
                fragment = new BillMenu();
                title = "Billers";
                break;
            case 5:
                fragment = new AirtimeTransf();
                title = "Airtime Transfer";
                break;
            *//*case 6:


                fragment = new OpenAcc();
                title = "Open Account";
                //   Toast.makeText(FMobActivity.this, "Open Account coming soon", Toast.LENGTH_LONG).show();
                break;*//*


            case 6:

                android.app.Fragment   fragmentt = new SelChart();
                android.app.FragmentManager fragmentManager = getFragmentManager();
                android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                //  String tag = Integer.toString(title);
                fragmentTransaction.replace(R.id.container_body, fragmentt,title);

                fragmentTransaction.addToBackStack(null);

                fragmentTransaction.commit();

                break;


            case 7:

                fragment = new ChangeACName();
                title = "My Profile";

                break;
            case 9:


                fragment = new ComingSoon();
                title = "Coming Soon";
                //  Toast.makeText(FMobActivity.this, "Chat Feature coming soon", Toast.LENGTH_LONG).show();
                break;
            case 10:

          *//* fragment = new RequestbankVisit();
                title = "Bank Visit";*//*
                fragment = new ComingSoon();
                title = "Coming Soon";
                break;
            case 8:
                Toast.makeText(FMobActivity.this, "You have successfully signed out", Toast.LENGTH_LONG).show();

                finish();
                Intent i = new Intent(FMobActivity.this, SignInActivity.class);

                startActivity(i);

                break;*/

            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment,title);

                fragmentTransaction.addToBackStack(title);

            fragmentTransaction.commit();

            // set the toolbar title
        //   getSupportActionBar().setTitle(title);
        }
    }
    public void setActionBarTitle(String title) {

     //   getSupportActionBar().setTitle(title);
    }


    protected void onDestroy() {

        super.onDestroy();
       // session.logoutUser();
        // Put code here.

    }

    private void replaceFragment (Fragment fragment){
        String backStateName = fragment.getClass().getName();

        FragmentManager manager = getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate (backStateName, 0);

        if (!fragmentPopped){ //fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.container_body, fragment);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }

    public void showEditDialog(String serv) {
        FragmentManager fm = getSupportFragmentManager();
        DialogSignInFragment editNameDialog = new DialogSignInFragment();
        Bundle bundle = new Bundle();
        bundle.putString("SERV",serv);
        editNameDialog.setArguments(bundle);
        editNameDialog.show(fm, "fragment_edit_name");
    }


    public void showNubanDialog(String recanno) {
        FragmentManager fm = getSupportFragmentManager();
        DialogNubanBanks nubbanks = new DialogNubanBanks();
        Bundle bundle = new Bundle();
        bundle.putString("recanno",recanno);
        nubbanks.setArguments(bundle);
        nubbanks.show(fm, "fragment_edit_name");
    }
    public void showWrongPinDialog(String serv) {
        FragmentManager fm = getSupportFragmentManager();
        WrongPinDialog editNameDialog = new WrongPinDialog();
        Bundle bundle = new Bundle();
        bundle.putString("SERV",serv);
        editNameDialog.setArguments(bundle);
        editNameDialog.show(fm, "fragment_edit_name");
    }

   /* @Override
    public void onFinishEditDialog(String inputText) {
        Toast.makeText(this, "Hi, " + inputText, Toast.LENGTH_SHORT).show();
    }*/
    @Override
    public void onBackPressed()
    {

        FragmentManager fm = getSupportFragmentManager();
        android.app.FragmentManager fm2 = getFragmentManager();
        int bentry = fm.getBackStackEntryCount();
        Log.i("TAG", "Frag Entry: " + bentry);

        int bentry2 = fm2.getBackStackEntryCount();
        Log.i("TAG", "Comm Report Frag Entry: " + bentry2);
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.container_body);
        if(f instanceof NewHomeGrid || f instanceof HomeAccountFragNewUI){
            // do something with f
            SecurityLayer.Log("I am here","I am here");
            finish();

    }

      /*  finish();
        startActivity(new Intent(getApplicationContext(),FMobActivity.class));*/


        if(bentry2 > 0){
/*finish();
            startActivity(new Intent(getApplicationContext(),FMobActivity.class));*/
        /*   Fragment fragment = null;
            String title = null;
            String list = session.getString("VTYPE");
            if( list == null){
                fragment = new NewHomeGrid();
            }else {
                if (list.equals("N") || list.equals("grid")) {
                    fragment = new NewHomeGrid();
                }
                if (list.equals("list")) {
                    fragment = new HomeAccountFragNewUI();
                }
            }
            title = "Welcome";

           addFragment(fragment,title);
            String ide = null;
            for(int entry = 0; entry < fm.getBackStackEntryCount(); entry++){
                ide = fm.getBackStackEntryAt(entry).getName();
                Log.i("TAG", "Found fragment: " + ide);
            }*/
        }/*else if(bentry > 0) {

finish();

            startActivity(new Intent(getApplicationContext(),FMobActivity.class));

          *//*  String ide = null;
            for (int entry = 0; entry < fm.getBackStackEntryCount(); entry++) {
                ide = fm.getBackStackEntryAt(entry).getName();
                Log.i("TAG", "Found fragment: " + ide);
            }
*//*
        }else {

            new MaterialDialog.Builder(FMobActivity.this)
                    .title("Confirm Exit")
                    .content("Are you sure you want to exit from FirstAgent? ")
                    .positiveText("YES")
                    .negativeText("NO")

                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                          *//*  finish();
                            session.logoutUser();*//*
                            //  checkConnBio();
                            session.logoutUser();

                            // After logout redirect user to Loing Activity
                            Intent i = new Intent(getApplicationContext(), SignInActivity.class);

                            // Staring Login Activity
                            startActivity(i);
                            Toast.makeText(getApplicationContext(), "You have logged out successfully", Toast.LENGTH_LONG).show();


                        }

                        @Override
                        public void onNegative(MaterialDialog dialog) {
                            // dialog.dismiss();
                            Fragment fragment = null;
                            String list = session.getString("VTYPE");

                            String title = "Welcome";
                            if (list == null) {
                                fragment = new NewHomeGrid();
                            }else {
                                if (list.equals("N") || list.equals("grid")) {
                                    fragment = new NewHomeGrid();
                                }
                                if (list.equals("list")) {
                                    fragment = new HomeAccountFragNewUI();
                                }
                            }
                            title = "Welcome";
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            //  String tag = Integer.toString(title);
                            fragmentTransaction.replace(R.id.container_body, fragment, title);

                            fragmentTransaction.addToBackStack(title);

                            fragmentTransaction.commit();

                            // set the toolbar title
                            // getSupportActionBar().setTitle(title);
                        }
                    })
                    .show();


        }*/

        super.onBackPressed();
    }

  public void addFragment( Fragment frag,String title){

      FragmentManager fragmentManager = getSupportFragmentManager();
      FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
      //  String tag = Integer.toString(title);
      fragmentTransaction.replace(R.id.container_body, frag,title);
      fragmentTransaction.addToBackStack(title);
      fragmentTransaction.commit();
     setActionBarTitle(title);
  }

    public void addAppFragment( android.app.Fragment frag,String title){



        android.app.FragmentManager fragmentManager = getFragmentManager();
        android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //  String tag = Integer.toString(title);
        fragmentTransaction.replace(R.id.container_body, frag,title);

        fragmentTransaction.addToBackStack(title);

        fragmentTransaction.commit();
    }
    @Override
    public void onClick(View v) {

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
    public void onResume() {
        super.onResume();
      /* *//* resetDisconnectTimer();*/

     /*   HashMap<String, Long> nurl = session.getCurrTime();
        long newurl = nurl.get(SessionManagement.KEY_TIMEST);

        if (newurl > 0) {
            long secs = (new Date().getTime()) / 1000;
            long diff = 0;
            if (secs >= newurl) {
                diff = secs - newurl;
                if (diff > 180) {

                    this.finish();
                //    session.logoutUser();
                    // After logout redirect user to Loing Activity
                    Intent i = new Intent(FMobActivity.this, FMobActivity.class);

                    // Staring Login Activity
                    startActivity(i);
                 //   Toast.makeText(FMobActivity.this, "Your session has expired. Please login again", Toast.LENGTH_LONG).show();
                }
            }
        }*/
    }



public  void LogOut(){
    session.logoutUser();

    // After logout redirect user to Loing Activity
    finish();
    Intent i = new Intent(getApplicationContext(), SignInActivity.class);

    // Staring Login Activity
    startActivity(i);
    Toast.makeText(
            getApplicationContext(),
            "You have been locked out of the app.Please call customer care for further details",
            Toast.LENGTH_LONG).show();
   // Toast.makeText(getApplicationContext(), "You have logged out successfully", Toast.LENGTH_LONG).show();

}

    private void LogRetro(String params) {


        pro.show();
        String endpoint= "login/login.action/";

        String urlparams = "";
        try {
            urlparams = SecurityLayer.generalLogin(params,"23322",getApplicationContext(),endpoint);
            //SecurityLayer.Log("cbcurl",url);
            SecurityLayer.Log("RefURL",urlparams);
            SecurityLayer.Log("refurl", urlparams);
            SecurityLayer.Log("params", params);
        } catch (Exception e) {
            SecurityLayer.Log("encryptionerror",e.toString());
        }





        ApiInterface apiService =
                ApiSecurityClient.getClient(getApplicationContext()).create(ApiInterface.class);


        Call<String> call = apiService.setGenericRequestRaw(urlparams);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    // JSON Object
                    SecurityLayer.Log("response..:", response.body());


                    JSONObject obj = new JSONObject(response.body());
                 /*   JSONObject jsdatarsp = obj.optJSONObject("data");
                    SecurityLayer.Log("JSdata resp", jsdatarsp.toString());
                    //obj = Utility.onresp(obj,getActivity()); */
                    obj = SecurityLayer.decryptGeneralLogin(obj, getApplicationContext());
                    SecurityLayer.Log("decrypted_response", obj.toString());

                    String respcode = obj.optString("responseCode");
                    String responsemessage = obj.optString("message");




                    JSONObject datas = obj.optJSONObject("data");

                    //session.setString(SecurityLayer.KEY_APP_ID,appid);

                    if (Utility.isNotNull(respcode) && Utility.isNotNull(responsemessage)) {
                        SecurityLayer.Log("Response Message", responsemessage);

                        if (respcode.equals("00")) {
                            if (!(datas == null)) {
                                android.app.Fragment  fragment = new Minstat();
                                String title = "Mini Statement";
                                addAppFragment(fragment,title);
                            }
                        }
                        else {

                            Toast.makeText(
                                    getApplicationContext(),
                                    responsemessage,
                                    Toast.LENGTH_LONG).show();


                        }

                    }
                    else {

                        Toast.makeText(
                                getApplicationContext(),
                                "There was an error on your request",
                                Toast.LENGTH_LONG).show();


                    }

                } catch (JSONException e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    // TODO Auto-generated catch block
                    Toast.makeText(getApplicationContext(), getApplicationContext().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                    // SecurityLayer.Log(e.toString());

                } catch (Exception e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    // SecurityLayer.Log(e.toString());
                }
                if ((pro != null) && pro.isShowing() && !(getApplicationContext() == null)) {
                    pro.dismiss();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                SecurityLayer.Log("Throwable error",t.toString());
                Toast.makeText(
                        getApplicationContext(),
                        "There was an error processing your request",
                        Toast.LENGTH_LONG).show();
                if ((pro != null) && pro.isShowing() && !(getApplicationContext() == null)) {
                    pro.dismiss();
                }

            }
        });

    }

    public void SetForceOutDialog(String msg, final String title, final Context c) {
        if (!(c == null)) {
            new MaterialDialog.Builder(c)
                    .title(title)
                    .content(msg)

                    .negativeText("CONTINUE")
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            dialog.dismiss();
                        }

                        @Override
                        public void onNegative(MaterialDialog dialog) {

                            dialog.dismiss();
                            FMobActivity.this.finish();
                            session.logoutUser();

                            // After logout redirect user to Loing Activity
                            Intent i = new Intent(c, SignInActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            // Staring Login Activity
                            startActivity(i);

                        }
                    })
                    .show();
        }
    }


    public  void SetCorrectActionBar(){
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void setGlValues() {
        //Read sim serial and save
        if (ccid == null){
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if ((ContextCompat.checkSelfPermission(this, READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)) {
            /* Ask for Permission */
            ActivityCompat.requestPermissions(this,
                    new String[]{READ_PHONE_STATE},
                    1);
        } else {

            ccid = telephonyManager.getSimSerialNumber();
        }
        ApplicationClass.get().getGlobalVals().setSimSerial(ccid);
//        ApplicationClass.get().getGlobalVals().setTermID("");
    }

    }


}
