package scw.core.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RegexUtils {
	/**
	 * Regex of simple mobile.
	 */
	private static final String REGEX_MOBILE_SIMPLE = "^[1]\\d{10}$";
	/**
	 * Regex of exact mobile.
	 * <p>
	 * china mobile: 134(0-8), 135, 136, 137, 138, 139, 147, 150, 151, 152, 157,
	 * 158, 159, 178, 182, 183, 184, 187, 188, 198
	 * </p>
	 * <p>
	 * china unicom: 130, 131, 132, 145, 155, 156, 166, 171, 175, 176, 185, 186
	 * </p>
	 * <p>
	 * china telecom: 133, 153, 173, 177, 180, 181, 189, 199
	 * </p>
	 * <p>
	 * global star: 1349
	 * </p>
	 * <p>
	 * virtual operator: 170
	 * </p>
	 */
	private static final String REGEX_MOBILE_EXACT = "^((13[0-9])|(14[5,7])|(15[0-3,5-9])|(16[6])|(17[0,1,3,5-8])|(18[0-9])|(19[8,9]))\\d{8}$";
	/**
	 * Regex of telephone number.
	 */
	private static final String REGEX_TEL = "^0\\d{2,3}[- ]?\\d{7,8}";
	
	/**
	 * Regex of email.
	 */
	public static final String REGEX_EMAIL = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
	/**
	 * Regex of url.
	 */
	private static final String REGEX_URL = "[a-zA-z]+://[^\\s]*";
	/**
	 * Regex of Chinese character.
	 */
	private static final String REGEX_ZH = "^[\\u4e00-\\u9fa5]+$";
	/**
	 * Regex of username.
	 * <p>
	 * scope for "a-z", "A-Z", "0-9", "_", "Chinese character"
	 * </p>
	 * <p>
	 * can't end with "_"
	 * </p>
	 * <p>
	 * length is between 6 to 20
	 * </p>
	 */
	private static final String REGEX_USERNAME = "^[\\w\\u4e00-\\u9fa5]{6,20}(?<!_)$";
	/**
	 * Regex of date which pattern is "yyyy-MM-dd".
	 */
	private static final String REGEX_DATE = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)$";
	/**
	 * Regex of ip address.
	 */
	private static final String REGEX_IP = "((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)";

	// /////////////////////////////////////////////////////////////////////////
	// The following come from http://tool.oschina.net/regex
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Regex of double-byte characters.
	 */
	public static final String REGEX_DOUBLE_BYTE_CHAR = "[^\\x00-\\xff]";
	/**
	 * Regex of blank line.
	 */
	public static final String REGEX_BLANK_LINE = "\\n\\s*\\r";
	/**
	 * Regex of QQ number.
	 */
	public static final String REGEX_QQ_NUM = "[1-9][0-9]{4,}";
	/**
	 * Regex of postal code in China.
	 */
	public static final String REGEX_CHINA_POSTAL_CODE = "[1-9]\\d{5}(?!\\d)";
	/**
	 * Regex of positive integer.
	 */
	public static final String REGEX_POSITIVE_INTEGER = "^[1-9]\\d*$";
	/**
	 * Regex of negative integer.
	 */
	public static final String REGEX_NEGATIVE_INTEGER = "^-[1-9]\\d*$";
	/**
	 * Regex of integer.
	 */
	public static final String REGEX_INTEGER = "^-?[1-9]\\d*$";
	/**
	 * Regex of non-negative integer.
	 */
	public static final String REGEX_NOT_NEGATIVE_INTEGER = "^[1-9]\\d*|0$";
	/**
	 * Regex of non-positive integer.
	 */
	public static final String REGEX_NOT_POSITIVE_INTEGER = "^-[1-9]\\d*|0$";
	/**
	 * Regex of positive float.
	 */
	public static final String REGEX_POSITIVE_FLOAT = "^[1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*$";
	/**
	 * Regex of negative float.
	 */
	public static final String REGEX_NEGATIVE_FLOAT = "^-[1-9]\\d*\\.\\d*|-0\\.\\d*[1-9]\\d*$";
	
	private static final String INNER_IP_PATTERN = "((192\\.168|172\\.([1][6-9]|[2]\\d|3[01]))"
			+ "(\\.([2][0-4]\\d|[2][5][0-5]|[01]?\\d?\\d)){2}|"
			+ "^(\\D)*10(\\.([2][0-4]\\d|[2][5][0-5]|[01]?\\d?\\d)){3})";

	private RegexUtils() {
	};

	/**
	 * Return whether input matches regex of simple mobile.
	 *
	 * @param input
	 *            The input.
	 * @return {@code true}: yes<br>
	 *         {@code false}: no
	 */
	public static boolean isMobileSimple(final CharSequence input) {
		return isMatch(REGEX_MOBILE_SIMPLE, input);
	}

	/**
	 * Return whether input matches regex of exact mobile.
	 *
	 * @param input
	 *            The input.
	 * @return {@code true}: yes<br>
	 *         {@code false}: no
	 */
	public static boolean isMobileExact(final CharSequence input) {
		return isMatch(REGEX_MOBILE_EXACT, input);
	}
	
	/**
	 * Return whether input matches regex of telephone number.
	 *
	 * @param input
	 *            The input.
	 * @return {@code true}: yes<br>
	 *         {@code false}: no
	 */
	public static boolean isTel(final CharSequence input) {
		return isMatch(REGEX_TEL, input);
	}

	/**
	 * Return whether input matches regex of email.
	 *
	 * @param input
	 *            The input.
	 * @return {@code true}: yes<br>
	 *         {@code false}: no
	 */
	public static boolean isEmail(final CharSequence input) {
		return isMatch(REGEX_EMAIL, input);
	}

	/**
	 * Return whether input matches regex of url.
	 *
	 * @param input
	 *            The input.
	 * @return {@code true}: yes<br>
	 *         {@code false}: no
	 */
	public static boolean isURL(final CharSequence input) {
		return isMatch(REGEX_URL, input);
	}

	/**
	 * Return whether input matches regex of Chinese character.
	 *
	 * @param input
	 *            The input.
	 * @return {@code true}: yes<br>
	 *         {@code false}: no
	 */
	public static boolean isZh(final CharSequence input) {
		return isMatch(REGEX_ZH, input);
	}

	/**
	 * Return whether input matches regex of username.
	 * <p>
	 * scope for "a-z", "A-Z", "0-9", "_", "Chinese character"
	 * </p>
	 * <p>
	 * can't end with "_"
	 * </p>
	 * <p>
	 * length is between 6 to 20
	 * </p>
	 * .
	 *
	 * @param input
	 *            The input.
	 * @return {@code true}: yes<br>
	 *         {@code false}: no
	 */
	public static boolean isUsername(final CharSequence input) {
		return isMatch(REGEX_USERNAME, input);
	}

	/**
	 * Return whether input matches regex of date which pattern is "yyyy-MM-dd".
	 *
	 * @param input
	 *            The input.
	 * @return {@code true}: yes<br>
	 *         {@code false}: no
	 */
	public static boolean isDate(final CharSequence input) {
		return isMatch(REGEX_DATE, input);
	}

	/**
	 * Return whether input matches regex of ip address.
	 *
	 * @param input
	 *            The input.
	 * @return {@code true}: yes<br>
	 *         {@code false}: no
	 */
	public static boolean isIP(final CharSequence input) {
		return isMatch(REGEX_IP, input);
	}

	/**
	 * Return whether input matches the regex.
	 *
	 * @param regex
	 *            The regex.
	 * @param input
	 *            The input.
	 * @return {@code true}: yes<br>
	 *         {@code false}: no
	 */
	public static boolean isMatch(final String regex, final CharSequence input) {
		return input != null && input.length() > 0
				&& Pattern.matches(regex, input);
	}

	/**
	 * Return the list of input matches the regex.
	 *
	 * @param regex
	 *            The regex.
	 * @param input
	 *            The input.
	 * @return the list of input matches the regex
	 */
	public static List<String> getMatches(final String regex,
			final CharSequence input) {
		if (input == null)
			return Collections.emptyList();
		List<String> matches = new ArrayList<String>();
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(input);
		while (matcher.find()) {
			matches.add(matcher.group());
		}
		return matches;
	}

	/**
	 * Splits input around matches of the regex.
	 *
	 * @param input
	 *            The input.
	 * @param regex
	 *            The regex.
	 * @return the array of strings computed by splitting input around matches
	 *         of regex
	 */
	public static String[] getSplits(final String input, final String regex) {
		if (input == null)
			return new String[0];
		return input.split(regex);
	}

	/**
	 * Replace the first subsequence of the input sequence that matches the
	 * regex with the given replacement string.
	 *
	 * @param input
	 *            The input.
	 * @param regex
	 *            The regex.
	 * @param replacement
	 *            The replacement string.
	 * @return the string constructed by replacing the first matching
	 *         subsequence by the replacement string, substituting captured
	 *         subsequences as needed
	 */
	public static String getReplaceFirst(final String input,
			final String regex, final String replacement) {
		if (input == null)
			return "";
		return Pattern.compile(regex).matcher(input).replaceFirst(replacement);
	}

	/**
	 * Replace every subsequence of the input sequence that matches the pattern
	 * with the given replacement string.
	 *
	 * @param input
	 *            The input.
	 * @param regex
	 *            The regex.
	 * @param replacement
	 *            The replacement string.
	 * @return the string constructed by replacing each matching subsequence by
	 *         the replacement string, substituting captured subsequences as
	 *         needed
	 */
	public static String getReplaceAll(final String input, final String regex,
			final String replacement) {
		if (input == null)
			return "";
		return Pattern.compile(regex).matcher(input).replaceAll(replacement);
	}
	
	/**
	 * 判断是否是内网IP
	 * 
	 * @param ip
	 * @return
	 */
	public static boolean isInnerIP(String ip) {
		Pattern p = Pattern.compile(INNER_IP_PATTERN);
		Matcher matcher = p.matcher(ip);
		return matcher.find();
	}
}
