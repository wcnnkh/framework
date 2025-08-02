package run.soeasy.framework.core.exchange;

/**
 * 可监听通道接口，定义支持注册监听器的消息通道。
 * 该接口继承自{@link Channel}和{@link Dispatcher}，
 * 结合了消息发布和监听器管理的功能，形成完整的事件总线模式。
 *
 * <p>核心特性：
 * <ul>
 *   <li>双向通信：既可以发布消息，也可以注册监听器接收消息</li>
 *   <li>批量操作：通过batch()方法支持批量消息处理和监听器管理</li>
 *   <li>事件分发：继承Dispatcher的事件分发能力</li>
 * </ul>
 *
 * <p>典型应用场景：
 * <ul>
 *   <li>事件驱动架构中的事件总线</li>
 *   <li>微服务间的消息通信</li>
 *   <li>组件间的解耦通信</li>
 *   <li>实时数据流的发布与订阅</li>
 * </ul>
 *
 * @param <T> 通道中传递的消息类型
 * 
 * @author soeasy.run
 * @see Channel
 * @see Dispatcher
 * @see BatchListenableChannel
 */
public interface ListenableChannel<T> extends Channel<T>, Dispatcher<T> {

    /**
     * 将当前可监听通道转换为批量可监听通道
     * 默认实现返回一个假的批量可监听通道包装器，内部仍使用单条处理逻辑
     * 
     * @return 批量可监听通道实例
     */
    @Override
    default BatchListenableChannel<T> batch() {
        return (FakeBatchListenableChannel<T, ListenableChannel<T>>) (() -> this);
    }
}