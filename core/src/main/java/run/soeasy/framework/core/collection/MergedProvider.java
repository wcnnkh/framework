package run.soeasy.framework.core.collection;

import java.util.function.Function;

/**
 * 提供者合并包装器，用于将多个Provider实例合并为统一的元素提供接口。
 * 该类实现了ReloadableElementsWrapper接口，支持批量刷新数据源并将多个Provider的元素合并为单一视图，
 * 适用于需要聚合多个数据源或分块数据提供者的场景。
 *
 * <p>核心特性：
 * <ul>
 *   <li>合并多个Provider实例，通过flatMap实现元素级联合并</li>
 *   <li>支持批量刷新：调用reload()会触发所有Provider的reload操作</li>
 *   <li>不可变设计：concat操作返回新的MergedProvider实例</li>
 *   <li>延迟计算：仅在调用getSource()时执行实际的元素合并</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>合并多个服务提供者（如多数据源服务发现）</li>
 *   <li>聚合不同模块的配置提供者</li>
 *   <li>处理分块加载的大数据集提供者</li>
 * </ul>
 *
 * @param <S> 元素类型
 * @param <T> 被合并的Provider类型，必须实现Provider接口
 * @see Provider
 * @see ReloadableElementsWrapper
 */
public class MergedProvider<S, T extends Provider<? extends S>> implements ReloadableElementsWrapper<S, Elements<S>> {
    
    /** 存储待合并的Provider实例集合 */
    private final Elements<Provider<? extends S>> elements;

    /**
     * 创建提供者合并包装器实例。
     * 
     * @param elements 待合并的Provider实例集合，不可为null或包含null元素
     */
    public MergedProvider(Elements<Provider<? extends S>> elements) {
        this.elements = elements;
    }

    /**
     * 批量刷新所有Provider实例。
     * 该方法会遍历所有Provider并调用其reload()方法，确保所有数据源更新到最新状态。
     */
    @Override
    public void reload() {
        elements.forEach(Provider::reload);
    }

    /**
     * 获取合并后的元素集合。
     * 该方法通过flatMap操作将所有Provider的元素合并为单一Elements实例：
     * 1. 对每个Provider调用map(Function.identity())保持元素不变
     * 2. 使用flatMap将多个Elements实例级联为一个
     * 
     * @return 合并后的Elements实例
     */
    @Override
    public Elements<S> getSource() {
        return elements.flatMap((e) -> e.map(Function.identity()));
    }

    /**
     * 连接新的Provider实例，返回新的合并包装器。
     * 该方法创建新的MergedProvider实例，包含原有的Provider和新添加的Provider。
     * 
     * @param serviceLoader 要连接的Provider实例，不可为null
     * @return 新的MergedProvider实例
     */
    @Override
    public Provider<S> concat(Provider<? extends S> serviceLoader) {
        return new MergedProvider<>(this.elements.concat(Elements.singleton(serviceLoader)));
    }
}