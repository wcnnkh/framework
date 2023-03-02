package io.basc.framework.math.gcd;

import io.basc.framework.math.NumberHolder;

import java.math.BigDecimal;
import java.math.BigInteger;

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

	NumberHolder gcd(NumberHolder m, NumberHolder n);
}
