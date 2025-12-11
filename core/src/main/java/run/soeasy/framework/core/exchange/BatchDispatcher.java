package run.soeasy.framework.core.exchange;

import run.soeasy.framework.core.streaming.Streamable;

/**
 * 批量分发器接口，定义支持批量消息处理和监听器管理的能力。
 * 该接口继承自{@link Dispatcher}、{@link BatchPublisher}和{@link BatchListenable}，
 * 结合了批量消息发布、接收和监听器管理的功能，形成完整的批量事件分发模式。
 *
 * <p>核心特性：
 * <ul>
 *   <li>批量操作：通过Streamable集合一次性处理多个消息</li>
 *   <li>模式转换：通过single()方法可转换为单条处理模式</li>
 *   <li>双向通信：支持批量消息的发布和监听</li>
 *   <li>原子性保证：实现类可选择保证批量操作的原子性</li>
 * </ul>
 *
 * <p>典型应用场景：
 * <ul>
 *   <li>高性能消息队列的批量处理</li>
 *   <li>大数据量的事件分发系统</li>
 *   <li>需要事务保证的多消息处理</li>
 *   <li>批处理模式的组件间通信</li>
 * </ul>
 *
 * @param <T> 分发的消息类型
 * 
 * @author soeasy.run
 * @see Dispatcher
 * @see BatchPublisher
 * @see BatchListenable
 * @see Streamable
 */
public interface BatchDispatcher<T> extends Dispatcher<Streamable<T>>, BatchPublisher<T>, BatchListenable<T> {

    /**
     * 将当前批量分发器转换为单条处理分发器
     * 默认实现返回一个假的单条分发器包装器，内部仍使用批量处理逻辑
     * 
     * @return 单条处理分发器实例
     */
    @Override
    default Dispatcher<T> single() {
        return (FakeSingleDispatcher<T, BatchDispatcher<T>>) () -> this;
    }
}