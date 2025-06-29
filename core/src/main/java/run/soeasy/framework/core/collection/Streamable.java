package run.soeasy.framework.core.collection;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.NonNull;
import run.soeasy.framework.core.ObjectUtils;
import run.soeasy.framework.core.function.ThrowingConsumer;
import run.soeasy.framework.core.function.ThrowingFunction;

/**
 * 可流式接口
 * 提供流式操作能力，支持各种集合操作并确保资源正确关闭
 * 
 * @author soeasy.run
 *
 * @param <E> 元素类型
 */
@FunctionalInterface
public interface Streamable<E> {
    /**
     * 创建空的可流式对象
     * 
     * @param <T> 元素类型
     * @return 空的可流式对象
     */
    @SuppressWarnings("unchecked")
    public static <T> Streamable<T> empty() {
        return (Streamable<T>) EmptyStreamable.EMPTY_STREAMABLE;
    }

    /**
     * 判断所有元素是否都匹配指定条件
     * 
     * @param predicate 条件判断函数
     * @return 所有元素都匹配返回true，否则返回false
     */
    default boolean allMatch(Predicate<? super E> predicate) {
        return test((stream) -> stream.allMatch(predicate));
    }

    /**
     * 判断是否存在任何元素匹配指定条件
     * 
     * @param predicate 条件判断函数
     * @return 存在元素匹配返回true，否则返回false
     */
    default boolean anyMatch(Predicate<? super E> predicate) {
        return test((stream) -> stream.anyMatch(predicate));
    }

    /**
     * 判断当前可流式对象中的任何元素是否与目标可流式对象中的任何元素匹配指定条件
     * 
     * @param <T> 目标元素类型
     * @param target 目标可流式对象
     * @param predicate 二元条件判断函数
     * @return 存在匹配返回true，否则返回false
     */
    default <T> boolean anyMatch(Streamable<T> target, BiPredicate<? super E, ? super T> predicate) {
        return anyMatch((s) -> target.anyMatch(((t) -> predicate.test(s, t))));
    }

    /**
     * 使用指定收集器收集元素
     * 
     * @param <R> 结果类型
     * @param <A> 中间累积类型
     * @param collector 收集器
     * @return 收集结果
     */
    default <R, A> R collect(Collector<? super E, A, R> collector) {
        return export((stream) -> stream.collect(collector));
    }

    /**
     * 判断是否包含指定元素
     * 
     * @param element 要检查的元素
     * @return 包含返回true，否则返回false
     */
    default boolean contains(Object element) {
        return anyMatch((e) -> e == element || ObjectUtils.equals(e, element));
    }

    /**
     * 计算元素数量
     * 
     * @return 元素数量
     */
    default long count() {
        Stream<E> stream = stream();
        try {
            return stream.count();
        } finally {
            stream.close();
        }
    }

    /**
     * 使用指定的二元谓词比较两个可流式对象是否相等
     * 
     * @param <T> 目标元素类型
     * @param streamable 目标可流式对象
     * @param predicate 二元谓词，用于比较元素
     * @return 如果所有元素都匹配且数量相同返回true，否则返回false
     */
    default <T> boolean equals(@NonNull Streamable<? extends T> streamable,
            @NonNull BiPredicate<? super E, ? super T> predicate) {
        Stream<E> stream = stream();
        try {
            Stream<? extends T> targetStream = streamable.stream();
            try {
                Iterator<E> sourceIterator = stream.iterator();
                Iterator<? extends T> targetIterator = targetStream.iterator();
                while (sourceIterator.hasNext() && targetIterator.hasNext()) {
                    E source = sourceIterator.next();
                    T target = targetIterator.next();
                    if (source == target) {
                        continue;
                    }

                    // 如果都为空已经在上一步拦截了
                    if (source == null || target == null) {
                        return false;
                    }

                    if (predicate.test(source, target)) {
                        continue;
                    }
                    return false;
                }

                // 都没有了才算相等
                return !sourceIterator.hasNext() && !targetIterator.hasNext();
            } finally {
                targetStream.close();
            }
        } finally {
            stream.close();
        }
    }

    /**
     * 使用指定处理器处理流并返回结果
     * 
     * @param <T> 结果类型
     * @param <X> 异常类型
     * @param processor 流处理器
     * @return 处理结果
     * @throws X 可能抛出的异常
     */
    default <T, X extends Throwable> T export(ThrowingFunction<? super Stream<E>, ? extends T, ? extends X> processor)
            throws X {
        Stream<E> stream = stream();
        try {
            return processor.apply(stream);
        } finally {
            stream.close();
        }
    }

    /**
     * 返回任意一个元素的Optional
     * 
     * @return 包含任意元素的Optional，可能为空
     */
    default Optional<E> findAny() {
        return export((stream) -> stream.findAny());
    }

    /**
     * 返回第一个元素的Optional
     * 
     * @return 包含第一个元素的Optional，可能为空
     */
    default Optional<E> findFirst() {
        return export((stream) -> stream.findFirst());
    }

    /**
     * 返回第一个元素，如果没有元素则返回null
     * 
     * @return 第一个元素或null
     */
    default E first() {
        return export((stream) -> {
            Iterator<E> iterator = stream.iterator();
            return iterator.hasNext() ? iterator.next() : null;
        });
    }

    /**
     * 对每个元素执行指定操作
     * 
     * @param action 操作
     */
    default void forEach(@NonNull Consumer<? super E> action) {
        transfer((stream) -> stream.forEach(action));
    }

    /**
     * 按顺序对每个元素执行指定操作
     * 
     * @param action 操作
     */
    default void forEachOrdered(@NonNull Consumer<? super E> action) {
        transfer((stream) -> stream.forEachOrdered(action));
    }

    /**
     * 获取唯一的元素
     * 
     * @return 唯一元素
     * @throws NoSuchElementException 如果没有元素
     * @throws NoUniqueElementException 如果存在多个元素
     */
    default E getUnique() throws NoSuchElementException, NoUniqueElementException {
        return export((stream) -> {
            Iterator<E> iterator = stream.iterator();
            if (!iterator.hasNext()) {
                throw new NoSuchElementException();
            }

            // 向后迭代一次
            E element = iterator.next();
            if (iterator.hasNext()) {
                // 如果还有说明不是只有一个
                throw new NoUniqueElementException();
            }
            return element;
        });
    }

    /**
     * 计算哈希值
     * 
     * @param hash 哈希函数
     * @return 哈希值
     */
    default int hashCode(@NonNull ToIntFunction<? super E> hash) {
        Stream<E> stream = stream();
        try {
            Iterator<E> iterator = stream.iterator();
            if (!iterator.hasNext()) {
                return 0;
            }

            int result = 1;
            while (iterator.hasNext()) {
                E element = iterator.next();
                result = 31 * result + (element == null ? 0 : hash.applyAsInt(element));
            }
            return result;
        } finally {
            stream.close();
        }
    }

    /**
     * 判断是否为空
     * 
     * @return 为空返回true，否则返回false
     */
    default boolean isEmpty() {
        return !findAny().isPresent();
    }

    /**
     * 判断是否只有一个元素
     * 
     * @return 只有一个元素返回true，否则返回false
     */
    default boolean isUnique() {
        return test((stream) -> {
            Iterator<E> iterator = stream.iterator();
            if (!iterator.hasNext()) {
                return false;
            }

            // 向后迭代一次
            iterator.next();
            if (iterator.hasNext()) {
                // 如果还有说明不是只有一个
                return false;
            }
            return true;
        });
    }

    /**
     * 返回最后一个元素，如果没有元素则返回null
     * 
     * @return 最后一个元素或null
     */
    default E last() {
        return export((stream) -> {
            Iterator<E> iterator = stream.iterator();
            while (iterator.hasNext()) {
                E e = iterator.next();
                if (!iterator.hasNext()) {
                    return e;
                }
            }
            return null;
        });
    }

    /**
     * 返回最大元素的Optional
     * 
     * @param comparator 比较器
     * @return 包含最大元素的Optional，可能为空
     */
    default Optional<E> max(Comparator<? super E> comparator) {
        return export((stream) -> stream.max(comparator));
    }

    /**
     * 返回最小元素的Optional
     * 
     * @param comparator 比较器
     * @return 包含最小元素的Optional，可能为空
     */
    default Optional<E> min(Comparator<? super E> comparator) {
        return export((stream) -> stream.min(comparator));
    }

    /**
     * 判断是否没有元素匹配指定条件
     * 
     * @param predicate 条件判断函数
     * @return 没有元素匹配返回true，否则返回false
     */
    default boolean noneMatch(Predicate<? super E> predicate) {
        return test((stream) -> stream.noneMatch(predicate));
    }

    /**
     * 使用指定的累积器归约元素
     * 
     * @param accumulator 累积器
     * @return 包含归约结果的Optional，可能为空
     */
    default Optional<E> reduce(BinaryOperator<E> accumulator) {
        return export((stream) -> stream.reduce(accumulator));
    }

    /**
     * 使用指定的初始值和累积器归约元素
     * 
     * @param identity 初始值
     * @param accumulator 累积器
     * @return 归约结果
     */
    default E reduce(E identity, BinaryOperator<E> accumulator) {
        return export((stream) -> stream.reduce(identity, accumulator));
    }

    /**
     * 使用指定的初始值、累积器和组合器归约元素
     * 
     * @param <U> 结果类型
     * @param identity 初始值
     * @param accumulator 累积器
     * @param combiner 组合器
     * @return 归约结果
     */
    default <U> U reduce(U identity, BiFunction<U, ? super E, U> accumulator, BinaryOperator<U> combiner) {
        return export((stream) -> stream.reduce(identity, accumulator, combiner));
    }

    /**
     * 获取流，使用后需关闭
     * 
     * @return 元素流
     */
    Stream<E> stream();

    /**
     * 使用指定的谓词测试流
     * 
     * @param predicate 谓词
     * @return 测试结果
     */
    default boolean test(@NonNull Predicate<? super Stream<E>> predicate) {
        Stream<E> stream = stream();
        try {
            return predicate.test(stream);
        } finally {
            stream.close();
        }
    }

    /**
     * 转换为数组
     * 
     * @return 对象数组
     */
    default Object[] toArray() {
        return toList().toArray();
    }

    /**
     * 转换为指定类型的数组
     * 
     * @param <A> 数组元素类型
     * @param generator 数组生成器
     * @return 指定类型的数组
     */
    default <A> A[] toArray(IntFunction<A[]> generator) {
        return export((stream) -> stream.toArray(generator));
    }

    /**
     * 转换为指定类型的数组
     * 
     * @param <T> 数组元素类型
     * @param array 目标数组
     * @return 包含元素的数组
     */
    default <T> T[] toArray(T[] array) {
        return toList().toArray(array);
    }

    /**
     * 转换为列表
     * 
     * @return 元素列表
     */
    default List<E> toList() {
        return collect(Collectors.toList());
    }

    /**
     * 转换为Map，键由键映射函数生成，值为元素本身
     * 
     * @param <K> 键类型
     * @param keyMapper 键映射函数
     * @return 映射结果
     */
    default <K> Map<K, E> toMap(Function<? super E, ? extends K> keyMapper) {
        return toMap(keyMapper, Function.identity());
    }

    /**
     * 转换为Map，键和值分别由键映射函数和值映射函数生成
     * 
     * @param <K> 键类型
     * @param <V> 值类型
     * @param keyMapper 键映射函数
     * @param valueMapper 值映射函数
     * @return 映射结果
     */
    default <K, V> Map<K, V> toMap(Function<? super E, ? extends K> keyMapper,
            Function<? super E, ? extends V> valueMapper) {
        return toMap(keyMapper, valueMapper, () -> new LinkedHashMap<>());
    }

    /**
     * 转换为指定类型的Map，键和值分别由键映射函数和值映射函数生成
     * 
     * @param <K> 键类型
     * @param <V> 值类型
     * @param <M> 映射类型
     * @param keyMapper 键映射函数
     * @param valueMapper 值映射函数
     * @param mapSupplier 映射供应商
     * @return 映射结果
     */
    default <K, V, M extends Map<K, V>> M toMap(Function<? super E, ? extends K> keyMapper,
            Function<? super E, ? extends V> valueMapper, Supplier<? extends M> mapSupplier) {
        return collect(Collectors.toMap(keyMapper, valueMapper, (u, v) -> {
            throw new IllegalStateException(String.format("Duplicate key %s", u));
        }, mapSupplier));
    }

    /**
     * 转换为集合
     * 
     * @return 元素集合
     */
    default Set<E> toSet() {
        return collect(Collectors.toCollection(() -> new LinkedHashSet<>()));
    }

    /**
     * 使用指定的消费者处理流
     * 
     * @param <X> 异常类型
     * @param processor 流处理器
     * @throws X 可能抛出的异常
     */
    default <X extends Throwable> void transfer(ThrowingConsumer<? super Stream<E>, ? extends X> processor) throws X {
        Stream<E> stream = stream();
        try {
            processor.accept(stream);
        } finally {
            stream.close();
        }
    }
}