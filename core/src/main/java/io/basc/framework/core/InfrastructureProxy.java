package io.basc.framework.core;

public interface InfrastructureProxy {

	/**
	 * Return the underlying resource (never {@code null}).
	 */
	Object getWrappedObject();
}
