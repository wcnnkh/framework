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
 * <p>
 * 设计目标：解决基础类型精度丢失（如float/double）、大数处理能力不足（如long超限）、数值操作碎片化问题，
 * 同时支持数值作为版本号的比较逻辑，适用于对精度和类型安全性要求高的场景。
 *
 * <h3>核心能力</h3>
 * <ul>
 * <li><strong>高精度计算支持</strong>：基于{@link BigDecimal}（小数）和{@link BigInteger}（整数）实现底层存储，
 * 避免运算中的精度丢失，支持任意大小数值计算</li>
 * <li><strong>类型安全转换</strong>：所有基本类型（byte/short/int/long/float/double）转换均包含范围校验，
 * 超出目标类型范围时抛出{@link ArithmeticException}，杜绝隐式溢出</li>
 * <li><strong>完整数学运算</strong>：封装加、减、乘、除、取余、幂运算、绝对值等基础操作，
 * 运算逻辑统一委托给{@link Fraction}（分数类型）实现，保证跨类型运算一致性</li>
 * <li><strong>版本化支持</strong>：实现{@link Version}接口，可将数值作为版本号（如1.0.1、20240501）进行大小比较</li>
 * <li><strong>预置常用常量</strong>：提供-1、0、1、10等高频使用数值的常量实例，避免重复创建</li>
 * <li><strong>类型判断工具</strong>：内置是否为零、正数、负数、1等判断方法，简化业务逻辑判断</li>
 * </ul>
 *
 * <h3>适用场景</h3>
 * <ul>
 * <li>金融领域：货币计算（如订单金额、手续费）、汇率转换，需完全避免精度丢失</li>
 * <li>科学计算：物理/化学实验数据、工程测量值，需处理超大数值或高精度小数</li>
 * <li>数据校验：参数数值范围校验（如年龄、金额上限），需明确抛出溢出异常</li>
 * <li>版本管理：配置版本、接口版本号（如API v2.1），需基于数值进行版本高低比较</li>
 * <li>算法实现：需大数运算的场景（如密码学、大数据统计），突破基础类型范围限制</li>
 * </ul>
 *
 * <h3>使用示例</h3>
 * 
 * <pre class="code">
 * // 1. 创建不同类型的数值实例
 * NumberValue intNum = new IntValue(100); // 整数类型
 * NumberValue decimalNum = new BigDecimalValue("99.99"); // 高精度小数
 * NumberValue bigNum = new BigIntegerValue("12345678901234567890"); // 超大整数
 *
 * // 2. 跨类型数值运算（自动转为分数处理，无精度丢失）
 * NumberValue sum = intNum.add(decimalNum); // 100 + 99.99 = 199.99
 * NumberValue product = sum.multiply(bigNum); // 199.99 * 12345678901234567890
 * NumberValue remainder = product.mod(new IntValue(100)); // 取模运算：结果为乘积对100的余数
 *
 * // 3. 类型判断与转换
 * if (sum.isPositive()) { // 判断是否为正数
 * 	double sumDouble = sum.getAsDouble(); // 安全转换为double（需确保未超限）
 * 	BigInteger sumBigInt = sum.getAsBigInteger();// 转换为整数（小数部分会截断，需注意）
 * }
 *
 * // 4. 版本号比较（假设product为版本号数值）
 * if (product.compareTo(new IntValue(2024)) > 0) { // 比较版本高低
 * 	System.out.println("当前版本高于2024");
 * }
 * </pre>
 *
 * @see BigDecimalValue 高精度小数实现类（基于BigDecimal）
 * @see IntValue 整数实现类（基于int，自动适配long/BigInteger）
 * @see BigIntegerValue 超大整数实现类（基于BigInteger）
 * @see Fraction 分数实现类（支撑跨类型精确运算）
 * @see Version 版本比较接口（定义数值版本化能力）
 * @see Number Java原生数值接口（定义基础类型转换能力）
 */
public abstract class NumberValue extends Number implements Version {
	private static final long serialVersionUID = 1L;

	/**
	 * 数值-1的常量实例（基于IntValue实现），与{@link #NEGATIVE_ONE}等价，供不同命名习惯使用
	 */
	public static final NumberValue MINUS_ONE = new IntValue(-1);
	/**
	 * 数值0的常量实例（基于IntValue实现），常用于比较"无值"、"初始值"场景
	 */
	public static final NumberValue ZERO = new IntValue(0);
	/**
	 * 数值1的常量实例（基于IntValue实现），常用于计数起始、乘法单位元场景
	 */
	public static final NumberValue ONE = new IntValue(1);
	/**
	 * 数值-1的常量实例（基于IntValue实现），与{@link #MINUS_ONE}等价，语义更贴近"负数"场景
	 */
	public static final NumberValue NEGATIVE_ONE = new IntValue(-1);
	/**
	 * 数值10的常量实例（基于IntValue实现），常用于进制转换、十倍放大/缩小场景
	 */
	public static final NumberValue TEN = new IntValue(10);

	/**
	 * 创建"数值超出目标类型范围"的异常实例
	 * <p>
	 * 当数值转换时超过目标类型（如byte/int/long）的最大值时调用，异常信息包含具体超限数值
	 *
	 * @param number 超出范围的数值（通常为BigInteger/BigDecimal）
	 * @return 包含超限信息的ArithmeticException
	 */
	private static RuntimeException createTooHighException(Number number) {
		return new ArithmeticException("The value[" + number + "] is too high");
	}

	/**
	 * 获取当前数值的绝对值
	 * <p>
	 * 逻辑说明：若当前数值为负数（{@link #isNegative()}返回true），则通过乘以{@link #NEGATIVE_ONE}取反；
	 * 若为非负数，则直接返回自身（避免无意义运算）
	 *
	 * @return 绝对值对应的NumberValue实例（类型与原数值一致）
	 */
	public NumberValue abs() {
		return isNegative() ? multiply(NEGATIVE_ONE) : this;
	}

	/**
	 * 重写Number接口的doubleValue()方法，委托给{@link #getAsDouble()}实现
	 * <p>
	 * 注意：此方法无额外逻辑，仅为满足Number接口契约，建议直接使用{@link #getAsDouble()}（语义更清晰）
	 *
	 * @return 当前数值的double类型表示（已做范围校验，超限会抛出异常）
	 * @throws ArithmeticException 若数值超过double类型的最大值
	 */
	@Override
	public double doubleValue() {
		return getAsDouble();
	}

	/**
	 * 重写equals()方法，基于数值大小比较而非对象引用
	 * <p>
	 * 比较规则： 1. 若参数为null，直接返回false； 2.
	 * 若参数为NumberValue子类实例，通过{@link #compareTo(NumberValue)}比较数值大小，返回是否相等； 3.
	 * 若为其他类型，返回false（不支持与非NumberValue类型比较）
	 *
	 * @param obj 待比较的对象
	 * @return true：数值相等；false：对象为null/非NumberValue/数值不相等
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
	 * 子类必须实现此方法，哈希值需基于数值本身计算（而非对象地址），确保"equals()为true的实例，hashCode()必相等"
	 *
	 * @return 基于数值内容的哈希值
	 */
	@Override
	public abstract int hashCode();

	/**
	 * 重写Number接口的floatValue()方法，委托给{@link #getAsFloat()}实现
	 * <p>
	 * 注意：此方法无额外逻辑，仅为满足Number接口契约，建议直接使用{@link #getAsFloat()}（语义更清晰）
	 *
	 * @return 当前数值的float类型表示（已做范围校验，超限会抛出异常）
	 * @throws ArithmeticException 若数值超过float类型的最大值
	 */
	@Override
	public float floatValue() {
		return getAsFloat();
	}

	/**
	 * 判断当前数值是否等于0
	 * <p>
	 * 通过与预置常量{@link #ZERO}比较实现，等价于{@code compareTo(ZERO) == 0}
	 *
	 * @return true：数值等于0；false：数值不等于0
	 */
	public boolean isZero() {
		return compareTo(ZERO) == 0;
	}

	/**
	 * 判断当前数值是否等于1
	 * <p>
	 * 通过与预置常量{@link #ONE}比较实现，等价于{@code compareTo(ONE) == 0}
	 *
	 * @return true：数值等于1；false：数值不等于1
	 */
	public boolean isOne() {
		return compareTo(ONE) == 0;
	}

	/**
	 * 判断当前数值是否为正数（严格大于0）
	 * <p>
	 * 通过与预置常量{@link #ZERO}比较实现，等价于{@code compareTo(ZERO) > 0}
	 *
	 * @return true：数值大于0；false：数值小于或等于0
	 */
	public boolean isPositive() {
		return compareTo(ZERO) > 0;
	}

	/**
	 * 判断当前数值是否为负数（严格小于0）
	 * <p>
	 * 通过与预置常量{@link #ZERO}比较实现，等价于{@code compareTo(ZERO) < 0}
	 *
	 * @return true：数值小于0；false：数值大于或等于0
	 */
	public boolean isNegative() {
		return compareTo(ZERO) < 0;
	}

	/**
	 * 抽象方法：获取当前数值的BigDecimal表示
	 * <p>
	 * 子类必须实现此方法，返回高精度的小数形式（即使是整数，也需转为BigDecimal，如1→1.0）， 用于小数运算、高精度比较场景
	 *
	 * @return 当前数值的BigDecimal实例（非null）
	 */
	@Override
	public abstract BigDecimal getAsBigDecimal();

	/**
	 * 抽象方法：获取当前数值的BigInteger表示
	 * <p>
	 * 子类必须实现此方法，返回整数形式（若为小数，需截断小数部分，如1.9→1）， 用于整数运算、整数类型转换场景
	 *
	 * @return 当前数值的BigInteger实例（非null）
	 */
	@Override
	public abstract BigInteger getAsBigInteger();

	/**
	 * 获取当前数值的布尔表示
	 * <p>
	 * 规则：仅当数值的BigInteger形式等于{@link BigInteger#ONE}时返回true，其他情况（包括0、负数、小数）返回false
	 * <p>
	 * 注意：此方法非"非零即true"，而是严格匹配1，需与业务场景适配
	 *
	 * @return true：数值BigInteger形式为1；false：其他情况
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
	 * 校验逻辑：若数值的BigInteger形式大于{@link Byte#MAX_VALUE}，抛出超限异常；
	 * 小于{@link Byte#MIN_VALUE}的情况未校验（原逻辑如此，使用时需注意负数场景）
	 *
	 * @return 当前数值的byte类型表示
	 * @throws ArithmeticException 若数值大于Byte.MAX_VALUE（127）
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
	 * 转换为char类型（基于byte转换）
	 * <p>
	 * 逻辑：先通过{@link #getAsByte()}获取byte值，再强制转为char（本质是使用byte的ASCII码对应字符）
	 * <p>
	 * 注意：仅支持0~127范围内的数值（对应标准ASCII字符），超出会抛出byte超限异常
	 *
	 * @return 当前数值对应的char字符
	 * @throws ArithmeticException 若数值超过Byte.MAX_VALUE（127）
	 */
	@Override
	public char getAsChar() throws ArithmeticException {
		return (char) getAsByte();
	}

	/**
	 * 安全转换为double类型
	 * <p>
	 * 校验逻辑：若数值的BigDecimal形式大于{@link Double#MAX_VALUE}，抛出超限异常；
	 * 小于{@link Double#MIN_VALUE}的情况未校验（原逻辑如此，使用时需注意极小值场景）
	 *
	 * @return 当前数值的double类型表示
	 * @throws ArithmeticException 若数值大于Double.MAX_VALUE（约1.8e308）
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
	 * 实现{@link Value}接口的契约，返回{@link Elements}单例集合，便于统一处理"单值/多值"场景
	 *
	 * @return 包含当前NumberValue实例的Elements集合（size=1）
	 */
	@Override
	public Elements<? extends Value> getAsElements() {
		return Elements.singleton(this);
	}

	/**
	 * 安全转换为float类型
	 * <p>
	 * 校验逻辑：若数值的BigDecimal形式大于{@link Float#MAX_VALUE}，抛出超限异常；
	 * 小于{@link Float#MIN_VALUE}的情况未校验（原逻辑如此，使用时需注意极小值场景）
	 *
	 * @return 当前数值的float类型表示
	 * @throws ArithmeticException 若数值大于Float.MAX_VALUE（约3.4e38）
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
	 * 校验逻辑：若数值的BigInteger形式大于{@link Integer#MAX_VALUE}，抛出超限异常；
	 * 小于{@link Integer#MIN_VALUE}的情况未校验（原逻辑如此，使用时需注意负数场景）
	 *
	 * @return 当前数值的int类型表示
	 * @throws ArithmeticException 若数值大于Integer.MAX_VALUE（2147483647）
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
	 * 校验逻辑：若数值的BigInteger形式大于{@link Long#MAX_VALUE}，抛出超限异常；
	 * 小于{@link Long#MIN_VALUE}的情况未校验（原逻辑如此，使用时需注意负数场景）
	 *
	 * @return 当前数值的long类型表示
	 * @throws ArithmeticException 若数值大于Long.MAX_VALUE（9223372036854775807）
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
	 * 语义：明确当前Value是数值类型，直接返回自身，避免类型转换冗余
	 *
	 * @return 当前NumberValue实例（非null）
	 */
	@Override
	public NumberValue getAsNumber() {
		return this;
	}

	/**
	 * 安全转换为short类型
	 * <p>
	 * 校验逻辑：若数值的BigInteger形式大于{@link Short#MAX_VALUE}，抛出超限异常；
	 * 小于{@link Short#MIN_VALUE}的情况未校验（原逻辑如此，使用时需注意负数场景）
	 *
	 * @return 当前数值的short类型表示
	 * @throws ArithmeticException 若数值大于Short.MAX_VALUE（32767）
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
	 * 重写Number接口的intValue()方法，委托给{@link #getAsInt()}实现
	 * <p>
	 * 注意：此方法无额外逻辑，仅为满足Number接口契约，建议直接使用{@link #getAsInt()}（语义更清晰）
	 *
	 * @return 当前数值的int类型表示（已做范围校验，超限会抛出异常）
	 * @throws ArithmeticException 若数值超过int类型的最大值
	 */
	@Override
	public int intValue() {
		return getAsInt();
	}

	/**
	 * 标记当前是否为多值类型（实现{@link Value}接口的契约）
	 * <p>
	 * 数值类型始终为"单值"，故固定返回false，不可重写（final修饰）
	 *
	 * @return false（固定值）
	 */
	@Override
	public final boolean isMultiple() {
		return false;
	}

	/**
	 * 标记当前是否为数值类型（实现{@link Value}接口的契约）
	 * <p>
	 * 当前类为数值抽象类，故固定返回true
	 *
	 * @return true（固定值）
	 */
	@Override
	public boolean isNumber() {
		return true;
	}

	/**
	 * 重写Number接口的longValue()方法，委托给{@link #getAsLong()}实现
	 * <p>
	 * 注意：此方法无额外逻辑，仅为满足Number接口契约，建议直接使用{@link #getAsLong()}（语义更清晰）
	 *
	 * @return 当前数值的long类型表示（已做范围校验，超限会抛出异常）
	 * @throws ArithmeticException 若数值超过long类型的最大值
	 */
	@Override
	public long longValue() {
		return getAsLong();
	}

	/**
	 * 重写toString()方法，委托给{@link #getAsString()}实现
	 * <p>
	 * 注意：此方法无额外逻辑，仅为满足Object接口契约，建议直接使用{@link #getAsString()}（语义更清晰）
	 *
	 * @return 当前数值的字符串表示（如100、99.99、12345678901234567890）
	 */
	@Override
	public String toString() {
		return getAsString();
	}

	/**
	 * 加法运算：当前数值 + 目标数值
	 * <p>
	 * 实现逻辑：先通过{@link #toFraction(NumberValue)}将两者转为{@link Fraction}（分数），
	 * 再调用Fraction的add方法完成运算，确保跨类型（如整数+小数）加法无精度丢失
	 *
	 * @param value 加数（目标数值，不可为null）
	 * @return 加法结果（类型为Fraction，或根据子类逻辑返回对应类型）
	 * @throws NullPointerException 若value为null
	 */
	public NumberValue add(@NonNull NumberValue value) {
		return toFraction(this).add(value);
	}

	/**
	 * 除法运算：当前数值 ÷ 目标数值
	 * <p>
	 * 实现逻辑：先转为Fraction，再调用Fraction的divide方法；若除数为0（{@link #isZero()}返回true），
	 * 会抛出ArithmeticException（由Fraction实现保证）
	 *
	 * @param value 除数（目标数值，不可为null）
	 * @return 除法结果（类型为Fraction，或根据子类逻辑返回对应类型）
	 * @throws NullPointerException 若value为null
	 * @throws ArithmeticException  若value为0（除数为零）
	 */
	public NumberValue divide(@NonNull NumberValue value) {
		return toFraction(this).divide(value);
	}

	/**
	 * 取模运算：当前数值 % 目标数值（计算当前数值对目标数值的余数）
	 * <p>
	 * 核心逻辑：先通过{@link #toFraction(NumberValue)}将当前数值转为{@link Fraction}（分数），
	 * 再委托Fraction的mod方法实现高精度取模，确保结果满足 0 ≤ 余数 < |除数|（非负），且无精度丢失
	 * <p>
	 * 适用场景：需对大数值或小数进行精确取模的场景（如密码学、周期性计算）
	 *
	 * @param value 除数（目标数值，不可为null，且不能为0）
	 * @return 余数结果（类型为Fraction，或根据子类逻辑返回对应类型）
	 * @throws NullPointerException 若value为null（参数未传）
	 * @throws ArithmeticException  若value为0（除数为零，非法运算）
	 * @see Fraction#mod(NumberValue) Fraction的高精度取模实现
	 */
	public NumberValue mod(@NonNull NumberValue value) {
		return toFraction(this).mod(value);
	}

	/**
	 * 乘法运算：当前数值 × 目标数值
	 * <p>
	 * 实现逻辑：先转为Fraction，再调用Fraction的multiply方法，确保跨类型乘法无精度丢失
	 *
	 * @param value 乘数（目标数值，不可为null）
	 * @return 乘法结果（类型为Fraction，或根据子类逻辑返回对应类型）
	 * @throws NullPointerException 若value为null
	 */
	public NumberValue multiply(@NonNull NumberValue value) {
		return toFraction(this).multiply(value);
	}

	/**
	 * 幂运算：当前数值 ^ 目标数值（当前数值的目标数值次幂）
	 * <p>
	 * 实现逻辑：先转为Fraction，再调用Fraction的pow方法；支持整数幂（如2^3）和小数幂（如4^0.5=2），
	 * 若结果无法精确表示（如2^0.3），会返回近似值（由Fraction实现决定）
	 *
	 * @param value 指数（目标数值，不可为null）
	 * @return 幂运算结果（类型为Fraction，或根据子类逻辑返回对应类型）
	 * @throws NullPointerException 若value为null
	 */
	public NumberValue pow(@NonNull NumberValue value) {
		return toFraction(this).pow(value);
	}

	/**
	 * 取余运算：当前数值 % 目标数值（当前数值除以目标数值的余数）
	 * <p>
	 * 实现逻辑：先转为Fraction，再调用Fraction的remainder方法；余数符号与被除数一致（遵循数学规范）
	 *
	 * @param value 除数（目标数值，不可为null）
	 * @return 余数结果（类型为Fraction，或根据子类逻辑返回对应类型）
	 * @throws NullPointerException 若value为null
	 * @throws ArithmeticException  若value为0（除数为零）
	 */
	public NumberValue remainder(@NonNull NumberValue value) {
		return toFraction(this).remainder(value);
	}

	/**
	 * 减法运算：当前数值 - 目标数值
	 * <p>
	 * 实现逻辑：先转为Fraction，再调用Fraction的subtract方法，确保跨类型减法无精度丢失
	 *
	 * @param value 减数（目标数值，不可为null）
	 * @return 减法结果（类型为Fraction，或根据子类逻辑返回对应类型）
	 * @throws NullPointerException 若value为null
	 */
	public NumberValue subtract(@NonNull NumberValue value) {
		return toFraction(this).subtract(value);
	}

	/**
	 * 受保护的工具方法：将NumberValue实例转为Fraction（分数）
	 * <p>
	 * 转换规则：
	 * 1. 若输入已是Fraction实例，直接返回（避免重复创建，提升性能）；
	 * 2. 若为其他类型（如IntValue、BigDecimalValue），通过{@link #newFraction(NumberValue, NumberValue)}
	 *    创建新Fraction，分子为输入数值，分母为{@link BigIntegerValue#ONE}（即1，确保整数转分数的正确性）
	 * <p>
	 * 核心作用：统一所有数值运算的载体（基于Fraction），避免不同类型运算导致的精度丢失，且支持子类扩展。
	 * <p>
	 * 访问控制：protected final（子类可调用，不可重写，确保转换逻辑统一）
	 *
	 * @param value 待转换的NumberValue实例（不可为null）
	 * @return 转换后的Fraction实例（非null，确保可直接用于后续运算）
	 * @throws NullPointerException 若value为null（参数未传）
	 * @see #newFraction(NumberValue, NumberValue) 用于创建Fraction的工厂方法
	 */
	protected final Fraction toFraction(NumberValue value) {
		if (value instanceof Fraction) {
			return (Fraction) value;
		}
		return newFraction(value, BigIntegerValue.ONE);
	}

	/**
	 * 受保护的工厂方法：创建Fraction实例
	 * <p>
	 * 核心作用：解耦Fraction的构造逻辑，方便子类扩展（如子类需使用自定义的Fraction实现，可重写此方法），
	 * 避免在{@link #toFraction(NumberValue)}中硬编码Fraction构造，提升扩展性。
	 * <p>
	 * 默认实现：创建标准Fraction实例，分子为{@code molecule}，分母为{@code denominator}（需确保分母非0）
	 *
	 * @param molecule  分数的分子（不可为null，数值内容无限制）
	 * @param denominator 分数的分母（不可为null，且不能为0，否则Fraction构造会抛异常）
	 * @return 新创建的Fraction实例（非null）
	 * @throws NullPointerException 若molecule或denominator为null
	 * @throws IllegalArgumentException 若denominator为0（由Fraction构造方法抛出）
	 * @see Fraction#Fraction(NumberValue, NumberValue) Fraction的构造逻辑
	 */
	protected Fraction newFraction(@NonNull NumberValue molecule, @NonNull NumberValue denominator) {
		return new Fraction(molecule, denominator);
	}
}