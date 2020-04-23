package scw.async;

public interface AsyncLifeCycle {
	void executeBefore(AsyncRunnable asyncRunnable) throws Exception;

	void executeAfter(AsyncRunnable asyncRunnable) throws Exception;

	void executeError(AsyncRunnable asyncRunnable) throws Exception;

	void executeComplete(AsyncRunnable asyncRunnable) throws Exception;
}
