package run.soeasy.framework.core;

import run.soeasy.framework.core.collection.ArrayUtils;

/**
 * Miscellaneous object utility methods.
 * 
 * @author wcnnkh
 *
 */
public final class ObjectUtils {
	public static final Object[] EMPTY_ARRAY = new Object[0];

	/**
	 * Determine whether the given object is an array: either an Object array or a
	 * primitive array.
	 * 
	 * @param obj the object to check
	 */
	public static boolean isArray(Object obj) {
		return (obj != null && obj.getClass().isArray());
	}

	public static String toString(Object source, boolean deep) {
		if (source == null) {
			return null;
		} else if (source.getClass().isArray()) {
			return ArrayUtils.toString(source, deep);
		} else {
			return source.toString();
		}
	}

	public static String toString(Object source) {
		return toString(source, true);
	}

	public static int hashCode(Object source, boolean deep) {
		if (source == null) {
			return 0;
		} else if (source.getClass().isArray()) {
			return ArrayUtils.hashCode(source, deep);
		} else {
			return source.hashCode();
		}
	}

	public static int hashCode(Object source) {
		return hashCode(source, true);
	}

	public static boolean equals(Object left, Object right, boolean deep) {
		if (left == right) {
			return true;
		}

		if (left == null || right == null) {
			return false;
		}

		if (left.equals(right)) {
			return true;
		}

		if (left.getClass().isArray() && right.getClass().isArray()) {
			return ArrayUtils.equals(left, right, deep);
		}
		return false;
	}

	public static boolean equals(Object left, Object right) {
		return equals(left, right, true);
	}

	public static void close(AutoCloseable... autoCloseables) throws Exception {
		if (autoCloseables == null) {
			return;
		}

		Exception exception = null;
		for (AutoCloseable autoCloseable : autoCloseables) {
			if (autoCloseable == null) {
				continue;
			}

			try {
				autoCloseable.close();
			} catch (Exception e) {
				if (exception == null) {
					exception = e;
				} else {
					exception.addSuppressed(e);
				}
			}
		}
		if (exception != null) {
			throw exception;
		}
	}

	public static void closeQuietly(AutoCloseable... autoCloseables) {
		if (autoCloseables == null) {
			return;
		}

		for (AutoCloseable autoCloseable : autoCloseables) {
			if (autoCloseable == null) {
				continue;
			}

			try {
				autoCloseable.close();
			} catch (final Exception e) {
				// ignore
			}
		}
	}
}
