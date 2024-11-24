package io.basc.framework.util.math.gcd;

import java.math.BigDecimal;
import java.math.BigInteger;

import io.basc.framework.util.math.BigIntegerValue;
import io.basc.framework.util.math.NumberValue;

/**
 * 辗转相除法
 * 
 * @author wcnnkh
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

	public NumberValue gcd(NumberValue m, NumberValue n) {
		NumberValue a = m.abs();
		NumberValue b = n.abs();
		while (true) {
			if ((a = a.remainder(b)).equals(BigIntegerValue.ZERO))
				return b;
			if ((b = b.remainder(a)).equals(BigIntegerValue.ZERO))
				return a;
		}
	}

}
