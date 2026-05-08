package run.soeasy.framework.retry;

import lombok.experimental.UtilityClass;

@UtilityClass
public class RetrySynchronizationManager {
	private static final ThreadLocal<RetryContext> context = new ThreadLocal<RetryContext>();

	public static RetryContext getContext() {
		RetryContext result = context.get();
		return result;
	}

	public static RetryContext register(RetryContext context) {
		RetryContext oldContext = getContext();
		RetrySynchronizationManager.context.set(context);
		return oldContext;
	}

	public static RetryContext clear() {
		RetryContext value = getContext();
		RetryContext parent = value == null ? null : value.getParent();
		RetrySynchronizationManager.context.set(parent);
		return value;
	}
}
