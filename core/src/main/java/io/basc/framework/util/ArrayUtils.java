package io.basc.framework.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class ArrayUtils {
	private ArrayUtils() {
	};

	public static boolean isEmpty(Object[] array) {
		return array == null || array.length == 0;
	}

	public static boolean isEmpty(int[] array) {
		return array == null || array.length == 0;
	}

	public static boolean isEmpty(long[] array) {
		return array == null || array.length == 0;
	}

	public static boolean isEmpty(float[] array) {
		return array == null || array.length == 0;
	}

	public static boolean isEmpty(double[] array) {
		return array == null || array.length == 0;
	}

	public static boolean isEmpty(short[] array) {
		return array == null || array.length == 0;
	}

	public static boolean isEmpty(byte[] array) {
		return array == null || array.length == 0;
	}

	public static boolean isEmpty(char[] array) {
		return array == null || array.length == 0;
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> toList(Object array) {
		if (array == null) {
			return Collections.emptyList();
		}

		int len = Array.getLength(array);
		if (len == 0) {
			return Collections.emptyList();
		}

		List<T> list = new ArrayList<T>(len);
		for (int i = 0; i < len; i++) {
			list.add((T) Array.get(array, i));
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public static <T> T clone(T array) {
		if (array == null) {
			return null;
		}

		int len = Array.getLength(array);
		Object v = Array.newInstance(array.getClass().getComponentType(), len);
		System.arraycopy(array, 0, v, 0, len);
		return (T) v;
	}

	@SuppressWarnings("unchecked")
	public static <T> T merge(T arr1, T arr2) {
		Assert.requiredArgument(!(arr1 == null && arr2 == null), "It can't be all empty");
		if (arr1 == null) {
			return clone(arr2);
		}

		if (arr2 == null) {
			return clone(arr1);
		}

		int len1 = Array.getLength(arr1);
		int len2 = Array.getLength(arr2);
		Object arr = Array.newInstance(arr1.getClass().getComponentType(), len1 + len2);
		System.arraycopy(arr1, 0, arr, 0, len1);
		System.arraycopy(arr2, 0, arr, len1, len2);
		return (T) arr;
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] reversal(Object array) {
		if (array == null) {
			return null;
		}

		int len = Array.getLength(array);
		Object newArray = Array.newInstance(array.getClass().getComponentType(), len);
		for (int i = len - 1, index = 0; i >= 0; i--) {
			Array.set(newArray, index++, Array.get(array, i));
		}
		return (T[]) newArray;
	}

	/**
	 * 比较两个数组
	 * 
	 * @param <T>
	 * @param array1
	 * @param array2
	 * @param comparator
	 * @return
	 */
	public static <T> int compare(T[] array1, T[] array2, Comparator<T> comparator) {
		if (ArrayUtils.isEmpty(array1)) {
			return ArrayUtils.isEmpty(array2) ? 0 : -1;
		}

		if (ArrayUtils.isEmpty(array2)) {
			return ArrayUtils.isEmpty(array1) ? 0 : 1;
		}

		for (int i = 0; i < Math.min(array1.length, array2.length); i++) {
			int v = comparator.compare(array1[i], array2[i]);
			if (v != 0) {
				return v;
			}
		}
		return array1.length - array2.length;
	}

	public static int compare(int[] array1, int[] array2, int defaultValue) {
		int size1 = array1 == null ? 0 : array1.length;
		int size2 = array2 == null ? 0 : array2.length;
		for (int i = 0, size = Math.max(size1, size2); i < size; i++) {
			int v1 = i < size1 ? array1[i] : defaultValue;
			int v2 = i < size2 ? array2[i] : defaultValue;
			int v = Integer.compare(v1, v2);
			if (v != 0) {
				return v;
			}
		}
		return 0;
	}

	public static int compare(long[] array1, long[] array2, long defaultValue) {
		int size1 = array1 == null ? 0 : array1.length;
		int size2 = array2 == null ? 0 : array2.length;
		for (int i = 0, size = Math.max(size1, size2); i < size; i++) {
			long v1 = i < size1 ? array1[i] : defaultValue;
			long v2 = i < size2 ? array2[i] : defaultValue;
			int v = Long.compare(v1, v2);
			if (v != 0) {
				return v;
			}
		}
		return 0;
	}

	public static int compare(double[] array1, double[] array2, double defaultValue) {
		int size1 = array1 == null ? 0 : array1.length;
		int size2 = array2 == null ? 0 : array2.length;
		for (int i = 0, size = Math.max(size1, size2); i < size; i++) {
			double v1 = i < size1 ? array1[i] : defaultValue;
			double v2 = i < size2 ? array2[i] : defaultValue;
			int v = Double.compare(v1, v2);
			if (v != 0) {
				return v;
			}
		}
		return 0;
	}

	public static int compare(Number[] array1, Number[] array2, Number defaultValue, Comparator<Number> comparator) {
		int size1 = array1 == null ? 0 : array1.length;
		int size2 = array2 == null ? 0 : array2.length;
		for (int i = 0, size = Math.max(size1, size2); i < size; i++) {
			Number v1 = i < size1 ? array1[i] : defaultValue;
			Number v2 = i < size2 ? array2[i] : defaultValue;
			int v = comparator.compare(v1, v2);
			if (v != 0) {
				return v;
			}
		}
		return 0;
	}
}