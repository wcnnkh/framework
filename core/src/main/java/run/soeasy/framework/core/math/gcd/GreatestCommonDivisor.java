package run.soeasy.framework.core.math.gcd;

import java.math.BigDecimal;
import java.math.BigInteger;

import run.soeasy.framework.core.math.Calculator;

/**
 * 求两数的最大公约数(Greatest Common Divisor)
 * 
 * @author soeasy.run
 *
 */
public interface GreatestCommonDivisor extends Calculator {
	static final String GCD_OPERATOR = "GCD";

	@Override
	default String getOperator() {
		return GCD_OPERATOR;
	}

	int gcd(int m, int n);

	long gcd(long m, long n);

	BigInteger gcd(BigInteger m, BigInteger n);

	BigDecimal gcd(BigDecimal m, BigDecimal n);
}
