package com.stanbicagent;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.stanbicagent.R;
import com.afollestad.materialdialogs.MaterialDialog;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import adapter.AccountList;
import security.SecurityLayer;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.stanbicagent.ApplicationConstants.FTP_IP;
import static com.stanbicagent.ApplicationConstants.FTP_PASSWORD;
import static com.stanbicagent.ApplicationConstants.FTP_USERNAME;

public class ChangeAcNameActivity extends BaseActivity implements View.OnClickListener {
    private Toolbar mToolbar;
    TextView agentname, agemail, agphonenumb;
    CheckBox chkus, chkast, chktpin, chkbal;
    String numb;
    boolean initdisp = false;
    String upLoadServerUri = null;
    private final int CAMERA_RESULT = 1;
    private static String filePath;
    private static final String IMAGE_DIRECTORY_NAME = Constant.ROOT_FOLDER_NAME;
    private Bitmap photoBitmap;
    RelativeLayout rlem, rlid, rlno, rllast;
    CardView cvlast;
    LinearLayout lyf;
    SessionManagement session;
    List<AccountList> planetsList = new ArrayList<AccountList>();
    int serverResponseCode = 0;
    public String acc, defac;
    String uploadFilePath = null;
    String uploadFileName = null, m_Text;
    private String image;
    RecyclerView lv, lv2, lv3;
    Button myact;
    RadioButton grid, list;
    LinearLayout change_pin_img, update_pos, connct,help;
    ProgressDialog prgDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_acname);
        session = new SessionManagement(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayShowHomeEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowCustomEnabled(true);
        ab.setDisplayShowTitleEnabled(false);
        lv = (RecyclerView) findViewById(R.id.listView1);
        //cvlast = (CardView) findViewById(R.id.card_view023);
        //cvlast.setOnClickListener(this);
        lv2 = (RecyclerView) findViewById(R.id.listView2);
        lv3 = (RecyclerView) findViewById(R.id.listView3);
       /* ivgrid = (ImageView) findViewById(R.id.ivgrid);
        ivlist = (ImageView) findViewById(R.id.ivlist);
        ivgrid.setOnClickListener(this);
        ivlist.setOnClickListener(this);*/
        agentname = (TextView) findViewById(R.id.textViewnb2);
        agemail = (TextView) findViewById(R.id.textViewrrs);
        agphonenumb = (TextView) findViewById(R.id.textViewcvv);
        String txtphonenumb = Utility.gettUtilMobno(getApplicationContext());
        String txtagname = Utility.gettUtilCustname(getApplicationContext());
        String txtemail = Utility.gettUtilEmail(getApplicationContext());
        agphonenumb.setText(txtphonenumb);
        agemail.setText(txtemail);
        agentname.setText(txtagname);
        myact = (Button) findViewById(R.id.tdispedit);
        change_pin_img = (LinearLayout) findViewById(R.id.change_pin_img);
        change_pin_img.setOnClickListener(this);
        update_pos = (LinearLayout) findViewById(R.id.update_pos);
        update_pos.setOnClickListener(this);
//        connct=(LinearLayout) findViewById(R.id.connectivity);
//        connct.setOnClickListener(this);
//        help = (LinearLayout) findViewById(R.id.help);
//        help.setOnClickListener(this);
        session = new SessionManagement(getApplicationContext());
        uploadFilePath = Environment.getExternalStorageDirectory() + File.separator + "cache" + File.separator;
        uploadFileName = numb;
        uploadFilePath = Environment.getExternalStorageDirectory() + File.separator + "req_images" + File.separator;
        upLoadServerUri = "";
        prgDialog = new ProgressDialog(getApplicationContext());
        prgDialog.setCancelable(false);
    }

    public void StartChartAct(int i) {
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();    //Call the back button's method
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.change_pin_img) {
            startActivity(new Intent(getApplicationContext(), ChangePinActivity.class));
        }
        if (v.getId() == R.id.update_pos) {
            new MaterialDialog.Builder(ChangeAcNameActivity.this)
                    .title("Update POS Applications")
                    .content("Do you want to update POS application now?")
                    .positiveText("YES")
                    .negativeText("Not Now")
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            dialog.dismiss();
                            prgDialog = ProgressDialog.show(ChangeAcNameActivity.this, "", "Downloading New Update.Please Wait..",
                                    true);
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Update_Apk();
                                }
                            }, 1000);
                        }

                        @Override
                        public void onNegative(MaterialDialog dialog) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
       /* if (v.getId()== R.id.connectivity){
            startActivity(new Intent(this, SettingsActivity.class));

        }
        if (v.getId()== R.id.help){
            startActivity(new Intent(this, GetHelpActivity.class));

        }*/

        if (v.getId() == R.id.homepagead) {
            if (chkast.isChecked()) {
                session.setAst();
            } else {
                session.UnSetAst();
            }
            Toast.makeText(
                    getApplicationContext(),
                    "Settings Applied Successfully",
                    Toast.LENGTH_LONG).show();
        }
        if (v.getId() == R.id.shwbal) {
            if (chkbal.isChecked()) {
                session.UnSetShwBal();
            } else {
                session.setShwbal();
            }
            Toast.makeText(
                    getApplicationContext(),
                    "Settings Applied Successfully",
                    Toast.LENGTH_LONG).show();
        }
        if (v.getId() == R.id.distpin) {
            if (chktpin.isChecked()) {
                session.setTpinPref();
            } else {
                session.UnSetTpinPref();
            }
            Toast.makeText(
                    getApplicationContext(),
                    "Settings Applied Successfully",
                    Toast.LENGTH_LONG).show();
        }
        if (v.getId() == R.id.chkus) {
            if (chkus.isChecked()) {
                session.setUser();
            } else {
                session.UnSetUser();
            }
            Toast.makeText(
                    getApplicationContext(),
                    "Settings Applied Successfully",
                    Toast.LENGTH_LONG).show();
        }
        if (v.getId() == R.id.tdispedit) {
        }
    }




    public void SetDialog(String msg, String title) {
        new MaterialDialog.Builder(this)
                .title(title)
                .content(msg)
                .negativeText("Close")
                .show();
    }

    public void Update_Apk() {
        String apk_name = "accessmf.apk";
        String pathFolder = Environment.getExternalStorageDirectory() + "/access";
        String pathFile = pathFolder + "/" + apk_name;
        File localFile = new File(pathFile);
        if (localFile.exists()) {
            SecurityLayer.Log("localFile.exists", "pathFolder > " + localFile);
            localFile.delete();
        }
        File localFile_pathFile = new File(pathFolder);
        if (!localFile_pathFile.exists()) {
            SecurityLayer.Log("localFile_pathFile", "Creating folder " + pathFolder);
            localFile_pathFile.mkdir();
        }
        try {
            if (downloadAndSaveFile(FTP_IP, 21, FTP_USERNAME, FTP_PASSWORD, "ftp/" + apk_name, localFile)) {
                SecurityLayer.Log("downloadAndSaveFile", "downloadAndSaveFile successfull");
            } else {
                prgDialog.cancel();
                SecurityLayer.Log("downloadAndSaveFile", "Unable to download app update!!");
            }
        } catch (IOException e) {
            prgDialog.cancel();
            e.printStackTrace();
        }
        if (localFile.exists()) {
            int file_size = Integer.parseInt(String.valueOf(localFile.length() / 1024));
            SecurityLayer.Log("ftp download", "ftp download successful = " + localFile + " Size = " + file_size);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(localFile), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this flag android returned a intent error!
            startActivity(intent);
            prgDialog.cancel();
        }
    }

    private Boolean downloadAndSaveFile(String server, int portNumber,
                                        String user, String password, String filename, File localFile)
            throws IOException {
        FTPClient ftp = null;
        try {
            ftp = new FTPClient();
            ftp.connect(server, portNumber);
            SecurityLayer.Log("FTPClient", "Connected. Reply: " + ftp.getReplyString());
            ftp.login(user, password);
            SecurityLayer.Log("FTPClient", "Logged in");
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            SecurityLayer.Log("FTPClient", "Downloading..");
            ftp.enterLocalPassiveMode();
            OutputStream outputStream = null;
            boolean success = false;
            try {
                outputStream = new BufferedOutputStream(new FileOutputStream(
                        localFile));
                success = ftp.retrieveFile(filename, outputStream);
            } finally {
                if (outputStream != null) {
                    outputStream.close();
                }
            }
            return success;
        } finally {
            if (ftp != null) {
                //ftp.logout();
                //ftp.disconnect();
            }
        }
    }


}
