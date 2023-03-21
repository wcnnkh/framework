package io.basc.framework.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
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

	/**
	 * 默认是不进行深拷贝
	 * 
	 * @param <T>
	 * @param array
	 * @return
	 */
	public static <T> T clone(T array) {
		return clone(array, false);
	}

	@SuppressWarnings("unchecked")
	public static <T> T clone(T array, boolean deep) {
		if (array == null) {
			return null;
		}

		if (array instanceof Object[]) {
			if (deep) {
				int len = Array.getLength(array);
				Object clone = Array.newInstance(array.getClass().getComponentType(), len);
				for (int i = 0; i < len; i++) {
					Array.set(clone, i, ObjectUtils.clone(Array.get(array, i), deep));
				}
				return (T) clone;
			} else {
				return (T) ((Object[]) array).clone();
			}
		} else if (array instanceof byte[]) {
			return (T) ((byte[]) array).clone();
		} else if (array instanceof short[]) {
			return (T) ((short[]) array).clone();
		} else if (array instanceof int[]) {
			return (T) ((int[]) array).clone();
		} else if (array instanceof long[]) {
			return (T) ((long[]) array).clone();
		} else if (array instanceof char[]) {
			return (T) ((char[]) array).clone();
		} else if (array instanceof float[]) {
			return (T) ((float[]) array).clone();
		} else if (array instanceof double[]) {
			return (T) ((double[]) array).clone();
		} else if (array instanceof boolean[]) {
			return (T) ((boolean[]) array).clone();
		}
		throw new IllegalArgumentException("Must be array type");
	}

	@SuppressWarnings("unchecked")
	public static <T> T merge(T... arrays) {
		if (arrays == null || arrays.length == 0) {
			return null;
		}

		int total = 0;
		T first = null;
		for (int i = 0; i < arrays.length; i++) {
			T arr = arrays[i];
			if (arr == null) {
				continue;
			}

			if (first == null) {
				first = arr;
			}
			total += Array.getLength(arr);
		}

		if (first == null) {
			return null;
		}

		Object target = Array.newInstance(first.getClass().getComponentType(), total);
		for (int i = 0, start = 0; i < arrays.length; i++) {
			T array = arrays[i];
			if (array == null) {
				continue;
			}

			int len = Array.getLength(arrays[i]);
			System.arraycopy(array, 0, target, start, len);
			start += len;
		}
		return (T) target;
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

	public static String toString(Object array, boolean deep) {
		if (array == null) {
			return null;
		}

		if (array instanceof Object[]) {
			return deep ? Arrays.deepToString((Object[]) array) : Arrays.toString((Object[]) array);
		} else if (array instanceof byte[]) {
			return Arrays.toString((byte[]) array);
		} else if (array instanceof short[]) {
			return Arrays.toString((short[]) array);
		} else if (array instanceof int[]) {
			return Arrays.toString((int[]) array);
		} else if (array instanceof long[]) {
			return Arrays.toString((long[]) array);
		} else if (array instanceof char[]) {
			return Arrays.toString((char[]) array);
		} else if (array instanceof float[]) {
			return Arrays.toString((float[]) array);
		} else if (array instanceof double[]) {
			return Arrays.toString((double[]) array);
		} else if (array instanceof boolean[]) {
			return Arrays.toString((boolean[]) array);
		}
		throw new IllegalArgumentException("Must be array type");
	}

	public static String toString(Object array) {
		return toString(array, true);
	}

	public static int hashCode(Object array, boolean deep) {
		if (array == null) {
			return 0;
		}

		if (array instanceof Object[]) {
			return deep ? Arrays.deepHashCode((Object[]) array) : Arrays.hashCode((Object[]) array);
		} else if (array instanceof byte[]) {
			return Arrays.hashCode((byte[]) array);
		} else if (array instanceof short[]) {
			return Arrays.hashCode((short[]) array);
		} else if (array instanceof int[]) {
			return Arrays.hashCode((int[]) array);
		} else if (array instanceof long[]) {
			return Arrays.hashCode((long[]) array);
		} else if (array instanceof char[]) {
			return Arrays.hashCode((char[]) array);
		} else if (array instanceof float[]) {
			return Arrays.hashCode((float[]) array);
		} else if (array instanceof double[]) {
			return Arrays.hashCode((double[]) array);
		} else if (array instanceof boolean[]) {
			return Arrays.hashCode((boolean[]) array);
		}
		throw new IllegalArgumentException("Must be array type");
	}

	public static int hashCode(Object array) {
		return hashCode(array, true);
	}

	public static boolean equals(Object left, Object right, boolean deep) {
		if (left == right) {
			return true;
		}

		if (left == null || right == null) {
			return false;
		}

		/**
		 * int[][] 是Object[]类型不是int[]类型, 同理其他类型也是
		 */
		if (left instanceof Object[] && right instanceof Object[]) {
			return deep ? Arrays.deepEquals((Object[]) left, (Object[]) right)
					: Arrays.equals((Object[]) left, (Object[]) right);
		} else if (left instanceof byte[] && right instanceof byte[]) {
			return Arrays.equals((byte[]) left, (byte[]) right);
		} else if (left instanceof short[] && right instanceof short[]) {
			return Arrays.equals((short[]) left, (short[]) right);
		} else if (left instanceof int[] && right instanceof int[]) {
			return Arrays.equals((int[]) left, (int[]) right);
		} else if (left instanceof long[] && right instanceof long[]) {
			return Arrays.equals((long[]) left, (long[]) right);
		} else if (left instanceof char[] && right instanceof char[])
			return Arrays.equals((char[]) left, (char[]) right);
		else if (left instanceof float[] && right instanceof float[]) {
			return Arrays.equals((float[]) left, (float[]) right);
		} else if (left instanceof double[] && right instanceof double[]) {
			return Arrays.equals((double[]) left, (double[]) right);
		} else if (left instanceof boolean[] && right instanceof boolean[]) {
			return Arrays.equals((boolean[]) left, (boolean[]) right);
		}
		return false;
	}

	public static boolean equals(Object left, Object right) {
		return equals(left, right, true);
	}

	@SuppressWarnings("unchecked")
	public static <T> T empty(Class<?> componentType) {
		Assert.requiredArgument(componentType != null, "componentType");
		if (componentType == int.class) {
			return (T) new int[0];
		} else if (componentType == byte.class) {
			return (T) new byte[0];
		} else if (componentType == short.class) {
			return (T) new short[0];
		} else if (componentType == char.class) {
			return (T) new char[0];
		} else if (componentType == long.class) {
			return (T) new long[0];
		} else if (componentType == float.class) {
			return (T) new float[0];
		} else if (componentType == double.class) {
			return (T) new double[0];
		} else if (componentType == boolean.class) {
			return (T) new boolean[0];
		} else {
			return (T) Array.newInstance(componentType, 0);
		}
	}
}