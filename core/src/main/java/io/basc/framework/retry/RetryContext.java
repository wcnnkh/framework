package io.basc.framework.retry;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.attribute.EditableAttributes;

public interface RetryContext extends EditableAttributes<String, Object>{
	/**
	 * Accessor for the parent context if retry blocks are nested.
	 * @return the parent or null if there is none.
	 */
	@Nullable
	RetryContext getParent();
	
	/**
	 * Counts the number of retry attempts. Before the first attempt this counter is zero,
	 * and before the first and subsequent attempts it should increment accordingly.
	 * @return the number of retries.
	 */
	int getRetryCount();
	
	/**
	 * Accessor for the exception object that caused the current retry.
	 * @return the last exception that caused a retry, or possibly null. It will be null
	 * if this is the first attempt, but also if the enclosing policy decides not to
	 * provide it (e.g. because of concerns about memory usage).
	 */
	@Nullable
	Throwable getLastThrowable();
	
	/**
	 * Signal to the framework that no more attempts should be made to try or retry the
	 * current {@link RetryCallback}.
	 */
	void setExhaustedOnly();

	/**
	 * Public accessor for the exhausted flag {@link #setExhaustedOnly()}.
	 * @return true if the flag has been set.
	 */
	boolean isExhaustedOnly();
}
