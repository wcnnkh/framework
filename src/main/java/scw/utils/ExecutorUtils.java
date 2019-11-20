package scw.utils;

import java.lang.reflect.Proxy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import scw.core.utils.StringUtils;
import scw.core.utils.SystemPropertyUtils;

public final class ExecutorUtils {
	private static final int DEFAULT_CORE_POOL_SIZE = StringUtils
			.parseInt(SystemPropertyUtils.getProperty("executor.pool.core.size"), 16);
	private static final int DEFAULT_MAXMUM_POOL_SIZE = StringUtils
			.parseInt(SystemPropertyUtils.getProperty("executor.pool.max.size"), 512);
	private static final long DEFAULT_KEEP_ALIVE_TIME = StringUtils
			.parseLong(SystemPropertyUtils.getProperty("executor.pool.keepAliveTime"), 1);
	private static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.valueOf(StringUtils
			.toString(SystemPropertyUtils.getProperty("executor.pool.keepAliveTime.unit"), TimeUnit.HOURS.name()));

	private ExecutorUtils() {
	};

	public static ExecutorService porxyDestroy(
			ExecutorServiceDestroyProxyInvocationHandler<? extends ExecutorService> invocationHandler) {
		return (ExecutorService) Proxy.newProxyInstance(ExecutorServiceDestroyProxy.class.getClassLoader(),
				new Class<?>[] { ExecutorServiceDestroyProxy.class }, invocationHandler);
	}

	public static ExecutorService porxyDestroy(ThreadPoolExecutor threadPoolExecutor, boolean shutdownNow) {
		return porxyDestroy(new ThreadPoolExecutorDestroyProxy(threadPoolExecutor, shutdownNow));
	}

	public static ExecutorService newExecutorService(int corePoolSize, int maximumPoolSize, long keepAliveTime,
			TimeUnit timeUnit, boolean proxyDestroy) {
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime,
				timeUnit, new SynchronousQueue<Runnable>());
		return proxyDestroy ? porxyDestroy(threadPoolExecutor, true) : threadPoolExecutor;
	}

	public static ExecutorService newExecutorService(boolean proxyDestroy) {
		return newExecutorService(DEFAULT_CORE_POOL_SIZE, DEFAULT_MAXMUM_POOL_SIZE, DEFAULT_KEEP_ALIVE_TIME,
				DEFAULT_TIME_UNIT, proxyDestroy);
	}

	static class ThreadPoolExecutorDestroyProxy
			extends ExecutorServiceDestroyProxyInvocationHandler<ThreadPoolExecutor> {
		private boolean shutdownNow;

		public ThreadPoolExecutorDestroyProxy(ThreadPoolExecutor executorService, boolean shutdownNow) {
			super(executorService);
			this.shutdownNow = shutdownNow;
		}

		public void destroy() {
			if (shutdownNow) {
				getTargetExecutorService().shutdownNow();
			} else {
				getTargetExecutorService().shutdown();
			}
		}
	}
}
