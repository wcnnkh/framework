package io.basc.framework.consistency.support;

import io.basc.framework.consistency.CompensatePolicy;
import io.basc.framework.consistency.Compensator;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;

import java.util.concurrent.locks.Lock;

public class DefaultCompensator implements Compensator{
	private static Logger logger = LoggerFactory.getLogger(DefaultCompensator.class);
	private final Runnable action;
	private final String group;
	private final String id;
	private final CompensatePolicy compenstPolicy;
	private boolean done;
	private boolean cancelled;
	
	public DefaultCompensator(String group, String id, Runnable action, CompensatePolicy compenstPolicy) {
		this.action = action;
		this.group = group;
		this.id = id;
		this.compenstPolicy = compenstPolicy;
	}
	
	public String getGroup() {
		return group;
	}

	public String getId() {
		return id;
	}

	@Override
	public void run() {
		if(compenstPolicy.isDone(group, id)){
			return ;
		}

		Lock lock = compenstPolicy.getLock(group, id);
		if(lock.tryLock()){
			try {
				action.run();
				compenstPolicy.done(group, id);
				done = true;
			} catch (Throwable e) {
				logger.error(e, "execute error group [{}] id [{}] runner [{}]", group, id, action);
			}finally{
				lock.unlock();
			}
		}
		return ;
	}

	@Override
	public boolean isCancelled() {
		return cancelled || compenstPolicy.isCancelled(group, id);
	}

	@Override
	public boolean cancel() {
		boolean b = compenstPolicy.cancel(group, id);
		if(b){
			cancelled = true;
		}
		return b;
	}

	@Override
	public boolean isDone() {
		return done || compenstPolicy.isDone(group, id);
	}

}
