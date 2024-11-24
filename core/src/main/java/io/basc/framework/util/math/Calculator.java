package io.basc.framework.util.math;

import java.math.BigDecimal;

import io.basc.framework.util.Assert;

/**
 * 运算器/计算器
 * 
 * @author wcnnkh
 *
 */
public interface Calculator extends MathFunction<NumberValue, NumberValue> {

	default NumberValue eval(String left, String right) {
		Assert.requiredArgument(left != null, "left");
		Assert.requiredArgument(right != null, "right");
		return eval(new BigDecimalValue(left), new BigDecimalValue(right));
	}

	default NumberValue eval(BigDecimal left, BigDecimal right) {
		Assert.requiredArgument(left != null, "left");
		Assert.requiredArgument(right != null, "right");
		return eval(new BigDecimalValue(left), new BigDecimalValue(right));
	}

	default NumberValue eval(BigDecimal left, String right) {
		Assert.requiredArgument(left != null, "left");
		Assert.requiredArgument(right != null, "right");
		return eval(new BigDecimalValue(left), new BigDecimalValue(right));
	}

	/**
	 * 计算符
	 * 
	 * @return
	 */
	String getOperator();
}
