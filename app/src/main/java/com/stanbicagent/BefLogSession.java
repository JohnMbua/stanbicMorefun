package com.stanbicagent;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

public class BefLogSession {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "MyPref";
    public static final String SCHID = "schoolids";
    public static final String STUDIDONE = "studone";
    public static final String STUDIDTWO = "studtwo";
    public static final String STUDIDTHREE = "studthree";
    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String IS_STARTED = "IsStarted";
    private static final String IS_REG = "IsReg";
    public static final String KEY_BIOLOG = "BioLog";
    public static final String IS_TPIN = "IsTPin";
    public static final String NETWORK_URL = "neturl";
    public static final String SEL_ACCOUNT = "accont";
    public static final String DEF_ACCOUNT = "defacc";
    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "name";

    // Email address (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";

    public static final String KEY_PRODID = "prodid";

    // Email address (make variable public to access from outside)
    public static final String KEY_TPIN = "ktpin";
    public static final String KEY_TIMEST = "timest";
    public static final String KEY_TXPIN = "txpin";
    public static final String KEY_INST = "inst";
    public static final String KEY_CUSTNAME = "inst";
    public static final String KEY_USERID = "userid";
    public static final String KEY_MOBILE = "mobno";
    public static final String KEY_FNAMES = "fname";
    public static final String KEY_WPIN = "wpin";
    public static final String KEY_ROLE = "role";
    public static final String KEY_DISP = "disp";

    public static final String KEY_TOP1 ="top1";
    public static final String KEY_TOP2 ="top2";
    public static final String KEY_TOP3 ="top3";

    // Email address (make variable public to access from outside)
    public static final String KEY_QTY = "qty";
    // Constructor
    public BefLogSession(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }
     
    /**
     * Create login session
     * */
    public void createLoginSession(String userid){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_USERID, userid);
        // Storing name in pref

         
        // commit changes
        editor.commit();
    }
    public void SetTpin(String tpin){
        // Storing login value as TRUE
        editor.putBoolean(IS_TPIN, true);
        editor.putString(KEY_TPIN, tpin);


        // Storing name in pref


        // commit changes
        editor.commit();
    }
    public void SetStarted(){
        // Storing login value as TRUE
        editor.putBoolean(IS_STARTED, true);

        // Storing name in pref


        // commit changes
        editor.commit();
    }
    public void SetReg(){
        // Storing login value as TRUE
        editor.putBoolean(IS_REG, true);

        // Storing name in pref


        // commit changes
        editor.commit();
    }
    public void SetPers(String serv){
        // Storing login value as TRUE
        editor.putBoolean(serv, true);

        // Storing name in pref


        // commit changes
        editor.commit();
    }
    public void SetPersFalse(String serv){
        // Storing login value as TRUE
        editor.putBoolean(serv, false);

        // Storing name in pref


        // commit changes
        editor.commit();
    }
    public void SetStud1(String serv){
        // Storing login value as TRUE
        editor.putString(STUDIDONE, serv);

        // Storing name in pref


        // commit changes
        editor.commit();
    }
    public HashMap<String, String> getStud1(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(STUDIDONE, pref.getString(STUDIDONE, null));

        return user;
    }
    public void SetStud2(String serv){
        // Storing login value as TRUE
        editor.putString(STUDIDTWO, serv);

        // Storing name in pref


        // commit changes
        editor.commit();
    }
    public HashMap<String, String> getStud2(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(STUDIDTWO, pref.getString(STUDIDTWO, null));

        return user;
    }
    public void SetStud3(String serv){
        // Storing login value as TRUE
        editor.putString(STUDIDTHREE, serv);

        // Storing name in pref


        // commit changes
        editor.commit();
    }
    public HashMap<String, String> getStud3(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(STUDIDTHREE, pref.getString(STUDIDTHREE, null));

        return user;
    }
    public void createStaffLoginSession(String userid, String role){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);
         
        // Storing name in pref
        editor.putString(KEY_USERID, userid);
       
         
        // Storing email in pref
        editor.putString(KEY_ROLE, role);
         
        // commit changes
        editor.commit();
    }  
    
   /* public void createCart(String prodid, String userid, String qty,String price){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);
         
        // Storing name in pref
        editor.putString(KEY_NAME, pwd);
         
        // Storing email in pref
        editor.putString(KEY_EMAIL, email);
         
        // commit changes
        editor.commit();
    } */
     
    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    public boolean checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
        	/*
            Intent i = new Intent(_context, Login.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
             
            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
             
            // Staring Login Activity
            _context.startActivity(i);*/
            
        return false;
        }else{
        	return true;
        }
         
    }

    public boolean checkStarted(){
        // Check login status
        if(!this.isStarted()){
            // user is not logged in redirect him to Login Activity
        	/*
            Intent i = new Intent(_context, Login.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);*/

            return false;
        }else{
            return true;
        }

    }
    public boolean checkReg(){
        // Check login status
        if(!this.isReg()){
            // user is not logged in redirect him to Login Activity
        	/*
            Intent i = new Intent(_context, Login.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);*/

            return false;
        }else{
            return true;
        }

    }
    public boolean checkTPin(){
        // Check login status
        if(!this.isTPin()){
            // user is not logged in redirect him to Login Activity
        	/*
            Intent i = new Intent(_context, Login.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);*/

            return false;
        }else{
            return true;
        }

    }
    public boolean checkPers(String serv){
        // Check login status
        if(!this.isPers(serv)){
            // user is not logged in redirect him to Login Activity
        	/*
            Intent i = new Intent(_context, Login.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);*/

            return false;
        }else{
            return true;
        }

    }
    public void putURL(String neturl){
        // Storing login value as TRUE

        editor.putString(NETWORK_URL, neturl);

        // commit changes
        editor.commit();
    }
    public HashMap<String, String> getNetURL(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(NETWORK_URL, pref.getString(NETWORK_URL, null));

        return user;
    }
    public void putCurrTime(Long time){
        // Storing login value as TRUE

        editor.putLong(KEY_TIMEST, time);

        // commit changes
        editor.commit();
    }
    public HashMap<String, Long> getCurrTime(){
        HashMap<String, Long> user = new HashMap<>();
        // user name
        user.put(KEY_TIMEST, pref.getLong(KEY_TIMEST, 0));

        return user;
    }
    public void putCustName(String mno){
        // Storing login value as TRUE

        editor.putString(KEY_CUSTNAME, mno);

        // commit changes
        editor.commit();
    }
    public HashMap<String, String> getCustName(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_CUSTNAME, pref.getString(KEY_CUSTNAME, null));

        return user;
    }
    public void putEmail(String mno){
        // Storing login value as TRUE

        editor.putString(KEY_EMAIL, mno);

        // commit changes
        editor.commit();
    }
    public HashMap<String, String> getEmail(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));

        return user;
    }
    public void putTop1(String mno){
        // Storing login value as TRUE

        editor.putString(KEY_TOP1, mno);

        // commit changes
        editor.commit();
    }
    public HashMap<String, String> getTop1(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_TOP1, pref.getString(KEY_TOP1, null));

        return user;
    }
    public void putTop2(String mno){
        // Storing login value as TRUE

        editor.putString(KEY_TOP2, mno);

        // commit changes
        editor.commit();
    }
    public HashMap<String, String> getTop2(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_TOP2, pref.getString(KEY_TOP2, null));

        return user;
    }
    public void putTop3(String mno){
        // Storing login value as TRUE

        editor.putString(KEY_TOP3, mno);

        // commit changes
        editor.commit();
    }
    public HashMap<String, String> getTop3(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_TOP3, pref.getString(KEY_TOP3, null));

        return user;
    }
    public void putMobNo(String mno){
        // Storing login value as TRUE

        editor.putString(KEY_MOBILE, mno);

        // commit changes
        editor.commit();
    }
    public HashMap<String, String> getMobNo(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_MOBILE, pref.getString(KEY_MOBILE, null));

        return user;
    }
    public void putBioLog(String mno){
        // Storing login value as TRUE

        editor.putString(KEY_BIOLOG, mno);

        // commit changes
        editor.commit();
    }
    public HashMap<String, String> getBioLog(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_BIOLOG, pref.getString(KEY_BIOLOG, null));

        return user;
    }
    public void putDisp(String mno){
        // Storing login value as TRUE

        editor.putString(KEY_DISP, mno);

        // commit changes
        editor.commit();
    }
    public HashMap<String, String> getDisp(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_DISP, pref.getString(KEY_DISP, null));

        return user;
    }

    public void putSelAcc(String acc){
        // Storing login value as TRUE

        editor.putString(SEL_ACCOUNT, acc);

        // commit changes
        editor.commit();
    }
    public HashMap<String, String> getSelAcc(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(SEL_ACCOUNT, pref.getString(SEL_ACCOUNT, null));

        return user;
    }

    public void putDefAcc(String acc){
        // Storing login value as TRUE

        editor.putString(DEF_ACCOUNT, acc);

        // commit changes
        editor.commit();
    }
    public HashMap<String, String> getDefAcc(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(DEF_ACCOUNT, pref.getString(DEF_ACCOUNT, null));

        return user;
    }

    public void putInst(String mno){
        // Storing login value as TRUE

        editor.putString(KEY_INST, mno);

        // commit changes
        editor.commit();
    }
    public HashMap<String, String> getInst(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_INST, pref.getString(KEY_INST, null));

        return user;
    }
    public void putTxnFlag(String mno){
        // Storing login value as TRUE

        editor.putString(KEY_TXPIN, mno);

        // commit changes
        editor.commit();
    }
    public HashMap<String, String> getTxnFlag(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_TXPIN, pref.getString(KEY_TXPIN, null));

        return user;
    }
    public HashMap<String, String> getTpin(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_TPIN, pref.getString(KEY_TPIN, null));

        return user;
    }
     
    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_USERID, pref.getString(KEY_USERID, null));
        user.put(KEY_FNAMES, pref.getString(KEY_FNAMES, null));
        // user email id
        user.put(KEY_ROLE, pref.getString(KEY_ROLE, null));
        user.put(KEY_MOBILE, pref.getString(KEY_MOBILE, null));
        user.put(KEY_WPIN, pref.getString(KEY_WPIN, null));
        // return user
        return user;
    }
     
    /**
     * Clear session details
     * */
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.remove(SEL_ACCOUNT);
        editor.remove(KEY_INST);
        editor.remove(KEY_TXPIN);
        editor.remove(KEY_MOBILE);
        editor.remove(KEY_USERID);
        editor.remove(KEY_TIMEST);
        editor.remove(DEF_ACCOUNT);
        editor.remove(KEY_DISP);
        editor.putBoolean(IS_LOGIN, false);
        editor.commit();
/*
        //After logout redirect user to Loing Activity
        Intent i = new Intent(_context, MainActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);*/


    }
     
    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){

        return pref.getBoolean(IS_LOGIN, false);
    }
    public boolean isStarted(){

        return pref.getBoolean(IS_STARTED, false);
    }
    public boolean isReg(){

        return pref.getBoolean(IS_REG, false);
    }
    public boolean isTPin(){

        return pref.getBoolean(IS_TPIN, false);
    }
    public boolean isPers(String serv){

        return pref.getBoolean(serv, false);
    }



}