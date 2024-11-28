package io.basc.framework.util;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.Random;
import java.util.function.Predicate;

import io.basc.framework.util.math.Addition;
import io.basc.framework.util.math.NumberComparator;
import lombok.NonNull;

public final class RandomUtils {
	private RandomUtils() {
	};

	public final static char[] CAPITAL_LETTERS = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
			'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', };

	public final static char[] LOWERCASE_LETTERS = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
			'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };

	public final static char[] NUMBERIC_CHARACTER = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

	/**
	 * 放在一起容易分辨的字符
	 */
	public final static char[] EASY_TO_DISTINGUISH = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'c', 'd',
			'e', 'f', 'h', 'k', 'm', 'n', 'p', 'r', 's', 't', 'v', 'w', 'y', 'A', 'B', 'C', 'E', 'F', 'G', 'H', 'K',
			'M', 'N', 'R', 'S', 'T', 'V', 'W', 'Y' };

	public final static char[] ALL = StringUtils.mergeCharArray(NUMBERIC_CHARACTER, LOWERCASE_LETTERS, CAPITAL_LETTERS);

	/**
	 * 获取某区间的随机值[min, max)
	 * 
	 * @param random
	 * @param min
	 * @param max
	 * @return
	 */
	public static int random(Random random, int min, int max) {
		if (max == min) {
			return min;
		}
		return (int) (random.nextDouble() * (max - min)) + min;
	}

	/**
	 * 获取某区间的随机值[min, max)
	 * 
	 * @param min
	 * @param max
	 * @return
	 */
	public static int random(int min, int max) {
		if (max == min) {
			return min;
		}
		return (int) (Math.random() * (max - min)) + min;
	}

	/**
	 * 获取某区间的随机值[min, max)
	 * 
	 * @param random
	 * @param min
	 * @param max
	 * @return [min, max)
	 */
	public static long random(Random random, long min, long max) {
		if (max == min) {
			return min;
		}
		return (long) (random.nextDouble() * (max - min)) + min;
	}

	/**
	 * 获取某区间的随机值[min, max)
	 * 
	 * @param min
	 * @param max
	 * @return
	 */
	public static long random(long min, long max) {
		if (max == min) {
			return min;
		}
		return (long) (Math.random() * (max - min)) + min;
	}

	/**
	 * 获取某闭区间的随机值[min, max)
	 * 
	 * @param random
	 * @param min
	 * @param max
	 * @return
	 */
	public static BigDecimal random(Random random, BigDecimal min, BigDecimal max) {
		Assert.requiredArgument(min != null, "min");
		Assert.requiredArgument(max != null, "max");
		if (min.equals(max)) {
			return min;
		}
		return new BigDecimal(random.nextDouble() + "").multiply(max.subtract(min)).add(min);
	}

	/**
	 * 获取某区间的随机值[min, max)
	 * 
	 * @param min
	 * @param max
	 * @return
	 */
	public static BigDecimal random(BigDecimal min, BigDecimal max) {
		Assert.requiredArgument(min != null, "min");
		Assert.requiredArgument(max != null, "max");
		if (min.equals(max)) {
			return min;
		}
		return new BigDecimal(Math.random() + "").multiply(max.subtract(min)).add(min);
	}

	/**
	 * 获取某闭区间的随机值[min, max)
	 * 
	 * @param random
	 * @param min
	 * @param max
	 * @return
	 */
	public static BigInteger random(Random random, BigInteger min, BigInteger max) {
		Assert.requiredArgument(min != null, "min");
		Assert.requiredArgument(max != null, "max");
		if (min.equals(max)) {
			return min;
		}
		return new BigInteger(random.nextDouble() + "").multiply(max.subtract(min)).add(min);
	}

	/**
	 * 获取某区间的随机值[min, max)
	 * 
	 * @param min
	 * @param max
	 * @return
	 */
	public static BigInteger random(BigInteger min, BigInteger max) {
		Assert.requiredArgument(min != null, "min");
		Assert.requiredArgument(max != null, "max");
		if (min.equals(max)) {
			return min;
		}
		return new BigInteger(Math.random() + "").multiply(max.subtract(min)).add(min);
	}

	/**
	 * 获取某区间的随机值[min, max)
	 * 
	 * @param random
	 * @param min
	 * @param max
	 * @return
	 */
	public static Number random(Random random, Number min, Number max) {
		Assert.requiredArgument(min != null, "min");
		Assert.requiredArgument(max != null, "max");
		if (min.equals(max)) {
			return min;
		}

		if (min instanceof BigDecimal || min instanceof Float || min instanceof Double || max instanceof BigDecimal
				|| max instanceof Float || max instanceof Double) {
			return random(random, (BigDecimal) Addition.INSTANCE.eval(BigDecimal.ZERO, min),
					(BigDecimal) Addition.INSTANCE.eval(BigDecimal.ZERO, max));
		} else if (max instanceof BigInteger || min instanceof BigInteger) {
			return random(random, (BigInteger) Addition.INSTANCE.eval(BigInteger.ZERO, min),
					(BigInteger) Addition.INSTANCE.eval(BigInteger.ZERO, max));
		}
		return random(random, min.longValue(), max.longValue());
	}

	/**
	 * 获取某区间的随机值[min, max)
	 * 
	 * @param min
	 * @param max
	 * @return
	 */
	public static Number random(Number min, Number max) {
		Assert.requiredArgument(min != null, "min");
		Assert.requiredArgument(max != null, "max");
		return random(new Random(), min, max);
	}

	public static <T> T random(Random random, T[] array) {
		Assert.requiredArgument(random != null, "random");
		Assert.requiredArgument(array != null && array.length != 0, "array");
		return array[random.nextInt(array.length)];
	}

	public static int random(Random random, int[] array) {
		Assert.requiredArgument(random != null, "random");
		Assert.requiredArgument(array != null && array.length != 0, "array");
		return array[random.nextInt(array.length)];
	}

	public static long random(Random random, long[] array) {
		Assert.requiredArgument(random != null, "random");
		Assert.requiredArgument(array != null && array.length != 0, "array");
		return array[random.nextInt(array.length)];
	}

	public static short random(Random random, short[] array) {
		Assert.requiredArgument(random != null, "random");
		Assert.requiredArgument(array != null && array.length != 0, "array");
		return array[random.nextInt(array.length)];
	}

	public static byte random(Random random, byte[] array) {
		Assert.requiredArgument(random != null, "random");
		Assert.requiredArgument(array != null && array.length != 0, "array");
		return array[random.nextInt(array.length)];
	}

	public static float random(Random random, float[] array) {
		Assert.requiredArgument(random != null, "random");
		Assert.requiredArgument(array != null && array.length != 0, "array");
		return array[random.nextInt(array.length)];
	}

	public static double random(Random random, double[] array) {
		Assert.requiredArgument(random != null, "random");
		Assert.requiredArgument(array != null && array.length != 0, "array");
		return array[random.nextInt(array.length)];
	}

	public static boolean random(Random random, boolean[] array) {
		Assert.requiredArgument(random != null, "random");
		Assert.requiredArgument(array != null && array.length != 0, "array");
		return array[random.nextInt(array.length)];
	}

	public static char random(Random random, char[] array) {
		Assert.requiredArgument(random != null, "random");
		Assert.requiredArgument(array != null && array.length != 0, "array");
		return array[random.nextInt(array.length)];
	}

	public static <T> T random(T[] array) {
		return random(new Random(), array);
	}

	public static int random(int[] array) {
		return random(new Random(), array);
	}

	public static long random(long[] array) {
		return random(new Random(), array);
	}

	public static short random(short[] array) {
		return random(new Random(), array);
	}

	public static byte random(byte[] array) {
		return random(new Random(), array);
	}

	public static float random(float[] array) {
		return random(new Random(), array);
	}

	public static double random(double[] array) {
		return random(new Random(), array);
	}

	public static char random(char[] array) {
		return random(new Random(), array);
	}

	public static boolean random(boolean[] array) {
		return random(new Random(), array);
	}

	public static int[] random(Random random, int[] array, int length) {
		Assert.requiredArgument(random != null, "random");
		Assert.requiredArgument(length > 0, "Length must be greater than 0");
		Assert.requiredArgument(array != null && array.length != 0, "array");
		int[] arr = new int[length];
		for (int i = 0, size = array.length; i < length; ++i) {
			arr[i] = array[random.nextInt(size)];
		}
		return arr;
	}

	public static int[] random(int[] array, int length) {
		return random(new Random(), array, length);
	}

	public static char[] random(Random random, char[] array, int length) {
		Assert.requiredArgument(random != null, "random");
		Assert.requiredArgument(length > 0, "Length must be greater than 0");
		Assert.requiredArgument(array != null && array.length != 0, "array");
		char[] arr = new char[length];
		for (int i = 0, size = array.length; i < length; ++i) {
			arr[i] = array[random.nextInt(size)];
		}
		return arr;
	}

	public static char[] random(char[] array, int length) {
		return random(new Random(), array, length);
	}

	public static short[] random(Random random, short[] array, int length) {
		Assert.requiredArgument(random != null, "random");
		Assert.requiredArgument(length > 0, "Length must be greater than 0");
		Assert.requiredArgument(array != null && array.length != 0, "array");
		short[] arr = new short[length];
		for (int i = 0, size = array.length; i < length; ++i) {
			arr[i] = array[random.nextInt(size)];
		}
		return arr;
	}

	public static short[] random(short[] array, int length) {
		return random(new Random(), array, length);
	}

	public static byte[] random(Random random, byte[] array, int length) {
		Assert.requiredArgument(random != null, "random");
		Assert.requiredArgument(length > 0, "Length must be greater than 0");
		Assert.requiredArgument(array != null && array.length != 0, "array");
		byte[] arr = new byte[length];
		for (int i = 0, size = array.length; i < length; ++i) {
			arr[i] = array[random.nextInt(size)];
		}
		return arr;
	}

	public static byte[] random(byte[] array, int length) {
		return random(new Random(), array, length);
	}

	public static long[] random(Random random, long[] array, int length) {
		Assert.requiredArgument(random != null, "random");
		Assert.requiredArgument(length > 0, "Length must be greater than 0");
		Assert.requiredArgument(array != null && array.length != 0, "array");
		long[] arr = new long[length];
		for (int i = 0, size = array.length; i < length; ++i) {
			arr[i] = array[random.nextInt(size)];
		}
		return arr;
	}

	public static long[] random(long[] array, int length) {
		return random(new Random(), array, length);
	}

	public static boolean[] random(Random random, boolean[] array, int length) {
		Assert.requiredArgument(random != null, "random");
		Assert.requiredArgument(length > 0, "Length must be greater than 0");
		Assert.requiredArgument(array != null && array.length != 0, "array");
		boolean[] arr = new boolean[length];
		for (int i = 0, size = array.length; i < length; ++i) {
			arr[i] = array[random.nextInt(size)];
		}
		return arr;
	}

	public static boolean[] random(boolean[] array, int length) {
		return random(new Random(), array, length);
	}

	public static float[] random(Random random, float[] array, int length) {
		Assert.requiredArgument(random != null, "random");
		Assert.requiredArgument(length > 0, "Length must be greater than 0");
		Assert.requiredArgument(array != null && array.length != 0, "array");
		float[] arr = new float[length];
		for (int i = 0, size = array.length; i < length; ++i) {
			arr[i] = array[random.nextInt(size)];
		}
		return arr;
	}

	public static float[] random(float[] array, int length) {
		return random(new Random(), array, length);
	}

	public static double[] random(Random random, double[] array, int length) {
		Assert.requiredArgument(random != null, "random");
		Assert.requiredArgument(length > 0, "Length must be greater than 0");
		Assert.requiredArgument(array != null && array.length != 0, "array");
		double[] arr = new double[length];
		for (int i = 0, size = array.length; i < length; ++i) {
			arr[i] = array[random.nextInt(size)];
		}
		return arr;
	}

	public static double[] random(double[] array, int length) {
		return random(new Random(), array, length);
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] random(Random random, T[] array, int length) {
		Assert.requiredArgument(random != null, "random");
		Assert.requiredArgument(length > 0, "Length must be greater than 0");
		Assert.requiredArgument(array != null && array.length != 0, "array");
		Object target = Array.newInstance(array.getClass().getComponentType(), length);
		for (int i = 0, size = array.length; i < length; ++i) {
			Array.set(target, i, array[random.nextInt(size)]);
		}
		return (T[]) target;
	}

	public static <T> T[] random(T[] array, int length) {
		return random(new Random(), array, length);
	}

	public static char[] random(Random random, CharSequence source, int length) {
		Assert.requiredArgument(StringUtils.isEmpty(source), "source");
		Assert.requiredArgument(length > 0, "Length must be greater than 0");
		char[] arr = new char[length];
		for (int i = 0, size = source.length(); i < length; ++i) {
			arr[i] = source.charAt(random.nextInt(size));
		}
		return arr;
	}

	public static char[] random(CharSequence source, int length) {
		return random(new Random(), source, length);
	}

	public static String random(Random random, String source, int length) {
		Assert.requiredArgument(StringUtils.isEmpty(source), "source");
		Assert.requiredArgument(length > 0, "Length must be greater than 0");
		char[] arr = new char[length];
		for (int i = 0, size = source.length(); i < length; ++i) {
			arr[i] = source.charAt(random.nextInt(size));
		}
		return new String(arr);
	}

	public static String random(String source, int length) {
		return random(new Random(), source, length);
	}

	/**
	 * 返回一个随机字段串
	 * 
	 * @param length
	 * @return
	 */
	public static String randomString(int length) {
		return new String(random(ALL, length));
	}

	/**
	 * 获取指定长度的随机数字组成的字符串
	 * 
	 * @param len
	 * @return
	 */
	public static String randomNumCode(int len) {
		return new String(random(NUMBERIC_CHARACTER, len));
	}

	/**
	 * 获取指定权重位的元素
	 * 
	 * @param <T>
	 * @param <E>
	 * @param totalWeight
	 * @param weight
	 * @param iterator
	 * @param weightProcessor 返回元素的权重，忽略0或null
	 * @param removePredicate 找到元素后如果返回true将删除该元素 {@link Iterator#remove()}
	 * @return
	 * @throws E
	 */
	public static <T, E extends Throwable> T random(@NonNull Number totalWeight, @NonNull Number weight,
			@NonNull Iterator<? extends T> iterator, @NonNull Pipeline<T, Number, E> weightProcessor,
			Predicate<? super T> removePredicate) throws E {
		Assert.requiredArgument(weightProcessor != null, "weightProcessor");
		Assert.requiredArgument(iterator != null, "iterator");
		Assert.isTrue(totalWeight != null && NumberComparator.INSTANCE.compare(totalWeight, 0) > 0,
				"totalWeight needs to be greater than 0");
		Assert.isTrue(weight != null && NumberComparator.INSTANCE.compare(weight, 0) > 0,
				"weight needs to be greater than 0");
		Assert.isTrue(NumberComparator.INSTANCE.compare(weight, totalWeight) <= 0,
				"weight[" + weight + "] cannot be greater than totalweight[" + totalWeight + "]");
		Number indexWeight = 0;
		while (iterator.hasNext()) {
			T item = iterator.next();
			if (item == null) {
				continue;
			}

			Number itemWeight = weightProcessor.apply(item);
			if (itemWeight == null) {
				continue;
			}

			int compareValue = NumberComparator.INSTANCE.compare(itemWeight, 0);
			if (compareValue == 0) {
				continue;
			}

			if (compareValue < 0) {
				// 权重需要大于0
				throw new IllegalArgumentException("Weight needs to be greater than 0");
			}

			indexWeight = Addition.INSTANCE.eval(indexWeight, itemWeight);
			// weight = 3
			// indexWeight = 2 + 2
			// weight <= indexWeight 成立
			// 1-2 3-4 5-6
			if (NumberComparator.INSTANCE.compare(weight, indexWeight) <= 0) {
				if (removePredicate != null && removePredicate.test(item)) {
					iterator.remove();
				}
				return item;
			}
		}
		return null;
	}

	/**
	 * 获取总权重
	 * 
	 * @param <T>
	 * @param <E>
	 * @param iterator
	 * @param weightProcessor 返回元素的权重，忽略0或null
	 * @return
	 * @throws E
	 */
	public static <T, E extends Throwable> Number getWeight(Iterator<? extends T> iterator,
			Pipeline<T, Number, E> weightProcessor) throws E {
		Assert.requiredArgument(weightProcessor != null, "weightProcessor");
		Assert.requiredArgument(iterator != null, "iterator");
		Number totalWegith = 0;
		while (iterator.hasNext()) {
			T item = iterator.next();
			if (item == null) {
				continue;
			}

			Number weight = weightProcessor.apply(item);
			if (weight == null) {
				continue;
			}

			int compareValue = NumberComparator.INSTANCE.compare(weight, 0);
			if (compareValue == 0) {
				continue;
			}

			if (compareValue < 0) {
				// 权重需要大于0
				throw new IllegalArgumentException("Weight needs to be greater than 0");
			}

			totalWegith = Addition.INSTANCE.eval(totalWegith, weight);
		}
		return totalWegith;
	}

	/**
	 * 随机获取一个总权重范围内的元素
	 * 
	 * @param <T>
	 * @param <E>
	 * @param totalWeight
	 * @param iterator
	 * @param weightProcessor 返回元素的权重，忽略0或null
	 * @param removePredicate 找到元素后如果返回true将删除该元素 {@link Iterator#remove()}
	 * @return
	 * @throws E
	 */
	public static <T, E extends Throwable> T random(@NonNull Number totalWeight,
			@NonNull Iterator<? extends T> iterator, @NonNull Pipeline<T, Number, E> weightProcessor,
			Predicate<? super T> removePredicate) throws E {
		return random(totalWeight, random(1, Addition.INSTANCE.eval(totalWeight, 1)), iterator, weightProcessor,
				removePredicate);
	}

	/**
	 * 随机获取一个元素
	 * 
	 * @param <T>
	 * @param <E>
	 * @param iterable
	 * @param weightProcessor 返回元素的权重，忽略0或null
	 * @param randomProcessor 获取随机数
	 * @param removePredicate 找到元素后如果返回true将删除该元素 {@link Iterator#remove()}
	 * @return
	 * @throws E
	 */
	public static <T, E extends Throwable> T random(Iterable<? extends T> iterable,
			@NonNull Pipeline<T, Number, E> weightProcessor, @NonNull Pipeline<Number, Number, E> randomProcessor,
			Predicate<? super T> removePredicate) throws E {
		if (iterable == null) {
			return null;
		}

		Number totalWegith = getWeight(iterable.iterator(), weightProcessor);
		Number randomWeight = randomProcessor.apply(totalWegith);
		if (randomWeight == null) {
			return null;
		}
		return random(totalWegith, randomWeight, iterable.iterator(), weightProcessor, removePredicate);
	}

	/**
	 * 随机获取一个元素
	 * 
	 * @param <T>
	 * @param <E>
	 * @param iterable
	 * @param weightProcessor 返回元素的权重，忽略0或null
	 * @param removePredicate 找到元素后如果返回true将删除该元素 {@link Iterator#remove()}
	 * @return
	 * @throws E
	 */
	public static <T, E extends Throwable> T random(@NonNull Iterable<? extends T> iterable,
			@NonNull Pipeline<T, Number, E> weightProcessor, Predicate<? super T> removePredicate) throws E {
		return random(iterable, weightProcessor, (e) -> random(1, Addition.INSTANCE.eval(e, 1)), removePredicate);
	}
}
