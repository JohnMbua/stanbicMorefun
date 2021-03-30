package security;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class Util {
	protected DataInputStream serverIn = null;
	protected DataOutputStream serverOut = null;
	protected String resp = null;
	public static final String[] hexStrings;

	static {
		hexStrings = new String[256];
		for (int i = 0; i < 256; i++ ) {
			StringBuilder d = new StringBuilder(2);
			char ch = Character.forDigit(((byte)i >> 4) & 0x0F, 16);
			//d.append(Character.toUpperCase(ch));
			d.append(Character.toUpperCase(ch));
			ch = Character.forDigit((byte)i & 0x0F, 16);
			d.append(Character.toUpperCase(ch));
			hexStrings[i] = d.toString();
		}

	}

	public static String hexdump (byte[] b, int offset, int len) {
		StringBuilder sb    = new StringBuilder ();
		StringBuilder hex   = new StringBuilder ();
		StringBuilder ascii = new StringBuilder ();
		String sep         = "  ";
		String lineSep     = System.getProperty ("line.separator");

		for (int i=offset; i<len; i++) {
			hex.append(hexStrings[(int)b[i] & 0xFF]);
			hex.append (' ');
			char c = (char) b[i];
			ascii.append ((c >= 32 && c < 127) ? c : '.');

			int j = i % 16;
			switch (j) {
			case 7 :
				hex.append (' ');
				break;
			case 15 :
				sb.append (hexOffset (i));
				sb.append (sep);
				sb.append (hex.toString());
				sb.append (' ');
				sb.append (ascii.toString());
				sb.append (lineSep);
				hex   = new StringBuilder ();
				ascii = new StringBuilder ();
				break;
			}
		}
		if (hex.length() > 0) {
			while (hex.length () < 49)
				hex.append (' ');

			sb.append (hexOffset (len));
			sb.append (sep);
			sb.append (hex.toString());
			sb.append (' ');
			sb.append (ascii.toString());
			sb.append (lineSep);
		}
		return sb.toString();
	}

	private static String hexOffset (int i) {
		i = (i>>4) << 4;
		int w = i > 0xFFFF ? 8 : 4;
		try {
			return zeropad (Integer.toString (i, 16), w);
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	private static String zeropad(String s, int len)  {
		return padding(s, len, '0', true);
	}


	public static String padding(String str,
			int len,
			char padChar,
			boolean isLeftPad){
		if (str != null){
			str = str.trim();
			StringBuffer sb = new StringBuffer();
			if(!isLeftPad){
				sb.append(str);
			}
			for (int i = 0; i < len-str.length(); i++){
				sb.append(padChar);
			}
			if(isLeftPad){
				sb.append(str);
			}
			str = sb.toString();
		}
		return str;

	}

	public static String getBitMap(String bitmap) {
		bitmap=bitmap.toUpperCase();
		char[] bits = bitmap.toCharArray();
		char [] hexval={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
		String [] binaryVal ={"0000","0001","0010","0011","0100","0101","0110","0111",
				"1000","1001","1010","1011","1100","1101","1110","1111"};
		StringBuilder sb = new StringBuilder() ;
		for(int i=0;i<bitmap.length();i++) {
			for(int j=0;j<16;j++) {
				if(bits[i]==hexval[j]){
					sb.append(binaryVal [j]);
				}
			}
		}
		String bitMapFields = sb.toString();
		sb.setLength(0);
		for (int i = 0; i < bitMapFields.length(); i++)
		{
			if('1' == bitMapFields.charAt(i))
			{
				sb.append(i+1).append(",");
			}
		}
		sb =  sb.deleteCharAt(sb.length()-1);
		bitMapFields = sb.toString();
		sb = null;
		return bitMapFields;
	}

	public static byte[] xor(byte[] op1, byte[] op2) {
		//make 2 arrays equal .. left justify with pad 0
		byte[] result;
		if (op1.length > op2.length) {
			byte[] a1 = new byte[op1.length];
			System.arraycopy(op2, 0, a1, 0, op2.length);
			return xor(op1, a1);
		} else if (op2.length > op1.length) {
			byte[] a1 = new byte[op2.length];
			System.arraycopy(op1, 0, a1, 0, op1.length);
			return xor(a1, op2);
		}
		result = new byte[op1.length];
		for (int i = 0; i < op1.length; i++) {
			result[i] = (byte) (op1[i] ^ op2[i]);
		}
		return result;

	}

	public static String hexString(byte[] b) {
		StringBuilder d = new StringBuilder(b.length * 2);
		for (byte aB : b) {
			d.append(hexStrings[(int) aB & 0xFF]);
		}
		return d.toString();
	}


	public static final void split(byte abyte0[], byte left[], byte[] right) {

		System.arraycopy(abyte0, 0, left, 0, abyte0.length / 2);
		System.arraycopy(abyte0, abyte0.length / 2, right, 0,abyte0.length / 2);
		//return null;
	}

	public static final byte[] concat(byte abyte0[], byte abyte1[]) {
		byte[] ai = new byte[abyte0.length + abyte1.length];
		System.arraycopy(abyte0, 0, ai, 0, abyte0.length);
		System.arraycopy(abyte1, 0, ai, abyte0.length, abyte1.length);
		return ai;
	}

	public static final byte[] concat(byte abyte0[], byte abyte1[],
			byte[] abyte2) {
		byte[] ai = new byte[abyte0.length + abyte1.length];
		System.arraycopy(abyte0, 0, ai, 0, abyte0.length);
		System.arraycopy(abyte1, 0, ai, abyte0.length, abyte1.length);
		return concat(ai, abyte2);
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

	public static void main(String[] args)
	{
		System.out.println(hexString("ISO_0".getBytes()));
	}
}
