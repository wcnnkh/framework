package run.soeasy.framework.core.exchange;

import run.soeasy.framework.core.streaming.Streamable;

/**
 * 批量发布器接口，定义发布批量消息的基本契约。
 * 该接口继承自{@link Publisher}，但发布的消息类型为{@link Streamable}，
 * 允许一次性发布多个消息，提高发布效率。
 *
 * <p>核心特性：
 * <ul>
 *   <li>批量发布：通过Streamable集合一次性发布多个消息</li>
 *   <li>模式转换：通过single()方法可转换为单条发布模式</li>
 *   <li>原子性保证：实现类可选择保证批量发布的原子性</li>
 * </ul>
 *
 * <p>典型应用场景：
 * <ul>
 *   <li>批量数据推送和广播</li>
 *   <li>批处理模式的消息队列生产</li>
 *   <li>需要事务保证的多消息发布</li>
 *   <li>性能敏感场景的消息合并发布</li>
 * </ul>
 *
 * @param <T> 发布的消息类型
 * 
 * @author soeasy.run
 * @see Publisher
 * @see Streamable
 */
public interface BatchPublisher<T> extends Publisher<Streamable<T>> {

    /**
     * 将当前批量发布器转换为单条发布器
     * 默认实现返回一个假的单条发布器包装器，内部仍使用批量发布逻辑
     * 
     * @return 单条发布器实例
     */
    default Publisher<T> single() {
        return (FakeSinglePublisher<T, BatchPublisher<T>>) (() -> this);
    }
}