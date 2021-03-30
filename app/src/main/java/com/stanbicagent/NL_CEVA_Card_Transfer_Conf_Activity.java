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
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

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

public class NL_CEVA_Card_Transfer_Conf_Activity extends BaseActivity implements View.OnClickListener {
    Button btnsub;
    ProgressDialog prgDialog, prgDialog2;
    Layout_to_Image layout_to_image;  //Create Object of Layout_to_Image Class
    LinearLayout relativeLayout, whole_layout;   //Define Any Layout
    Button shareImage, repissue;
    Bitmap bitmap;                  //Bitmap for holding Image of layout
    private static Boolean forceCLaim = true;
    RelativeLayout rlsave, rlshare, rlprint;
    SharedPreferences pref;
    String tid, mid, mer_name, loc, ccode, user_only, fld_43, totfee, send_date, send_time, mTypeFacePath, appvers = "";
    int card_no_len = 0;
    String cardNo, cholder_name, amt, rrn, card_label, tvr, aid, stan, txn_amount, mask_card_no, auth = "";
    TextView txt_card_no, txt_holder_name, txt_amount, txt_ref_code, textfinaldatet = null;
    String recanno, narra, ednamee, ednumbb, txtname;
    TextView txt_ben_name, textViewnb2, txtamount_disp, textViewrr, txt_sendnammm, txt_sendno;
    DeviceServiceEngine mSDKManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nl_activity_final_conf_card_transfer_trans);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        appvers = Utility.getAppVersion(getApplicationContext());
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        //user_only = pref.getString("user_only", "");
        user_only = Utility.gettUtilUserId(getApplicationContext());
        tid = pref.getString("tid", "");
        mid = pref.getString("mid", "");
        mer_name = pref.getString("mer_name", "");
        loc = pref.getString("loc", "");
        ccode = pref.getString("ccode", "");
        fld_43 = pref.getString("fld_43", "");
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat sdate_df = new SimpleDateFormat("dd MMM yyyy");
        SimpleDateFormat stime_df = new SimpleDateFormat("HH:mm");
        send_date = sdate_df.format(c);
        send_time = stime_df.format(c);

        txt_card_no = (TextView) findViewById(R.id.txtcard_no);
        txt_holder_name = (TextView) findViewById(R.id.txtcard_holder);
        txt_amount = (TextView) findViewById(R.id.txtamount_disp);
        txt_ref_code = (TextView) findViewById(R.id.txt_ref_code);
        textfinaldatet = (TextView) findViewById(R.id.textfinaldatet);
        shareImage = (Button) findViewById(R.id.share_image);
        shareImage.setOnClickListener(this);
        repissue = (Button) findViewById(R.id.reportiss);
        repissue.setOnClickListener(this);
        rlsave = (RelativeLayout) findViewById(R.id.rlsave);
        rlshare = (RelativeLayout) findViewById(R.id.rlshare);
        rlprint = (RelativeLayout) findViewById(R.id.rlprint);

        cardNo = pref.getString("cardNo", "");
        card_no_len = cardNo.length();
        try {
            if(card_no_len > 16){
                mask_card_no = maskString(cardNo, 6, 15, '*');
            }else {
                mask_card_no = maskString(cardNo, 6, 12, '*');
            }
            txt_card_no.setText(mask_card_no);
        } catch (Exception e) {
            e.printStackTrace();
        }
        cholder_name = pref.getString("cholder_name", "");
        amt = pref.getString("txn_amount", "");
        rrn = pref.getString("rrn", "");
        auth = pref.getString("auth", "");
        card_label = pref.getString("card_label", "");
        tvr = pref.getString("tvr", "");
        aid = pref.getString("aid", "");
        stan = pref.getString("stan", "");
        txn_amount = Utility.returnNumberFormat(amt);
        txt_amount.setText(ApplicationConstants.KEY_NAIRA + txn_amount);
        textfinaldatet.setText(send_date + " " + send_time);
        txt_ref_code.setText(rrn);
        txt_holder_name.setText(cholder_name);

        whole_layout = (LinearLayout) findViewById(R.id.whole_layout);
        relativeLayout = (LinearLayout) findViewById(R.id.receipt);

        txt_ben_name = (TextView) findViewById(R.id.txt_ben_name);
        textViewnb2 = (TextView) findViewById(R.id.textViewnb2);
        txtamount_disp = (TextView) findViewById(R.id.txtamount_disp);
        textViewrr = (TextView) findViewById(R.id.textViewrr);
        txt_sendnammm = (TextView) findViewById(R.id.txt_sendnammm);
        txt_sendno = (TextView) findViewById(R.id.txt_sendno);
        Intent intent = getIntent();
        if (intent != null) {
            recanno = intent.getStringExtra("recanno");
            narra = intent.getStringExtra("narra");
            ednamee = intent.getStringExtra("ednamee");
            ednumbb = intent.getStringExtra("ednumbb");
            txtname = intent.getStringExtra("txtname");
            textViewnb2.setText(recanno);
            txt_ben_name.setText(txtname);
            txtamount_disp.setText(ApplicationConstants.KEY_NAIRA + Utility.returnNumberFormat(amt));
            textViewrr.setText(narra);
            txt_sendnammm.setText(ednamee);
            txt_sendno.setText(ednumbb);
        }

        mSDKManager = SDKManager.getInstance().getDeviceServiceEngine();
        if (mSDKManager == null) {
            SecurityLayer.Log("MF_SDKManager", "ServiceEngine is Null");
            return;
        }

        mTypeFacePath = initTypeFacePath();
        rlsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout_to_image = new Layout_to_Image(NL_CEVA_Card_Transfer_Conf_Activity.this, relativeLayout);
                //now call the main working function ;) and hold the returned image in bitmap
                bitmap = layout_to_image.convert_layout();
                String filename = "ShareRec" + System.currentTimeMillis() + ".jpg";
                if (Utility.checkPermission(NL_CEVA_Card_Transfer_Conf_Activity.this)) {
                    saveImage(bitmap, filename);
                    Toast.makeText(
                            getApplicationContext(),
                            "Receipt downloaded successfully to gallery",
                            Toast.LENGTH_LONG).show();
                }
                whole_layout.setVisibility(View.GONE);
                whole_layout.setVisibility(View.VISIBLE);
            }
        });

        rlshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout_to_image = new Layout_to_Image(getApplicationContext(), relativeLayout);
                //now call the main working function ;) and hold the returned image in bitmap
                bitmap = layout_to_image.convert_layout();
                if (Utility.checkWriteStoragePermission(NL_CEVA_Card_Transfer_Conf_Activity.this)) {
                    if (!(getApplicationContext() == null)) {
                        shareImage(getImageUri(bitmap));
                    }
                }
                whole_layout.setVisibility(View.GONE);
                whole_layout.setVisibility(View.VISIBLE);
            }
        });

        rlprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                After_Printer_Check();
            }
        });

        prgDialog2 = new ProgressDialog(this);
        prgDialog2.setMessage("Loading....");
        prgDialog2.setCancelable(false);
        btnsub = (Button) findViewById(R.id.button2);
        btnsub.setOnClickListener(this);
        whole_layout = (LinearLayout) findViewById(R.id.whole_layout);
        relativeLayout = (LinearLayout) findViewById(R.id.receipt);
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

    public void After_Printer_Check() {
        try {
            Print_Card_Wdl_Txn();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void Print_Card_Wdl_Txn() throws RemoteException {
        com.morefun.yapi.device.printer.Printer printer = mSDKManager.getPrinter();
        try {
            if (card_no_len > 16) {
                cardNo = maskString(cardNo, 6, 15, '*');
            } else {
                cardNo = maskString(cardNo, 6, 12, '*');
            }
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
            bundle.putInt(PrinterConfig.COMMON_GRAYLEVEL,  ApplicationConstants.printer_gray_scale_level);
            printer.setConfig(bundle);

            printer.appendPrnStr("AGENT: " + mer_name, fontSize, false);
            printer.appendPrnStr("LOC: " + loc, fontSize, false);
            printer.appendPrnStr("CARD NUMBER: " + cardNo, fontSize, false);
            printer.appendPrnStr("HOLDER: " + cholder_name, fontSize, false);
            printer.appendPrnStr("TID: " + tid + "  MID: " + mid, fontSize, false);
            printer.appendPrnStr("DATE: " + send_date + "        TIME: " + send_time, fontSize, false);
            printer.appendPrnStr("-----------------------------------------------------", fontSize, false);
            printer.appendPrnStr("TO ACC: " + recanno, fontSize, false);
            printer.appendPrnStr("ACC NAME: " + txtname, fontSize, false);
            printer.appendPrnStr("SENDER NAME: " + ednamee, fontSize, false);
            printer.appendPrnStr("SENDER NUMBER: " + ednumbb, fontSize, false);
            printer.appendPrnStr("NARRATION: " + narra, fontSize, false);
            printer.appendPrnStr("-----------------------------------------------------", fontSize, false);
            printer.appendPrnStr("CARD: " + card_label, fontSize, false);
            printer.appendPrnStr("AID: " + aid, fontSize, false);
            printer.appendPrnStr("TVR: " + tvr, fontSize, false);
            printer.appendPrnStr("-----------------------------------------------------", fontSize, false);
            printer.appendPrnStr("CARD TRANSFER", fontSize, false);
            fontSize = FontFamily.BIG;
            printer.appendPrnStr("TOTAL: KES " + txn_amount, fontSize, false);
            fontSize = FontFamily.BIG;
            printer.appendPrnStr("     APPROVED", fontSize, false);
            fontSize = FontFamily.MIDDLE;

            printer.appendPrnStr("-----------------------------------------------------", fontSize, false);
            printer.appendPrnStr("Response Code: 00", fontSize, false);
            printer.appendPrnStr("Trace No: " + stan, fontSize, false);
            printer.appendPrnStr("Ref ID: " + rrn, fontSize, false);
            printer.appendPrnStr("Auth: " + auth, fontSize, false);
            printer.appendPrnStr("App Version: " + appvers, fontSize, false);
            printer.appendPrnStr("-----------------------------------------------------", fontSize, false);
            printer.appendPrnStr("SERVED BY: " + Utility.gettUtilUserId(getApplicationContext()), fontSize, false);
            printer.appendPrnStr("          **** AGENT COPY ****", fontSize, false);
            printer.appendPrnStr("\n\n", fontSize, false);
            printer.startPrint(new OnPrintListener.Stub() {
                @Override
                public void onPrintResult(int ret) throws RemoteException {
                    SecurityLayer.Log("onPrintResult", "onPrintResult = " + ret);
                    //listener.showMessage(ret == ServiceResult.Success ? context.getString(R.string.msg_succ) : context.getString(R.string.msg_fail));
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }
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
            mTypefacePath = FileUtils.getExternalCacheDir(NL_CEVA_Card_Transfer_Conf_Activity.this, namePath + ".ttf");
        } else {
            String filePath = FileUtils.createTmpDir(NL_CEVA_Card_Transfer_Conf_Activity.this);
            SecurityLayer.Log("initTypeFacePath", "filePath = " + filePath);
            mTypefacePath = filePath + namePath + ".ttf";
        }
        SecurityLayer.Log("initTypeFacePath", "mTypefacePath = " + mTypefacePath);
        try {
            FileUtils.copyFromAssets(NL_CEVA_Card_Transfer_Conf_Activity.this.getAssets(), namePath, mTypefacePath, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SecurityLayer.Log("initTypeFacePath", "mTypefacePath = " + mTypefacePath);
        return mTypefacePath;
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
            Intent i = new Intent(NL_CEVA_Card_Transfer_Conf_Activity.this, FMobActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            // Staring Login Activity
            startActivity(i);
        }
        if (view.getId() == R.id.share_image) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("SELECT AN OPTION");
            String[] animals = {"Save to Gallery", "Share Receipt"};
            builder.setItems(animals, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0: // horse
                            layout_to_image = new Layout_to_Image(getApplicationContext(), relativeLayout);
                            //now call the main working function ;) and hold the returned image in bitmap
                            bitmap = layout_to_image.convert_layout();
                            String filename = "ShareRec" + System.currentTimeMillis() + ".jpg";
                            if (Utility.checkPermission(NL_CEVA_Card_Transfer_Conf_Activity.this)) {
                                saveImage(bitmap, filename);
                                Toast.makeText(
                                        getApplicationContext(),
                                        "Receipt downloaded successfully to gallery",
                                        Toast.LENGTH_LONG).show();
                            }
                            break;
                        case 1: // cow
                            layout_to_image = new Layout_to_Image(getApplicationContext(), relativeLayout);
                            //now call the main working function ;) and hold the returned image in bitmap
                            bitmap = layout_to_image.convert_layout();
                            if (Utility.checkPermission(NL_CEVA_Card_Transfer_Conf_Activity.this)) {
                                if (!(getApplicationContext() == null)) {
                                    shareImage(getImageUri(bitmap));
                                }
                            }
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

    @Override
    public void onResume() {
        super.onResume();
    }

    private void saveImage(Bitmap finalBitmap, String image_name) {
        addJpgSignatureToGallery(finalBitmap);
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
        NL_CEVA_Card_Transfer_Conf_Activity.this.sendBroadcast(mediaScanIntent);
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
}
