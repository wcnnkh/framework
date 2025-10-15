package run.soeasy.framework.core.math;

import java.math.BigDecimal;
import java.math.BigInteger;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.domain.Value;
import run.soeasy.framework.core.domain.Version;

/**
 * 数值处理抽象基类，同时实现{@link Number}（Java原生数值接口）与{@link Version}（版本比较接口），
 * 为框架提供统一的高精度数值操作、类型安全转换及版本化能力，是所有具体数值类型（如整数、小数、分数）的顶层抽象。
 * 
 * <p>设计目标：
 * 解决基础类型的三大痛点：① float/double浮点数运算精度丢失（如{@code 0.1 + 0.2 ≠ 0.3}）；② long类型超限后无法处理大数；③ 数值操作碎片化（不同类型需单独处理）；
 * 同时支持将数值作为版本号（如{@code 1.0.1}、{@code 20240501}）进行大小比较，适用于对精度和类型安全性要求高的业务场景。
 *
 * <h3>核心能力</h3>
 * <ul>
 * <li><strong>高精度计算支持</strong>：基于{@link BigDecimal}（小数）和{@link BigInteger}（整数）实现底层存储，
 * 完全避免运算精度丢失，支持任意大小数值（包括超出{@code long}范围的超大整数、保留任意小数位的高精度小数）计算；</li>
 * <li><strong>类型安全转换</strong>：所有基本类型（byte/short/int/long/float/double）转换均包含范围校验，
 * 超出目标类型取值范围时抛出{@link ArithmeticException}，杜绝隐式溢出导致的逻辑错误（如int类型存储{@code 2147483648}时直接报错）；</li>
 * <li><strong>完整数学运算</strong>：封装加、减、乘、除、取模、幂运算、绝对值等基础操作，
 * 所有运算逻辑统一委托给{@link Fraction}（分数类型）实现，保证跨类型运算（如整数+小数、大数×小数）的一致性；</li>
 * <li><strong>版本化支持</strong>：实现{@link Version}接口，可将数值直接作为版本号使用，通过{@link #compareTo(Value)}方法完成版本高低比较（如{@code 2.1 > 2.0.1}）；</li>
 * <li><strong>预置常用常量</strong>：提供-1（{@link #MINUS_ONE}）、0（{@link #ZERO}）、1（{@link #ONE}）、10（{@link #TEN}）等高频使用数值的常量实例，
 * 避免重复创建对象，提升性能；</li>
 * <li><strong>类型判断工具</strong>：内置{@link #isZero()}（是否为0）、{@link #isPositive()}（是否为正数）、{@link #isNegative()}（是否为负数）等方法，
 * 简化业务层数值判断逻辑（如无需手动写{@code num.compareTo(ZERO) > 0}）。</li>
 * </ul>
 *
 * <h3>适用场景</h3>
 * <ul>
 * <li>金融领域：货币计算（如订单金额、手续费、税费）、汇率转换，需完全避免精度丢失（如1分误差导致财务对账异常）；</li>
 * <li>科学计算：物理/化学实验数据记录、工程测量值计算，需处理超大数值（如天文数据中的星体质量）或高精度小数（如实验室微观尺寸测量）；</li>
 * <li>数据校验：参数数值范围校验（如年龄上限150、金额阈值100万），需明确抛出溢出异常，而非隐式截断（如将200岁截断为44岁）；</li>
 * <li>版本管理：配置文件版本、接口API版本号（如API v2.1、配置v1.0.3），需基于数值完成版本高低排序与兼容性判断；</li>
 * <li>算法实现：需大数运算的场景（如密码学中的大素数运算、大数据统计中的用户数计数），突破基础类型的取值范围限制。</li>
 * </ul>
 *
 * <h3>使用示例</h3>
 * 
 * <pre class="code">
 * // 1. 创建不同类型的数值实例（整数、高精度小数、超大整数）
 * NumberValue intNum = new IntValue(100); // 基础整数类型（基于int，适用于小范围整数）
 * NumberValue decimalNum = new BigDecimalValue("99.99"); // 高精度小数（避免浮点数误差）
 * NumberValue bigNum = new BigIntegerValue("12345678901234567890"); // 超大整数（超出long范围）
 *
 * // 2. 跨类型数值运算（自动转为分数处理，无精度丢失）
 * NumberValue sum = intNum.add(decimalNum); // 100 + 99.99 = 199.99（结果为Fraction类型）
 * NumberValue product = sum.multiply(bigNum); // 199.99 × 12345678901234567890（高精度大数乘法）
 * NumberValue remainder = product.mod(new IntValue(100)); // 取模运算：结果为乘积对100的余数（非负）
 *
 * // 3. 类型判断与安全转换（含范围校验，超限抛异常）
 * if (sum.isPositive()) { // 判断数值是否为正数（sum &gt; 0）
 *     double sumDouble = sum.getAsDouble(); // 安全转为double（需确保数值 ≤ Double.MAX_VALUE）
 *     BigInteger sumBigInt = sum.getAsBigInteger(); // 转为整数（小数部分截断，如199.99→199）
 * }
 *
 * // 4. 版本号比较（假设product对应版本号数值，如20240501）
 * if (product.compareTo(new IntValue(2024)) &gt; 0) { // 比较版本高低（product &gt; 2024）
 *     System.out.println("当前版本高于2024");
 * }
 * </pre>
 *
 * @see BigDecimalValue 高精度小数实现类（基于{@link BigDecimal}，支持任意小数位精度）
 * @see IntValue 基础整数实现类（基于int，自动适配long/{@link BigInteger}，适用于小范围整数）
 * @see BigIntegerValue 超大整数实现类（基于{@link BigInteger}，支持超出long范围的整数）
 * @see Fraction 分数实现类（支撑跨类型精确运算的核心载体，避免整数/小数运算误差）
 * @see Version 版本比较接口（定义数值版本化能力的核心规范）
 * @see Number Java原生数值接口（定义基础类型转换的标准契约）
 */
public abstract class NumberValue extends Number implements Version {
	private static final long serialVersionUID = 1L;

	/**
	 * 数值-1的常量实例（基于{@link IntValue}实现），与{@link #NEGATIVE_ONE}等价，供习惯"MINUS_ONE"命名的场景使用
	 */
	public static final NumberValue MINUS_ONE = new IntValue(-1);
	/**
	 * 数值0的常量实例（基于{@link IntValue}实现），常用于"无值""初始值""默认值"等场景的比较（如判断是否未赋值）
	 */
	public static final NumberValue ZERO = new IntValue(0);
	/**
	 * 数值1的常量实例（基于{@link IntValue}实现），常用于计数起始值、乘法单位元（如1×n=n）、布尔值映射（1表示true）等场景
	 */
	public static final NumberValue ONE = new IntValue(1);
	/**
	 * 数值-1的常量实例（基于{@link IntValue}实现），与{@link #MINUS_ONE}等价，语义更贴近"负数"场景（如取反、减法运算）
	 */
	public static final NumberValue NEGATIVE_ONE = new IntValue(-1);
	/**
	 * 数值10的常量实例（基于{@link IntValue}实现），常用于进制转换（如十进制→二进制）、十倍放大/缩小（如金额单位从"分"转"元"）等场景
	 */
	public static final NumberValue TEN = new IntValue(10);

	/**
	 * 创建"数值超出目标类型范围"的异常实例
	 * <p>
	 * 当数值转换为目标基础类型（如byte/int/long）时，若数值大于该类型的最大值，调用此方法生成带具体超限信息的异常。
	 * <p>
	 * <strong>注意</strong>：当前逻辑仅校验"数值大于最大值"的场景，"数值小于最小值"（如负数值超出{@code Byte.MIN_VALUE}）未校验，使用时需额外关注负数转换场景。
	 *
	 * @param number 超出范围的数值（通常为{@link BigInteger}或{@link BigDecimal}实例）
	 * @return 包含超限数值信息的{@link ArithmeticException}
	 */
	private static RuntimeException createTooHighException(Number number) {
		return new ArithmeticException("The value[" + number + "] is too high");
	}

	/**
	 * 获取当前数值的绝对值
	 * <p>
	 * 逻辑说明：
	 * 1. 若当前数值为负数（{@link #isNegative()}返回true），则通过乘以{@link #NEGATIVE_ONE}（-1）取反；
	 * 2. 若当前数值为非负数（0或正数），则直接返回自身（避免无意义的运算，提升性能）。
	 * <p>
	 * 结果类型：返回值的类型与原数值一致（如{@link IntValue}的绝对值仍为{@link IntValue}，{@link BigDecimalValue}的绝对值仍为{@link BigDecimalValue}）。
	 *
	 * @return 绝对值对应的{@link NumberValue}实例（非null，类型与原数值相同）
	 */
	public NumberValue abs() {
		return isNegative() ? multiply(NEGATIVE_ONE) : this;
	}

	/**
	 * 重写{@link Number}接口的doubleValue()方法，委托给{@link #getAsDouble()}实现
	 * <p>
	 * <strong>注意</strong>：此方法仅为满足{@link Number}接口的契约，无额外逻辑；建议业务层直接使用{@link #getAsDouble()}（语义更清晰，且明确包含范围校验）。
	 *
	 * @return 当前数值的double类型表示（已做范围校验，若数值&gt;{@code Double.MAX_VALUE}则抛出{@link ArithmeticException}）
	 * @throws ArithmeticException 若数值超过double类型的最大值（约{@code 1.8×10³⁰⁸}）
	 */
	@Override
	public double doubleValue() {
		return getAsDouble();
	}

	/**
	 * 重写equals()方法，基于数值大小比较而非对象引用
	 * <p>
	 * 比较规则（严格遵循"equals契约"）：
	 * <ul>
	 * <li>1. 若参数为null，直接返回false（null与任何对象不相等）；</li>
	 * <li>2. 若参数为{@link NumberValue}的子类实例（如{@link IntValue}、{@link BigDecimalValue}），
	 * 通过{@link #compareTo(Value)}比较数值大小，返回"是否相等"（即compareTo结果为0）；</li>
	 * <li>3. 若参数为非{@link NumberValue}类型（如{@link Integer}、{@link Double}），返回false（不支持与非数值抽象类的实例比较）。</li>
	 * </ul>
	 *
	 * @param obj 待比较的对象
	 * @return boolean：true表示数值相等；false表示对象为null/非{@link NumberValue}类型/数值不相等
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof NumberValue) {
			return compareTo((NumberValue) obj) == 0;
		}
		return false;
	}

	/**
	 * 抽象方法：获取当前数值的哈希值
	 * <p>
	 * 子类必须实现此方法，且需满足"equals()为true的实例，hashCode()必相等"的契约：
	 * <ul>
	 * <li>哈希值需基于数值本身的内容计算（而非对象内存地址）；</li>
	 * <li>例如：{@link IntValue}(100)与{@link BigIntegerValue}("100")的equals()为true，故两者hashCode()需一致。</li>
	 * </ul>
	 *
	 * @return 基于数值内容计算的哈希值（int类型）
	 */
	@Override
	public abstract int hashCode();

	/**
	 * 重写{@link Number}接口的floatValue()方法，委托给{@link #getAsFloat()}实现
	 * <p>
	 * <strong>注意</strong>：此方法仅为满足{@link Number}接口的契约，无额外逻辑；建议业务层直接使用{@link #getAsFloat()}（语义更清晰，且明确包含范围校验）。
	 *
	 * @return 当前数值的float类型表示（已做范围校验，若数值&gt;{@code Float.MAX_VALUE}则抛出{@link ArithmeticException}）
	 * @throws ArithmeticException 若数值超过float类型的最大值（约{@code 3.4×10³⁸}）
	 */
	@Override
	public float floatValue() {
		return getAsFloat();
	}

	/**
	 * 判断当前数值是否等于0
	 * <p>
	 * 实现逻辑：通过与预置常量{@link #ZERO}比较实现，等价于{@code this.compareTo(ZERO) == 0}，简化业务层判断代码。
	 *
	 * @return boolean：true表示数值等于0；false表示数值不等于0
	 */
	public boolean isZero() {
		return compareTo(ZERO) == 0;
	}

	/**
	 * 判断当前数值是否等于1
	 * <p>
	 * 实现逻辑：通过与预置常量{@link #ONE}比较实现，等价于{@code this.compareTo(ONE) == 0}，简化业务层判断代码（如判断是否为计数起始值）。
	 *
	 * @return boolean：true表示数值等于1；false表示数值不等于1
	 */
	public boolean isOne() {
		return compareTo(ONE) == 0;
	}

	/**
	 * 判断当前数值是否为正数（严格大于0）
	 * <p>
	 * 实现逻辑：通过与预置常量{@link #ZERO}比较实现，等价于{@code this.compareTo(ZERO) > 0}，不包含0（0既非正数也非负数）。
	 *
	 * @return boolean：true表示数值&gt;0；false表示数值≤0
	 */
	public boolean isPositive() {
		return compareTo(ZERO) > 0;
	}

	/**
	 * 判断当前数值是否为负数（严格小于0）
	 * <p>
	 * 实现逻辑：通过与预置常量{@link #ZERO}比较实现，等价于{@code this.compareTo(ZERO) < 0}，不包含0（0既非正数也非负数）。
	 *
	 * @return boolean：true表示数值&lt;0；false表示数值≥0
	 */
	public boolean isNegative() {
		return compareTo(ZERO) < 0;
	}

	/**
	 * 抽象方法：获取当前数值的{@link BigDecimal}表示
	 * <p>
	 * 子类必须实现此方法，返回高精度的小数形式（即使是整数类型，也需转为带小数位的{@link BigDecimal}）：
	 * <ul>
	 * <li>例如：{@link IntValue}(100)需返回{@code new BigDecimal("100.0")}；</li>
	 * <li>用途：供小数运算、高精度比较（如货币金额比较）等场景使用。</li>
	 * </ul>
	 *
	 * @return 当前数值的{@link BigDecimal}实例（非null，确保精度不丢失）
	 */
	@Override
	public abstract BigDecimal getAsBigDecimal();

	/**
	 * 抽象方法：获取当前数值的{@link BigInteger}表示
	 * <p>
	 * 子类必须实现此方法，返回整数形式（若为小数类型，需截断小数部分，仅保留整数部分）：
	 * <ul>
	 * <li>例如：{@link BigDecimalValue}("199.99")需返回{@code new BigInteger("199")}；</li>
	 * <li>用途：供整数运算、整数类型转换（如转为int/long）等场景使用。</li>
	 * </ul>
	 *
	 * @return 当前数值的{@link BigInteger}实例（非null，小数部分已截断）
	 */
	@Override
	public abstract BigInteger getAsBigInteger();

	/**
	 * 获取当前数值的布尔表示
	 * <p>
	 * 转换规则（严格匹配"数值1即true"）：
	 * 仅当数值的{@link BigInteger}形式等于{@link BigInteger#ONE}（即数值为1）时返回true，其他情况（包括0、负数、非1的正数、小数）均返回false。
	 * <p>
	 * <strong>注意</strong>：此方法非"非零即true"（与Java基础类型的自动拆箱逻辑不同），需结合业务场景使用（如"是否启用"的标记判断）。
	 *
	 * @return boolean：true表示数值的{@link BigInteger}形式为1；false表示其他情况
	 */
	@Override
	public boolean getAsBoolean() {
		BigInteger number = getAsBigInteger();
		if (number == null) {
			return false;
		}

		return number.compareTo(BigInteger.ONE) == 0;
	}

	/**
	 * 安全转换为byte类型
	 * <p>
	 * 校验逻辑：
	 * <ul>
	 * <li>1. 先通过{@link #getAsBigInteger()}获取数值的整数形式；</li>
	 * <li>2. 若数值&gt;{@code Byte.MAX_VALUE}（127），抛出{@link ArithmeticException}（超限）；</li>
	 * <li>3. 若数值≤{@code Byte.MAX_VALUE}，调用{@link BigInteger#byteValue()}完成转换。</li>
	 * </ul>
	 * <p>
	 * <strong>注意</strong>：当前逻辑未校验"数值&lt;{@code Byte.MIN_VALUE}（-128）"的场景，若需处理负数值转换，需在业务层额外判断。
	 *
	 * @return 当前数值的byte类型表示（范围：-128~127）
	 * @throws ArithmeticException 若数值&gt;{@code Byte.MAX_VALUE}（127），超出byte类型最大值
	 */
	@Override
	public byte getAsByte() throws ArithmeticException {
		BigInteger number = getAsBigInteger();
		if (number == null) {
			return 0;
		}

		if (number.compareTo(BigInteger.valueOf(Byte.MAX_VALUE)) > 0) {
			throw createTooHighException(number);
		}
		return number.byteValue();
	}

	/**
	 * 转换为char类型（基于byte类型的ASCII码映射）
	 * <p>
	 * 转换逻辑：
	 * <ul>
	 * <li>1. 先通过{@link #getAsByte()}获取数值的byte表示（已校验范围≤127）；</li>
	 * <li>2. 将byte值强制转为char类型（本质是使用byte对应的ASCII码映射字符）。</li>
	 * </ul>
	 * <p>
	 * 适用范围：仅支持0~127范围内的数值（对应标准ASCII字符集，如65→'A'、97→'a'），超出范围会触发{@link #getAsByte()}的超限异常。
	 *
	 * @return 当前数值对应的char字符（标准ASCII字符，范围：\u0000~\u007F）
	 * @throws ArithmeticException 若数值&gt;{@code Byte.MAX_VALUE}（127），超出ASCII字符范围
	 */
	@Override
	public char getAsChar() throws ArithmeticException {
		return (char) getAsByte();
	}

	/**
	 * 安全转换为double类型
	 * <p>
	 * 校验逻辑：
	 * <ul>
	 * <li>1. 先通过{@link #getAsBigDecimal()}获取数值的高精度小数形式；</li>
	 * <li>2. 若数值&gt;{@code Double.MAX_VALUE}（约{@code 1.8×10³⁰⁸}），抛出{@link ArithmeticException}（超限）；</li>
	 * <li>3. 若数值≤{@code Double.MAX_VALUE}，调用{@link BigDecimal#doubleValue()}完成转换。</li>
	 * </ul>
	 * <p>
	 * <strong>注意</strong>：当前逻辑未校验"数值&lt;{@code Double.MIN_VALUE}（约{@code 4.9×10⁻³²⁴}）"的场景，若需处理极小值转换，需在业务层额外判断。
	 *
	 * @return 当前数值的double类型表示
	 * @throws ArithmeticException 若数值&gt;{@code Double.MAX_VALUE}（约{@code 1.8×10³⁰⁸}），超出double类型最大值
	 */
	@Override
	public double getAsDouble() throws ArithmeticException {
		BigDecimal number = getAsBigDecimal();
		if (number == null) {
			return 0;
		}
		if (number.compareTo(BigDecimal.valueOf(Double.MAX_VALUE)) > 0) {
			throw createTooHighException(number);
		}
		return number.doubleValue();
	}

	/**
	 * 获取包含当前数值的单元素集合
	 * <p>
	 * 实现{@link Value}接口的契约，返回{@link Elements}单例集合（size=1），便于业务层统一处理"单值/多值"场景（如统一迭代、批量处理）。
	 *
	 * @return 包含当前{@link NumberValue}实例的{@link Elements}集合（非null，size=1）
	 */
	@Override
	public Elements<? extends Value> getAsElements() {
		return Elements.singleton(this);
	}

	/**
	 * 安全转换为float类型
	 * <p>
	 * 校验逻辑：
	 * <ul>
	 * <li>1. 先通过{@link #getAsBigDecimal()}获取数值的高精度小数形式；</li>
	 * <li>2. 若数值&gt;{@code Float.MAX_VALUE}（约{@code 3.4×10³⁸}），抛出{@link ArithmeticException}（超限）；</li>
	 * <li>3. 若数值≤{@code Float.MAX_VALUE}，调用{@link BigDecimal#floatValue()}完成转换。</li>
	 * </ul>
	 * <p>
	 * <strong>注意</strong>：当前逻辑未校验"数值&lt;{@code Float.MIN_VALUE}（约{@code 1.4×10⁻⁴⁵}）"的场景，若需处理极小值转换，需在业务层额外判断。
	 *
	 * @return 当前数值的float类型表示
	 * @throws ArithmeticException 若数值&gt;{@code Float.MAX_VALUE}（约{@code 3.4×10³⁸}），超出float类型最大值
	 */
	@Override
	public float getAsFloat() throws ArithmeticException {
		BigDecimal number = getAsBigDecimal();
		if (number == null) {
			return 0;
		}

		if (number.compareTo(BigDecimal.valueOf(Float.MAX_VALUE)) > 0) {
			throw createTooHighException(number);
		}
		return number.floatValue();
	}

	/**
	 * 安全转换为int类型
	 * <p>
	 * 校验逻辑：
	 * <ul>
	 * <li>1. 先通过{@link #getAsBigInteger()}获取数值的整数形式；</li>
	 * <li>2. 若数值&gt;{@code Integer.MAX_VALUE}（2147483647），抛出{@link ArithmeticException}（超限）；</li>
	 * <li>3. 若数值≤{@code Integer.MAX_VALUE}，调用{@link BigInteger#intValue()}完成转换。</li>
	 * </ul>
	 * <p>
	 * <strong>注意</strong>：当前逻辑未校验"数值&lt;{@code Integer.MIN_VALUE}（-2147483648）"的场景，若需处理负数值转换，需在业务层额外判断。
	 *
	 * @return 当前数值的int类型表示（范围：-2147483648~2147483647）
	 * @throws ArithmeticException 若数值&gt;{@code Integer.MAX_VALUE}（2147483647），超出int类型最大值
	 */
	@Override
	public int getAsInt() throws ArithmeticException {
		BigInteger number = getAsBigInteger();
		if (number == null) {
			return 0;
		}

		if (number.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0) {
			throw createTooHighException(number);
		}
		return number.intValue();
	}

	/**
	 * 安全转换为long类型
	 * <p>
	 * 校验逻辑：
	 * <ul>
	 * <li>1. 先通过{@link #getAsBigInteger()}获取数值的整数形式；</li>
	 * <li>2. 若数值&gt;{@code Long.MAX_VALUE}（9223372036854775807），抛出{@link ArithmeticException}（超限）；</li>
	 * <li>3. 若数值≤{@code Long.MAX_VALUE}，调用{@link BigInteger#longValue()}完成转换。</li>
	 * </ul>
	 * <p>
	 * <strong>注意</strong>：当前逻辑未校验"数值&lt;{@code Long.MIN_VALUE}（-9223372036854775808）"的场景，若需处理负数值转换，需在业务层额外判断。
	 *
	 * @return 当前数值的long类型表示（范围：-9223372036854775808~9223372036854775807）
	 * @throws ArithmeticException 若数值&gt;{@code Long.MAX_VALUE}（9223372036854775807），超出long类型最大值
	 */
	@Override
	public long getAsLong() throws ArithmeticException {
		BigInteger number = getAsBigInteger();
		if (number == null) {
			return 0;
		}

		if (number.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0) {
			throw createTooHighException(number);
		}
		return number.longValue();
	}

	/**
	 * 获取当前数值自身（实现{@link Value}接口的契约）
	 * <p>
	 * 语义说明：明确当前{@link Value}是数值类型，直接返回自身实例，避免业务层额外的类型转换（如{@code (Value)→(NumberValue)}）。
	 *
	 * @return 当前{@link NumberValue}实例（非null，即自身）
	 */
	@Override
	public NumberValue getAsNumber() {
		return this;
	}

	/**
	 * 安全转换为short类型
	 * <p>
	 * 校验逻辑：
	 * <ul>
	 * <li>1. 先通过{@link #getAsBigInteger()}获取数值的整数形式；</li>
	 * <li>2. 若数值&gt;{@code Short.MAX_VALUE}（32767），抛出{@link ArithmeticException}（超限）；</li>
	 * <li>3. 若数值≤{@code Short.MAX_VALUE}，调用{@link BigInteger#shortValue()}完成转换。</li>
	 * </ul>
	 * <p>
	 * <strong>注意</strong>：当前逻辑未校验"数值&lt;{@code Short.MIN_VALUE}（-32768）"的场景，若需处理负数值转换，需在业务层额外判断。
	 *
	 * @return 当前数值的short类型表示（范围：-32768~32767）
	 * @throws ArithmeticException 若数值&gt;{@code Short.MAX_VALUE}（32767），超出short类型最大值
	 */
	@Override
	public short getAsShort() throws ArithmeticException {
		BigInteger number = getAsBigInteger();
		if (number == null) {
			return 0;
		}

		if (number.compareTo(BigInteger.valueOf(Short.MAX_VALUE)) > 0) {
			throw createTooHighException(number);
		}
		return number.shortValue();
	}

	/**
	 * 重写{@link Number}接口的intValue()方法，委托给{@link #getAsInt()}实现
	 * <p>
	 * <strong>注意</strong>：此方法仅为满足{@link Number}接口的契约，无额外逻辑；建议业务层直接使用{@link #getAsInt()}（语义更清晰，且明确包含范围校验）。
	 *
	 * @return 当前数值的int类型表示（已做范围校验，若数值&gt;{@code Integer.MAX_VALUE}则抛出{@link ArithmeticException}）
	 * @throws ArithmeticException 若数值超过int类型的最大值（2147483647）
	 */
	@Override
	public int intValue() {
		return getAsInt();
	}

	/**
	 * 标记当前是否为多值类型（实现{@link Value}接口的契约）
	 * <p>
	 * 数值类型始终为"单值"（一个实例对应一个具体数值），故固定返回false，且用final修饰不可重写（避免子类破坏语义）。
	 *
	 * @return boolean：false（固定值，数值类型无多值场景）
	 */
	@Override
	public final boolean isMultiple() {
		return false;
	}

	/**
	 * 标记当前是否为数值类型（实现{@link Value}接口的契约）
	 * <p>
	 * 当前类是数值抽象基类，所有子类均为数值类型，故固定返回true（子类可继承此实现，无需重写）。
	 *
	 * @return boolean：true（固定值，当前实例为数值类型）
	 */
	@Override
	public boolean isNumber() {
		return true;
	}

	/**
	 * 重写{@link Number}接口的longValue()方法，委托给{@link #getAsLong()}实现
	 * <p>
	 * <strong>注意</strong>：此方法仅为满足{@link Number}接口的契约，无额外逻辑；建议业务层直接使用{@link #getAsLong()}（语义更清晰，且明确包含范围校验）。
	 *
	 * @return 当前数值的long类型表示（已做范围校验，若数值&gt;{@code Long.MAX_VALUE}则抛出{@link ArithmeticException}）
	 * @throws ArithmeticException 若数值超过long类型的最大值（9223372036854775807）
	 */
	@Override
	public long longValue() {
		return getAsLong();
	}

	/**
	 * 重写toString()方法，委托给{@link #getAsString()}实现
	 * <p>
	 * <strong>注意</strong>：此方法仅为满足Object接口的契约，无额外逻辑；建议业务层直接使用{@link #getAsString()}（语义更清晰，且返回格式更统一）。
	 *
	 * @return 当前数值的字符串表示（如"100"、"99.99"、"12345678901234567890"）
	 */
	@Override
	public String toString() {
		return getAsString();
	}

	/**
	 * 加法运算：当前数值 + 目标数值
	 * <p>
	 * 实现逻辑（确保跨类型高精度运算）：
	 * <ul>
	 * <li>1. 调用{@link #toFraction(NumberValue)}将当前数值和目标数值转为{@link Fraction}（分数）；</li>
	 * <li>2. 调用{@link Fraction#add(NumberValue)}完成加法运算（分数运算无精度丢失）；</li>
	 * <li>3. 返回运算结果（类型为{@link Fraction}，或子类重写后返回对应类型）。</li>
	 * </ul>
	 *
	 * @param value 加数（目标数值，不可为null）
	 * @return {@link NumberValue}：加法运算结果（高精度，无精度丢失）
	 * @throws NullPointerException 若value为null（未传入加数）
	 */
	public NumberValue add(@NonNull NumberValue value) {
		return toFraction(this).add(value);
	}

	/**
	 * 除法运算：当前数值 ÷ 目标数值
	 * <p>
	 * 实现逻辑（确保高精度与合法性）：
	 * <ul>
	 * <li>1. 调用{@link #toFraction(NumberValue)}将当前数值转为{@link Fraction}；</li>
	 * <li>2. 调用{@link Fraction#divide(NumberValue)}完成除法运算（分数除法支持精确结果）；</li>
	 * <li>3. 若目标数值为0（{@link #isZero()}返回true），由{@link Fraction#divide(NumberValue)}抛出{@link ArithmeticException}（除数为零）。</li>
	 * </ul>
	 *
	 * @param value 除数（目标数值，不可为null，且不能为0）
	 * @return {@link NumberValue}：除法运算结果（高精度，无精度丢失）
	 * @throws NullPointerException 若value为null（未传入除数）
	 * @throws ArithmeticException  若value为0（除数为零，非法数学运算）
	 */
	public NumberValue divide(@NonNull NumberValue value) {
		return toFraction(this).divide(value);
	}

	/**
	 * 取模运算：当前数值 % 目标数值（计算当前数值对目标数值的余数，结果非负）
	 * <p>
	 * 核心逻辑（遵循数学规范）：
	 * <ul>
	 * <li>1. 调用{@link #toFraction(NumberValue)}将当前数值转为{@link Fraction}；</li>
	 * <li>2. 委托{@link Fraction#mod(NumberValue)}实现高精度取模，确保结果满足"0 ≤ 余数 &lt; |除数|"（非负）；</li>
	 * <li>3. 适用场景：周期性计算（如日期归属月/周）、密码学中的大素数取模等。</li>
	 * </ul>
	 *
	 * @param value 除数（目标数值，不可为null，且不能为0）
	 * @return {@link NumberValue}：取模运算结果（非负，高精度）
	 * @throws NullPointerException 若value为null（未传入除数）
	 * @throws ArithmeticException  若value为0（除数为零，非法数学运算）
	 * @see Fraction#mod(NumberValue) {@link Fraction}的高精度取模实现（定义具体计算规则）
	 */
	public NumberValue mod(@NonNull NumberValue value) {
		return toFraction(this).mod(value);
	}

	/**
	 * 乘法运算：当前数值 × 目标数值
	 * <p>
	 * 实现逻辑（确保跨类型高精度运算）：
	 * <ul>
	 * <li>1. 调用{@link #toFraction(NumberValue)}将当前数值和目标数值转为{@link Fraction}；</li>
	 * <li>2. 调用{@link Fraction#multiply(NumberValue)}完成乘法运算（分数运算无精度丢失）；</li>
	 * <li>3. 返回运算结果（类型为{@link Fraction}，或子类重写后返回对应类型）。</li>
	 * </ul>
	 *
	 * @param value 乘数（目标数值，不可为null）
	 * @return {@link NumberValue}：乘法运算结果（高精度，无精度丢失）
	 * @throws NullPointerException 若value为null（未传入乘数）
	 */
	public NumberValue multiply(@NonNull NumberValue value) {
		return toFraction(this).multiply(value);
	}

	/**
	 * 幂运算：当前数值 ^ 目标数值（当前数值的目标数值次幂）
	 * <p>
	 * 实现逻辑（支持整数幂与小数幂）：
	 * <ul>
	 * <li>1. 调用{@link #toFraction(NumberValue)}将当前数值转为{@link Fraction}；</li>
	 * <li>2. 调用{@link Fraction#pow(NumberValue)}完成幂运算：
	 *     <ul>
	 *     <li>整数幂（如{@code 2^3=8}）：返回精确结果；</li>
	 *     <li>小数幂（如{@code 4^0.5=2}）：返回精确结果；</li>
	 *     <li>无法精确表示的幂（如{@code 2^0.3}）：返回近似值（具体精度由{@link Fraction}实现决定）。</li>
	 *     </ul>
	 * </li>
	 * </ul>
	 *
	 * @param value 指数（目标数值，不可为null）
	 * @return {@link NumberValue}：幂运算结果（高精度，尽可能保留精确值）
	 * @throws NullPointerException 若value为null（未传入指数）
	 */
	public NumberValue pow(@NonNull NumberValue value) {
		return toFraction(this).pow(value);
	}

	/**
	 * 取余运算：当前数值 % 目标数值（当前数值除以目标数值的余数，结果符号与被除数一致）
	 * <p>
	 * 与{@link #mod(NumberValue)}的区别：
	 * <ul>
	 * <li>{@link #mod(NumberValue)}：结果非负（遵循数学取模规范，如{@code (-5)%3=1}）；</li>
	 * <li>此方法：结果符号与被除数一致（遵循Java基础类型取余逻辑，如{@code (-5)%3=-2}）。</li>
	 * </ul>
	 * <p>
	 * 实现逻辑：调用{@link #toFraction(NumberValue)}转为{@link Fraction}，委托{@link Fraction#remainder(NumberValue)}实现。
	 *
	 * @param value 除数（目标数值，不可为null，且不能为0）
	 * @return {@link NumberValue}：取余运算结果（符号与被除数一致）
	 * @throws NullPointerException 若value为null（未传入除数）
	 * @throws ArithmeticException  若value为0（除数为零，非法数学运算）
	 */
	public NumberValue remainder(@NonNull NumberValue value) {
		return toFraction(this).remainder(value);
	}

	/**
	 * 减法运算：当前数值 - 目标数值
	 * <p>
	 * 实现逻辑（确保跨类型高精度运算）：
	 * <ul>
	 * <li>1. 调用{@link #toFraction(NumberValue)}将当前数值和目标数值转为{@link Fraction}；</li>
	 * <li>2. 调用{@link Fraction#subtract(NumberValue)}完成减法运算（分数运算无精度丢失）；</li>
	 * <li>3. 返回运算结果（类型为{@link Fraction}，或子类重写后返回对应类型）。</li>
	 * </ul>
	 *
	 * @param value 减数（目标数值，不可为null）
	 * @return {@link NumberValue}：减法运算结果（高精度，无精度丢失）
	 * @throws NullPointerException 若value为null（未传入减数）
	 */
	public NumberValue subtract(@NonNull NumberValue value) {
		return toFraction(this).subtract(value);
	}

	/**
	 * 受保护的工具方法：将{@link NumberValue}实例转为{@link Fraction}（分数）
	 * <p>
	 * 转换规则（避免重复创建，提升性能）：
	 * <ul>
	 * <li>1. 若输入已是{@link Fraction}实例，直接返回自身（无需重新构造）；</li>
	 * <li>2. 若输入为其他类型（如{@link IntValue}、{@link BigDecimalValue}），
	 * 通过{@link #newFraction(NumberValue, NumberValue)}创建新{@link Fraction}，
	 * 分子为输入数值，分母为{@link BigIntegerValue#ONE}（即1，确保整数转分数的正确性，如100→100/1）。</li>
	 * </ul>
	 * <p>
	 * 核心作用：统一所有数值运算的载体（基于{@link Fraction}的精确运算特性），避免不同类型运算导致的精度丢失，且支持子类扩展。
	 * <p>
	 * 访问控制：protected final（子类可调用，不可重写，确保转换逻辑统一，避免子类破坏运算一致性）。
	 *
	 * @param value 待转换的{@link NumberValue}实例（不可为null）
	 * @return 转换后的{@link Fraction}实例（非null，可直接用于后续运算）
	 * @throws NullPointerException 若value为null（未传入待转换数值）
	 * @see #newFraction(NumberValue, NumberValue) 用于创建{@link Fraction}的工厂方法（解耦构造逻辑）
	 */
	protected final Fraction toFraction(NumberValue value) {
		if (value instanceof Fraction) {
			return (Fraction) value;
		}
		return newFraction(value, BigIntegerValue.ONE);
	}

	/**
	 * 受保护的工厂方法：创建{@link Fraction}实例（解耦构造逻辑）
	 * <p>
	 * 核心作用：将{@link Fraction}的构造逻辑与运算逻辑分离，方便子类扩展（如子类需使用自定义的{@link Fraction}实现，可重写此方法），
	 * 避免在{@link #toFraction(NumberValue)}中硬编码{@link Fraction}构造，提升框架扩展性。
	 * <p>
	 * 默认实现：创建标准{@link Fraction}实例，分子为{@code molecule}，分母为{@code denominator}（需确保分母非0，否则{@link Fraction}构造会抛异常）。
	 *
	 * @param molecule    分数的分子（不可为null，数值内容无限制，可为整数或小数）
	 * @param denominator 分数的分母（不可为null，且不能为0，否则抛出{@link IllegalArgumentException}）
	 * @return 新创建的{@link Fraction}实例（非null，分子和分母已校验合法性）
	 * @throws NullPointerException 若molecule或denominator为null（未传入分子/分母）
	 * @throws IllegalArgumentException 若denominator为0（分母为零，非法分数），由{@link Fraction}构造方法抛出
	 * @see Fraction#Fraction(NumberValue, NumberValue) {@link Fraction}的构造逻辑（定义分子/分母的合法性校验）
	 */
	protected Fraction newFraction(@NonNull NumberValue molecule, @NonNull NumberValue denominator) {
		return new Fraction(molecule, denominator);
	}
}
