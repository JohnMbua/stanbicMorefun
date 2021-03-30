package com.stanbicagent;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SessionManagement {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "AndroidHivePref";
    public static final String SCHID = "schoolids";
    public static final String STUDIDONE = "studone";
    public static final String CHKLOGIN = "chklogin";
    public static final String STUDIDTWO = "studtwo";
    public static final String STUDIDTHREE = "studthree";
    public static final String US_TYPE = "ustype";

    public static final String SESS_REG = "sessreg";
    public static final String KEY_THEME = "theme";
    public static final String SCHONE = "schone";
    public static final String SCHTWO = "schtwo";
    public static final String SCHTHREE = "schthree";
    public static final String SHWBAL = "shwbal";
    public static final String AGMOB = "agmobno";

    public static final String PUBLICKEY = "publickey";
    public static final String COMMDATA = "commdata";
    public static final String SUMMDATA = "perfdata";
    public static final String MYPERFTEXT = "myperftext";
    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String IS_BIOLOGIN = "IsBioLoggedIn";
    private static final String IS_TPINPREF = "IsTpinPref";
    private static final String IS_AST = "IsAst";
    private static final String IS_STARTED = "IsStarted";
    private static final String IS_REG = "IsReg";
    public static final String KEY_BIOLOG = "BioLog";
    public static final String BOOL_USERID = "booluserid";
    public static final String IS_TPIN = "IsTPin";
    public static final String NETWORK_URL = "neturl";
    public static final String SEL_ACCOUNT = "accont";
    public static final String LASTL = "lastl";
    public static final String AGENTID = "AGENTID";
    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "name";
    public static final String KEY_SETAIRTIME = "setairtime";
    public static final String KEY_AIRTIME = "keysairtime";
    public static final String KEY_SETBILLERS = "setbillers";
    public static final String KEY_SETBANKS = "setbanks";
    public static final String KEY_BANKS = "keybanks";
    public static final String KEY_BILLERS = "keysbillers";
    public static final String KEY_WALLETS = "keywallets";
    public static final String KEY_SETWALLETS = "setwallets";
    public static final String KEY_SETCNTOPEN = "cntopen";

    // Email address (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";
    public static final String STARS = "stars";
    public static final String FETCHSTARS = "fetchstars";
    public static final String KEY_PRODID = "prodid";


    public static final String STAR_DATE = "stdate";
    public static final String STAR_MSG = "stmsg";

    // Email address (make variable public to access from outside)
    public static final String KEY_TPIN = "ktpin";
    public static final String KEY_TIMEST = "timest";
    public static final String KEY_TXPIN = "txpin";
    public static final String KEY_INST = "inst";
    public static final String KEY_CUSTNAME = "cust";
    public static final String KEY_USERID = "userid";
    public static final String KEY_NEWUSERID = "newuserid";
    public static final String KEY_MOBILE = "mobno";
    public static final String KEY_FNAMES = "fname";
    public static final String KEY_WPIN = "wpin";
    public static final String KEY_FULLN = "fulln";
    public static final String KEY_DISP = "disp";

    public static final String KEY_TOP1 = "top1";
    public static final String KEY_TOP2 = "top2";
    public static final String KEY_TOP3 = "top3";


    public static final String KEY_MERCHID = "merchid";
    public static final String KEY_ACCO = "acco";
    public static final String KEY_ALL_USERS = "all_users_pass";
    public static final String KEY_ENTRY_POINT = "entry_point";

    // Email address (make variable public to access from outside)
    public static final String KEY_QTY = "qty";

    // Constructor
    public SessionManagement(Context context) {
        this._context = context;
        if(!(_context == null)) {
            pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
            editor = pref.edit();
        }
    }

    /**
     * Create login session
     */
    public void createLoginSession() {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);
        // editor.putString(KEY_USERID, userid);
        // Storing name in pref


        // commit changes
        editor.commit();
    }

    public void setAst() {
        // Storing login value as TRUE
        editor.putBoolean(IS_AST, true);

        // Storing name in pref


        // commit changes
        editor.commit();
    }

    public void UnSetAst() {
        // Storing login value as TRUE
        editor.putBoolean(IS_AST, false);

        // Storing name in pref


        // commit changes
        editor.commit();
    }

    public void setReg() {
        // Storing login value as TRUE
        editor.putBoolean(IS_REG, true);

        // Storing name in pref


        // commit changes
        editor.commit();
    }


    public void setShwbal() {
        // Storing login value as TRUE
        editor.putBoolean(SHWBAL, true);

        // Storing name in pref


        // commit changes
        editor.commit();
    }

    public void UnSetShwBal() {
        // Storing login value as TRUE
        editor.putBoolean(SHWBAL, false);

        // Storing name in pref


        // commit changes
        editor.commit();
    }


    public void setTpinPref() {
        // Storing login value as TRUE
        editor.putBoolean(IS_TPINPREF, true);

        // Storing name in pref


        // commit changes
        editor.commit();
    }

    public void UnSetTpinPref() {
        // Storing login value as TRUE
        editor.putBoolean(IS_TPINPREF, false);

        // Storing name in pref


        // commit changes
        editor.commit();
    }

    public void setBioLog() {
        // Storing login value as TRUE
        editor.putBoolean(IS_BIOLOGIN, true);

        // Storing name in pref


        // commit changes
        editor.commit();
    }

    public void UnSetBioLog() {
        // Storing login value as TRUE
        editor.putBoolean(IS_BIOLOGIN, false);

        // Storing name in pref


        // commit changes
        editor.commit();
    }

    public void setUser() {
        // Storing login value as TRUE
        editor.putBoolean(BOOL_USERID, true);

        // Storing name in pref


        // commit changes
        editor.commit();
    }

    public void UnSetUser() {
        // Storing login value as TRUE
        editor.putBoolean(BOOL_USERID, false);

        // Storing name in pref


        // commit changes
        editor.commit();
    }

    public void SetTpin(String tpin) {
        // Storing login value as TRUE
        editor.putBoolean(IS_TPIN, true);
        editor.putString(KEY_TPIN, tpin);


        // Storing name in pref


        // commit changes
        editor.commit();
    }

    public void SetStarted() {
        // Storing login value as TRUE
        editor.putBoolean(IS_STARTED, true);

        // Storing name in pref


        // commit changes
        editor.commit();
    }


    public void SetPersWidg(String serv) {
        // Storing login value as TRUE
        editor.putBoolean(serv, true);

        // Storing name in pref


        // commit changes
        editor.commit();
    }

    public void SetPersWidgFalse(String serv) {
        // Storing login value as TRUE
        editor.putBoolean(serv, false);

        // Storing name in pref


        // commit changes
        editor.commit();
    }

    public void SetPers(String serv) {
        // Storing login value as TRUE
        editor.putBoolean(serv, true);

        // Storing name in pref


        // commit changes
        editor.commit();
    }

    public void SetPersFalse(String serv) {
        // Storing login value as TRUE
        editor.putBoolean(serv, false);

        // Storing name in pref


        // commit changes
        editor.commit();
    }

    public void SetPersNews(String serv) {
        // Storing login value as TRUE
        editor.putBoolean(serv, true);

        // Storing name in pref


        // commit changes
        editor.commit();
    }

    public void SetPersNewsFalse(String serv) {
        // Storing login value as TRUE
        editor.putBoolean(serv, false);

        // Storing name in pref


        // commit changes
        editor.commit();
    }

    public void SetAgentID(String serv) {
        // Storing login value as TRUE
        editor.putString(AGENTID, serv);

        // Storing name in pref


        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getAgentID() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(AGENTID, pref.getString(AGENTID, null));

        return user;
    }


    public void SetUserID(String serv) {
        // Storing login value as TRUE
        editor.putString(KEY_USERID, serv);

        // Storing name in pref


        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getUserIdd() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_USERID, pref.getString(KEY_USERID, null));

        return user;
    }

    public void SetSch2(String serv) {
        // Storing login value as TRUE
        editor.putString(SCHTWO, serv);

        // Storing name in pref


        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getSch2() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(SCHTWO, pref.getString(SCHTWO, null));

        return user;
    }

    public void SetSch3(String serv) {
        // Storing login value as TRUE
        editor.putString(SCHTHREE, serv);

        // Storing name in pref


        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getSch3() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(SCHTHREE, pref.getString(SCHTHREE, null));

        return user;
    }


    public void SetStud1(String serv) {
        // Storing login value as TRUE
        editor.putString(STUDIDONE, serv);

        // Storing name in pref


        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getStud1() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(STUDIDONE, pref.getString(STUDIDONE, null));

        return user;
    }

    public void SetStud2(String serv) {
        // Storing login value as TRUE
        editor.putString(STUDIDTWO, serv);

        // Storing name in pref


        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getStud2() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(STUDIDTWO, pref.getString(STUDIDTWO, null));

        return user;
    }

    public void SetStud3(String serv) {
        // Storing login value as TRUE
        editor.putString(STUDIDTHREE, serv);

        // Storing name in pref


        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getStud3() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(STUDIDTHREE, pref.getString(STUDIDTHREE, null));

        return user;
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     */
    public boolean checkLogin() {
        // Check login status
        if (!this.isLoggedIn()) {
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
        } else {
            return true;
        }

    }

    public boolean checkTpinPref() {
        // Check login status
        if (!this.isTpinPref()) {
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
        } else {
            return true;
        }

    }

    public boolean checkBioLogin() {
        // Check login status
        if (!this.isBioLoggedIn()) {
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
        } else {
            return true;
        }

    }

    public boolean checkRegg() {
        // Check login status
        if (!this.isReg()) {


            return false;
        } else {
            return true;
        }

    }

    public boolean checkAst() {
        // Check login status
        if (!this.isAst()) {


            return false;
        } else {
            return true;
        }

    }

    public boolean checkShwBal() {
        // Check login status
        if (!this.isShwBal()) {


            return false;
        } else {
            return true;
        }

    }

    public void setString(String key, String string) {
        editor.putString(key, string);
        editor.commit();
    }

    public String getString(String key) {
        return pref.getString(key, null);
    }



    public void setLong(String key, Long longsec) {
        editor.putLong(key, longsec);
        editor.commit();
    }

    public Long getLong(String key) {
        return pref.getLong(key, 0);
    }

    public void setSetss(String key, ArrayList<String> arrlist) {
        Set<String> set = new HashSet<String>();
        set.addAll(arrlist);
        editor.putStringSet(key, set);
        editor.commit();

    }

    public ArrayList<String> getSets(String key) {

        Set<String> set = pref.getStringSet(key, null);
        ArrayList<String> sample = null;
        if (!(set == null)) {
            sample = new ArrayList<String>(set);
        }

        return sample;


    }

    public boolean checkUser() {
        // Check login status
        if (!this.isUserId()) {
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
        } else {
            return true;
        }

    }

    public boolean checkStarted() {
        // Check login status
        if (!this.isStarted()) {
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
        } else {
            return true;
        }

    }

    public boolean checkReg() {
        // Check login status
        if (!this.isReg()) {
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
        } else {
            return true;
        }

    }

    public boolean checkTPin() {
        // Check login status
        if (!this.isTPin()) {
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
        } else {
            return true;
        }

    }

    public boolean checkPersWidg(String serv) {
        // Check login status
        if (!this.isPers(serv)) {
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
        } else {
            return true;
        }

    }

    public boolean checkPers(String serv) {
        // Check login status
        if (!this.isPers(serv)) {
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
        } else {
            return true;
        }

    }

    public boolean checkPersNews(String serv) {
        // Check login status
        if (!this.isPersNews(serv)) {
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
        } else {
            return true;
        }

    }

    public void putURL(String neturl) {
        // Storing login value as TRUE

        editor.putString(NETWORK_URL, neturl);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getNetURL() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(NETWORK_URL, pref.getString(NETWORK_URL, null));

        return user;
    }

    public void putCurrTime(Long time) {
        // Storing login value as TRUE

        editor.putLong(KEY_TIMEST, time);

        // commit changes
        editor.commit();
    }

    public HashMap<String, Long> getCurrTime() {
        HashMap<String, Long> user = new HashMap<>();
        // user name
        user.put(KEY_TIMEST, pref.getLong(KEY_TIMEST, 0));

        return user;
    }

    public void putCurrTheme(int pref) {
        // Storing login value as TRUE

        editor.putInt(KEY_THEME, pref);

        // commit changes
        editor.commit();
    }

    public HashMap<String, Integer> getCurrTheme() {
        HashMap<String, Integer> user = new HashMap<>();
        // user name
        user.put(KEY_THEME, pref.getInt(KEY_THEME, 0));

        return user;
    }

    public void putCustName(String mno) {
        // Storing login value as TRUE

        editor.putString(KEY_CUSTNAME, mno);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getCustName() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_CUSTNAME, pref.getString(KEY_CUSTNAME, null));

        return user;
    }

    public void putStar(String mno) {
        // Storing login value as TRUE

        editor.putString(STARS, mno);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getStar() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(STARS, pref.getString(STARS, null));

        return user;
    }


    public void putFetchedStar(String mno) {
        // Storing login value as TRUE

        editor.putString(FETCHSTARS, mno);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getFetchedStar() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(FETCHSTARS, pref.getString(FETCHSTARS, null));

        return user;
    }

    public void putStarDate(String mno) {
        // Storing login value as TRUE

        editor.putString(STAR_DATE, mno);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getStarDate() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(STAR_DATE, pref.getString(STAR_DATE, null));

        return user;
    }


    public void putStarMsg(String mno) {
        // Storing login value as TRUE

        editor.putString(STAR_MSG, mno);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getStarMsg() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(STAR_MSG, pref.getString(STAR_MSG, null));

        return user;
    }

    public void putLastl(String mno) {
        // Storing login value as TRUE

        editor.putString(LASTL, mno);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getLastl() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(LASTL, pref.getString(LASTL, null));

        return user;
    }


    public void putUsType(String mno) {
        // Storing login value as TRUE

        editor.putString(US_TYPE, mno);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getUsType() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(US_TYPE, pref.getString(US_TYPE, null));

        return user;
    }

    public void putMerchid(String mno) {
        // Storing login value as TRUE

        editor.putString(KEY_MERCHID, mno);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getMerchid() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_MERCHID, pref.getString(KEY_MERCHID, null));

        return user;
    }

    public void putAcco(String mno) {
        // Storing login value as TRUE

        editor.putString(KEY_ACCO, mno);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getAcco() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_ACCO, pref.getString(KEY_ACCO, null));

        return user;
    }

    public void putEmail(String mno) {
        // Storing login value as TRUE

        editor.putString(KEY_EMAIL, mno);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getEmail() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));

        return user;
    }

    public void putAccountno(String mno) {
        // Storing login value as TRUE

        editor.putString(KEY_ACCO, mno);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getAccountNo() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_ACCO, pref.getString(KEY_ACCO, null));

        return user;
    }


    public void putTop1(String mno) {
        // Storing login value as TRUE

        editor.putString(KEY_TOP1, mno);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getTop1() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_TOP1, pref.getString(KEY_TOP1, null));

        return user;
    }

    public void putTop2(String mno) {
        // Storing login value as TRUE

        editor.putString(KEY_TOP2, mno);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getTop2() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_TOP2, pref.getString(KEY_TOP2, null));

        return user;
    }

    public void putTop3(String mno) {
        // Storing login value as TRUE

        editor.putString(KEY_TOP3, mno);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getTop3() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_TOP3, pref.getString(KEY_TOP3, null));

        return user;
    }

    public void putMobNo(String mno) {
        // Storing login value as TRUE

        editor.putString(KEY_MOBILE, mno);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getMobNo() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_MOBILE, pref.getString(KEY_MOBILE, null));

        return user;
    }

    public void putUserid(String fulln) {
        // Storing login value as TRUE

        editor.putString(KEY_NEWUSERID, fulln);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getUserid() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_NEWUSERID, pref.getString(KEY_NEWUSERID, null));

        return user;
    }

    public void putFulln(String fulln) {
        // Storing login value as TRUE

        editor.putString(KEY_FULLN, fulln);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getFulln() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_FULLN, pref.getString(KEY_FULLN, null));

        return user;
    }

    public void putBioLog(String mno) {
        // Storing login value as TRUE

        editor.putString(KEY_BIOLOG, mno);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getBioLog() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_BIOLOG, pref.getString(KEY_BIOLOG, null));

        return user;
    }

    public void putDisp(String mno) {
        // Storing login value as TRUE

        editor.putString(KEY_DISP, mno);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getDisp() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_DISP, pref.getString(KEY_DISP, null));

        return user;
    }

    public void putSelAcc(String acc) {
        // Storing login value as TRUE

        editor.putString(SEL_ACCOUNT, acc);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getSelAcc() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(SEL_ACCOUNT, pref.getString(SEL_ACCOUNT, null));

        return user;
    }


    public void putInst(String mno) {
        // Storing login value as TRUE

        editor.putString(KEY_INST, mno);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getInst() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_INST, pref.getString(KEY_INST, null));

        return user;
    }

    public void putTxnFlag(String mno) {
        // Storing login value as TRUE

        editor.putString(KEY_TXPIN, mno);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getTxnFlag() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_TXPIN, pref.getString(KEY_TXPIN, null));

        return user;
    }

    public HashMap<String, String> getTpin() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_TPIN, pref.getString(KEY_TPIN, null));

        return user;
    }

    /**
     * Get stored session data
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_USERID, pref.getString(KEY_USERID, null));
        user.put(KEY_FNAMES, pref.getString(KEY_FNAMES, null));
        // user email id

        user.put(KEY_MOBILE, pref.getString(KEY_MOBILE, null));
        user.put(KEY_WPIN, pref.getString(KEY_WPIN, null));
        // return user
        return user;
    }

    /**
     * Clear session details
     */
    public void removeTimest() {
        editor.remove(KEY_TIMEST);
    }


    public void clearallPref(){
editor.clear().commit();
    }

    public void logoutUser() {
        // Clearing all data from Shared Preferences
        editor.remove(SEL_ACCOUNT);
        editor.remove(KEY_INST);
        editor.remove(KEY_FULLN);
        editor.remove(KEY_TXPIN);
        editor.remove(KEY_MOBILE);

        editor.remove(KEY_TIMEST);
        editor.remove(AGENTID);
        editor.remove(KEY_AIRTIME);
        editor.remove(KEY_BANKS);
        editor.remove(KEY_BILLERS);
        editor.remove(KEY_WALLETS);

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

    public void removekey(String key) {
        // Clearing all data from Shared Preferences
        editor.remove(key);

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
     **/
    // Get Login State
    public Map<String, ?> getAllEntries() {

    Map<String, ?> allentr = pref.getAll();
        return allentr;
}

    public boolean isUserId(){

        return pref.getBoolean(BOOL_USERID, false);
    }
    public boolean isLoggedIn(){

        return pref.getBoolean(IS_LOGIN, false);
    }
    public boolean isTpinPref(){

        return pref.getBoolean(IS_TPINPREF, false);
    }
    public boolean isBioLoggedIn(){

        return pref.getBoolean(IS_BIOLOGIN, false);
    }
    public boolean isAst(){

        return pref.getBoolean(IS_AST, false);
    }
    public boolean isDevReg(){

        return pref.getBoolean(IS_AST, false);
    }
    public boolean isShwBal(){

        return pref.getBoolean(SHWBAL, false);
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
    public boolean isPersNews(String serv){

        return pref.getBoolean(serv, false);
    }

    public void SetEntryPoint(String entry) {
        editor.putString(KEY_ENTRY_POINT, entry);
        editor.commit();
    }

}