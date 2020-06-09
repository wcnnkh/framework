package scw.math.gcd;

import java.math.BigInteger;

/**
 * 辗转相除法
 * 
 * @author shuchaowen
 *
 */
public class DivisionAlgorithm implements GreatestCommonDivisor {

	public int gcd(int m, int n) {
		int a = Math.abs(m);
		int b = Math.abs(n);
		while (true) {
			if ((a = a % b) == 0)
				return b;
			if ((b = b % a) == 0)
				return a;
		}
	}

	public BigInteger gcd(BigInteger m, BigInteger n) {
		BigInteger a = m.abs();
		BigInteger b = n.abs();
		while (true) {
			if ((a = a.remainder(b)).equals(BigInteger.ZERO))
				return b;
			if ((b = b.remainder(a)).equals(BigInteger.ZERO))
				return a;
		}
	}

}
