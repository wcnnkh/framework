package run.soeasy.framework.core.math;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Comparator;

public class NumberComparator implements MathFunction<Number, Integer>, Comparator<Number> {
	public static final NumberComparator INSTANCE = new NumberComparator();
	
	@Override
	public int compare(Number left, Number right) {
		if (left == right) {
			return 0;
		}

		if (left == null) {
			return -1;
		}

		if (right == null) {
			return 1;
		}

		if (left.equals(right)) {
			return 0;
		}

		if (left instanceof BigDecimal) {
			if (right instanceof BigDecimal) {
				return ((BigDecimal) left).compareTo((BigDecimal) right);
			} else if (right instanceof BigInteger) {
				return ((BigDecimal) left).compareTo(new BigDecimal((BigInteger) right));
			} else if (right instanceof Float || right instanceof Double) {
				return ((BigDecimal) left).compareTo(new BigDecimal(right.doubleValue()));
			} else {
				return ((BigDecimal) left).compareTo(new BigDecimal(right.longValue()));
			}
		} else if (left instanceof BigInteger) {
			if (right instanceof BigDecimal) {
				return -compare(right, left);
			} else if (right instanceof BigInteger) {
				return ((BigInteger) left).compareTo((BigInteger) right);
			} else if (right instanceof Float || right instanceof Double) {
				return new BigDecimal((BigInteger) left).compareTo(new BigDecimal(right.doubleValue()));
			} else {
				return ((BigInteger) left).compareTo(BigInteger.valueOf(right.longValue()));
			}
		} else if (left instanceof Float || left instanceof Double) {
			if (right instanceof BigDecimal || right instanceof BigInteger) {
				return -compare(right, left);
			} else {
				return Double.compare(left.doubleValue(), right.doubleValue());
			}
		} else {
			if (right instanceof BigDecimal || right instanceof BigInteger || right instanceof Float
					|| right instanceof Double) {
				return -compare(right, left);
			} else {
				return Long.compare(left.longValue(), right.longValue());
			}
		}
	}

	@Override
	public Integer eval(Number left, Number right) {
		return compare(left, right);
	}

}
