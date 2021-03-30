package com.stanbicagent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.stanbicagent.R;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import androidx.appcompat.app.AlertDialog;
import security.SecurityLayer;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import utils.AESUtils;

import static android.Manifest.permission.READ_PHONE_STATE;
import static com.stanbicagent.ApplicationConstants.NET_URL;

public class Splash extends Activity {
    SessionManagement session;
    private static int SPLASH_TIME_OUT = 4000;
    ProgressBar prgDialog;
    // String NET_URL = "https://mbanking.nationalbank.co.ke:8443";
//    String NET_URL = "http://196.32.226.78:8010",ccid;
    TextView gm;
    Integer tmt=0;

    public void showAlertDialogWithAutoDismiss() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update")
                .setMessage("Restart and accept un_installation of old application")
                .setCancelable(false).setCancelable(false)
                .setPositiveButton("Next", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //this for skip dialog
                        dialog.cancel();
                    }
                });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (alertDialog.isShowing()){
                    alertDialog.dismiss();
                }
            }
        }, 9000); //change 5000 with a specific time you want
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newsplash);
        SDKManager.getInstance().bindService(getApplicationContext());
        session = new SessionManagement(this);
        gm = (TextView) findViewById(R.id.gm);
        session.putURL(NET_URL);


//        final String packageNamed = "com.accessbank.accessagent";
//        android.content.pm.PackageManager pmd = getPackageManager();
//        try {
//            PackageInfo infor = pmd.getPackageInfo(packageNamed, 0);
//            SecurityLayer.Log("infor", String.valueOf(infor));
//
//            showAlertDialogWithAutoDismiss();
//            SecurityLayer.Log("timer", String.valueOf(tmt));
//
//            int UNINSTALL_REQUEST_CODE = 1;
//            Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
//            intent.setData(Uri.parse("package:" + packageNamed));
//            intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
//
//            startActivityForResult(intent, UNINSTALL_REQUEST_CODE);
//
//
//        } catch (PackageManager.NameNotFoundException e) {
//            SecurityLayer.Log("packager", "com.accessbank.accessagent is not available.");
//            e.printStackTrace();
//        }

        if (!(Utility.isEmulator())) {
            // if (!(Utility.isRooted())) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = null;
                   String chkreg = session.getString(SessionManagement.SESS_REG);



                    if ((chkreg == null) || chkreg.equals("")) {
                        i = new Intent(Splash.this, ActivateAgentBefore.class);
                        session.clearallPref();
                        startActivity(i);
                        finish();
                        //throw new RuntimeException("This is a crash");
                    } else {
                        boolean isloggedin = session.isLoggedIn();
                        if (isloggedin) {
                            i = new Intent(Splash.this, FMobActivity.class);
                        } else {
                            i = new Intent(Splash.this,SignInActivity.class);// SignInActivity.class); FMobActivity
                        }
                        startActivity(i);
                        finish();
                    }
                    //  overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }


            }, SPLASH_TIME_OUT);
            /*} else {
                Toast.makeText(
                        getApplicationContext(),
                        "You have currently rooted your device hence cant access this app"
                        , Toast.LENGTH_LONG).show();
                finish();
            }*/
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    "You are currently using a mobile Emulator which is not acceptable."
                    , Toast.LENGTH_LONG).show();
            finish();
        }

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


}
