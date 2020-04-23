package scw.async;

public interface AsyncExecutor {
	void addAsyncLifeCycle(AsyncLifeCycle asyncLifeCycle);

	void execute(AsyncRunnable asyncRunnable);
}
