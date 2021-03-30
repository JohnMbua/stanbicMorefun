package publib;

import android.annotation.SuppressLint;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * 字符串工具类
 * @author chenkh
 * 2015-03-12
 */
public class StringUtils {
	
	/**
	 * 判断ip合法性
	 */
	@SuppressLint("NewApi")
	public static boolean isMacthIp(String ip) {
		if (ip != null && !ip.isEmpty()) {
			// 定义正则表达式            
			String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
					+ "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
					+ "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
					+ "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$"; // 判断ip地址是否与正则表达式匹配            
			if (ip.matches(regex)) {           
				return true;            
			}
		}    
		return false;    
	}
	
	/**
	 * 判断字符串是否为空
	 */
	public static boolean isEmpty(String str){
		if (str==null || "".equals(str)){
			return true;
		}
		return false;
	}

	/**
	 * 判断字符串是否为空(包括对"null")
	 */
	public static boolean isNullOrEmpty(String str){
		if (str==null || "".equals(str) || "null".equals(str) ){
			return true;
		}
		return false;
	}

	
	/**
	 * 判断字符串是否纯数字
	 */
	public static boolean isDigital(String str){
		if (!isEmpty(str))
			return str.matches("[0-9]+");
		return false;
	}
	
	/**
	 * 计算含有中文的字符串长度
	 * @param value 字符串（支持含中文字符串）
	 * @return
	 */
	public static int length(String value) {
        int valueLength = 0;
        String chinese = "[\u0391-\uFFE5]";
        /* 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1 */
        for (int i = 0; i < value.length(); i++) {
            /* 获取一个字符 */
            String temp = value.substring(i, i + 1);
            /* 判断是否为中文字符 */
            if (temp.matches(chinese)) {
                /* 中文字符长度为2 */
                valueLength += 2;
            } else {
                /* 其他字符长度为1 */
                valueLength += 1;
            }
        }
        return valueLength;
    }
	
	/**
	 * 字符串数组转换List<String>
	 * @param items
	 * @return
	 */
	public static List<String> stringsToList(String[] items) {
		List<String> lists = new ArrayList<String>();
		for(int i=0; i<items.length; i++){
			lists.add(items[i]);
		}
		return lists;
	}

	/**
	 * 字符串填充，将sour使用fillStr前补或后补满len长度
	 * @param sour 待填充字符串，支持含有中文
	 * @param fillStr 填充数据
 	 * @param len 填充完整字符串长度
	 * @param isLeft 是否左补填充数据，否则右补填充数据
	 * @return
	 */
	public static String fill(String sour, String fillStr, int len, boolean isLeft){
		if (sour == null) {
			sour = "";
		}
		int fillLen = len - length(sour);
		String fill = "";
		for (int i=0; i<fillLen; i++) {
			fill = fill + fillStr;
		}
		if (isLeft) {
			return fill + sour;
		} else {
			return sour + fill;
		}
	}

	public static String paddingString(String strData, int nLen, String subStr,
                                       int nOption) {
		int i, addCharLen;

		String strHead = "";
		String strEnd = "";

		i = strData.length();
		if (i >= nLen) {
			return strData;
		}

		switch (nOption) {
		case 0:
			addCharLen = (nLen - i) / subStr.length();
			for (i = 0; i < addCharLen; i++) {
				strHead += subStr;
			}
			return strHead + strData;
		case 1:
			addCharLen = (nLen - i) / subStr.length();
			for (i = 0; i < addCharLen; i++) {
				strEnd += subStr;
			}
			return strData + strEnd;
		case 2:
			addCharLen = (nLen - i) / (subStr.length() * 2);
			for (i = 0; i < addCharLen; i++) {
				strHead += subStr;
				strEnd += subStr;
			}
			return strHead + strData + strEnd;
		default:
			return strData;
		}
	}

	/**
	 * 整形转换成BCD型的字符串
	 * 9转换成后将变成09,00 09
	 * 19转换后将变成19, 00 19
	 * @param value
	 * @param bytesNum
	 *            BCD字节个数
	 * @return
	 */
	public static String intToBcd(int value, int bytesNum) {
		switch(bytesNum){
		case 1:
			if (value >= 0  && value <= 99){
				return paddingString(String.valueOf(value),2,"0",0);
			}
			break;
		case 2:
			if (value >= 0  && value <= 999) {
				return paddingString(String.valueOf(value),4,"0",0);
			}
			break;
			
		case 3:
			if (value >= 0  && value <= 999) {
				return paddingString(String.valueOf(value),3,"0",0);
			}
			break;
		}
		
		return "";
	}

	/**
	 * Hex数据转换成字符串
	 * 
	 * @param value
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String hexToStr(String value) throws UnsupportedEncodingException {
		return new String(BytesUtils.hexToBytes(value),"GBK");
	}

	/**
	 * 字符串转换成Hex
	 * 
	 * @param value
	 * @return
	 */
	public static String strToHex(String value) {
		return BytesUtils.bytesToHex(BytesUtils.getBytes(value));
	}

	/**
	 * 往value中填充一个字符0 ,当数据长度正好为2的整数倍时，不填充
	 * 
	 * @param value
	 * @param option
	 *            0:往后填充 ;1:往前填充
	 * @return
	 */
	public static String paddingZeroToHexStr(String value, int option) {
		
		if (value.length() % 2 == 0){
			return value;
		}
		
		if (option == 0){
			return "0" + value;
		}
		else if (option == 1){
			return value + "0";
		}
		else{
			return value;
		}
	}

	/**
	 * 判断是否是Hex格式数据
	 * 
	 * @param value
	 * @return
	 */
	public static boolean checkHexStr(String value) {
		int i;
		int len;
		
		if (value == null) return false;
		
		len = value.length();
		if (len == 0) return false;

	  for (i= 0;i<len;i++){
		  if (!((value.charAt(i) >= '0' && value.charAt(i) <= '9')|| 
				  (value.charAt(i) >= 'a' && value.charAt(i) <= 'f') ||
				  (value.charAt(i) >= 'A' && value.charAt(i) <= 'F'))){
			  return false; 			  
		  }
	  }
	  return true;
	}

	/**
	 * 判断字符串是否是数字0-9
	 * 
	 * @param value
	 * @return
	 */
/*	public static boolean checkDigitStr(String value) {
		int i;
		int len;
		
		if (value == null) return false;
		
		len = value.length();
		if (len == 0) return false;

	  for (i= 0;i<len;i++){
		  if (value.charAt(i) < '0' || value.charAt(i) > '9') {
			  return false; 			  
		  }
	  }
	  return true;
	}*/
	
	/**
	 * Binary数据转换成Hex
	 * 
	 * @param value
	 * @return
	 */
	public static String binaryToHex(String value) {
		int i,j,len;
		String result ="";
		char[] hexVocable = { '0', '1', '2', '3', 
				'4', '5', '6','7', 
				'8', '9', 'A', 'B', 
				'C', 'D', 'E', 'F' };
		String[] binString  = {"0000", "0001", "0010", "0011",
                "0100", "0101", "0110", "0111",
                "1000", "1001", "1010", "1011",
                "1100", "1101", "1110", "1111"};
		//System.out.println("value: " + value);
		
		len = value.length();
		for(i=0; i<len; i += 4){
			for(j=0; j<16; j++){
				if(binString[j].equals(value.substring(i, i+4))){
					result += hexVocable[j];
					break;
				}				
			}
		}
		//System.out.println("result: " + result);
		return result;
	}

	/**
	 * Hex数据转换成Binary
	 * 
	 * @param value
	 * @return
	 */
	public static String hexToBinary(String value) {
		int i,j,len;
		String result ="";
		char[] hexVocable = { '0', '1', '2', '3', 
				'4', '5', '6','7', 
				'8', '9', 'A', 'B', 
				'C', 'D', 'E', 'F' };
		String[] binString  = {"0000", "0001", "0010", "0011",
                "0100", "0101", "0110", "0111",
                "1000", "1001", "1010", "1011",
                "1100", "1101", "1110", "1111"};
		
		len = value.length();
		for(i=0; i<len; i++){
			for(j=0; j<16; j++){
				if(value.charAt(i) == hexVocable[j]){
					result += binString[j];
					break;
				}				
			}
		}
		//System.out.println("result: " + result);
		return result;
	}
	
	/**
	 * 获取二进制字符串
	 * 0x00 0x01 0x00 0x01 0x01转换成"01011"
	 * @param value
	 * @return
	 */
	public static String getBinaryString(byte[] value) {
		int len;
		String result ="";
		
		len = value.length;
		
		for(int i=0;i<len;i++) {
			result += String.valueOf(value[i]);
		}
		
		return result;
	}
}
