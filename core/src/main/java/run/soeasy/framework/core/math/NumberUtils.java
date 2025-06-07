/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package run.soeasy.framework.core.math;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.CharBuffer;
import java.text.DecimalFormat;
import java.util.function.IntPredicate;

import lombok.NonNull;
import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.function.ThrowingFunction;

/**
 * Miscellaneous utility methods for number conversion and parsing. Mainly for
 * internal use within the framework; consider Jakarta's Commons Lang for a more
 * comprehensive suite of string utilities.
 */
public abstract class NumberUtils {
	/**
	 * Parse the given text into a number instance of the given target class, using
	 * the corresponding {@code decode} / {@code valueOf} methods.
	 * <p>
	 * Trims the input {@code String} before attempting to parse the number.
	 * Supports numbers in hex format (with leading "0x", "0X" or "#") as well.
	 * 
	 * @param text        the text to convert
	 * @param targetClass the target class to parse into
	 * @return the parsed number
	 * @throws IllegalArgumentException if the target class is not supported (i.e.
	 *                                  not a standard Number subclass as included
	 *                                  in the JDK)
	 * @see Byte#decode
	 * @see Short#decode
	 * @see Integer#decode
	 * @see Long#decode
	 * @see #decodeBigInteger(String)
	 * @see Float#valueOf
	 * @see Double#valueOf
	 * @see java.math.BigDecimal#BigDecimal(String)
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Number> T parseNumber(@NonNull String text, @NonNull Class<T> targetClass) {
		String trimmed = text.trim();
		if (targetClass.equals(Byte.class)) {
			return (T) (isHexNumber(trimmed) ? Byte.decode(trimmed) : Byte.valueOf(trimmed));
		} else if (targetClass.equals(Short.class)) {
			return (T) (isHexNumber(trimmed) ? Short.decode(trimmed) : Short.valueOf(trimmed));
		} else if (targetClass.equals(Integer.class)) {
			return (T) (isHexNumber(trimmed) ? Integer.decode(trimmed) : Integer.valueOf(trimmed));
		} else if (targetClass.equals(Long.class)) {
			return (T) (isHexNumber(trimmed) ? Long.decode(trimmed) : Long.valueOf(trimmed));
		} else if (targetClass.equals(BigInteger.class)) {
			return (T) (isHexNumber(trimmed) ? decodeBigInteger(trimmed) : new BigInteger(trimmed));
		} else if (targetClass.equals(Float.class)) {
			return (T) Float.valueOf(trimmed);
		} else if (targetClass.equals(Double.class)) {
			return (T) Double.valueOf(trimmed);
		} else if (targetClass.equals(BigDecimal.class) || targetClass.equals(Number.class)) {
			return (T) new BigDecimal(trimmed);
		} else {
			throw new IllegalArgumentException(
					"Cannot convert String [" + text + "] to target class [" + targetClass.getName() + "]");
		}
	}

	/**
	 * 是否是数字类型
	 * 
	 * @param type
	 * @return
	 */
	public static boolean isNumber(Class<?> type) {
		return type == long.class || type == int.class || type == byte.class || type == short.class
				|| type == float.class || type == double.class || Number.class.isAssignableFrom(type);
	}

	/**
	 * 是否是整数类型
	 * 
	 * @param type
	 * @return
	 */
	public static boolean isInteger(Class<?> type) {
		return type == long.class || type == int.class || type == byte.class || type == short.class
				|| BigInteger.class.isAssignableFrom(type);
	}

	/**
	 * Determine whether the given value String indicates a hex number, i.e. needs
	 * to be passed into {@code Integer.decode} instead of {@code Integer.valueOf}
	 * (etc).
	 */
	private static boolean isHexNumber(String value) {
		int index = (value.startsWith("-") ? 1 : 0);
		return (value.startsWith("0x", index) || value.startsWith("0X", index) || value.startsWith("#", index));
	}

	/**
	 * Decode a {@link java.math.BigInteger} from a {@link String} value. Supports
	 * decimal, hex and octal notation.
	 * 
	 * @see BigInteger#BigInteger(String, int)
	 */
	private static BigInteger decodeBigInteger(String value) {
		int radix = 10;
		int index = 0;
		boolean negative = false;

		// Handle minus sign, if present.
		if (value.startsWith("-")) {
			negative = true;
			index++;
		}

		// Handle radix specifier, if present.
		if (value.startsWith("0x", index) || value.startsWith("0X", index)) {
			index += 2;
			radix = 16;
		} else if (value.startsWith("#", index)) {
			index++;
			radix = 16;
		} else if (value.startsWith("0", index) && value.length() > 1 + index) {
			index++;
			radix = 8;
		}

		BigInteger result = new BigInteger(value.substring(index), radix);
		return (negative ? result.negate() : result);
	}

	/**
	 * 是否是整数
	 * 
	 * @param number
	 * @return
	 */
	public static boolean isInteger(BigDecimal number) {
		return number.signum() == 0 || number.scale() <= 0 || number.stripTrailingZeros().scale() <= 0;
	}

	/**
	 * 清除无用的0
	 * 
	 * @see BigDecimal#stripTrailingZeros()
	 * @see BigDecimal#setScale(int)
	 * @param number
	 * @return
	 */
	public static BigDecimal stripTrailingZeros(BigDecimal number) {
		if (number.signum() == 0 || number.scale() <= 0) {
			return number;
		}

		// 存在小数
		BigDecimal target = number.stripTrailingZeros();
		if (target.scale() <= 0) {
			return number.setScale(0);
		}
		return number;
	}

	public static String format(BigDecimal number, NumberUnit... units) {
		return format(number, (e) -> stripTrailingZeros(e).toPlainString(), units);
	}

	public static BigDecimal parse(String source, NumberUnit... units) {
		return parse(source, (e) -> new BigDecimal(e), units);
	}

	/**
	 * 通过数字单位格式化数字(忽略正负符号)
	 * 
	 * @param number
	 * @param toString
	 * @param units
	 * @return
	 */
	public static <E extends Throwable> String format(@NonNull BigDecimal number,
			@NonNull ThrowingFunction<? super BigDecimal, ? extends String, ? extends E> toString, NumberUnit... units)
			throws E {
		if (units == null || units.length == 0 || number.compareTo(BigDecimal.ZERO) == 0) {
			return toString.apply(number.abs());
		}

		StringBuilder sb = new StringBuilder();
		format(sb, number.abs(), toString, 0, units.length, units);
		return sb.toString();
	}

	private static <E extends Throwable> void format(StringBuilder sb, BigDecimal number,
			ThrowingFunction<? super BigDecimal, ? extends String, ? extends E> toString, int startUnitsIndex,
			int endUnitsIndex, NumberUnit... units) throws E {
		BigDecimal surplus = number;
		for (int i = startUnitsIndex; i < Math.min(endUnitsIndex, units.length); i++) {
			NumberUnit unit = units[i];
			if (unit.getRadix().compareTo(surplus) > 0) {
				continue;
			}

			BigDecimal[] decimals = surplus.divideAndRemainder(unit.getRadix());
			// 是否是最后一个
			boolean last = true;
			for (NumberUnit u : units) {
				if (decimals[1].compareTo(u.getRadix()) >= 0) {
					last = false;
					break;
				}
			}

			if (last) {
				decimals[0] = surplus.divide(unit.getRadix());
				decimals[1] = BigDecimal.ZERO;
			}

			// 是否嵌套
			boolean notNested = last || decimals[1].compareTo(BigDecimal.ZERO) == 0
			// 如果小于1那个取模就会越来越大
					|| unit.getRadix().compareTo(BigDecimal.ONE) < 0;
			if (notNested) {
				sb.append(toString.apply(decimals[0]));
			} else {
				format(sb, decimals[0], toString, i, units.length - 1, units);
			}

			sb.append(unit.getName());
			if (!notNested && i < units.length - 1
					&& decimals[1].divideToIntegralValue(units[i + 1].getRadix()).compareTo(BigDecimal.ZERO) == 0) {
				sb.append(toString.apply(BigDecimal.ZERO));
			}
			surplus = decimals[1];
		}

		if (surplus.compareTo(BigDecimal.ZERO) > 0) {
			sb.append(toString.apply(surplus));
		}
	}

	public static <E extends Throwable> BigDecimal parse(String source,
			ThrowingFunction<? super String, ? extends BigDecimal, ? extends E> converter, NumberUnit... units)
			throws E {
		for (NumberUnit unit : units) {
			int index = source.indexOf(unit.getName());
			if (index == -1) {
				continue;
			}

			String left = source.substring(0, index);
			BigDecimal value = parse(left, converter, units).multiply(unit.getRadix());
			if (index < source.length() - unit.getName().length()) {
				String right = source.substring(index + unit.getName().length());
				value = value.add(parse(right, converter, units));
			}
			return value;
		}
		return converter.apply(source);
	}

	/**
	 * 保留小数点精度
	 * 
	 * @param number
	 * @param len    保留多少位
	 * @return
	 */
	public static String formatPrecision(double number, int len) {
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
		return new DecimalFormat(new String(charBuffer.array())).format(number);
	}

	public static String extractNumberic(int radix, boolean unsigned, CharSequence source, IntPredicate filter) {
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

	public static String extractNumberic(int radix, boolean unsigned, CharSequence source) {
		return extractNumberic(radix, unsigned, source,
				(c) -> (radix > 10 || radix <= 0) ? Character.isLetterOrDigit(c) : Character.isDigit(c));
	}

	public static boolean isNumeric(int radix, boolean unsigned, CharSequence source) {
		return isNumeric(radix, unsigned, source,
				(c) -> (radix > 10 || radix <= 0) ? Character.isLetterOrDigit(c) : Character.isDigit(c));
	}

	public static boolean isNumeric(int radix, boolean unsigned, CharSequence source, IntPredicate filter) {
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

	public static boolean isNumberSign(char chr) {
		return chr == '-' || chr == '+';
	}
}
