package run.soeasy.framework.core.collection;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.NonNull;

/**
 * 元素集合接口
 * 提供无需关闭的迭代器，支持一次性加载或部分加载到内存
 * 继承自Streamable、Iterable和Enumerable接口
 * 
 * @author soeasy.run
 * @param <E> 元素类型
 */
public interface Elements<E> extends Streamable<E>, Iterable<E>, Enumerable<E> {

    @SuppressWarnings("unchecked")
    public static <T> Elements<T> empty() {
        return (Elements<T>) EmptyElements.EMPTY_ELEMENTS;
    }

    /**
     * 创建已知大小的元素集合
     * 
     * @param statisticsSize 用于计算大小的函数，不可为null
     * @return 已知大小的元素集合
     */
    default Elements<E> knownSize(@NonNull ToLongFunction<? super Elements<E>> statisticsSize) {
        return new KnownSizeElements<>(this, statisticsSize);
    }

    /**
     * 从数组创建元素集合
     * 
     * @param elements 数组元素
     * @param <T> 元素类型
     * @return 元素集合
     */
    @SafeVarargs
    public static <T> Elements<T> forArray(T... elements) {
        if (elements == null || elements.length == 0) {
            return empty();
        }
        return Elements.of(Arrays.asList(elements));
    }

    /**
     * 从可枚举对象创建元素集合
     * 
     * @param enumerable 可枚举对象
     * @param <T> 元素类型
     * @return 元素集合
     */
    @SuppressWarnings("unchecked")
    public static <T> Elements<T> of(Enumerable<? extends T> enumerable) {
        if (enumerable == null) {
            return empty();
        }

        if (enumerable instanceof Elements) {
            return (Elements<T>) enumerable;
        }

        Iterable<T> iterable = new EnumerableToIterable<>(enumerable, Function.identity());
        return new StandardIterableElements<>(iterable);
    }

    /**
     * 从可迭代对象创建元素集合
     * 
     * @param iterable 可迭代对象
     * @param <T> 元素类型
     * @return 元素集合
     */
    @SuppressWarnings("unchecked")
    public static <T> Elements<T> of(Iterable<? extends T> iterable) {
        if (iterable == null) {
            return empty();
        }

        if (iterable instanceof Elements) {
            return (Elements<T>) iterable;
        }

        if (iterable instanceof List) {
            return new StandardListElements<>((List<T>) iterable);
        }

        if (iterable instanceof Set) {
            return new StandardSetElements<>((Set<T>) iterable);
        }

        if (iterable instanceof Collection) {
            return new StandardCollectionElements<>((Collection<T>) iterable);
        }
        // 不管为啥,强行转换
        return new StandardIterableElements<>((Iterable<T>) iterable);
    }

    /**
     * 从可流式对象创建元素集合
     * 
     * @param streamable 可流式对象
     * @param <T> 元素类型
     * @return 元素集合
     */
    @SuppressWarnings("unchecked")
    public static <T> Elements<T> of(Streamable<? extends T> streamable) {
        if (streamable == null) {
            return empty();
        }

        if (streamable instanceof Elements) {
            return (Elements<T>) streamable;
        }

        return new StandardStreamableElements<>((Streamable<T>) streamable);
    }

    /**
     * 从供给者创建元素集合
     * 
     * @param supplier 供给者，不可为null
     * @param <T> 元素类型
     * @return 元素集合
     */
    public static <T> Elements<T> forSupplier(@NonNull Supplier<T> supplier) {
        return Elements.of(() -> Stream.generate(supplier));
    }

    /**
     * 创建单元素集合
     * 
     * @param element 元素
     * @param <T> 元素类型
     * @return 单元素集合
     */
    public static <T> Elements<T> singleton(T element) {
        return of(Collections.singleton(element));
    }

    /**
     * 创建可缓存的元素提供者
     * 
     * @return 元素提供者
     */
    default Provider<E> cacheable() {
        return new CacheableElements<>(this, Collectors.toList());
    }

    /**
     * 合并元素集合
     * 
     * @param elements 要合并的元素集合，不可为null
     * @return 合并后的元素集合
     */
    default Elements<E> concat(@NonNull Elements<? extends E> elements) {
        return new MergedElements<>(this, elements);
    }

    /**
     * 转换元素集合
     * 
     * @param <U> 目标元素类型
     * @param resize 转换器是否可能改变大小
     * @param converter 转换函数
     * @return 转换后的元素集合
     */
    default <U> Elements<U> map(boolean resize, Function<? super Stream<E>, ? extends Stream<U>> converter) {
        return new ConvertedElements<>(this, resize, converter);
    }

    /**
     * 去重元素集合
     * 
     * @return 去重后的元素集合
     */
    default Elements<E> distinct() {
        return map(true, (e) -> e.distinct());
    }

    @Override
    default Enumeration<E> enumeration() {
        Iterator<E> iterator = iterator();
        if (iterator == null) {
            // 理论上不会为空
            return null;
        }
        return new IteratorToEnumeration<>(iterator, Function.identity());
    }

    /**
     * 排除满足条件的元素
     * 
     * @param predicate 排除条件，不可为null
     * @return 排除后的元素集合
     */
    default Elements<E> exclude(@NonNull Predicate<? super E> predicate) {
        return filter(predicate.negate());
    }

    /**
     * 过滤元素集合
     * 
     * @param predicate 过滤条件，不可为null
     * @return 过滤后的元素集合
     */
    default Elements<E> filter(@NonNull Predicate<? super E> predicate) {
        return map(true, (stream) -> stream.filter(predicate));
    }

    /**
     * 扁平映射元素集合
     * 
     * @param <U> 目标元素类型
     * @param mapper 映射函数，不可为null
     * @return 扁平映射后的元素集合
     */
    default <U> Elements<U> flatMap(@NonNull Function<? super E, ? extends Streamable<U>> mapper) {
        return map(true, (stream) -> {
            return stream.flatMap((e) -> {
                Streamable<U> streamy = mapper.apply(e);
                return streamy == null ? Stream.empty() : streamy.stream();
            });
        });
    }

    /**
     * 遍历元素集合
     * 默认使用Streamable的forEach实现，因为大多数迭代实现都是一次性加载到内存
     */
    @Override
    default void forEach(Consumer<? super E> action) {
        Streamable.super.forEach(action);
    }

    /**
     * 获取指定索引的元素
     * 
     * @param index 索引
     * @return 指定索引的元素
     * @throws IndexOutOfBoundsException 索引越界时抛出
     */
    default E get(int index) throws IndexOutOfBoundsException {
        return sequential().filter((e) -> e.getIndex() == index).findFirst()
                .orElseThrow(() -> new IndexOutOfBoundsException(String.valueOf(index))).getElement();
    }

    /**
     * 创建顺序元素集合
     * 
     * @return 顺序元素集合
     */
    default Elements<Sequential<E>> sequential() {
        return map(false, (stream) -> {
            Stream<Sequential<E>> newStream = CollectionUtils
                    .unknownSizeStream(new SequentialIterator<>(stream.iterator()));
            return newStream.onClose(stream::close);
        });
    }

    @Override
    Iterator<E> iterator();

    /**
     * 限制元素集合大小
     * 
     * @param maxSize 最大大小
     * @return 限制后的元素集合
     */
    default Elements<E> limit(long maxSize) {
        return map(true, (e) -> e.limit(maxSize));
    }

    /**
     * 映射元素集合
     * 
     * @param <U> 目标元素类型
     * @param mapper 映射函数，不可为null
     * @return 映射后的元素集合
     */
    default <U> Elements<U> map(@NonNull Function<? super E, ? extends U> mapper) {
        return map(false, (stream) -> stream.map(mapper));
    }

    /**
     * 窥视元素集合
     * 
     * @param action 窥视操作，不可为null
     * @return 窥视后的元素集合
     */
    default Elements<E> peek(@NonNull Consumer<? super E> action) {
        return map(false, (e) -> e.peek(action));
    }

    /**
     * 反转元素集合顺序
     * 
     * @return 反转后的元素集合
     */
    default Elements<E> reverse() {
        return map(false, (stream) -> stream.sorted(Collections.reverseOrder()));
    }

    /**
     * 跳过指定数量的元素
     * 
     * @param n 跳过数量
     * @return 跳过指定数量元素后的集合
     */
    default Elements<E> skip(long n) {
        return map(true, (e) -> e.skip(n));
    }

    /**
     * 排序元素集合（使用自然顺序）
     * 
     * @return 排序后的元素集合
     */
    default Elements<E> sorted() {
        return map(false, (e) -> e.sorted());
    }

    /**
     * 排序元素集合（使用指定比较器）
     * 
     * @param comparator 比较器，不可为null
     * @return 排序后的元素集合
     */
    default Elements<E> sorted(@NonNull Comparator<? super E> comparator) {
        return map(false, (e) -> e.sorted(comparator));
    }

    @Override
    default ListElementsWrapper<E, ?> toList() {
        return new ListElements<>(this);
    }

    @Override
    default SetElementsWrapper<E, ?> toSet() {
        return new SetElements<>(this);
    }

    /**
     * 取消元素集合的顺序
     * 
     * @return 取消顺序后的元素集合
     */
    default Elements<E> unordered() {
        return map(false, (e) -> e.unordered());
    }
}