package run.soeasy.framework.core.exchange;

import java.util.EventListener;
import java.util.function.Consumer;

/**
 * 事件监听器接口，定义事件处理的基本契约。
 * 该接口继承自{@link Consumer}和{@link EventListener}，
 * 允许作为标准Java事件监听器使用，同时支持函数式编程风格。
 *
 * <p>核心特性：
 * <ul>
 *   <li>函数式接口：可作为lambda表达式使用</li>
 *   <li>事件处理：通过accept方法处理事件</li>
 *   <li>批量处理支持：通过batch()方法转换为批量监听器</li>
 * </ul>
 *
 * <p>典型应用场景：
 * <ul>
 *   <li>事件驱动系统中的事件处理</li>
 *   <li>消息队列的消息消费</li>
 *   <li>状态变更的回调处理</li>
 *   <li>组件间的解耦通信</li>
 * </ul>
 *
 * @param <T> 监听器处理的事件类型
 * 
 * @author soeasy.run
 * @see Consumer
 * @see EventListener
 * @see BatchListener
 */
public interface Listener<T> extends Consumer<T>, EventListener {

    /**
     * 处理接收到的事件
     * 
     * @param source 事件源对象
     */
    @Override
    void accept(T source);

    /**
     * 将当前监听器转换为批量监听器
     * 默认实现返回一个假的批量监听器包装器，内部仍使用单条处理逻辑
     * 
     * @return 批量监听器实例
     */
    default BatchListener<T> batch() {
        return (FakeBatchListener<T, Listener<T>>) (() -> this);
    }
}