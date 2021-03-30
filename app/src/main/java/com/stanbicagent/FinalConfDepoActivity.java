package com.stanbicagent;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.stanbicagent.R;
import com.morefun.yapi.device.printer.FontFamily;
import com.morefun.yapi.device.printer.OnPrintListener;
import com.morefun.yapi.device.printer.PrinterConfig;
import com.morefun.yapi.engine.DeviceServiceEngine;
import com.vipul.hp_hp.library.Layout_to_Image;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import security.SecurityLayer;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import utils.FileUtils;

import static com.stanbicagent.ApplicationConstants.album_folder_name;

public class FinalConfDepoActivity extends BaseActivity implements View.OnClickListener {
    TextView recacno, recname, recamo, recnarr, recsendnum, recsendnam, recfee, recagcmn, txtrfcd, recdatetimee;
    Button btnsub;
    String recanno, amou, narra, ednamee, ednumbb, txtname, strfee, stragcms;
    ProgressDialog prgDialog, prgDialog2;
    RelativeLayout rlsendname, rlsendno;
    EditText etpin;
    String txtrfc;
    Layout_to_Image layout_to_image;  //Create Object of Layout_to_Image Class
    LinearLayout relativeLayout;   //Define Any Layout
    Button shareImage, repissue;
    Bitmap bitmap;                  //Bitmap for holding Image of layout
    RelativeLayout rlagfee, rlaccom, rlprint;
    RelativeLayout rlsave, rlshare;
    SharedPreferences pref;
    String tid, mid, mer_name, loc, ccode, user_only, fld_43, totfee, send_date, send_time, trantype, rrn, auth_id, stan, batch, appvers = "",tntp;
    int check = 0;
    String mTypeFacePath;
    DeviceServiceEngine mSDKManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_conf_depo_trans);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SDKManager.getInstance().bindService(getApplicationContext());
        mTypeFacePath = initTypeFacePath();
        appvers = Utility.getAppVersion(getApplicationContext());
        recacno = (TextView) findViewById(R.id.textViewnb2);
        txtrfcd = (TextView) findViewById(R.id.txtrfcd);
        recname = (TextView) findViewById(R.id.textViewcvv);
        etpin = (EditText) findViewById(R.id.pin);
        recamo = (TextView) findViewById(R.id.textViewrrs);
        recnarr = (TextView) findViewById(R.id.textViewrr);
        recfee = (TextView) findViewById(R.id.txtfee);
        recagcmn = (TextView) findViewById(R.id.txtaccom);
        recsendnam = (TextView) findViewById(R.id.sendnammm);
        recsendnum = (TextView) findViewById(R.id.sendno);
        recdatetimee = (TextView) findViewById(R.id.textfinaldatet);
        shareImage = (Button) findViewById(R.id.share_image);
        shareImage.setOnClickListener(this);
        repissue = (Button) findViewById(R.id.reportiss);
        repissue.setOnClickListener(this);
        rlagfee = (RelativeLayout) findViewById(R.id.rlagfee);
        rlaccom = (RelativeLayout) findViewById(R.id.rlaccom);
        rlsave = (RelativeLayout) findViewById(R.id.rlsave);
        rlshare = (RelativeLayout) findViewById(R.id.rlshare);
        rlprint = (RelativeLayout) findViewById(R.id.rlprint);

        rlsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rlaccom.setVisibility(View.GONE);
                rlagfee.setVisibility(View.GONE);
                layout_to_image = new Layout_to_Image(getApplicationContext(), relativeLayout);
                //now call the main working function ;) and hold the returned image in bitmap
                bitmap = layout_to_image.convert_layout();
                String filename = "ShareRec" + System.currentTimeMillis() + ".jpg";
                if (Utility.checkPermission(FinalConfDepoActivity.this)) {
                    saveImage(bitmap, filename);
                    Toast.makeText(
                            getApplicationContext(),
                            "Receipt downloaded successfully to gallery",
                            Toast.LENGTH_LONG).show();
                }
                rlaccom.setVisibility(View.VISIBLE);
                rlagfee.setVisibility(View.VISIBLE);
            }
        });

        rlshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rlaccom.setVisibility(View.GONE);
                rlagfee.setVisibility(View.GONE);
                layout_to_image = new Layout_to_Image(getApplicationContext(), relativeLayout);
                bitmap = layout_to_image.convert_layout();
                if (Utility.checkWriteStoragePermission(FinalConfDepoActivity.this)) {
                    if (!(getApplicationContext() == null)) {
                        shareImage(getImageUri(bitmap));
                    }
                }
                rlaccom.setVisibility(View.VISIBLE);
                rlagfee.setVisibility(View.VISIBLE);
            }
        });

        rlprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSDKManager = SDKManager.getInstance().getDeviceServiceEngine();
                if (mSDKManager == null) {
                    SecurityLayer.Log("mSDKManager", "ServiceEngine is Null");
                    return;
                }
                Check_Printer();
            }
        });

        prgDialog2 = new ProgressDialog(this);
        prgDialog2.setMessage("Loading....");
        prgDialog2.setCancelable(false);
        rlsendname = (RelativeLayout) findViewById(R.id.rlsendnam);
        rlsendno = (RelativeLayout) findViewById(R.id.rlsendnum);
        btnsub = (Button) findViewById(R.id.button2);
        btnsub.setOnClickListener(this);
        relativeLayout = (LinearLayout) findViewById(R.id.receipt);
        Intent intent = getIntent();
        if (intent != null) {
            recanno = intent.getStringExtra("recanno");
            amou = intent.getStringExtra("amou");
            narra = intent.getStringExtra("narra");
            ednamee = intent.getStringExtra("ednamee");
            ednumbb = intent.getStringExtra("ednumbb");
            txtname = intent.getStringExtra("txtname");
            txtrfc = intent.getStringExtra("refcode");
            rrn = intent.getStringExtra("rrn");
            auth_id = intent.getStringExtra("auth_id");
            String redatetim = intent.getStringExtra("datetime");
            recdatetimee.setText(Utility.changeDate(redatetim));
            txtrfcd.setText(txtrfc);
            stragcms = Utility.returnNumberFormat(intent.getStringExtra("agcmsn"));
            trantype = intent.getStringExtra("trantype");
            strfee = intent.getStringExtra("fee");
            if (trantype.equals("D")) {
                rlsendname.setVisibility(View.GONE);
                rlsendno.setVisibility(View.GONE);
            }
            recacno.setText(recanno);
            recname.setText(txtname);
            recamo.setText(ApplicationConstants.KEY_NAIRA + amou);
            recnarr.setText(narra);
            recfee.setText(ApplicationConstants.KEY_NAIRA + strfee);
            recsendnam.setText(ednamee);
            recsendnum.setText(ednumbb);
            recagcmn.setText(ApplicationConstants.KEY_NAIRA + stragcms);
        }

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        user_only = pref.getString("user_only", "");
        tid = pref.getString("tid", "");
        mid = pref.getString("mid", "");
        mer_name = pref.getString("mer_name", "");
        loc = pref.getString("loc", "");
        ccode = pref.getString("ccode", "");
        fld_43 = pref.getString("fld_43", "");

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat sdate_df = new SimpleDateFormat("dd MMM yyyy");
        SimpleDateFormat stime_df = new SimpleDateFormat("HH:mm");
        SimpleDateFormat stan_df = new SimpleDateFormat("ddHHmm");
        send_date = sdate_df.format(c);
        send_time = stime_df.format(c);
        stan = stan_df.format(c);
        batch = stan_df.format(c);
    }

    private void shareImage(Uri imagePath) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        sharingIntent.setType("image/*");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, imagePath);
        startActivity(Intent.createChooser(sharingIntent, "Share Image Using"));
    }

    public void StartChartAct(int i) {
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), FMobActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
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
    public void onClick(View view) {
        if (view.getId() == R.id.button2) {
            finish();
            Intent i = new Intent(FinalConfDepoActivity.this, FMobActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }
        if (view.getId() == R.id.share_image) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("SELECT AN OPTION");
            // add a list
            String[] animals = {"Save to Gallery", "Share Receipt"};
            builder.setItems(animals, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0: // horse
                            rlaccom.setVisibility(View.GONE);
                            rlagfee.setVisibility(View.GONE);
                            layout_to_image = new Layout_to_Image(getApplicationContext(), relativeLayout);
                            //now call the main working function ;) and hold the returned image in bitmap
                            bitmap = layout_to_image.convert_layout();
                            String filename = "ShareRec" + System.currentTimeMillis() + ".jpg";
                            if (Utility.checkPermission(FinalConfDepoActivity.this)) {
                                saveImage(bitmap, filename);
                                Toast.makeText(
                                        getApplicationContext(),
                                        "Receipt downloaded successfully to gallery",
                                        Toast.LENGTH_LONG).show();
                            }
                            break;
                        case 1: // cow
                            rlaccom.setVisibility(View.GONE);
                            rlagfee.setVisibility(View.GONE);
                            layout_to_image = new Layout_to_Image(getApplicationContext(), relativeLayout);
                            //now call the main working function ;) and hold the returned image in bitmap
                            bitmap = layout_to_image.convert_layout();
                            if (Utility.checkPermission(FinalConfDepoActivity.this)) {
                                if (!(getApplicationContext() == null)) {
                                    shareImage(getImageUri(bitmap));
                                }
                            }
                            rlaccom.setVisibility(View.VISIBLE);
                            rlagfee.setVisibility(View.VISIBLE);
                            break;
                    }
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

        }
    }

    private Uri getImageUri(Bitmap image) {
        Uri uri = null;
        try {
            File file = new File(getAlbumStorageDir(album_folder_name), "to-share.png");
            FileOutputStream stream = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 90, stream);
            stream.close();
            uri = Uri.fromFile(file);
        } catch (IOException e) {
            Log.d("TAG", "IOException while trying to write file for sharing: " + e.getMessage());
        }
        return uri;
    }

    /*public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }*/

    public void Check_Printer() {
        try {
            com.morefun.yapi.device.printer.Printer printer = mSDKManager.getPrinter();
            if (printer.getStatus() != 0) {
                check = -1;
            } else {
                check = 0;
            }
            After_Printer_Check();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void After_Printer_Check() throws RemoteException {
        if (check == 0) {
            Print_Cash_Deposit_Receipt();
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(
                            getApplicationContext(),
                            "Please insert paper receipt in order to print.",
                            Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void Print_Cash_Deposit_Receipt() throws RemoteException {
        com.morefun.yapi.device.printer.Printer printer = mSDKManager.getPrinter();
        try {
            recanno = maskString(recanno, 0, 6, '*');
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (printer.getStatus() != 0) {
                SecurityLayer.Log("getStatus", "getStatus = " + (R.string.msg_nopaper));
                return;
            }
            if (printer.initPrinter() != 0) {
                return;
            }

            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(getApplicationContext().getAssets().open("receipt_logo.bmp"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            printer.appendImage(bitmap);
            int fontSize = FontFamily.MIDDLE;
            Bundle bundle = new Bundle();
            if (!TextUtils.isEmpty(mTypeFacePath)) {
                bundle.putString(PrinterConfig.COMMON_TYPEFACE, mTypeFacePath);
            }
            bundle.putInt(PrinterConfig.COMMON_GRAYLEVEL, 15);
            printer.setConfig(bundle);

            printer.appendPrnStr("AGENT: " + mer_name, fontSize, false);
            printer.appendPrnStr("LOC: " + loc, fontSize, false);
            printer.appendPrnStr("ACC NO: " + recanno, fontSize, false);
            printer.appendPrnStr("NAME: " + txtname, fontSize, false);
            printer.appendPrnStr("TID: " + tid + "  MID: " + mid, fontSize, false);
            printer.appendPrnStr("DATE: " + send_date + "    TIME: " + send_time, fontSize, false);
            printer.appendPrnStr("-----------------------------------------------------", fontSize, false);
//            if (trantype.equals("D")) {
                printer.appendPrnStr("CASH DEPOSIT", fontSize, false);
                tntp="CASH DEPOSIT";
//            }else if (trantype.equals("T")) {
//                printer.appendPrnStr("FUNDS TRANSFER", fontSize, false);
//                tntp="FUNDS TRANSFER";
//            }

            fontSize = FontFamily.BIG;
            printer.appendPrnStr("TOTAL: KES " + amou, fontSize, false);
            fontSize = FontFamily.MIDDLE;
            printer.appendPrnStr("Narration: " + narra, fontSize, false);
//            if (trantype.equals("T")) {
                printer.appendPrnStr("Sender Phone: " + ednumbb, fontSize, false);
                printer.appendPrnStr("Sender Name: " + ednamee, fontSize, false);
//            }
            fontSize = FontFamily.BIG;
            printer.appendPrnStr("     APPROVED", fontSize, false);
            fontSize = FontFamily.MIDDLE;
            printer.appendPrnStr("-----------------------------------------------------", fontSize, false);
            printer.appendPrnStr("Response Code: " + "00", fontSize, false);
            printer.appendPrnStr("Ref ID: " + rrn, fontSize, false);
            printer.appendPrnStr("Auth: " + auth_id, fontSize, false);
            printer.appendPrnStr("App Version: " + appvers, fontSize, false);
            printer.appendPrnStr("-----------------------------------------------------", fontSize, false);
            printer.appendPrnStr("SERVED BY: " + Utility.gettUtilUserId(getApplicationContext()), fontSize, false);
            printer.appendPrnStr("**** CUSTOMER COPY ****", fontSize, false);
            printer.appendPrnStr("\n\n", fontSize, false);
            printer.startPrint(new OnPrintListener.Stub() {
                @Override
                public void onPrintResult(int ret) throws RemoteException {
                    Log.d("onPrintResult", "onPrintResult = " + ret);
                    //listener.showMessage(ret == ServiceResult.Success ? context.getString(R.string.msg_succ) : context.getString(R.string.msg_fail));
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }
//        ApplicationClass.get().getGlobalVals().setReprintData(tntp + "^" + "" + "^" + amou + "^" +  recanno + "^" + rrn + "^" + "00" + "^" + narra + "^"+""+ "^" + auth_id + "^" + ""+"^"+""+"^"+""+"^"+""+"^"+loc);
//        ApplicationClass.get().getGlobalVals().setReprintData("CASH DEPOSIT" + "^" + recanno + "^" + amou + "^" +  txtname + "^" + rrn + "^" + rpc_code + "^" + narra + "^"+auth_id+ "^" +rpc_message+"^"+ loc);

    }

    private static String maskString(String strText, int start, int end, char maskChar)
            throws Exception {
        if (strText == null || strText.equals(""))
            return "";

        if (start < 0)
            start = 0;

        if (end > strText.length())
            end = strText.length();

        if (start > end)
            throw new Exception("End index cannot be greater than start index");

        int maskLength = end - start;

        if (maskLength == 0)
            return strText;

        StringBuilder sbMaskString = new StringBuilder(maskLength);

        for (int i = 0; i < maskLength; i++) {
            sbMaskString.append(maskChar);
        }

        return strText.substring(0, start)
                + sbMaskString.toString()
                + strText.substring(start + maskLength);
    }

    public String initTypeFacePath() {
        String namePath = "arial";
        String mTypefacePath = null;
        if (Build.VERSION.SDK_INT >= 23) {
            mTypefacePath = FileUtils.getExternalCacheDir(FinalConfDepoActivity.this, namePath + ".ttf");
        } else {
            String filePath = FileUtils.createTmpDir(FinalConfDepoActivity.this);
            SecurityLayer.Log("initTypeFacePath", "filePath = " + filePath);
            mTypefacePath = filePath + namePath + ".ttf";
        }
        SecurityLayer.Log("initTypeFacePath", "mTypefacePath = " + mTypefacePath);
        try {
            FileUtils.copyFromAssets(FinalConfDepoActivity.this.getAssets(), namePath, mTypefacePath, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SecurityLayer.Log("initTypeFacePath", "mTypefacePath = " + mTypefacePath);
        return mTypefacePath;
    }

    @Override
    public void onResume() {
        super.onResume();
        // put your code here...
    }

    private Uri saveImageExternal(Bitmap image) {
        //TODO - Should be processed in another thread
        Uri uri = null;
        try {
            File file = new File(getAlbumStorageDir(album_folder_name), "to-share.png");
            FileOutputStream stream = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 90, stream);
            stream.close();
            uri = Uri.fromFile(file);
        } catch (IOException e) {
            Log.d("TAG", "IOException while trying to write file for sharing: " + e.getMessage());
        }
        return uri;
    }

    private void saveImage(Bitmap finalBitmap, String image_name) {
        addJpgSignatureToGallery(finalBitmap);
        rlaccom.setVisibility(View.VISIBLE);
        rlagfee.setVisibility(View.VISIBLE);
    }

    public File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e("SignaturePad", "Directory not created");
        }
        return file;
    }

    public void saveBitmapToJPG(Bitmap bitmap, File photo) throws IOException {
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        OutputStream stream = new FileOutputStream(photo);
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        stream.close();
    }

    public boolean addJpgSignatureToGallery(Bitmap signature) {
        boolean result = false;
        try {
            String flname = String.format("ShareRec_%d", System.currentTimeMillis());
            File photo = new File(getAlbumStorageDir(album_folder_name), String.format("ShareR%d.jpg", System.currentTimeMillis()));
            File filename = photo;
            saveBitmapToJPG(signature, photo);
            scanMediaFile(photo);
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void scanMediaFile(File photo) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(photo);
        mediaScanIntent.setData(contentUri);
        FinalConfDepoActivity.this.sendBroadcast(mediaScanIntent);
    }
}
