package io.basc.framework.util;

public interface Lifecycle {
	/**
	 * Start this component.
	 * <p>
	 * Should not throw an exception if the component is already running.
	 * <p>
	 * In the case of a container, this will propagate the start signal to all
	 * components that apply.
	 */
	void start();

	/**
	 * Stop this component, typically in a synchronous fashion, such that the
	 * component is fully stopped upon return of this method.
	 * <p>
	 * Note that this stop notification is not guaranteed to come before
	 * destruction: On regular shutdown, {@code Lifecycle} beans will first receive
	 * a stop notification before the general destruction callbacks are being
	 * propagated; however, on hot refresh during a context's lifetime or on aborted
	 * refresh attempts, a given bean's destroy method will be called without any
	 * consideration of stop signals upfront.
	 * <p>
	 * Should not throw an exception if the component is not running (not started
	 * yet).
	 * <p>
	 * In the case of a container, this will propagate the stop signal to all
	 * components that apply.
	 */
	void stop();

	/**
	 * Check whether this component is currently running.
	 * <p>
	 * In the case of a container, this will return {@code true} only if <i>all</i>
	 * components that apply are currently running.
	 * 
	 * @return whether the component is currently running
	 */
	boolean isRunning();
}
