package run.soeasy.framework.core.domain;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import lombok.NonNull;
import run.soeasy.framework.core.NumberUtils;
import run.soeasy.framework.core.collection.Elements;

/**
 * 框架统一数值处理抽象基类，整合 Java 原生 {@link Number} 接口的类型转换能力与自定义 {@link Version}
 * 接口的版本比较能力，为整数、小数、大数等所有数值类型提供标准化的类型安全转换、数值判断及版本化支持，解决基础类型转换痛点。
 *
 * <h2>设计背景与目标</h2>
 * <p>
 * Java 基础数值类型存在三大核心痛点：
 * <ol>
 * <li>浮点数精度陷阱：{@code float/double}
 * 基于二进制浮点存储，存在固有精度限制（如有效数字位数上限），直接使用易出现隐性丢失；</li>
 * <li>数值范围限制：{@code long} 最大支持 {@code 9e18}，无法处理超大整数（如天文数据、密码学大素数）；</li>
 * <li>转换安全性低：不同类型（int/long/BigDecimal）转换无统一校验逻辑，易出现隐式溢出。</li>
 * </ol>
 * 本类基于 {@link NumberUtils}
 * 高精度防护能力，通过「契约优先+最小必要设计」，提供无隐性精度丢失的安全转换、便捷数值判断及版本号比较能力（支持
 * {@code 1.0.1}、{@code 20240501} 等格式）， 适用于对类型安全性、精度、版本化有要求的业务场景，同时保持代码简洁高效。
 *
 * <h2>核心特性</h2>
 * <ul>
 * <li><strong>多类型统一抽象</strong>：兼容基础整数（int）、高精度小数（BigDecimal）、超大整数（BigInteger），
 * 提供一致的调用接口，降低类型切换成本；</li>
 * <li><strong>零隐性精度丢失转换</strong>：基于 {@link NumberUtils} 实现
 * byte/short/int/long/float/double/char
 * 等基础类型转换，通过「范围校验+有效数字校验+反向兜底校验」三层防护，确保转换无隐性精度丢失， 超出限制（如有效数字超界、范围溢出）直接抛出
 * {@link ArithmeticException}，杜绝隐式问题；</li>
 * <li><strong>标准接口完整实现</strong>：全覆盖 {@link Number} 接口所有方法（新增
 * {@code shortValue()}/{@code byteValue()} 标准实现）， 委托对应 {@code getAsXxx()}
 * 方法，语义统一且符合 Java 原生契约；</li>
 * <li><strong>版本化比较能力</strong>：实现 {@link Version} 接口，可直接将数值作为版本号使用，通过
 * {@link #compareTo(Value)} 方法完成版本高低排序（如 {@code 2.1 > 2.0.1}）；</li>
 * <li><strong>预置高频常量</strong>：提供
 * {@link #NEGATIVE_ONE}（-1）、{@link #ZERO}（0）、{@link #ONE}（1）、
 * {@link #TEN}（10）等常用数值常量，避免重复创建对象，提升性能（命名遵循 Java 原生类库惯例）；</li>
 * <li><strong>便捷类型判断</strong>：内置
 * {@link #isZero()}（是否为0）、{@link #isPositive()}（是否正数）、
 * {@link #isNegative()}（是否负数）、{@link #isOne()}（是否为1）等方法，简化业务层数值判断逻辑。</li>
 * </ul>
 *
 * <h2>适用场景</h2>
 * <ul>
 * <li>金融领域：货币金额存储、汇率转换，需保证数值精度不丢失；</li>
 * <li>科学计算：工程测量、实验数据存储，需兼容超大数值或高精度小数；</li>
 * <li>数据校验：参数范围校验（如年龄、金额阈值），需明确抛出溢出异常而非隐式截断；</li>
 * <li>版本管理：API 版本、配置文件版本号比较（如 {@code v2.1.0} 与 {@code v2.0.3} 排序）；</li>
 * <li>大数据场景：超大整数存储（突破 long 范围限制），如用户 ID、订单号等。</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 *
 * <pre class="code">
 * // 1. 创建不同类型数值实例
 * NumberValue intNum = new IntValue(100); // 基础整数（int 范围）
 * NumberValue decimalNum = new BigDecimalValue("99.99"); // 高精度小数（无精度丢失）
 * NumberValue bigNum = new BigIntegerValue("12345678901234567890"); // 超 long 范围大数
 *
 * // 2. 便捷数值判断
 * if (intNum.isPositive() && !decimalNum.isZero()) {
 * 	System.out.println("intNum 是正数，decimalNum 非零");
 * }
 * if (bigNum.isNegative()) {
 * 	System.out.println("bigNum 是负数");
 * }
 *
 * // 3. 安全类型转换（超出范围/精度限制会抛出 ArithmeticException，无隐性精度丢失）
 * byte byteVal = intNum.getAsByte(); // 安全转 byte，也可使用 intNum.byteValue()（标准接口）
 * int intVal = decimalNum.getAsInt(); // 小数截断为 int（99.99 → 99，不四舍五入）
 * double doubleVal = bigNum.getAsDouble(); // 超大数转 double（经 NumberUtils 校验，无隐性精度丢失）
 * char charVal = new IntValue(65).getAsChar(); // 转 ASCII 字符（65→'A'）
 *
 * // 4. 版本号比较
 * NumberValue version1 = new BigDecimalValue("2.1.0");
 * NumberValue version2 = new BigDecimalValue("2.0.3");
 * if (version1.compareTo(version2) > 0) {
 * 	System.out.println("version1 高于 version2");
 * }
 *
 * // 5. 常量使用（遵循 Java 原生类库命名习惯）
 * NumberValue negativeOne = NumberValue.NEGATIVE_ONE; // -1 常量
 * NumberValue zero = NumberValue.ZERO; // 0 常量
 * </pre>
 *
 * <h2>注意事项</h2>
 * <ul>
 * <li>基础类型转换基于 {@link NumberUtils} 实现「范围校验+有效数字校验+反向兜底校验」，数值超出目标类型限制（如 float
 * 有效数字超7位）或无法精确表示时，均会抛出 {@link ArithmeticException}，无隐性精度丢失；</li>
 * <li>{@link #getAsBoolean()} 遵循"严格等于1为true"规则（非"非零即true"），与 Java
 * 基础类型自动拆箱逻辑不同；</li>
 * <li>小数转整数（如 {@link #getAsInt()}、{@link #getAsLong()}）采用
 * {@link RoundingMode#DOWN} 模式（直接截断小数部分，不四舍五入），此为业务规则定义，非精度丢失；</li>
 * <li>{@link #getAsChar()} 仅支持 0~127 范围的标准 ASCII 字符，超出范围会抛出异常；</li>
 * <li>子类需严格实现 {@link #hashCode()} 与 {@link #equals(Object)} 契约：equals 为 true
 * 的实例，hashCode 必须一致；</li>
 * <li>子类实现 {@link #getAsBigDecimal()} 时，需保证整数类型补0位小数（如
 * 100→100.0），确保小数位一致性；</li>
 * <li>常量命名规范：{@link #NEGATIVE_ONE} 与 Java 原生
 * {@link BigInteger#NEGATIVE_ONE}、{@link BigDecimal#NEGATIVE_ONE}
 * 保持一致，降低学习成本；</li>
 * <li>子类必须实现 {@link #getAsString()} 方法，确保数值字符串表示的统一性与可读性；</li>
 * <li>{@link Number}
 * 接口原生方法（{@code byteValue()}/{@code shortValue()}/{@code intValue()} 等）均为 final
 * 修饰，不可重写，确保转换逻辑一致性。</li>
 * </ul>
 *
 * @author soeasy.run
 * @see BigDecimalValue 高精度小数实现（基于 {@link BigDecimal}，支持任意小数位）
 * @see IntValue 基础整数实现（基于 int，适用于普通整数场景）
 * @see BigIntegerValue 超大整数实现（基于 {@link BigInteger}，突破 long 范围限制）
 * @see Version 版本比较接口（定义版本化能力规范）
 * @see Number Java 原生数值接口（定义基础类型转换契约）
 * @see NumberUtils 高精度数值处理工具类（核心依赖，提供零隐性精度丢失的转换与校验能力）
 */
public abstract class NumberValue extends Number implements Version {
	private static final long serialVersionUID = 1L;

	/**
	 * 数值 0 的常量实例（基于 {@link IntValue} 实现）
	 * <p>
	 * 适用于"无值标记""初始值""默认值"等场景的数值比较（如判断是否未赋值）
	 */
	public static final NumberValue ZERO = new IntValue(0);

	/**
	 * 数值 1 的常量实例（基于 {@link IntValue} 实现）
	 * <p>
	 * 适用于计数起始值、布尔值映射（1 表示 true）、默认步长等场景
	 */
	public static final NumberValue ONE = new IntValue(1);

	/**
	 * 数值 -1 的常量实例（基于 {@link IntValue} 实现）
	 * <p>
	 * 适用于负数标记、默认偏移量等场景，命名与 Java 原生 {@link BigInteger#NEGATIVE_ONE}、
	 * {@link BigDecimal#NEGATIVE_ONE} 保持一致，符合行业惯例，避免语义歧义
	 */
	public static final NumberValue NEGATIVE_ONE = new IntValue(-1);

	/**
	 * 数值 10 的常量实例（基于 {@link IntValue} 实现）
	 * <p>
	 * 适用于进制转换、十倍放大/缩小（如金额"分"转"元"）等场景
	 */
	public static final NumberValue TEN = new IntValue(10);

	/**
	 * 获取当前数值的绝对值
	 * <p>
	 * 逻辑说明：
	 * <ol>
	 * <li>若当前数值为负数（{@link #isNegative()} 返回 true），尝试通过 {@link #negate()} 取反；</li>
	 * <li>若取反溢出（如 {@link Integer#MIN_VALUE}），自动转为 {@link BigIntegerValue}
	 * 返回绝对值；</li>
	 * <li>若为非负数（0 或正数），直接返回自身（避免无意义运算，提升性能）。</li>
	 * </ol>
	 * 结果特性：返回值类型与原数值一致（无溢出时）或高精度类型（溢出时），确保绝对值始终有效且无精度丢失
	 *
	 * @return 绝对值对应的 {@link NumberValue} 实例（非 null）
	 * @throws ArithmeticException 若取反过程中发生溢出（如 int 最小值取反超出 int 范围）
	 */
	public NumberValue abs() throws ArithmeticException {
		if (!isNegative()) {
			return this;
		}
		try {
			return negate();
		} catch (ArithmeticException e) {
			BigInteger absBigInteger = getAsBigInteger().abs();
			return new BigIntegerValue(absBigInteger);
		}
	}

	/**
	 * 实现 {@link Version} 接口的比较方法，支持数值类型与其他 {@link Value} 类型的比较
	 * <p>
	 * 逻辑说明：
	 * <ol>
	 * <li>若目标对象是数值类型（{@link Value#isNumber()} 返回 true），通过 {@link BigDecimal}
	 * 进行高精度比较，无精度丢失；</li>
	 * <li>若目标对象是非数值类型，委托 {@link Version} 接口的默认实现处理。</li>
	 * </ol>
	 *
	 * @param other 待比较的 {@link Value} 对象（非 null）
	 * @return 比较结果：负数表示当前对象小于目标对象，0 表示相等，正数表示当前对象大于目标对象
	 */
	@Override
	public int compareTo(@NonNull Value other) {
		if (other.isNumber()) {
			NumberValue otherNumber = other.getAsNumber();
			return getAsBigDecimal().compareTo(otherNumber.getAsBigDecimal());
		}
		return Version.super.compareTo(other);
	}

	/**
	 * 对当前数值取反（正数→负数，负数→正数，0→0）
	 * <p>
	 * 子类需实现具体的取反逻辑，确保取反后数值精度不丢失，类型与原数值一致； 若取反导致溢出（如 {@link Integer#MIN_VALUE}
	 * 取反），需抛出 {@link ArithmeticException}
	 *
	 * @return 取反后的 {@link NumberValue} 实例（非 null，精度无丢失）
	 * @throws ArithmeticException 若取反导致数值溢出（如 int 最小值取反超出 int 范围）
	 */
	public abstract NumberValue negate() throws ArithmeticException;

	/**
	 * 重写 {@link Number} 接口方法，委托 {@link #getAsDouble()} 实现
	 * <p>
	 * 仅为满足接口契约，无额外逻辑；建议业务层直接使用 {@link #getAsDouble()}（语义更清晰，含三层校验，无隐性精度丢失）
	 *
	 * @return 当前数值的 double 类型表示
	 * @throws ArithmeticException 若数值低于 {@link Double#NEGATIVE_INFINITY} 或高于
	 *                             {@link Double#MAX_VALUE}，或无法精确表示
	 */
	@Override
	public final double doubleValue() throws ArithmeticException {
		return getAsDouble();
	}

	/**
	 * 重写 equals 方法，支持与 {@link NumberValue} 子类及 Java 原生 {@link Number} 子类比较
	 * <p>
	 * 比较规则（严格遵循 equals 契约，无精度丢失）：
	 * <ol>
	 * <li>若参数为当前对象本身，直接返回 true；</li>
	 * <li>若参数为 null 或非 {@link Number} 类型，返回 false；</li>
	 * <li>若参数为 {@link NumberValue} 子类实例，通过 {@link #compareTo(Value)} 判断数值是否相等（返回 0
	 * 则相等）；</li>
	 * <li>若参数为 Java 原生 {@link Number} 子类，转换为 {@link BigDecimal}
	 * 并忽略末尾零后比较，确保精度一致性。</li>
	 * </ol>
	 *
	 * @param obj 待比较的对象
	 * @return true：数值相等；false：对象为 null/非数值类型/数值不相等
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || !(obj instanceof Number)) {
			return false;
		}

		if (obj instanceof NumberValue) {
			return compareTo((NumberValue) obj) == 0;
		}

		BigDecimal thisBigDecimal = getAsBigDecimal();
		BigDecimal otherBigDecimal = NumberUtils.toBigDecimal((Number) obj);
		return thisBigDecimal.stripTrailingZeros().compareTo(otherBigDecimal.stripTrailingZeros()) == 0;
	}

	/**
	 * 基于数值内容计算哈希值，确保 equals 为 true 的实例哈希值一致
	 * <p>
	 * 实现逻辑：基于 {@link #getAsBigDecimal()} 忽略末尾零后的结果计算哈希值，保证数值相等时哈希值一致，无精度影响
	 *
	 * @return 基于数值内容的哈希值（int 类型）
	 */
	@Override
	public int hashCode() {
		return getAsBigDecimal().stripTrailingZeros().hashCode();
	}

	/**
	 * 重写 {@link Number} 接口方法，委托 {@link #getAsFloat()} 实现
	 * <p>
	 * 仅为满足接口契约，无额外逻辑；建议业务层直接使用 {@link #getAsFloat()}（语义更清晰，含三层校验，无隐性精度丢失）
	 *
	 * @return 当前数值的 float 类型表示
	 * @throws ArithmeticException 若数值低于 {@link Float#NEGATIVE_INFINITY} 或高于
	 *                             {@link Float#MAX_VALUE}，或无法精确表示
	 */
	@Override
	public final float floatValue() throws ArithmeticException {
		return getAsFloat();
	}

	/**
	 * 判断当前数值是否等于 0
	 * <p>
	 * 等价于 {@code this.compareTo(ZERO) == 0}，简化业务层判断逻辑，无精度丢失
	 *
	 * @return true：数值等于 0；false：数值不等于 0
	 */
	public boolean isZero() {
		return compareTo(ZERO) == 0;
	}

	/**
	 * 判断当前数值是否等于 1
	 * <p>
	 * 等价于 {@code this.compareTo(ONE) == 0}，适用于标记位、步长等场景的判断，无精度丢失
	 *
	 * @return true：数值等于 1；false：数值不等于 1
	 */
	public boolean isOne() {
		return compareTo(ONE) == 0;
	}

	/**
	 * 判断当前数值是否为正数（严格大于 0）
	 * <p>
	 * 等价于 {@code this.compareTo(ZERO) > 0}，不包含 0（0 既非正数也非负数），判断逻辑无精度丢失
	 *
	 * @return true：数值 > 0；false：数值 ≤ 0
	 */
	public boolean isPositive() {
		return compareTo(ZERO) > 0;
	}

	/**
	 * 判断当前数值是否为负数（严格小于 0）
	 * <p>
	 * 等价于 {@code this.compareTo(ZERO) < 0}，不包含 0（0 既非正数也非负数），判断逻辑无精度丢失
	 *
	 * @return true：数值 < 0；false：数值 ≥ 0
	 */
	public boolean isNegative() {
		return compareTo(ZERO) < 0;
	}

	/**
	 * 获取当前数值的 {@link BigDecimal} 表示（子类必须实现）
	 * <p>
	 * 要求：
	 * <ol>
	 * <li>整数类型需转为带 0 位小数的 {@link BigDecimal}（如 {@link IntValue}(100) → new
	 * BigDecimal("100.0")）；</li>
	 * <li>小数类型保留原始精度，不自动舍入或截断；</li>
	 * <li>返回实例非 null，且精度不丢失。</li>
	 * </ol>
	 * 用途：供高精度比较、小数场景适配等使用，确保精度不丢失
	 *
	 * @return 当前数值的 {@link BigDecimal} 实例（非 null，精度无丢失）
	 */
	@Override
	@NonNull
	public abstract BigDecimal getAsBigDecimal();

	/**
	 * 获取当前数值的 {@link BigInteger} 表示（子类必须实现）
	 * <p>
	 * 要求：
	 * <ol>
	 * <li>小数类型采用 {@link RoundingMode#DOWN} 模式（直接截断小数部分，不四舍五入），此为业务规则定义，非精度丢失；</li>
	 * <li>整数类型直接转为 {@link BigInteger}，无精度损失；</li>
	 * <li>返回实例非 null。</li>
	 * </ol>
	 * 用途：供整数场景适配、整数类型转换等使用
	 *
	 * @return 当前数值的 {@link BigInteger} 实例（非 null，小数部分已截断）
	 */
	@Override
	@NonNull
	public abstract BigInteger getAsBigInteger();

	/**
	 * 获取当前数值的布尔表示
	 * <p>
	 * 转换规则：仅当数值的 {@link BigInteger} 形式等于 {@link BigInteger#ONE}（即数值为 1）时返回 true，
	 * 其他情况（0、负数、非 1 的正数、小数）均返回 false
	 * <p>
	 * 注意：与 Java 基础类型"非零即 true"的自动拆箱逻辑不同，需结合业务场景使用（如"启用标记"判断）
	 *
	 * @return true：数值的 {@link BigInteger} 形式为 1；false：其他情况
	 */
	@Override
	public boolean getAsBoolean() {
		return getAsBigInteger().compareTo(BigInteger.ONE) == 0;
	}

	/**
	 * 安全转换为 byte 类型（范围：-128 ~ 127）
	 * <p>
	 * 基于 {@link NumberUtils#toByte(Number)} 实现，经范围校验，无溢出、无精度丢失
	 *
	 * @return 当前数值的 byte 类型表示
	 * @throws ArithmeticException 若数值低于 {@link Byte#MIN_VALUE}（-128）或高于
	 *                             {@link Byte#MAX_VALUE}（127）
	 */
	@Override
	public byte getAsByte() throws ArithmeticException {
		return NumberUtils.toByte(getAsBigInteger());
	}

	/**
	 * 安全转换为 char 类型（仅支持标准 ASCII 字符，范围：0 ~ 127）
	 * <p>
	 * 转换逻辑：
	 * <ol>
	 * <li>通过 {@link #getAsBigInteger()} 获取整数形式（小数截断）；</li>
	 * <li>若数值 < 0 或 > 127，抛出 {@link ArithmeticException}（超出标准 ASCII 范围）；</li>
	 * <li>否则将整数强制转为 char 类型（映射标准 ASCII 字符集），无精度丢失。</li>
	 * </ol>
	 * 适用场景：仅用于转换标准 ASCII 字符（如 65→'A'、97→'a'、10→'\n'）
	 *
	 * @return 当前数值对应的标准 ASCII 字符（范围：\u0000 ~ \u007F）
	 * @throws ArithmeticException 若数值低于 0 或高于 127（超出标准 ASCII 范围）
	 */
	@Override
	public char getAsChar() throws ArithmeticException {
		BigInteger number = getAsBigInteger();
		if (number.compareTo(BigInteger.ZERO) < 0 || number.compareTo(BigInteger.valueOf(127)) > 0) {
			throw new ArithmeticException("The value[" + number + "] is out of ASCII range (0~127)");
		}
		return (char) number.intValue();
	}

	/**
	 * 安全转换为 double 类型
	 * <p>
	 * 基于 {@link NumberUtils#toDouble(Number)} 实现，经「范围+有效数字+反向」三层校验，无隐性精度丢失
	 *
	 * @return 当前数值的 double 类型表示
	 * @throws ArithmeticException 若数值低于 {@link Double#NEGATIVE_INFINITY} 或高于
	 *                             {@link Double#MAX_VALUE}，或无法精确表示
	 */
	@Override
	public double getAsDouble() throws ArithmeticException {
		return NumberUtils.toDouble(getAsBigDecimal());
	}

	/**
	 * 获取包含当前数值的单元素集合
	 * <p>
	 * 实现 {@link Value} 接口契约，返回 {@link Elements} 单例集合（size=1），
	 * 便于业务层统一处理"单值/多值"场景（如批量迭代、统一存储）
	 *
	 * @return 包含当前实例的 {@link Elements} 集合（非 null，size=1）
	 */
	@Override
	public Elements<? extends Value> getAsElements() {
		return Elements.singleton(this);
	}

	/**
	 * 安全转换为 float 类型
	 * <p>
	 * 基于 {@link NumberUtils#toFloat(Number)} 实现，经「范围+有效数字+反向」三层校验，无隐性精度丢失
	 *
	 * @return 当前数值的 float 类型表示
	 * @throws ArithmeticException 若数值低于 {@link Float#NEGATIVE_INFINITY} 或高于
	 *                             {@link Float#MAX_VALUE}，或无法精确表示
	 */
	@Override
	public float getAsFloat() throws ArithmeticException {
		return NumberUtils.toFloat(getAsBigDecimal());
	}

	/**
	 * 安全转换为 int 类型（范围：-2147483648 ~ 2147483647）
	 * <p>
	 * 基于 {@link NumberUtils#toInteger(Number)} 实现，经范围校验，无溢出、无精度丢失（小数部分已截断）
	 *
	 * @return 当前数值的 int 类型表示
	 * @throws ArithmeticException 若数值低于 {@link Integer#MIN_VALUE} 或高于
	 *                             {@link Integer#MAX_VALUE}
	 */
	@Override
	public int getAsInt() throws ArithmeticException {
		return NumberUtils.toInteger(getAsBigInteger());
	}

	/**
	 * 安全转换为 long 类型（范围：-9223372036854775808 ~ 9223372036854775807）
	 * <p>
	 * 基于 {@link NumberUtils#toLong(Number)} 实现，经范围校验，无溢出、无精度丢失（小数部分已截断）
	 *
	 * @return 当前数值的 long 类型表示
	 * @throws ArithmeticException 若数值低于 {@link Long#MIN_VALUE} 或高于
	 *                             {@link Long#MAX_VALUE}
	 */
	@Override
	public long getAsLong() throws ArithmeticException {
		return NumberUtils.toLong(getAsBigInteger());
	}

	/**
	 * 获取当前数值自身（实现 {@link Value} 接口契约）
	 * <p>
	 * 语义：明确当前 {@link Value} 为数值类型，直接返回自身，避免业务层额外类型转换
	 *
	 * @return 当前 {@link NumberValue} 实例（非 null）
	 */
	@Override
	public NumberValue getAsNumber() {
		return this;
	}

	/**
	 * 安全转换为 short 类型（范围：-32768 ~ 32767）
	 * <p>
	 * 基于 {@link NumberUtils#toShort(Number)} 实现，经范围校验，无溢出、无精度丢失（小数部分已截断）
	 *
	 * @return 当前数值的 short 类型表示
	 * @throws ArithmeticException 若数值低于 {@link Short#MIN_VALUE} 或高于
	 *                             {@link Short#MAX_VALUE}
	 */
	@Override
	public short getAsShort() throws ArithmeticException {
		return NumberUtils.toShort(getAsBigInteger());
	}

	/**
	 * 重写 {@link Number} 接口方法，委托 {@link #getAsInt()} 实现
	 * <p>
	 * 仅为满足接口契约，无额外逻辑；建议业务层直接使用 {@link #getAsInt()}（语义更清晰，含范围校验，无精度丢失）
	 *
	 * @return 当前数值的 int 类型表示
	 * @throws ArithmeticException 若数值低于 {@link Integer#MIN_VALUE}（-2147483648）或高于
	 *                             {@link Integer#MAX_VALUE}（2147483647）
	 */
	@Override
	public final int intValue() throws ArithmeticException {
		return getAsInt();
	}

	/**
	 * 重写 {@link Number} 接口方法，委托 {@link #getAsShort()} 实现
	 * <p>
	 * 仅为满足接口契约，无额外逻辑；语义清晰，含范围校验，无精度丢失
	 *
	 * @return 当前数值的 short 类型表示
	 * @throws ArithmeticException 若数值低于 {@link Short#MIN_VALUE}（-32768）或高于
	 *                             {@link Short#MAX_VALUE}（32767）
	 */
	@Override
	public final short shortValue() {
		return getAsShort();
	}

	/**
	 * 重写 {@link Number} 接口方法，委托 {@link #getAsByte()} 实现
	 * <p>
	 * 仅为满足接口契约，无额外逻辑；语义清晰，含范围校验，无精度丢失
	 *
	 * @return 当前数值的 byte 类型表示
	 * @throws ArithmeticException 若数值低于 {@link Byte#MIN_VALUE}（-128）或高于
	 *                             {@link Byte#MAX_VALUE}（127）
	 */
	@Override
	public final byte byteValue() {
		return getAsByte();
	}

	/**
	 * 标记当前是否为多值类型（实现 {@link Value} 接口契约）
	 * <p>
	 * 数值类型始终为单值（一个实例对应一个具体数值），故固定返回 false，且不可重写（避免子类破坏语义）
	 *
	 * @return false（固定值，数值类型无多值场景）
	 */
	@Override
	public final boolean isMultiple() {
		return false;
	}

	/**
	 * 标记当前是否为数值类型（实现 {@link Value} 接口契约）
	 * <p>
	 * 当前类为数值抽象基类，所有子类均为数值类型，故固定返回 true（子类可直接继承，无需重写）
	 *
	 * @return true（固定值，当前实例为数值类型）
	 */
	@Override
	public boolean isNumber() {
		return true;
	}

	/**
	 * 重写 {@link Number} 接口方法，委托 {@link #getAsLong()} 实现
	 * <p>
	 * 仅为满足接口契约，无额外逻辑；建议业务层直接使用 {@link #getAsLong()}（语义更清晰，含范围校验，无精度丢失）
	 *
	 * @return 当前数值的 long 类型表示
	 * @throws ArithmeticException 若数值低于
	 *                             {@link Long#MIN_VALUE}（-9223372036854775808）或高于
	 *                             {@link Long#MAX_VALUE}（9223372036854775807）
	 */
	@Override
	public final long longValue() throws ArithmeticException {
		return getAsLong();
	}

	/**
	 * 重写 toString 方法，委托 {@link #getAsString()} 实现
	 * <p>
	 * 仅为满足 Object 接口契约，无额外逻辑；建议业务层直接使用 {@link #getAsString()}（语义更清晰，格式统一）
	 *
	 * @return 当前数值的字符串表示（如"100"、"99.99"、"12345678901234567890"）
	 */
	@Override
	public String toString() {
		return getAsString();
	}
}