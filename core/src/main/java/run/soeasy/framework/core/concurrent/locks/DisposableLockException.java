package run.soeasy.framework.core.concurrent.locks;

/**
 * 一次性锁异常，当尝试获取一次性锁失败时抛出。
 * 该异常继承自InterruptedException，保留中断异常的特性，
 * 同时用于标识锁获取失败是由于一次性锁的特殊语义导致的。
 *
 * <p>核心特性：
 * <ul>
 *   <li>继承InterruptedException，支持线程中断机制</li>
 *   <li>用于标识锁获取失败是永久性的（一次性锁特性）</li>
 *   <li>提供带消息的构造函数，便于问题定位</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>在DisposableLock.lockInterruptibly()方法中使用</li>
 *   <li>当一次性锁获取失败时需要特殊处理的场景</li>
 *   <li>区分普通中断和一次性锁获取失败的场景</li>
 * </ul>
 *
 * @author soeasy.run
 * @see DisposableLock
 */
public class DisposableLockException extends InterruptedException {
    private static final long serialVersionUID = 1L;

    /**
     * 使用指定的错误消息构造DisposableLockException。
     *
     * @param message 详细错误消息，用于描述锁获取失败的原因
     */
    public DisposableLockException(String message) {
        super(message);
    }

    /**
     * 重写fillInStackTrace方法，提升性能。
     * 由于异常通常用于控制流而非错误处理，不生成堆栈轨迹以提高性能。
     *
     * @return 当前Throwable实例
     */
    @Override
    public synchronized Throwable fillInStackTrace() {
        // 性能优化：不生成堆栈轨迹
        return this;
    }
}