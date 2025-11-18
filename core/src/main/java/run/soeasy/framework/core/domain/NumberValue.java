package run.soeasy.framework.core.domain;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import lombok.NonNull;
import run.soeasy.framework.core.NumberUtils;
import run.soeasy.framework.core.collection.Elements;

/**
 * 框架统一数值处理抽象基类，整合 Java 原生 {@link Number} 接口的类型转换能力与自定义 {@link Version} 接口的版本比较能力，
 * 为所有数值类型（基础整数、高精度小数、超大整数）提供「安全转换+精度保障+版本化支持」的标准化解决方案，彻底解决原生数值类型的精度丢失、范围限制、转换不安全等核心痛点。
 *
 * <h2>设计背景与核心目标</h2>
 * <p>
 * Java 原生数值类型存在三大不可忽视的痛点，直接影响业务稳定性与数据准确性：
 * <ol>
 * <li><strong>精度丢失陷阱</strong>：float/double 基于二进制浮点存储，存在固有精度限制（如 0.1+0.2≠0.3），金融、科学计算等场景下易引发隐性问题；</li>
 * <li><strong>范围限制瓶颈</strong>：long 最大仅支持 9e18，无法处理超大整数（如天文数据、密码学大素数、超大规模订单号）；</li>
 * <li><strong>转换安全性低</strong>：不同类型（int/long/BigDecimal）转换无统一校验逻辑，隐式溢出、精度截断等问题难以察觉。</li>
 * </ol>
 * 本类基于 {@link NumberUtils} 的高精度防护能力，以「契约优先+最小必要设计」为原则，实现三大核心目标：
 * <ol>
 * <li>零隐性精度丢失：所有类型转换均经「范围校验+有效数字校验+反向兜底校验」三层防护，超出限制直接抛异常，杜绝隐式问题；</li>
 * <li>多类型统一接口：兼容基础整数、高精度小数、超大整数，提供一致的调用体验，降低类型切换成本；</li>
 * <li>功能按需扩展：整合数值判断、版本比较、常量复用等高频功能，适配多场景业务需求，同时保持代码简洁高效。</li>
 * </ol>
 *
 * <h2>核心特性</h2>
 * <ul>
 * <li><strong>多类型统一抽象</strong>：兼容 int/long 等基础类型、BigDecimal 高精度小数、BigInteger 超大整数，
 * 所有数值类型共用一套接口，无需关注底层实现，降低开发复杂度；</li>
 * <li><strong>安全无损转换机制</strong>：支持 byte/short/int/long/float/double/char 等基础类型转换，
 * 基于 {@link NumberUtils} 实现三层校验，确保转换无隐性精度丢失，超出限制（溢出、有效数字超界）直接抛 {@link ArithmeticException}；</li>
 * <li><strong>Java 原生契约兼容</strong>：全覆盖 {@link Number} 接口所有方法（补充 shortValue()/byteValue() 标准实现），
 * 委托对应 {@code getAsXxx()} 方法，语义与原生类库一致，无学习成本；</li>
 * <li><strong>原生版本化比较能力</strong>：实现 {@link Version} 接口，可直接将数值作为版本号使用，
 * 通过 {@link #compareTo(Value)} 完成版本排序（如 2.1 &gt; 2.0.1、20240501 &gt; 20240430），无需额外封装；</li>
 * <li><strong>高频常量预置优化</strong>：提供 {@link #NEGATIVE_ONE}（-1）、{@link #ZERO}（0）、{@link #ONE}（1）、{@link #TEN}（10）等常用常量，
 * 遵循 Java 原生类库命名惯例，避免重复创建对象，提升性能与代码可读性；</li>
 * <li><strong>便捷数值判断工具</strong>：内置 {@link #isZero()}（是否为0）、{@link #isPositive()}（是否正数）、
 * {@link #isNegative()}（是否负数）、{@link #isOne()}（是否为1）等方法，简化业务层数值校验逻辑，减少重复代码；</li>
 * <li><strong>安全绝对值计算</strong>：{@link #abs()} 方法自动处理溢出场景（如 Integer.MIN_VALUE 取反），
 * 溢出时自动升级为 BigIntegerValue，确保绝对值计算无精度丢失；</li>
 * <li><strong>严格 equals-hashCode 契约</strong>：支持与 NumberValue 子类及 Java 原生 Number 子类比较，
 * 基于 BigDecimal 忽略末尾零后比较，确保数值相等则 equals 为 true、hashCode 一致，可安全用于哈希容器。</li>
 * </ul>
 *
 * <h2>适用场景</h2>
 * <ul>
 * <li>金融财务场景：货币金额存储、汇率转换、税费计算，需严格保证数值精度不丢失；</li>
 * <li>科学工程计算：工程测量数据、实验结果存储，需兼容超大数值或高精度小数；</li>
 * <li>数据校验场景：参数范围校验（如年龄、金额阈值、订单号合法性），需明确抛出溢出异常而非隐式截断；</li>
 * <li>版本管理场景：API 版本、配置文件版本、软件版本号比较排序（如 v2.1.0 与 v2.0.3 对比）；</li>
 * <li>大数据场景：超 long 范围的超大整数存储（如分布式 ID、超大规模用户量统计），突破原生类型范围限制。</li>
 * </ul>
 *
 * <h2>关键注意事项</h2>
 * <ul>
 * <li><strong>转换校验规则</strong>：所有类型转换均经「范围校验+有效数字校验+反向校验」，
 * 如 float 仅支持 6-7 位有效数字、double 支持 15-17 位有效数字，超出则抛异常，杜绝隐性精度丢失；</li>
 * <li><strong>布尔转换规则</strong>：{@link #getAsBoolean()} 仅当数值为 1（含 1.0、1.00 等）时返回 true，
 * 与 Java 原生「非零即 true」逻辑不同，适用于标记位、状态位等场景，需结合业务使用；</li>
 * <li><strong>小数转整数规则</strong>：小数类型转整数（getAsInt()/getAsLong()）采用 {@link RoundingMode#DOWN} 模式，
 * 直接截断小数部分（如 99.99 → 99），非四舍五入，此为明确业务规则，需提前知晓；</li>
 * <li><strong>字符转换限制</strong>：{@link #getAsChar()} 仅支持 0~127 范围的标准 ASCII 字符，
 * 超出则抛异常，不支持扩展 ASCII 或 Unicode 字符（如中文、特殊符号）；</li>
 * <li><strong>子类实现约束</strong>：
 *   <ol>
 *   <li>必须实现 getAsBigDecimal()：整数类型需转为 0 位小数格式（如 100 → 100.0），小数类型保留原始精度；</li>
 *   <li>必须实现 getAsBigInteger()：小数部分截断（RoundingMode.DOWN），整数类型完整转换；</li>
 *   <li>必须实现 getAsString()：避免科学计数法，整数无小数位、小数保留原始位数、超大数完整输出；</li>
 *   <li>必须实现 negate()：取反后精度不丢失，溢出需抛异常（不允许隐式类型升级）；</li>
 *   <li>建议设计为不可变类：基于 BigDecimal/BigInteger 的不可变性，确保线程安全，避免并发问题。</li>
 *   </ol>
 * </li>
 * <li><strong>原生接口方法限制</strong>：Number 接口的 byteValue()/shortValue()/intValue() 等方法均为 final 修饰，
 * 不可重写，确保转换逻辑一致性，避免子类破坏安全转换契约；</li>
 * <li><strong>常量使用规范</strong>：预置常量（ZERO/ONE 等）基于 IntValue 实现，适用于 int 范围场景，
 * 若需高精度场景（如 BigDecimal 类型的零），需自行创建对应子类实例，避免类型转换开销。</li>
 * </ul>
 *
 * @author soeasy.run
 * @see BigDecimalValue 高精度小数实现类（基于 BigDecimal，支持任意小数位，无精度丢失）
 * @see IntValue 基础整数实现类（基于 int，适用于普通整数场景，轻量高效）
 * @see BigIntegerValue 超大整数实现类（基于 BigInteger，突破 long 范围限制，支持无限精度整数）
 * @see Version 版本比较接口（定义版本化能力规范，支持版本号分段比较）
 * @see Number Java 原生数值接口（定义基础类型转换契约，确保接口兼容性）
 * @see NumberUtils 高精度数值处理工具类（核心依赖，提供安全转换与校验能力）
 * @see Elements 框架集合工具类（支持单值集合封装，适配多值统一处理场景）
 */
public abstract class NumberValue extends Number implements Version {
	private static final long serialVersionUID = 1L;

	/**
	 * 数值 0 的常量实例（基于 {@link IntValue} 实现）
	 * <p>适用于"无值标记""初始值""默认值"等场景（如判断是否未赋值、金额默认值），命名遵循 Java 原生类库惯例
	 */
	public static final NumberValue ZERO = new IntValue(0);

	/**
	 * 数值 1 的常量实例（基于 {@link IntValue} 实现）
	 * <p>适用于计数起始值、布尔值映射（1 表示 true）、默认步长等场景，语义清晰无歧义
	 */
	public static final NumberValue ONE = new IntValue(1);

	/**
	 * 数值 -1 的常量实例（基于 {@link IntValue} 实现）
	 * <p>适用于负数标记、默认偏移量等场景
	 */
	public static final NumberValue NEGATIVE_ONE = new IntValue(-1);

	/**
	 * 数值 10 的常量实例（基于 {@link IntValue} 实现）
	 * <p>适用于进制转换、单位换算（如金额"分"转"元"）、十倍放大/缩小等场景
	 */
	public static final NumberValue TEN = new IntValue(10);

	/**
	 * 获取当前数值的绝对值（自动处理溢出场景，无精度丢失）
	 * <p>
	 * 核心逻辑：
	 * <ol>
	 * <li>非负数（0 或正数）：直接返回自身，避免无意义运算，提升性能；</li>
	 * <li>负数：尝试通过 {@link #negate()} 取反，若取反无溢出，返回取反结果（与原类型一致）；</li>
	 * <li>溢出场景（如 Integer.MIN_VALUE 取反超出 int 范围）：自动转为 {@link BigIntegerValue} 计算绝对值，确保精度不丢失。</li>
	 * </ol>
	 * 结果特性：返回值类型与原数值一致（无溢出）或高精度类型（溢出），绝对值始终有效且无精度损耗
	 *
	 * @return 绝对值对应的 {@link NumberValue} 实例（非 null）
	 * @throws ArithmeticException 若取反过程中发生溢出（子类未处理且未触发 BigInteger 升级场景）
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
	 * 实现 {@link Version} 接口的比较方法，支持数值与其他 {@link Value} 类型的高精度比较
	 * <p>
	 * 比较规则：
	 * <ol>
	 * <li>目标为数值类型（{@link Value#isNumber()} 为 true）：通过 {@link BigDecimal} 进行高精度比较，
	 * 避免数值比较时的精度丢失（如 2.1 与 2.01 精准对比）；</li>
	 * <li>目标为非数值类型（如字符串版本号 "v2.1"）：委托 {@link Version} 接口默认实现，按版本号分段比较（点分隔，缺省位补0）。</li>
	 * </ol>
	 *
	 * @param other 待比较的 {@link Value} 对象（非 null）
	 * @return 比较结果：负数=当前对象小于目标对象，0=相等，正数=当前对象大于目标对象
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
	 * 子类实现约束（必须遵守，确保语义一致性）：
	 * <ol>
	 * <li>精度保障：取反后数值精度不丢失，类型与原数值一致（如 IntValue 取反后仍为 IntValue）；</li>
	 * <li>溢出处理：若取反导致溢出（如 Integer.MIN_VALUE 取反），必须抛出 {@link ArithmeticException}，
	 * 不允许隐式转换为高精度类型（溢出场景由 {@link #abs()} 统一处理）；</li>
	 * <li>性能优化：0 取反后仍为 0，返回自身实例，避免无意义对象创建。</li>
	 * </ol>
	 *
	 * @return 取反后的 {@link NumberValue} 实例（非 null，精度无丢失）
	 * @throws ArithmeticException 若取反导致数值溢出（如 int 最小值取反超出 int 范围）
	 */
	public abstract NumberValue negate() throws ArithmeticException;

	/**
	 * 重写 {@link Number} 接口方法，委托 {@link #getAsDouble()} 实现
	 * <p>仅为满足接口契约，无额外逻辑；建议业务层直接使用 {@link #getAsDouble()}（语义更清晰，含三层校验）
	 *
	 * @return 当前数值的 double 类型表示
	 * @throws ArithmeticException 若数值超出 double 范围，或有效数字超 15-17 位（无法精确表示）
	 */
	@Override
	public final double doubleValue() throws ArithmeticException {
		return getAsDouble();
	}

	/**
	 * 重写 equals 方法，支持跨类型数值相等判定（严格遵循 equals-hashCode 契约）
	 * <p>
	 * 比较规则（优先级从高到低）：
	 * <ol>
	 * <li>引用相等：当前对象与参数为同一实例，直接返回 true；</li>
	 * <li>无效对象：参数为 null 或非 {@link Number} 类型，返回 false；</li>
	 * <li>NumberValue 子类：通过 {@link #compareTo(Value)} 判断数值是否相等（返回 0 则相等）；</li>
	 * <li>原生 Number 子类（如 Integer、Float、DoubleAdder）：先通过 {@link NumberUtils#toBigDecimal(Number)} 转为 BigDecimal，
	 * 忽略末尾零后比较（如 100 与 100.0 相等），确保精度一致性。</li>
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
	 * <p>实现逻辑：基于 {@link #getAsBigDecimal()} 忽略末尾零后的结果计算哈希值，
	 * 保证数值相等时（如 100 与 100.0）哈希值一致，可安全用于 HashMap、HashSet 等哈希容器
	 *
	 * @return 基于数值内容的哈希值（int 类型）
	 */
	@Override
	public int hashCode() {
		return getAsBigDecimal().stripTrailingZeros().hashCode();
	}

	/**
	 * 重写 {@link Number} 接口方法，委托 {@link #getAsFloat()} 实现
	 * <p>仅为满足接口契约，无额外逻辑；建议业务层直接使用 {@link #getAsFloat()}（语义更清晰，含三层校验）
	 *
	 * @return 当前数值的 float 类型表示
	 * @throws ArithmeticException 若数值超出 float 范围，或有效数字超 6-7 位（无法精确表示）
	 */
	@Override
	public final float floatValue() throws ArithmeticException {
		return getAsFloat();
	}

	/**
	 * 判断当前数值是否等于 0（无精度丢失）
	 * <p>等价于 {@code this.compareTo(ZERO) == 0}，简化业务层判断逻辑，避免重复代码
	 *
	 * @return true：数值等于 0；false：数值不等于 0
	 */
	public boolean isZero() {
		return compareTo(ZERO) == 0;
	}

	/**
	 * 判断当前数值是否等于 1（无精度丢失）
	 * <p>等价于 {@code this.compareTo(ONE) == 0}，适用于标记位、步长等场景的快速判断
	 *
	 * @return true：数值等于 1；false：数值不等于 1
	 */
	public boolean isOne() {
		return compareTo(ONE) == 0;
	}

	/**
	 * 判断当前数值是否为正数（严格大于 0）
	 * <p>等价于 {@code this.compareTo(ZERO) > 0}，不包含 0（0 既非正数也非负数），判断逻辑无精度丢失
	 *
	 * @return true：数值 &gt; 0；false：数值 ≤ 0
	 */
	public boolean isPositive() {
		return compareTo(ZERO) > 0;
	}

	/**
	 * 判断当前数值是否为负数（严格小于 0）
	 * <p>等价于 {@code this.compareTo(ZERO) < 0}，不包含 0（0 既非正数也非负数），判断逻辑无精度丢失
	 *
	 * @return true：数值 &lt; 0；false：数值 ≥ 0
	 */
	public boolean isNegative() {
		return compareTo(ZERO) < 0;
	}

	/**
	 * 获取当前数值的 {@link BigDecimal} 表示（子类必须实现，核心抽象方法）
	 * <p>
	 * 子类实现约束（确保多类型一致性）：
	 * <ol>
	 * <li>整数类型：转为带 0 位小数的 BigDecimal（如 IntValue(100) → new BigDecimal("100.0")）；</li>
	 * <li>小数类型：保留原始精度，不自动舍入或截断（如 BigDecimalValue("99.999") 需返回原值）；</li>
	 * <li>格式要求：返回实例非 null、不可变，避免科学计数法（如 1000 不可表示为 1E+3）；</li>
	 * <li>用途：供高精度比较、小数场景适配、跨类型转换等核心功能使用。</li>
	 * </ol>
	 *
	 * @return 当前数值的 {@link BigDecimal} 实例（非 null，精度无丢失）
	 */
	@Override
	@NonNull
	public abstract BigDecimal getAsBigDecimal();

	/**
	 * 获取当前数值的 {@link BigInteger} 表示（子类必须实现，核心抽象方法）
	 * <p>
	 * 子类实现约束（确保多类型一致性）：
	 * <ol>
	 * <li>小数处理：采用 {@link RoundingMode#DOWN} 模式，直接截断小数部分（如 99.99 → 99），非四舍五入；</li>
	 * <li>整数处理：直接转为 BigInteger，无精度丢失（如 IntValue(2147483647) 完整转换）；</li>
	 * <li>格式要求：返回实例非 null、不可变，确保整数语义一致性。</li>
	 * </ol>
	 *
	 * @return 当前数值的 {@link BigInteger} 实例（非 null，小数部分已截断）
	 */
	@Override
	@NonNull
	public abstract BigInteger getAsBigInteger();

	/**
	 * 获取当前数值的布尔表示（严格映射规则）
	 * <p>
	 * 转换规则：仅当数值的 {@link BigInteger} 形式等于 {@link BigInteger#ONE}（即数值为 1，含 1.0、1.00 等）时返回 true，
	 * 其他情况（0、负数、非 1 的正数、小数如 1.1）均返回 false
	 * <p>注意：与 Java 原生"非零即 true"的自动拆箱逻辑不同，适用于"启用标记""状态位校验"等场景
	 *
	 * @return true：数值的 {@link BigInteger} 形式为 1；false：其他情况
	 */
	@Override
	public boolean getAsBoolean() {
		return getAsBigInteger().compareTo(BigInteger.ONE) == 0;
	}

	/**
	 * 安全转换为 byte 类型（范围：-128 ~ 127）
	 * <p>基于 {@link NumberUtils#toByte(Number)} 实现，经范围校验，无溢出、无精度丢失
	 *
	 * @return 当前数值的 byte 类型表示
	 * @throws ArithmeticException 若数值低于 {@link Byte#MIN_VALUE}（-128）或高于 {@link Byte#MAX_VALUE}（127）
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
	 * <li>获取整数形式：通过 {@link #getAsBigInteger()} 截断小数部分；</li>
	 * <li>范围校验：若数值 &lt; 0 或 &gt; 127，抛出 {@link ArithmeticException}（超出标准 ASCII 范围）；</li>
	 * <li>类型转换：将整数强制转为 char 类型，映射标准 ASCII 字符集（如 65→'A'、32→空格）。</li>
	 * </ol>
	 * 适用场景：仅用于标准 ASCII 字符转换，不支持扩展 ASCII 或 Unicode 字符（如中文、特殊符号）
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
	 * 安全转换为 double 类型（经三层校验，无隐性精度丢失）
	 * <p>
	 * 校验逻辑（基于 {@link NumberUtils#toDouble(Number)}）：
	 * <ol>
	 * <li>范围校验：数值在 {@link Double#MIN_VALUE} ~ {@link Double#MAX_VALUE} 之间；</li>
	 * <li>有效数字校验：BigDecimal 有效数字不超过 15-17 位（double 最大精确有效数字位数）；</li>
	 * <li>反向校验：转换后转回 BigDecimal，与原数值忽略末尾零后相等（确保无精度丢失）。</li>
	 * </ol>
	 *
	 * @return 当前数值的 double 类型表示
	 * @throws ArithmeticException 若数值超出 double 范围，或无法精确表示
	 */
	@Override
	public double getAsDouble() throws ArithmeticException {
		return NumberUtils.toDouble(getAsBigDecimal());
	}

	/**
	 * 获取包含当前数值的单元素集合（实现 {@link Value} 接口契约）
	 * <p>返回 {@link Elements} 单例集合（size=1），便于业务层统一处理"单值/多值"场景（如批量迭代、统一存储）
	 *
	 * @return 包含当前实例的 {@link Elements} 集合（非 null，size=1）
	 */
	@Override
	public Elements<NumberValue> getAsElements() {
		return Elements.singleton(this);
	}

	/**
	 * 安全转换为 float 类型（经三层校验，无隐性精度丢失）
	 * <p>
	 * 校验逻辑（基于 {@link NumberUtils#toFloat(Number)}）：
	 * <ol>
	 * <li>范围校验：数值在 {@link Float#MIN_VALUE} ~ {@link Float#MAX_VALUE} 之间；</li>
	 * <li>有效数字校验：BigDecimal 有效数字不超过 6-7 位（float 最大精确有效数字位数）；</li>
	 * <li>反向校验：转换后转回 BigDecimal，与原数值忽略末尾零后相等（确保无精度丢失）。</li>
	 * </ol>
	 *
	 * @return 当前数值的 float 类型表示
	 * @throws ArithmeticException 若数值超出 float 范围，或无法精确表示
	 */
	@Override
	public float getAsFloat() throws ArithmeticException {
		return NumberUtils.toFloat(getAsBigDecimal());
	}

	/**
	 * 安全转换为 int 类型（范围：-2147483648 ~ 2147483647）
	 * <p>基于 {@link NumberUtils#toInteger(Number)} 实现，经范围校验，无溢出、无精度丢失（小数部分已截断）
	 *
	 * @return 当前数值的 int 类型表示
	 * @throws ArithmeticException 若数值低于 {@link Integer#MIN_VALUE} 或高于 {@link Integer#MAX_VALUE}
	 */
	@Override
	public int getAsInt() throws ArithmeticException {
		return NumberUtils.toInteger(getAsBigInteger());
	}

	/**
	 * 安全转换为 long 类型（范围：-9223372036854775808 ~ 9223372036854775807）
	 * <p>基于 {@link NumberUtils#toLong(Number)} 实现，经范围校验，无溢出、无精度丢失（小数部分已截断）
	 *
	 * @return 当前数值的 long 类型表示
	 * @throws ArithmeticException 若数值低于 {@link Long#MIN_VALUE} 或高于 {@link Long#MAX_VALUE}
	 */
	@Override
	public long getAsLong() throws ArithmeticException {
		return NumberUtils.toLong(getAsBigInteger());
	}

	/**
	 * 获取当前数值自身（实现 {@link Value} 接口契约）
	 * <p>语义：明确当前 {@link Value} 为数值类型，直接返回自身，避免业务层额外类型转换（如强制转型、类型判断）
	 *
	 * @return 当前 {@link NumberValue} 实例（非 null）
	 */
	@Override
	public NumberValue getAsNumber() {
		return this;
	}

	/**
	 * 安全转换为 short 类型（范围：-32768 ~ 32767）
	 * <p>基于 {@link NumberUtils#toShort(Number)} 实现，经范围校验，无溢出、无精度丢失（小数部分已截断）
	 *
	 * @return 当前数值的 short 类型表示
	 * @throws ArithmeticException 若数值低于 {@link Short#MIN_VALUE} 或高于 {@link Short#MAX_VALUE}
	 */
	@Override
	public short getAsShort() throws ArithmeticException {
		return NumberUtils.toShort(getAsBigInteger());
	}

	/**
	 * 获取当前数值的字符串表示（子类必须实现，核心抽象方法）
	 * <p>
	 * 子类实现约束（确保格式统一、可读性强）：
	 * <ol>
	 * <li>整数类型：直接输出数字字符串（如 100 → "100"，-50 → "-50"），无小数位；</li>
	 * <li>小数类型：保留原始小数位（如 99.99 → "99.99"，100.0 → "100.0"），不自动省略末尾零；</li>
	 * <li>超大整数：完整输出所有数字（如 12345678901234567890 → 对应完整字符串），避免科学计数法；</li>
	 * <li>格式要求：字符串非 null、非空，确保日志输出、序列化、前端展示的兼容性。</li>
	 * </ol>
	 *
	 * @return 当前数值的字符串表示（非 null、非空，格式统一）
	 */
	@Override
	@NonNull
	public abstract String getAsString();

	/**
	 * 重写 {@link Number} 接口方法，委托 {@link #getAsInt()} 实现
	 * <p>仅为满足接口契约，无额外逻辑；建议业务层直接使用 {@link #getAsInt()}（语义更清晰，含范围校验）
	 *
	 * @return 当前数值的 int 类型表示
	 * @throws ArithmeticException 若数值超出 int 范围
	 */
	@Override
	public final int intValue() throws ArithmeticException {
		return getAsInt();
	}

	/**
	 * 重写 {@link Number} 接口方法，委托 {@link #getAsShort()} 实现
	 * <p>仅为满足接口契约，无额外逻辑；语义清晰，含范围校验，无精度丢失
	 *
	 * @return 当前数值的 short 类型表示
	 * @throws ArithmeticException 若数值超出 short 范围
	 */
	@Override
	public final short shortValue() {
		return getAsShort();
	}

	/**
	 * 重写 {@link Number} 接口方法，委托 {@link #getAsByte()} 实现
	 * <p>仅为满足接口契约，无额外逻辑；语义清晰，含范围校验，无精度丢失
	 *
	 * @return 当前数值的 byte 类型表示
	 * @throws ArithmeticException 若数值超出 byte 范围
	 */
	@Override
	public final byte byteValue() {
		return getAsByte();
	}

	/**
	 * 标记当前是否为多值类型（实现 {@link Value} 接口契约）
	 * <p>数值类型始终为单值（一个实例对应一个具体数值），故固定返回 false，且不可重写（避免子类破坏语义）
	 *
	 * @return false（固定值，数值类型无多值场景）
	 */
	@Override
	public final boolean isMultiple() {
		return false;
	}

	/**
	 * 标记当前是否为数值类型（实现 {@link Value} 接口契约）
	 * <p>当前类为数值抽象基类，所有子类均为数值类型，故固定返回 true（子类可直接继承，无需重写）
	 *
	 * @return true（固定值，当前实例为数值类型）
	 */
	@Override
	public boolean isNumber() {
		return true;
	}

	/**
	 * 重写 {@link Number} 接口方法，委托 {@link #getAsLong()} 实现
	 * <p>仅为满足接口契约，无额外逻辑；建议业务层直接使用 {@link #getAsLong()}（语义更清晰，含范围校验）
	 *
	 * @return 当前数值的 long 类型表示
	 * @throws ArithmeticException 若数值超出 long 范围
	 */
	@Override
	public final long longValue() throws ArithmeticException {
		return getAsLong();
	}

	/**
	 * 重写 toString 方法，委托 {@link #getAsString()} 实现
	 * <p>仅为满足 Object 接口契约，无额外逻辑；建议业务层直接使用 {@link #getAsString()}（语义更清晰，格式统一）
	 *
	 * @return 当前数值的字符串表示（如"100"、"99.99"、"12345678901234567890"）
	 */
	@Override
	public String toString() {
		return getAsString();
	}
}