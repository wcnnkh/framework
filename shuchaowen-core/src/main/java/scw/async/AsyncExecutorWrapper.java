package scw.async;

public class AsyncExecutorWrapper implements AsyncExecutor {
	private AsyncExecutor asyncExecutor;

	public AsyncExecutorWrapper(AsyncExecutor asyncExecutor) {
		this.asyncExecutor = asyncExecutor;
	}

	public AsyncExecutor getAsyncExecutor() {
		return asyncExecutor;
	}

	public void addAsyncLifeCycle(AsyncLifeCycle asyncLifeCycle) {
		asyncExecutor.addAsyncLifeCycle(asyncLifeCycle);
	}

	public void execute(AsyncRunnable asyncRunnable) {
		asyncExecutor.execute(asyncRunnable);
	}
}
