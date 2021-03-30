package com.stanbicagent;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import security.SecurityLayer;
import utils.AESUtils;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            Preference ip= findPreference("ip");
            Preference port= findPreference("port");
            Preference Url= findPreference("Url");
            System.out.println(String.valueOf(((EditTextPreference) ip).getText().toString()));  //read ip
            System.out.println(String.valueOf(((EditTextPreference) port).getText().toString()));  //read ip
            System.out.println(String.valueOf(((EditTextPreference) Url).getText().toString()));  //read ip#


            //save set data
            new AlertDialog.Builder(getContext())
                    .setTitle("Host Config")
                    .setMessage("Changing these values affects connectivity")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getContext(), "You clicked on YES", Toast.LENGTH_SHORT).show();
                            savedetails(String.valueOf(((EditTextPreference) ip).getText().toString()+"|"+ String.valueOf(((EditTextPreference) port).getText().toString())+"|"+ String.valueOf(((EditTextPreference) Url).getText().toString())),getContext());
                        }
                    })
                    .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getContext(), "You clicked on NO", Toast.LENGTH_SHORT).show();
                            savedetails(String.valueOf(((EditTextPreference) ip).getText().toString()+"|"+ String.valueOf(((EditTextPreference) port).getText().toString())+"|"+ String.valueOf(((EditTextPreference) Url).getText().toString())),getContext());

                            String dat="";
                            readdetails(dat,getContext());
                            dialog.cancel();
                            startActivity(new Intent(getActivity(), ChangeAcNameActivity.class));
                        }
                    })

                    .create()
                    .show();

        }
    }


    public static void savedetails(String dat, Context c){
        try {
            //final String TESTSTRING = new String("abcdefghijklmnopqrstuvwxyz");
            try {
                FileOutputStream fOut = c.openFileOutput("params.txt",  MODE_PRIVATE);

//            String sourceStr = "This is any source string";
                OutputStreamWriter osw = new OutputStreamWriter(fOut);

                //encrypt String
                String encrypted = "";
                try {
                    encrypted = AESUtils.encrypt(dat);
                    Log.d("TEST", "encrypted:" + encrypted);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Write the string to the file
                osw.write(encrypted);
                osw.flush();
                osw.close();

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }


    public static void readdetails(String dat,Context c){
        try {
           // final String TESTSTRING = "6652F7EF9615C2D1DFD0530CF6DC9840AF9708E68B79624B31F43A4DBC85865C60AFBF129B283E5F74EF5A4C8F1FDCC8ADCDE413179C41AB12620AE38E4F39F8828AA7615C9EA1EF87BF5F745315C6EDD12CDC1E04DC5366D074892E1025DACCD18D9FA228F4016F4DAA1C95DFCB58470A81D9D27FB4133661DB432785752B13F0FBC20AB8C43BB38894E68BE912BBC68F5A08DB3823FE9A54D15443293E8C52";
            try {
                FileInputStream fIn = c.openFileInput("params.txt");

                InputStreamReader isr = new InputStreamReader(fIn);
                char[] inputBuffer = new char[128];
                isr.read(inputBuffer);
                String readString = new String(inputBuffer);
               // boolean isTheSame = TESTSTRING.equals(readString);
                Log.i("readString ", "readString = " + readString);
               // Log.i("File Reading stuff", "success = " + isTheSame);


                //decrypt read string
                String decrypted = "";
                try {
                    decrypted = AESUtils.decrypt(readString);
                    Log.d("TEST", "decrypted:" + decrypted);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }

    }

}