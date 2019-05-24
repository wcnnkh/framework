package scw.core.utils;

import java.io.File;
import java.nio.CharBuffer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import scw.core.exception.ParameterException;

public final class StringUtils {
	private static final String IOS_NULL = "(null)";
	private static final char[] DEFAULT_SPLIT_CHARS = new char[] { ' ', ',', ';', '、' };

	private StringUtils() {
	};

	public static boolean isNull(boolean trim, String... text) {
		for (String s : text) {
			if (s == null || s.length() == 0 || (trim && s.trim().length() == 0)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isNull(String... text) {
		return isNull(false, text);
	}

	public static boolean isNull(CharSequence text) {
		return isEmpty(text);
	}

	/**
	 * 在ios中由于前端未做判断导致的空 (null)
	 * 
	 * @param strs
	 * @return
	 */
	public static boolean isNullByIOS(CharSequence... text) {
		for (CharSequence s : text) {
			if (s == null || s.length() == 0 || IOS_NULL.equals(s)) {
				return true;
			}
		}
		return false;
	}

	public static boolean trimIsNull(String... strs) {
		return isNull(true, strs);
	}

	public static boolean isEmpty(CharSequence text) {
		return text == null || text.length() == 0;
	}

	public static boolean isEmpty(CharSequence... text) {
		for (CharSequence s : text) {
			if (s == null || s.length() == 0) {
				return true;
			}
		}
		return false;
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
		return StringSplitUtils.split(str, DEFAULT_SPLIT_CHARS);
	}

	public static String join(Collection<?> collection, String join) {
		if (collection == null || collection.isEmpty()) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		Iterator<?> iterator = collection.iterator();
		if (isNull(join)) {
			while (iterator.hasNext()) {
				Object o = iterator.next();
				if (o == null) {
					continue;
				}

				sb.append(o);
			}
			return sb.toString();
		} else {
			while (iterator.hasNext()) {
				Object o = iterator.next();
				if (o == null) {
					continue;
				}

				if (sb.length() != 0) {
					sb.append(join);
				}
				sb.append(o);
			}
			return sb.toString();
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> splitList(Class<T> type, String strs, String filter, boolean isTrim) {
		Assert.notNull(type);
		Assert.notNull(filter);

		List<T> list = new ArrayList<T>();
		if (strs == null) {
			return list;
		}

		String[] arr = StringSplitUtils.split(strs, isTrim, filter);
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

	public static String toLowerCase(String str, int begin, int end) {
		char[] chars = str.toCharArray();
		for (int i = begin; i < end; i++) {
			chars[i] = Character.toLowerCase(chars[i]);
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
	 * 把不足的地方用指定字符填充
	 * 
	 * @param str
	 * @param complemented
	 * @param length
	 * @return
	 */
	public static String complemented(String str, char complemented, int length) {
		if (length < str.length()) {
			throw new ParameterException("length error [" + str + "]");
		}

		if (length == str.length()) {
			return str;
		} else {
			CharBuffer charBuffer = CharBuffer.allocate(length);
			for (int i = 0; i < length - str.length(); i++) {
				charBuffer.put(complemented);
			}
			charBuffer.put(str);
			return new String(charBuffer.array());
		}
	}

	/**
	 * 颠倒字符串
	 * 
	 * @param str
	 * @return
	 */
	public static String reversed(String str) {
		if (isEmpty(str)) {
			return str;
		}

		return new String(reversedCharArray(str.toCharArray()));
	}

	// 根据Unicode编码判断中文汉字和符号
	public static boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
			return true;
		}
		return false;
	}

	/**
	 * 判断字符串中是否存在中文
	 * 
	 * @param charSequence
	 * @return
	 */
	public static boolean containsChinese(CharSequence charSequence) {
		if (charSequence == null || charSequence.length() == 0) {
			return false;
		}

		for (int i = 0; i < charSequence.length(); i++) {
			if (isChinese(charSequence.charAt(i))) {
				return true;
			}
		}
		return false;
	}

	public static char[] mergeCharArray(char[]... chars) {
		StringBuilder sb = new StringBuilder();
		for (char[] cs : chars) {
			sb.append(cs);
		}
		return sb.toString().toCharArray();
	}

	public static char[] reversedCharArray(char[] array) {
		if (array == null) {
			return array;
		}

		char[] newArray = new char[array.length];
		int index = 0;
		for (int i = newArray.length - 1; i >= 0; i--, index++) {
			newArray[index] = array[i];
		}
		return newArray;
	}

	/**
	 * 把钱保留两位小数
	 * 
	 * @param price
	 *            单位:分
	 * @return
	 */
	public static String formatNothingToYuan(double price) {
		return formatNumberPrecision(price / 100, 2);
	}

	/**
	 * 保留小数点精度
	 * 
	 * @param number
	 * @param len
	 *            保留多少位
	 * @return
	 */
	public static String formatNumberPrecision(double number, int len) {
		if (len < 0) {
			throw new IllegalStateException("len < 0");
		}

		if (len == 0) {
			return ((long) number) + "";
		}

		if (number == 0) {
			CharBuffer charBuffer = CharBuffer.allocate(len + 2);
			charBuffer.put('0');
			charBuffer.put('.');
			for (int i = 0; i < len; i++) {
				charBuffer.put('0');
			}
			return new String(charBuffer.array());
		}

		CharBuffer charBuffer = CharBuffer.allocate(len + 3);
		charBuffer.put("#0.");
		for (int i = 0; i < len; i++) {
			charBuffer.put("0");
		}
		DecimalFormat decimalFormat = new DecimalFormat(new String(charBuffer.array()));
		return decimalFormat.format(number);
	}

	public static String format(String text, String placeholder, Object... args) {
		if (isEmpty(text) || isEmpty(placeholder) || args == null || args.length == 0) {
			return text;
		}

		int lastFind = 0;
		StringBuilder sb = null;
		for (int i = 0; i < args.length; i++) {
			int index = text.indexOf(placeholder, lastFind);
			if (index == -1) {
				break;
			}

			if (sb == null) {
				sb = new StringBuilder(text.length() * 2);
			}

			sb.append(text.substring(lastFind, index));
			sb.append(args[i]);
			lastFind = index + placeholder.length();
		}

		if (lastFind == 0) {
			return text;
		} else {
			sb.append(text.substring(lastFind));
		}
		return sb.toString();
	}

	/**
	 * 判断字符串是否与通配符匹配 只能存在通配符\\*和? ?代表1个 *代表多个
	 * 
	 * @param text
	 * @param match
	 * @return
	 */
	public static boolean test(String text, String match) {
		if (StringUtils.isEmpty(match)) {
			return false;
		}

		if ("*".equals(match)) {
			return true;
		}

		if (match.indexOf("*") == -1) {
			if (match.indexOf("?") == -1) {
				return text.equals(match);
			} else {
				return test(text, match, '?', false);
			}
		}

		String[] arr = StringSplitUtils.split(match, true, '*');
		int begin = 0;
		int len = text.length();
		for (String v : arr) {
			int vLen = v.length();
			if (len < vLen) {
				return false;
			}

			boolean b = false;
			int a = begin;
			for (; a < len; a++) {
				int end = a + vLen;
				if (end > text.length()) {
					return false;
				}

				String c = text.substring(a, end);
				if (test(c, v, '?', false)) {
					b = true;
					break;
				}
			}

			if (!b) {
				return false;
			}

			begin = a + vLen;
		}
		return true;
	}

	public static boolean test(String text, String match, char matchChar, boolean multiple) {
		if (match.indexOf(matchChar) == -1) {
			return text.equals(match);
		}

		int size = match.length();
		if (multiple) {
			int index = 0;
			int findIndex = 0;
			for (int i = 0; i < size; i++) {
				char c = match.charAt(i);
				if (c != matchChar) {
					continue;
				}

				String v = match.substring(index, i);
				index = i;
				if (v.length() == 0) {
					continue;
				}

				int tempIndex = text.indexOf(v, findIndex);
				if (tempIndex == -1) {
					return false;
				}

				findIndex = tempIndex + v.length();
			}
			return true;
		} else {
			if (text.length() != size) {
				return false;
			}

			for (int i = 0; i < size; i++) {
				if (match.charAt(i) == matchChar) {
					continue;
				}

				if (match.charAt(i) != text.charAt(i)) {
					return false;
				}
			}

			return true;
		}
	}
}
