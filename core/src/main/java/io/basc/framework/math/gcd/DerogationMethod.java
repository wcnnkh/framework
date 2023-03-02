package io.basc.framework.math.gcd;

import io.basc.framework.math.NumberHolder;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 更相减损法
 * 
 * @author wcnnkh
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

	public BigDecimal gcd(BigDecimal m, BigDecimal n) {
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

	public NumberHolder gcd(NumberHolder m, NumberHolder n) {
		NumberHolder a = m.abs();
		NumberHolder b = n.abs();
		while (!a.equals(b)) {
			if (a.compareTo(b) > 0)
				a = a.subtract(b);
			else
				b = b.subtract(a);
		}
		return a;
	}
}
