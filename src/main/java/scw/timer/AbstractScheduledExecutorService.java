package scw.timer;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import scw.common.RunnableStaticProxy;

public class AbstractScheduledExecutorService implements ScheduledExecutorService{
	private java.util.concurrent.ScheduledExecutorService service;
	
	public AbstractScheduledExecutorService(int corePoolSize){
		this.service = Executors.newScheduledThreadPool(corePoolSize);
	}
	
	public void scheduleAtFixedRate(String key, Runnable command, long initialDelay, long period, TimeUnit unit) {
		service.scheduleAtFixedRate(new RunnableStaticProxy(command) {
			
			@Override
			protected boolean before() {
				
				return false;
			}
			
			@Override
			protected void after() {
				//ignore
			}
		}, initialDelay, period, unit);
	}

	public void schedule(Runnable command, long delay, TimeUnit unit) {
		// TODO Auto-generated method stub
		
	}

	public void scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
		// TODO Auto-generated method stub
		
	}

	public void scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
		// TODO Auto-generated method stub
		
	}

}
