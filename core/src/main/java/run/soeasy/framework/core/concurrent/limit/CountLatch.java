package run.soeasy.framework.core.concurrent.limit;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * 计数闩锁，提供类似CountDownLatch的功能，但增加了计数递增能力。
 * 该类允许线程等待直到计数减少到零或增加到零，适用于需要动态控制线程同步点的场景。
 *
 * <p>核心特性：
 * <ul>
 *   <li>继承CountDownLatch功能，支持计数递减和等待</li>
 *   <li>新增countUp()方法支持计数递增，提供更灵活的同步控制</li>
 *   <li>基于AQS实现，保证线程安全和高性能同步</li>
 *   <li>支持超时等待和中断响应</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>动态调整同步点的并发控制</li>
 *   <li>需要增加计数的复杂同步场景</li>
 *   <li>替代传统CountDownLatch实现更灵活的线程协调</li>
 *   <li>资源加载完成后的多线程通知</li>
 * </ul>
 *
 * @author soeasy.run
 * @see java.util.concurrent.CountDownLatch
 */
public final class CountLatch {
    /**
     * 同步控制实现，继承AQS框架。
     * 使用AQS的state字段表示计数状态，实现共享模式的同步控制。
     */
    private static final class Sync extends AbstractQueuedSynchronizer {
        private static final long serialVersionUID = 4982264981922014374L;

        /**
         * 初始化同步器状态为指定计数。
         *
         * @param count 初始计数值，必须非负
         */
        Sync(int count) {
            setState(count);
        }

        /**
         * 获取当前计数状态。
         *
         * @return 当前计数值
         */
        int getCount() {
            return getState();
        }

        /**
         * 尝试以共享模式获取同步状态。
         * 当计数为0时返回成功，否则失败。
         *
         * @param acquires 申请的资源量（固定为1）
         * @return 成功返回1，失败返回-1
         */
        @Override
        protected int tryAcquireShared(int acquires) {
            return (getState() == 0) ? 1 : -1;
        }

        /**
         * 尝试以共享模式释放同步状态。
         * 支持正（递减）和负（递增）的释放量，当计数变为0时唤醒所有等待线程。
         *
         * @param releases 释放的资源量，正数表示递减，负数表示递增
         * @return 释放后计数是否为0
         */
        @Override
        protected boolean tryReleaseShared(int releases) {
            for (;;) {
                int current = getState();
                int next = current - releases; // 注意：releases为负时实际是增加计数
                if (compareAndSetState(current, next)) {
                    return next == 0;
                }
            }
        }
    }

    private final Sync sync;

    /**
     * 构造一个指定初始计数的CountLatch。
     *
     * @param count 初始计数值，必须非负
     * @throws IllegalArgumentException 当count为负数时抛出
     */
    public CountLatch(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("count < 0");
        }
        this.sync = new Sync(count);
    }

    /**
     * 使当前线程等待直到计数变为0，除非线程被中断。
     * <p>
     * 若当前计数为0则立即返回；否则当前线程会被阻塞，直到：
     * <ol>
     *   <li>计数通过countDown()或countUp()变为0</li>
     *   <li>其他线程中断当前线程</li>
     * </ol>
     *
     * @throws InterruptedException 当线程等待时被中断
     */
    public void await() throws InterruptedException {
        sync.acquireSharedInterruptibly(1);
    }

    /**
     * 使当前线程等待直到计数变为0，或超时，或线程被中断。
     * <p>
     * 若当前计数为0则立即返回true；否则当前线程会被阻塞，直到：
     * <ol>
     *   <li>计数变为0</li>
     *   <li>其他线程中断当前线程</li>
     *   <li>指定等待时间结束</li>
     * </ol>
     *
     * @param timeout 最大等待时间
     * @param unit    时间单位
     * @return 计数变为0返回true，超时返回false
     * @throws InterruptedException 当线程等待时被中断
     */
    public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireSharedNanos(1, unit.toNanos(timeout));
    }

    /**
     * 递减计数，若计数变为0则释放所有等待线程。
     * <p>
     * 若当前计数大于0则递减；若新计数为0则唤醒所有等待线程；
     * 若当前计数为0则无操作。
     */
    public void countDown() {
        sync.releaseShared(1);
    }

    /**
     * 递增计数，若计数变为0则释放所有等待线程。
     * <p>
     * 若当前计数大于0则递增；若新计数为0则唤醒所有等待线程；
     * 若当前计数为0则无操作。
     * <p>
     * <b>注意：</b>该方法提供了CountDownLatch没有的计数递增能力，
     * 可用于动态调整同步点。
     */
    public void countUp() {
        sync.releaseShared(-1);
    }

    /**
     * 获取当前计数值。
     * <p>
     * 主要用于调试和测试场景。
     *
     * @return 当前计数值
     */
    public long getCount() {
        return sync.getCount();
    }

    /**
     * 返回CountLatch的字符串表示，包含当前计数状态。
     *
     * @return 格式为"类名[Count = 计数值]"的字符串
     */
    @Override
    public String toString() {
        return super.toString() + "[Count = " + sync.getCount() + "]";
    }
}