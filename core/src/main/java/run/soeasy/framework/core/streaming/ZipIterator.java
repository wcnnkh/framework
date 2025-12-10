package run.soeasy.framework.core.streaming;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;

import lombok.NonNull;

/**
 * 拉链式迭代器，用于同步遍历两个迭代器的元素，并支持多种迭代策略。
 * <p>
 * 核心能力： 1. 支持四种迭代策略（任意有元素、都有元素、左优先耗尽、右优先耗尽）； 2.
 * 支持移除最后一次迭代获取的元素（依赖底层迭代器的remove能力）； 3. 防死循环设计，迭代器无元素时自动终止； 4.
 * 兼容迭代器next()返回null的场景，仅判断是否调用了next()，而非元素是否非空。
 *
 * @param <L> 左侧迭代器的元素类型
 * @param <R> 右侧迭代器的元素类型
 * @param <T> 合并后元素的类型
 * @author soeasy.run
 */
public class ZipIterator<L, R, T> implements Iterator<T> {

	/**
	 * 迭代策略枚举，定义hasNext()的核心判断逻辑
	 */
	public static enum Rule {
		/**
		 * 任意迭代器有元素则继续（逻辑或 ||），无优先级，全量处理所有元素
		 */
		ANY_HAS_NEXT,
		/**
		 * 两个迭代器都有元素才继续（逻辑与 &&），仅处理两侧重叠的元素
		 */
		BOTH_HAS_NEXT,
		/**
		 * 左优先耗尽：先耗尽左侧迭代器，再处理右侧迭代器剩余元素
		 */
		LEFT_FIRST,
		/**
		 * 右优先耗尽：先耗尽右侧迭代器，再处理左侧迭代器剩余元素
		 */
		RIGHT_FIRST
	}

	/**
	 * 元素合并函数，用于将左右迭代器的元素合并为目标类型
	 */
	private final BiFunction<? super L, ? super R, ? extends T> combiner;

	/**
	 * 标记最后一次调用next()时是否从左侧迭代器调用了next()（无论返回是否为null）
	 */
	private boolean lastFetchedLeft;

	/**
	 * 标记最后一次调用next()时是否从右侧迭代器调用了next()（无论返回是否为null）
	 */
	private boolean lastFetchedRight;

	/**
	 * 左侧迭代器
	 */
	private final Iterator<? extends L> leftIterator;

	/**
	 * 标记是否允许调用remove()（每次next()后仅允许调用一次）
	 */
	private boolean removeAllowed;

	/**
	 * 右侧迭代器
	 */
	private final Iterator<? extends R> rightIterator;

	/**
	 * 迭代策略，决定hasNext()的判断逻辑
	 */
	private final Rule rule;

	/**
	 * 构造拉链式迭代器
	 *
	 * @param leftIterator  左侧迭代器，不可为null
	 * @param rightIterator 右侧迭代器，不可为null
	 * @param rule          迭代策略，不可为null
	 * @param combiner      元素合并函数，不可为null
	 * @throws NullPointerException 若任意入参为null时抛出
	 */
	public ZipIterator(@NonNull Iterator<? extends L> leftIterator, @NonNull Iterator<? extends R> rightIterator,
			@NonNull Rule rule, @NonNull BiFunction<? super L, ? super R, ? extends T> combiner) {
		this.leftIterator = leftIterator;
		this.rightIterator = rightIterator;
		this.rule = rule;
		this.combiner = combiner;
	}

	/**
	 * 判断是否还有下一个元素，判断逻辑由迭代策略决定
	 *
	 * @return true-有下一个元素，false-无下一个元素
	 */
	@Override
	public boolean hasNext() {
		switch (rule) {
		case ANY_HAS_NEXT:
			return leftIterator.hasNext() || rightIterator.hasNext();
		case BOTH_HAS_NEXT:
			return leftIterator.hasNext() && rightIterator.hasNext();
		case LEFT_FIRST:
			return leftIterator.hasNext() || (rightIterator.hasNext() && !leftIterator.hasNext());
		case RIGHT_FIRST:
			return rightIterator.hasNext() || (leftIterator.hasNext() && !rightIterator.hasNext());
		}
		return false;
	}

	/**
	 * 获取下一个合并后的元素
	 * <p>
	 * 兼容迭代器next()返回null的场景：仅标记是否调用了next()，而非元素是否非空。
	 *
	 * @return 合并后的元素
	 * @throws NoSuchElementException 当无更多元素时抛出
	 */
	@Override
	public T next() {
		if (!hasNext()) {
			throw new NoSuchElementException("No more elements to zip | left has next: " + leftIterator.hasNext()
					+ ", right has next: " + rightIterator.hasNext());
		}

		// 重置remove权限和取值标记（标记是否调用了迭代器的next()）
		removeAllowed = true;
		lastFetchedLeft = false;
		lastFetchedRight = false;

		// 处理左侧元素：判断是否有元素，有则调用next()（无论返回是否为null），并标记
		L left = null;
		if (leftIterator.hasNext()) {
			lastFetchedLeft = true; // 标记调用了左侧next()
			left = leftIterator.next(); // 允许返回null
		}

		// 处理右侧元素：逻辑同左侧
		R right = null;
		if (rightIterator.hasNext()) {
			lastFetchedRight = true; // 标记调用了右侧next()
			right = rightIterator.next(); // 允许返回null
		}

		return combiner.apply(left, right);
	}

	/**
	 * 移除最后一次调用next()时从迭代器获取的元素（依赖底层迭代器的remove能力）
	 * <p>
	 * 注意事项： 1. 每次next()后仅允许调用一次remove()； 2.
	 * 若底层迭代器不支持remove()，会抛出UnsupportedOperationException； 3.
	 * 若尚未调用next()或已调用过remove()，会抛出IllegalStateException； 4.
	 * 仅移除「调用过next()」的迭代器的最后一个元素（无论next()返回是否为null）。
	 *
	 * @throws IllegalStateException         调用时机不合法时抛出
	 * @throws UnsupportedOperationException 底层迭代器不支持remove时抛出
	 */
	@Override
	public void remove() {
		if (!removeAllowed) {
			throw new IllegalStateException("remove() can only be called once after each next()");
		}
		if (!lastFetchedLeft && !lastFetchedRight) {
			throw new IllegalStateException("No elements fetched from iterators, cannot call remove()");
		}

		removeAllowed = false;

		try {
			// 仅移除调用过next()的迭代器的元素（无论返回是否为null）
			if (lastFetchedLeft) {
				leftIterator.remove();
			}
			if (lastFetchedRight) {
				rightIterator.remove();
			}
		} catch (UnsupportedOperationException e) {
			throw new UnsupportedOperationException("Underlying iterator does not support remove() | fetched left: "
					+ lastFetchedLeft + ", fetched right: " + lastFetchedRight, e);
		}
	}
}