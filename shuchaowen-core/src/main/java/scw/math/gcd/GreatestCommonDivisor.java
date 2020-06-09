package scw.math.gcd;

import java.math.BigInteger;

/**
 * 求两数的最大公约数(Greatest Common Divisor)
 * @author shuchaowen
 *
 */
public interface GreatestCommonDivisor {
	
	int gcd(int m, int n);
	
	BigInteger gcd(BigInteger m, BigInteger n);
}
