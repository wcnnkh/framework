package io.basc.framework.math;

import java.math.BigDecimal;

import io.basc.framework.util.Assert;

/**
 * 运算器/计算器
 * 
 * @author wcnnkh
 *
 */
public interface Calculator extends MathFunction<NumberHolder, NumberHolder> {

	default NumberHolder eval(String left, String right) {
		Assert.requiredArgument(left != null, "left");
		Assert.requiredArgument(right != null, "right");
		return eval(new BigDecimalHolder(left), new BigDecimalHolder(right));
	}

	default NumberHolder eval(BigDecimal left, BigDecimal right) {
		Assert.requiredArgument(left != null, "left");
		Assert.requiredArgument(right != null, "right");
		return eval(new BigDecimalHolder(left), new BigDecimalHolder(right));
	}

	default NumberHolder eval(BigDecimal left, String right) {
		Assert.requiredArgument(left != null, "left");
		Assert.requiredArgument(right != null, "right");
		return eval(new BigDecimalHolder(left), new BigDecimalHolder(right));
	}

	/**
	 * 计算符
	 * 
	 * @return
	 */
	String getOperator();
}
