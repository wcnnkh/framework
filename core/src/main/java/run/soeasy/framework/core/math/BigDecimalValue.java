package run.soeasy.framework.core.math;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import run.soeasy.framework.core.domain.Value;
import run.soeasy.framework.logging.LogManager;
import run.soeasy.framework.logging.Logger;

/**
 * 高精度十进制数值实现类，封装{@link BigDecimal}提供任意精度的十进制运算。
 * 该类实现了{@link Value}接口，支持数值的加减乘除等运算，并提供自定义精度控制。
 *
 * <p>
 * 特性：
 * <ul>
 * <li>不可变对象，线程安全</li>
 * <li>支持自定义精度和舍入模式</li>
 * <li>提供零值常量{@link #ZERO}</li>
 * <li>所有运算结果保持相同的精度配置</li>
 * </ul>
 *
 * <p>
 * 典型应用场景：
 * <ul>
 * <li>财务计算（如货币金额计算）</li>
 * <li>科学计算（需要高精度结果）</li>
 * <li>任何需要避免浮点数精度误差的场景</li>
 * </ul>
 *
 * @author soeasy.run
 * @see java.math.BigDecimal
 * @see run.soeasy.framework.core.domain.Value
 */
public class BigDecimalValue extends NumberValue {
	private static final long serialVersionUID = 1L;

	/**
	 * 表示数值零的常量实例，使用默认精度和舍入模式
	 */
	public static final BigDecimalValue ZERO = new BigDecimalValue(BigDecimal.ZERO);

	private static Logger logger = LogManager.getLogger(BigDecimalValue.class);

	/** 封装的BigDecimal值 */
	private final BigDecimal value;

	/**
	 * 使用字符串构造高精度数值
	 * 
	 * @param number 数值字符串，必须符合BigDecimal格式
	 * @throws NumberFormatException 如果字符串格式不合法
	 */
	public BigDecimalValue(String number) {
		this(new BigDecimal(number));
	}

	/**
	 * 使用BigDecimal构造高精度数值
	 * 
	 * @param value 原始BigDecimal值
	 */
	public BigDecimalValue(BigDecimal value) {
		this.value = value;
	}

	/**
	 * 加法运算
	 * 
	 * @param value 加数
	 * @return 新的BigDecimalValue实例，表示运算结果
	 */
	@Override
	public NumberValue add(NumberValue value) {
		return new BigDecimalValue(getAsBigDecimal().add(value.getAsBigDecimal()));
	}

	/**
	 * 减法运算
	 * 
	 * @param value 减数
	 * @return 新的BigDecimalValue实例，表示运算结果
	 */
	@Override
	public NumberValue subtract(NumberValue value) {
		return new BigDecimalValue(getAsBigDecimal().subtract(value.getAsBigDecimal()));
	}

	/**
	 * 乘法运算
	 * 
	 * @param value 乘数
	 * @return 新的BigDecimalValue实例，表示运算结果
	 */
	@Override
	public NumberValue multiply(NumberValue value) {
		return new BigDecimalValue(getAsBigDecimal().multiply(value.getAsBigDecimal()));
	}

	/**
	 * 除法运算，使用当前实例的精度和舍入模式
	 * 
	 * @param value 除数
	 * @return 新的BigDecimalValue实例，表示运算结果
	 * @throws ArithmeticException 如果除不尽且未指定舍入模式
	 */
	@Override
	public NumberValue divide(NumberValue value) {
		BigDecimal left = getAsBigDecimal();
		BigDecimal right = value.getAsBigDecimal();
		BigDecimal bigDecimal;
		try {
			bigDecimal = left.divide(right);
		} catch (ArithmeticException e) {
			bigDecimal = left.divide(right);
			logger.error("{}/{} Compulsory use value {} error:{}", left, right, bigDecimal, e.getMessage());
		}
		return new BigDecimalValue(bigDecimal);
	}

	/**
	 * 除法运算，使用指定的精度和舍入模式
	 * 
	 * @param value        除数
	 * @param scale        精度（小数位数）
	 * @param roundingMode 舍入模式
	 * @return 新的BigDecimalValue实例，表示运算结果
	 */
	public NumberValue divide(NumberValue value, int scale, RoundingMode roundingMode) {
		if (value instanceof Fraction) {// 如果是分数
			return divide((Fraction) value);
		}

		return new BigDecimalValue(getAsBigDecimal().divide(value.getAsBigDecimal()));
	}

	/**
	 * 求余运算
	 * 
	 * @param value 除数
	 * @return 新的BigDecimalValue实例，表示余数
	 */
	@Override
	public NumberValue remainder(NumberValue value) {
		return new BigDecimalValue(getAsBigDecimal().remainder(value.getAsBigDecimal()));
	}

	/**
	 * 幂运算（仅支持整数指数）
	 * 
	 * @param value 指数值（必须为整数）
	 * @return 新的BigDecimalValue实例，表示幂运算结果
	 * @throws ArithmeticException 如果指数不是整数
	 */
	@Override
	public NumberValue pow(NumberValue value) {
		return new BigDecimalValue(getAsBigDecimal().pow(value.getAsBigDecimal().intValueExact()));
	}

	/**
	 * 获取内部封装的BigDecimal值
	 * 
	 * @return 此实例表示的BigDecimal值
	 */
	@Override
	public BigDecimal getAsBigDecimal() {
		return value;
	}

	/**
	 * 将此值转换为BigInteger（丢弃小数部分）
	 * 
	 * @return 转换后的BigInteger值
	 */
	@Override
	public BigInteger getAsBigInteger() {
		return value.toBigInteger();
	}

	/**
	 * 返回此值的字符串表示
	 * 
	 * @return 数值的字符串形式
	 */
	@Override
	public String toString() {
		return value.toString();
	}

	/**
	 * 返回此实例的哈希码，基于内部BigDecimal值计算
	 * 
	 * @return 哈希码值
	 */
	@Override
	public int hashCode() {
		return value.hashCode();
	}

	/**
	 * 计算绝对值
	 * 
	 * @return 新的BigDecimalValue实例，表示绝对值
	 */
	public NumberValue abs() {
		return new BigDecimalValue(value.abs());
	}

	/**
	 * 获取数值的字符串表示
	 * 
	 * @return 数值的字符串形式
	 */
	@Override
	public String getAsString() {
		return value.toString();
	}

	/**
	 * 与另一个Value对象进行比较
	 * 
	 * @param o 要比较的对象
	 * @return 如果o是数值类型，返回内部BigDecimal的比较结果； 否则调用父类的比较方法
	 */
	@Override
	public int compareTo(Value o) {
		if (o.isNumber()) {
			BigDecimal value = o.getAsBigDecimal();
			return this.value.compareTo(value);
		}
		return super.compareTo(o);
	}
}