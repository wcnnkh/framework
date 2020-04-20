package scw.async.local;

import java.util.concurrent.ExecutorService;

import scw.async.AbstractAsyncExecutor;
import scw.async.AsyncRunnable;
import scw.core.Destroy;
import scw.logger.Logger;
import scw.logger.LoggerUtils;

public abstract class ExecutorServiceAsyncExecutor extends
		AbstractAsyncExecutor implements Destroy {
	protected final Logger logger = LoggerUtils.getLogger(getClass());
	private final boolean useShutdown;

	public ExecutorServiceAsyncExecutor(boolean useShutdown) {
		this.useShutdown = useShutdown;
	}

	public boolean isUseShutdown() {
		return useShutdown;
	}

	protected abstract ExecutorService getExecutorService();

	public void destroy() throws Exception {
		if (isUseShutdown()) {
			getExecutorService().shutdownNow();
		}
	}

	protected Runnable createRunnable(AsyncRunnable asyncRunnable) {
		return new InternalRunnable(asyncRunnable);
	}

	public void execute(AsyncRunnable asyncRunnable) {
		getExecutorService().execute(createRunnable(asyncRunnable));
	}

	final class InternalRunnable implements Runnable {
		private final AsyncRunnable asyncRunnable;

		public InternalRunnable(AsyncRunnable asyncRunnable) {
			this.asyncRunnable = asyncRunnable;
		}

		public void run() {
			try {
				executeInternal(asyncRunnable);
			} catch (Exception e) {
				logger.error(e, "async execute error: {}", asyncRunnable);
			}
		}
	}

}
