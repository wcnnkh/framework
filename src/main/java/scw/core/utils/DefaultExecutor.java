package scw.core.utils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import scw.core.Destroy;
import scw.core.annotation.NotRequire;

public final class DefaultExecutor implements Executor, Destroy {
	private ThreadPoolExecutor threadPoolExecutor;

	public DefaultExecutor(@NotRequire ThreadFactory threadFactory,
			@NotRequire RejectedExecutionHandler rejectedExecutionHandler) {
		int corePoolSize = StringUtils.parseInt(SystemPropertyUtils.getProperty("executor.pool.core.size"), 32);
		int maximumPoolSize = StringUtils.parseInt(SystemPropertyUtils.getProperty("executor.pool.max.size"), 512);
		long keepAliveTime = StringUtils.parseLong(SystemPropertyUtils.getProperty("executor.pool.keepAliveTime"), 1);
		String unitName = SystemPropertyUtils.getProperty("executor.pool.keepAliveTime.unit");
		TimeUnit timeUnit = (TimeUnit) (StringUtils.isEmpty(unitName) ? TimeUnit.HOURS
				: EnumUtils.valueOf(TimeUnit.class, unitName));
		if (rejectedExecutionHandler == null) {
			this.threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, timeUnit,
					createWorkQueue(), threadFactory == null ? Executors.defaultThreadFactory() : threadFactory);
		} else {
			this.threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, timeUnit,
					createWorkQueue(), threadFactory == null ? Executors.defaultThreadFactory() : threadFactory,
					rejectedExecutionHandler);
		}
	}

	protected BlockingQueue<Runnable> createWorkQueue() {
		return new SynchronousQueue<Runnable>();
	}

	public void execute(Runnable command) {
		threadPoolExecutor.execute(command);
	}

	public void destroy() {
		threadPoolExecutor.shutdownNow();
	}
}
