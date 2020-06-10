package scw.math.gcd;

import java.math.BigDecimal;
import java.math.BigInteger;

import scw.math.NumberHolder;

/**
 * 求两数的最大公约数(Greatest Common Divisor)
 * @author shuchaowen
 *
 */
public interface GreatestCommonDivisor {
	
	int gcd(int m, int n);
	
	BigInteger gcd(BigInteger m, BigInteger n);
	
	BigDecimal gcd(BigDecimal m, BigDecimal n);
	
	NumberHolder gcd(NumberHolder m, NumberHolder n);
}
