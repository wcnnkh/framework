package run.soeasy.framework.core.exchange;

import java.util.concurrent.TimeUnit;

/**
 * 消息通道接口，定义单向消息发布的基本契约。
 * 该接口继承自{@link Publisher}，提供带有超时控制的消息发布能力，
 * 是框架中消息传递的核心抽象之一。
 *
 * <p>核心特性：
 * <ul>
 *   <li>超时控制：支持带超时的发布操作，避免无限等待</li>
 *   <li>批量处理：通过{@link BatchChannel}支持批量消息发布</li>
 *   <li>异步回执：返回{@link Receipt}跟踪发布操作状态</li>
 * </ul>
 *
 * <p>典型应用场景：
 * <ul>
 *   <li>事件驱动系统中的事件发布</li>
 *   <li>微服务间的异步消息传递</li>
 *   <li>实时数据流处理管道</li>
 *   <li>系统间的解耦通信</li>
 * </ul>
 *
 * @param <T> 通道中传递的消息类型
 * 
 * @author soeasy.run
 * @see Publisher
 * @see BatchChannel
 * @see Receipt
 */
public interface Channel<T> extends Publisher<T> {

    /**
     * 常量，表示无超时限制的消息发布操作。
     * 当使用该值作为超时参数时，发布操作将阻塞直到完成或被中断。
     */
    long INDEFINITE_TIMEOUT = -1;

    /**
     * 将当前通道转换为批量通道
     * 默认实现返回一个假的批量通道包装器，内部仍使用单条发布逻辑
     * 
     * @return 批量通道实例
     */
    @Override
    default BatchChannel<T> batch() {
        return (FakeBatchChannel<T, Channel<T>>) (() -> this);
    }

    /**
     * 发布消息到通道，使用默认的无超时策略
     * 等同于调用 publish(resource, INDEFINITE_TIMEOUT, TimeUnit.MILLISECONDS)
     * 
     * @param resource 待发布的消息资源
     * @return 发布操作的回执，用于跟踪操作状态
     */
    @Override
    default Operation publish(T resource) {
        return publish(resource, INDEFINITE_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    /**
     * 发布消息到通道，并指定超时时间
     * 
     * @param resource 待发布的消息资源
     * @param timeout 超时时间，负值表示无限等待
     * @param timeUnit 超时时间单位
     * @return 发布操作的回执，用于跟踪操作状态
     */
    Operation publish(T resource, long timeout, TimeUnit timeUnit);
}