package run.soeasy.framework.core.exchange;

import run.soeasy.framework.core.streaming.Streamable;

/**
 * 批量通道接口，定义支持批量消息处理的通道。
 * 该接口继承自{@link Channel}和{@link BatchPublisher}，
 * 结合了批量消息发布和通道的特性，支持高效的批量操作。
 *
 * <p>核心特性：
 * <ul>
 *   <li>批量处理：通过Streamable集合一次性处理多个消息</li>
 *   <li>模式转换：通过single()方法可转换为单条处理模式</li>
 *   <li>双向能力：同时支持发布和接收批量消息</li>
 * </ul>
 *
 * <p>典型应用场景：
 * <ul>
 *   <li>批处理系统中的数据交换</li>
 *   <li>高性能消息队列的批量操作</li>
 *   <li>需要事务保证的多消息处理</li>
 *   <li>数据同步和批量推送场景</li>
 * </ul>
 *
 * @param <T> 通道中传递的消息类型
 * 
 * @author soeasy.run
 * @see Channel
 * @see BatchPublisher
 * @see Streamable
 */
public interface BatchChannel<T> extends Channel<Streamable<T>>, BatchPublisher<T> {

    /**
     * 将当前批量通道转换为单条处理通道
     * 默认实现返回一个假的单条通道包装器，内部仍使用批量处理逻辑
     * 
     * @return 单条处理通道实例
     */
    @Override
    default Channel<T> single() {
        return (FakeSingleChannel<T, BatchChannel<T>>) (() -> this);
    }
}