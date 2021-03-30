package security;

import android.content.Context;
import android.util.Log;

import com.stanbicagent.ApplicationConstants;
import com.stanbicagent.BuildConfig;
import com.stanbicagent.SessionManagement;
import com.stanbicagent.Utility;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.UUID;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import static security.AESCBCEncryption.base64Decode;
import static security.AESCBCEncryption.base64Encode;
import static security.AESCBCEncryption.decrypt;
import static security.AESCBCEncryption.encrypt;
import static security.AESCBCEncryption.initVector;
import static security.AESCBCEncryption.key;
import static security.AESCBCEncryption.toHex;

public class SecurityLayer {
   // public static boolean isDebug = true;
   public static boolean isDebug = false;
    public final static String KEY_PIV = "pvoke";
    public final static String KEY_PKEY = "pkey";
    public static final String KEY_SIV = "svoke";
    public static final String KEY_SKEY = "skey";
    public static final String KEY_APP_ID = "appid";
    public static final String KEY_DHASH = "DHASH";
    public static final String KEY_INP = "inp";
    public static final String KEY_TOKEN = "token";
    public static final String KEY_SESSION_ID = "sessionID";
    public static final String KEY_REPRINT = "reprint_data";
    public static final String KEY_ACCOUNT_NUMBER = "agentAccountNumber";

    public static String firstLogin(String params, String endpoint, Context context) throws UnsupportedEncodingException {


        String finpoint = "";
        StringBuffer sb = new StringBuffer();
        SessionManagement session = new SessionManagement(context);
        if (Utility.checkInternetConnection(context)) {

            String vers = "2.0.0";
            String year = Utility.getAppVersion(context);
            String hexkey = getrandkey();
            String imei = Utility.getDevImei(context);
            String session_id = UUID.randomUUID().toString();

            session.setString(KEY_SESSION_ID, session_id);
            Log("Imei is " + imei);
            Log("Session ID is " + session_id);
            finpoint = sb.append(ApplicationConstants.NET_URL + endpoint)
                    //   .append(params)
                    .append(toHex(encrypt(key, initVector, params)))
                    .append("/" + Utility.generateHashString(params))
                    .append("/" + toHex(hexkey))
                    .append(ApplicationConstants.CH_KEY)
                    .append("/" + ApplicationConstants.APP_ID)
                    .append("/1212")
                    .append("/" + toHex(encrypt(key, initVector, imei)))
                    .append("/" + toHex(encrypt(key, initVector, session_id)))
                    .append("/" + vers)
                    .append("/" + year)
                    .toString();
        }
        return finpoint;
    }

    public static String firstLoginSecure(String params, String endpoint, Context context) throws UnsupportedEncodingException {
        String finpoint = "";
        if (Utility.checkInternetConnection(context)) {
            StringBuffer sb = new StringBuffer();
            String vers = "2.0.0";
            String year = Utility.getAppVersion(context);
            String hexkey = getrandkey();
            String imei = Utility.getDevImei(context);
            String session_id = UUID.randomUUID().toString();
            SessionManagement session = new SessionManagement(context);
            session.setString(KEY_SESSION_ID, session_id);
            Log("Imei is " + imei);
            Log("Session ID is " + session_id);
            finpoint = sb.append(ApplicationConstants.NET_URL + endpoint)
                    .append(toHex(encrypt(key, initVector, params)))
                    .append("/" + Utility.generateHashString(params))
                    .append("/" + toHex(hexkey))
                    .append(ApplicationConstants.CH_KEY)
                    .append("/" + ApplicationConstants.APP_ID)
                    .append("/1212")
                    .append("/" + toHex(encrypt(key, initVector, imei)))
                    .append("/" + toHex(encrypt(key, initVector, session_id)))
                    .append("/" + vers)
                    .append("/" + year)
                    .toString();
        }
        return finpoint;
    }

    public static String getrandkey() {
        byte[] genkey = null;
        try {
            genkey = generateSessionKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        String sessid = android.util.Base64.encodeToString(genkey, android.util.Base64.NO_WRAP);
        return sessid;

    }

    public static byte[] generateSessionKey() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyGenerator kgen = KeyGenerator.getInstance("AES", "BC");
        kgen.init(256);
        SecretKey key = kgen.generateKey();
        byte[] symmKey = key.getEncoded();
        return symmKey;
    }

    public static String beforeLogin(String params, Context context, String endpoint) throws UnsupportedEncodingException {
        String finpoint = "";
        if (Utility.checkInternetConnection(context)) {
            StringBuffer sb = new StringBuffer();
            String vers = "2.0.0";
            String year = Utility.getAppVersion(context);
            String imei = Utility.getDevImei(context);

            String hexkey = getrandkey();
            try {
                hexkey = toHex(hexkey);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            SessionManagement session = new SessionManagement(context);
            finpoint = sb.append(ApplicationConstants.NET_URL + endpoint)
                    // .append(params)
                    .append(toHex(encrypt(key, initVector, params)))
                    .append("/" + Utility.generateHashString(params))
                    .append("/" + hexkey)
                    .append(ApplicationConstants.CH_KEY)
                    .append("/" + ApplicationConstants.APP_OUTSIDEID)
                    .append("/1212")
                    .append("/" + toHex(encrypt(key, initVector, imei)))

                    .append("/" + vers)
                    .append("/" + year)
                    .toString();
        }
        return finpoint;
    }

    public static String beforeLoginSecure(String params, Context context, String endpoint) throws UnsupportedEncodingException {
        String finpoint = "";
        if (Utility.checkInternetConnection(context)) {
            StringBuffer sb = new StringBuffer();
            String vers = "2.0.0";
            String year = Utility.getAppVersion(context);
            String imei = Utility.getDevImei(context);

            String hexkey = getrandkey();
            try {
                hexkey = toHex(hexkey);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            finpoint = sb.append(ApplicationConstants.NET_URL + endpoint)
                    .append(toHex(encrypt(key, initVector, params)))
                    .append("/" + Utility.generateHashString(params))
                    .append("/" + hexkey)
                    .append(ApplicationConstants.CH_KEY)
                    .append("/" + ApplicationConstants.APP_OUTSIDEID)
                    .append("/1212")
                    .append("/" + toHex(encrypt(key, initVector, imei)))

                    .append("/" + vers)
                    .append("/" + year)
                    .toString();
        }
        return finpoint;
    }

    public static JSONObject decryptFirstTimeLogin(JSONObject jsonobj, Context context) throws Exception {
        SessionManagement session = new SessionManagement(context);
        String status = jsonobj.getString("status");
        String svoke = jsonobj.getString("svoke");
        String input = jsonobj.getString("inp");
        // System.out.println("svoke [" + svoke + "]");
        String pkey = jsonobj.getString("pkey");
        SecurityLayer.Log("pkey [" + pkey + "]");
        String pvoke = jsonobj.getString("pvoke");
        String skey = jsonobj.getString("skey");
        String dhash = jsonobj.getString("DHASH");
        SecurityLayer.Log(input);
        SecurityLayer.Log(pkey);
        JSONObject decjsonobj = new JSONObject();

        String finalresp = "";


        String dehexcab = AESCBCEncryption.toString(pkey);
        SecurityLayer.Log("pkey_dehex [" + dehexcab + "]");
        SecurityLayer.Log("pkey_dehex", dehexcab);
        String pvokecab = AESCBCEncryption.toString(pvoke);
        SecurityLayer.Log("pvoke_cab", pvokecab);
        SecurityLayer.Log("pvokecab [" + pvokecab + "]");
        String pkey_dec = decrypt(key, initVector, AESCBCEncryption.toString(pkey));
        String pvoke_dec = decrypt(key, initVector, AESCBCEncryption.toString(pvoke));
        String sessionkey = decrypt(base64Decode(pkey_dec), base64Decode(pvoke_dec), AESCBCEncryption.toString(skey));
        String sessioniv = decrypt(base64Decode(pkey_dec), base64Decode(pvoke_dec), AESCBCEncryption.toString(svoke));

        session.setString(KEY_PKEY, pkey_dec);//set pkey
        SecurityLayer.Log("setpkey [" + pkey_dec + "]");
        session.setString(KEY_PIV, pvoke_dec);//set piv
        SecurityLayer.Log("setpiv [" + pvoke_dec + "]");
        session.setString(KEY_SKEY, sessionkey);//set skey
        SecurityLayer.Log("setskey [" + sessionkey + "]");
        session.setString(KEY_SIV, sessioniv);
        SecurityLayer.Log("setsiv [" + sessioniv + "]");
        session.setString(KEY_DHASH, dhash);

        finalresp = decrypt(base64Decode(pkey_dec), base64Decode(pvoke_dec), AESCBCEncryption.toString(input));
        JSONObject newjs = new JSONObject(finalresp);
        String tken = newjs.optString("token");
        String nwappid = newjs.optString("appid");
        session.setString("PLAINAPPID",nwappid);
        String encappid = toHex(encrypt(key, initVector, nwappid));
        session.setString("NWAPPID", encappid);
        session.setString(KEY_TOKEN, tken);
        SecurityLayer.Log("pkey_dec [" + pkey_dec + "]");
        SecurityLayer.Log("pvoke_dec [" + pvoke_dec + "");
        SecurityLayer.Log("session key [" + sessionkey + "]");
        SecurityLayer.Log("sessioniv [" + sessioniv + "]");
        SecurityLayer.Log("finalresp [" + finalresp + "]");

        String gen = Utility.generateHashString(finalresp);
        SecurityLayer.Log("Hashing Status [" + gen.equals(dhash) + "]");

        decjsonobj.put("pkey_dec", pkey_dec);
        decjsonobj.put("pvoke_dec", pvoke_dec);
        decjsonobj.put("sessionkey", sessionkey);
        decjsonobj.put("sessioniv", sessioniv);
        decjsonobj.put("finalresp", finalresp);
        decjsonobj.put("hashstatus", gen.equals(dhash));


        return newjs;
        //return decjsonobj;
        //System.out.println(decjsonobj);

    }

    public static String generalLogin(String params, String session_id, Context context, String endpoint) throws Exception {
        String finpoint = "";
        if (Utility.checkInternetConnection(context)) {
            SessionManagement session = new SessionManagement(context);
            String skey = session.getString(SecurityLayer.KEY_SKEY);
            //  String skey = "4UhIX09CelA75Rdao2u+j/vnnkAopFQbbO/nbHnebf4=";
            SecurityLayer.Log("skey", skey);
            String siv = session.getString(SecurityLayer.KEY_SIV);
            //   String siv = "j3at/AyFvk4J7h32D+cECQ==";
            SecurityLayer.Log("siv", siv);
            String pkey = (session.getString(SecurityLayer.KEY_PKEY));
            //  String pkey = "xB8sn12WDeP8gM608ZkFjvd5CE/bxtmDb6MH9kk6R+4=";
            SecurityLayer.Log("pkey", pkey);
            //   String piv = "xLJ6Z4p+wgKik3jMG6V3lw==";
            String piv = (session.getString(SecurityLayer.KEY_PIV));
            SecurityLayer.Log("piv", piv);
            //  String appid = "113260437012100";
            int count = 0;
            String encappid = session.getString("NWAPPID");
            SecurityLayer.Log("appid gott", encappid);
            StringBuffer sb = new StringBuffer();
            String imei = Utility.getDevImei(context);
            byte[] randomKey = base64Decode(skey);
            byte[] randomSIV = base64Decode(siv);
            byte[] dummy_pkey = base64Decode(pkey);
            byte[] dummy_piv = base64Decode(piv);
            // String encryptedUrl = LoginAESProcess.getEncryptedUrlByPropKey(params, randkey);
            String encryptedUrl = encrypt(base64Decode(skey), base64Decode(siv), params);
            SecurityLayer.Log("Base Decode Pkey", new String(base64Decode(pkey)));
            SecurityLayer.Log("Base Decode PIV", new String(base64Decode(piv)));
            SecurityLayer.Log("Base Encode RandomKey", new String(base64Encode(randomKey)));
            String encryptedpkey = toHex(encrypt(base64Decode(pkey), base64Decode(piv), base64Encode(randomKey)));//LoginAESProcess.getEncryptedUrlByPropKey(randkey, pkey);
            String encryptedRandomIV = toHex(encrypt(base64Decode(pkey), base64Decode(piv), base64Encode(randomSIV)));
            String vers = Utility.getAppVersion(context);
            SecurityLayer.Log("encappid", encappid);
            finpoint = sb.append(ApplicationConstants.NET_URL + endpoint)
                    .append(toHex(encryptedUrl))
                    .append("/" + Utility.generateHashString(params))
                    .append("/" + encryptedpkey)
                    .append(ApplicationConstants.CH_KEY)
                    .append("/" + encappid)
                    .append("/1212")
                    .append("/" + toHex(encrypt(dummy_pkey, dummy_piv, imei)))
                    .append("/" + toHex(encrypt(dummy_pkey, dummy_piv, session_id)))
                    .append("/" + encryptedRandomIV)
                    .append("/" + vers)
                    .toString();
        }
        return finpoint;
    }

    public static JSONObject decryptGeneralLogin(JSONObject jsonobj, Context context) throws Exception {
        SessionManagement session = new SessionManagement(context);
        String status = jsonobj.getString("status");
        String svoke = jsonobj.getString("svoke");
        String input = jsonobj.getString("inp");
        Log("svoke [" + svoke + "]");
        //String pkey = jsonobj.getString("pkey");
        //String pvoke = jsonobj.getString("pvoke");
        String skey = jsonobj.getString("skey");
        String dhash = jsonobj.getString("DHASH");
        // System.out.println("pkey ["+pkey+"]\n pvoke ["+pvoke+"]");
        JSONObject decjsonobj = new JSONObject();
        byte[] pkey = base64Decode(session.getString(KEY_PKEY));    //generateSessionKey(); // 256 bit key
        byte[] pinitVector = base64Decode(session.getString(KEY_PIV));
        String finalresp = "";
        String sessionkey = decrypt(pkey, pinitVector, AESCBCEncryption.toString(skey));
        String sessioniv = decrypt(pkey, pinitVector, AESCBCEncryption.toString(svoke));
        finalresp = decrypt(pkey, pinitVector, AESCBCEncryption.toString(input));
        JSONObject newjs = new JSONObject(finalresp);
        String tken = newjs.optString("token");
        session.setString(KEY_TOKEN, tken);
        session.setString(KEY_SKEY, sessionkey);
        session.setString(KEY_SIV, sessioniv);

        ///System.out.println("pkey_dec ["+pkey_dec+"]");
        //System.out.println("pvoke_dec ["+pvoke_dec+"");
        Log("session key [" + sessionkey + "]");
        Log("sessioniv [" + sessioniv + "]");
        Log("finalresp [" + finalresp + "]");
        String gen = Utility.generateHashString(finalresp);
        Log("Hashing Status [" + gen.equals(dhash) + "]");
        decjsonobj.put("sessionkey", sessionkey);
        decjsonobj.put("sessioniv", sessioniv);
        decjsonobj.put("finalresp", finalresp);
        decjsonobj.put("hashstatus", gen.equals(dhash));

        Log(decjsonobj.toString());
        return new JSONObject(finalresp);

    }

    public static String genURLCBC(String params, String endpoint, Context c) {
        String finpoint = "";
        if (Utility.checkInternetConnection(c)) {
            SessionManagement session = new SessionManagement(c);
            String token = session.getString(KEY_TOKEN);
            SecurityLayer.Log("existing_token", token);
            String fnltkt = "2";
            fnltkt = Utility.nextToken(fnltkt);
            SecurityLayer.Log("next_token", fnltkt);
            String skey = session.getString(SecurityLayer.KEY_SKEY);
            SecurityLayer.Log("skey", skey);
            String siv = session.getString(SecurityLayer.KEY_SIV);
            SecurityLayer.Log("siv", siv);
            String pkey = (session.getString(SecurityLayer.KEY_PKEY));
            SecurityLayer.Log("pkey", pkey);
            String piv = (session.getString(SecurityLayer.KEY_PIV));
            SecurityLayer.Log("piv", piv);
            int count = 0;
            String encappid = session.getString("NWAPPID");
            SecurityLayer.Log("appid gen url", encappid);
            System.out.println("appid gen url [" + encappid + "]");
            String encryptedpkey = "";
            String encryptedrandomIV = "";
            String encryptedUrl = "";
            String hash = null;
            String encryptedimei = "";
            String fsess = session.getString(KEY_SESSION_ID);
            SecurityLayer.Log("siv", siv);
            SecurityLayer.Log("appid", encappid);
            StringBuffer sb = new StringBuffer();
            String imei = "Vokez";
            /*Log("Session Key", skey);
            Log("Personal  Key", pkey);
            Log("App ID", encappid);
            Log("Session ID", fsess);
            Log("Params", params);
            Log("Imei", imei);*/
            System.out.println(imei);
            SecurityLayer.Log("Imei chosen", imei);
            String year = Utility.getAppVersion(c);
            Log("vers", year);
            try {
                encryptedpkey = toHex(AESCBCEncryption.encrypt(base64Decode(pkey), base64Decode(piv), AESCBCEncryption.base64Encode(AESCBCEncryption.generateSessionKey())));
                encryptedrandomIV = toHex(AESCBCEncryption.encrypt(base64Decode(pkey), base64Decode(piv), AESCBCEncryption.base64Encode(AESCBCEncryption.generateIV())));
                encryptedUrl = toHex(AESCBCEncryption.encrypt(base64Decode(skey), base64Decode(siv), params));

                hash = Utility.generateHashString(params);
                encryptedimei = toHex(AESCBCEncryption.encrypt(base64Decode(skey), base64Decode(siv), imei));
                fsess = toHex(AESCBCEncryption.encrypt(base64Decode(skey), base64Decode(siv), fsess));
                // year = toHex(year);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String vers = encryptedrandomIV;
            finpoint = endpoint + "/" + encryptedUrl + "/" + hash + "/" + encryptedpkey + ApplicationConstants.CH_KEY + "/" + encappid + "/" + fnltkt + "/" + encryptedimei + "/" + fsess + "/" + vers + "/" + year;
        }
        return finpoint;
    }

    public static JSONObject decryptTransaction(JSONObject jsonobj, Context context) throws Exception {
        SessionManagement session = new SessionManagement(context);
        String status = jsonobj.getString("status");
        String svoke = jsonobj.getString("svoke");
        String input = jsonobj.getString("inp");
        System.out.println("svoke [" + svoke + "]");
        String dhash = jsonobj.getString("DHASH");
        // System.out.println("pkey ["+pkey+"]\n pvoke ["+pvoke+"]");
        JSONObject decjsonobj = new JSONObject();
        byte[] pkey = base64Decode(session.getString(KEY_PKEY));    //generateSessionKey(); // 256 bit key
        byte[] pinitVector = base64Decode(session.getString(KEY_PIV));
        byte[] skey = base64Decode(session.getString(KEY_SKEY));
        String finalresp = "";
        JSONObject data = null;
        if ("S".equals(status)) {
            String sessioniv = decrypt(pkey, pinitVector, AESCBCEncryption.toString(svoke));
            finalresp = decrypt(skey, base64Decode(sessioniv), AESCBCEncryption.toString(input));
            session.setString(KEY_SIV, sessioniv);
            data = new JSONObject(finalresp);
            String token = data.optString("token");
            SecurityLayer.Log("returned_token", token);
            session.setString(KEY_TOKEN, token);
            int count = 0;
            //System.out.println("finalresp [" + finalresp + "]");
            String gen = Utility.generateHashString(finalresp);
            //System.out.println("Hashing Status [" + gen.equals(dhash) + "]");
            decjsonobj.put("pkey_dec", "");
            decjsonobj.put("pvoke_dec", "");
            decjsonobj.put("sessioniv", sessioniv);
            decjsonobj.put("finalresp", finalresp);
            decjsonobj.put("hashstatus", gen.equals(dhash));

        }
        SecurityLayer.Log(decjsonobj.toString());
        String respcode = data.optString("responseCode");
        return data;
    }

    public static void Log(String tag, String message) {
//        if (isDebug) {
//            Log.v(tag, message);
//        }

        if(BuildConfig.DEBUG)
            Log.v(tag, message);

    }

    public static void Log(String message) {
//        if (isDebug) {
//            Log.v("", message);
//        }
        if(BuildConfig.DEBUG)
            Log.v("", message);
    }

    public static JSONObject decryptFirstTimeLoginUnsecure(JSONObject jsonobj, Context context) throws Exception {
        SessionManagement session = new SessionManagement(context);
        session.setString("NWAPPID", "NA");
        return jsonobj;
    }

    public static String generalLoginUnsecure(String params, String session_id, Context context, String endpoint) throws Exception {
        String finpoint = "";
        SessionManagement session = new SessionManagement(context);
        StringBuffer sb = new StringBuffer();
        finpoint = sb.append(ApplicationConstants.NET_URL + endpoint)
                .append(params)
                .toString();
        SecurityLayer.Log("finalfinpoint", finpoint);
        return finpoint;
    }

    public static JSONObject decryptGeneralLoginUnsecure(JSONObject jsonobj, Context context) throws Exception {
        SessionManagement session = new SessionManagement(context);
        return jsonobj;
    }

    public static String genURLCBCUnsecure(String params, String endpoint, Context c) {
        String finpoint = "";
        SessionManagement session = new SessionManagement(c);
        finpoint = endpoint + params;
        StringBuffer sb = new StringBuffer();
        finpoint = sb.append(ApplicationConstants.NET_URL + endpoint + "/")
                .append(params)
                .toString();
        return finpoint;
    }

    public static JSONObject decryptTransactionUnsecure(JSONObject jsonobj, Context context) throws Exception {
        SessionManagement session = new SessionManagement(context);
        return jsonobj;
    }
}
