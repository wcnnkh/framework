package run.soeasy.framework.util.function;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.util.RandomUtils;
import run.soeasy.framework.util.collection.CollectionUtils;
import run.soeasy.framework.util.collection.Elements;
import run.soeasy.framework.util.comparator.OrderComparator;

/**
 * 多对一
 * 
 * @author shuchaowen
 *
 * @param <E>
 */
@FunctionalInterface
public interface Selector<E> extends Function<Elements<? extends E>, E> {
	@RequiredArgsConstructor
	public class First<E> implements Selector<E> {
		private static First<?> INSTANCE = new First<>(OrderComparator.INSTANCE);

		@NonNull
		private final Comparator<? super E> comparator;

		@Override
		public E apply(@NonNull Elements<? extends E> elements) {
			return elements.convert((e) -> e.sorted(comparator)).first();
		}
	}

	/**
	 * 使用第一个
	 * 
	 * @param <T>
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> Selector<T> first() {
		return (Selector<T>) First.INSTANCE;
	}

	/**
	 * 使用第一个
	 * 
	 * @param <T>
	 * @param comparator
	 * @return
	 */
	public static <T> Selector<T> first(@NonNull Comparator<? super T> comparator) {
		return new First<>(comparator);
	}

	/**
	 * 轮询
	 * 
	 * @author wcnnkh
	 *
	 * @param <E>
	 */
	public static class RoundRobin<E> implements Selector<E> {
		private final AtomicInteger position = new AtomicInteger();

		@Override
		public E apply(@NonNull Elements<? extends E> elements) {
			List<? extends E> list = elements.toList();
			if (CollectionUtils.isEmpty(list)) {
				return null;
			}

			if (list.size() == 1) {
				return list.get(0);
			}

			int pos = Math.abs(position.getAndIncrement());
			pos = pos % list.size();
			return list.get(pos);
		}
	}

	/**
	 * 轮询选择器
	 * 
	 * @param <T> 任意类型
	 * @return 返回轮询选择器
	 */
	public static <T> Selector<T> roundRobin() {
		return new RoundRobin<>();
	}

	/**
	 * 加权随机
	 * 
	 * @author wcnnkh
	 *
	 * @param <E>
	 */
	public static class WeightedRandom<E> implements Selector<E> {
		public static final WeightedRandom<?> INSTANCE = new WeightedRandom<>();

		@Override
		public E apply(@NonNull Elements<? extends E> elements) {
			List<? extends E> list = elements.toList();
			if (CollectionUtils.isEmpty(list)) {
				return null;
			}

			if (list.size() == 1) {
				return list.get(0);
			}

			return RandomUtils.random(list, (e) -> (e instanceof Weighted) ? ((Weighted) e).getWeight() : 1, null);
		}
	}

	/**
	 * 随机选择器
	 * 
	 * @param <T> 任意类型
	 * @return 返回随机选择器
	 */
	@SuppressWarnings("unchecked")
	public static <T> Selector<T> random() {
		return (Selector<T>) WeightedRandom.INSTANCE;
	}

	@Override
	E apply(@NonNull Elements<? extends E> elements);
}
