package run.soeasy.framework.core.streaming;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;

import lombok.NonNull;

/**
 * 拉链式迭代器，实现{@link Iterator}接口，用于同步遍历两个迭代器的元素，
 * 并根据指定的迭代策略（{@link Rule}）合并元素，支持灵活的遍历逻辑和元素移除操作。
 * <p>
 * 核心特性：
 * <ul>
 * <li>多策略遍历：支持四种核心迭代策略，适配不同的遍历场景（全量处理、仅重叠、左/右优先耗尽）；</li>
 * <li>兼容空元素：迭代器{@code next()}返回{@code null}时仍正常处理，仅标记是否调用过{@code next()}；</li>
 * <li>安全移除：支持移除最后一次迭代获取的元素，严格控制{@code remove()}调用时机和权限；</li>
 * <li>防死循环：迭代策略逻辑严谨，无元素时自动终止遍历，避免无限循环；</li>
 * <li>类型灵活：通过泛型和函数式接口（{@link BiFunction}）支持任意类型元素的合并。</li>
 * </ul>
 *
 * @param <L> 左侧迭代器的元素类型
 * @param <R> 右侧迭代器的元素类型
 * @param <T> 左右元素合并后的目标类型
 * @author soeasy.run
 * @see Iterator
 * @see BiFunction
 * @see NoSuchElementException
 */
public class ZipIterator<L, R, T> implements Iterator<T> {

	/**
	 * 迭代策略枚举，定义{@link #hasNext()}方法的核心判断逻辑，决定迭代器的遍历终止条件。
	 */
	public static enum Rule {
		/**
		 * 任意迭代器有元素则继续遍历（逻辑或：left.hasNext() || right.hasNext()），
		 * 会处理完左侧和右侧迭代器的所有元素，无优先级区分。
		 */
		ANY_HAS_NEXT,
		/**
		 * 两个迭代器都有元素时才继续遍历（逻辑与：left.hasNext() &amp;&amp; right.hasNext()），
		 * 仅处理两侧迭代器重叠的元素，任一迭代器耗尽则终止。
		 */
		BOTH_HAS_NEXT,
		/**
		 * 左优先耗尽策略：先耗尽左侧迭代器的所有元素，再处理右侧迭代器剩余元素，
		 * 左侧未耗尽时，即使右侧有元素也优先处理左侧。
		 */
		LEFT_FIRST,
		/**
		 * 右优先耗尽策略：先耗尽右侧迭代器的所有元素，再处理左侧迭代器剩余元素，
		 * 右侧未耗尽时，即使左侧有元素也优先处理右侧。
		 */
		RIGHT_FIRST
	}

	/**
	 * 元素合并函数，用于将左侧和右侧迭代器的元素合并为目标类型{T}，
	 * 支持自定义合并逻辑，允许接收{@code null}元素（迭代器{@code next()}返回null时）。
	 */
	private final BiFunction<? super L, ? super R, ? extends T> combiner;

	/**
	 * 标记最后一次调用{@link #next()}时，是否从左侧迭代器调用了{@code next()}方法（无论返回是否为null），
	 * 用于{@link #remove()}方法判断需要移除哪个迭代器的元素。
	 */
	private boolean lastFetchedLeft;

	/**
	 * 标记最后一次调用{@link #next()}时，是否从右侧迭代器调用了{@code next()}方法（无论返回是否为null），
	 * 用于{@link #remove()}方法判断需要移除哪个迭代器的元素。
	 */
	private boolean lastFetchedRight;

	/**
	 * 左侧迭代器，存储待遍历的{L}类型元素，不可为null。
	 */
	private final Iterator<? extends L> leftIterator;

	/**
	 * 标记是否允许调用{@link #remove()}方法，每次调用{@link #next()}后重置为{@code true}，
	 * 调用{@link #remove()}后置为{@code false}，确保每次{@code next()}后仅允许一次{@code remove()}。
	 */
	private boolean removeAllowed;

	/**
	 * 右侧迭代器，存储待遍历的{R}类型元素，不可为null。
	 */
	private final Iterator<? extends R> rightIterator;

	/**
	 * 迭代策略，决定{@link #hasNext()}的判断逻辑，不可为null。
	 */
	private final Rule rule;

	/**
	 * 构造拉链式迭代器，初始化左右迭代器、迭代策略和元素合并函数。
	 *
	 * @param leftIterator  左侧迭代器，不可为null，存储{L}类型元素
	 * @param rightIterator 右侧迭代器，不可为null，存储{R}类型元素
	 * @param rule          迭代策略，不可为null，定义遍历终止条件
	 * @param combiner      元素合并函数，不可为null，用于合并左右迭代器的元素为{T}类型
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
	 * 判断迭代器是否还有下一个元素，判断逻辑由指定的{@link Rule}迭代策略决定。
	 *
	 * @return {@code true} - 有下一个元素，{@code false} - 无下一个元素
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
	 * 获取下一个合并后的元素，按迭代策略从左右迭代器获取元素并通过合并函数处理。
	 * <p>
	 * 处理逻辑：
	 * <ol>
	 * <li>校验是否有下一个元素，无则抛出{@link NoSuchElementException}；</li>
	 * <li>重置{@code removeAllowed}和取值标记（{@code lastFetchedLeft/Right}）；</li>
	 * <li>分别从左右迭代器获取元素（有元素则调用{@code next()}，允许返回null），并标记是否调用过{@code next()}；</li>
	 * <li>通过合并函数返回合并后的{T}类型元素。</li>
	 * </ol>
	 *
	 * @return 合并后的{T}类型元素，可能为null（取决于合并函数和迭代器元素）
	 * @throws NoSuchElementException 当无更多元素可遍历（{@link #hasNext()}返回false）时抛出
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
	 * 移除最后一次调用{@link #next()}时从迭代器获取的元素，依赖底层迭代器的{@code remove()}能力。
	 * <p>
	 * 约束规则：
	 * <ul>
	 * <li>调用时机：仅允许在每次{@link #next()}调用后调用一次，多次调用或未调用{@code next()}时抛出异常；</li>
	 * <li>移除范围：仅移除「调用过{@code next()}」的迭代器的最后一个元素（无论{@code next()}返回是否为null）；</li>
	 * <li>异常场景：底层迭代器不支持{@code remove()}时抛出{@link UnsupportedOperationException}；</li>
	 * <li>状态校验：无元素被获取时（{@code lastFetchedLeft/Right}均为false）抛出{@link IllegalStateException}。</li>
	 * </ul>
	 *
	 * @throws IllegalStateException         调用时机不合法（如未调用{@code next()}、已调用过{@code remove()}、无元素被获取）时抛出
	 * @throws UnsupportedOperationException 底层迭代器（左侧/右侧）不支持{@code remove()}操作时抛出
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