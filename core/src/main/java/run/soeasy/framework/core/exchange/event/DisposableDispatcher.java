package run.soeasy.framework.core.exchange.event;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Lock;

import lombok.NonNull;
import run.soeasy.framework.core.exchange.AbstractChannel;
import run.soeasy.framework.core.exchange.ListenableChannel;
import run.soeasy.framework.core.exchange.Listener;
import run.soeasy.framework.core.exchange.Registration;
import run.soeasy.framework.core.exchange.container.ElementRegistration;
import run.soeasy.framework.core.exchange.container.collection.QueueContainer;
import run.soeasy.framework.logging.LogManager;
import run.soeasy.framework.logging.Logger;

/**
 * 一次性事件分发器，确保每个监听器仅接收一次事件后即被移除。
 * 该分发器采用FIFO队列存储监听器，在事件发布时按顺序触发监听器，
 * 并在触发后立即将其从注册表中移除，实现一次性通知机制。
 *
 * <p>核心特性：
 * <ul>
 *   <li>原子性触发：每个监听器保证仅被触发一次</li>
 *   <li>异常隔离：单个监听器的异常不会影响其他监听器</li>
 *   <li>线程安全：通过写锁保证并发环境下的操作一致性</li>
 *   <li>错误处理：提供可扩展的异常处理机制</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>一次性事件通知（如初始化完成、资源释放等）</li>
 *   <li>需要防止重复处理的事件场景</li>
 *   <li>异步操作的回调管理</li>
 * </ul>
 *
 * @param <T> 事件数据类型
 * 
 * @author soeasy.run
 * @see ListenableChannel
 * @see QueueContainer
 */
public class DisposableDispatcher<T> extends AbstractChannel<T> implements ListenableChannel<T> {
    private static final Logger logger = LogManager.getLogger(DisposableDispatcher.class);
    
    /** 监听器注册表，使用队列容器确保FIFO顺序处理 */
    private final QueueContainer<Listener<T>, Queue<ElementRegistration<Listener<T>>>> registry = 
        new QueueContainer<>(LinkedList::new);

    /**
     * 注册事件监听器，该监听器将在事件发布时仅被触发一次。
     * 监听器会被添加到队列尾部，按FIFO顺序处理。
     * 
     * @param listener 待注册的监听器，不可为null
     * @return 注册操作的回执，可用于取消注册
     */
    @Override
    public Registration registerListener(@NonNull Listener<T> listener) {
        return registry.register(listener);
    }

    /**
     * 同步发布事件并触发所有等待的监听器。
     * 该方法会获取写锁，按FIFO顺序触发监听器，并在触发后立即移除。
     * 即使某个监听器抛出异常，后续监听器仍会继续执行。
     * 
     * @param resource 要发布的事件数据，可为null
     */
    @Override
    public void syncPublish(T resource) {
        Lock lock = registry.writeLock();
        lock.lock();
        try {
            Listener<? super T> listener;
            // 按FIFO顺序处理所有监听器
            while ((listener = registry.peek()) != null) {
                try {
                    // 触发监听器处理事件
                    listener.accept(resource);
                } catch (Throwable e) {
                    // 异常处理，默认记录错误日志
                    handleError(listener, resource, e);
                } finally {
                    // 无论成功或失败，确保监听器只被触发一次
                    registry.remove();
                }
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * 处理监听器执行过程中抛出的异常。
     * 子类可重写此方法以实现自定义的异常处理逻辑。
     * 
     * @param listener 抛出异常的监听器
     * @param resource 事件数据
     * @param e 抛出的异常
     */
    protected void handleError(Listener<? super T> listener, T resource, Throwable e) {
        logger.error(e, "Error occurred while processing listener {} with resource {}", listener, resource);
    }

    /**
     * 清除所有注册的监听器。
     * 此操作会释放所有未触发的监听器资源，后续发布的事件将不再触发任何监听器。
     */
    public void clear() {
        registry.clear();
    }
}