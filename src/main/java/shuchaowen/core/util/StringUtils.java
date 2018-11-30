package shuchaowen.core.util;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import shuchaowen.core.exception.ShuChaoWenRuntimeException;

public final class StringUtils {
	private static final String randomStr = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	private static final Charset DEFAULT_OLD_CHARSET = Charset.forName("ISO-8859-1");
	private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
	private static final String IOS_NULL = "(null)";
	
	private StringUtils(){};
	
	public static boolean isNull(boolean trim, String... str) {
		for (String s : str) {
			if (s == null || s.length() == 0 || (trim && s.trim().length() == 0)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isNull(String... strs) {
		return isNull(false, strs);
	}
	
	/**
	 * 在ios中由于前端未做判断导致的空
	 * (null)
	 * @param strs
	 * @return
	 */
	public static boolean isNullByIOS(String ...strs){
		for (String s : strs) {
			if (s == null || s.length() == 0 || IOS_NULL.equals(s)) {
				return true;
			}
		}
		return false;
	}

	public static boolean trimIsNull(String... strs) {
		return isNull(true, strs);
	}

	public static boolean isAeqB(String strA, String strB) {
		if (isNull(strA)) {
			strA = "";
		}

		if (isNull(strB)) {
			strB = "";
		}

		if (strA == strB || strA.equals(strB)) {
			return true;
		}
		return false;
	}

	public static String[] commonSplit(String str) {
		return split(str, ' ', ',', ';', '、');
	}

	public static String[] split(String str, char... regex) {
		if (str == null || str.length() == 0) {
			return new String[] { str };
		}

		int lastFind = 0;
		List<String> list = new ArrayList<String>();
		char[] chars = str.toCharArray();
		int i = 0;
		for (; i < chars.length; i++) {
			boolean b = false;
			for (char r : regex) {
				if (r == chars[i]) {
					b = true;
					break;
				}
			}

			if (b) {// 找到了
				if (i != lastFind) {
					list.add(new String(chars, lastFind, i - lastFind));
				}
				i++;
				lastFind = i;
			}
		}

		if (lastFind != i) {
			list.add(new String(chars, lastFind, i - lastFind));
		}

		if (list.isEmpty()) {
			return new String[] { str };
		}
		return list.toArray(new String[list.size()]);
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> splitList(Class<T> type, String strs, String regex, boolean isTrim) {
		if (type == null || regex == null) {
			throw new NullPointerException();
		}

		List<T> list = new ArrayList<T>();
		if (strs == null) {
			return list;
		}

		String[] arr = strs.split(regex);
		if (String.class.isAssignableFrom(type)) {
			for (String str : arr) {
				if (str.length() == 0) {
					continue;
				}

				if (isTrim) {
					str = str.trim();
					if (str.length() == 0) {
						continue;
					}
				}

				T t = (T) str;
				list.add(t);
			}
		} else if (Integer.class.isAssignableFrom(type)) {
			for (String str : arr) {
				if (str.length() == 0) {
					continue;
				}

				if (isTrim) {
					str = str.trim();
					if (str.length() == 0) {
						continue;
					}
				}

				T t = (T) Integer.valueOf(str);
				list.add(t);
			}
		} else if (Short.class.isAssignableFrom(type)) {
			for (String str : arr) {
				if (str.length() == 0) {
					continue;
				}

				if (isTrim) {
					str = str.trim();
					if (str.length() == 0) {
						continue;
					}
				}

				T t = (T) Short.valueOf(str);
				list.add(t);
			}
		} else if (Long.class.isAssignableFrom(type)) {
			for (String str : arr) {
				if (str.length() == 0) {
					continue;
				}

				if (isTrim) {
					str = str.trim();
					if (str.length() == 0) {
						continue;
					}
				}

				T t = (T) Long.valueOf(str);
				list.add(t);
			}
		} else if (Float.class.isAssignableFrom(type)) {
			for (String str : arr) {
				if (str.length() == 0) {
					continue;
				}

				if (isTrim) {
					str = str.trim();
					if (str.length() == 0) {
						continue;
					}
				}

				T t = (T) Float.valueOf(str);
				list.add(t);
			}
		} else if (Double.class.isAssignableFrom(type)) {
			for (String str : arr) {
				if (str.length() == 0) {
					continue;
				}

				if (isTrim) {
					str = str.trim();
					if (str.length() == 0) {
						continue;
					}
				}

				T t = (T) Double.valueOf(str);
				list.add(t);
			}
		}
		return (List<T>) list;
	}

	public static List<String> toStrList(String strs, boolean isTrim) {
		if (isNull(strs)) {
			return null;
		}

		List<String> list = new ArrayList<String>();
		String[] strList = strs.split(",");
		for (String str : strList) {
			if (isNull(str)) {
				continue;
			}

			if (isTrim) {
				str = str.trim();
			}

			if (isNull(str)) {
				continue;
			}
			list.add(str);
		}
		return list;
	}

	public static String addStr(String str, String addStr, int beginIndex) {
		if (addStr != null && addStr.length() != 0) {
			String str1 = str.substring(0, beginIndex);
			String str2 = str.substring(beginIndex);
			return str1 + addStr + str2;
		}
		return str;
	}

	public static int[] splitIntArr(String str, String regex) {
		String[] arr = splitStringArr(str, regex, false);
		if (arr == null) {
			return null;
		}

		int[] dataArr = new int[arr.length];
		for (int i = 0; i < arr.length; i++) {
			dataArr[i] = Integer.parseInt(arr[i]);
		}
		return dataArr;
	}

	public static long[] splitLongArr(String str, String regex) {
		String[] arr = splitStringArr(str, regex, false);
		if (arr == null) {
			return null;
		}

		long[] dataArr = new long[arr.length];
		for (int i = 0; i < arr.length; i++) {
			dataArr[i] = Long.parseLong(arr[i]);
		}
		return dataArr;
	}

	public static String[] splitStringArr(String str, String regex, boolean isTrim) {
		if (isNull(str, regex)) {
			return null;
		}

		String[] dataArr = str.split(regex);
		if (isTrim) {
			for (int i = 0; i < dataArr.length; i++) {
				dataArr[i] = dataArr[i].trim();
			}
		}
		return dataArr;
	}

	/**
	 * 1M = 1024K
	 * 
	 * @param size
	 * @param toSuffix
	 * @return
	 */
	public static double parseDiskSize(String size, String toSuffix) {
		int len = size.length();
		double oldSize;
		if (size.endsWith("GB") || size.endsWith("G")) {
			oldSize = Double.parseDouble(size.substring(0, len - 2)) * 1024 * 1024 * 1024;
		} else if (size.endsWith("MB") || size.endsWith("M")) {
			oldSize = Double.parseDouble(size.substring(0, len - 2)) * 1024 * 1024;
		} else if (size.endsWith("KB") || size.endsWith("K")) {
			oldSize = Double.parseDouble(size.substring(0, len - 2)) * 1024;
		} else if (size.endsWith("B")) {
			oldSize = Double.parseDouble(size.substring(0, len - 1));
		} else {
			oldSize = Double.parseDouble(size);
		}

		if ("GB".equals(toSuffix) || "G".equals(toSuffix)) {
			return oldSize / (1024 * 1024 * 1024);
		} else if ("MB".equals(toSuffix) || "M".equals(toSuffix)) {
			return oldSize / (1024 * 1024);
		} else if ("KB".equals(toSuffix) || "K".equals(toSuffix)) {
			return oldSize / (1024);
		} else if ("B".equals(toSuffix)) {
			return oldSize;
		} else {
			return oldSize;
		}
	}

	public static String getRandomStr(int length) {
		return getRandomStr(randomStr, length);
	}

	public static String getRandomStr(String randomStr, int length) {
		Random random = new Random();
		char[] cArr = new char[length];
		int len = randomStr.length();
		for (int i = 0; i < length; ++i) {
			int number = random.nextInt(len);// [0,62)
			cArr[i] = randomStr.charAt(number);
		}
		return new String(cArr);
	}

	public static boolean isMobileNum(String telNum) {
		Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		Matcher m = p.matcher(telNum);
		return m.matches();
	}

	/**
	 * 将字符串的走出指定长度的部分截取，向后面添加指定字符串
	 * 
	 * @param len
	 * @param repStr
	 */
	public static String sub(String str, int len, String repStr) {
		if (str.length() > len) {
			return str.substring(0, len) + repStr;
		}
		return str;
	}

	/**
	 * 将字符串转换编码
	 * 
	 * @param str
	 * @param oldCharsetName
	 * @param charsetName
	 * @return
	 */
	public static String charsetConvert(String str, Charset oldCharset, Charset charset) {
		String v = null;
		if (str != null) {
			v = new String(str.getBytes(oldCharset), charset);
		}
		return v;
	}
	
	public static String commonCharsetConvert(String str){
		return charsetConvert(str, DEFAULT_OLD_CHARSET, DEFAULT_CHARSET);
	}

	/**
	 * 把unicode 转成中文
	 * 
	 * @return
	 */
	public static String convertUnicode(String ori) {
		char aChar;
		int len = ori.length();
		StringBuffer outBuffer = new StringBuffer(len);
		for (int x = 0; x < len;) {
			aChar = ori.charAt(x++);
			if (aChar == '\\') {
				aChar = ori.charAt(x++);
				if (aChar == 'u') {
					// Read the xxxx
					int value = 0;
					for (int i = 0; i < 4; i++) {
						aChar = ori.charAt(x++);
						switch (aChar) {
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
							value = (value << 4) + aChar - '0';
							break;
						case 'a':
						case 'b':
						case 'c':
						case 'd':
						case 'e':
						case 'f':
							value = (value << 4) + 10 + aChar - 'a';
							break;
						case 'A':
						case 'B':
						case 'C':
						case 'D':
						case 'E':
						case 'F':
							value = (value << 4) + 10 + aChar - 'A';
							break;
						default:
							throw new IllegalArgumentException("Malformed   \\uxxxx   encoding.");
						}
					}
					outBuffer.append((char) value);
				} else {
					if (aChar == 't')
						aChar = '\t';
					else if (aChar == 'r')
						aChar = '\r';
					else if (aChar == 'n')
						aChar = '\n';
					else if (aChar == 'f')
						aChar = '\f';
					outBuffer.append(aChar);
				}
			} else
				outBuffer.append(aChar);

		}
		return outBuffer.toString();
	}

	/**
	 * 产生一串随机的邀请码
	 * 
	 * @param strLength
	 *            邀请码的长度
	 * @return
	 */
	public static String getStrNo(int strLength) {
		String strNo = "";
		Random rand = new Random();
		char option[] = { 'a', 'c', 'd', 'e', 'f', 'h', 'i', 'j', 'k', 'm', 'n', 'p', 'q', 'r', 't', 'u', 'v', 'w', 'x',
				'y' };// 可以出现的字符
		for (int i = 0; i < strLength; i++) {
			int randNum = rand.nextInt(2);
			int strTemp = rand.nextInt(option.length);
			char strBig = option[strTemp];
			char strSmall = (char) ('0' + Math.random() * 10);
			if (randNum == 0) {
				strNo = strNo + strBig;
			} else if (randNum == 1) {
				strNo = strNo + strSmall;
			}
		}
		return strNo;
	}

	/**
	 * 半角转全角
	 * 
	 * @param input
	 *            String.
	 * @return 全角字符串.
	 */
	public static String ToSBC(String input) {
		char c[] = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == ' ') {
				c[i] = '\u3000';
			} else if (c[i] < '\177') {
				c[i] = (char) (c[i] + 65248);

			}
		}
		return new String(c);
	}

	/**
	 * 全角转半角
	 * 
	 * @param input
	 *            String.
	 * @return 半角字符串
	 */
	public static String ToDBC(String input) {
		char c[] = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == '\u3000') {
				c[i] = ' ';
			} else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
				c[i] = (char) (c[i] - 65248);

			}
		}
		String returnString = new String(c);
		return returnString;
	}

	/**
	 * 判断是否数字
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		for (int i = str.length(); --i >= 0;) {
			int chr = str.charAt(i);
			if (chr < 48 || chr > 57)
				return false;
		}
		return true;
	}

	/**
	 * 检测字符串,只能中\英文\数字
	 * 
	 * @param name
	 * @return
	 */
	public static boolean checkName(String name, int len) {
		String reg = "^[\\u4E00-\\u9FA5\\uF900-\\uFA2D\\w]{1," + len + "}$";
		Pattern p = Pattern.compile(reg);
		Matcher m = p.matcher(name);
		return m.matches();
	}

	/**
	 * 隐藏部分手机号
	 * 
	 * @param phone
	 * @return
	 */
	public static String hidePhone(String phone) {
		return phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
	}

	public static String toUpperCase(String str, int begin, int end) {
		char[] chars = str.toCharArray();
		for (int i = begin; i < end; i++) {
			chars[i] = Character.toUpperCase(chars[i]);
		}
		return new String(chars);
	}

	/**
	 * 将文件分割符换成与当前操作系统一致
	 * 
	 * @param path
	 * @return
	 */
	public static String replaceSeparator(String path) {
		if (path == null) {
			return path;
		}

		if (File.separator.equals("/")) {
			return path.replaceAll("\\\\", "/");
		} else {
			return path.replaceAll("/", "\\\\");
		}
	}

	/**
	 * 如果是string类类型就返回本身
	 * 
	 * @param value
	 * @param basicType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T conversion(String value, Class<T> basicType) {
		if (ClassUtils.isStringType(basicType)) {
			return (T) value;
		} else {
			return (T) conversionBasicType(value, basicType);
		}
	}

	/**
	 * 自动把string转化为基本数据类型 string不是基本数据类型
	 * 
	 * @param value
	 * @param basicType
	 * @return
	 */
	public static Object conversionBasicType(String value, Class<?> basicType) {
		if (int.class.isAssignableFrom(basicType)) {
			return Integer.parseInt(value);
		} else if (Integer.class.isAssignableFrom(basicType)) {
			return Integer.valueOf(value);
		} else if (long.class.isAssignableFrom(basicType)) {
			return Long.parseLong(value);
		} else if (Long.class.isAssignableFrom(basicType)) {
			return Long.valueOf(value);
		} else if (float.class.isAssignableFrom(basicType)) {
			return Float.parseFloat(value);
		} else if (Float.class.isAssignableFrom(basicType)) {
			return Float.valueOf(value);
		} else if (short.class.isAssignableFrom(basicType)) {
			return Short.parseShort(value);
		} else if (Short.class.isAssignableFrom(basicType)) {
			return Short.valueOf(value);
		} else if (boolean.class.isAssignableFrom(basicType)) {
			return "1".equals(value) ? true : Boolean.parseBoolean(value);
		} else if (Boolean.class.isAssignableFrom(basicType)) {
			return "1".equals(value) ? true : Boolean.valueOf(value);
		} else if (byte.class.isAssignableFrom(basicType)) {
			return Byte.parseByte(value);
		} else if (Byte.class.isAssignableFrom(basicType)) {
			return Byte.valueOf(value);
		} else if (char.class.isAssignableFrom(basicType)) {
			return value.charAt(0);
		} else if (Character.class.isAssignableFrom(basicType)) {
			return value == null ? null : value.charAt(0);
		} else {
			return value;
		}
	}

	/**
	 * 获取指定长度的随机数字组成的字符串
	 * 
	 * @param len
	 * @return
	 */
	public static String getNumCode(int len) {
		StringBuilder sb = new StringBuilder(len);
		Random random = new Random();
		for (int i = 0; i < len; i++) {
			sb.append((random.nextInt(9)));
		}
		return sb.toString();
	}

	/**
	 * 把不足的地方用指定字符填充
	 * 
	 * @param str
	 * @param complemented
	 * @param length
	 * @param reversal
	 *            是否反转
	 * @return
	 */
	public static String complemented(String str, char complemented, int length, boolean reversal) {
		if (length < str.length()) {
			throw new ShuChaoWenRuntimeException("length error");
		}

		if (length == str.length()) {
			if (reversal) {
				char[] chars = str.toCharArray();
				char[] newChars;
				newChars = new char[length];
				for (int i = chars.length - 1, index=0; i >= 0; i--, index++) {
					newChars[index] = chars[i];
				}
				
				return new String(newChars);
			}else{
				return str;
			}
		}else{
			char[] chars = str.toCharArray();
			char[] newChars = new char[length];
			if(reversal){
				int index = 0;
				for (int i = chars.length - 1; i >= 0; i--, index++) {
					newChars[index] = chars[i];
				}
			}else{
				System.arraycopy(chars, 0, newChars, 0, chars.length);
				int index = chars.length;
				for(int i=0; i<length - chars.length; i++, index ++){
					newChars[index] = complemented;
				}
			}
			return new String(newChars);
		}
	}
	
	public static String urlDecode(String content, String charsetName, int count) throws UnsupportedEncodingException{
		if(count <= 0){
			return content;
		}
		
		String newContent = content;
		for(int i=0; i<count; i++){
			newContent = URLDecoder.decode(newContent, charsetName);
		}
		return newContent;
	}
	
	public static String urlEncode(String content, String charsetName, int count) throws UnsupportedEncodingException{
		if(count <= 0){
			return content;
		}
		
		String newContent = content;
		for(int i=0; i<count; i++){
			newContent = URLEncoder.encode(newContent, charsetName);
		}
		return newContent;
	}
}
