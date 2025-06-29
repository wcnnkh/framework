package run.soeasy.framework.core.collection;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToLongFunction;
import java.util.stream.Stream;

import lombok.NonNull;

/**
 * 提供者接口
 * 继承自Elements和Reloadable，定义了元素的提供和重载能力
 * 
 * @author soeasy.run
 * @param <S> 提供的元素类型
 */
public interface Provider<S> extends Elements<S>, Reloadable {

    /**
     * 创建一个空的提供者实例
     * 
     * @param <E> 元素类型
     * @return 空的提供者实例
     */
    @SuppressWarnings("unchecked")
    public static <E> Provider<E> empty() {
        return (Provider<E>) EmptyProvider.EMPTY_PROVIDER;
    }

    /**
     * 根据可迭代对象创建提供者
     * 
     * @param <T> 元素类型
     * @param iterable 可迭代对象，不可为null
     * @return 基于可迭代对象的提供者
     */
    @SuppressWarnings("unchecked")
    public static <T> Provider<T> forIterable(@NonNull Iterable<? extends T> iterable) {
        if (iterable instanceof Provider) {
            return (Provider<T>) iterable;
        }

        return new IterableProvider<>(iterable);
    }

    /**
     * 根据提供者创建提供者
     * 
     * @param <T> 元素类型
     * @param supplier 元素提供者，不可为null
     * @return 基于提供者的提供者
     */
    public static <T> Provider<T> forSupplier(@NonNull Supplier<? extends T> supplier) {
        return forIterable(Elements.forSupplier(supplier));
    }

    /**
     * 连接当前提供者与另一个元素集合
     * 
     * @param elements 要连接的元素集合
     * @return 连接后的提供者
     */
    @Override
    default Provider<S> concat(Elements<? extends S> elements) {
        Provider<S> serviceLoader = Provider.forIterable(elements.map((e) -> e));
        return Provider.this.concat(serviceLoader);
    }

    /**
     * 连接当前提供者与另一个提供者
     * 
     * @param serviceLoader 要连接的提供者
     * @return 连接后的提供者
     */
    default Provider<S> concat(Provider<? extends S> serviceLoader) {
        return new MergedProvider<>(Elements.forArray(this, serviceLoader));
    }

    /**
     * 使用转换器映射当前提供者的元素流
     * 
     * @param <U> 转换后的元素类型
     * @param resize 是否调整大小
     * @param converter 元素流转换器
     * @return 转换后的提供者
     */
    @Override
    default <U> Provider<U> map(boolean resize, Function<? super Stream<S>, ? extends Stream<U>> converter) {
        return new ConvertedProvider<>(this, resize, converter);
    }

    /**
     * 过滤当前提供者的元素
     * 
     * @param predicate 过滤条件，不可为null
     * @return 过滤后的提供者
     */
    @Override
    default Provider<S> filter(@NonNull Predicate<? super S> predicate) {
        return map(true, (e) -> e.filter(predicate));
    }

    /**
     * 映射当前提供者的元素
     * 
     * @param <U> 转换后的元素类型
     * @param mapper 元素映射函数
     * @return 映射后的提供者
     */
    @Override
    default <U> Provider<U> map(Function<? super S, ? extends U> mapper) {
        return map(false, (e) -> e.map(mapper));
    }

    /**
     * 为提供者设置已知大小
     * 
     * @param statisticsSize 计算大小的函数，不可为null
     * @return 设置大小后的提供者
     */
    @Override
    default Provider<S> knownSize(@NonNull ToLongFunction<? super Elements<S>> statisticsSize) {
        return new KnownSizeProvider<>(this, statisticsSize);
    }

    /**
     * 将提供者的元素转换为流
     * 
     * @return 元素流
     */
    @Override
    default Stream<S> stream() {
        return CollectionUtils.unknownSizeStream(iterator());
    }
}