package scw.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 中国身份证验证
 * 
 * @author shuchaowen
 *
 */
public final class ChinaIDCardUtils {
	private static final char[] ID_CARD_CODE = new char[] { '1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2' };
	private static final int[] ID_CARD_FACTOR = new int[] { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 };

	private static final Map<String, String> CITY_MAP = new HashMap<String, String>();
	static {
		CITY_MAP.put("11", "北京");
		CITY_MAP.put("12", "天津");
		CITY_MAP.put("13", "河北");
		CITY_MAP.put("14", "山西");
		CITY_MAP.put("15", "内蒙古");
		CITY_MAP.put("21", "辽宁");
		CITY_MAP.put("22", "吉林");
		CITY_MAP.put("23", "黑龙江");
		CITY_MAP.put("31", "上海");
		CITY_MAP.put("32", "江苏");
		CITY_MAP.put("33", "浙江");
		CITY_MAP.put("34", "安徽");
		CITY_MAP.put("35", "福建");
		CITY_MAP.put("36", "江西");
		CITY_MAP.put("37", "山东");
		CITY_MAP.put("41", "河南");
		CITY_MAP.put("42", "湖北");
		CITY_MAP.put("43", "湖南");
		CITY_MAP.put("44", "广东");
		CITY_MAP.put("45", "广西");
		CITY_MAP.put("46", "海南");
		CITY_MAP.put("50", "重庆");
		CITY_MAP.put("51", "四川");
		CITY_MAP.put("52", "贵州");
		CITY_MAP.put("53", "云南");
		CITY_MAP.put("54", "西藏");
		CITY_MAP.put("61", "陕西");
		CITY_MAP.put("62", "甘肃");
		CITY_MAP.put("63", "青海");
		CITY_MAP.put("64", "宁夏");
		CITY_MAP.put("65", "新疆");
		CITY_MAP.put("71", "台湾");
		CITY_MAP.put("81", "香港");
		CITY_MAP.put("82", "澳门");
		CITY_MAP.put("91", "国外");
	}

	/**
	 * Regex of id card number which length is 15.
	 */
	private static final String REGEX_ID_CARD15 = "^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$";
	/**
	 * Regex of id card number which length is 18.
	 */
	private static final String REGEX_ID_CARD18 = "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9Xx])$";

	/**
	 * Return whether input matches regex of exact id card number which length
	 * is 18.
	 *
	 * @param input
	 *            The input.
	 * @return {@code true}: yes<br>
	 *         {@code false}: no
	 */
	public static boolean isIDCard18Exact(final CharSequence input) {
		if (isIDCard18(input)) {
			if (CITY_MAP.containsKey(input.subSequence(0, 2).toString())) {
				int weightSum = 0;
				for (int i = 0; i < 17; ++i) {
					weightSum += (input.charAt(i) - '0') * ID_CARD_FACTOR[i];
				}
				int idCardMod = weightSum % 11;
				char idCardLast = input.charAt(17);
				return idCardLast == ID_CARD_CODE[idCardMod];
			}
		}
		return false;
	}

	/**
	 * Return whether input matches regex of id card number which length is 15.
	 *
	 * @param input
	 *            The input.
	 * @return {@code true}: yes<br>
	 *         {@code false}: no
	 */
	public static boolean isIDCard15(final CharSequence input) {
		return RegexUtils.isMatch(REGEX_ID_CARD15, input);
	}

	/**
	 * Return whether input matches regex of id card number which length is 18.
	 *
	 * @param input
	 *            The input.
	 * @return {@code true}: yes<br>
	 *         {@code false}: no
	 */
	public static boolean isIDCard18(final CharSequence input) {
		return RegexUtils.isMatch(REGEX_ID_CARD18, input);
	}
}
