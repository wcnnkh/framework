package run.soeasy.framework.core.exchange;

import run.soeasy.framework.core.streaming.Streamable;

/**
 * 批量可监听通道接口，定义支持批量消息处理和监听器管理的高级通道。
 * 该接口继承自{@link ListenableChannel}、{@link BatchChannel}和{@link BatchDispatcher}，
 * 整合了批量消息发布、接收、监听和分发的全部功能，形成完整的批量事件处理体系。
 *
 * <p>核心特性：
 * <ul>
 *   <li>批量操作：通过Streamable集合一次性处理多个消息</li>
 *   <li>模式转换：通过single()方法可转换为单条处理模式</li>
 *   <li>全方位通信：支持批量消息的发布、监听、接收和分发</li>
 *   <li>原子性保证：实现类可选择保证批量操作的原子性</li>
 * </ul>
 *
 * <p>典型应用场景：
 * <ul>
 *   <li>高性能消息队列的批量处理</li>
 *   <li>大数据量的事件驱动系统</li>
 *   <li>需要事务保证的多消息处理</li>
 *   <li>批处理模式的微服务通信</li>
 * </ul>
 *
 * @param <T> 通道中传递的消息类型
 * 
 * @author soeasy.run
 * @see ListenableChannel
 * @see BatchChannel
 * @see BatchDispatcher
 * @see Streamable
 */
public interface BatchListenableChannel<T>
        extends ListenableChannel<Streamable<T>>, BatchChannel<T>, BatchDispatcher<T> {

    /**
     * 将当前批量可监听通道转换为单条处理模式
     * 默认实现返回一个假的单条可监听通道包装器，内部仍使用批量处理逻辑
     * 
     * @return 单条处理模式的可监听通道实例
     */
    @Override
    default ListenableChannel<T> single() {
        return (FakeSingleListenableChannel<T, BatchListenableChannel<T>>) (() -> this);
    }
}