package com.stanbicagent;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.stanbicagent.R;
import com.vipul.hp_hp.library.Layout_to_Image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FinalConfOtherWalletsActivity extends BaseActivity implements View.OnClickListener {
    TextView recacno,recname,recamo,recnarr,recsendnum,recsendnam,recbnknm,recfee,recagcmn,txtrfcd,recdatetimee;
    Button btnsub;
    String recanno, amou ,narra, ednamee,ednumbb,txtname,bankname,bankcode,strfee,stragcms;
    ProgressDialog prgDialog,prgDialog2;
    EditText etpin;
    RelativeLayout rlsave,rlshare;
    RelativeLayout rlagfee,rlaccom;
    LinearLayout relativeLayout;
    Bitmap bitmap;
    Layout_to_Image layout_to_image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_conf_other_wallets);


        recacno = (TextView) findViewById(R.id.textViewnb2);
        txtrfcd = (TextView) findViewById(R.id.txtrfcd);
        recname = (TextView) findViewById(R.id.textViewcvv);
        recbnknm = (TextView) findViewById(R.id.textViewweryu);
        recamo = (TextView) findViewById(R.id.textViewrrs);
        recnarr = (TextView) findViewById(R.id.textViewrr);
        etpin = (EditText) findViewById(R.id.pin);
        recfee = (TextView) findViewById(R.id.txtfee);
        recsendnam = (TextView) findViewById(R.id.sendnammm);
        recsendnum = (TextView)findViewById(R.id.sendno);
        recagcmn = (TextView) findViewById(R.id.txtaccom);
        prgDialog2 = new ProgressDialog(this);
        prgDialog2.setMessage("Loading....");
        prgDialog2.setCancelable(false);

        rlagfee = (RelativeLayout) findViewById(R.id.rlagfee);
        rlaccom = (RelativeLayout) findViewById(R.id.rlaccom);


        btnsub = (Button) findViewById(R.id.button2);
        btnsub.setOnClickListener(this);
        recdatetimee = (TextView) findViewById(R.id.textfinaldatet);
        Intent intent = getIntent();
        if (intent != null) {


            recanno = intent.getStringExtra("walphno");
            amou = intent.getStringExtra("amou");
            narra = intent.getStringExtra("narra");
            ednamee = intent.getStringExtra("ednamee");
            ednumbb = intent.getStringExtra("ednumbb");
            txtname = intent.getStringExtra("txtname");
            bankname = intent.getStringExtra("walletname");
            bankcode = intent.getStringExtra("walletcode");
            strfee = intent.getStringExtra("fee");
            String   txtrfc = intent.getStringExtra("refcode");
            txtrfcd.setText(txtrfc);
            stragcms = Utility.returnNumberFormat(intent.getStringExtra("agcmsn"));
            recacno.setText(recanno);
            recname.setText(txtname);
            recbnknm.setText(bankname);
            recamo.setText(ApplicationConstants.KEY_NAIRA+amou);
            recnarr.setText(narra);
            recfee.setText(ApplicationConstants.KEY_NAIRA+strfee);
            recsendnam.setText(ednamee);
            recsendnum.setText(ednumbb);
            recagcmn.setText(ApplicationConstants.KEY_NAIRA+stragcms);

            String redatetim = intent.getStringExtra("datetime");
            recdatetimee.setText(Utility.changeDate(redatetim));
        }

        relativeLayout=(LinearLayout)findViewById(R.id.receipt);
        rlsave = (RelativeLayout) findViewById(R.id.rlsave);
        rlshare = (RelativeLayout) findViewById(R.id.rlshare);

        rlsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rlaccom.setVisibility(View.GONE);
                rlagfee.setVisibility(View.GONE);

                layout_to_image=new Layout_to_Image(getApplicationContext(),relativeLayout);

                //now call the main working function ;) and hold the returned image in bitmap

                bitmap=layout_to_image.convert_layout();

                String filename = "ShareRec"+System.currentTimeMillis()+".jpg";
                if(Utility.checkPermission(FinalConfOtherWalletsActivity.this)) {
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
                layout_to_image=new Layout_to_Image(getApplicationContext(),relativeLayout);

                //now call the main working function ;) and hold the returned image in bitmap

                bitmap=layout_to_image.convert_layout();
                if(Utility.checkWriteStoragePermission(FinalConfOtherWalletsActivity.this)) {
                    shareImage(getImageUri(getApplicationContext(), bitmap));
                }

                rlaccom.setVisibility(View.VISIBLE);
                rlagfee.setVisibility(View.VISIBLE);
            }
        });

    }

    private void shareImage(Uri imagePath) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        sharingIntent.setType("image/*");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, imagePath);
        startActivity(Intent.createChooser(sharingIntent, "Share Image Using"));
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), FMobActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    private void saveImage(Bitmap finalBitmap, String image_name) {
/*
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root);
        myDir.mkdirs();
        String fname = image_name;
        File file = new File(Environment.getExternalStorageDirectory(), "/FirstAgent/"+fname);
        if (file.exists()) file.delete();
        Log.i("LOAD", root + fname);
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        addJpgSignatureToGallery(finalBitmap);
        rlaccom.setVisibility(View.VISIBLE);
        rlagfee.setVisibility(View.VISIBLE);
    }
    public boolean addJpgSignatureToGallery(Bitmap signature) {
        boolean result = false;
        try {


            String flname = String.format("ShareRec_%d", System.currentTimeMillis());
            File photo = new File(getAlbumStorageDir("FirstAgent"), String.format("ShareR%d.jpg", System.currentTimeMillis()));
            File filename = photo;
            saveBitmapToJPG(signature, photo);
            scanMediaFile(photo);
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
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
    private void scanMediaFile(File photo) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(photo);
        mediaScanIntent.setData(contentUri);
        FinalConfOtherWalletsActivity.this.sendBroadcast(mediaScanIntent);
    }
    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.button2) {


            finish();


            Intent i = new Intent(FinalConfOtherWalletsActivity.this, FMobActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            // Staring Login Activity
            startActivity(i);
        }
    }
}
