package run.soeasy.framework.core.collection;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToLongFunction;
import java.util.stream.Stream;

import lombok.NonNull;

/**
 * 支持元素类型转换的提供者实现，用于将一种类型的元素提供者转换为另一种类型的元素提供者。
 * 该类继承自ConvertedElements并实现Provider接口，在元素转换的基础上增加了数据重载和动态计算的能力。
 *
 * <p>设计特点：
 * <ul>
 *   <li>通过Function函数式接口实现元素类型的转换</li>
 *   <li>支持数据源的动态重载（reload）操作</li>
 *   <li>基于Stream流处理，提供惰性计算和并行操作能力</li>
 *   <li>链式调用支持，可组合多个转换和过滤操作</li>
 * </ul>
 *
 * <p>使用示例：
 * <pre>{@code
 * Provider<String> stringProvider = ...;
 * Provider<Integer> intProvider = new ConvertedProvider<>(
 *     stringProvider, 
 *     false, 
 *     stream -> stream.map(Integer::parseInt)
 * );
 * }</pre>
 *
 * @param <S> 源元素类型
 * @param <T> 目标元素类型
 * @param <W> 源提供者类型，必须实现Provider&lt;S&gt;接口
 * @see ConvertedElements
 * @see Provider
 * @see Function
 */
public class ConvertedProvider<S, T, W extends Provider<S>> extends ConvertedElements<S, T, W> implements Provider<T> {

    /**
     * 构造函数，创建一个转换提供者实例。
     *
     * @param target    被转换的源提供者
     * @param resize    是否在转换后重新计算元素数量
     * @param converter 元素转换函数，将源元素流转换为目标元素流
     */
    public ConvertedProvider(@NonNull W target, boolean resize,
            @NonNull Function<? super Stream<S>, ? extends Stream<T>> converter) {
        super(target, resize, converter);
    }

    /**
     * 重载数据源。
     * 调用此方法会触发源提供者重新加载数据，确保后续获取的元素为最新状态。
     */
    @Override
    public void reload() {
        getTarget().reload();
    }

    /**
     * 映射转换，将当前提供者的元素流转换为另一种类型的元素流。
     *
     * @param <U>       目标元素类型
     * @param resize    是否在转换后重新计算元素数量
     * @param converter 元素转换函数
     * @return 转换后的元素提供者
     */
    @Override
    public <U> Provider<U> map(boolean resize, Function<? super Stream<T>, ? extends Stream<U>> converter) {
        return Provider.super.map(resize, converter);
    }

    /**
     * 连接两个元素集合，返回一个包含原集合和新元素的提供者。
     *
     * @param elements 要连接的元素集合
     * @return 连接后的元素提供者
     */
    @Override
    public Provider<T> concat(Elements<? extends T> elements) {
        return Provider.super.concat(elements);
    }

    /**
     * 获取元素流。
     * 该方法返回经过转换后的元素流，支持流式操作和惰性计算。
     *
     * @return 元素流
     */
    @Override
    public Stream<T> stream() {
        return getSource().stream();
    }

    /**
     * 提供已知的元素数量统计函数。
     * 该方法允许预先计算或提供元素数量，避免后续重复计算。
     *
     * @param statisticsSize 元素数量统计函数
     * @return 具有已知元素数量的提供者
     */
    @Override
    public Provider<T> knownSize(@NonNull ToLongFunction<? super Elements<T>> statisticsSize) {
        return Provider.super.knownSize(statisticsSize);
    }

    /**
     * 过滤元素，返回一个只包含满足条件元素的提供者。
     *
     * @param predicate 过滤条件
     * @return 过滤后的元素提供者
     */
    @Override
    public Provider<T> filter(@NonNull Predicate<? super T> predicate) {
        return Provider.super.filter(predicate);
    }

    /**
     * 映射转换，将每个元素转换为另一种类型。
     *
     * @param <U>    目标元素类型
     * @param mapper 元素映射函数
     * @return 转换后的元素提供者
     */
    @Override
    public <U> Provider<U> map(Function<? super T, ? extends U> mapper) {
        return Provider.super.map(mapper);
    }
}