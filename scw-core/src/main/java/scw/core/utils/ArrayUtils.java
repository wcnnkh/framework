package scw.core.utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import scw.core.Assert;

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
			Array.set(newArray, index ++, Array.get(array, i));
		}
		return (T[]) newArray;
	}
}