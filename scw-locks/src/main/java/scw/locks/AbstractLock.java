package scw.locks;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import scw.core.GlobalPropertyFactory;

public abstract class AbstractLock implements Lock {
	private static final long DEFAULT_SLEEP_TIME = GlobalPropertyFactory
			.getInstance().getValue("lock.sleep.time", Long.class, 1L);
	private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newScheduledThreadPool(1);
	
	static{
		Runtime.getRuntime().addShutdownHook(new Thread(){
			@Override
			public void run() {
				SCHEDULED_EXECUTOR_SERVICE.shutdownNow();
				super.run();
			}
		});
	}
	
	public boolean tryLock(long period, TimeUnit timeUnit)
			throws InterruptedException {
		boolean b = false;
		while (!(b = tryLock())) {
			timeUnit.sleep(period);
		}
	
		
		return b;
	}
	
	private AtomicBoolean autoRenewal = new AtomicBoolean(false);
	private volatile ScheduledFuture<?> scheduledFuture;
	
	/**
	 * 在未解锁进自动续期以处理存在过期时间的锁自动解锁的问题
	 * @return
	 */
	public boolean autoRenewal(long period, TimeUnit timeUnit){
		if(autoRenewal.get()){
			return false;
		}
		
		if(autoRenewal.compareAndSet(false, true)){
			scheduledFuture = SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(new Runnable() {
				
				public void run() {
					if(!renewal()){
						cancelAutoRenewal();
					}
				}
			}, period, period, timeUnit);
			return true;
		}
		
		return false;
	}
	
	/**
	 * 取消自动续期
	 * @return
	 */
	public boolean cancelAutoRenewal(){
		if(!autoRenewal.get()){
			return false;
		}
		
		if(autoRenewal.compareAndSet(true, false)){
			scheduledFuture.cancel(true);
			return true;
		}
		return false;
	}
	
	

	/**
	 * 默认为ms试一次
	 */
	public void lock() {
		try {
			lockInterruptibly();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public void lockInterruptibly() throws InterruptedException {
		while (!tryLock()) {
			TimeUnit.MILLISECONDS.sleep(DEFAULT_SLEEP_TIME);
		}
	}
}
