package run.soeasy.framework.core.concurrent.locks;

import java.util.concurrent.TimeUnit;

/**
 * 一次性锁接口，扩展自Lockable接口，提供只能获取一次的锁语义。
 * 该锁的特性是：一旦尝试获取失败，后续永远无法获取，适用于需要一次性资源分配或单例初始化的场景。
 *
 * <p>核心特性：
 * <ul>
 *   <li>锁获取具有"一次性"特性：首次尝试失败后，后续尝试永远失败</li>
 *   <li>不支持长时间等待：tryLock(long, TimeUnit)等价于tryLock()</li>
 *   <li>中断锁获取会抛出DisposableLockException</li>
 *   <li>支持锁释放操作，但具体行为由实现类定义</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>资源的一次性分配（如单例初始化）</li>
 *   <li>实现不可重入的锁机制</li>
 *   <li>需要快速失败而非等待的场景</li>
 *   <li>初始化阶段的排他性操作</li>
 * </ul>
 *
 * @author soeasy.run
 * @see Lockable
 * @see DisposableLockException
 */
public interface DisposableLock extends Lockable {

    /**
     * 尝试获取锁，如果锁不可用则立即失败。
     * 该方法具有"一次性"语义：如果首次调用返回false，
     * 则后续所有调用都将返回false，即使锁已被释放。
     *
     * @return 获取锁成功返回true，失败返回false
     */
    @Override
    boolean tryLock();

    /**
     * 获取锁，可响应中断。
     * 如果锁不可用，则立即抛出DisposableLockException，
     * 因为一次性锁一旦失败就永远无法获取。
     *
     * @throws InterruptedException 如果当前线程被中断
     * @throws DisposableLockException 如果锁不可用
     */
    @Override
    default void lockInterruptibly() throws InterruptedException, DisposableLockException {
        if (!tryLock()) {
            // 一次性锁获取失败，永远无法再获取
            throw new DisposableLockException("Lock acquisition failed permanently");
        }
    }

    /**
     * 尝试在指定时间内获取锁。
     * 对于一次性锁，此方法等价于tryLock()，不会等待指定时间，
     * 因为锁一旦失败就永远无法获取。
     *
     * @param time 等待时间
     * @param unit 时间单位
     * @return 获取锁成功返回true，失败返回false
     * @throws InterruptedException 如果当前线程被中断
     */
    @Override
    default boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return tryLock(); // 不等待，直接尝试
    }

    /**
     * 释放锁。
     * 具体行为由实现类定义，可能允许或禁止重复释放。
     * 建议实现类在释放后保持锁的不可用状态（符合一次性语义）。
     */
    @Override
    void unlock();
}