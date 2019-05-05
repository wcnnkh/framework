package scw.core.utils;

import java.lang.reflect.Array;
import java.util.Collection;

public final class ArrayUtils {
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
}
