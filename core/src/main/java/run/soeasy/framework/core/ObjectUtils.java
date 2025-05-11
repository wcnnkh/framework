package run.soeasy.framework.core;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

import run.soeasy.framework.core.collection.ArrayUtils;
import run.soeasy.framework.core.collection.CollectionUtils;

/**
 * Miscellaneous object utility methods.
 * 
 * @author wcnnkh
 *
 */
public abstract class ObjectUtils {
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

	/**
	 * Determine whether the given array is empty: i.e. {@code null} or of zero
	 * length.
	 * 
	 * @param array the array to check
	 */
	/*
	 * public static boolean isEmpty(Object[] array) { return (array == null ||
	 * array.length == 0); }
	 */

	/**
	 * 判断是否为空的，自动识别类型
	 * 
	 * @param obj
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isEmpty(Object obj) {
		if (obj == null) {
			return true;
		} else if (obj instanceof CharSequence) {
			return StringUtils.isEmpty((CharSequence) obj);
		} else if (obj instanceof Collection) {
			return CollectionUtils.isEmpty((Collection) obj);
		} else if (obj instanceof Map) {
			return CollectionUtils.isEmpty((Map) obj);
		} else if (obj.getClass().isArray()) {
			return Array.getLength(obj) == 0;
		}
		return false;
	}

	public static boolean isNotEmpty(Object value) {
		return !isEmpty(value);
	}

	public static boolean isAllEmpty(Object... values) {
		if (values == null || values.length == 0) {
			return true;
		}

		for (Object value : values) {
			if (isNotEmpty(value)) {
				return false;
			}
		}
		return true;
	}

	public static boolean isAnyEmpty(Object... values) {
		if (values == null || values.length == 0) {
			return true;
		}

		for (Object s : values) {
			if (isEmpty(s)) {
				return true;
			}
		}
		return false;
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
}
