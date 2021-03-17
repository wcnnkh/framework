package scw.retry.support;

import scw.lang.NamedThreadLocal;
import scw.retry.RetryContext;
import scw.retry.RetryOperations;


public final class RetrySynchronizationManager {

	private RetrySynchronizationManager() {}

	private static final ThreadLocal<RetryContext> context = new NamedThreadLocal<RetryContext>(RetrySynchronizationManager.class.getSimpleName());

	/**
	 * Public accessor for the locally enclosing {@link RetryContext}.
	 * 
	 * @return the current retry context, or null if there isn't one
	 */
	public static RetryContext getContext() {
		RetryContext result = (RetryContext) context.get();
		return result;
	}

	/**
	 * Method for registering a context - should only be used by
	 * {@link RetryOperations} implementations to ensure that
	 * {@link #getContext()} always returns the correct value.
	 * 
	 * @param context the new context to register
	 * @return the old context if there was one
	 */
	public static RetryContext register(RetryContext context) {
		RetryContext oldContext = getContext();
		RetrySynchronizationManager.context.set(context);
		return oldContext;
	}

	/**
	 * Clear the current context at the end of a batch - should only be used by
	 * {@link RetryOperations} implementations.
	 * 
	 * @return the old value if there was one.
	 */
	public static RetryContext clear() {
		RetryContext value = getContext();
		RetryContext parent = value == null ? null : value.getParent();
		RetrySynchronizationManager.context.set(parent);
		return value;
	}

}
