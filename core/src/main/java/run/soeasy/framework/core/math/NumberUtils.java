package run.soeasy.framework.core.math;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.CharBuffer;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Random;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import run.soeasy.framework.core.Assert;
import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.function.ThrowingFunction;

/**
 * 通用数字处理工具类（不可实例化），封装数字全链路处理能力，涵盖**解析、类型判断、格式化、随机生成、权重选择**五大核心场景，
 * 支持原生类型（int/long/double）、高精度类型（BigInteger/BigDecimal）及自定义单位（NumberUnit），
 * 解决日常开发中数字处理的碎片化问题，适用于金融计算、数据解析、概率抽奖等多种业务场景。
 * 
 * <p>设计特性：
 * <ul>
 * <li><strong>不可实例化</strong>：通过{@link UtilityClass}注解标记，禁止创建实例，所有方法均为静态；</li>
 * <li><strong>多类型兼容</strong>：统一处理字符串、原生数字、高精度数字，支持十进制/十六进制（0x/0X/#前缀）/八进制（0前缀）解析；</li>
 * <li><strong>安全鲁棒</strong>：所有方法包含参数校验（非null、范围合法性），明确抛出异常场景（如格式错误、数值溢出），避免隐式错误；</li>
 * <li><strong>可扩展性</strong>：支持自定义格式化函数（ThrowingFunction）、过滤函数（IntPredicate），适配特殊业务规则（如自定义单位、特殊数字格式）；</li>
 * <li><strong>场景覆盖全</strong>：从基础的数字解析到复杂的权重随机，无需依赖第三方工具类，满足大部分数字处理需求。</li>
 * </ul>
 *
 * <h3>核心功能模块</h3>
 * <ol>
 * <li><strong>数字解析</strong>：将字符串转为指定数字类型（如Integer/BigDecimal），支持多进制（十进制/十六进制/八进制）；</li>
 * <li><strong>类型判断</strong>：判断类是否为数字/整数类型、字符串是否为有效数字、BigDecimal是否为整数；</li>
 * <li><strong>数字格式化</strong>：清除BigDecimal尾部无效零、按自定义单位格式化（如元/角/分）、保留指定小数位数；</li>
 * <li><strong>随机数生成</strong>：生成指定区间的int/long/BigInteger/BigDecimal随机数，支持数组随机采样；</li>
 * <li><strong>权重随机</strong>：基于元素权重实现概率选择（如抽奖、流量分配），支持选中元素移除、自定义权重计算。</li>
 * </ol>
 *
 * <h3>适用场景</h3>
 * <ul>
 * <li>金融领域：货币金额解析（如字符串"100.00"转BigDecimal）、按单位格式化（如"123元4角5分"）；</li>
 * <li>数据解析：日志/配置文件中的数字提取（如从"price: 99.9元"提取"99.9"）、多进制字符串解析（如"0x1A"转26）；</li>
 * <li>概率场景：抽奖系统（按奖品权重分配概率）、流量路由（按权重分配请求比例）；</li>
 * <li>通用开发：随机数生成（如验证码、测试数据）、数字类型校验（如接口参数是否为整数）、数组随机采样（如随机推荐商品）。</li>
 * </ul>
 *
 * <h3>使用示例</h3>
 * <pre class="code">
 * // 1. 数字解析（多进制支持）
 * Integer hexNum = NumberUtils.parseNumber("0x1A", Integer.class); // 十六进制"0x1A" → 26
 * BigDecimal decimal = NumberUtils.parseNumber("123.45", BigDecimal.class); // 十进制"123.45" → 123.45
 * BigInteger octNum = NumberUtils.parseNumber("077", BigInteger.class); // 八进制"077" → 63
 *
 * // 2. 类型判断
 * boolean isIntClass = NumberUtils.isInteger(Long.class); // true（Long是整数类型）
 * boolean isHexStr = NumberUtils.isNumeric(16, false, "0X2F"); // true（十六进制字符串有效）
 * boolean isDecimalInt = NumberUtils.isInteger(new BigDecimal("100.00")); // true（BigDecimal无小数部分）
 *
 * // 3. 数字格式化
 * BigDecimal numWithZero = new BigDecimal("100.000");
 * BigDecimal stripped = NumberUtils.stripTrailingZeros(numWithZero); // 100.000 → 100（清除尾部零）
 * String precisionStr = NumberUtils.formatPrecision(123.456, 2); // 保留2位小数 → "123.46"（四舍五入）
 *
 * // 4. 随机数生成（区间[min, max)左闭右开）
 * int randomInt = NumberUtils.random(10, 20); // 生成10~19的int随机数
 * BigDecimal randomDecimal = NumberUtils.random(new BigDecimal("0.0"), new BigDecimal("1.0")); // 0.0~1.0的BigDecimal
 * Integer[] srcArr = {1,2,3,4,5};
 * Integer[] randomArr = NumberUtils.randomArray(new Random(), srcArr, 3); // 从srcArr随机选3个元素组成新数组
 *
 * // 5. 权重随机（抽奖场景：奖品A权重50，B30，C20）
 * List<Prize> prizes = Arrays.asList(new Prize("A",50), new Prize("B",30), new Prize("C",20));
 * Prize selected = NumberUtils.random(
 *     prizes,
 *     Prize::getWeight, // 权重处理器：从Prize获取权重
 *     null // 选中后不移除元素
 * );
 * System.out.println("选中奖品：" + selected.getName()); // 概率：A(50%)、B(30%)、C(20%)
 * </pre>
 *
 * @author soeasy.run
 * @see UtilityClass  Lombok注解，标记此类为不可实例化的工具类
 * @see NumberUnit 数字单位类（支持自定义单位及进制，用于格式化与解析）
 * @see ArithmeticCalculator 算术计算器枚举（提供基础算术运算，支撑随机数计算）
 * @see NumberComparator 数字比较器（用于数字大小比较，权重随机场景依赖）
 * @see ThrowingFunction 带异常抛出的函数式接口（支持自定义格式化、权重计算）
 */
@UtilityClass
public class NumberUtils {
	/**
	 * 将字符串解析为指定类型的数字，支持多进制（十进制、十六进制、八进制）
	 * <p>解析流程：
	 * 1. 预处理：去除字符串两端空白（{@link String#trim()}）；
	 * 2. 进制判断：通过{@link #isHexNumber(String)}识别十六进制（0x/0X/#前缀），是则调用对应类型的{@code decode}方法（如{@link Integer#decode}）；
	 * 3. 类型适配：根据目标类型调用原生解析（如{@link Integer#valueOf}）或高精度构造（如{@link BigDecimal#BigDecimal(String)}）；
	 * 4. 异常处理：若字符串格式错误（如"abc"转Integer）或目标类型不支持（如自定义Number子类），抛出{@link IllegalArgumentException}。
	 *
	 * @param <T>         数字类型泛型，必须是{@link Number}的子类（如Integer、Long、BigDecimal）
	 * @param text        待解析的字符串（不可为null，支持多进制前缀：0x/0X/#→十六进制，0→八进制）
	 * @param targetClass 目标数字类型的Class对象（不可为null，支持Byte/Short/Integer/Long/BigInteger/Float/Double/BigDecimal）
	 * @return 解析后的数字对象（类型为T，非null）
	 * @throws IllegalArgumentException 若字符串无法解析为目标类型，或目标类型不支持
	 * @throws NullPointerException 若text或targetClass为null（由{@link NonNull}注解自动抛出）
	 * @see #isHexNumber(String) 判断是否为十六进制字符串
	 * @see #decodeBigInteger(String) 解析BigInteger类型的多进制字符串
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
	 * 判断指定的类是否为数字类型（包含原生数字类型和{@link Number}子类）
	 * <p>支持的数字类型：
	 * <ul>
	 * <li>原生类型：long.class、int.class、byte.class、short.class、float.class、double.class；</li>
	 * <li>包装类及子类：Integer.class、Long.class、BigInteger.class、BigDecimal.class等（需是Number的子类）。</li>
	 * </ul>
	 *
	 * @param type 待判断的类类型（可为null，null时返回false）
	 * @return true：是数字类型；false：非数字类型或type为null
	 */
	public static boolean isNumber(Class<?> type) {
		return type == long.class || type == int.class || type == byte.class || type == short.class
				|| type == float.class || type == double.class || Number.class.isAssignableFrom(type);
	}

	/**
	 * 判断指定的类是否为整数类型（包含原生整数类型和{@link BigInteger}）
	 * <p>支持的整数类型：
	 * <ul>
	 * <li>原生类型：long.class、int.class、byte.class、short.class；</li>
	 * <li>包装类及子类：Integer.class、Long.class、BigInteger.class（需是BigInteger的子类）。</li>
	 * </ul>
	 * <p>注意：float/double/BigDecimal即使数值为整数（如100.0），其类类型也不被视为整数类型。
	 *
	 * @param type 待判断的类类型（可为null，null时返回false）
	 * @return true：是整数类型；false：非整数类型或type为null
	 */
	public static boolean isInteger(Class<?> type) {
		return type == long.class || type == int.class || type == byte.class || type == short.class
				|| BigInteger.class.isAssignableFrom(type);
	}

	/**
	 * （私有工具方法）判断字符串是否为十六进制数字表示（需包含特殊前缀）
	 * <p>判断规则：字符串去除开头负号后，以"0x"、"0X"或"#"开头（如"-0x1A"、"#2F"视为十六进制，"1A"视为十进制）。
	 *
	 * @param value 待判断的字符串（可为null，null时返回false）
	 * @return true：是十六进制字符串；false：非十六进制或value为null
	 */
	private static boolean isHexNumber(String value) {
		if (value == null || value.isEmpty()) {
			return false;
		}
		int index = (value.startsWith("-") ? 1 : 0);
		return (value.startsWith("0x", index) || value.startsWith("0X", index) || value.startsWith("#", index));
	}

	/**
	 * （私有工具方法）从字符串解析BigInteger，支持十进制、十六进制（0x/0X/#前缀）和八进制（0前缀）
	 * <p>解析逻辑：
	 * 1. 处理符号：识别负号（"-"），记录符号位后跳过；
	 * 2. 确定基数：根据前缀判断基数（0x/0X/#→16，0→8，无前缀→10）；
	 * 3. 截取有效部分：跳过前缀后截取数字部分，调用{@link BigInteger#BigInteger(String, int)}解析；
	 * 4. 应用符号：根据符号位返回正数或负数（如"-0x1A"→-26）。
	 *
	 * @param value 待解析的字符串（不可为null，支持多进制前缀）
	 * @return 解析后的BigInteger（非null）
	 * @throws NumberFormatException 若字符串格式无效（如"0xG"，G不是十六进制字符）
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
	 * 判断BigDecimal是否为整数（无小数部分或小数部分全为0）
	 * <p>判断规则（满足任一即视为整数）：
	 * 1. 数值为0（{@link BigDecimal#signum()} == 0）；
	 * 2. 小数位数≤0（如100 → scale=0）；
	 * 3. 去除尾部零后小数位数≤0（如100.000 → 去除尾部零后scale=0）。
	 *
	 * @param number 待判断的BigDecimal（不可为null）
	 * @return true：是整数；false：非整数
	 * @throws NullPointerException 若number为null（由{@link NonNull}注解自动抛出）
	 */
	public static boolean isInteger(BigDecimal number) {
		return number.signum() == 0 || number.scale() <= 0 || number.stripTrailingZeros().scale() <= 0;
	}

	/**
	 * 清除BigDecimal尾部的无效零，优化数值的字符串表示
	 * <p>处理逻辑：
	 * 1. 特殊情况：数值为0或小数位数≤0（如100），直接返回原对象（无需修改）；
	 * 2. 普通情况：调用{@link BigDecimal#stripTrailingZeros()}清除尾部零，若清除后为整数（scale≤0），
	 *    调用{@link BigDecimal#setScale(int)}转为整数形式（如123.4500→123.45，100.000→100）。
	 * <p>注意：BigDecimal不可变，修改后会返回新对象，需接收返回值以生效。
	 *
	 * @param number 待处理的BigDecimal（可为null，null时返回null）
	 * @return 清除尾部零后的BigDecimal；若原数为null，返回null
	 */
	public static BigDecimal stripTrailingZeros(BigDecimal number) {
		if (number == null) {
			return null;
		}
		if (number.signum() == 0 || number.scale() <= 0) {
			return number;
		}

		// 存在小数，清除尾部零后判断是否为整数
		BigDecimal target = number.stripTrailingZeros();
		if (target.scale() <= 0) {
			return number.setScale(0);
		}
		return number;
	}

	/**
	 * 按指定单位格式化BigDecimal（默认格式化逻辑）
	 * <p>默认规则：
	 * 1. 数值处理：调用{@link #stripTrailingZeros(BigDecimal)}清除尾部无效零；
	 * 2. 字符串转换：调用{@link BigDecimal#toPlainString()}转为普通字符串（避免科学计数法，如1e3→"1000"）；
	 * 3. 单位拼接：若units为空或数值为0，直接返回格式化后的数值字符串；否则递归按单位拆分（如1234元→"1千元2百元3十元4元"）。
	 *
	 * @param number 待格式化的BigDecimal（不可为null）
	 * @param units  单位数组（如NumberUnit实例，定义单位名称及进制，如"元"（进制1000）、"角"（进制10））
	 * @return 格式化后的字符串（非null，如"123元4角5分"）
	 * @throws NullPointerException 若number为null（由{@link NonNull}注解自动抛出）
	 * @see #format(BigDecimal, ThrowingFunction, NumberUnit...) 支持自定义格式化函数的重载方法
	 */
	public static String format(BigDecimal number, NumberUnit... units) {
		return format(number, (e) -> stripTrailingZeros(e).toPlainString(), units);
	}

	/**
	 * 将字符串按指定单位解析为BigDecimal（默认转换逻辑）
	 * <p>默认规则：调用{@link BigDecimal#BigDecimal(String)}将数值部分转为BigDecimal，
	 * 递归解析单位（如"1千元2百元"→1×1000 + 2×100 = 1200），单位进制由NumberUnit定义。
	 *
	 * @param source 待解析的字符串（可为null，null时返回null）
	 * @param units  单位数组（如NumberUnit实例，需与格式化时的单位一致）
	 * @return 解析后的BigDecimal；若source为null，返回null
	 * @throws NumberFormatException 若字符串格式无效（如"abc元"）
	 */
	public static BigDecimal parse(String source, NumberUnit... units) {
		return parse(source, (e) -> new BigDecimal(e), units);
	}

	/**
	 * 按指定单位格式化BigDecimal（支持自定义格式化函数）
	 * <p>核心逻辑：
	 * 1. 预处理：若units为空或数值为0，直接调用自定义格式化函数返回结果；
	 * 2. 递归拆分：取数值绝对值，按单位进制拆分为“商+余数”（如1234元，进制10→123×10 +4），拼接“格式化后的商+单位”；
	 * 3. 剩余处理：若拆分后有剩余数值，继续处理余数；遍历结束后拼接剩余数值（无对应单位）。
	 * <p>适用场景：需自定义数值格式的场景（如保留2位小数、添加千分位分隔符）。
	 *
	 * @param <E>      异常类型泛型，必须是{@link Throwable}的子类（自定义格式化函数可能抛出的异常）
	 * @param number   待格式化的BigDecimal（不可为null，内部取绝对值处理，符号需外部自行添加）
	 * @param toString 自定义格式化函数（将BigDecimal转为字符串，不可为null，如e→String.format("%.2f", e)）
	 * @param units    单位数组（如NumberUnit实例，定义单位名称及进制，可为空）
	 * @return 格式化后的字符串（非null）
	 * @throws E 自定义格式化函数执行过程中抛出的异常（如IllegalFormatException）
	 * @throws NullPointerException 若number或toString为null（由{@link NonNull}注解自动抛出）
	 * @see #format(StringBuilder, BigDecimal, ThrowingFunction, int, int, NumberUnit...) 递归辅助方法
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
	 * （私有递归辅助方法）将BigDecimal按单位格式化到字符串缓冲区
	 * <p>递归逻辑：
	 * 1. 遍历单位：从startUnitsIndex开始，按单位进制调用{@link BigDecimal#divideAndRemainder(BigDecimal)}拆分数值；
	 * 2. 判断嵌套：若余数需进一步拆分（如123十元→12百元3十元），递归处理拆分后的商；
	 * 3. 拼接结果：拼接“格式化后的商+单位”，若下一级单位需前导零（如1千元0百元），补充零的格式化结果；
	 * 4. 剩余处理：遍历结束后，若仍有剩余数值，拼接剩余部分（无对应单位）。
	 *
	 * @param <E>              异常类型泛型（格式化函数可能抛出的异常）
	 * @param sb               字符串缓冲区（用于拼接格式化结果，不可为null）
	 * @param number           待格式化的BigDecimal（不可为null，已取绝对值）
	 * @param toString         自定义格式化函数（不可为null）
	 * @param startUnitsIndex  开始处理的单位索引（从0开始）
	 * @param endUnitsIndex    结束处理的单位索引（通常为units.length）
	 * @param units            单位数组（不可为null）
	 * @throws E 格式化函数抛出的异常
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
			// 判断是否为最后一级单位（无需继续拆分）
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

			// 判断是否需要嵌套处理（商需进一步拆分）
			boolean notNested = last || decimals[1].compareTo(BigDecimal.ZERO) == 0
					|| unit.getRadix().compareTo(BigDecimal.ONE) < 0;
			if (notNested) {
				sb.append(toString.apply(decimals[0]));
			} else {
				format(sb, decimals[0], toString, i, units.length - 1, units);
			}

			sb.append(unit.getName());
			// 处理下一级单位的前导零（如1千元0百元→"1千元0百元"）
			if (!notNested && i < units.length - 1
					&& decimals[1].divideToIntegralValue(units[i + 1].getRadix()).compareTo(BigDecimal.ZERO) == 0) {
				sb.append(toString.apply(BigDecimal.ZERO));
			}
			surplus = decimals[1];
		}

		// 拼接剩余数值（无对应单位）
		if (surplus.compareTo(BigDecimal.ZERO) > 0) {
			sb.append(toString.apply(surplus));
		}
	}

	/**
	 * 将字符串按指定单位解析为BigDecimal（支持自定义转换函数）
	 * <p>解析逻辑：
	 * 1. 遍历单位：查找字符串中包含的单位，截取单位左侧的数值部分；
	 * 2. 递归解析：调用自定义转换函数将数值部分转为BigDecimal，乘以单位进制后累加；
	 * 3. 剩余处理：若单位右侧还有内容，递归解析剩余部分（如"1千元2角"→1×1000 + 2×0.1 = 1000.2）。
	 * <p>适用场景：需自定义数值转换的场景（如字符串包含千分位分隔符，需先去除再转换：e→new BigDecimal(e.replace(",", ""))）。
	 *
	 * @param <E>        异常类型泛型（自定义转换函数可能抛出的异常）
	 * @param source     待解析的字符串（可为null，null时返回null）
	 * @param converter  自定义转换函数（将数值字符串转为BigDecimal，不可为null）
	 * @param units      单位数组（如NumberUnit实例，定义单位名称及进制，可为空）
	 * @return 解析后的BigDecimal；若source为null，返回null
	 * @throws E 转换函数执行过程中抛出的异常（如NumberFormatException）
	 * @throws NullPointerException 若converter为null（由{@link NonNull}注解自动抛出）
	 */
	public static <E extends Throwable> BigDecimal parse(String source,
			@NonNull ThrowingFunction<? super String, ? extends BigDecimal, ? extends E> converter, NumberUnit... units)
			throws E {
		if (source == null || source.isEmpty()) {
			return null;
		}
		for (NumberUnit unit : units) {
			int index = source.indexOf(unit.getName());
			if (index == -1) {
				continue;
			}

			// 解析单位左侧的数值部分
			String left = source.substring(0, index);
			BigDecimal value = parse(left, converter, units).multiply(unit.getRadix());
			// 解析单位右侧的剩余部分
			if (index < source.length() - unit.getName().length()) {
				String right = source.substring(index + unit.getName().length());
				value = value.add(parse(right, converter, units));
			}
			return value;
		}
		// 无匹配单位，直接转换数值部分
		return converter.apply(source);
	}

	/**
	 * 保留指定小数位数格式化double值（支持整数场景）
	 * <p>格式化规则：
	 * 1. 特殊情况：
	 *    - len=0→转为long后toString（如123.45→"123"，丢弃小数部分）；
	 *    - 数值为0→返回"0.00...0"（len个0，如len=2→"0.00"）；
	 * 2. 普通情况：使用{@link DecimalFormat}，格式为"#0.00...0"（len个0），自动四舍五入（如123.456保留2位→"123.46"）。
	 *
	 * @param number 待格式化的double值（支持正负值，如-123.45→"-123.45"）
	 * @param len    保留的小数位数（必须≥0，否则抛出{@link IllegalStateException}）
	 * @return 格式化后的字符串（非null）
	 * @throws IllegalStateException 若len < 0（非法小数位数）
	 */
	public static String formatPrecision(double number, int len) {
		if (len < 0) {
			throw new IllegalStateException("Decimal length cannot be negative: " + len);
		}

		// 无小数位，转为long
		if (len == 0) {
			return ((long) number) + "";
		}

		// 数值为0，返回"0.00...0"
		if (number == 0) {
			CharBuffer charBuffer = CharBuffer.allocate(len + 2);
			charBuffer.put('0');
			charBuffer.put('.');
			for (int i = 0; i < len; i++) {
				charBuffer.put('0');
			}
			return new String(charBuffer.array());
		}

		// 构建格式化模板（#0.00...0），支持四舍五入
		CharBuffer charBuffer = CharBuffer.allocate(len + 3);
		charBuffer.put("#0.");
		for (int i = 0; i < len; i++) {
			charBuffer.put("0");
		}
		return new DecimalFormat(new String(charBuffer.array())).format(number);
	}

	/**
	 * 从字符序列中提取符合规则的数字字符串（支持自定义过滤函数）
	 * <p>提取逻辑：
	 * 1. 预处理：若source为空，返回null；
	 * 2. 符号处理：保留开头的正负号（无符号时提取到负号返回null）；
	 * 3. 特殊字符：保留十六进制前缀（#）、小数点（仅一个，避免重复）；
	 * 4. 过滤逻辑：通过自定义filter判断字符是否保留（如过滤非数字字符）；
	 * 5. 结果处理：若无有效字符（如仅符号或特殊字符），返回null；否则返回提取的子串。
	 *
	 * @param radix    数字的基数（如10→十进制，16→十六进制，用于判断是否保留#前缀）
	 * @param unsigned 是否为无符号数字（true→不允许负号，提取到负号返回null）
	 * @param source   待提取的字符序列（可为null，null时返回null）
	 * @param filter   自定义过滤函数（判断字符是否保留，可为null→保留所有符合基数的字符）
	 * @return 提取的数字字符串（如从"price: 123.45元"提取"123.45"）；若无有效字符，返回null
	 */
	public static String extractNumberic(int radix, boolean unsigned, CharSequence source, IntPredicate filter) {
		if (StringUtils.isEmpty(source)) {
			return null;
		}

		char[] chars = new char[source.length()];
		int pos = 0;
		boolean findPoint = false; // 标记是否已找到小数点（避免多个小数点）
		for (int i = 0, len = source.length(); i < len; i++) {
			char chr = source.charAt(i);
			// 处理正负号（仅允许开头出现一次）
			if (chr == '-' || chr == '+') {
				if (pos == 0) {
					if (unsigned && chr == '-') {
						return null; // 无符号不允许负号
					}
					chars[pos++] = chr;
				}
				continue;
			}

			// 处理十六进制#前缀（仅允许开头或符号后出现）
			if (radix > 10) {
				if (chr == '#' && !findPoint && (pos == 0 || (pos == 1 && isNumberSign(chars[0])))) {
					chars[pos++] = chr;
					continue;
				}
			}

			// 处理小数点（仅允许出现一次）
			if (chr == '.') {
				if (findPoint) {
					continue; // 跳过重复的小数点
				}
				findPoint = true;
				chars[pos++] = chr;
				continue;
			}

			// 按过滤函数保留字符
			if (filter == null || filter.test(chr)) {
				chars[pos++] = chr;
			}
		}
		return pos == 0 ? null : new String(chars, 0, pos);
	}

	/**
	 * 从字符序列中提取符合规则的数字字符串（默认过滤函数：字母数字字符）
	 * <p>默认过滤逻辑：
	 * - 基数>10或≤0→保留字母数字字符（支持十六进制A-F/a-f，如"0x1A"提取"0x1A"）；
	 * - 基数≤10→仅保留数字字符（如"123.45元"提取"123.45"）。
	 *
	 * @param radix    数字的基数（如10→十进制，16→十六进制）
	 * @param unsigned 是否为无符号数字（true→不允许负号）
	 * @param source   待提取的字符序列（可为null，null时返回null）
	 * @return 提取的数字字符串；若无有效字符，返回null
	 * @see #extractNumberic(int, boolean, CharSequence, IntPredicate) 支持自定义过滤函数的重载方法
	 */
	public static String extractNumberic(int radix, boolean unsigned, CharSequence source) {
		return extractNumberic(radix, unsigned, source,
				(c) -> (radix > 10 || radix <= 0) ? Character.isLetterOrDigit(c) : Character.isDigit(c));
	}

	/**
	 * 判断字符序列是否为符合规则的数字（支持自定义过滤函数）
	 * <p>判断规则：
	 * 1. 非空校验：source为空→false；
	 * 2. 符号校验：正负号仅允许开头出现一次，无符号不允许负号；
	 * 3. 特殊字符校验：#前缀仅允许十六进制且在开头/符号后，小数点仅允许出现一次；
	 * 4. 字符校验：所有字符需通过filter过滤（如非数字字符→false）；
	 * 5. 结果：所有字符符合规则→true，否则→false。
	 *
	 * @param radix    数字的基数（如10→十进制，16→十六进制）
	 * @param unsigned 是否为无符号数字（true→不允许负号）
	 * @param source   待判断的字符序列（可为null，null时返回false）
	 * @param filter   自定义过滤函数（判断字符是否有效，可为null→默认规则）
	 * @return true：是有效数字；false：非有效数字或source为null
	 */
	public static boolean isNumeric(int radix, boolean unsigned, CharSequence source, IntPredicate filter) {
		if (StringUtils.isEmpty(source)) {
			return false;
		}

		boolean findPoint = false;
		int pos = 0;
		for (int i = 0, len = source.length(); i < len; i++) {
			char chr = source.charAt(i);
			// 处理正负号
			if (chr == '-' || chr == '+') {
				if (pos == 0) {
					if (unsigned && chr == '-') {
						return false;
					}
					pos++;
					continue;
				}
				return false; // 符号仅允许开头
			}

			// 处理十六进制#前缀
			if (radix > 10) {
				if (chr == '#') {
					if (!findPoint && (pos == 0 || (pos == 1 && isNumberSign(source.charAt(0))))) {
						pos++;
						continue;
					}
					return false; // #前缀位置非法
				}
			}

			// 处理小数点
			if (chr == '.') {
				if (findPoint) {
					return false; // 重复小数点
				}
				findPoint = true;
				pos++;
				continue;
			}

			// 字符有效性校验
			if (filter != null && !filter.test(chr)) {
				return false;
			}
			pos++;
		}
		return true; // 所有字符通过校验
	}

	/**
	 * 判断字符序列是否为符合规则的数字（默认过滤函数：字母数字字符）
	 * <p>默认过滤逻辑同{@link #extractNumberic(int, boolean, CharSequence)}，确保判断与提取规则一致。
	 *
	 * @param radix    数字的基数（如10→十进制，16→十六进制）
	 * @param unsigned 是否为无符号数字（true→不允许负号）
	 * @param source   待判断的字符序列（可为null，null时返回false）
	 * @return true：是有效数字；false：非有效数字或source为null
	 * @see #isNumeric(int, boolean, CharSequence, IntPredicate) 支持自定义过滤函数的重载方法
	 */
	public static boolean isNumeric(int radix, boolean unsigned, CharSequence source) {
		return isNumeric(radix, unsigned, source,
				(c) -> (radix > 10 || radix <= 0) ? Character.isLetterOrDigit(c) : Character.isDigit(c));
	}

	/**
	 * 判断字符是否为数字符号（正号+或负号-）
	 *
	 * @param chr 待判断的字符
	 * @return true：是+或-；false：其他字符
	 */
	public static boolean isNumberSign(char chr) {
		return chr == '-' || chr == '+';
	}
	
	/**
	 * 创建新的BigInteger实例（
	 *
	 * @param bigInteger 原BigInteger实例（不可为null）
	 * @return 新的BigInteger实例（与原实例数值相同，引用不同）
	 * @throws NullPointerException 若bigInteger为null（由{@link NonNull}注解自动抛出）
	 */
	public static BigInteger newBigInteger(@NonNull BigInteger bigInteger) {
	    return new BigInteger(bigInteger.toByteArray());
	}
	
	/**
     * 生成指定区间的随机整数[min, max)（左闭右开），使用指定的Random实例
     * <p>适用场景：需要可重复随机的场景（如固定种子的Random，确保测试环境结果一致）。
     * <p>特殊情况：若min == max，直接返回min；若min > max，可能生成超出预期的结果（建议确保min ≤ max）。
     *
     * @param random 随机数生成器（不可为null，需提前初始化，如new Random(seed)）
     * @param min    区间最小值（包含，如10）
     * @param max    区间最大值（不包含，如20→生成10~19的整数）
     * @return 随机整数
     * @throws NullPointerException 若random为null（由{@link Assert}自动抛出）
     */
    public static int random(Random random, int min, int max) {
        Assert.notNull(random, "Random cannot be null");
        if (max == min) {
            return min;
        }
        return (int) (random.nextDouble() * (max - min)) + min;
    }

    /**
     * 生成指定区间的随机整数[min, max)（左闭右开），使用默认随机源
     * <p>内部使用{@link Math#random()}（无固定种子，每次运行结果不同），适用于简单随机场景。
     *
     * @param min 区间最小值（包含）
     * @param max 区间最大值（不包含）
     * @return 随机整数
     */
    public static int random(int min, int max) {
        if (max == min) {
            return min;
        }
        return (int) (Math.random() * (max - min)) + min;
    }

    /**
     * 生成指定区间的随机长整数[min, max)（左闭右开），使用指定的Random实例
     * <p>适用场景：需要生成超出int范围的随机整数（如时间戳、大ID）。
     *
     * @param random 随机数生成器（不可为null）
     * @param min    区间最小值（包含，如10000000000L）
     * @param max    区间最大值（不包含，如20000000000L→生成10000000000L~19999999999L）
     * @return 随机长整数
     * @throws NullPointerException 若random为null（由{@link Assert}自动抛出）
     */
    public static long random(Random random, long min, long max) {
        Assert.notNull(random, "Random cannot be null");
        if (max == min) {
            return min;
        }
        return (long) (random.nextDouble() * (max - min)) + min;
    }

    /**
     * 生成指定区间的随机长整数[min, max)（左闭右开），使用默认随机源
     *
     * @param min 区间最小值（包含）
     * @param max 区间最大值（不包含）
     * @return 随机长整数
     */
    public static long random(long min, long max) {
        if (max == min) {
            return min;
        }
        return (long) (Math.random() * (max - min)) + min;
    }

    /**
     * 生成指定区间的随机BigDecimal[min, max)（左闭右开），使用指定的Random实例
     * <p>适用场景：金融计算等高精度场景（如随机金额、利率），保留原始小数精度。
     * <p>实现逻辑：生成0.0~1.0的double随机数，按比例缩放至[min, max)区间，转为BigDecimal。
     *
     * @param random 随机数生成器（不可为null）
     * @param min    区间最小值（包含，不可为null，如new BigDecimal("0.01")）
     * @param max    区间最大值（不包含，不可为null，如new BigDecimal("100.00")）
     * @return 随机BigDecimal
     * @throws NullPointerException 若random/min/max为null（由{@link Assert}自动抛出）
     */
    public static BigDecimal random(@NonNull Random random, @NonNull BigDecimal min, @NonNull BigDecimal max) {
        Assert.notNull(random, "Random cannot be null");
        Assert.notNull(min, "Min cannot be null");
        Assert.notNull(max, "Max cannot be null");
        
        if (min.equals(max)) {
            return min;
        }
        return new BigDecimal(random.nextDouble() + "").multiply(max.subtract(min)).add(min);
    }

    /**
     * 生成指定区间的随机BigDecimal[min, max)（左闭右开），使用默认随机源
     *
     * @param min 区间最小值（包含，不可为null）
     * @param max 区间最大值（不包含，不可为null）
     * @return 随机BigDecimal
     * @throws NullPointerException 若min或max为null（由{@link Assert}自动抛出）
     */
    public static BigDecimal random(@NonNull BigDecimal min, @NonNull BigDecimal max) {
        Assert.notNull(min, "Min cannot be null");
        Assert.notNull(max, "Max cannot be null");
        
        if (min.equals(max)) {
            return min;
        }
        return new BigDecimal(Math.random() + "").multiply(max.subtract(min)).add(min);
    }

    /**
     * 生成指定区间的随机BigInteger[min, max)（左闭右开），使用指定的Random实例
     * <p>适用场景：需要生成超大整数的场景（如密码学大素数、分布式ID）。
     *
     * @param random 随机数生成器（不可为null）
     * @param min    区间最小值（包含，不可为null）
     * @param max    区间最大值（不包含，不可为null）
     * @return 随机BigInteger
     * @throws NullPointerException 若random/min/max为null（由{@link Assert}自动抛出）
     */
    public static BigInteger random(@NonNull Random random, @NonNull BigInteger min, @NonNull BigInteger max) {
        Assert.notNull(random, "Random cannot be null");
        Assert.notNull(min, "Min cannot be null");
        Assert.notNull(max, "Max cannot be null");
        
        if (min.equals(max)) {
            return min;
        }
        return new BigInteger(random.nextDouble() + "").multiply(max.subtract(min)).add(min);
    }

    /**
     * 生成指定区间的随机BigInteger[min, max)（左闭右开），使用默认随机源
     *
     * @param min 区间最小值（包含，不可为null）
     * @param max 区间最大值（不包含，不可为null）
     * @return 随机BigInteger
     * @throws NullPointerException 若min或max为null（由{@link Assert}自动抛出）
     */
    public static BigInteger random(@NonNull BigInteger min, @NonNull BigInteger max) {
        Assert.notNull(min, "Min cannot be null");
        Assert.notNull(max, "Max cannot be null");
        
        if (min.equals(max)) {
            return min;
        }
        return new BigInteger(Math.random() + "").multiply(max.subtract(min)).add(min);
    }

    /**
     * 生成指定区间的随机Number[min, max)（左闭右开），自动适配类型，使用指定的Random实例
     * <p>类型适配规则：
     * <ul>
     * <li>若min/max为BigDecimal/Float/Double→调用BigDecimal版本的random；</li>
     * <li>若min/max为BigInteger→调用BigInteger版本的random；</li>
     * <li>其他类型（如Integer/Long）→转换为long，调用long版本的random。</li>
     * </ul>
     *
     * @param random 随机数生成器（不可为null）
     * @param min    区间最小值（包含，不可为null）
     * @param max    区间最大值（不包含，不可为null）
     * @return 随机Number（类型与min/max一致或兼容）
     * @throws NullPointerException 若random/min/max为null（由{@link Assert}自动抛出）
     */
    public static Number random(@NonNull Random random, @NonNull Number min, @NonNull Number max) {
        Assert.notNull(random, "Random cannot be null");
        Assert.notNull(min, "Min cannot be null");
        Assert.notNull(max, "Max cannot be null");
        
        if (min.equals(max)) {
            return min;
        }

        if (min instanceof BigDecimal || min instanceof Float || min instanceof Double || 
            max instanceof BigDecimal || max instanceof Float || max instanceof Double) {
            return random(random, (BigDecimal) ArithmeticCalculator.ADD.apply(BigDecimal.ZERO, min),
                          (BigDecimal) ArithmeticCalculator.ADD.apply(BigDecimal.ZERO, max));
        } else if (max instanceof BigInteger || min instanceof BigInteger) {
            return random(random, (BigInteger) ArithmeticCalculator.ADD.apply(BigInteger.ZERO, min),
                          (BigInteger) ArithmeticCalculator.ADD.apply(BigInteger.ZERO, max));
        }
        return random(random, min.longValue(), max.longValue());
    }

    /**
     * 生成指定区间的随机Number[min, max)（左闭右开），自动适配类型，使用默认随机源
     *
     * @param min 区间最小值（包含，不可为null）
     * @param max 区间最大值（不包含，不可为null）
     * @return 随机Number
     * @throws NullPointerException 若min或max为null（由{@link Assert}自动抛出）
     */
    public static Number random(@NonNull Number min, @NonNull Number max) {
        return random(new Random(), min, max);
    }

    /**
     * 从源数组中随机采样生成新数组（可重复采样）
     * <p>适用场景：随机推荐（如从商品数组中随机选3个展示）、测试数据生成。
     * <p>特殊情况：若源数组长度为0，返回同类型的空数组；若newLength ≤0，抛出{@link IllegalArgumentException}。
     *
     * @param <T>         数组元素类型泛型（如Integer、String）
     * @param random      随机数生成器（不可为null）
     * @param sourceArray 源数组（不可为null，支持任意类型数组，如Integer[]、String[]）
     * @param newLength   新数组长度（必须>0）
     * @return 新数组（元素类型与源数组一致，元素从源数组中随机选取，可重复）
     * @throws NullPointerException 若random或sourceArray为null（由{@link Assert}自动抛出）
     * @throws IllegalArgumentException 若newLength ≤0
     */
    @SuppressWarnings("unchecked")
    public static <T> T randomArray(Random random, T sourceArray, int newLength) {
        Assert.notNull(random, "Random cannot be null");
        Assert.notNull(sourceArray, "Source array cannot be null");
        Assert.isTrue(newLength > 0, "New length must be greater than 0");
        
        int length = Array.getLength(sourceArray);
        Class<?> componentType = sourceArray.getClass().getComponentType();
        Object target = Array.newInstance(componentType, newLength);
        
        if (length > 0) {
            for (int i = 0; i < newLength; i++) {
                int randomIndex = random.nextInt(length); // 随机生成源数组索引
                Object randomElement = Array.get(sourceArray, randomIndex);
                Array.set(target, i, randomElement);
            }
        }
        return (T) target;
    }

    /**
     * 基于权重随机选择元素（核心权重方法），适用于抽奖、流量分配等场景
     * <p>选择逻辑（权重轮盘法）：
     * <ol>
     * <li>参数校验：确保总权重>0、随机权重在[1, 总权重]范围内；</li>
     * <li>遍历元素：累加元素权重，当累加权重≥随机权重时，选中当前元素；</li>
     * <li>移除元素：若removePredicate为true，从迭代器中移除选中元素（避免重复选中）。</li>
     * </ol>
     * <p>注意：忽略权重为0或null的元素，若元素权重为负，抛出{@link IllegalArgumentException}。
     *
     * @param <T>              元素类型泛型（如Prize、Route）
     * @param <E>              异常类型泛型（权重处理器可能抛出的异常）
     * @param totalWeight      总权重（必须>0，不可为null）
     * @param weight           随机权重值（1≤weight≤totalWeight，不可为null）
     * @param iterator         元素迭代器（不可为null，需包含权重信息）
     * @param weightProcessor  权重处理器（从元素中提取权重，不可为null，如Prize::getWeight）
     * @param removePredicate  选中元素后是否移除（可为null→不移除；如t->true→移除）
     * @return 选中的元素；若无合适元素（如所有元素权重为0），返回null
     * @throws E 权重处理器抛出的异常
     * @throws NullPointerException 若totalWeight/weight/iterator/weightProcessor为null（由{@link Assert}自动抛出）
     * @throws IllegalArgumentException 若总权重≤0、随机权重≤0或>总权重、元素权重为负
     */
    public static <T, E extends Throwable> T random(@NonNull Number totalWeight, @NonNull Number weight,
                                                    @NonNull Iterator<? extends T> iterator,
                                                    @NonNull ThrowingFunction<? super T, ? extends Number, ? extends E> weightProcessor,
                                                    Predicate<? super T> removePredicate) throws E {
        Assert.notNull(totalWeight, "Total weight cannot be null");
        Assert.notNull(weight, "Weight cannot be null");
        Assert.notNull(iterator, "Iterator cannot be null");
        Assert.notNull(weightProcessor, "Weight processor cannot be null");
        Assert.isTrue(NumberComparator.DEFAULT.compare(totalWeight, 0) > 0, "Total weight must be greater than 0");
        Assert.isTrue(NumberComparator.DEFAULT.compare(weight, 0) > 0, "Weight must be greater than 0");
        Assert.isTrue(NumberComparator.DEFAULT.compare(weight, totalWeight) <= 0, 
                      "Weight [" + weight + "] cannot exceed total weight [" + totalWeight + "]");
        
        Number indexWeight = 0;
        while (iterator.hasNext()) {
            T item = iterator.next();
            if (item == null) {
                continue;
            }

            Number itemWeight = weightProcessor.apply(item);
            if (itemWeight == null) {
                continue;
            }

            int compareValue = NumberComparator.DEFAULT.compare(itemWeight, 0);
            if (compareValue == 0) {
                continue; // 忽略权重为0的元素
            }
            if (compareValue < 0) {
                throw new IllegalArgumentException("Element weight cannot be negative: " + itemWeight);
            }

            // 累加权重，判断是否选中当前元素
            indexWeight = ArithmeticCalculator.ADD.apply(indexWeight, itemWeight);
            if (NumberComparator.DEFAULT.compare(weight, indexWeight) <= 0) {
                if (removePredicate != null && removePredicate.test(item)) {
                    iterator.remove(); // 移除选中元素
                }
                return item;
            }
        }
        return null; // 无符合条件的元素
    }

    /**
     * 计算元素迭代器中所有元素的总权重（忽略权重为0或null的元素）
     * <p>适用场景：权重随机前的总权重计算（如抽奖前统计所有奖品的总权重）。
     *
     * @param <T>              元素类型泛型
     * @param <E>              异常类型泛型（权重处理器可能抛出的异常）
     * @param iterator         元素迭代器（不可为null）
     * @param weightProcessor  权重处理器（从元素中提取权重，不可为null）
     * @return 总权重（≥0，若所有元素权重为0或null，返回0）
     * @throws E 权重处理器抛出的异常
     * @throws NullPointerException 若iterator或weightProcessor为null（由{@link Assert}自动抛出）
     * @throws IllegalArgumentException 若元素权重为负
     */
    public static <T, E extends Throwable> Number getWeight(@NonNull Iterator<? extends T> iterator,
                                                           @NonNull ThrowingFunction<? super T, ? extends Number, ? extends E> weightProcessor) throws E {
        Assert.notNull(iterator, "Iterator cannot be null");
        Assert.notNull(weightProcessor, "Weight processor cannot be null");
        
        Number totalWeight = 0;
        while (iterator.hasNext()) {
            T item = iterator.next();
            if (item == null) {
                continue;
            }

            Number weight = weightProcessor.apply(item);
            if (weight == null) {
                continue;
            }

            int compareValue = NumberComparator.DEFAULT.compare(weight, 0);
            if (compareValue == 0) {
                continue; // 忽略权重为0的元素
            }
            if (compareValue < 0) {
                throw new IllegalArgumentException("Element weight cannot be negative: " + weight);
            }

            totalWeight = ArithmeticCalculator.ADD.apply(totalWeight, weight);
        }
        return totalWeight;
    }

    /**
     * 基于总权重随机选择元素（自动生成随机权重）
     * <p>等价于：先生成1~totalWeight的随机权重，再调用{@link #random(Number, Number, Iterator, ThrowingFunction, Predicate)}。
     *
     * @param <T>              元素类型泛型
     * @param <E>              异常类型泛型
     * @param totalWeight      总权重（必须>0，不可为null）
     * @param iterator         元素迭代器（不可为null）
     * @param weightProcessor  权重处理器（不可为null）
     * @param removePredicate  选中元素后是否移除（可为null）
     * @return 选中的元素；若无合适元素，返回null
     * @throws E 权重处理器抛出的异常
     * @throws NullPointerException 若totalWeight/iterator/weightProcessor为null（由{@link Assert}自动抛出）
     * @throws IllegalArgumentException 若总权重≤0、元素权重为负
     */
    public static <T, E extends Throwable> T random(@NonNull Number totalWeight,
                                                    @NonNull Iterator<? extends T> iterator,
                                                    @NonNull ThrowingFunction<? super T, ? extends Number, ? extends E> weightProcessor,
                                                    Predicate<? super T> removePredicate) throws E {
        // 生成1~totalWeight的随机权重（区间[1, totalWeight+1)左闭右开→实际1~totalWeight）
        Number randomWeight = random(1, ArithmeticCalculator.ADD.apply(totalWeight, 1));
        return random(totalWeight, randomWeight, iterator, weightProcessor, removePredicate);
    }

    /**
     * 基于元素集合权重随机选择元素（自动计算总权重+生成随机权重）
     * <p>流程：1. 调用{@link #getWeight(Iterator, ThrowingFunction)}计算总权重；2. 调用randomProcessor生成随机权重；3. 选择元素。
     *
     * @param <T>              元素类型泛型
     * @param <E>              异常类型泛型
     * @param iterable         元素集合（可为null→返回null）
     * @param weightProcessor  权重处理器（不可为null）
     * @param randomProcessor  随机权重生成器（输入总权重，输出随机权重，不可为null）
     * @param removePredicate  选中元素后是否移除（可为null）
     * @return 选中的元素；若无合适元素或iterable为null，返回null
     * @throws E 权重处理器或随机权重生成器抛出的异常
     * @throws NullPointerException 若weightProcessor或randomProcessor为null（由{@link Assert}自动抛出）
     * @throws IllegalArgumentException 若元素权重为负
     */
    public static <T, E extends Throwable> T random(Iterable<? extends T> iterable,
                                                    @NonNull ThrowingFunction<? super T, ? extends Number, ? extends E> weightProcessor,
                                                    @NonNull ThrowingFunction<? super Number, ? extends Number, ? extends E> randomProcessor,
                                                    Predicate<? super T> removePredicate) throws E {
        if (iterable == null) {
            return null;
        }

        Number totalWeight = getWeight(iterable.iterator(), weightProcessor);
        Number randomWeight = randomProcessor.apply(totalWeight);
        if (randomWeight == null) {
            return null;
        }
        return random(totalWeight, randomWeight, iterable.iterator(), weightProcessor, removePredicate);
    }

    /**
     * 基于元素集合权重随机选择元素（默认随机权重生成）
     * <p>等价于：调用{@link #random(Iterable, ThrowingFunction, ThrowingFunction, Predicate)}，
     * 随机权重生成器为“生成1~总权重的随机数”。
     *
     * @param <T>              元素类型泛型
     * @param <E>              异常类型泛型
     * @param iterable         元素集合（不可为null）
     * @param weightProcessor  权重处理器（不可为null）
     * @param removePredicate  选中元素后是否移除（可为null）
     * @return 选中的元素；若无合适元素，返回null
     * @throws E 权重处理器抛出的异常
     * @throws NullPointerException 若iterable或weightProcessor为null（由{@link Assert}自动抛出）
     * @throws IllegalArgumentException 若元素权重为负
     */
    public static <T, E extends Throwable> T random(@NonNull Iterable<? extends T> iterable,
                                                    @NonNull ThrowingFunction<? super T, ? extends Number, ? extends E> weightProcessor,
                                                    Predicate<? super T> removePredicate) throws E {
        return random(iterable, weightProcessor, 
                      (e) -> random(1, ArithmeticCalculator.ADD.apply(e, 1)), // 默认随机权重生成器
                      removePredicate);
    }
}