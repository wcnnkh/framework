package io.basc.framework.retry.support;

import io.basc.framework.lang.Nullable;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.retry.ExhaustedRetryException;
import io.basc.framework.retry.RecoveryCallback;
import io.basc.framework.retry.RetryCallback;
import io.basc.framework.retry.RetryContext;
import io.basc.framework.retry.RetryException;
import io.basc.framework.retry.RetryListener;
import io.basc.framework.retry.RetryOperations;
import io.basc.framework.retry.RetryPolicy;
import io.basc.framework.retry.TerminatedRetryException;
import io.basc.framework.retry.policy.SimpleRetryPolicy;

public class RetryTemplate implements RetryOperations {
	private static Logger logger = LoggerFactory.getLogger(RetryTemplate.class);
	private volatile RetryListener[] listeners = new RetryListener[0];
	private final RetryPolicy retryPolicy;
	private boolean throwLastExceptionOnExhausted;

	public RetryTemplate() {
		this(new SimpleRetryPolicy());
	}

	public RetryTemplate(RetryPolicy retryPolicy) {
		this.retryPolicy = retryPolicy;
	}

	public final boolean isThrowLastExceptionOnExhausted() {
		return throwLastExceptionOnExhausted;
	}

	public void setThrowLastExceptionOnExhausted(boolean throwLastExceptionOnExhausted) {
		this.throwLastExceptionOnExhausted = throwLastExceptionOnExhausted;
	}

	public final RetryListener[] getListeners() {
		return listeners.clone();
	}

	public void setListeners(RetryListener[] listeners) {
		this.listeners = listeners == null ? new RetryListener[0] : listeners;
	}

	public final <T, E extends Throwable> T execute(RetryCallback<T, E> retryCallback) throws E, RetryException {
		return execute(retryCallback, null);
	}

	public <T, E extends Throwable> T execute(RetryCallback<T, E> retryCallback,
			@Nullable RecoveryCallback<T, E> recoveryCallback) throws E, RetryException {
		RetryPolicy retryPolicy = this.retryPolicy;
		RetryContext context = open(retryPolicy);
		if (logger.isTraceEnabled()) {
			logger.trace("RetryContext retrieved: " + context);
		}

		// Make sure the context is available globally for clients who need
		// it...
		RetrySynchronizationManager.register(context);

		Throwable lastException = null;

		try {

			// Give clients a chance to enhance the context...
			boolean running = doOpenInterceptors(retryCallback, context);

			if (!running) {
				throw new TerminatedRetryException("Retry terminated abnormally by interceptor before first attempt");
			}

			/*
			 * We allow the whole loop to be skipped if the policy or context already forbid
			 * the first try. This is used in the case of external retry to allow a recovery
			 * in handleRetryExhausted without the callback processing (which would throw an
			 * exception).
			 */
			while (canRetry(retryPolicy, context) && !context.isExhaustedOnly()) {

				try {
					if (logger.isDebugEnabled()) {
						logger.debug("Retry: count=" + context.getRetryCount());
					}
					// Reset the last exception, so if we are successful
					// the close interceptors will not think we failed...
					lastException = null;
					return retryCallback.doWithRetry(context);
				} catch (Throwable e) {

					lastException = e;

					doOnErrorInterceptors(retryCallback, context, e);

					try {
						registerThrowable(retryPolicy, context, e);
					} catch (Exception ex) {
						throw new TerminatedRetryException("Could not register throwable", ex);
					}

					if (logger.isDebugEnabled()) {
						logger.debug("Checking for rethrow: count=" + context.getRetryCount());
					}

					if (logger.isDebugEnabled()) {
						logger.debug("Rethrow in retry for policy: count=" + context.getRetryCount());
					}
				}

				/*
				 * A stateful attempt that can retry should have rethrown the exception by now -
				 * i.e. we shouldn't get this far for a stateful attempt if it can retry.
				 */
			}

			if (logger.isDebugEnabled()) {
				logger.debug("Retry failed last attempt: count=" + context.getRetryCount());
			}

			if (context.isExhaustedOnly()) {
				rethrow(context, "Retry exhausted after last attempt with no recovery path.");
			}

			return handleRetryExhausted(recoveryCallback, context);
		} catch (Throwable e) {
			E ex = wrapIfNecessary(e);
			throw ex;
		} finally {
			close(retryPolicy, context, lastException == null);
			doCloseInterceptors(retryCallback, context, lastException);
			RetrySynchronizationManager.clear();
		}

	}

	/**
	 * Delegate to the {@link RetryPolicy} having checked in the cache for an
	 * existing value if the state is not null.
	 *
	 * @param retryPolicy a {@link RetryPolicy} to delegate the context creation
	 * @return a retry context, either a new one or the one used last time the same
	 *         state was encountered
	 */
	protected RetryContext open(RetryPolicy retryPolicy) {
		return retryPolicy.open(RetrySynchronizationManager.getContext());
	}

	/**
	 * @param retryPolicy
	 * @param context
	 * @param e
	 */
	protected void registerThrowable(RetryPolicy retryPolicy, RetryContext context, Throwable e) {
		retryPolicy.registerThrowable(context, e);
	}

	/**
	 * Clean up the cache if necessary and close the context provided (if the flag
	 * indicates that processing was successful).
	 *
	 * @param context
	 * @param succeeded
	 */
	protected void close(RetryPolicy retryPolicy, RetryContext context, boolean succeeded) {
		retryPolicy.close(context);
	}

	private <T, E extends Throwable> boolean doOpenInterceptors(RetryCallback<T, E> callback, RetryContext context) {
		boolean result = true;
		for (RetryListener listener : listeners) {
			result = result && listener.open(context, callback);
		}
		return result;

	}

	/**
	 * Actions to take after final attempt has failed. If there is state clean up
	 * the cache. If there is a recovery callback, execute that and return its
	 * result. Otherwise throw an exception.
	 *
	 * @param recoveryCallback the callback for recovery (might be null)
	 * @param context          the current retry context
	 * @throws Exception               if the callback does, and if there is no
	 *                                 callback and the state is null then the last
	 *                                 exception from the context
	 * @throws ExhaustedRetryException if the state is not null and there is no
	 *                                 recovery callback
	 */
	protected <T, E extends Throwable> T handleRetryExhausted(RecoveryCallback<T, E> recoveryCallback,
			RetryContext context) throws Throwable {
		if (recoveryCallback != null) {
			return recoveryCallback.recover(context);
		}

		logger.debug("Retry exhausted after last attempt with no recovery path.");
		rethrow(context, "Retry exhausted after last attempt with no recovery path");
		// STOP
		// 不可能执行到下面
		throw wrapIfNecessary(context.getLastThrowable());
	}

	/**
	 * Decide whether to proceed with the ongoing retry attempt. This method is
	 * called before the {@link RetryCallback} is executed, but after the backoff
	 * and open interceptors.
	 *
	 * @param retryPolicy the policy to apply
	 * @param context     the current retry context
	 * @return true if we can continue with the attempt
	 */
	protected boolean canRetry(RetryPolicy retryPolicy, RetryContext context) {
		return retryPolicy.canRetry(context);
	}

	protected <E extends Throwable> void rethrow(RetryContext context, String message) throws E {
		if (throwLastExceptionOnExhausted) {
			@SuppressWarnings("unchecked")
			E rethrow = (E) context.getLastThrowable();
			throw rethrow;
		} else {
			throw new ExhaustedRetryException(message, context.getLastThrowable());
		}
	}

	private <T, E extends Throwable> void doCloseInterceptors(RetryCallback<T, E> callback, RetryContext context,
			Throwable lastException) {
		for (int i = listeners.length; i-- > 0;) {
			listeners[i].close(context, callback, lastException);
		}
	}

	private <T, E extends Throwable> void doOnErrorInterceptors(RetryCallback<T, E> callback, RetryContext context,
			Throwable throwable) {
		for (int i = listeners.length; i-- > 0;) {
			listeners[i].onError(context, callback, throwable);
		}
	}

	/**
	 * Re-throws the original throwable if it is an Exception, and wraps
	 * non-exceptions into {@link RetryException}.
	 */
	private static <E extends Throwable> E wrapIfNecessary(Throwable throwable) throws RetryException {
		if (throwable instanceof Error) {
			throw (Error) throwable;
		} else if (throwable instanceof Exception) {
			@SuppressWarnings("unchecked")
			E rethrow = (E) throwable;
			return rethrow;
		} else {
			throw new RetryException("Exception in batch process", throwable);
		}
	}
}
