package run.soeasy.framework.core.concurrent.limit;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;

import run.soeasy.framework.core.concurrent.locks.DisposableLock;
import run.soeasy.framework.core.concurrent.locks.DisposableLockException;
import run.soeasy.framework.core.concurrent.locks.NoOpLock;

/**
 * 递减计数限制器，通过递减的次数来限制资源访问。 该限制器维护一个原子计数器，每次获取资源时递减计数， 当计数小于等于0时拒绝所有新的资源请求。
 *
 * <p>
 * 核心特性：
 * <ul>
 * <li>原子操作：使用AtomicLong保证计数操作的线程安全性</li>
 * <li>一次性获取：每个资源锁只能获取一次，获取失败后永久失败</li>
 * <li>可主动限制：通过limited()方法可立即将计数置为0，拒绝后续请求</li>
 * <li>轻量级实现：无需显式释放资源，unlock()方法为空操作</li>
 * </ul>
 *
 * <p>
 * 使用场景：
 * <ul>
 * <li>限制操作执行次数（如API调用配额）</li>
 * <li>资源访问次数限制（如文件下载次数）</li>
 * <li>实现熔断机制（当错误次数达到阈值时拒绝请求）</li>
 * <li>临时资源分配（如启动阶段的初始化操作）</li>
 * </ul>
 *
 * @author soeasy.run
 * @see Limiter
 * @see DisposableLock
 */
public class CountDownLimiter implements Limiter {
	/**
	 * 原子计数器，记录剩余可获取资源的次数
	 */
	private final AtomicLong count;

	/**
	 * 创建具有指定初始计数的递减限制器。
	 *
	 * @param count 初始计数值，必须非负
	 * @throws IllegalArgumentException 如果count为负数
	 */
	public CountDownLimiter(long count) {
		if (count < 0) {
			throw new IllegalArgumentException("count must be non-negative");
		}
		this.count = new AtomicLong(count);
	}

	/**
	 * 检查当前是否处于受限状态。 当计数小于等于0时，认为处于受限状态。
	 *
	 * @return 若计数小于等于0返回true，否则返回false
	 */
	@Override
	public boolean isLimited() {
		return count.get() <= 0;
	}

	/**
	 * 立即将限制器置为受限状态。 将计数设置为0，并返回之前的计数值是否大于0。
	 *
	 * @return 若之前的计数值大于0返回true，否则返回false
	 */
	@Override
	public boolean limited() {
		return count.getAndSet(0) > 0;
	}

	/**
	 * 获取资源锁。 若已受限，返回NoOpLock.DEAD（永久拒绝）； 否则返回可尝试获取资源的CountResource锁。
	 *
	 * @return 资源锁实例
	 */
	@Override
	public Lock getResource() {
		if (isLimited()) {
			return NoOpLock.DEAD;
		}
		return new CountResource();
	}

	/**
	 * 资源锁实现，基于DisposableLock接口。 尝试获取锁时递减计数，获取成功后永久持有锁。
	 */
	private class CountResource implements DisposableLock {
		/**
		 * 尝试获取锁。 原子性地递减计数，并检查是否大于等于0： - 若大于等于0，表示获取成功 - 若小于0，表示获取失败，且后续永远无法获取
		 *
		 * @return 获取成功返回true，失败返回false
		 */
		@Override
		public boolean tryLock() {
			return count.decrementAndGet() >= 0;
		}

		/**
		 * 释放锁。 由于是一次性锁且计数不可恢复，此方法为空实现。
		 */
		@Override
		public void unlock() {
			// 空实现，无需释放
		}

		/**
		 * 获取锁，可响应中断。 实现了DisposableLock接口的默认行为，获取失败时抛出异常。
		 *
		 * @throws InterruptedException    如果当前线程被中断
		 * @throws DisposableLockException 如果锁不可用
		 */
		@Override
		public void lockInterruptibly() throws InterruptedException, DisposableLockException {
			DisposableLock.super.lockInterruptibly();
		}

		/**
		 * 尝试在指定时间内获取锁。 实现了DisposableLock接口的默认行为，立即返回结果。
		 *
		 * @param time 等待时间
		 * @param unit 时间单位
		 * @return 获取成功返回true，失败返回false
		 */
		@Override
		public boolean tryLock(long time, java.util.concurrent.TimeUnit unit) {
			return tryLock();
		}

		/**
		 * 不支持newCondition()方法，始终抛出异常。
		 *
		 * @throws UnsupportedOperationException 调用此方法
		 */
		@Override
		public java.util.concurrent.locks.Condition newCondition() {
			throw new UnsupportedOperationException("Conditions not supported");
		}
	}
}