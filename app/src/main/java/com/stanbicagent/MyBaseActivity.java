package com.stanbicagent;


import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;

public class MyBaseActivity extends AppCompatActivity {
    SessionManagement session;
    public static final long DISCONNECT_TIMEOUT = 180000; // 5 min = 5 * 60 * 1000 ms

    private Handler disconnectHandler = new Handler(){
        public void handleMessage(Message msg) {
        }
    };

    private Runnable disconnectCallback = new Runnable() {
        @Override
        public void run() {
            // Perform any required operation on disconnect
            finish();
            Intent i = new Intent(MyBaseActivity.this, SignInActivity.class);

            // Staring Login Activity
            startActivity(i);
            Toast.makeText(MyBaseActivity.this, "Your have been inactive for too long. Please login again", Toast.LENGTH_LONG).show();

        }
    };

    public void resetDisconnectTimer(){
        disconnectHandler.removeCallbacks(disconnectCallback);
        disconnectHandler.postDelayed(disconnectCallback, DISCONNECT_TIMEOUT);
    }

    public void stopDisconnectTimer(){
        disconnectHandler.removeCallbacks(disconnectCallback);
    }

    @Override
    public void onUserInteraction(){
        resetDisconnectTimer();


    }
    @Override
    public void onPause()
    {

        super.onPause();
     /*  session = new SessionManagement(this);
        long secs = (new Date().getTime())/1000;
        SecurityLayer.Log("Seconds Loged",Long.toString(secs));*/
       // session.putCurrTime(secs);
    }
    @Override
    public void onResume() {
        super.onResume();
        resetDisconnectTimer();
     /*   session = new SessionManagement(this);
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
                    Intent i = new Intent(MyBaseActivity.this, SignInActivity.class);

                    // Staring Login Activity
                    startActivity(i);
                    Toast.makeText(MyBaseActivity.this, "Your session has expired. Please login again", Toast.LENGTH_LONG).show();
                }
            }
        }*/
    }

    @Override
    public void onStop() {
        super.onStop();
        stopDisconnectTimer();
    }
}