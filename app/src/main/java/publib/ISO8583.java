package publib;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import publib.tools.XmlField;
import publib.tools.XmlFieldType;
import publib.tools.XmlNode;
import publib.tools.XmlToObject;
import security.SecurityLayer;

class ISOFormat {
	@XmlField(fieldName="length", fieldType= XmlFieldType.ATTR)
	int maxLen; // Max Length or Fix Length;

	@XmlField(fieldName="type", fieldType=XmlFieldType.ATTR)
	int type; // 0: ASC Type 1: BCD Type

	@XmlField(fieldName="flag", fieldType=XmlFieldType.ATTR)
	int lengthType; /*
					 * 0-Fix length, 1-var length (00-99), 2-var length
					 * (00-999), 3-Fix length ASC Type 4-Var Length (00-99) ASC
					 * Type 5-var length (00-999) ASC Type
					 */


	@XmlField(fieldName="option", fieldType=XmlFieldType.ATTR)
	int option; /*
				 * 0 左靠 即位数不足时右补0 ; 1 右靠 即位数不足时左补0
				 */

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ISOFormat [len=");
		builder.append(maxLen);
		builder.append(", type=");
		builder.append(type);
		builder.append(", flag=");
		builder.append(lengthType);
		builder.append(", option=");
		builder.append(option);
		builder.append("]");
		return builder.toString();
	}
}

class ISOField {
	String sData;
	boolean bIsExist;

}

@SuppressLint("DefaultLocale")
public class ISO8583 {
	private ISOFormat[] fieldFormat = new ISOFormat[129];
	private ISOField[] fieldElement = new ISOField[129];
	private byte[] bitmap = new byte[128];
	private final String FIELD_SETTING_TAG = "FIELD_SETTING";
	private final String FILED_FORMAT = "FIELD%03d";
	private boolean bExfield = false;
	
	/**
	 * 为了打开存在于assets的文件，定义Context类型的成员变量
	 */
	private Context context = null;

	public ISO8583(Context context) {
		this.context = context;
		for (int i = 0; i <= 128; i++) {
			fieldFormat[i] = new ISOFormat();
			fieldFormat[i].maxLen = 0;
			fieldFormat[i].type = 0;
			fieldFormat[i].lengthType = 0;

		}

		for (int i = 0; i <= 128; i++) {
			fieldElement[i] = new ISOField();
			fieldElement[i].sData = null;
			fieldElement[i].bIsExist = false;

		}

	}

	public void initPack() {
		for (int i = 0; i <= 128; i++) {
			fieldElement[i].sData = null;
			fieldElement[i].bIsExist = false;

		}
	}

	/**
	 * 加载XML文件，初始化各个域的格式
	 */
	public void loadXmlFile(String xmlFilename) {
		XmlNode node = new XmlNode();
		try {
			InputStream ins = this.context.getAssets().open(xmlFilename);
			node.decodeXml(ins);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		XmlNode fieldSetNode = node.getChild(FIELD_SETTING_TAG);
		if (fieldSetNode == null) {
			// no continue
			return;
		}
		
		for (int i = 0; i < fieldFormat.length; i++) {
			String fieldName = String.format(FILED_FORMAT, i);
			XmlNode filedNode = fieldSetNode.getChild(fieldName);
			XmlToObject.xmlToObject(filedNode, fieldFormat[i]);
			//System.out.println("ISOFormat" + i + ":" + fieldFormat[i]);
		}
	}
	
	public byte[] getBitmap() {
		return bitmap;
	}
	
	public void setField(int nIndex, String sDataField) {
		if (nIndex < 0 && nIndex > 128) {
			return;
		}

		fieldElement[nIndex].sData = sDataField;
		fieldElement[nIndex].bIsExist = true;

		return;
	}

	public String getField(int nIndex) {
		if (nIndex < 0 && nIndex > 128) {
			return null;
		}
		return fieldElement[nIndex].sData;

	}
	
	/**
	 * 设置位图模式
	 * 注意： 默认位图为64字节或者有大于64域的域支持自动增长，打包前不必调用该函数
	 *     在位图强制为16字节而不管64域后有没有数据，打包前必须调用该函数
	 * @param mode,1=位图长度固定16字节,0=8 or 16字节
	 * @return
	 */
	public boolean setBitmapMode(int mode) {
		if(1 == mode)
			this.bExfield = true;
		else
			this.bExfield = false;
		return true;
	}

	
	/**
	 * Fixed-length domain data packing format
	 * 
	 * @param isoFormat
	 * @param value
	 * @return
	 * @throws ISO8583Exception
	 */
	private String packFixLenField(ISOFormat isoFormat, String value) throws ISO8583Exception {
		int fieldLen;

		fieldLen = isoFormat.maxLen;
		if (isoFormat.type == 0) {
			switch (isoFormat.option) {
			case 0:// Left padding 0
					// 123456789转换成30313233343536373839
				return StringUtils.strToHex(StringUtils.paddingString(value,
						fieldLen, "0", 0));
			case 1:// Right padding 0
					// 123456789转换成31323334353637383930
				return StringUtils.strToHex(StringUtils.paddingString(value,
						fieldLen, "0", 1));
			case 2:// Left padding
					// 123456789 converted to 20313233343536373839
				return StringUtils.strToHex(StringUtils.paddingString(value,
						fieldLen, " ", 0));
			case 3:// Filled spaces on the right
					// 123456789 converted to 31323334353637383920
				return StringUtils.strToHex(StringUtils.paddingString(value,
						fieldLen, " ", 1));
			}
		} else {
			fieldLen = ((fieldLen + 1) / 2) * 2;
			switch (isoFormat.option) {
			case 0:// 左侧填充0
					// 123456789 转换成0x01 0x23 0x45 0x67 0x89
				return StringUtils.paddingString(value, fieldLen, "0", 0);
			case 1:// 右侧填充0
					// 1234567890 转换成0x12 0x34 0x56 0x78 0x90
				return StringUtils.paddingString(value, fieldLen, "0", 1);
			case 2:// 左侧填充F
					// 123456789 转换成0xF1 0x23 0x45 0x67 0x89
				return StringUtils.paddingString(value, fieldLen, "F", 0);
			case 3:// 右侧填充F
					// 123456789 转换成0x12 0x34 0x56 0x78 0x9F
				return StringUtils.paddingString(value, fieldLen, "F", 1);
			}
		}
		throw new ISO8583Exception("Wrong domain value parameter.");
	}

	/**
	 * Variable length domain data packing format
	 * 
	 * @param isoFormat
	 * @param value
	 * @return
	 * @throws ISO8583Exception
	 */
	private String packVarLenField(ISOFormat isoFormat, String value,
                                   int digits, boolean lenIsBcd) throws ISO8583Exception {
		int fieldLen;
		String strLen;
		
		fieldLen = value.length();
		if(fieldLen > isoFormat.maxLen) { 
			fieldLen = isoFormat.maxLen;
		}
		if (isoFormat.type == 0) {
			strLen = StringUtils.intToBcd(fieldLen, digits);
			//System.out.println("strLen1: "+ strLen);
			//System.out.println("digits: " + digits );
			if (!lenIsBcd) {
				/**
				 * Domain length is set to ASC format
				 */
				strLen = StringUtils.strToHex(strLen);
			}
			// 123456789转换成09313233343536373839
			return strLen + StringUtils.strToHex(value);
		} else {
			/**
			 * Binary Type Len 
			 */
			if (isoFormat.type == 2){
				strLen = StringUtils.intToBcd(fieldLen/2, digits);
			}else{
				strLen = StringUtils.intToBcd(fieldLen, digits);
			}
			//System.out.println("strLen2:[" + strLen+"]" + digits );
			if (!lenIsBcd) {
				/**
				 * Domain length is set to ASC format
				 */
				strLen = StringUtils.strToHex(strLen);
			}
			fieldLen = ((fieldLen + 1) / 2 * 2);
			switch (isoFormat.option) {
			case 0:// 左侧填充0
					// 123456789 转换成0x09 0x01 0x23 0x45 0x67 0x89
				return strLen
						+ StringUtils.paddingString(value, fieldLen, "0", 0);
			case 1:// 右侧填充0
					// 123456789 转换成0x09 0x12 0x34 0x56 0x78 0x90
				return strLen
						+ StringUtils.paddingString(value, fieldLen, "0", 1);
			case 2:// 左侧填充F
					// 123456789 转换成0x09 0xF1 0x23 0x45 0x67 0x89
				return strLen
						+ StringUtils.paddingString(value, fieldLen, "F", 0);
			case 3:// 右侧填充F
					// 123456789 转换成0x09 0x12 0x34 0x56 0x78 0x9F
				return strLen
						+ StringUtils.paddingString(value, fieldLen, "F", 1);
			}
		}
		throw new ISO8583Exception("Wrong domain value parameter.");
	}

	public String pack() throws ISO8583Exception {
		String strMsgType = "";
		String strBitmap = "";
		String buffer = "";
		String strElement="";
		
		strMsgType = packFixLenField(fieldFormat[0],fieldElement[0].sData);
	
		for (int i = 2; i <= 128; i++) {
			if (!fieldElement[i].bIsExist) {
				bitmap[i-1] = '0';
				continue;
			}
			bitmap[i-1] = '1';

			if (fieldFormat[i].type == 1) { 
				if (!StringUtils.checkHexStr(fieldElement[i].sData)) {
					throw new ISO8583Exception("【Bale】area [" + i + "] The value is not in the Hex format." + "["
							+ fieldElement[i].sData + "]");
				}
			}

			int len1 = ((fieldFormat[i].maxLen));
			//Log.v("math: ", Integer.toString((fieldFormat[i].maxLen + 1)) + " >> "+ Integer.toString(len1) + " >>>> " +
			//		Integer.toString(fieldElement[i].sData.length()));
			if (((fieldFormat[i].maxLen + 1)/2)*2 < fieldElement[i].sData.length()) {
				throw new ISO8583Exception("【Bale】area[" + i +"]Value exceeds maximum setting." + "["
						+ fieldElement[i].sData + "]");
			}
			strElement = "";

			switch (fieldFormat[i].lengthType) {
			case 0:
			case 3:
				strElement += packFixLenField(fieldFormat[i],
						fieldElement[i].sData);
				break;
			case 1:// LLVar域数据打包(长度为BCD)格式
				strElement += packVarLenField(fieldFormat[i],
						fieldElement[i].sData, 1, true);
				break;
			case 2:// LLLVar域数据打包(长度为BCD)格式
				strElement += packVarLenField(fieldFormat[i],
						fieldElement[i].sData, 2, true);
				break;
			case 4: // LLVar域数据打包(长度为ASC)格式
				strElement += packVarLenField(fieldFormat[i],
						fieldElement[i].sData, 1, false);
				break;
			case 5:// LLLVar域数据打包(长度为ASC)格式
				strElement += packVarLenField(fieldFormat[i],
						fieldElement[i].sData, 3, false);
				break;
			}
			buffer += strElement;
			LoggerUtils.d("域 [" + i + "] 值:" + "["+ strElement + "]");
		}
		
		bitmap[0] = '0';
		if (this.bExfield){
			bitmap[0] = '1';
		} else {
			for(int i=64;i<128;i++) {
				if(bitmap[i] == '1') {
					bitmap[0] = '1';
				}
			}
		}
		
		int bitmapLen = 0;
		if(bitmap[0] == '1') {
			bitmapLen = 128;
		} else {
			bitmapLen = 64;
		}
		strBitmap = new String(bitmap).substring(0,bitmapLen);
		strBitmap = StringUtils.binaryToHex(strBitmap);
		LoggerUtils.d("ISO8583:" + "["+ strMsgType + strBitmap + buffer + "]");
		return strMsgType + strBitmap + buffer;
	}
	
	public byte[] packBytes() throws ISO8583Exception {
	    String result;
	   result = pack();
	   return BytesUtils.hexStringToBytes(result);
	
	}

	public void unpack(String buffer) throws NumberFormatException, UnsupportedEncodingException, ISO8583Exception {
			int msgIdLen;
			int bitmapLen;
			int currentIndex = 0;
			String strBitmap;
			int fieldLen = 0;
			int compareLen = 0;
			int fieldRealLen = 0;
			
			//this.initPack();
		 	SecurityLayer.Log("res iso", buffer);
			if(fieldFormat[0].type == 1) {
				msgIdLen = fieldFormat[0].maxLen;
				Log.v("1 >> fld len", Integer.valueOf(msgIdLen).toString());
				setField(0, StringUtils.hexToStr(buffer.substring(0,msgIdLen)));
			} else {
				//msgIdLen = fieldFormat[0].maxLen*2;
                msgIdLen = fieldFormat[0].maxLen;
                Log.v("2 ** fld len", Integer.valueOf(fieldFormat[0].maxLen).toString() + " >>> " + Integer.valueOf(msgIdLen).toString());
				setField(0,buffer.substring(0,msgIdLen));
			}
			fieldElement[0].sData = buffer.substring(currentIndex, msgIdLen);
			currentIndex += msgIdLen;
	
			if((BytesUtils.hexStringToBytes(buffer.substring(currentIndex,currentIndex+2))[0]& 0x80) != 0){
				strBitmap = StringUtils.hexToBinary(buffer.substring(currentIndex,currentIndex+32));
				currentIndex += 32;
				bitmapLen = 128;
			}else{
				strBitmap = StringUtils.hexToBinary(buffer.substring(currentIndex,currentIndex+16));
				currentIndex += 16;
				bitmapLen = 64;
			}
			
			byte[] tmp = strBitmap.getBytes();
			System.arraycopy(tmp, 0, bitmap, 0, tmp.length);//strBitmap.getBytes();
			for(int i=2; i<=bitmapLen; i++) {
				if(bitmap[i-1] == '0') {
					continue;
				}
				//Log.v("Ray fieldFormat >> ", Integer.valueOf(fieldFormat[i].lengthType).toString());
				switch (fieldFormat[i].lengthType) {
				case 0:
				case 3:
					fieldLen = fieldFormat[i].maxLen;
					fieldRealLen = fieldLen;
					if((fieldFormat[i].type == 0) || (fieldFormat[i].type == 2)){
						fieldLen *= 2;
						fieldRealLen = fieldLen;
					} else {
						fieldLen = ((fieldLen + 1) / 2) *2;
						
					}
					break;
				case 1:// LLVar域数据打包(长度为BCD)格式
					//System.out.println("substring: " + buffer.substring(currentIndex ,currentIndex+2));
					fieldLen = Integer.valueOf(buffer.substring(currentIndex,currentIndex+2));
					fieldRealLen = fieldLen;
					currentIndex += 2;
					if((fieldFormat[i].type == 0) || (fieldFormat[i].type == 2)){
						fieldLen *= 2;
						fieldRealLen = fieldLen;
					}else if(fieldFormat[i].type == 1) {
						fieldLen = ((fieldLen + 1) / 2) *2;
					}
					break;
				case 2:// LLLVar域数据打包(长度为BCD)格式
					fieldLen = Integer.valueOf( buffer.substring(currentIndex,currentIndex+4));
					fieldRealLen = fieldLen;
					currentIndex += 4;
					if((fieldFormat[i].type == 0) || (fieldFormat[i].type == 2)){
						fieldLen *= 2;
						fieldRealLen = fieldLen;
					}else if(fieldFormat[i].type == 1) {
						fieldLen = ((fieldLen + 1) / 2) *2;
					}
					break;
				case 4: // LLVar域数据打包(长度为ASC)格式
					fieldLen = Integer.valueOf(StringUtils.hexToStr(buffer.substring(currentIndex,currentIndex+4)));
					fieldRealLen = fieldLen;
					currentIndex += 4;
					if((fieldFormat[i].type == 0) || (fieldFormat[i].type == 2)){
						fieldLen *= 2;
						fieldRealLen = fieldLen;
					}else if(fieldFormat[i].type == 1) {
						fieldLen = ((fieldLen + 1) / 2) *2;
					}
					break;
				case 5:// LLLVar域数据打包(长度为ASC)格式
					fieldLen = Integer.valueOf(StringUtils.hexToStr(buffer.substring(currentIndex,currentIndex+6)));
					fieldRealLen = fieldLen;
					currentIndex += 6;
					if((fieldFormat[i].type == 0) || (fieldFormat[i].type == 2)){
						fieldLen *= 2;
						fieldRealLen = fieldLen;
					}else if(fieldFormat[i].type == 1) {
						fieldLen = ((fieldLen + 1) / 2) *2;
					}
					break;
				}
				LoggerUtils.d("【解包】域 len is: [" + i + "] 值:" + "["+ fieldLen + "]");
				//System.out.println("【解包】域 value offset is: [" + i + "] 值:" + "["+ buffer.substring(currentIndex,currentIndex+fieldLen) + "]");
				if((fieldFormat[i].type == 0) || (fieldFormat[i].type == 2)){
					compareLen = fieldLen / 2;
				} else if(fieldFormat[i].type == 1) {
					compareLen = fieldLen - 1;
				} 
				
				if(compareLen > fieldFormat[i].maxLen) 
					throw new ISO8583Exception("域 [" + i + "]长度超过最大值设置." + "[fieldLen=" + fieldLen + "]");
			if(fieldFormat[i].type == 0) {
				setField(i, StringUtils.hexToStr(buffer.substring(currentIndex,currentIndex+fieldRealLen)));
			} else {
				setField(i,buffer.substring(currentIndex,currentIndex+fieldRealLen));
			}
			currentIndex += fieldLen;
			LoggerUtils.d("【解包】域 [" + i + "] 值:" + "["+ getField(i) + "]");
		}
		Log.v("lens", Integer.valueOf(buffer.length()).toString() + " >>> " + Integer.valueOf(currentIndex).toString());
		if(buffer.length() > currentIndex)
			throw new ISO8583Exception("返回报文长度太大");
	}
	
	public void unpack(byte[] value) throws NumberFormatException, UnsupportedEncodingException, ISO8583Exception {
		String buffer = BytesUtils.bytesToHex(value);
		unpack(buffer);
	}

}
