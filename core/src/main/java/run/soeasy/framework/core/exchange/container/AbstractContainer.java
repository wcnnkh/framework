package run.soeasy.framework.core.exchange.container;

import java.util.concurrent.locks.Lock;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.concurrent.LockableContainer;
import run.soeasy.framework.core.exchange.Publisher;
import run.soeasy.framework.core.exchange.event.ChangeEvent;
import run.soeasy.framework.core.function.ThrowingSupplier;

/**
 * 抽象容器基类，提供容器的基本实现和事件发布功能。
 * <p>
 * 该类继承自{@link LockableContainer}，实现了线程安全的容器操作，并支持元素变更事件的发布。
 * 采用延迟初始化策略，首次访问时通过{@link ThrowingSupplier}创建容器实例。
 *
 * <p><b>线程安全说明：</b>
 * 借助{@link LockableContainer}的读写锁机制，确保多线程环境下的安全访问。
 * 但需注意{@link Publisher}的实现必须自身是线程安全的。
 *
 * @param <C> 容器类型
 * @param <E> 容器元素类型
 * @param <P> 注册类型，需继承自{@link PayloadRegistration}&lt;{@link E}&gt;
 * 
 * @author soeasy.run
 * @see Container
 * @see LockableContainer
 */
public abstract class AbstractContainer<C, E, P extends PayloadRegistration<E>>
        extends LockableContainer<C, RuntimeException> implements Container<E, P> {
    
    /** 元素变更事件的发布者，默认忽略所有事件 */
    private volatile Publisher<? super Elements<ChangeEvent<E>>> publisher = Publisher.ignore();

    /**
     * 构造函数，初始化抽象容器
     * <p>
     * 采用延迟初始化，首次写入时通过{@link ThrowingSupplier}创建容器实例。
     * 
     * @param containerSource 容器初始化供应商，不可为null
     * @throws NullPointerException 若containerSource为null
     * @see LockableContainer#LockableContainer(ThrowingSupplier)
     */
    public AbstractContainer(@NonNull ThrowingSupplier<? extends C, ? extends RuntimeException> containerSource) {
        super(containerSource);
    }

    /**
     * 获取元素变更事件的发布者
     * <p>
     * 该方法使用读锁保护，确保线程安全地获取发布者实例。
     * 
     * @return 事件发布者实例，默认返回{@link Publisher#ignore()}
     */
    public Publisher<? super Elements<ChangeEvent<E>>> getPublisher() {
        Lock lock = readLock();
        lock.lock();
        try {
            return publisher;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 设置元素变更事件的发布者
     * <p>
     * 该方法使用写锁保护，确保线程安全地更新发布者实例。
     * 
     * @param publisher 事件发布者，不可为null
     * @throws NullPointerException 若publisher为null
     */
    public void setPublisher(@NonNull Publisher<? super Elements<ChangeEvent<E>>> publisher) {
        Lock lock = writeLock();
        lock.lock();
        try {
            this.publisher = publisher;
        } finally {
            lock.unlock();
        }
    }
}