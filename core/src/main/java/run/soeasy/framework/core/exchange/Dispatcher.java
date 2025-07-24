package run.soeasy.framework.core.exchange;

/**
 * 事件分发器接口，定义消息发布和监听器管理的双重能力。
 * 该接口继承自{@link Publisher}和{@link Listenable}，
 * 结合了消息发布和监听器注册的功能，形成完整的事件分发模式。
 *
 * <p>核心特性：
 * <ul>
 *   <li>双向通信：既可以发布消息，也可以注册监听器接收消息</li>
 *   <li>批量操作：通过batch()方法支持批量消息处理和监听器管理</li>
 *   <li>事件路由：负责将消息正确分发给注册的监听器</li>
 * </ul>
 *
 * <p>典型应用场景：
 * <ul>
 *   <li>事件驱动架构中的事件总线</li>
 *   <li>消息队列的生产者-消费者模型</li>
 *   <li>组件间的解耦通信</li>
 *   <li>状态变更的通知机制</li>
 * </ul>
 *
 * @param <T> 分发的消息类型
 * 
 * @author soeasy.run
 * @see Publisher
 * @see Listenable
 * @see BatchDispatcher
 */
public interface Dispatcher<T> extends Publisher<T>, Listenable<T> {

    /**
     * 将当前分发器转换为批量分发器
     * 默认实现返回一个假的批量分发器包装器，内部仍使用单条处理逻辑
     * 
     * @return 批量分发器实例
     */
    @Override
    default BatchDispatcher<T> batch() {
        return (FakeBatchDispatcher<T, Dispatcher<T>>) () -> this;
    }
}