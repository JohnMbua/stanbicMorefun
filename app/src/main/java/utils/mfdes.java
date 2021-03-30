package utils;

import javax.crypto.Cipher;

import security.CryptoUtil;
import security.SecurityLayer;
import security.Util;

import static security.EncryptTransactionPin.rightPadding;

public class mfdes {
    public mfdes() {
    }

    public static String encrypt(String key, String password, char pad) throws Exception {
        byte[] byteKey = hex2byte(key);
        byte[] bytePwd = hex2byte(rightPadding(password, 16, pad));
        byte[] enc = CryptoUtil.desede(bytePwd, byteKey, Cipher.ENCRYPT_MODE);
        return Util.hexString(enc);
    }

    public static String decrypt(String key, String password) throws Exception {
        byte[] byteKey = hex2byte(key);
        byte[] bytePwd = hex2byte(password);
        byte[] dec = CryptoUtil.desede(bytePwd, byteKey, Cipher.DECRYPT_MODE);
        return Util.hexString(dec);
    }

    public static byte[] hex2byte (String s) {
        if (s.length() % 2 == 0) {
            return hex2byte (s.getBytes(), 0, s.length() >> 1);
        } else {
            // Padding left zero to make it even size #Bug raised by tommy
            return hex2byte("0"+s);
        }
    }

    public static byte[] hex2byte (byte[] b, int offset, int len) {
        byte[] d = new byte[len];
        for (int i=0; i<len*2; i++) {
            int shift = i%2 == 1 ? 0 : 4;
            d[i>>1] |= Character.digit((char) b[offset+i], 16) << shift;
        }
        return d;
    }
}