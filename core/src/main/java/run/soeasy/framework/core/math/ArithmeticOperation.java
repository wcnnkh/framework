package run.soeasy.framework.core.math;

import java.math.BigDecimal;

/**
 * 常用的运算方式，按运算优先级排序
 * 
 * @author soeasy.run
 *
 */
public enum ArithmeticOperation implements Calculator {
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
	public static final Calculator[][] GROUPS = new Calculator[][] { { MULTIPLY, DIVIDE, REMAINDER },
			{ ADD, SUBTRACT } };

	private final String operator;

	ArithmeticOperation(String operator) {
		this.operator = operator;
	}

	@Override
	public String getOperator() {
		return operator;
	}

	@Override
	public BigDecimal apply(BigDecimal left, BigDecimal right) {
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
		default:
			throw new UnsupportedOperationException(this.toString());
		}
	}
}
