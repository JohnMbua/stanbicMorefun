package com.stanbicagent;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;

import android.os.Bundle;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.morefun.yapi.engine.DeviceInfoConstrants;
import com.morefun.yapi.engine.DeviceServiceEngine;

import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import security.EncryptTransactionPin;
import security.SecurityLayer;

/**
 * Class which has Utility methods
 */
public class Utility {
	private static Pattern pattern;
	private static Matcher matcher;
	//Email Pattern
	public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
	private static final String EMAIL_PATTERN =
			"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
					+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	private static final String NUMBER_PATTERN = "[0-9]+";
	private static final String LWCASE_PATTERN = "[a-z]+";
	public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 124;
	public static final String KEY_TOKEN = "token";
	private static final String SPEC_CHARPATTERN = "[a-zA-Z0-9]+";
	public static final String AGMOB = "agmobno";
	private static SessionManagement session;

	/**
	 * Validate Email with regular expression
	 *
	 * @param email
	 * @return true for Valid Email and false for Invalid Email
	 */
	public static boolean validate(String email) {
		pattern = Pattern.compile(EMAIL_PATTERN);
		matcher = pattern.matcher(email);
		return matcher.matches();

	}

	public static boolean isValidWord(String str) {
//  return str.matches(".*[a-zA-Z]+.*"); // match a number with optional
		// '-' and decimal.
		return str.matches("^[a-zA-Z0-9]*$"); // match a number with optional
	}

	public static boolean isValidWordHyphen(String str) {
//  return str.matches(".*[a-zA-Z]+.*"); // match a number with optional
		// '-' and decimal.
		return str.matches("^[a-zA-Z0-9_]*$"); // match a number with optional
	}

	public static boolean isValidWordSpace(String str) {
//  return str.matches(".*[a-zA-Z]+.*"); // match a number with optional
		// '-' and decimal.
		return str.matches("^[a-zA-Z0-9 ]*$"); // match a number with optional
	}

	public static boolean isValidWordStudId(String str) {
//  return str.matches(".*[a-zA-Z]+.*"); // match a number with optional
		// '-' and decimal.

		return str.matches("^[a-zA-Z0-9-|/\\\\_]*$"); // match a number with optional
	}

	public static boolean checknum(String num) {
		pattern = Pattern.compile(NUMBER_PATTERN);
		matcher = pattern.matcher(num);
		return matcher.matches();

	}

	public static boolean checklwcase(String tstcase) {
		pattern = Pattern.compile(LWCASE_PATTERN);
		matcher = pattern.matcher(tstcase);
		return matcher.matches();

	}

	public static boolean checkemail(String tstcase) {
		pattern = Pattern.compile(EMAIL_PATTERN);
		matcher = pattern.matcher(tstcase);
		return matcher.matches();

	}

	/**
	 * Checks for Null String object
	 *
	 * @param txt
	 * @return true for not null and false for null String object
	 */
	public static boolean isNotNull(String txt) {
		return txt != null && txt.trim().length() > 0 ? true : false;
	}

	public static String MaskAccNo(String pan) {

		char[] myNameChars = pan.toCharArray();
		myNameChars[6] = 'x';
		myNameChars[7] = 'x';
		myNameChars[8] = 'x';
		myNameChars[9] = 'x';
		myNameChars[10] = 'x';
		myNameChars[11] = 'x';
		String newpan = String.valueOf(myNameChars);
		return newpan;
	}

    public static String MaskPan(String pan) {

        char[] myNameChars = pan.toCharArray();
        myNameChars[4] = 'x';
        myNameChars[5] = 'x';
        myNameChars[6] = 'x';
        myNameChars[7] = 'x';
        myNameChars[8] = 'x';
        myNameChars[9] = 'x';
        myNameChars[10] = 'x';
        myNameChars[11] = 'x';
        String newpan = String.valueOf(myNameChars);
        return newpan;
    }
	public static boolean isEmulator() {
		return Build.FINGERPRINT.startsWith("generic")
				|| Build.FINGERPRINT.startsWith("unknown")
				|| Build.MODEL.contains("google_sdk")
				|| Build.MODEL.contains("Emulator")
				|| Build.MODEL.contains("Android SDK built for x86")
				|| Build.MANUFACTURER.contains("Genymotion")
				|| (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
				|| "google_sdk".equals(Build.PRODUCT);
	}

	public static boolean isRooted() {
		return findBinary("su");
	}


	public static boolean findBinary(String binaryName) {
		boolean found = false;
		if (!found) {
			String[] places = {"/sbin/", "/system/bin/", "/system/xbin/", "/data/local/xbin/",
					"/data/local/bin/", "/system/sd/xbin/", "/system/bin/failsafe/", "/data/local/"};
			for (String where : places) {
				if (new File(where + binaryName).exists()) {
					found = true;
					break;
				}
			}
		}
		return found;
	}

	public static boolean findweakPin(String pin) {
		boolean strstrong = true;
		if (pin.equals("12345") || pin.equals("00000")) {
			strstrong = false;
		}
		return strstrong;
	}

	public static int containerHeight(MainActivity ba) {
		DisplayMetrics dm = new DisplayMetrics();
		ba.getWindowManager().getDefaultDisplay().getMetrics(dm);
		//Toast.makeText(ba.getApplicationContext(), "Screen Height of " + Build.MANUFACTURER + "  is "+Integer.toString(dm.heightPixels) , Toast.LENGTH_LONG).show();

		//get predefined value
		double ratio = 15;

		//check if there is cached value; using SnappyDB


		return (int) (dm.heightPixels / ratio);
	}

	public static JSONArray getNubanAlgo(String account) {

		JSONArray arr = new JSONArray();
		try {
			String nubanprefix = "044|014|023|063|050|040|011|214|070|085|058|069|056|082|076|084|221|068|232|033|032|035|057|215";
			String respectivebanks = "Access Bank|Afribank|Citibank|Diamond Bank|Ecobank|Equitorial Trust|First Bank of Nigeria|FCMB|Fidelity Bank|Finbank|Guaranty Trust Bank|Intercontinental Bank|Oceanic Bank|Keystone Bank|Skye Bank|Spring Bank|StanbicIBTC|Standard Chartered Bank|Sterling Bank|United Bank of Africa|Union Bank|Wema Bank|Zenith Bank|Unity Bank";
			//	String account = "2009415379";
			String nubanserial = account.substring(0, account.length() - 1);
			int checkdigit = Integer.parseInt(account.substring(account.length() - 1)); //9
			String allbankspref[] = nubanprefix.split("\\|");
			String allbanksprefname[] = respectivebanks.split("\\|");
			int numberofmatchedbanks = 0;
			int constants[] = {3, 7, 3, 3, 7, 3, 3, 7, 3, 3, 7, 3};
			String prependprefix = null;
			JSONObject obj = new JSONObject();

			JSONObject obj2 = null;
			for (int y = 0; y < allbankspref.length; y++) {
				prependprefix = allbankspref[y] + nubanserial;
				int count = 0, step1 = 0, x = 0;
				for (char ch : prependprefix.toCharArray()) {
					x = Character.getNumericValue(ch) * constants[count];

					step1 = step1 + x;
					count++;
				}

				int step3 = 10 - (step1 % 10);
				if ((step3 == checkdigit) || (step3 == 10)) {
					obj2 = new JSONObject();
					obj2.put("sortcode", allbankspref[y]);
					obj2.put("name", allbanksprefname[y]);
					arr.put(obj2);

				}
				obj2 = null;

			}

			obj.put("matchedbanks", arr);
			System.out.println(obj.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return arr;
	}

	public static double round(double value, int places) {
		if (places < 0) throw new IllegalArgumentException();

		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	public static String returnNumberFormat(String amount) {
		String fmamo = "0.00";
		try {
			Double amou = Double.parseDouble(amount);
			if (amou > 0) {

				DecimalFormat df = new DecimalFormat("#,###.00");

				fmamo = df.format(amou);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return fmamo;
	}


	public static Number returnOldNumberFormat(String amount) {
		Number number = 0;
		try {
			NumberFormat format = NumberFormat.getInstance(Locale.US);
			number = format.parse(amount);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return number;
	}

	public static boolean checkInternetConnection(final Context c) {
		ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm.getActiveNetworkInfo() != null
				&& cm.getActiveNetworkInfo().isAvailable()
				&& cm.getActiveNetworkInfo().isConnected()) {

			return true;
		} else {
			((Activity) c).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(
							c,
							"No Internet Connection. Please check your internet settings",
							Toast.LENGTH_LONG).show();
				}
			});
		}
		return false;
	}

	public static void hideKeyboardFrom(Context context, View view) {
		if (context != null) {
			InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	public static String getDevImei(Context c) {
//		TelephonyManager telephonyManager = (TelephonyManager) c.getSystemService(c.TELEPHONY_SERVICE);
//		String imei = telephonyManager.getDeviceId();
		String imei=  ApplicationClass.get().getGlobalVals().getSimSerial();
		return imei;
	}


	public static String getFinAppid(Context c) {
		SessionManagement sess = new SessionManagement(c);
		final String appid = sess.getString("NWAPPID");
		return appid;
	}


	public static String getNewAppID(Context c) {
		SessionManagement sess = new SessionManagement(c);
		final String appid = sess.getString("NWAPPID");
		return appid;
	}

	public static String getPlainAppid(Context c) {
		SessionManagement sess = new SessionManagement(c);
		final String appid = sess.getString("PLAINAPPID");
		return appid;
	}

	public static String getMacAddress(Context c) {
		WifiManager wifiManager = (WifiManager) c.getSystemService(c.WIFI_SERVICE);
		WifiInfo wInfo = wifiManager.getConnectionInfo();
		String mac = wInfo.getMacAddress();
		if (mac == null || mac.equals("")) {
			mac = "0.0.0.0";
		}
		return convertStringFromNull(mac);
	}

	public static String getIP(Context c) {
		WifiManager wifiManager = (WifiManager) c.getSystemService(c.WIFI_SERVICE);

		String ip = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
		if (ip == null || ip.equals("")) {
			ip = "0.0.0.0";
		}
		return convertStringFromNull(ip);
	}

	public static String getSerial() {
		String build = Build.SERIAL;
		return build;
	}

	public static boolean checkStateCollect(String servid) {
		boolean trf = false;
		if (servid.equals("3")) {
			trf = true;
		} else {
			trf = false;
		}
		return trf;
	}

	public static String getDevVersion() {
		int bld = Build.VERSION.SDK_INT;
		String blid = Integer.toString(bld);
		return convertStringFromNull(blid);
	}

	public static String getDevModel() {
		String myDeviceModel = Build.MODEL;
		return convertStringFromNull(myDeviceModel);
	}


	public static String convertMobNumber(String mobno) {
		String newno = mobno.substring(mobno.length() - 10);
		return newno;
	}

	public static String convertProperNumber(String mobno) {
		String newno = mobno.replaceAll(",", "");
		return newno;
	}

	public static String convertStringFromNull(String s) {
		if (s == null) {
			s = "";
		}
		return s;
	}

	public static String b64_sha256(String inputString) {
		String outputString = null;
		if (inputString != null) {
			outputString = Base64.encodeToString(
					DigestUtils.sha256(inputString), Base64.DEFAULT).trim();
		} else {
			System.out.println("Input String Missing for b64_sha256");
		}

		try {
			outputString = toHex(outputString.substring(0, outputString.length() - 1));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return outputString;

	}

	public static String gettUtilAgentId(Context c) {
		session = new SessionManagement(c);
		HashMap<String, String> defa = session.getAgentID();
		String defac = defa.get(SessionManagement.AGENTID);
		return defac;
	}

	public static String errornexttoken() {
		String token = session.getString(KEY_TOKEN);
		SecurityLayer.Log("existing_token", token);
		String fnltkt = token;
		fnltkt = Utility.nextToken(fnltkt);
		String finall = Utility.nextToken(fnltkt);
		session.setString(KEY_TOKEN, finall);
		return fnltkt;
	}

	public static String gettUtilUserId(Context c) {
		session = new SessionManagement(c);
		HashMap<String, String> defa = session.getUserIdd();
		String defac = defa.get(SessionManagement.KEY_USERID);
		return defac;
	}

	public static String getBase64Image(Context c) {
		session = new SessionManagement(c);
		String sdf = session.getString("Base64image");
		if (sdf == null || sdf.equals("")) {
			sdf = "N";
		} else {
			try {
				sdf = toHex(sdf);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return sdf;
	}

	public static String gettUtilMobno(Context c) {
		session = new SessionManagement(c);
		String defac = session.getString(AGMOB);
		return defac;
	}


	public static String gettUtilEmail(Context c) {
		session = new SessionManagement(c);
		HashMap<String, String> defa = session.getEmail();
		String defac = defa.get(SessionManagement.KEY_EMAIL);
		return defac;
	}

	public static String gettUtilCustname(Context c) {
		session = new SessionManagement(c);
		HashMap<String, String> defa = session.getCustName();
		String defac = defa.get(SessionManagement.KEY_CUSTNAME);
		return defac;
	}

	public static String getLastl(Context c) {
		session = new SessionManagement(c);
		HashMap<String, String> defa = session.getLastl();
		String defac = defa.get(SessionManagement.LASTL);
		return defac;
	}

	public static String getAcountno(Context c) {
		session = new SessionManagement(c);
		HashMap<String, String> defa = session.getAccountNo();
		String defac = defa.get(SessionManagement.KEY_ACCO);
		return defac;
	}

	public static String toHex(String arg) throws UnsupportedEncodingException {
		return String.format("%040x", new BigInteger(1, arg.getBytes("UTF-8")));
	}

	public static String convertDate(String date) {
		String fdate = date;
		SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date datee = null;
		try {
			datee = dt.parse(date);
			SimpleDateFormat dt1 = new SimpleDateFormat("dd MMMM yyyy hh:mm");

			fdate = dt1.format(datee);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		// *** same for the format String below
		return fdate;
	}

	public static String convertPerfDate(String date) {
		String fdate = date;
		SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
		Date datee = null;
		try {
			datee = dt.parse(date);
			SimpleDateFormat dt1 = new SimpleDateFormat("dd/MM");

			fdate = dt1.format(datee);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		// *** same for the format String below
		return fdate;
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public static boolean checkCameraPermission(final Context context) {
		int currentAPIVersion = Build.VERSION.SDK_INT;
		if (currentAPIVersion >= Build.VERSION_CODES.M) {
			if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
				if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.CAMERA)) {
					AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
					alertBuilder.setCancelable(true);
					alertBuilder.setTitle("Permission necessary");
					alertBuilder.setMessage("Permission to use camera is necessary");
					alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
						@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
						public void onClick(DialogInterface dialog, int which) {
							ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
						}
					});
					AlertDialog alert = alertBuilder.create();
					alert.show();
				} else {
					ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
				}
				return false;
			} else {
				return true;
			}
		} else {
			return true;
		}
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public static boolean checkWritePermission(final Context context) {
		int currentAPIVersion = Build.VERSION.SDK_INT;
		if (currentAPIVersion >= Build.VERSION_CODES.M) {
			if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
				if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
					AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
					alertBuilder.setCancelable(true);
					alertBuilder.setTitle("Permission necessary");
					alertBuilder.setMessage("Permission to store camera images is necessary");
					alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
						@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
						public void onClick(DialogInterface dialog, int which) {
							ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
						}
					});
					AlertDialog alert = alertBuilder.create();
					alert.show();
				} else {
					ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
				}
				return false;
			} else {
				return true;
			}
		} else {
			return true;
		}
	}

	public static String getAppVersion(final Context context) {
		String bvers = "";
		try {
			PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			bvers = pInfo.versionName;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return bvers;
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public static boolean checkWriteStoragePermission(final Context context) {
		int currentAPIVersion = Build.VERSION.SDK_INT;
		if (currentAPIVersion >= Build.VERSION_CODES.M) {
			if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
				if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
					AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
					alertBuilder.setCancelable(true);
					alertBuilder.setTitle("Permission necessary");
					alertBuilder.setMessage("External storage permission is necessary");
					alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
						@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
						public void onClick(DialogInterface dialog, int which) {
							ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
						}
					});
					AlertDialog alert = alertBuilder.create();
					alert.show();
				} else {
					ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
				}
				return false;
			} else {
				return true;
			}
		} else {
			return true;
		}
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public static boolean checkPermission(final Context context) {
		int currentAPIVersion = Build.VERSION.SDK_INT;
		if (currentAPIVersion >= Build.VERSION_CODES.M) {
			if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
				if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
					AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
					alertBuilder.setCancelable(true);
					alertBuilder.setTitle("Permission necessary");
					alertBuilder.setMessage("External storage permission is necessary");
					alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
						@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
						public void onClick(DialogInterface dialog, int which) {
							ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
						}
					});
					AlertDialog alert = alertBuilder.create();
					alert.show();
				} else {
					ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
				}
				return false;
			} else {
				return true;
			}
		} else {
			return true;
		}
	}


	public static String returnCustname(String sesn) {
		if (!(sesn == null)) {
			if (sesn.contains(" ")) {
				String fname = sesn.substring(0, sesn.indexOf(" "));
				String lname = sesn.substring(sesn.lastIndexOf(" "), sesn.length());
				SecurityLayer.Log(" SessFname is", fname);
				SecurityLayer.Log("L Sessname is", lname);

				sesn = fname;
			}
		} else {
			sesn = "";
		}
		return sesn;
	}

	public static String convertTxnCodetoServ(String txncode) {
		if (txncode.equals("FTINTRABANK")) {
			txncode = "Access BankTransfer";
		} else if (txncode.equals("FTINTERBANK")) {
			txncode = "Other Bank";
		} else if (txncode.equals("CWDBYACT")) {
			txncode = "Cash Withdrawal";
		} else if (txncode.equals("CASHDEP")) {
			txncode = "Cash Deposit";
		} else if (txncode.equals("MMO")) {
			txncode = "Airtime";
		} else if (txncode.equals("BILLPAYMENT")) {
			txncode = "Pay Bills";
		} else if (txncode.equals("CWDOTRBNK")) {
			txncode = "Other Bank Withdrawal";
		}

		return txncode;
	}

	public static String nextToken(String token) {
		String s = "000000";
		try {
			Integer tokenNo = Integer.parseInt(token);
			tokenNo = (tokenNo < 9999) ? tokenNo + 1 : 1;
			return s.substring(0, s.length() - tokenNo.toString().length()) + tokenNo.toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw new NumberFormatException("Invalid Token Number");
		}
	}

	public static String generateHashString(String data) throws UnsupportedEncodingException {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] dataInBytes = data.getBytes("UTF-8");
			md.update(dataInBytes);

			byte[] mdbytes = md.digest();

			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < mdbytes.length; i++) {
				hexString.append(Integer.toHexString(0xFF & mdbytes[i]));
			}

			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static boolean checkUserLocked(String respcode) {
		boolean chklock = false;
		if (respcode.equals("991") || respcode.equals("92") || respcode.equals("993")) {
			chklock = true;
		}
		return chklock;
	}

	public static boolean check(String respcode) {
		boolean chklock = false;
		if (respcode.equals("991") || respcode.equals("92") || respcode.equals("993")) {
			chklock = true;
		}
		return chklock;
	}

	public static String CheckNumberZero(String mobno) {
		String firstLetter = String.valueOf(mobno.charAt(0));
		int nxtlngth = mobno.length();
		if (firstLetter.equals("0")) {
			mobno = mobno.substring(1, nxtlngth);
		}
		return mobno;
	}

	public static String appendSlash(String... args) {
		String fslash = null;
		int counter = 0;
		for (String arg : args) {
			if (counter == 0) {
				fslash = arg;
			} else {
				fslash = "/" + arg;
			}
			counter++;
		}
		return fslash;
	}

	public static String roundto2dp(String number) {
		String fins = "0.00";
		try {
			double nmb = Double.parseDouble(number);
			double rounded = round(nmb, 3);
			fins = Double.toString(rounded);
		} catch (Exception ex) {

		}
		return fins;
	}

	public static String getencpin(String finpin) {


		String outputString = null;
		if (finpin != null) {
			outputString = Base64.encodeToString(
					DigestUtils.sha256(finpin), Base64.DEFAULT).trim();
		} else {
			System.out.println("Input String Missing for b64_sha256");
		}

		try {
			outputString = toHex(outputString.substring(0, outputString.length() - 1));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return outputString;
	}


	public static String getencryptedpin(String finpin, String key) {


		String encrypted = null;
		try {

			encrypted = EncryptTransactionPin.encrypt(key, finpin, 'F');
			System.out.println("Encrypt Pin " + EncryptTransactionPin.encrypt(key, finpin, 'F'));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return encrypted;
	}


	static public String changeDate(String olddate) {
		String changeddate = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (Utility.isNotNull(olddate)) {
			String dateInString = olddate;


			try {
				Date datefrom = sdf.parse(dateInString);
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm");
				changeddate = dateFormat.format(datefrom);
				System.out.println(changeddate);

				//   txtdtdiff = Integer.toString(dtdiff);

			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		return changeddate;
	}


	public static String convertBVNDate(String date){
		String fdate = date;
		SimpleDateFormat dt = new SimpleDateFormat("dd-MMM-yy");
		Date datee = null;
		try {
			datee = dt.parse(date);
			SimpleDateFormat dt1 = new SimpleDateFormat("yyyyMMdd");

			fdate = dt1.format(datee);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		// *** same for the format String below
		return fdate;
	}

	public static boolean compareversionsupdate(String server, String device) {
		String[] v1 = device.split("\\.");
		String[] v2 = server.split("\\.");
		boolean chk = false;
		if (v1.length == v2.length) {
			chk = false;
		}
		for (int pos = 0; pos < v1.length; pos++) {
			// compare v1[pos] with v2[pos] as necessary
			if (Integer.parseInt(v1[pos]) > Integer.parseInt(v2[pos])) {
				chk = false;
			} else if (Integer.parseInt(v1[pos]) < Integer.parseInt(v2[pos])) {
				chk = true;
			}
		}
		return chk;
	}


	public static boolean compareversionsupdatenew(String server, String device) {
		String dev = device.replace(".", "");
		String serv = server.replace(".", "");


		boolean chk = false;
		if (Integer.parseInt(dev) < Integer.parseInt(serv)) {
			chk = true;
		} else {
			chk = false;
		}

		return chk;
	}

    static DeviceServiceEngine mSDKManager;

    public static String getSerialNo() {
        String serial_no = "";

        mSDKManager = SDKManager.getInstance().getDeviceServiceEngine();
        if (mSDKManager == null) {
            SecurityLayer.Log("MF_SDKManager", "ServiceEngine is Null");
           return null;
        }
        Bundle devInfo = null;
        try {
            devInfo = mSDKManager.getDevInfo();
            serial_no = devInfo.getString(DeviceInfoConstrants.COMMOM_SN);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        if(BuildConfig.DEBUG)
            serial_no = "19990004";
//            serial_no="01223125";
        else
            serial_no = serial_no.substring(serial_no.length() - 8);


        return serial_no;
    }

}
