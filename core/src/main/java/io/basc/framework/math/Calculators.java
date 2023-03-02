package io.basc.framework.math;

import io.basc.framework.lang.UnsupportedException;

/**
 * 常用的运算方式，按运算优先级排序
 * 
 * @author wcnnkh
 *
 */
public enum Calculators implements Calculator {
	/**
	 * 指数运算
	 */
	POW("^"),
	/**
	 * 乘法
	 */
	MULTIPLY("*"),
	/**
	 * 除法
	 */
	DIVIDE("/"),
	/**
	 * 取余
	 */
	REMAINDER("%"),
	/**
	 * 加法
	 */
	ADD("+"),
	/**
	 * 减法
	 */
	SUBTRACT("-");

	/**
	 * 按运算优先级将运算器分组
	 */
	public static final Calculator[][] GROUPS = new Calculator[][] { { POW }, { MULTIPLY, DIVIDE, REMAINDER },
			{ ADD, SUBTRACT } };

	private final String operator;

	Calculators(String operator) {
		this.operator = operator;
	}

	@Override
	public String getOperator() {
		return operator;
	}

	@Override
	public NumberHolder eval(NumberHolder left, NumberHolder right) {
		switch (this) {
		case ADD:
			return left.add(right);
		case SUBTRACT:
			return left.subtract(right);
		case MULTIPLY:
			return left.multiply(right);
		case DIVIDE:
			return left.divide(right);
		case REMAINDER:
			return left.remainder(right);
		case POW:
			return left.pow(right);
		default:
			throw new UnsupportedException(this.toString());
		}
	}
}
