package io.basc.framework.locks;

import io.basc.framework.core.Assert;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 一些锁的实现为了防止死锁会设置超时时间，有时因一些意外导致锁超时但不应该释放所以提供了续期的方法
 * @author shuchaowen
 *
 */
public abstract class RenewableLock extends AbstractLock{
	private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
	
	static{
		Runtime.getRuntime().addShutdownHook(new Thread(){
			@Override
			public void run() {
				SCHEDULED_EXECUTOR_SERVICE.shutdownNow();
				super.run();
			}
		});
	}
	
	private AtomicBoolean autoRenewal = new AtomicBoolean(false);
	private volatile ScheduledFuture<?> scheduledFuture;
	
	private final TimeUnit timeUnit;
	private final long timeout;
	
	public RenewableLock(TimeUnit timeUnit, long timeout){
		this.timeout = timeout;
		this.timeUnit = timeUnit;
	}
	
	public long getTimeout(TimeUnit timeUnit) {
		return timeUnit.convert(this.timeout, this.timeUnit);
	}
	
	public boolean autoRenewal(){
		return autoRenewal(getTimeout(TimeUnit.MILLISECONDS)/2, TimeUnit.MILLISECONDS);
	}
	
	/**
	 * 在未解锁进自动续期以处理存在过期时间的锁自动解锁的问题
	 * @return
	 */
	public boolean autoRenewal(long period, TimeUnit timeUnit){
		Assert.requiredArgument(period > 0, "period");
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
	 * 续期
	 * @return
	 */
	public boolean renewal(){
		return renewal(timeout, timeUnit);
	}
	
	/**
	 * 续期
	 * @param time
	 * @param unit
	 * @return
	 */
	public abstract boolean renewal(long time, TimeUnit unit);
}
