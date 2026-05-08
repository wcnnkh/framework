package run.soeasy.framework.retry;

public interface RetryPolicy {

	boolean canRetry(RetryContext context);

	RetryContext open(RetryContext parent);

	void close(RetryContext context);
	
	void registerThrowable(RetryContext context, Throwable throwable);
}
