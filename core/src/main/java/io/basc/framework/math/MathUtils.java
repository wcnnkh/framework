package io.basc.framework.math;

import io.basc.framework.math.gcd.DivisionAlgorithm;
import io.basc.framework.math.gcd.GreatestCommonDivisor;
import io.basc.framework.util.spi.NativeServiceLoader;

public final class MathUtils {
	private static final GreatestCommonDivisor GREATEST_COMMON_DIVISOR = NativeServiceLoader
			.load(GreatestCommonDivisor.class).findFirst().orElseGet(DivisionAlgorithm::new);

	private MathUtils() {
	};

	/**
	 * 计算最大公约数
	 * 
	 * @return
	 */
	public static GreatestCommonDivisor getGreatestCommonDivisor() {
		return GREATEST_COMMON_DIVISOR;
	}

	/**
	 * 得到一组数是的最大值或最小值
	 * 
	 * @param isMax
	 * @param num
	 * @return
	 */
	public static long getMaxOrMinNum(boolean isMax, long... num) {
		long temp = num[0];
		for (long n : num) {
			if (n > temp && isMax) {
				temp = n;
			}
		}
		return temp;
	}
}
