package com.stanbicagent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.stanbicagent.R;
import com.afollestad.materialdialogs.MaterialDialog;
import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.crashlytics.android.answers.LoginEvent;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.security.ProviderInstaller;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

import io.fabric.sdk.android.Fabric;
import model.GetAirtimeBillersData;
import rest.ApiClient;
import rest.ApiInterface;
import rest.ApiSecurityClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import security.SecurityLayer;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.stanbicagent.ApplicationConstants.CHANNEL_ID;
import static com.stanbicagent.SessionManagement.KEY_ALL_USERS;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {
    Button signinn, bio;
    EditText us;
    EditText pin;
    String city = "Lagos,NG";
    EditText et4;
    TextView gm, hiagent, prvpin, opacc, succt, fpin, opacchd, succthead;
    String finlnom;
    boolean chkcard = false;
    SessionManagement session;
    ProgressDialog prgDialog, prgDialog2;
    static Hashtable<String, String> data1;
    public static final String SHAREDPREFFILE = "temp";
    public static final String USERIDPREF = "uid";
    public static final String TOKENPREF = "tkn";
    public static final String AGMOB = "agmobno";

    boolean chkv = false;
    String finlussd, finlpin, finlimei, finip, finmac;
    boolean chkbio = false;
    boolean chkbiolog = false;
    boolean isFeatureEnabled = false;
    boolean isBioLogEnabled = false;
    private boolean onReadyIdentify = false;
    private boolean onReadyEnroll = false;
    String fingindex;
    private Toolbar mToolbar;
    private TextView temp, gethelp, registeruser;
    LinearLayout linearLayoutMine;
    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket socket;
    BluetoothDevice bluetoothDevice;
    OutputStream outputStream;
    InputStream inputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;
    String value = "";
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String KEY_NAME = "my_key";
    private static final String DIALOG_FRAGMENT_TAG = "myFragment";
    private static final String SECRET_MESSAGE = "Very secret message";
    private static final String KEY_NAME_NOT_INVALIDATED = "key_not_invalidated";
    static final String DEFAULT_KEY_NAME = "default_key";
    KeyguardManager mKeyguardManager;
    FingerprintManager mFingerprintManager;
    KeyStore mKeyStore;
    ProgressDialog pro;
    KeyGenerator mKeyGenerator;
    Cipher mCipher;
    InputMethodManager mInputMethodManager;
    SharedPreferences mSharedPreferences;
    private FingerprintManager.CryptoObject cryptoObject;
    private PinLockView mPinLockView;
    private IndicatorDots mIndicatorDots;
    TextView txappvers;
    String all_users = "";
    RelativeLayout rl_multiusers;
    List<GetAirtimeBillersData> planetsList = new ArrayList<GetAirtimeBillersData>();
    ArrayAdapter<GetAirtimeBillersData> mobadapt;
    Spinner sp_att_id;
    int users_count = 0;

    String finpin;
    private PinLockListener mPinLockListener = new PinLockListener() {
        @Override
        public void onComplete(String pin) {
            finpin = pin;
        }

        @Override
        public void onEmpty() {
            SecurityLayer.Log(TAG, "Pin empty");
        }

        @Override
        public void onPinChange(int pinLength, String intermediatePin) {
            //	SecurityLayer.Log(TAG, "Pin changed, new length " + pinLength + " with intermediate pin " + intermediatePin);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Answers());
        setContentView(R.layout.signinnew);
        pro = new ProgressDialog(this);
        pro.setMessage("Loading...");
        pro.setTitle("");
        pro.setCancelable(false);

        temp = (TextView) findViewById(R.id.txt9);
        gm = (TextView) findViewById(R.id.txt);
        hiagent = (TextView) findViewById(R.id.txt2);
        prvpin = (TextView) findViewById(R.id.txt5);
        opacc = (TextView) findViewById(R.id.numbers);
        succt = (TextView) findViewById(R.id.succtrans);
        txappvers = (TextView) findViewById(R.id.versname);
        registeruser = (TextView) findViewById(R.id.text17);
        registeruser.setOnClickListener(this);
        session = new SessionManagement(getApplicationContext());
        //	datetime = (TextView) findViewById(R.id.txt10);
        String dt = getDateTimeStamp();
        //datetime.setText(dt);
        prgDialog2 = new ProgressDialog(this);
        prgDialog2.setMessage("Loading....");
        prgDialog2.setCancelable(false);
        mPinLockView = (PinLockView) findViewById(R.id.pin_lock_view);
        mPinLockView.setPinLockListener(mPinLockListener);
        mIndicatorDots = (IndicatorDots) findViewById(R.id.indicator_dots);
        mPinLockView.attachIndicatorDots(mIndicatorDots);
        mPinLockView.setPinLength(5);
        mPinLockView.setTextColor(getResources().getColor(R.color.textcolorcircle));
        gethelp = (TextView) findViewById(R.id.text13);
        gethelp.setOnClickListener(this);
        signinn = (Button) findViewById(R.id.signinn);
        signinn.setOnClickListener(this);
        updateAndroidSecurityProvider(this);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Map<String, ?> allentr = session.getAllEntries();
        for (Map.Entry<String, ?> entry : allentr.entrySet()) {
            String value = entry.getKey();
            if (value.startsWith("bllser") || value.startsWith("getbillpay")) {
                SecurityLayer.Log("remove key values ", entry.getKey() + ": " + entry.getValue().toString());
                session.removekey(entry.getKey());
            }
            session.setString("Base64image", "N");
        }
        onKeyMetric();
        //IntentPrint("\nThis is me testing this app\n Hello World, it's John Oke\n Testing Bluetooth Printing\n");
        String appvers = Utility.getAppVersion(getApplicationContext());
        txappvers.setText("Version " + appvers);
        rl_multiusers = (RelativeLayout)findViewById(R.id.rl_multiusers);
        sp_att_id = (Spinner) findViewById(R.id.sp_att_id);
        all_users = session.getString(KEY_ALL_USERS);
        if(all_users != null) {
            String[] splitString = all_users.split("\\*");
            users_count = splitString.length;
            if (users_count > 1) {
                Set_Users();
            }else{
                rl_multiusers.setVisibility(View.GONE);
            }
        }
    }

    public void Set_Users() {
        planetsList.clear();
        String[] splitString = all_users.split("\\*");
        int count = splitString.length;
        SecurityLayer.Log("count", "count == " + count);
        for (String user_n_pass : splitString) {
            String[] split_user_n_pass = user_n_pass.split(",");
            for (int i = 1; i < split_user_n_pass.length; i++){
                String uid = split_user_n_pass[0];
                SecurityLayer.Log("uid", "uid >> " + uid);
                if(!planetsList.contains(uid)) {
                    planetsList.add(new GetAirtimeBillersData(uid, uid, uid));
                }
            }
        }
        mobadapt = new ArrayAdapter<GetAirtimeBillersData>(SignInActivity.this, R.layout.my_spinner, planetsList);
        mobadapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_att_id.setAdapter(mobadapt);
        sp_att_id.setSelection(planetsList.size() - 1);
    }

    /*static {
        System.loadLibrary("F_ENTRY_LIB_Android");
    }*/
    @Override
    protected void onResume() {
        super.onResume();
    }

    public void onKeyMetric() {
        Answers.getInstance().logCustom(new CustomEvent("Login Session Start")
                .putCustomAttribute("User ID", Utility.gettUtilUserId(getApplicationContext())));
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void updateAndroidSecurityProvider(Activity callingActivity) {
        try {
            ProviderInstaller.installIfNeeded(this);
        } catch (GooglePlayServicesRepairableException e) {
            GooglePlayServicesUtil.getErrorDialog(e.getConnectionStatusCode(), callingActivity, 0);
        } catch (GooglePlayServicesNotAvailableException e) {
            SecurityLayer.Log("SecurityException", "Google Play Services not available.");
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.signinn) {
            if (Utility.checkInternetConnection(this)) {
                if (!(finpin == null)) {
                     loginRetrofit();
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter a valid pin", Toast.LENGTH_LONG).show();
                }
            }
        }
        if (v.getId() == R.id.text17) {
            startActivity(new Intent(getApplicationContext(), ActivateAgent.class));
        }
        if (v.getId() == R.id.text13) {
            startActivity(new Intent(getApplicationContext(), GetHelpActivity.class));
        }
    }

    public void forceCrash() {
        throw new RuntimeException("This is a crash");
    }

    public void LoginLog() {
        Answers.getInstance().logLogin(new LoginEvent()
                .putMethod("Digits")
                .putCustomAttribute("USER ID:", Utility.gettUtilUserId(getApplicationContext()))
                .putSuccess(true));
    }

    private void showDialog() {
        final CharSequence[] items = {"Super Agent", "Agent",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);
        builder.setTitle("");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Super Agent")) {
                    finish();
                    startActivity(new Intent(getApplicationContext(), SupAgentActivity.class));
                } else if (items[item].equals("Agent")) {
                    loginRetrofit();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public void showDialog2() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(SignInActivity.this);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                getApplicationContext(),
                android.R.layout.select_dialog_singlechoice);
        arrayAdapter.add("Superagent");
        arrayAdapter.add("Agent");

        builderSingle.setNegativeButton(
                "cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setAdapter(
                arrayAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            finish();
                            startActivity(new Intent(getApplicationContext(), SupAgentActivity.class));
                        } else if (which == 1) {
                            loginRetrofit();
                        }
                    }
                });
        builderSingle.show();
    }

    public static String getDateTimeStamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd MMM yyy ");
        Date convertedCurrentDate = new Date();
        String strDate = sdf2.format(convertedCurrentDate);
        return strDate;
    }

    public String setMobFormat(String mobno) {
        String vb = mobno.substring(Math.max(0, mobno.length() - 9));
        SecurityLayer.Log("Logged Number is", vb);
        if (vb.length() == 9 && (vb.substring(0, Math.min(mobno.length(), 1)).equals("7"))) {
            return "254" + vb;
        } else {
            return "N";
        }
    }

    public void SetDialog(String msg, String title) {
        new MaterialDialog.Builder(this)
                .title(title)
                .content(msg)
                .negativeText("Close")
                .show();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void ClearPin() {
        mPinLockView.resetPinLockView();
    }

    public void loginRetrofit() {
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        String encrypted = null;
        String pubkey = "97206B46CE46376894703ECE161F31F2";
        //encrypted = Utility.getencryptedpin(finpin, pubkey);
        encrypted = Utility.b64_sha256(finpin);
        SecurityLayer.Log("Dev Reg", "3" + "/CEVA/" + encrypted + "2347777777777/");
        String usid = "";
        if(users_count > 1) {
            session.SetUserID(sp_att_id.getSelectedItem().toString());
        }
        usid = Utility.gettUtilUserId(getApplicationContext());
        SecurityLayer.Log("usid", "usid >> " + usid);
        String mobnoo = Utility.gettUtilMobno(getApplicationContext());
        String params = CHANNEL_ID + usid + "/" + encrypted + "/" + mobnoo;
        session.setString("ENC_PIN", encrypted);
        String getchklogin = session.getString(SessionManagement.CHKLOGIN);
        if ((pro != null) && !(getApplicationContext() == null)) {
            pro.show();
        }
        if (!(getchklogin == null) && getchklogin.equals("Y")) {
            SecurityLayer.Log("GetChkLg Params", getchklogin);
            setLogout(params, encrypted);
        } else {
            LogRetro(params, encrypted);
        }
    }

    private void LogRetro(String params, final String encrypted) {
        String endpoint = "login/login.action/";
        String urlparams = "";
        try {
            urlparams = SecurityLayer.generalLogin(params, "23322", getApplicationContext(), endpoint);
            //SecurityLayer.Log("cbcurl",url);
            SecurityLayer.Log("RefURL", urlparams);
            SecurityLayer.Log("refurl", urlparams);
            SecurityLayer.Log("params", params);
        } catch (Exception e) {
            SecurityLayer.Log("encryptionerror", e.toString());
        }

        ApiInterface apiService =
                ApiSecurityClient.getClient(getApplicationContext()).create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(urlparams);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    SecurityLayer.Log("response..:", response.body());
                    JSONObject obj = new JSONObject(response.body());
                    obj = SecurityLayer.decryptGeneralLogin(obj, getApplicationContext());
                    SecurityLayer.Log("decrypted_response", obj.toString());
                    String respcode = obj.optString("responseCode");
                    String responsemessage = obj.optString("message");
                    JSONObject datas = obj.optJSONObject("data");
                    if (Utility.isNotNull(respcode) && Utility.isNotNull(responsemessage)) {
                        SecurityLayer.Log("Response Message", responsemessage);
                        if (respcode.equals("00")) {
                            SecurityLayer.Log("all_users", "saving all_users");
                            if (!(datas == null)) {
                                String status = datas.optString("status");
                                if (status.equals("F")) {
                                    finish();
                                    Intent mIntent = new Intent(getApplicationContext(), ForceResetPin.class);
                                    mIntent.putExtra("pinna", encrypted);
                                    SecurityLayer.Log("pinna >> ", encrypted);
                                    String agentid = datas.optString("agent");
                                    session.SetAgentID(agentid);
                                    startActivity(mIntent);
                                } else {
                                    session.setString(SessionManagement.CHKLOGIN, "Y");
                                    String agentid = datas.optString("agent");
                                    String userid = datas.optString("userId");
                                    String username = datas.optString("userName");
                                    String email = datas.optString("email");
                                    String lastl = datas.optString("lastLoggedIn");
                                    String mobno = datas.optString("mobileNo");
                                    String accno = datas.optString("acountNumber");
                                    String cntopen = datas.optString("canOpenAccount");
                                    session.setString(SessionManagement.KEY_SETCNTOPEN, cntopen);

                                    session.SetAgentID(agentid);
                                    session.SetUserID(userid);
                                    session.putCustName(username);
                                    session.putEmail(email);
                                    session.putLastl(lastl);
                                    session.setString(AGMOB, mobno);
                                    session.putAccountno(accno);
                                    session.SetEntryPoint("2");

                                    session.setString("Base64image", "N");
                                    session.setString(SessionManagement.KEY_SETBANKS, "N");
                                    session.setString(SessionManagement.KEY_SETBILLERS, "N");
                                    session.setString(SessionManagement.KEY_SETWALLETS, "N");
                                    session.setString(SessionManagement.KEY_SETAIRTIME, "N");
                                    session.createLoginSession();
                                    boolean checknewast = session.checkAst();
                                    LoginLog();
                                    if (checknewast == false) {
                                        finish();
                                        startActivity(new Intent(getApplicationContext(), FMobActivity.class));
                                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                    } else {
                                        finish();
                                        startActivity(new Intent(getApplicationContext(), AdActivity.class));
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(
                                    getApplicationContext(),
                                    responsemessage,
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(
                                getApplicationContext(),
                                "There was an error on your request",
                                Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    Toast.makeText(getApplicationContext(), getApplicationContext().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                }
                if ((pro != null) && pro.isShowing() && !(getApplicationContext() == null)) {
                    pro.dismiss();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                SecurityLayer.Log("Throwable error", t.toString());
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

    private void setLogout(final String lginparams, final String encrypted) {
        String endpoint = "login/logout.action";
        String usid = Utility.gettUtilUserId(getApplicationContext());
        String appid = Utility.getNewAppID(getApplicationContext());
        SecurityLayer.Log("appid", appid);
        String params = CHANNEL_ID + usid + "/" + appid;
        String urlparams = "";
        try {
            urlparams = SecurityLayer.genURLCBC(params, endpoint, getApplicationContext());
            //SecurityLayer.Log("cbcurl",url);
            SecurityLayer.Log("RefURL", urlparams);
            SecurityLayer.Log("refurl", urlparams);
            SecurityLayer.Log("params", params);
        } catch (Exception e) {
            SecurityLayer.Log("encryptionerror", e.toString());
        }

        ApiInterface apiService =
                ApiSecurityClient.getClient(getApplicationContext()).create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(urlparams);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    SecurityLayer.Log("response..:", response.body());
                    JSONObject obj = new JSONObject(response.body());
                    obj = SecurityLayer.decryptTransaction(obj, getApplicationContext());
                    SecurityLayer.Log("decrypted_response", obj.toString());
                    String respcode = obj.optString("responseCode");
                    String responsemessage = obj.optString("message");
                    if (!(response.body() == null)) {
                        if (respcode.equals("00")) {
                            SecurityLayer.Log("Response Message", responsemessage);
//                            session.setString(SessionManagement.CHKLOGIN, "N");
                        } else {
                        }
                    } else {
                    }
                } catch (JSONException e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    Toast.makeText(getApplicationContext(), getApplicationContext().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                }
                session.logoutUser();
                LogRetro(lginparams, encrypted);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                SecurityLayer.Log("Throwable error", t.toString());
                session.logoutUser();
                LogRetro(lginparams, encrypted);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pro != null && pro.isShowing()) {
            pro.dismiss();
        }
    }
}
