package scw.async;

public interface AsyncLifeCycle {
	void executeBefore(AsyncRunnable asyncRunnable) throws Throwable;

	void executeAfter(AsyncRunnable asyncRunnable) throws Throwable;

	void executeError(Throwable error, AsyncRunnable asyncRunnable) throws Throwable;

	void executeComplete(AsyncRunnable asyncRunnable) throws Throwable;
}
