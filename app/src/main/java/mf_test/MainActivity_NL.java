package mf_test;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.stanbicagent.R;
import com.stanbicagent.SDKManager;
import com.morefun.yapi.device.printer.FontFamily;
import com.morefun.yapi.device.printer.OnPrintListener;
import com.morefun.yapi.device.printer.Printer;
import com.morefun.yapi.device.printer.PrinterConfig;
import com.morefun.yapi.engine.DeviceServiceEngine;

import java.io.IOException;

import security.SecurityLayer;
import utils.FileUtils;

public class MainActivity_NL extends AppCompatActivity {
    Button print, LoadTlk, EmvCard, ISO;
    DeviceServiceEngine mSDKManager;
    String mTypeFacePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nl_activity_main_newland);
        SDKManager.getInstance().bindService(getApplicationContext());
        print = (Button) findViewById(R.id.print);
        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSDKManager = SDKManager.getInstance().getDeviceServiceEngine();
                if (mSDKManager == null) {
                    SecurityLayer.Log("mSDKManager", "ServiceEngine is Null");
                    return;
                }
               /* try {
                    Print_Cash_Test();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }*/
            }
        });
        /*LoadTlk = (Button) findViewById(R.id.LoadTLK);
        LoadTlk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity_NL.this.finish();
                startActivity(new Intent(MainActivity_NL.this,
                        LoadKeysActivity.class));
            }
        });*/
        EmvCard = (Button) findViewById(R.id.EmvCard);
        EmvCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity_NL.this.finish();
                startActivity(new Intent(MainActivity_NL.this,
                        CardReaderActivity.class));
            }
        });
    }

   /* public void Print_Cash_Test() throws RemoteException {
        String mer_name = "Test Agent";
        String loc = "Marina";
        Printer printer = mSDKManager.getPrinter();
        mTypeFacePath = initTypeFacePath();
        try {
            if (printer.getStatus() != 0){
                SecurityLayer.Log("getStatus", "getStatus = " + (R.string.msg_nopaper));
                return;
            }
            if (printer.initPrinter() != 0) {
                return ;
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
            if (!TextUtils.isEmpty(mTypeFacePath)){
                bundle.putString(PrinterConfig.COMMON_TYPEFACE , mTypeFacePath);
            }
            bundle.putInt(PrinterConfig.COMMON_GRAYLEVEL, 15);
            printer.setConfig(bundle);
            printer.appendPrnStr("LOC: " + loc, fontSize, false);
            printer.appendPrnStr("TERMINAL NO.：25689753", fontSize, false);
            fontSize = FontFamily.BIG;
            printer.appendPrnStr("TOTAL: KES " + "10,000.00", fontSize, false);
            printer.appendPrnStr("        APPROVED", fontSize, false);
            fontSize = FontFamily.MIDDLE;
            printer.appendPrnStr("EXP DATE：2029", fontSize, false);
            printer.appendPrnStr("BATCH NO：000012", fontSize, false);
            printer.appendPrnStr("---X---X---X---X---X--X--X--X--X--X--\n\n", fontSize, false);
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
    }*/

    public String initTypeFacePath() {
        String namePath = "wawa";
        String mTypefacePath = null;
        if (Build.VERSION.SDK_INT >= 23) {
            mTypefacePath = FileUtils.getExternalCacheDir(MainActivity_NL.this, namePath + ".ttf");
        }else {
            String filePath  = FileUtils.createTmpDir(MainActivity_NL.this);
            Log.d("initTypeFacePath" ,"filePath = " + filePath);
            mTypefacePath = filePath + namePath +".ttf";
        }
        Log.d("initTypeFacePath" ,"mTypefacePath = " + mTypefacePath);
        try {
            FileUtils.copyFromAssets(MainActivity_NL.this.getAssets(), namePath, mTypefacePath ,true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("initTypeFacePath" ,"mTypefacePath = " + mTypefacePath);
        return mTypefacePath ;
    }
}
