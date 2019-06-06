package scw.core.utils;

import java.lang.reflect.Array;
import java.util.Collection;

public final class ArrayUtils {
	public static final String[] EMPTY_STRING_ARRAY = new String[0];
	public static final int[] EMPTY_INT_ARRAY = new int[0];
	public static final long[] EMPTY_LONG_ARRAY = new long[0];
	public static final short[] EMPTY_SHORT_ARRAY = new short[0];
	public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
	public static final float[] EMPTY_FLOAT_ARRAY = new float[0];
	public static final double[] EMPTY_DOUBLE_ARRAY = new double[0];
	public static final char[] EMPTY_CHAR_ARRAY = new char[0];

	private ArrayUtils() {
	};

	public static boolean isEmpty(Object[] array) {
		return array == null || array.length == 0;
	}

	public static boolean isEmpty(int[] array) {
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
	public static <T> T[] toArray(Class<T> type, Collection<? extends T> collection) {
		if (CollectionUtils.isEmpty(collection)) {
			return (T[]) Array.newInstance(type, 0);
		}

		T[] arr = (T[]) Array.newInstance(type, collection.size());
		return collection.toArray(arr);
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
}