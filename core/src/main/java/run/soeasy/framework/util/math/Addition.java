package run.soeasy.framework.util.math;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 两数相加, 返回大类型
 * 
 * @author wcnnkh
 *
 */
public class Addition implements MathFunction<Number, Number> {
	public static final Addition INSTANCE = new Addition();

	@Override
	public Number eval(Number left, Number right) {
		if (left == null) {
			return right;
		}

		if (right == null) {
			return left;
		}

		if (left instanceof BigDecimal) {
			if (right instanceof BigDecimal) {
				return ((BigDecimal) left).add((BigDecimal) right);
			} else if (right instanceof BigInteger) {
				return ((BigDecimal) left).add(new BigDecimal((BigInteger) right));
			} else if (right instanceof Float || right instanceof Double) {
				return ((BigDecimal) left).add(new BigDecimal(right.doubleValue()));
			} else {
				return ((BigDecimal) left).add(new BigDecimal(right.longValue()));
			}
		} else if (left instanceof BigInteger) {
			if (right instanceof BigDecimal) {
				// A+B = B+A
				return eval(right, left);
			} else if (right instanceof BigInteger) {
				return ((BigInteger) left).add((BigInteger) right);
			} else if (right instanceof Float || right instanceof Double) {
				return new BigDecimal((BigInteger) left).add(new BigDecimal(right.doubleValue()));
			} else {
				return ((BigInteger) left).add(BigInteger.valueOf(right.longValue()));
			}
		} else if (left instanceof Float || left instanceof Double) {
			if (right instanceof BigDecimal || right instanceof BigInteger) {
				// A+B = B+A
				return eval(right, left);
			} else {
				return left.doubleValue() + right.doubleValue();
			}
		} else {
			if (right instanceof BigDecimal || right instanceof BigInteger || right instanceof Float
					|| right instanceof Double) {
				// A+B = B+A
				return eval(right, left);
			} else {
				return left.longValue() + right.longValue();
			}
		}
	}
}
