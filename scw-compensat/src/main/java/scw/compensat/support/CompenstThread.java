package scw.compensat.support;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import scw.compensat.CompenstPolicy;
import scw.logger.Logger;
import scw.logger.LoggerFactory;

public class CompenstThread extends Thread {
	private static Logger logger = LoggerFactory
			.getLogger(CompenstThread.class);
	private final CompenstPolicy compenstPolicy;
	private final String group;
	private final long timeout;
	private final TimeUnit timeUnit;

	public CompenstThread(CompenstPolicy compenstPolicy, String group,
			long timeout, TimeUnit timeUnit) {
		this.compenstPolicy = compenstPolicy;
		this.group = group;
		this.timeout = timeout;
		this.timeUnit = timeUnit;
	}

	@Override
	public void run() {
		super.run();
		String id = compenstPolicy.getLastUnfinishedId(group);
		if (id == null) {
			return;
		}

		Runnable runnable = compenstPolicy.get(group, id);
		if (runnable == null) {
			return;
		}

		Lock lock = compenstPolicy.getLock(group, id);
		try {
			if (lock.tryLock(timeout, timeUnit)) {
				runnable.run();
				compenstPolicy.done(group, id);
			}
		} catch (Throwable e) {
			logger.error(e, "Compenst fail group [{}] id [{}] runner [{}]",
					group, id, runnable);
		} finally {
			lock.unlock();
		}
	}

}
