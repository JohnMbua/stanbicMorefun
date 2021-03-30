package com.stanbicagent;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.stanbicagent.R;
import com.afollestad.materialdialogs.MaterialDialog;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;

import rest.ApiInterface;
import rest.ApiSecurityClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import security.SecurityLayer;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.stanbicagent.ApplicationConstants.CHANNEL_ID;

public class OpenAccStepTwoActivity extends BaseActivity implements View.OnClickListener {
    File finalFile;
    int REQUEST_CAMERA = 3293;
    Button sigin;
    TextView gendisp;
    SessionManagement session;
    EditText idno, mobno, fnam, lnam, yob;
    List<String> planetsList = new ArrayList<String>();
    List<String> prodid = new ArrayList<String>();
    ArrayAdapter<String> mArrayAdapter;

    Button btn4;
    TextView step2;
    static Hashtable<String, String> data1;
    String paramdata = "";
    String strmobn;
    ProgressDialog prgDialog, prgDialog2, prgDialog7;
    int fcsize = 0;
    List<String> mobopname = new ArrayList<String>();
    List<String> mobopid = new ArrayList<String>();

    TextView tvdate;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    String mCurrentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 1;
    String strfname, strlname, stryob, stremail, strhmadd, strsalut, strmno, bvn, strgender;
    EditText edemail, edmobno, edhm, edstraddr;
    public static final String DATEPICKER_TAG = "datepicker";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_acc_step_two);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get the ActionBar here to configure the way it behaves.
        final ActionBar ab = getSupportActionBar();
        //ab.setHomeAsUpIndicator(R.drawable.ic_menu); // set a custom icon for the default home button
        ab.setDisplayShowHomeEnabled(true); // show or hide the default home button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
        ab.setDisplayShowTitleEnabled(false); // disable the default title element here (for centered title)


        sigin = (Button) findViewById(R.id.button1);
        sigin.setOnClickListener(this);


        session = new SessionManagement(this);


        edemail = (EditText) findViewById(R.id.email);
        edhm = (EditText) findViewById(R.id.hmaddr);
        edmobno = (EditText) findViewById(R.id.mobno);


        step2 = (TextView) findViewById(R.id.tv2);
        step2.setOnClickListener(this);


        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Loading....");
        prgDialog.setCancelable(false);


        Intent intent = getIntent();
        if (intent != null) {


            strfname = intent.getStringExtra("fname");

            strlname = intent.getStringExtra("lname");
            strgender = intent.getStringExtra("gender");
            stryob = intent.getStringExtra("yob");
            bvn = intent.getStringExtra("bvn");

            stremail = intent.getStringExtra("email");
            strhmadd = intent.getStringExtra("hmadd");
            strsalut = intent.getStringExtra("salut");

            strmno = intent.getStringExtra("mobn");
            edemail.setText(stremail);
            edhm.setText(strhmadd);
            edmobno.setText(strmno);


        }


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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CAMERA) {
            onCaptureImageResult(data);
        }
    }


    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float neww = ((float) width) * ((float) 0.6);
        float newh = ((float) height) * ((float) 0.6);
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
                matrix, false);

        return resizedBitmap;
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = getResizedBitmap((Bitmap) data.getExtras().get("data"), 300, 300);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        String filename = System.currentTimeMillis() + ".jpg";
        finalFile = new File(Environment.getExternalStorageDirectory(), filename);
        FileOutputStream fo;
        try {
            finalFile.createNewFile();
            fo = new FileOutputStream(finalFile);
            fo.write(bytes.toByteArray());
            fo.close();
            SecurityLayer.Log("Filename stored", filename);
            String filePath = finalFile.getPath();
            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
           /* new Thread(new Runnable() {
                public void run() {


                    uploadFile(finalFile);

                }
            }).start();*/
            //    new FragmentDrawer.AsyncUplImg().execute();
           /* getApplicationContext().finish();
            Toast.makeText(
                    getApplicationContext(),
                    "Image Set Successfully",
                    Toast.LENGTH_LONG).show();
            startActivity(new Intent(getApplicationContext(),FMobActivity.class));*/
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //   iv.setImageBitmap(thumbnail);


    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button1) {
            Bundle bundle = new Bundle();
            strmobn = edmobno.getText().toString();
            String strhmadd = edhm.getText().toString();
            stremail = edemail.getText().toString();


            boolean boolemail = false;
            if (Utility.isNotNull(stremail)) {
                if (Utility.validate(stremail)) {
                    boolemail = true;
                } else {
                    boolemail = false;
                }
            } else {
                boolemail = false;
            }
            if (Utility.checkInternetConnection(getApplicationContext())) {
                if (Utility.isNotNull(strmobn)) {


                    if (strmobn.length() > 9) {

                        if (boolemail) {

                            if (!(Utility.isNotNull(stremail)) || stremail.equals("")) {
                                stremail = "NA";
                            }

                            strmobn = "0" + strmobn.substring(strmobn.length() - 10);
                            String usid = Utility.gettUtilUserId(getApplicationContext());
                            String agentid = Utility.gettUtilAgentId(getApplicationContext());
                            String params = CHANNEL_ID + usid + "/" + agentid + "/" + strmobn;

                            GenCustOTP(params);

                                      /*
                                        Fragment fragment = new OpenAccUpPic();
                                        fragment.setArguments(bundle);

                                        FragmentManager fragmentManager = getFragmentManager();
                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                        //  String tag = Integer.toString(title);
                                        fragmentTransaction.replace(R.id.container_body, fragment, "Step Three");
                                        fragmentTransaction.addToBackStack("Step Three");
                                        ((FMobActivity) getApplicationContext())
                                                .setActionBarTitle("Step Three");
                                        fragmentTransaction.commit(); */


                        } else {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Please enter a valid email value",
                                    Toast.LENGTH_LONG).show();
                        }

                    } else {
                        Toast.makeText(
                                getApplicationContext(),
                                "Please enter a valid value for mobile number of proper length",
                                Toast.LENGTH_LONG).show();
                    }


                } else {
                    Toast.makeText(
                            getApplicationContext(),
                            "Please enter a value for mobile Number",
                            Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(
                        getApplicationContext(),
                        "Please enable internet connection",
                        Toast.LENGTH_LONG).show();
            }

        }
        if (view.getId() == R.id.tv2) {
           /* Bundle bundle = new Bundle();
            bundle.putString("fname", strfname);
            bundle.putString("lname", strlname);

            bundle.putString("yob", stryob);



            Fragment  fragment = new OpenAcc();

            fragment.setArguments(bundle);
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment,"Biller Menu");
            fragmentTransaction.addToBackStack("Biller Menu");
            ((FMobActivity)getApplicationContext())
                    .setActionBarTitle("Biller Menu");
            fragmentTransaction.commit();*/

            finish();
            Intent intent = new Intent(OpenAccStepTwoActivity.this, OpenAccActivity.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);
        }


        if (view.getId() == R.id.button4) {

        }
        if (view.getId() == R.id.tdispedit) {

          /*  Fragment fragment =  new NatWebProd();;
String title = "Bank Info";
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment, title);
            fragmentTransaction.addToBackStack(title);
            fragmentTransaction.commit();
            Activity activity123 = getApplicationContext();
            if(activity123 instanceof MainActivity) {
                ((MainActivity)getApplicationContext())
                        .setActionBarTitle(title);
            }
            if(activity123 instanceof SignInActivity) {
                ((SignInActivity) getApplicationContext())
                        .setActionBarTitle(title);
            }*/
        }

        if (view.getId() == R.id.textView3) {


        }
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void invokeWS(String acctype, String msisdn, String id, String fname, String lname, String yearob) {
        // Show Progress Dialog
        prgDialog.show();

        // Make RESTful webservice call using AsyncHttpClient object
        final AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(35000);
        HashMap<String, String> nurl = session.getNetURL();
        String newurl = nurl.get(SessionManagement.NETWORK_URL);
        client.setURLEncodingEnabled(true);

        HashMap<String, String> stuse = session.getDisp();
        String username = stuse.get(SessionManagement.KEY_DISP);
        String url = ApplicationConstants.NET_URL + ApplicationConstants.AND_ENPOINT + "agencyopenAccount/1/01261/" + msisdn + "/1/" + id + "/" + fname + "/" + lname + "/" + yearob + "/ANDROID/" + username;

        SecurityLayer.Log("Open Acc URL", url);

        client.post(url, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {
                // Hide Progress Dialog
                prgDialog.hide();
                try {
                    // JSON Object
                    SecurityLayer.Log("response..:", response);
                    JSONObject obj = new JSONObject(response);


                    String rpcode = obj.optString("responsecode");
                    String rsmesaage = obj.optString("responsemessage");
                    String fname = obj.optString("fullname");
                    String mno = obj.optString("mobilenumber");
                    SecurityLayer.Log("Response Code", rsmesaage);
                    if (rpcode.equals("00")) {

                    } else {


                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    SetDialog(" The device has not successfully connected to server. Please check your internet settings", "Check Settings");
                    e.printStackTrace();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error,
                                  String content) {

                // Hide Progress Dialog
                prgDialog.hide();
                // When Http response code is '404'
                if (statusCode == 404) {
                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    SetDialog(" The device has not successfully connected to server. Please check your internet settings", "Check Settings");
                }
            }
        });
    }


    public void ClearOpenAcc() {
        //   sp1.setSelection(0);

        mobno.setText(" ");
        idno.setText(" ");
        fnam.setText(" ");
        lnam.setText(" ");
        //   yob.setText(" ");
    }


    public void SetDialog(String msg, String title) {
        new MaterialDialog.Builder(getApplicationContext())
                .title(title)
                .content(msg)

                .negativeText("Close")
                .show();
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

    private void CallOTP() {

        String endpoint = "otp/generatecustomerotp.action";

        prgDialog.show();
        String usid = Utility.gettUtilUserId(getApplicationContext());
        String agentid = Utility.gettUtilAgentId(getApplicationContext());
        String mobnoo = Utility.gettUtilMobno(getApplicationContext());
        String params = CHANNEL_ID + usid + "/" + agentid + "/" + mobnoo;
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
                    // JSON Object

                    SecurityLayer.Log("response..:", response.body());
                    JSONObject obj = new JSONObject(response.body());
                    //obj = Utility.onresp(obj,getApplicationContext());
                    obj = SecurityLayer.decryptTransaction(obj, getApplicationContext());
                    SecurityLayer.Log("decrypted_response", obj.toString());

                    String respcode = obj.optString("responseCode");
                    String responsemessage = obj.optString("message");


                    JSONArray plan = obj.optJSONArray("data");
                    //session.setString(SecurityLayer.KEY_APP_ID,appid);


                    if (!(response.body() == null)) {
                        if (respcode.equals("00")) {

                            Bundle bundle = new Bundle();
                            String strmobn = edmobno.getText().toString();
                            String strhmadd = edhm.getText().toString();
                            stremail = edemail.getText().toString();

                            if (Utility.checkInternetConnection(getApplicationContext())) {
                                if (Utility.isNotNull(strmobn)) {
                                    if (Utility.isNotNull(strhmadd)) {

                                        if (Utility.isNotNull(stremail)) {



                                            /*bundle.putString("fname", strfname);
                                            bundle.putString("lname", strlname);

                                            bundle.putString("yob", stryob);



                                            bundle.putString("email", stremail);
                                            bundle.putString("hmadd", strhmadd);
                                            bundle.putString("mobn", strmobn);
                                            bundle.putString("salut", salut);
                                            bundle.putString("marstatus", marstatus);
                                            Fragment fragment = new OpenAccUpPic();
                                            fragment.setArguments(bundle);

                                            FragmentManager fragmentManager = getFragmentManager();
                                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                            //  String tag = Integer.toString(title);
                                            fragmentTransaction.replace(R.id.container_body, fragment, "Step Three");
                                            fragmentTransaction.addToBackStack("Step Three");
                                            ((FMobActivity) getApplicationContext())
                                                    .setActionBarTitle("Step Three");
                                            fragmentTransaction.commit();*/

                                        } else {
                                            Toast.makeText(
                                                    getApplicationContext(),
                                                    "Please enter a value for Email",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    } else {
                                        Toast.makeText(
                                                getApplicationContext(),
                                                "Please enter a value for Home Address",
                                                Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(
                                            getApplicationContext(),
                                            "Please enter a value for mobile Number",
                                            Toast.LENGTH_LONG).show();
                                }

                            } else {
                                Toast.makeText(
                                        getApplicationContext(),
                                        "Please enable internet connection",
                                        Toast.LENGTH_LONG).show();
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
                                "There was an error processing your request ",
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
                prgDialog.dismiss();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                SecurityLayer.Log("Throwable error", t.toString());

                Toast.makeText(
                        getApplicationContext(),
                        "There was an error processing your request",
                        Toast.LENGTH_LONG).show();
                prgDialog.dismiss();
            }
        });

    }

    private void GenCustOTP(final String params) {

        prgDialog.show();

        String sessid = UUID.randomUUID().toString();


        String endpoint = "otp/generatecustomerotp.action";

        String url = "";
        try {
            url = SecurityLayer.genURLCBC(params, endpoint, getApplicationContext());
            SecurityLayer.Log("cbcurl", url);
            SecurityLayer.Log("params", params);
            SecurityLayer.Log("refurl", url);
        } catch (Exception e) {
            SecurityLayer.Log("encryptionerror", e.toString());
        }

        ApiInterface apiService =
                ApiSecurityClient.getClient(getApplicationContext()).create(ApiInterface.class);


        Call<String> call = apiService.setGenericRequestRaw(url);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    SecurityLayer.Log("response..:", response.body());


                    JSONObject obj = new JSONObject(response.body());
                 /*   JSONObject jsdatarsp = obj.optJSONObject("data");
                    SecurityLayer.Log("JSdata resp", jsdatarsp.toString());
                    //obj = Utility.onresp(obj,getApplicationContext()); */
                    obj = SecurityLayer.decryptTransaction(obj, getApplicationContext());
                    SecurityLayer.Log("decrypted_response", obj.toString());

                    String respcode = obj.optString("responseCode");
                    String responsemessage = obj.optString("message");


                    if (!(response.body() == null)) {
                        if (Utility.isNotNull(respcode) && Utility.isNotNull(respcode)) {
                            if (!(Utility.checkUserLocked(respcode))) {
                                if (respcode.equals("00")) {

                                    SecurityLayer.Log("Response Message", responsemessage);
                            /*Bundle bundle = new Bundle();
                            bundle.putString("fname", strfname);
                            bundle.putString("lname", strlname);
                            bundle.putString("midname", strmidnm);
                            bundle.putString("yob", stryob);
                            bundle.putString("email", stremail);
                            bundle.putString("hmadd", strhmdd);
                            bundle.putString("mobn", strmobn);
                            bundle.putString("salut", strsalut);
                            bundle.putString("marstatus", strmarst);
                            bundle.putString("gender", strgender);
                            bundle.putString("city", strcity);
                            bundle.putString("state", strstate);

                            Fragment fragment = new OpenAccOTP();
                            fragment.setArguments(bundle);


                            FragmentManager fragmentManager = getFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            //  String tag = Integer.toString(title);
                            fragmentTransaction.replace(R.id.container_body, fragment,"Step Four");
                            fragmentTransaction.addToBackStack("Step Four");
                            ((FMobActivity)getApplicationContext())
                                    .setActionBarTitle("Step Four");
                            fragmentTransaction.commit();*/

                                    Intent intent = new Intent(OpenAccStepTwoActivity.this, OpenAccOTPActivity.class);


                                    intent.putExtra("fname", strfname);
                                    intent.putExtra("lname", strlname);

                                    intent.putExtra("yob", stryob);

                                    intent.putExtra("gender", strgender);


                                    intent.putExtra("email", stremail);
                                    intent.putExtra("hmadd", bvn);
                                    intent.putExtra("mobn", strmobn);


                                    startActivity(intent);


                                } else {
                                    Toast.makeText(
                                            getApplicationContext(),
                                            responsemessage,
                                            Toast.LENGTH_LONG).show();
                                }
                            } else {
                                LogOut();
                            }

                        } else {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "There was an error processing your request ",
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(
                                getApplicationContext(),
                                "There was an error processing your request ",
                                Toast.LENGTH_LONG).show();
                    }


                } catch (JSONException e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    // TODO Auto-generated catch block
                    if (!(getApplicationContext() == null)) {
                        Toast.makeText(getApplicationContext(), getApplicationContext().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                        // SecurityLayer.Log(e.toString());
                        SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getApplicationContext());
                    }

                } catch (Exception e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    if (!(getApplicationContext() == null)) {
                        Toast.makeText(getApplicationContext(), getApplicationContext().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                        // SecurityLayer.Log(e.toString());
                        SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getApplicationContext());
                    }
                    // SecurityLayer.Log(e.toString());
                }
                prgDialog.dismiss();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                SecurityLayer.Log("Throwable error", t.toString());

                if (!(getApplicationContext() == null)) {
                    Toast.makeText(
                            getApplicationContext(),
                            "There was an error processing your request",
                            Toast.LENGTH_LONG).show();
                    // SecurityLayer.Log(e.toString());
                    SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getApplicationContext());
                }
                prgDialog.dismiss();
            }
        });

    }


    public void SetForceOutDialog(String msg, final String title, final Context c) {
        if (!(c == null)) {
            new MaterialDialog.Builder(this)
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
                            finish();
                            session.logoutUser();

                            // After logout redirect user to Loing Activity
                            Intent i = new Intent(c, SignInActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            // Staring Login Activity
                            startActivity(i);

                        }
                    })
                    .show();
        }
    }


}
