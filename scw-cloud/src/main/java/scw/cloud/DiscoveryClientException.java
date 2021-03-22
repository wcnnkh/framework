package scw.cloud;

import scw.lang.NestedRuntimeException;

public class DiscoveryClientException extends NestedRuntimeException {
	private static final long serialVersionUID = 1L;

	public DiscoveryClientException(String msg) {
		super(msg);
	}

	public DiscoveryClientException(Throwable cause) {
		super(cause);
	}

	public DiscoveryClientException(String message, Throwable cause) {
		super(message, cause);
	}
}
