package run.soeasy.framework.core.concurrent.limit;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;

import run.soeasy.framework.core.concurrent.locks.DisposableLock;
import run.soeasy.framework.core.concurrent.locks.DisposableLockException;

/**
 * 一次性限制器，实现资源的一次性获取和永久限制。 该限制器在首次获取资源后会永久切换到受限状态，
 * 之后所有资源获取请求都将被拒绝，适用于需要一次性资源分配的场景。
 *
 * <p>
 * 核心特性：
 * <ul>
 * <li>原子操作：使用AtomicBoolean保证状态变更的线程安全性</li>
 * <li>一次性语义：首次获取资源后永久受限，无法重置</li>
 * <li>轻量级实现：无需显式释放资源，unlock()方法为空操作</li>
 * </ul>
 *
 * <p>
 * 使用场景：
 * <ul>
 * <li>单例资源初始化保护</li>
 * <li>熔断机制实现（故障后永久拒绝请求）</li>
 * <li>资源的一次性分配（如启动配置加载）</li>
 * <li>防止重复执行关键操作（如初始化代码）</li>
 * </ul>
 *
 * @author soeasy.run
 * @see Limiter
 * @see DisposableLock
 */
public class DisposableLimiter implements Limiter {
	/**
	 * 限制状态标志：true表示已受限，false表示未受限
	 */
	private final AtomicBoolean limited = new AtomicBoolean();

	/**
	 * 检查当前是否处于受限状态。 由于该限制器是一次性的，一旦受限将永久受限。
	 *
	 * @return 若已获取过资源则返回true，否则返回false
	 */
	@Override
	public boolean isLimited() {
		return limited.get();
	}

	/**
	 * 设置限制器为受限状态。 使用CAS操作确保原子性，首次调用返回true，后续调用返回false。
	 *
	 * @return 首次调用返回true，后续调用返回false
	 */
	@Override
	public boolean limited() {
		return limited.compareAndSet(false, true);
	}

	/**
	 * 获取资源锁。 返回的锁实现了DisposableLock接口，获取成功后会将限制器置为受限状态。
	 *
	 * @return 一次性资源锁实例
	 */
	@Override
	public Lock getResource() {
		return new DisposableResource();
	}

	/**
	 * 一次性资源锁实现，内部类。 实现了DisposableLock接口，确保资源只能被获取一次。
	 */
	private class DisposableResource implements DisposableLock {
		/**
		 * 尝试获取锁。 使用CAS操作确保原子性，首次调用返回true并将限制器置为受限状态， 后续调用返回false。
		 *
		 * @return 首次调用返回true，后续调用返回false
		 */
		@Override
		public boolean tryLock() {
			return limited.compareAndSet(false, true);
		}

		/**
		 * 释放锁。 由于是一次性锁，释放操作无实际效果，为空实现。
		 */
		@Override
		public void unlock() {
			// 空实现，无需释放
		}

		/**
		 * 获取锁，可响应中断。 实现了DisposableLock接口的默认行为，获取失败时抛出异常。
		 * 
		 * @throws InterruptedException,DisposableLockException
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