package com.stanbicagent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.stanbicagent.R;

import printer.PrintOrder;

public class USBPrintingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usbprinting);
        String msg = "This is a test message";
        PrintOrder printer = new PrintOrder();
        printer.Print(this,msg);
    }
}
