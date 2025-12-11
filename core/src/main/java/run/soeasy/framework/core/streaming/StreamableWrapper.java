package run.soeasy.framework.core.streaming;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

import lombok.NonNull;
import run.soeasy.framework.core.collection.CloseableIterator;
import run.soeasy.framework.core.domain.KeyValue;
import run.soeasy.framework.core.domain.Wrapper;
import run.soeasy.framework.core.function.ThrowingConsumer;
import run.soeasy.framework.core.function.ThrowingFunction;

/**
 * Streamable包装器核心接口：封装对{@link Streamable}实例的包装能力，所有{@link Streamable}方法默认透传至被包装的源实例
 * <p>
 * 核心设计： 1.
 * 继承{@link Streamable}和{@link Wrapper}，兼具流式操作能力和包装器语义，实现类只需实现{@link Wrapper#getSource()}即可复用所有Streamable默认逻辑；
 * 2. 全方法透传：所有Streamable的操作方法（过滤、映射、收集、遍历等）均默认调用源实例的对应方法，无需重复实现； 3.
 * 扩展友好：实现类可按需重写任意方法，覆盖默认透传逻辑，实现自定义增强（如添加日志、缓存、权限校验等）； 4.
 * 语义闭环：reload/iterator/enumeration等核心方法也透传至源实例，保证动态数据源刷新、资源关闭等特性一致。
 * </p>
 * <b>使用约束</b>： -
 * 实现类必须重写{@link Wrapper#getSource()}，返回非空的被包装源{@link Streamable}实例； -
 * 泛型W限定为Streamable&lt;E&gt;的子类型，保证源实例类型匹配； -
 * 如需自定义行为，仅需重写目标方法（如在forEach前添加日志），未重写方法自动透传。
 *
 * @param <E> 流式操作的元素类型
 * @param <W> 被包装的源Streamable子类型，限定为Streamable&lt;E&gt;的实现类
 * @author soeasy.run
 * @see Streamable
 * @see Wrapper
 * @see CloseableIterator
 */
public interface StreamableWrapper<E, W extends Streamable<E>> extends Streamable<E>, Wrapper<W> {

	/**
	 * 检查流中的所有元素是否都满足给定的谓词。
	 *
	 * @param predicate 用于测试元素的谓词
	 * @return 如果所有元素都满足谓词则返回true，否则返回false
	 */
	@Override
	default boolean allMatch(Predicate<? super E> predicate) {
		return getSource().allMatch(predicate);
	}

	/**
	 * 检查流中是否存在至少一个元素满足给定的谓词。
	 *
	 * @param predicate 用于测试元素的谓词
	 * @return 如果存在至少一个元素满足谓词则返回true，否则返回false
	 */
	@Override
	default boolean anyMatch(Predicate<? super E> predicate) {
		return getSource().anyMatch(predicate);
	}

	/**
	 * 使用Collector对流中的元素进行收集操作。
	 *
	 * @param collector 收集器，定义了收集的规则
	 * @param <R>       收集结果的类型
	 * @param <A>       收集过程中的中间容器类型
	 * @return 收集操作的结果
	 */
	@Override
	default <R, A> R collect(Collector<? super E, A, R> collector) {
		return getSource().collect(collector);
	}

	/**
	 * 检查流中是否包含指定的元素。
	 *
	 * @param element 要检查的元素
	 * @return 如果流包含该元素则返回true，否则返回false
	 */
	@Override
	default boolean contains(Object element) {
		return getSource().contains(element);
	}

	/**
	 * 返回流中元素的数量。
	 *
	 * @return 流中元素的数量
	 */
	@Override
	default long count() {
		return getSource().count();
	}

	/**
	 * 使用抛出异常的函数处理流，并导出处理结果。
	 *
	 * @param processor 处理流的函数，可以抛出异常
	 * @param <T>       处理结果的类型
	 * @param <X>       可能抛出的异常类型
	 * @return 处理后的结果
	 * @throws X 如果处理器抛出指定类型的异常
	 */
	@Override
	default <T, X extends Throwable> T extract(ThrowingFunction<? super Stream<E>, ? extends T, ? extends X> processor)
			throws X {
		return getSource().extract(processor);
	}

	/**
	 * 返回流中的任意一个元素，可能为空。
	 *
	 * @return 包含任意元素的Optional，若无元素则为empty
	 */
	@Override
	default Optional<E> findAny() {
		return getSource().findAny();
	}

	/**
	 * 返回流中的第一个元素，可能为空。
	 *
	 * @return 包含第一个元素的Optional，若无元素则为empty
	 */
	@Override
	default Optional<E> findFirst() {
		return getSource().findFirst();
	}

	/**
	 * 返回流中的第一个元素，若流为空则抛出异常。
	 *
	 * @return 流中的第一个元素
	 * @throws NoSuchElementException 如果流为空
	 */
	@Override
	default E first() {
		return getSource().first();
	}

	/**
	 * 对流中的每个元素执行给定的操作。
	 *
	 * @param action 要执行的操作
	 */
	@Override
	default void forEach(Consumer<? super E> action) {
		getSource().forEach(action);
	}

	/**
	 * 按流的 encounter order 对流中的每个元素执行给定的操作。
	 *
	 * @param action 要执行的操作
	 */
	@Override
	default void forEachOrdered(Consumer<? super E> action) {
		getSource().forEachOrdered(action);
	}

	/**
	 * 返回流中唯一的元素，若流为空或元素不唯一则抛出异常。
	 *
	 * @return 流中唯一的元素
	 * @throws NoSuchElementException   如果流为空
	 * @throws NoUniqueElementException 如果流中有多个元素
	 */
	@Override
	default E getUnique() throws NoSuchElementException, NoUniqueElementException {
		return getSource().getUnique();
	}

	/**
	 * 检查流是否为空。
	 *
	 * @return 如果流中没有元素则返回true，否则返回false
	 */
	@Override
	default boolean isEmpty() {
		return getSource().isEmpty();
	}

	/**
	 * 检查流中的元素是否唯一。
	 *
	 * @return 如果流中元素唯一或为空则返回true，否则返回false
	 */
	@Override
	default boolean isUnique() {
		return getSource().isUnique();
	}

	/**
	 * 返回流中的最后一个元素，若流为空则抛出异常。
	 *
	 * @return 流中的最后一个元素
	 * @throws NoSuchElementException 如果流为空
	 */
	@Override
	default E last() {
		return getSource().last();
	}

	/**
	 * 检查流中是否没有元素满足给定的谓词。
	 *
	 * @param predicate 用于测试元素的谓词
	 * @return 如果没有元素满足谓词则返回true，否则返回false
	 */
	@Override
	default boolean noneMatch(Predicate<? super E> predicate) {
		return getSource().noneMatch(predicate);
	}

	/**
	 * 返回流的Stream表示。
	 *
	 * @return 流的Stream实例
	 */
	@Override
	default Stream<E> stream() {
		return getSource().stream();
	}

	/**
	 * 测试流是否满足给定的谓词。
	 *
	 * @param predicate 用于测试流的谓词
	 * @return 如果流满足谓词则返回true，否则返回false
	 */
	@Override
	default boolean check(Predicate<? super Stream<E>> predicate) {
		return getSource().check(predicate);
	}

	/**
	 * 返回包含流中所有元素的数组。
	 *
	 * @return 包含所有元素的数组
	 */
	@Override
	default Object[] toArray() {
		return getSource().toArray();
	}

	/**
	 * 返回包含流中所有元素的数组，使用给定的生成函数创建数组。
	 *
	 * @param generator 用于创建数组的函数
	 * @param <A>       数组的类型
	 * @return 包含所有元素的数组
	 */
	@Override
	default <A> A[] toArray(IntFunction<A[]> generator) {
		return getSource().toArray(generator);
	}

	/**
	 * 将流转换为List。
	 *
	 * @return 包含流中所有元素的List
	 */
	@Override
	default List<E> toList() {
		return getSource().toList();
	}

	/**
	 * 将流转换为Map，键由给定的映射函数确定。
	 *
	 * @param keyMapper 用于生成键的映射函数
	 * @param <K>       键的类型
	 * @return 包含流中元素的Map，键由keyMapper生成
	 */
	@Override
	default <K> Map<K, E> toMap(@NonNull Function<? super E, ? extends K> keyMapper) {
		return getSource().toMap(keyMapper);
	}

	@Override
	default <K> Map<K, E> toMap(@NonNull Function<? super E, ? extends K> keyMapper,
			@NonNull BinaryOperator<E> mergeFunction) {
		return getSource().toMap(keyMapper, mergeFunction);
	}

	/**
	 * 将流转换为Set。
	 *
	 * @return 包含流中所有元素的Set
	 */
	@Override
	default Set<E> toSet() {
		return getSource().toSet();
	}

	/**
	 * 使用抛出异常的消费者处理流。
	 *
	 * @param processor 处理流的消费者，可以抛出异常
	 * @param <X>       可能抛出的异常类型
	 * @throws X 如果处理器抛出指定类型的异常
	 */
	@Override
	default <X extends Throwable> void consume(@NonNull ThrowingConsumer<? super Stream<E>, ? extends X> processor)
			throws X {
		getSource().consume(processor);
	}

	@Override
	default <T> Streamable<T> transform(@NonNull Function<? super Stream<E>, ? extends Stream<T>> mapper) {
		return getSource().transform(mapper);
	}

	@Override
	default <T> Streamable<T> map(@NonNull Function<? super E, ? extends T> mapper) {
		return getSource().map(mapper);
	}

	@Override
	default Streamable<E> filter(@NonNull Predicate<? super E> predicate) {
		return getSource().filter(predicate);
	}

	@Override
	default <T> Streamable<T> flatMap(@NonNull Function<? super E, ? extends Stream<T>> mapper) {
		return getSource().flatMap(mapper);
	}

	@SuppressWarnings("unchecked")
	@Override
	default Streamable<E> concat(E... values) {
		return getSource().concat(values);
	}

	@Override
	default Streamable<E> concat(Streamable<? extends E> streamable) {
		return getSource().concat(streamable);
	}

	@Override
	default Streamable<E> concat(Supplier<? extends Stream<? extends E>> streamSupplier) {
		return getSource().concat(streamSupplier);
	}

	@Override
	default <T> Streamable<T> mapPure(@NonNull Function<? super E, ? extends T> mapper) {
		return getSource().mapPure(mapper);
	}

	@Override
	default Streamable<E> reload() {
		return getSource().reload();
	}

	@Override
	default Streamable<E> cached() {
		return getSource().cached();
	}

	@Override
	default Streamable<E> cached(@NonNull Supplier<? extends Collection<E>> collectionFactory) {
		return getSource().cached(collectionFactory);
	}

	@Override
	default <R> R collect(@NonNull Supplier<R> supplier, @NonNull BiConsumer<R, ? super E> accumulator,
			@NonNull BiConsumer<R, R> combiner) {
		return getSource().collect(supplier, accumulator, combiner);
	}

	@Override
	default Streamable<E> distinct() {
		return getSource().distinct();
	}

	default E getAt(int index) throws IndexOutOfBoundsException {
		return getSource().getAt(index);
	}

	@Override
	default Streamable<E> limit(long maxSize) {
		return getSource().limit(maxSize);
	}

	@Override
	default Optional<E> max(@NonNull Comparator<? super E> comparator) {
		return getSource().max(comparator);
	}

	@Override
	default Optional<E> min(@NonNull Comparator<? super E> comparator) {
		return getSource().min(comparator);
	}

	@Override
	default Streamable<E> onClose(@NonNull Runnable closeHandler) {
		return getSource().onClose(closeHandler);
	}

	@Override
	default Streamable<E> parallel() {
		return getSource().parallel();
	}

	@Override
	default Streamable<E> peek(@NonNull Consumer<? super E> action) {
		return getSource().peek(action);
	}

	@Override
	default Optional<E> reduce(@NonNull BinaryOperator<E> accumulator) {
		return getSource().reduce(accumulator);
	}

	@Override
	default E reduce(E identity, @NonNull BinaryOperator<E> accumulator) {
		return getSource().reduce(identity, accumulator);
	}

	@Override
	default <U> U reduce(U identity, @NonNull BiFunction<U, ? super E, U> accumulator,
			@NonNull BinaryOperator<U> combiner) {
		return getSource().reduce(identity, accumulator, combiner);
	}

	@Override
	default Streamable<E> sequential() {
		return getSource().sequential();
	}

	@Override
	default Streamable<E> skip(long n) {
		return getSource().skip(n);
	}

	@Override
	default Streamable<E> sorted() {
		return getSource().sorted();
	}

	@Override
	default Streamable<E> sorted(@NonNull Comparator<? super E> comparator) {
		return getSource().sorted(comparator);
	}

	@Override
	default Collection<E> toCollection() {
		return getSource().toCollection();
	}

	@Override
	default <C extends Collection<E>> C toCollection(@NonNull Class<?> collectionType) {
		return getSource().toCollection(collectionType);
	}

	@Override
	default Streamable<E> unordered() {
		return getSource().unordered();
	}

	@Override
	default <T, R> Streamable<R> zip(@NonNull Streamable<T> other,
			@NonNull BiFunction<? super E, ? super T, ? extends R> comparator) {
		return getSource().zip(other, comparator);
	}

	@Override
	default <T, R> Streamable<R> zip(@NonNull Streamable<T> other, @NonNull ZipIterator.Rule rule,
			@NonNull BiFunction<? super E, ? super T, ? extends R> combiner) {
		return getSource().zip(other, rule, combiner);
	}

	@Override
	default <T> boolean equalsInAnyOrder(@NonNull Streamable<? extends T> other,
			@NonNull BiPredicate<? super E, ? super T> predicate) {
		return getSource().equalsInAnyOrder(other, predicate);
	}

	@Override
	default <T> boolean equalsInOrder(@NonNull Streamable<? extends T> other,
			@NonNull BiPredicate<? super E, ? super T> predicate) {
		return getSource().equalsInOrder(other, predicate);
	}

	@Override
	default <K, V> Mapping<K, V> toKeyValues(@NonNull Function<? super E, ? extends KeyValue<K, V>> mapper) {
		return getSource().toKeyValues(mapper);
	}

	@Override
	default Optional<E> unique() {
		return getSource().unique();
	}

	@Override
	default Optional<E> at(int index) {
		return getSource().at(index);
	}

	@Override
	default boolean isRandomAccess() {
		return getSource().isRandomAccess();
	}

	@Override
	default <K> Mapping<K, E> groupingBy(@NonNull Function<? super E, ? extends K> keyMapper) {
		return getSource().groupingBy(keyMapper);
	}

	@Override
	default <K, V> Mapping<K, V> toKeyValues(@NonNull Function<? super E, ? extends K> keyMapper,
			@NonNull Function<? super E, ? extends V> valueMapper) {
		return getSource().toKeyValues(keyMapper, valueMapper);
	}

}