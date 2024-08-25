package io.basc.framework.core.number;

import io.basc.framework.convert.lang.Value;

public interface NumberValue extends Value, Comparable<NumberValue> {
	/**
	 * 加法
	 * 
	 * @param value
	 * @return
	 */
	NumberValue add(NumberValue value);

	/**
	 * 减法
	 * 
	 * @param value
	 * @return
	 */
	NumberValue subtract(NumberValue value);

	/**
	 * 乘法
	 * 
	 * @param value
	 * @return
	 */
	NumberValue multiply(NumberValue value);

	/**
	 * 除法
	 * 
	 * @param value
	 * @return
	 */
	NumberValue divide(NumberValue value);

	/**
	 * 余数
	 * 
	 * @param value
	 * @return
	 */
	NumberValue remainder(NumberValue value);

	/**
	 * 指数运算
	 * 
	 * @param value
	 * @return
	 */
	NumberValue pow(NumberValue value);

	/**
	 * 绝对值
	 * 
	 * @return
	 */
	NumberValue abs();
}
