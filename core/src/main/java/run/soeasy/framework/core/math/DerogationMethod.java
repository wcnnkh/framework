package run.soeasy.framework.core.math;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 更相减损法
 * 
 * @author soeasy.run
 *
 */
class DerogationMethod implements GreatestCommonDivisor {

	public int apply(int m, int n) {
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

	@Override
	public long apply(long m, long n) {
		long a = Math.abs(m);
		long b = Math.abs(n);
		while (a != b) {
			if (a > b)
				a -= b;
			else
				b -= a;
		}
		return a;
	}

	public BigInteger apply(BigInteger m, BigInteger n) {
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

	public BigDecimal apply(BigDecimal m, BigDecimal n) {
		BigDecimal a = m.abs();
		BigDecimal b = n.abs();
		while (!a.equals(b)) {
			if (a.compareTo(b) > 0)
				a = a.subtract(b);
			else
				b = b.subtract(a);
		}
		return a;
	}
}
