package run.soeasy.framework.core.collection;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToLongFunction;
import java.util.stream.Stream;

import lombok.NonNull;

/**
 * 支持元素重载的可包装元素集合接口，提供数据源动态刷新和流式操作能力。
 * 该接口继承自Provider和ElementsWrapper，结合了元素提供者的动态加载特性和包装器的转换能力。
 *
 * <p>设计特点：
 * <ul>
 *   <li>通过组合Provider和ElementsWrapper接口，实现数据源的动态刷新和转换</li>
 *   <li>提供默认方法实现，基于Stream API实现元素的映射、过滤和连接等操作</li>
 *   <li>支持已知大小统计，优化大数据集的处理性能</li>
 *   <li>所有操作默认委托给被包装的源元素集合实现</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>需要定期刷新的缓存数据集合</li>
 *   <li>动态变化的配置项集合</li>
 *   <li>需要实时反映底层数据变化的视图层数据</li>
 * </ul>
 *
 * @param <S> 元素类型
 * @param <W> 被包装的元素集合类型，必须实现Elements接口
 * @see Provider
 * @see ElementsWrapper
 * @see Elements
 */
public interface ReloadableElementsWrapper<S, W extends Elements<S>> extends Provider<S>, ElementsWrapper<S, W> {

    /**
     * 映射转换，将当前元素集合的元素流转换为另一种类型的元素流。
     * 该方法默认委托给Provider接口的实现。
     *
     * @param <U>       目标元素类型
     * @param resize    是否在转换后重新计算元素数量
     * @param converter 元素转换函数
     * @return 转换后的元素提供者
     */
    @Override
    default <U> Provider<U> map(boolean resize, Function<? super Stream<S>, ? extends Stream<U>> converter) {
        return Provider.super.map(resize, converter);
    }

    /**
     * 连接两个元素集合，返回一个包含原集合和新元素的提供者。
     * 该方法默认委托给Provider接口的实现。
     *
     * @param elements 要连接的元素集合
     * @return 连接后的元素提供者
     */
    @Override
    default Provider<S> concat(Elements<? extends S> elements) {
        return Provider.super.concat(elements);
    }

    /**
     * 获取元素流。
     * 该方法默认委托给ElementsWrapper接口的实现。
     *
     * @return 元素流
     */
    @Override
    default Stream<S> stream() {
        return ElementsWrapper.super.stream();
    }

    /**
     * 提供已知的元素数量统计函数。
     * 该方法允许预先计算或提供元素数量，避免后续重复计算。
     * 该方法默认委托给Provider接口的实现。
     *
     * @param statisticsSize 元素数量统计函数
     * @return 具有已知元素数量的提供者
     */
    @Override
    default Provider<S> knownSize(@NonNull ToLongFunction<? super Elements<S>> statisticsSize) {
        return Provider.super.knownSize(statisticsSize);
    }

    /**
     * 过滤元素，返回一个只包含满足条件元素的提供者。
     * 该方法默认委托给Provider接口的实现。
     *
     * @param predicate 过滤条件
     * @return 过滤后的元素提供者
     */
    @Override
    default Provider<S> filter(@NonNull Predicate<? super S> predicate) {
        return Provider.super.filter(predicate);
    }

    /**
     * 映射转换，将每个元素转换为另一种类型。
     * 该方法默认委托给Provider接口的实现。
     *
     * @param <U>    目标元素类型
     * @param mapper 元素映射函数
     * @return 转换后的元素提供者
     */
    @Override
    default <U> Provider<U> map(Function<? super S, ? extends U> mapper) {
        return Provider.super.map(mapper);
    }
}