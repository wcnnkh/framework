package scw.math.gcd;

import java.math.BigInteger;

/**
 * 更相减损法
 * 
 * @author shuchaowen
 *
 */
public class DerogationMethod implements GreatestCommonDivisor {

	public int gcd(int m, int n) {
		int a = Math.abs(m);
		int b = Math.abs(n);
		while (a != b) {
			if (a > b)
				a -= b;
			else
				b -= a;
		}
		return a;
	}

	public BigInteger gcd(BigInteger m, BigInteger n) {
		BigInteger a = m.abs();
		BigInteger b = n.abs();
		while (!a.equals(b)) {
			if (a.compareTo(b) > 0)
				a = a.subtract(b);
			else
				b = b.subtract(a);
		}
		return a;
	}

}
