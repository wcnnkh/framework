package scw.utils.locks;

import java.util.concurrent.TimeUnit;

public interface Lock {
	/**
	 * 尝试获取锁 获取到就返回true
	 * @return
	 */
	boolean lock();
	
	/**
	 * 尝试获取锁，如果失败就一直重试
	 * @param period 重试周期
	 * @param timeUnit 重试周期计算方式
	 * @throws InterruptedException
	 */
	void lockWait(long period, TimeUnit timeUnit) throws InterruptedException;
	
	/**
	 * 尝试获取锁，如果失败就一直重试
	 */
	void lockWait() throws InterruptedException;
	
	/**
	 * 取消锁
	 */
	void unlock();
}
