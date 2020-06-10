package scw.math.gcd;

import java.math.BigDecimal;
import java.math.BigInteger;

import scw.math.BigIntegerHolder;
import scw.math.NumberHolder;

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

	public BigDecimal gcd(BigDecimal m, BigDecimal n) {
		BigDecimal a = m.abs();
		BigDecimal b = n.abs();
		while (true) {
			if ((a = a.remainder(b)).equals(BigDecimal.ZERO))
				return b;
			if ((b = b.remainder(a)).equals(BigDecimal.ZERO))
				return a;
		}
	}

	public NumberHolder gcd(NumberHolder m, NumberHolder n) {
		NumberHolder a = m.abs();
		NumberHolder b = n.abs();
		while (true) {
			if ((a = a.remainder(b)).equals(BigIntegerHolder.ZERO))
				return b;
			if ((b = b.remainder(a)).equals(BigIntegerHolder.ZERO))
				return a;
		}
	}

}
