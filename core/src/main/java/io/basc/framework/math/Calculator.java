package io.basc.framework.math;

import io.basc.framework.core.Assert;

import java.math.BigDecimal;

/**
 * 运算器/计算器
 * @author shuchaowen
 *
 */
public interface Calculator {

	default NumberHolder calculate(String left, String right) {
		Assert.requiredArgument(left != null, "left");
		Assert.requiredArgument(right != null, "right");
		return calculate(new BigDecimalHolder(left), new BigDecimalHolder(right));
	}

	default NumberHolder calculate(BigDecimal left, BigDecimal right) {
		Assert.requiredArgument(left != null, "left");
		Assert.requiredArgument(right != null, "right");
		return calculate(new BigDecimalHolder(left), new BigDecimalHolder(right));
	}
	
	default NumberHolder calculate(BigDecimal left, String right) {
		Assert.requiredArgument(left != null, "left");
		Assert.requiredArgument(right != null, "right");
		return calculate(new BigDecimalHolder(left), new BigDecimalHolder(right));
	}

	/**
	 * 计算符<br/>
	 * 
	 * @return
	 */
	String getOperator();

	NumberHolder calculate(NumberHolder left, NumberHolder right);
}
