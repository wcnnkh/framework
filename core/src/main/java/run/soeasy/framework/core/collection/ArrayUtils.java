package run.soeasy.framework.core.collection;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ArrayUtils {
	public static final int[] EMPTY_INT_ARRAY = new int[0];
	public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
	public static final short[] EMPTY_SHORT_ARRAY = new short[0];
	public static final char[] EMPTY_CHAR_ARRAY = new char[0];
	public static final long[] EMPTY_LONG_ARRAY = new long[0];
	public static final float[] EMPTY_FLOAT_ARRAY = new float[0];
	public static final double[] EMPTY_DOUBLE_ARRAY = new double[0];
	public static final boolean[] EMPTY_BOOLEAN_ARRAY = new boolean[0];
	public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
	private static ConcurrentReferenceHashMap<Class<?>, Object> EMPTY_ARRAY_CACHE_MAP = new ConcurrentReferenceHashMap<>();

	/**
	 * 至少要测试通过一个
	 * 
	 * @param <T>
	 * @param predicate
	 * @param elements
	 * @return elements长度为0则返回false
	 */
	@SuppressWarnings("unchecked")
	public static <T> boolean anyTest(@NonNull Predicate<? super T> predicate, @NonNull T... elements) {
		for (T arg : elements) {
			if (predicate.test(arg)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 要全部通过测试
	 * 
	 * @param <T>
	 * @param predicate
	 * @param elements
	 * @return elements长度为0则返回true
	 */
	@SuppressWarnings("unchecked")
	public static <T> boolean allTest(@NonNull Predicate<? super T> predicate, @NonNull T... elements) {
		for (T element : elements) {
			if (!predicate.test(element)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 默认是不进行深拷贝
	 * 
	 * @param <T>
	 * @param array
	 * @return
	 */
	public static <T> T clone(T array) {
		return clone(array, false, UnaryOperator.identity());
	};

	@SuppressWarnings("unchecked")
	public static <T> T clone(T array, boolean deep, @NonNull UnaryOperator<? super Object> cloner) {
		if (array == null) {
			return null;
		}

		if (array instanceof Object[]) {
			if (deep) {
				int len = Array.getLength(array);
				Object clone = Array.newInstance(array.getClass().getComponentType(), len);
				for (int i = 0; i < len; i++) {
					Array.set(clone, i, cloner.apply(Array.get(array, i)));
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

	public static <T> void copy(@NonNull T src, int srcPos, T dest, int destPos, int length, boolean deep,
			@NonNull UnaryOperator<? super Object> copyer) {
		if (deep) {
			copy(src, srcPos, dest, destPos, length, copyer);
		} else {
			System.arraycopy(src, srcPos, dest, destPos, length);
		}
	}

	public static <T> void copy(@NonNull T src, int srcPos, @NonNull T dest, int destPos, int length,
			@NonNull UnaryOperator<? super Object> copyer) {
		for (int i = 0; i < length; i++) {
			Object item = Array.get(src, srcPos + i);
			item = copyer.apply(item);
			Array.set(dest, destPos + i, item);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T empty(@NonNull Class<?> componentType) {
		if (componentType == int.class) {
			return (T) EMPTY_INT_ARRAY;
		} else if (componentType == byte.class) {
			return (T) EMPTY_BYTE_ARRAY;
		} else if (componentType == short.class) {
			return (T) EMPTY_SHORT_ARRAY;
		} else if (componentType == char.class) {
			return (T) EMPTY_CHAR_ARRAY;
		} else if (componentType == long.class) {
			return (T) EMPTY_LONG_ARRAY;
		} else if (componentType == float.class) {
			return (T) EMPTY_FLOAT_ARRAY;
		} else if (componentType == double.class) {
			return (T) EMPTY_DOUBLE_ARRAY;
		} else if (componentType == boolean.class) {
			return (T) EMPTY_BOOLEAN_ARRAY;
		} else if (componentType == Object.class) {
			return (T) EMPTY_OBJECT_ARRAY;
		} else {
			Object array = EMPTY_ARRAY_CACHE_MAP.get(componentType);
			if (array == null) {
				Object newArray = (T) Array.newInstance(componentType, 0);
				Object oldArray = EMPTY_ARRAY_CACHE_MAP.putIfAbsent(componentType, newArray);
				if (oldArray == null) {
					array = newArray;
					EMPTY_ARRAY_CACHE_MAP.purgeUnreferencedEntries();
				}
			}
			return (T) array;
		}
	}

	public static boolean equals(Object left, Object right) {
		return equals(left, right, true);
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

	public static int hashCode(Object array) {
		return hashCode(array, true);
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

	public static boolean isEmpty(byte[] array) {
		return array == null || array.length == 0;
	}

	public static boolean isEmpty(char[] array) {
		return array == null || array.length == 0;
	}

	public static boolean isEmpty(double[] array) {
		return array == null || array.length == 0;
	}

	public static boolean isEmpty(float[] array) {
		return array == null || array.length == 0;
	}

	public static boolean isEmpty(int[] array) {
		return array == null || array.length == 0;
	}

	public static boolean isEmpty(long[] array) {
		return array == null || array.length == 0;
	}

	public static boolean isEmpty(Object[] array) {
		return array == null || array.length == 0;
	}

	public static boolean isEmpty(short[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * 合并两个数组
	 * 
	 * @param <T>
	 * @param leftArray
	 * @param rightArray
	 * @return
	 */
	public static <T> T merge(T leftArray, T rightArray) {
		return merge(leftArray, rightArray, false, UnaryOperator.identity());
	}

	/**
	 * 合并两个数组
	 * 
	 * @param <T>
	 * @param leftArray
	 * @param rightArray
	 * @param deep
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T merge(T leftArray, T rightArray, boolean deep, @NonNull UnaryOperator<? super Object> copyer) {
		if (leftArray == null) {
			return clone(rightArray, deep, copyer);
		}

		if (rightArray == null) {
			return clone(leftArray, deep, copyer);
		}

		int leftLength = Array.getLength(leftArray);
		if (leftLength == 0) {
			return clone(rightArray, deep, copyer);
		}

		int rightLength = Array.getLength(rightArray);
		if (rightLength == 0) {
			return clone(leftArray, deep, copyer);
		}

		Object target = Array.newInstance(leftArray.getClass().getComponentType(), leftLength + rightLength);
		copy(leftArray, 0, target, 0, leftLength, deep, copyer);
		copy(rightArray, 0, target, leftLength, rightLength, deep, copyer);
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

	public static List<Object> toList(Object array) {
		if (array == null) {
			return Collections.emptyList();
		}

		int len = Array.getLength(array);
		if (len == 0) {
			return Collections.emptyList();
		}

		List<Object> list = new ArrayList<>(len);
		for (int i = 0; i < len; i++) {
			list.add(Array.get(array, i));
		}
		return list;
	}

	public static String toString(Object array) {
		return toString(array, true);
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

	/**
	 * Convert the given array (which may be a primitive array) to an object array
	 * (if necessary of primitive wrapper objects).
	 * <p>
	 * A {@code null} source value will be converted to an empty Object array.
	 * 
	 * @param source the (potentially primitive) array
	 * @return the corresponding object array (never {@code null})
	 * @throws IllegalArgumentException if the parameter is not an array
	 */
	@SuppressWarnings("rawtypes")
	public static Object[] toObjectArray(Object source) {
		if (source instanceof Object[]) {
			return (Object[]) source;
		}
		if (source == null) {
			return new Object[0];
		}
		if (!source.getClass().isArray()) {
			throw new IllegalArgumentException("Source is not an array: " + source);
		}
		int length = Array.getLength(source);
		if (length == 0) {
			return new Object[0];
		}
		Class wrapperType = Array.get(source, 0).getClass();
		Object[] newArray = (Object[]) Array.newInstance(wrapperType, length);
		for (int i = 0; i < length; i++) {
			newArray[i] = Array.get(source, i);
		}
		return newArray;
	}

	/**
	 * 使用反射读取
	 * 
	 * @param array
	 * @return
	 */
	public static Stream<Object> stream(Object array) {
		if (array == null) {
			return Stream.empty();
		}

		if (array instanceof Object[]) {
			return Arrays.stream((Object[]) array);
		}

		return IntStream.range(0, Array.getLength(array)).mapToObj((index) -> Array.get(array, index));
	}
}