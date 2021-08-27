package io.basc.framework.util.comparator;

public final class CompareUtils {
	private CompareUtils() {
	};

	/**
	 * @param left
	 * @param right
	 * @param desc 是否降序
	 * @return
	 */
	public static int compare(double left, double right, boolean desc) {
		if (desc) {
			return (left < right) ? 1 : ((left == right) ? 0 : -1);
		} else {
			return (left < right) ? -1 : ((left == right) ? 0 : 1);
		}
	}
}
