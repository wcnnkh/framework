package run.soeasy.framework.core.math;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 辗转相除法
 * 
 * @author soeasy.run
 *
 */
class DivisionAlgorithm implements GreatestCommonDivisor {
	public int apply(int m, int n) {
		int a = Math.abs(m);
		int b = Math.abs(n);
		while (true) {
			if ((a = a % b) == 0)
				return b;
			if ((b = b % a) == 0)
				return a;
		}
	}

	@Override
	public long apply(long m, long n) {
		long a = Math.abs(m);
		long b = Math.abs(n);
		while (true) {
			if ((a = a % b) == 0)
				return b;
			if ((b = b % a) == 0)
				return a;
		}
	}

	public BigInteger apply(BigInteger m, BigInteger n) {
		BigInteger a = m.abs();
		BigInteger b = n.abs();
		while (true) {
			if ((a = a.remainder(b)).equals(BigInteger.ZERO))
				return b;
			if ((b = b.remainder(a)).equals(BigInteger.ZERO))
				return a;
		}
	}

	public BigDecimal apply(BigDecimal m, BigDecimal n) {
		BigDecimal a = m.abs();
		BigDecimal b = n.abs();
		while (true) {
			if ((a = a.remainder(b)).equals(BigDecimal.ZERO))
				return b;
			if ((b = b.remainder(a)).equals(BigDecimal.ZERO))
				return a;
		}
	}
}
