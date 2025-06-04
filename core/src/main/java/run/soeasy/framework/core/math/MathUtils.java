package run.soeasy.framework.core.math;

import run.soeasy.framework.core.math.gcd.DivisionAlgorithm;
import run.soeasy.framework.core.math.gcd.GreatestCommonDivisor;
import run.soeasy.framework.core.spi.NativeProvider;

public final class MathUtils {
	private static final GreatestCommonDivisor GREATEST_COMMON_DIVISOR = NativeProvider
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
