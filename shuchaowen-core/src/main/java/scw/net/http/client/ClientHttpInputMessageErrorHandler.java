package scw.net.http.client;

import java.io.IOException;

public interface ClientHttpInputMessageErrorHandler {
	/**
	 * Indicate whether the given response has any errors.
	 * <p>
	 * Implementations will typically inspect the
	 * {@link ClientHttpResponse#getStatusCode() HttpStatus} of the response.
	 * 
	 * @param response
	 *            the response to inspect
	 * @return {@code true} if the response indicates an error; {@code false}
	 *         otherwise
	 * @throws IOException
	 *             in case of I/O errors
	 */
	boolean hasError(ClientHttpInputMessage response) throws IOException;

	/**
	 * Handle the error in the given response.
	 * <p>
	 * This method is only called when {@link #hasError(ClientHttpResponse)} has
	 * returned {@code true}.
	 * 
	 * @param response
	 *            the response with the error
	 * @throws IOException
	 *             in case of I/O errors
	 */
	void handleError(ClientHttpInputMessage response) throws IOException;
}
