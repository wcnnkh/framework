package run.soeasy.framework.core.exchange;

import javax.lang.model.util.Elements;

import run.soeasy.framework.core.streaming.Streamable;

/**
 * 批量监听器接口，定义处理批量事件的基本契约。
 * 该接口继承自{@link Listener}，但处理的事件类型为{@link Elements}，
 * 允许一次性处理多个事件，提高处理效率。
 *
 * <p>核心特性：
 * <ul>
 *   <li>批量处理：通过Elements集合一次性处理多个事件</li>
 *   <li>模式转换：通过single()方法可转换为单条处理模式</li>
 *   <li>原子性保证：实现类可选择保证批量处理的原子性</li>
 * </ul>
 *
 * <p>典型应用场景：
 * <ul>
 *   <li>批量数据处理和持久化</li>
 *   <li>批处理模式的消息队列消费</li>
 *   <li>需要事务保证的多事件处理</li>
 *   <li>性能敏感场景的事件合并处理</li>
 * </ul>
 *
 * @param <T> 监听器处理的事件类型
 * 
 * @author soeasy.run
 * @see Listener
 * @see Elements
 */
public interface BatchListener<T> extends Listener<Streamable<T>> {

    /**
     * 将当前批量监听器转换为单条处理监听器
     * 默认实现返回一个假的单条监听器包装器，内部仍使用批量处理逻辑
     * 
     * @return 单条处理监听器实例
     */
    default Listener<T> single() {
        return (FakeSingleListener<T, BatchListener<T>>) (() -> this);
    }
}