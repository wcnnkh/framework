package run.soeasy.framework.core.collection;

import java.util.function.ToLongFunction;

import lombok.NonNull;

/**
 * 已知大小的元素提供者包装器，用于为可重载的元素提供者提供预先计算的大小统计功能。
 * 该类继承自KnownSizeElements，在已知大小统计的基础上增加了元素重载能力，
 * 适用于需要动态刷新数据且频繁查询元素数量的场景。
 *
 * <p>核心特性：
 * <ul>
 *   <li>继承KnownSizeElements的预计算大小统计功能，避免重复计算元素数量</li>
 *   <li>实现ReloadableElementsWrapper接口，支持数据源的动态刷新</li>
 *   <li>所有大小相关操作（count/isEmpty/isUnique）均基于预定义的统计函数</li>
 *   <li>重载操作直接委托给被包装的源提供者，保持行为一致性</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>元素数量计算代价较高且数据需要动态刷新的场景</li>
 *   <li>需要频繁获取元素数量但数据变更不频繁的场景</li>
 *   <li>需要统一元素数量统计逻辑和数据刷新机制的场景</li>
 * </ul>
 *
 * @param <S> 元素类型
 * @param <W> 被包装的提供者类型，必须实现Provider接口
 * @see KnownSizeElements
 * @see ReloadableElementsWrapper
 * @see ToLongFunction
 */
public class KnownSizeProvider<S, W extends Provider<S>> extends KnownSizeElements<S, W>
        implements ReloadableElementsWrapper<S, W> {

    /**
     * 创建已知大小的元素提供者实例。
     * 
     * @param source         被包装的源提供者，不可为null
     * @param statisticsSize 元素数量统计函数，不可为null
     */
    public KnownSizeProvider(@NonNull W source, @NonNull ToLongFunction<? super W> statisticsSize) {
        super(source, statisticsSize);
    }

    /**
     * 重新加载数据。
     * 该方法直接委托给被包装的源提供者执行重载操作，
     * 确保数据刷新逻辑与原始提供者一致。
     */
    @Override
    public void reload() {
        getSource().reload();
    }
}