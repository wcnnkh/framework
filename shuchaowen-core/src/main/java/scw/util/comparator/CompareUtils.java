package scw.util.comparator;

public final class CompareUtils {
	private CompareUtils() {
	};

	/**
	 * @param x
	 * @param y
	 * @param desc 是否降序
	 * @return
	 */
	public static int compare(int x, int y, boolean desc) {
		if (desc) {
			return (x < y) ? 1 : ((x == y) ? 0 : -1);
		} else {
			return (x < y) ? -1 : ((x == y) ? 0 : 1);
		}
	}
}
