package scw.cloud;

import scw.lang.NestedRuntimeException;

public class ServiceRegistryException extends NestedRuntimeException {
	private static final long serialVersionUID = 1L;

	public ServiceRegistryException(String msg) {
		super(msg);
	}

	public ServiceRegistryException(Throwable cause) {
		super(cause);
	}

	public ServiceRegistryException(String message, Throwable cause) {
		super(message, cause);
	}
}
