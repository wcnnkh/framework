package run.soeasy.framework.core.collection;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.Collector;
import java.util.stream.Stream;

import run.soeasy.framework.core.domain.Wrapper;
import run.soeasy.framework.core.function.ThrowingConsumer;
import run.soeasy.framework.core.function.ThrowingFunction;

/**
 * 提供对Streamable对象的包装功能，允许对可流操作对象进行封装并操作原始对象。
 * 实现此接口的类可以将Streamable对象包装起来，提供额外的功能或修改默认行为。
 *
 * @param <E> 元素的类型
 * @param <W> 被包装的Streamable类型
 * 
 * @author soeasy.run
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
     * 检查当前流与目标流中是否存在至少一对元素满足给定的二元谓词。
     *
     * @param target 目标流
     * @param predicate 用于测试元素对的二元谓词
     * @param <T> 目标流的元素类型
     * @return 如果存在满足条件的元素对则返回true，否则返回false
     */
    @Override
    default <T> boolean anyMatch(Streamable<T> target, BiPredicate<? super E, ? super T> predicate) {
        return getSource().anyMatch(target, predicate);
    }

    /**
     * 使用Collector对流中的元素进行收集操作。
     *
     * @param collector 收集器，定义了收集的规则
     * @param <R> 收集结果的类型
     * @param <A> 收集过程中的中间容器类型
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
     * 检查当前流与目标流是否在给定的二元谓词下相等。
     *
     * @param streamable 目标流
     * @param predicate 用于比较元素的二元谓词
     * @param <T> 目标流的元素类型
     * @return 如果所有对应元素都满足谓词则返回true，否则返回false
     */
    @Override
    default <T> boolean equals(Streamable<? extends T> streamable, BiPredicate<? super E, ? super T> predicate) {
        return getSource().equals(streamable, predicate);
    }

    /**
     * 使用抛出异常的函数处理流，并导出处理结果。
     *
     * @param processor 处理流的函数，可以抛出异常
     * @param <T> 处理结果的类型
     * @param <X> 可能抛出的异常类型
     * @return 处理后的结果
     * @throws X 如果处理器抛出指定类型的异常
     */
    @Override
    default <T, X extends Throwable> T export(
            ThrowingFunction<? super Stream<E>, ? extends T, ? extends X> processor) throws X {
        return getSource().export(processor);
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
     * @throws NoSuchElementException 如果流为空
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
     * 根据比较器返回流中的最大元素，可能为空。
     *
     * @param comparator 用于比较元素的比较器
     * @return 包含最大元素的Optional，若无元素则为empty
     */
    @Override
    default Optional<E> max(Comparator<? super E> comparator) {
        return getSource().max(comparator);
    }

    /**
     * 根据比较器返回流中的最小元素，可能为空。
     *
     * @param comparator 用于比较元素的比较器
     * @return 包含最小元素的Optional，若无元素则为empty
     */
    @Override
    default Optional<E> min(Comparator<? super E> comparator) {
        return getSource().min(comparator);
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
     * 使用二进制操作符对流中的元素进行归约操作，可能为空。
     *
     * @param accumulator 用于归约的二进制操作符
     * @return 包含归约结果的Optional，若无元素则为empty
     */
    @Override
    default Optional<E> reduce(BinaryOperator<E> accumulator) {
        return getSource().reduce(accumulator);
    }

    /**
     * 使用二进制操作符对流中的元素进行归约操作，从给定的初始值开始。
     *
     * @param identity 归约操作的初始值
     * @param accumulator 用于归约的二进制操作符
     * @return 归约后的结果
     */
    @Override
    default E reduce(E identity, BinaryOperator<E> accumulator) {
        return getSource().reduce(identity, accumulator);
    }

    /**
     * 使用二进制操作符对流中的元素进行归约操作，支持并行处理。
     *
     * @param identity 归约操作的初始值
     * @param accumulator 用于归约的累加操作符
     * @param combiner 用于合并并行归约结果的操作符
     * @param <U> 归约结果的类型
     * @return 归约后的结果
     */
    @Override
    default <U> U reduce(U identity, BiFunction<U, ? super E, U> accumulator, BinaryOperator<U> combiner) {
        return getSource().reduce(identity, accumulator, combiner);
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
    default boolean test(Predicate<? super Stream<E>> predicate) {
        return getSource().test(predicate);
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
     * @param <A> 数组的类型
     * @return 包含所有元素的数组
     */
    @Override
    default <A> A[] toArray(IntFunction<A[]> generator) {
        return getSource().toArray(generator);
    }

    /**
     * 返回包含流中所有元素的数组，数组类型由参数指定。
     *
     * @param array 用于存储元素的数组
     * @param <T> 数组的类型
     * @return 包含所有元素的数组
     */
    @Override
    default <T> T[] toArray(T[] array) {
        return getSource().toArray(array);
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
     * @param <K> 键的类型
     * @return 包含流中元素的Map，键由keyMapper生成
     */
    @Override
    default <K> Map<K, E> toMap(Function<? super E, ? extends K> keyMapper) {
        return getSource().toMap(keyMapper);
    }

    /**
     * 将流转换为Map，键和值分别由给定的映射函数确定。
     *
     * @param keyMapper 用于生成键的映射函数
     * @param valueMapper 用于生成值的映射函数
     * @param <K> 键的类型
     * @param <V> 值的类型
     * @return 包含流中元素的Map，键和值由映射函数生成
     */
    @Override
    default <K, V> Map<K, V> toMap(Function<? super E, ? extends K> keyMapper,
            Function<? super E, ? extends V> valueMapper) {
        return getSource().toMap(keyMapper, valueMapper);
    }

    /**
     * 将流转换为指定类型的Map，键和值由映射函数确定，Map由供应商提供。
     *
     * @param keyMapper 用于生成键的映射函数
     * @param valueMapper 用于生成值的映射函数
     * @param mapSupplier 用于提供Map实例的供应商
     * @param <K> 键的类型
     * @param <V> 值的类型
     * @param <M> Map的类型
     * @return 包含流中元素的Map，由mapSupplier提供实例
     */
    @Override
    default <K, V, M extends Map<K, V>> M toMap(Function<? super E, ? extends K> keyMapper,
            Function<? super E, ? extends V> valueMapper, Supplier<? extends M> mapSupplier) {
        return getSource().toMap(keyMapper, valueMapper, mapSupplier);
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
     * @param <X> 可能抛出的异常类型
     * @throws X 如果处理器抛出指定类型的异常
     */
    @Override
    default <X extends Throwable> void transfer(ThrowingConsumer<? super Stream<E>, ? extends X> processor)
            throws X {
        getSource().transfer(processor);
    }

    /**
     * 根据给定的哈希函数计算流的哈希码。
     *
     * @param hash 用于计算元素哈希值的函数
     * @return 流的哈希码
     */
    @Override
    default int hashCode(ToIntFunction<? super E> hash) {
        return getSource().hashCode(hash);
    }
}