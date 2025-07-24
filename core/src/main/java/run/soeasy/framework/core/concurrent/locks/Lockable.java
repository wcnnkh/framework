package run.soeasy.framework.core.concurrent.locks;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 可锁定接口，扩展自JDK的Lock接口，提供默认实现。
 * 该接口定义了锁操作的标准行为，并为部分方法提供了默认实现，
 * 使得实现类只需关注核心的锁获取和释放逻辑。
 *
 * <p>默认实现特点：
 * <ul>
 *   <li>lock()方法：通过循环调用lockInterruptibly()实现，忽略中断直到成功获取锁</li>
 *   <li>lockInterruptibly()方法：通过无限等待的tryLock()实现，可响应中断</li>
 *   <li>newCondition()方法：默认抛出UnsupportedOperationException，需实现类按需重写</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>简化自定义锁实现，只需关注核心逻辑</li>
 *   <li>需要灵活组合锁行为的场景</li>
 *   <li>实现轻量级锁机制</li>
 * </ul>
 *
 * @see Lock
 * @see Condition
 */
public interface Lockable extends Lock {

    /**
     * 获取锁，忽略中断直到成功。
     * 该默认实现通过循环调用lockInterruptibly()并忽略InterruptedException实现，
     * 确保线程在获取锁的过程中不会被中断，直到成功获取锁。
     *
     * <p>注意：
     * <ul>
     *   <li>实现类若有更高效的非中断锁获取方式，应重写此方法</li>
     *   <li>调用此方法后，当前线程会一直阻塞直到获取到锁</li>
     * </ul>
     */
    @Override
    default void lock() {
        while (true) {
            try {
                lockInterruptibly();
                break;
            } catch (InterruptedException e) {
                // 忽略中断，继续尝试获取锁
                Thread.currentThread().interrupt(); // 恢复中断状态
            }
        }
    }

    /**
     * 获取锁，可响应中断。
     * 该默认实现通过调用tryLock()并设置最大等待时间为Long.MAX_VALUE毫秒实现，
     * 确保线程在等待锁的过程中可被中断。
     *
     * <p>注意：
     * <ul>
     *   <li>实现类若有更高效的可中断锁获取方式，应重写此方法</li>
     *   <li>调用此方法后，若线程被中断，会抛出InterruptedException</li>
     * </ul>
     *
     * @throws InterruptedException 如果当前线程在等待锁的过程中被中断
     */
    @Override
    default void lockInterruptibly() throws InterruptedException {
        while (!tryLock(Long.MAX_VALUE, TimeUnit.MILLISECONDS)) {
            // 循环直到获取到锁
            if (Thread.interrupted()) {
                throw new InterruptedException("Lock acquisition interrupted");
            }
        }
    }

    /**
     * 返回一个绑定到此Lock实例的Condition实例。
     * 该默认实现抛出UnsupportedOperationException，因为并非所有锁都需要支持条件变量。
     * 实现类若需要支持条件等待/通知机制，应重写此方法。
     *
     * @return 绑定到此Lock的Condition实例
     * @throws UnsupportedOperationException 默认抛出此异常，除非实现类重写此方法
     */
    @Override
    default Condition newCondition() {
        throw new UnsupportedOperationException("Condition not supported by default implementation");
    }
}