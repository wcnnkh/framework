package scw.http.client;

import java.io.IOException;

public class ResourceAccessException extends RestClientException {

	private static final long serialVersionUID = -8513182514355844870L;


	/**
	 * Construct a new {@code HttpIOException} with the given message.
	 * @param msg the message
	 */
	public ResourceAccessException(String msg) {
		super(msg);
	}

	/**
	 * Construct a new {@code HttpIOException} with the given message and {@link IOException}.
	 * @param msg the message
	 * @param ex the {@code IOException}
	 */
	public ResourceAccessException(String msg, IOException ex) {
		super(msg, ex);
	}

}
