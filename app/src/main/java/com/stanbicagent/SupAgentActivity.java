package com.stanbicagent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

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
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SupAgentActivity extends AppCompatActivity implements SupFragmentDrawer.FragmentDrawerListener,View.OnClickListener {
    private Toolbar mToolbar;
    int count = 1;
    private SupFragmentDrawer drawerSupFragment;

    //  public ResideMenu resideMenuClass;

    private static int SPLASH_TIME_OUT = 2000;
    TextView greet;

    SessionManagement session;
    public static final long DISCONNECT_TIMEOUT = 180000; // 5 min = 5 * 60 * 1000 ms

 /*   private Handler disconnectHandler = new Handler(){
        public void handleMessage(Message msg) {
        }
    };

    private Runnable disconnectCallback = new Runnable() {
        @Override
        public void run() {
            // Perform any required operation on disconnect
            finish();
            Intent i = new Intent(FMobActivity.this, SignInActivity.class);

            // Staring Login Activity
            startActivity(i);
            Toast.makeText(FMobActivity.this, "Your have been inactive for too long. Please login again", Toast.LENGTH_LONG).show();

        }
    };*/

/*    public void resetDisconnectTimer(){
        disconnectHandler.removeCallbacks(disconnectCallback);
        disconnectHandler.postDelayed(disconnectCallback, DISCONNECT_TIMEOUT);
    }

    public void stopDisconnectTimer(){
        disconnectHandler.removeCallbacks(disconnectCallback);
    }*/

    @Override
    public void onUserInteraction(){
        //   resetDisconnectTimer();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        session = new SessionManagement(this);

        super.onCreate(savedInstanceState);
        // session = new SessionManagement(this);
        setContentView(R.layout.activity_sup_agent);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        // getSupportActionBar().setLogo(R.drawable.fbnbluesmall);

        drawerSupFragment = (SupFragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawerr);
        //   drawerFragment.setArguments(bundle);
        drawerSupFragment
                .setUp(R.id.fragment_navigation_drawerr, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerSupFragment.setDrawerListener(this);


        displayView(40);
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
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


        if(id == R.id.inbox) {
           /* Fragment fragment = new Inbox();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment,"Inbox");
            fragmentTransaction.addToBackStack("Inbox");*/
            //   getSupportActionBar().setTitle("Notifications");
            //setActionBarTitle("Inbox");
          //  fragmentTransaction.commit();

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

                fragment = new SupHomeFrag();
                title = "Welcome";

                break;
            case 0:

                fragment = new SupHomeFrag();
                title = "Welcome";

                break;

            case 1:
              /*  fragment = new AgentTopUp();
                title = "Agent Topup";
*/
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
            case 6:
                 /*   fragment = new OpenAccMenu();
                    title = "Remittances";*/

                fragment = new OpenAcc();
                title = "Open Account";
                break;


            case 7:
              /*  fragment = new SelChart();
                title = "My Stats";*/
                android.app.Fragment   fragmentt = new SelChart();
                android.app.FragmentManager fragmentManager = getFragmentManager();
                android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                //  String tag = Integer.toString(title);
                fragmentTransaction.replace(R.id.container_body, fragmentt,title);

                fragmentTransaction.addToBackStack(null);

                fragmentTransaction.commit();

                break;


            case 8:

                fragment = new ChangeACName();
                title = "My Profile";

                break;
            case 9:

                Intent ppp = new Intent(SupAgentActivity.this, ChatActivity.class);

                // Staring Login Activity
                startActivity(ppp);
                break;
            case 10:

           /*     fragment = new RequestbankVisit();
                title = "Bank Visit";*/
                Toast.makeText(SupAgentActivity.this, "Visit Agent Shop Feature coming soon", Toast.LENGTH_LONG).show();
                break;
            case 11:
                this.finish();
                //     session.logoutUser();
                // After logout redirect user to Loing Activity
                Intent i = new Intent(SupAgentActivity.this, SignInActivity.class);

                // Staring Login Activity
                startActivity(i);
                Toast.makeText(SupAgentActivity.this, "You have successfully signed out", Toast.LENGTH_LONG).show();
                break;


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
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        FragmentManager fm = getSupportFragmentManager();
        int bentry = fm.getBackStackEntryCount();
        Log.i("TAG", "Frag Entry: " + bentry);

        if(bentry > 0){

            finish();

            startActivity(new Intent(getApplicationContext(),FMobActivity.class));
        }else{

            new MaterialDialog.Builder(SupAgentActivity.this)
                    .title("Confirm Exit")
                    .content("Are you sure you want to exit from FirstAgent? ")
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
                            // dialog.dismiss();

                            String title = "Welcome";
                            Fragment    fragment = new HomeAccountFragNewUI();
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            //  String tag = Integer.toString(title);
                            fragmentTransaction.replace(R.id.container_body, fragment,title);

                            fragmentTransaction.addToBackStack(title);

                            fragmentTransaction.commit();

                            // set the toolbar title
                            // getSupportActionBar().setTitle(title);
                        }
                    })
                    .show();


        }
    }

    public void addFragment( Fragment frag,String title){

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
       /* resetDisconnectTimer();*/

        HashMap<String, Long> nurl = session.getCurrTime();
        long newurl = nurl.get(SessionManagement.KEY_TIMEST);

        if (newurl > 0) {
            long secs = (new Date().getTime()) / 1000;
            long diff = 0;
            if (secs >= newurl) {
                diff = secs - newurl;
                if (diff > 180) {

                    this.finish();
                    session.logoutUser();
                    // After logout redirect user to Loing Activity
                    Intent i = new Intent(SupAgentActivity.this, SignInActivity.class);

                    // Staring Login Activity
                    startActivity(i);
                    Toast.makeText(SupAgentActivity.this, "Your session has expired. Please login again", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
