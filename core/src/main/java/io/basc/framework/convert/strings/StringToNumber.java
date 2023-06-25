package io.basc.framework.convert.strings;

import java.math.BigDecimal;
import java.util.function.Function;
import java.util.function.IntPredicate;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.StringUtils;

public class StringToNumber implements Function<String, Number> {

	/**
	 * 默认使用10进制
	 */
	public static final StringToNumber DEFAULT = new StringToNumber(false, 10);

	private final boolean unsigned;
	private final int radix;

	/**
	 * @param unsigned
	 * @param radix    进制(小于等于0表示未知)
	 */
	public StringToNumber(boolean unsigned, int radix) {
		this.unsigned = unsigned;
		this.radix = radix;
	}

	public boolean isUnsigned() {
		return unsigned;
	}

	public int getRadix() {
		return radix;
	}

	/**
	 * @see BigDecimal
	 */
	@Nullable
	@Override
	public Number apply(String source) {
		String value = format(source);
		if (StringUtils.isEmpty(value)) {
			return null;
		}

		return new BigDecimal(value);
	}

	public String format(String source) throws NumberFormatException {
		if (StringUtils.isEmpty(source)) {
			return source;
		}

		String numberString = source.trim();
		if (numberString.isEmpty()) {
			return numberString;
		}

		if (unsigned && numberString.startsWith("-")) {
			throw new NumberFormatException("Cannot contain symbols");
		} else if (numberString.startsWith("+")) {
			numberString = numberString.substring(0);
		}
		return numberString;
	}

	/**
	 * 转换为可解析的数字字符串, 不支持科学计数法
	 * 
	 * @param source
	 * @return
	 */
	public String extractNumberic(CharSequence source) {
		return extractNumberic(source,
				(c) -> (radix > 10 || radix <= 0) ? Character.isLetterOrDigit(c) : Character.isDigit(c));
	}

	@Nullable
	public String extractNumberic(@Nullable CharSequence source, @Nullable IntPredicate filter) {
		if (StringUtils.isEmpty(source)) {
			return null;
		}

		char[] chars = new char[source.length()];
		int pos = 0;
		boolean findPoint = false;
		for (int i = 0, len = source.length(); i < len; i++) {
			char chr = source.charAt(i);
			if (isNumberSign(chr)) {
				if (pos == 0) {
					// 无符号类型的不应该存在符号
					if (unsigned && chr == '-') {
						// 不支持解析？
						return null;
					}
				}
				chars[pos++] = chr;
				continue;
			}

			if (radix > 10) {
				if (chr == '#' && !findPoint && (pos == 0 || (pos == 1 && isNumberSign(chars[0])))) {
					chars[pos++] = chr;
					continue;
				}
			}

			if (chr == '.') {
				if (findPoint) {
					continue;
				}

				findPoint = true;
				chars[pos++] = chr;
				continue;
			}

			if (filter == null || filter.test(chr)) {
				chars[pos++] = chr;
			}
		}
		return pos == 0 ? null : new String(chars, 0, pos);
	}

	private static boolean isNumberSign(char chr) {
		return chr == '-' || chr == '+';
	}

	/**
	 * 是否是可解析的数字字符串
	 * 
	 * @param source
	 * @return
	 */
	public boolean isNumeric(String source) {
		String numberString;
		try {
			numberString = format(source);
		} catch (NumberFormatException ignore) {
			return false;
		}

		if (isNumeric(numberString,
				(c) -> (radix > 10 || radix <= 0) ? Character.isLetterOrDigit(c) : Character.isDigit(c))) {
			return true;
		}

		try {
			apply(numberString);
			return true;
		} catch (NumberFormatException e) {
		}
		return false;
	}

	public boolean isNumeric(@Nullable CharSequence source, @Nullable IntPredicate filter) {
		if (StringUtils.isEmpty(source)) {
			return false;
		}

		boolean findPoint = false;
		char[] chars = new char[source.length()];
		int pos = 0;
		for (int i = 0, len = source.length(); i < len; i++) {
			char chr = source.charAt(i);
			if (chr == '-' || chr == '+') {
				if (pos == 0) {
					// 如果是无符号的
					if (unsigned && chr == '-') {
						return false;
					}

					pos++;
					continue;
				}

				return false;
			}

			if (radix > 10) {
				if (chr == '#') {
					if (!findPoint && (pos == 0 || (pos == 1 && isNumberSign(chars[0])))) {
						chars[pos++] = chr;
						continue;
					}
					return false;
				}
			}

			if (chr == '.') {
				if (findPoint) {
					return false;
				}

				findPoint = true;
				pos++;
				continue;
			}

			if (filter != null && !filter.test(chr)) {
				return false;
			}
			pos++;
		}
		return true;
	}
}
