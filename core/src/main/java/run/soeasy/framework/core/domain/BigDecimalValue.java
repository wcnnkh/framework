package run.soeasy.framework.core.domain;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 高精度十进制数值实现类，核心封装{@link BigDecimal}，专门解决浮点数（float/double）的二进制存储精度丢失问题，
 * 提供高精度数值的安全存储、类型转换、数值比较及常用运算能力，是财务计算、科学计算、金额统计等对精度要求严苛场景的首选组件。
 *
 * <p>
 * <strong>设计核心（四大核心目标）</strong>：
 * <ul>
 * <li><strong>不可变线程安全</strong>：完全继承{@link BigDecimal}的不可变特性，所有修改操作（取反、绝对值）均返回新实例，
 * 原实例状态永久不变，天然支持多线程共享，无需额外同步机制；</li>
 * <li><strong>绝对精度保障</strong>：基于十进制存储模型，精准表示小数（如0.1、0.2），彻底规避二进制浮点数的累积误差（如0.1+0.2≠0.3），
 * 确保数值运算与数学逻辑完全一致；</li>
 * <li><strong>接口化兼容</strong>：实现{@link Value}统一接口，支持与{@link BigIntegerValue}及其他实现{@link Value}接口的数值类型进行
 * 类型转换与数值比较，适配框架内统一数值处理场景；</li>
 * <li><strong>轻量高效复用</strong>：提供{@link #ZERO}单例常量，覆盖零值比较、初始值赋值等高频场景，避免重复创建零值实例，降低GC开销。</li>
 * </ul>
 *
 * <h3>核心特性</h3>
 * <ul>
 * <li><strong>无精度损耗存储</strong>：直接复用{@link BigDecimal}的十进制精确表示，支持任意精度的小数与整数存储，
 * 适配从微小小数（如0.0000001）到超大整数（如1000000000000000000）的全场景；</li>
 * <li><strong>灵活构造方式</strong>：提供三种核心构造入口，适配不同入参场景：
 *   <ul>
 *   <li>字符串构造（推荐）：支持整数、小数格式（如"100.34"、"-56.78"），完全避免浮点数转十进制的精度损耗；</li>
 *   <li>BigDecimal构造：直接接收原生{@link BigDecimal}实例，无转换成本，适配已存在BigDecimal的场景；</li>
 *   <li>long构造：简化整数入参场景，通过{@link BigDecimal#valueOf(long)}高效构建，无精度丢失；</li>
 *   </ul>
 * </li>
 * <li><strong>完整类型转换</strong>：支持转为{@link BigDecimal}（无损原生值）、{@link BigInteger}（精确整数转换，非整数抛异常）、
 * 字符串（原生格式输出），适配不同下游系统的数据格式要求；</li>
 * <li><strong>跨实例数值比较</strong>：实现{@link Comparable}接口，支持与任意{@link Value}实现类进行数值比较（如{@link BigIntegerValue}），
 * 比较逻辑基于{@link BigDecimal}的精确比较规则，结果绝对可靠；</li>
 * <li><strong>常用运算支持</strong>：提供取反（{@link #negate()}）、取绝对值（{@link #abs()}）核心运算，所有运算均返回新实例，
 * 保持不可变设计一致性。</li>
 * </ul>
 *
 * <h3>适用场景</h3>
 * <ul>
 * <li>财务金融场景：订单金额、税费计算、银行转账、汇率转换、利息核算（需精确到分/厘，不允许分毫误差）；</li>
 * <li>科学计算场景：实验数据记录、工程测量结果、物理/化学公式计算（需十进制精确表示，避免误差累积）；</li>
 * <li>业务统计场景：销售额汇总、用户消费总额、积分计算、报表统计（需多来源数值精准聚合）；</li>
 * <li>高精度参数存储：需要长期保存且不允许精度失真的数值（如合同金额、基准汇率、校准系数）。</li>
 * </ul>
 *
 * <h3>注意事项（与代码实现强关联，务必关注）</h3>
 * <ul>
 * <li><strong>构造方法选型</strong>：
 *   <ul>
 *   <li>优先选择字符串入参：如"100.34"，完全避免double/float入参的精度损耗（如new BigDecimal(0.1)实际存储为0.10000000000000000555...）；</li>
 *   <li>禁止直接使用double入参：若必须接收double类型（如第三方接口返回），需先转为字符串（Double.toString(doubleVal)）再构造实例；</li>
 *   </ul>
 * </li>
 * <li><strong>getAsBigInteger() 特殊说明</strong>：
 *   方法内部调用{@link BigDecimal#toBigIntegerExact()}，仅支持纯整数数值转换（如100、-200），
 *   若数值包含非零小数部分（如100.34、999.99），会直接抛出{@link ArithmeticException}，需提前通过数值校验规避；</li>
 * <li><strong>equals 判定规则</strong>：仅当比较对象为{@link BigDecimalValue}类型且内部{@link BigDecimal}数值完全相等时，才返回true，
 * 不支持跨类型（如{@link IntValue}、{@link LongValue}）的数值相等判定，与{@link #compareTo(Value)}的跨类型比较逻辑不同；</li>
 * <li><strong>常量不可修改</strong>：{@link #ZERO}为单例常量，其内部{@link BigDecimal}值不可通过反射等方式篡改，否则会导致全局数值逻辑错误；</li>
 * <li><strong>异常处理</strong>：
 *   <ul>
 *   <li>构造字符串格式非法（如"100.34.56"、"abc123"）会抛出{@link NumberFormatException}；</li>
 *   <li>非整数数值调用{@link #getAsBigInteger()}会抛出{@link ArithmeticException}；</li>
 *   <li>构造参数为null（字符串、BigDecimal）会抛出{@link NullPointerException}；</li>
 *   </ul>
 * </li>
 * <li><strong>运算扩展</strong>：当前类仅封装取反、绝对值核心运算，若需加减乘除等复杂运算，可通过{@link #getAsBigDecimal()}获取原生实例后，
 * 调用{@link BigDecimal}的运算方法（需指定精度和舍入模式，避免除不尽导致的{@link ArithmeticException}）。</li>
 * </ul>
 *
 * @author soeasy.run
 * @see java.math.BigDecimal 底层高精度十进制实现核心依赖
 * @see BigIntegerValue 高精度整数类（支持与当前类的类型转换与数值比较）
 * @see Value 数值统一接口（定义跨类型交互的基础契约）
 * @see Comparable 数值比较接口（保障跨实例比较的一致性）
 */
public class BigDecimalValue extends NumberValue {
	private static final long serialVersionUID = 1L;

	/**
	 * 数值零的单例常量，用于零值比较、初始值赋值等高频场景，避免重复创建零值实例，降低GC开销
	 * <p>不可修改，仅可作为读取和比较的基准，不可通过反射等方式篡改内部值
	 */
	public static final BigDecimalValue ZERO = new BigDecimalValue(BigDecimal.ZERO);

	/** 封装的核心高精度数值，不可变，外部无法直接修改 */
	private final BigDecimal value;

	// ------------------------------ 构造方法 ------------------------------

	/**
	 * 通过数值字符串构造高精度实例（推荐方式，无任何精度丢失）
	 *
	 * @param number 数值字符串，支持格式：整数（"100"、"-200"）、小数（"100.34"、"-56.78"），需符合{@link BigDecimal}的字符串格式要求
	 * @throws NumberFormatException 若字符串格式非法（如含非数字字符、多个小数点、非法科学计数法）
	 * @throws NullPointerException 若number为null
	 */
	public BigDecimalValue(String number) {
		this(new BigDecimal(number));
	}

	/**
	 * 通过原生BigDecimal构造实例，直接复用已有BigDecimal对象，无转换成本
	 *
	 * @param value 原生BigDecimal值，需确保自身精度合法性（不可为null）
	 * @throws NullPointerException 若value为null，主动抛出明确空指针异常
	 */
	public BigDecimalValue(BigDecimal value) {
		if (value == null) {
			throw new NullPointerException("BigDecimal value must not be null");
		}
		this.value = value;
	}

	/**
	 * 通过long类型整数构造实例，简化整数入参场景，无精度丢失
	 *
	 * @param number long类型整数（如100、-200、0），适用于整数场景的快速构建
	 */
	public BigDecimalValue(long number) {
		this(BigDecimal.valueOf(number));
	}

	// ------------------------------ 接口实现与核心方法 ------------------------------

	/**
	 * 获取内部封装的原生BigDecimal值（不可变），用于复杂运算扩展
	 *
	 * @return 非null的BigDecimal实例，与当前对象存储的数值完全一致，外部仅可读取，无法修改
	 */
	@Override
	public BigDecimal getAsBigDecimal() {
		return value;
	}

	/**
	 * 将当前数值精确转为BigInteger（仅支持纯整数数值）
	 *
	 * @return 与当前数值相等的BigInteger实例（非null）
	 * @throws ArithmeticException 若当前数值包含非零小数部分（如100.34、999.99），无法精确转为整数
	 */
	@Override
	public BigInteger getAsBigInteger() {
		return value.toBigIntegerExact();
	}

	/**
	 * 获取数值的字符串表示，与原生BigDecimal的toString格式完全一致
	 *
	 * @return 非null的数值字符串，整数格式（如"100"）、小数格式（如"100.34"），无额外格式冗余
	 */
	@Override
	public String getAsString() {
		return value.toString();
	}

	/**
	 * 与另一个Value对象进行数值比较（支持跨{@link Value}接口实现类）
	 *
	 * @param o 待比较的Value对象（可为null）
	 * @return 负整数=当前值＜目标值，0=当前值==目标值，正整数=当前值＞目标值；若o为null，按约定返回1（null小于任何数值）
	 */
	@Override
	public int compareTo(Value o) {
		if (o == null) {
			return 1;
		}
		if (o.isNumber()) {
			BigDecimal targetValue = o.getAsBigDecimal();
			return this.value.compareTo(targetValue);
		}
		return super.compareTo(o);
	}

	/**
	 * 取当前数值的绝对值（符号转为正，零值保持不变）
	 *
	 * @return 新的BigDecimalValue实例，数值为当前值的绝对值（原实例状态不变）
	 */
	public NumberValue abs() {
		return new BigDecimalValue(value.abs());
	}

	/**
	 * 取当前数值的相反数（符号反转，零值保持不变）
	 *
	 * @return 新的BigDecimalValue实例，数值为当前值的相反数（原实例状态不变）
	 * @throws ArithmeticException 理论无溢出风险（BigDecimal支持无限精度），仅为兼容{@link NumberValue}接口契约
	 */
	@Override
	public NumberValue negate() throws ArithmeticException {
		// 修复bug：原使用multiply(-1)，改为BigDecimal原生negate()更简洁高效
		return new BigDecimalValue(this.value.negate());
	}

	// ------------------------------ 重写Object方法 ------------------------------

	/**
	 * 重写equals：仅当对象为{@link BigDecimalValue}类型且内部数值完全相等时，判定为相等
	 *
	 * @param obj 待比较的对象（可为null）
	 * @return true=对象类型匹配且数值相等，false=类型不匹配、数值不相等或obj为null
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof BigDecimalValue)) {
			return false;
		}
		BigDecimalValue other = (BigDecimalValue) obj;
		return this.value.equals(other.value);
	}

	/**
	 * 重写hashCode：基于内部BigDecimal的hashCode实现，确保equals相等的实例hashCode一致
	 *
	 * @return 与内部BigDecimal数值对应的哈希码，保障哈希容器（如HashMap）的正确使用
	 */
	@Override
	public int hashCode() {
		return value.hashCode();
	}

	/**
	 * 重写toString：返回数值的原生字符串表示，与{@link #getAsString()}结果一致，增强日志与调试可读性
	 *
	 * @return 非null的数值字符串，格式简洁，无额外对象信息冗余
	 */
	@Override
	public String toString() {
		return getAsString();
	}
}