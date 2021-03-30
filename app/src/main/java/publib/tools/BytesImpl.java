package publib.tools;

import java.io.UnsupportedEncodingException;

import publib.LoggerUtils;

public class BytesImpl {
	private byte[] refbytes = null;
	private int curIndex = 0;

	public int getCurIndex() {
		return this.curIndex;
	}

	public BytesImpl(byte[] paramArrayOfByte) {
		this.curIndex = 0;
		this.refbytes = paramArrayOfByte;
	}

	public void setCurIndex(int paramInt) {
		this.curIndex = paramInt;
	}

	public void addbyte(byte paramByte) {
		this.refbytes[this.curIndex] = paramByte;
		this.curIndex += 1;
	}

	public void addbytes(byte[] paramArrayOfByte) {
		if (paramArrayOfByte.length <= 0)
			return;
		System.arraycopy(paramArrayOfByte, 0, this.refbytes, this.curIndex,
				paramArrayOfByte.length);
		this.curIndex += paramArrayOfByte.length;
	}

	public void addbytes(byte[] paramArrayOfByte, int offset, int length) {
		if (paramArrayOfByte.length <= 0)
			return;
		System.arraycopy(paramArrayOfByte, offset, this.refbytes,
				this.curIndex, length);
		this.curIndex += length;
	}

	public boolean IsEOF() {

		if (this.curIndex >= this.refbytes.length) {

			return true;
		}

		return false;
	}

	public byte getbyte() {
		if (this.curIndex >= this.refbytes.length)
			return -1;
		return this.refbytes[(this.curIndex++)];
	}

	public byte[] getbytes(int length) {

		if (this.curIndex >= this.refbytes.length)
			return null;
		byte[] retb = new byte[length];
		System.arraycopy(refbytes, this.curIndex, retb, 0, length);
		this.curIndex += length;

		return retb;
	}

	public int readbyte() {
		if (this.curIndex >= this.refbytes.length)
			return -1;
		return this.refbytes[(this.curIndex++)] & 0xFF;
	}

	public void movCur(int paramInt) {
		this.curIndex += paramInt;
	}

	/**
	 * 增加字符串到bytes
	 * 
	 * @param paramString
	 *            字符串信息
	 * @param paramInt
	 *            固定长度,0则增加字符串的长度
	 * @throws UnsupportedEncodingException
	 */
	public void addString(String paramString, int paramInt)
			throws UnsupportedEncodingException {

		LoggerUtils.d("[addString]:" + paramString);

		if (paramString == null) {
			// 空处理
			this.curIndex += paramInt;
			return;
		}

		if (paramString == "") {
			if (paramInt != 0)
				this.curIndex += paramInt;
			return;
		}

		byte[] arrayOfByte = paramString.getBytes("GBK"); 
		if (paramInt == 0) {
			System.arraycopy(arrayOfByte, 0, this.refbytes, this.curIndex,
					arrayOfByte.length);
			this.curIndex += arrayOfByte.length;
		} else {
			System.arraycopy(arrayOfByte, 0, this.refbytes, this.curIndex,
					arrayOfByte.length);
			this.curIndex += paramInt;
		}
	}

	public void addString(String paramString, int paramInt, byte paramByte)
			throws UnsupportedEncodingException {
		if (paramString == "") {
			if (paramInt != 0)
				this.curIndex += paramInt;
			return;
		}

		byte[] arrayOfByte = paramString.getBytes("GBK");

		if (paramInt == 0) {
			System.arraycopy(arrayOfByte, 0, this.refbytes, this.curIndex,
					arrayOfByte.length);
			this.curIndex += arrayOfByte.length;
		} else {
			System.arraycopy(arrayOfByte, 0, this.refbytes, this.curIndex,
					arrayOfByte.length);
			for (int i = this.curIndex + arrayOfByte.length; i < this.curIndex
					+ paramInt; ++i)
				this.refbytes[i] = paramByte;
			this.curIndex += paramInt;
		}
	}

	public String getHexString(int paramInt)
			throws UnsupportedEncodingException {
		if (paramInt == 0)
			return "";
		String str = Byte2HexString(this.refbytes, this.curIndex, paramInt);
		this.curIndex += paramInt;
		return str;
	}

	public String getString(int paramInt) throws UnsupportedEncodingException {
		if (paramInt == 0)
			return "";

		byte[] datBytes = new byte[paramInt];
		System.arraycopy(this.refbytes, this.curIndex, datBytes, 0,
				datBytes.length);
		this.curIndex += paramInt;
		return new String(datBytes, "GBK");
	}

	public void getBytes(byte[] out, int offset, int nlen) {
		System.arraycopy(this.refbytes, this.curIndex, out, offset, nlen);
		this.curIndex += nlen;
	}

	/**
	 * Transform the array to the hex string
	 * 
	 * @param b
	 *            array
	 * @param index
	 *            index position
	 * @param length
	 *            length
	 * @return hex string
	 */
	public static String Byte2HexString(byte[] b, int index, int length) {

		// 转成16进制字符串
		StringBuffer hs = new StringBuffer();
		String stmp = "";

		for (int n = index; n < index + length; n++) {

			// 整数转成十六进制表示
			stmp = (Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1) {
				hs.append("0");
			}
			hs.append(stmp);
		}

		// 转成大写
		return hs.toString().toUpperCase();
	}

	public static boolean bytescmp(byte[] dat1, int d1offset, byte[] dat2,
			int d2offset, int length) {
		for (int i = 0; i < length; i++) {
			if (d1offset + i > dat1.length)
				return false;
			if (d2offset + i > dat2.length)
				return false;
			if (dat1[d1offset + i] != dat2[d2offset + i]) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Debug hex string of 16
	 * 
	 * @param title
	 *            the title
	 * @param info
	 *            Need to debug information
	 */
	public static void DebugHex(String title, byte[] info) {
		DebugHex(title, info, 0, info.length);
	}

	/**
	 * Debug hex string of 16
	 * 
	 * @param title
	 *            the title
	 * @param info
	 *            Need to debug information
	 * @param offset
	 *            information offset
	 * @param length
	 *            debug length
	 */
	public static void DebugHex(String title, byte[] info, int offset,
                                int length) {
		BytesImpl refInfo = new BytesImpl(info);
		refInfo.setCurIndex(offset);

		try {
			System.out.println(title + ":" + refInfo.getHexString(length));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		refInfo = null;
	}

	public static byte[]hex2byte(byte[]b){
		if((b.length%2)!=0)
		throw new IllegalArgumentException("长度不是偶数");
		byte[]b2=new byte[b.length/2];
		for(int n=0;n<b.length;n+=2){
			String item=new String(b,n,2);
			//两位一组，表示一个字节,把这样表示的16进制字符串，还原成一个进制字节
			b2[n/2]=(byte) Integer.parseInt(item,16);
		}
		return b2;
	}

	public static byte[] toBcdBytes(String sRcv) throws UnsupportedEncodingException {
		return hex2byte(sRcv.getBytes("GBK"));
	}
	
	/**
	 * Function:AsciiToBcd Introduction: Convert the ASCII code in the source
	 * buffer to the packed BCD code,and stored the result to the destination
	 * buffer Description:ASCII code is case-insensitive，range(0~9，a~f，A~F)
	 * Input Parameters: char *bcd_buf: the destination buffer
	 * char *ascii_buf: the source buffer int conv_len: the length of the ASCII
	 * code to be converted char type: 0-left alignment,1-right alignment Output
	 * Parameters:none Return Values:none
	 * 
	 */
	public static void AsciiToBcd(byte[] bcd_buf, int bcd_offset,
			byte[] ascii_buf, int ascii_offset, int conv_len, int type) {
		int cnt;
		int ch, ch1;

		if ((0x01 == (conv_len & 0x01)) && type == 1)
			ch1 = 0;
		else
			ch1 = 0x55;

		for (cnt = 0; cnt < conv_len; ascii_offset++, cnt++) {
			int tmpascii = (ascii_buf[ascii_offset] & 0xFF);
			if (tmpascii >= 'a')
				ch = (tmpascii - 'a' + 10);
			else if (tmpascii >= 'A')
				ch = (tmpascii - 'A' + 10);
			else if (ascii_buf[ascii_offset] >= '0')
				ch = (tmpascii - '0');
			else {

				ch = tmpascii;
				ch &= 0x0f;// 保留低四位
			}
			if (ch1 == 0x55)
				ch1 = ch;
			else {
				bcd_buf[bcd_offset] = (byte) ((ch1 << 4) | ch);
				bcd_offset++;
				ch1 = 0x55;
			}
		} // for

		if (ch1 != 0x55)
			bcd_buf[bcd_offset] = (byte) (ch1 << 4);

		return;
	}

	/**
	 * Function：BcdToAscii Introduction:Convert the packed BCD code in the
	 * source buffer to the ASCII code,and stored the result to the destination
	 * buffer Description: Input Parameters: char *ascii_buf:
	 * the destination buffer char *bcd_buf: the source buffer int conv_len:the
	 * length of the packed BCD code to be converted char type: 0-left
	 * alignment,1-right alignment Output Parameters:none Return Values:none
	 */
	public static void BcdToAscii(byte[] ascii_buf, int ascii_offset,
			byte[] bcd_buf, int bcd_offset, int conv_len, int type) {
		int cnt;

		if (((conv_len & 0x01) == 0x01) && type == 1)
		{
			/* 0左，1右 */
			cnt = 1;
			conv_len++;
		} else
			cnt = 0;

		for (; cnt < conv_len; cnt++, ascii_offset++) {
			int ascii = ((cnt & 0x01) == 0x01 ? ((bcd_buf[bcd_offset++] & 0xFF) & 0x0F)
					: ((bcd_buf[bcd_offset] & 0xFF) >> 4));
			ascii += ((ascii > 9) ? ('A' - 10) : '0');
			ascii_buf[ascii_offset] = (byte) ascii;
		}

		return;
	}
}
