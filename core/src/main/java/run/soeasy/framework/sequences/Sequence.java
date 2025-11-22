package run.soeasy.framework.sequences;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.NonNull;
import run.soeasy.framework.core.collection.CollectionUtils;

/**
 * <p>
 * 一个功能强大且高度可扩展的序列生成器核心接口。它定义了一种标准方式来连续生成有序、唯一的值， 并巧妙地集成了现代Java特性，以提供卓越的灵活性和易用性。
 * </p>
 *
 * <p>
 * <strong>设计哲学：</strong> 本接口基于以下核心原则构建：
 * <ul>
 * <li><b>单一职责</b>：核心职责是生成下一个元素 ({@link #next()})。</li>
 * <li><b>默认假设</b>：序列默认是无限的，以适应ID生成等常见场景。</li>
 * <li><b>契约优先</b>：清晰定义了有限序列和无限序列的行为边界。</li>
 * <li><b>组合优于继承</b>：通过函数式转换和装饰器模式实现功能扩展。</li>
 * <li><b>无缝集成</b>：继承自{@link Iterator}，使其能自然融入Java集合框架。</li>
 * </ul>
 * </p>
 *
 * <p>
 * <strong>核心功能与API概览：</strong>
 * <ol>
 * <li><b>基础迭代</b>：{@link #next()}, {@link #hasNext()}</li>
 * <li><b>流式处理</b>：{@link #stream()}</li>
 * <li><b>函数式转换</b>：{@link #map(Function)}</li>
 * <li><b>性能优化</b>：{@link #withPrefetch(long)}</li>
 * <li><b>序列截断</b>：{@link #limit(long)}</li>
 * <li><b>快照创建</b>：{@link #snapshot(long)}</li>
 * <li><b>适配器</b>：{@link #unknownSize(Iterator)}</li>
 * </ol>
 * </p>
 *
 * <p>
 * <strong>实现者指南：</strong>
 * <ul>
 * <li><b>无限序列</b>：只需实现{@link #next()}方法，无需重写{@link #hasNext()}。</li>
 * <li><b>有限序列</b>：<strong>必须</strong>重写{@link #hasNext()}方法，在序列耗尽时返回{@code false}。</li>
 * <li><b>异常处理</b>：当{@link #hasNext()}返回{@code false}时，调用{@link #next()}必须抛出{@link UnsupportedOperationException}。</li>
 * <li><b>线程安全</b>：如果序列可能被多个线程并发访问，实现者必须确保{@link #next()}方法的线程安全性，或在文档中明确说明其非线程安全特性。</li>
 * <li><b>空值禁止</b>：{@link #next()}方法绝对不能返回{@code null}。</li>
 * </ul>
 * </p>
 *
 * <p>
 * <strong>典型应用场景：</strong>
 * <ul>
 * <li>分布式唯一ID生成器</li>
 * <li>业务订单号、流水号生成</li>
 * <li>数据库主键生成策略</li>
 * <li>测试数据或样本数据生成</li>
 * <li>任何需要连续、有序、唯一值的场景</li>
 * </ul>
 *
 * @author soeasy.run
 * @param <T> 序列元素的类型
 * @see Iterator
 * @see Stream
 * @see Function
 */
@FunctionalInterface
public interface Sequence<T> extends Iterator<T> {

	/**
	 * <p>
	 * 一个静态工厂方法，用于将任意{@link Iterator}适配为{@code Sequence}。
	 * </p>
	 * <p>
	 * 这是一个非常实用的适配器，可以将现有的迭代器（例如来自集合、流或其他数据源）无缝桥接到
	 * {@code Sequence}框架中，从而利用{@code Sequence}提供的丰富功能（如{@link #map},
	 * {@link #withPrefetch}等）。
	 * 生成的{@code Sequence}是有限的，其{@link #hasNext()}方法会委托给原始迭代器。
	 * </p>
	 *
	 * @param <E>      迭代器元素的类型
	 * @param iterator 要被适配的迭代器，不能为{@code null}
	 * @return 一个新的{@code Sequence}实例，其元素由输入的{@code iterator}提供
	 * @throws NullPointerException 如果{@code iterator}为{@code null}
	 */
	public static <E> Sequence<E> unknownSize(@NonNull Iterator<? extends E> iterator) {
		return new IteratorToSequence<>(iterator);
	}

	/**
	 * 判断序列是否还有下一个元素。
	 * <p>
	 * <b>Sequence特定契约：</b>
	 * <ul>
	 * <li><i>无限序列</i>：无需重写，默认返回{@code true}。</li>
	 * <li><i>有限序列</i>：<strong>必须</strong>重写此方法。当序列中没有更多元素时，必须返回{@code false}。</li>
	 * </ul>
	 *
	 * @return 如果存在下一个元素，则为{@code true}；否则为{@code false}
	 */
	@Override
	default boolean hasNext() {
		return true;
	}

	/**
	 * 获取序列中的下一个元素。
	 * <p>
	 * <b>Sequence特定契约：</b>
	 * <ul>
	 * <li><b>顺序性</b>：多次调用的返回值必须具有可预测的顺序。</li>
	 * <li><b>唯一性</b>：在其设计的上下文中（如分布式环境）应保证返回值的唯一性。</li>
	 * <li><b>非空性</b>：返回的元素绝不能为{@code null}。</li>
	 * <li><b>耗尽处理</b>：如果{@link #hasNext()}返回{@code false}，调用此方法必须抛出{@link UnsupportedOperationException}。</li>
	 * </ul>
	 *
	 * @return 序列中的下一个非空元素
	 * @throws NoSuchElementException 如果序列已耗尽（即{@link #hasNext()}为{@code false}）
	 */
	@NonNull
	T next() throws NoSuchElementException;

	/**
	 * 从基础集合中移除迭代器返回的最后一个元素。
	 * <p>
	 * <b>Sequence特定说明：</b> 此操作对于纯序列生成器是不支持的，因为序列不维护一个可变的集合状态。
	 *
	 * @throws UnsupportedOperationException 总是抛出此异常
	 */
	@Override
	default void remove() throws UnsupportedOperationException {
		throw new UnsupportedOperationException("remove() is not supported by Sequence, as it is a pure generator.");
	}

	/**
	 * 将此序列转换为一个连续的{@link Stream}。
	 * <p>
	 * 对于无限序列，在终端操作中应使用短路操作（如{@code limit()}）以防止无限循环。
	 *
	 * @return 包含此序列元素的{@link Stream}
	 * @see CollectionUtils#unknownSizeStream(Iterator)
	 */
	default Stream<T> stream() {
		return CollectionUtils.unknownSizeStream(this);
	}

	/**
	 * <p>
	 * 返回一个被截断的序列，其最大元素数量不超过指定的{@code maxSize}。
	 * </p>
	 * <p>
	 * 这是一种便捷的方式来创建一个有限的序列。它对于处理无限序列或限制从大型序列中获取的元素数量非常有用。
	 * 新序列的{@link #hasNext()}方法会在返回了{@code maxSize}个元素后返回{@code false}。
	 * </p>
	 *
	 * @param maxSize 新序列所能包含的最大元素数量
	 * @return 一个包含当前序列前{@code maxSize}个元素的新{@code Sequence}
	 */
	default Sequence<T> limit(long maxSize) {
		return Sequence.unknownSize(this.stream().limit(maxSize).iterator());
	}

	/**
	 * <p>
	 * 创建一个当前序列的快照（Snapshot）。
	 * </p>
	 * <p>
	 * 此方法会从当前序列中获取最多{@code maxSize}个元素，将它们存入一个线程安全的队列中，
	 * 并返回一个基于此队列的新{@code Sequence}。这个新序列是原始序列的一个独立副本。
	 * </p>
	 * <p>
	 * 与{@link #withPrefetch(long)}不同，{@code snapshot}是一个一次性操作，它会立即消耗原始序列中的元素。
	 * 而{@code withPrefetch}是一个装饰器，它会在后台动态、按需地预取元素。
	 * </p>
	 *
	 * @param maxSize 要获取的快照元素数量
	 * @return 一个包含快照元素的新{@code Sequence}
	 */
	default Sequence<T> snapshot(long maxSize) {
		Queue<T> queue = this.stream().limit(maxSize).collect(Collectors.toCollection(ConcurrentLinkedQueue::new));
		return new QueueSequence<>(queue);
	}

	/**
	 * <p>
	 * 返回一个带有本地预取缓存功能的新{@link Sequence}。
	 * </p>
	 * <p>
	 * 这是一个强大的性能优化工具。通过批量预取并缓存一定数量的元素，可以显著减少对底层序列（特别是那些
	 * 获取成本高昂的序列，如数据库或网络调用）的频繁访问，从而在高并发场景下极大地提升吞吐量和响应速度。
	 * </p>
	 * <p>
	 * 与{@link #snapshot(long)}不同，{@code withPrefetch}是一个装饰器，它不会立即消耗原始序列。
	 * 它会在内部维护一个缓存，只有当缓存耗尽时，才会从原始序列中预取下一批元素。
	 * </p>
	 *
	 * @param batchSize 每次从当前序列预取的元素数量，必须大于0。
	 * @return 一个新的、带有预取功能的装饰器{@link Sequence}。
	 * @throws IllegalArgumentException 如果{@code batchSize}小于或等于0。
	 */
	default Sequence<T> withPrefetch(long batchSize) {
		if (batchSize <= 0) {
			throw new IllegalArgumentException("batchSize must be greater than 0");
		}
		return new PrefetchingSequence<>(this, batchSize);
	}

	/**
	 * 通过一个映射函数将当前序列的元素转换为另一种类型，返回一个新的序列。
	 * <p>
	 * 这是一种函数式转换，新序列的元素是通过对原序列的每个元素应用{@code mapper}函数得到的。 新序列的耗尽特性（有限或无限）与原序列一致。
	 * </p>
	 * <p>
	 * 此方法遵循延迟执行（lazy evaluation）原则，即映射操作是在新序列的{@link #next()}方法被调用时才执行的。
	 * </p>
	 *
	 * @param <R>    新序列元素的类型
	 * @param mapper 一个非空的函数，用于将类型{@code T}的元素映射到类型{@code R}
	 * @return 一个由映射后的元素组成的新{@code Sequence}
	 * @throws NullPointerException 如果{@code mapper}为{@code null}
	 * @see Function
	 */
	default <R> Sequence<R> map(@NonNull Function<? super T, ? extends R> mapper) {
		return new MappedSequence<>(this, mapper);
	}
}