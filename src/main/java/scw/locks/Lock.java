package scw.locks;

public interface Lock {
	/**
	 * 尝试获取锁 获取到就返回true
	 * @return
	 */
	boolean lock();
	
	/**
	 * 获取锁，如果没获取到就会一直等待
	 * @param millis 多久重试(毫秒)
	 * @param nanos 多久重试(纳秒)
	 * @throws InterruptedException
	 */
	void lockWait(long millis, int nanos) throws InterruptedException;
	
	/**
	 * 取消锁
	 */
	void unlock();
}
