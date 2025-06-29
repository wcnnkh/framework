package run.soeasy.framework.core.exchange;

/**
 * 发布者接口
 * 定义数据发布的标准行为，支持单条发布和批量发布模式
 * 
 * @author shuchaowen
 *
 * @param <T> 发布资源的类型
 */
public interface Publisher<T> {

    /**
     * 创建一个忽略所有发布请求的发布者
     * 所有发布操作将不会实际执行，仅返回成功收据
     * 
     * @param <E> 资源类型
     * @return 忽略发布者实例
     */
    @SuppressWarnings("unchecked")
    public static <E> Publisher<E> ignore() {
        return (Publisher<E>) IgnorePublisher.INSTANCE;
    }

    /**
     * 将当前发布者转换为批量发布者
     * 默认实现返回假批量发布者（实际仍为单条发布）
     * 
     * @return 批量发布者实例
     */
    default BatchPublisher<T> batch() {
        return (FakeBatchPublisher<T, Publisher<T>>) (() -> this);
    }

    /**
     * 发布单个资源
     * 
     * @param resource 待发布的资源对象
     * @return 发布操作的收据，包含操作结果信息
     */
    Receipt publish(T resource);
}