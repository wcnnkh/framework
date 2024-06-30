package io.basc.framework.net.server;

public class ServerException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ServerException() {
		super();
	}

	public ServerException(String message) {
		super(message);
	}

	public ServerException(String message, Throwable cause) {
		super(message, cause);
	}

	public ServerException(Throwable cause) {
		super(cause);
	}
}
