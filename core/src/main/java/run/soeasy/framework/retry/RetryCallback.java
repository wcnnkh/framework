package run.soeasy.framework.retry;

public interface RetryCallback<T, E extends Throwable> {
	T doWithRetry(RetryContext retryContext) throws E;
}
