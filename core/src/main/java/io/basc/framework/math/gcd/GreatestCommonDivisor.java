package io.basc.framework.math.gcd;

import java.math.BigDecimal;
import java.math.BigInteger;

import io.basc.framework.math.NumberValue;

/**
 * 求两数的最大公约数(Greatest Common Divisor)
 * 
 * @author wcnnkh
 *
 */
public interface GreatestCommonDivisor {

	int gcd(int m, int n);

	BigInteger gcd(BigInteger m, BigInteger n);

	BigDecimal gcd(BigDecimal m, BigDecimal n);

	NumberValue gcd(NumberValue m, NumberValue n);
}
