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
 * 数字处理工具类，提供数字解析、类型判断、格式化、提取等功能。
 * 支持多种数字类型（整数、浮点数、大数字等）的处理，包括十六进制、八进制等表示法的解析，
 * 以及数字单位的格式化与解析。
 */
public abstract class NumberUtils {
	/**
	 * 将字符串解析为指定类型的数字，支持十进制、十六进制（0x/0X/#前缀）和八进制表示。
	 * <p>
	 * 解析前会先去除字符串两端的空白，根据目标类型调用对应的解析方法（如{@link Integer#decode}、{@link BigDecimal#BigDecimal(String)}等）。
	 * 
	 * @param <T>         数字类型泛型，必须是Number的子类（如Integer、Long、BigDecimal等），作为返回的数字类型
	 * @param text        待解析的字符串，不可为null，会先去除两端空白
	 * @param targetClass 目标数字类型的Class对象，不可为null（如Integer.class）
	 * @return 解析后的数字对象，类型为T
	 * @throws IllegalArgumentException 如果字符串无法解析为目标类型，或目标类型不支持
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
	 * 判断指定的类是否为数字类型（包括基本数字类型和Number的子类）。
	 * 
	 * @param type 待判断的类类型，可为null（返回false）
	 * @return 如果是数字类型（如long.class、Integer.class、BigDecimal.class等）则返回true，否则返回false
	 */
	public static boolean isNumber(Class<?> type) {
		return type == long.class || type == int.class || type == byte.class || type == short.class
				|| type == float.class || type == double.class || Number.class.isAssignableFrom(type);
	}

	/**
	 * 判断指定的类是否为整数类型（包括基本整数类型和BigInteger）。
	 * 
	 * @param type 待判断的类类型，可为null（返回false）
	 * @return 如果是整数类型（如long.class、Integer.class、BigInteger.class等）则返回true，否则返回false
	 */
	public static boolean isInteger(Class<?> type) {
		return type == long.class || type == int.class || type == byte.class || type == short.class
				|| BigInteger.class.isAssignableFrom(type);
	}

	/**
	 * 判断给定的字符串是否表示十六进制数字（需要特殊解析，如0x、0X或#前缀）。
	 * 
	 * @param value 待判断的字符串
	 * @return 如果是十六进制数字表示则返回true，否则返回false
	 */
	private static boolean isHexNumber(String value) {
		int index = (value.startsWith("-") ? 1 : 0);
		return (value.startsWith("0x", index) || value.startsWith("0X", index) || value.startsWith("#", index));
	}

	/**
	 * 从字符串解析BigInteger，支持十进制、十六进制和八进制表示。
	 * 
	 * @param value 待解析的字符串
	 * @return 解析后的BigInteger
	 * @see BigInteger#BigInteger(String, int)
	 */
	private static BigInteger decodeBigInteger(String value) {
		int radix = 10;
		int index = 0;
		boolean negative = false;

		// 处理负号
		if (value.startsWith("-")) {
			negative = true;
			index++;
		}

		// 处理基数前缀（十六进制0x/0X/#，八进制0）
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
	 * 判断BigDecimal是否为整数（无小数部分或小数部分为0）。
	 * 
	 * @param number 待判断的BigDecimal，不可为null
	 * @return 如果是整数则返回true，否则返回false
	 */
	public static boolean isInteger(BigDecimal number) {
		return number.signum() == 0 || number.scale() <= 0 || number.stripTrailingZeros().scale() <= 0;
	}

	/**
	 * 清除BigDecimal尾部的无效零（如100.000变为100，123.4500变为123.45）。
	 * 
	 * @param number 待处理的BigDecimal，可为null（返回null）
	 * @return 清除尾部零后的BigDecimal；如果原数为整数（如100.0），则返回整数形式（100）
	 * @see BigDecimal#stripTrailingZeros()
	 * @see BigDecimal#setScale(int)
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

	/**
	 * 使用默认格式化器将BigDecimal按指定单位格式化（如单位为"元"、"角"等）。
	 * <p>
	 * 默认格式化器会清除尾部零并转为普通字符串（{@link #stripTrailingZeros(BigDecimal)}和{@link BigDecimal#toPlainString()}）。
	 * 
	 * @param number 待格式化的BigDecimal，不可为null
	 * @param units  单位数组（如NumberUnit实例），定义格式化的单位及进制
	 * @return 格式化后的字符串
	 */
	public static String format(BigDecimal number, NumberUnit... units) {
		return format(number, (e) -> stripTrailingZeros(e).toPlainString(), units);
	}

	/**
	 * 将字符串按指定单位解析为BigDecimal。
	 * 
	 * @param source 待解析的字符串
	 * @param units  单位数组（如NumberUnit实例），定义解析的单位及进制
	 * @return 解析后的BigDecimal
	 */
	public static BigDecimal parse(String source, NumberUnit... units) {
		return parse(source, (e) -> new BigDecimal(e), units);
	}

	/**
	 * 通过数字单位格式化BigDecimal（忽略正负符号），支持自定义格式化函数。
	 * 
	 * @param <E>      异常类型泛型，必须是Throwable的子类，为格式化函数可能抛出的异常类型
	 * @param number   待格式化的BigDecimal，不可为null（取绝对值处理）
	 * @param toString 自定义格式化函数，将BigDecimal转换为字符串，不可为null
	 * @param units    单位数组（如NumberUnit实例），定义格式化的单位及进制，可为空
	 * @return 格式化后的字符串
	 * @throws E 格式化函数执行过程中可能抛出的异常
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

	/**
	 * 递归格式化数字到字符串缓冲区，辅助{@link #format(BigDecimal, ThrowingFunction, NumberUnit...)}方法。
	 * 
	 * @param <E>              异常类型泛型，为格式化函数可能抛出的异常类型
	 * @param sb               字符串缓冲区，用于拼接结果
	 * @param number           待格式化的BigDecimal（绝对值）
	 * @param toString         自定义格式化函数
	 * @param startUnitsIndex  开始处理的单位索引
	 * @param endUnitsIndex    结束处理的单位索引
	 * @param units            单位数组
	 * @throws E 格式化函数执行过程中可能抛出的异常
	 */
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

	/**
	 * 将字符串按指定单位解析为BigDecimal，支持自定义转换函数。
	 * 
	 * @param <E>        异常类型泛型，必须是Throwable的子类，为转换函数可能抛出的异常类型
	 * @param source     待解析的字符串
	 * @param converter  自定义转换函数，将字符串转换为BigDecimal，不可为null
	 * @param units      单位数组（如NumberUnit实例），定义解析的单位及进制
	 * @return 解析后的BigDecimal
	 * @throws E 转换函数执行过程中可能抛出的异常
	 */
	public static <E extends Throwable> BigDecimal parse(String source,
			@NonNull ThrowingFunction<? super String, ? extends BigDecimal, ? extends E> converter, NumberUnit... units)
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
	 * 保留指定小数位数格式化double（如保留2位小数）。
	 * 
	 * @param number 待格式化的double值
	 * @param len    保留的小数位数，必须大于等于0
	 * @return 格式化后的字符串
	 * @throws IllegalStateException 如果len小于0
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

	/**
	 * 从字符序列中提取符合指定基数和符号规则的数字字符串，支持自定义过滤函数。
	 * 
	 * @param radix    数字的基数（如10为十进制，16为十六进制）
	 * @param unsigned 是否为无符号数字（无符号则不允许负号）
	 * @param source   待提取的字符序列
	 * @param filter   自定义过滤函数，判断字符是否保留，可为null（保留所有符合基数的字符）
	 * @return 提取的数字字符串；如果无有效字符则返回null
	 */
	public static String extractNumberic(int radix, boolean unsigned, CharSequence source, IntPredicate filter) {
		if (StringUtils.isEmpty(source)) {
			return null;
		}

		char[] chars = new char[source.length()];
		int pos = 0;
		boolean findPoint = false;
		for (int i = 0, len = source.length(); i < len; i++) {
			char chr = source.charAt(i);
			if (chr == '-' || chr == '+') {
				if (pos == 0) {
					// 无符号类型的不应该存在负号
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

	/**
	 * 从字符序列中提取符合指定基数和符号规则的数字字符串（默认过滤函数：字母数字字符）。
	 * 
	 * @param radix    数字的基数（如10为十进制，16为十六进制）
	 * @param unsigned 是否为无符号数字（无符号则不允许负号）
	 * @param source   待提取的字符序列
	 * @return 提取的数字字符串；如果无有效字符则返回null
	 */
	public static String extractNumberic(int radix, boolean unsigned, CharSequence source) {
		return extractNumberic(radix, unsigned, source,
				(c) -> (radix > 10 || radix <= 0) ? Character.isLetterOrDigit(c) : Character.isDigit(c));
	}

	/**
	 * 判断字符序列是否为符合指定基数和符号规则的数字。
	 * 
	 * @param radix    数字的基数（如10为十进制，16为十六进制）
	 * @param unsigned 是否为无符号数字（无符号则不允许负号）
	 * @param source   待判断的字符序列
	 * @param filter   自定义过滤函数，判断字符是否有效，可为null
	 * @return 如果是有效的数字则返回true，否则返回false
	 */
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

	/**
	 * 判断字符序列是否为符合指定基数和符号规则的数字（默认过滤函数：字母数字字符）。
	 * 
	 * @param radix    数字的基数（如10为十进制，16为十六进制）
	 * @param unsigned 是否为无符号数字（无符号则不允许负号）
	 * @param source   待判断的字符序列
	 * @return 如果是有效的数字则返回true，否则返回false
	 */
	public static boolean isNumeric(int radix, boolean unsigned, CharSequence source) {
		return isNumeric(radix, unsigned, source,
				(c) -> (radix > 10 || radix <= 0) ? Character.isLetterOrDigit(c) : Character.isDigit(c));
	}

	/**
	 * 判断字符是否为数字符号（正号+或负号-）。
	 * 
	 * @param chr 待判断的字符
	 * @return 如果是+或-则返回true，否则返回false
	 */
	public static boolean isNumberSign(char chr) {
		return chr == '-' || chr == '+';
	}
}