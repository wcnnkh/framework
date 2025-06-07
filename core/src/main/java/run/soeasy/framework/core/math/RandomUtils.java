package run.soeasy.framework.core.math;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.Random;
import java.util.function.Predicate;

import lombok.NonNull;
import run.soeasy.framework.core.Assert;
import run.soeasy.framework.core.function.ThrowingFunction;

public final class RandomUtils {
	private RandomUtils() {
	};

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
	public static BigDecimal random(@NonNull Random random, @NonNull BigDecimal min, @NonNull BigDecimal max) {
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
	public static BigDecimal random(@NonNull BigDecimal min, @NonNull BigDecimal max) {
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
	public static BigInteger random(@NonNull Random random, @NonNull BigInteger min, @NonNull BigInteger max) {
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
	public static BigInteger random(@NonNull BigInteger min, @NonNull BigInteger max) {
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
	public static Number random(@NonNull Random random, @NonNull Number min, @NonNull Number max) {
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
	public static Number random(@NonNull Number min, @NonNull Number max) {
		return random(new Random(), min, max);
	}

	@SuppressWarnings("unchecked")
	public static <T> T randomArray(Random random, T sourceArray, int newLength) {
		Assert.isTrue(newLength > 0, "Length must be greater than 0");
		int length = Array.getLength(sourceArray);
		Object target = Array.newInstance(sourceArray.getClass().getComponentType(), newLength);
		if (length > 0) {
			for (int i = 0; i < newLength; i++) {
				int randomIndex = random.nextInt(length);
				Object randomElement = Array.get(sourceArray, randomIndex);
				Array.set(target, i, randomElement);
			}
		}
		return (T) target;
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
			@NonNull Iterator<? extends T> iterator,
			@NonNull ThrowingFunction<? super T, ? extends Number, ? extends E> weightProcessor,
			Predicate<? super T> removePredicate) throws E {
		Assert.isTrue(NumberComparator.INSTANCE.compare(totalWeight, 0) > 0, "totalWeight needs to be greater than 0");
		Assert.isTrue(NumberComparator.INSTANCE.compare(weight, 0) > 0, "weight needs to be greater than 0");
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
	public static <T, E extends Throwable> Number getWeight(@NonNull Iterator<? extends T> iterator,
			@NonNull ThrowingFunction<? super T, ? extends Number, ? extends E> weightProcessor) throws E {
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
			@NonNull Iterator<? extends T> iterator,
			@NonNull ThrowingFunction<? super T, ? extends Number, ? extends E> weightProcessor,
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
			@NonNull ThrowingFunction<? super T, ? extends Number, ? extends E> weightProcessor,
			@NonNull ThrowingFunction<? super Number, ? extends Number, ? extends E> randomProcessor,
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
			@NonNull ThrowingFunction<? super T, ? extends Number, ? extends E> weightProcessor,
			Predicate<? super T> removePredicate) throws E {
		return random(iterable, weightProcessor, (e) -> random(1, Addition.INSTANCE.eval(e, 1)), removePredicate);
	}
}
