package scw.net.http;

import java.io.IOException;

import scw.net.http.HttpHeaders;
import scw.net.http.HttpStatus;
import scw.net.message.InputMessage;

public interface HttpResponseMessage extends InputMessage {
	HttpHeaders getHeaders();

	/**
	 * Return the HTTP status code of the response.
	 * 
	 * @return the HTTP status as an HttpStatus enum value
	 * @throws IOException
	 *             in case of I/O errors
	 * @throws IllegalArgumentException
	 *             in case of an unknown HTTP status code
	 * @see HttpStatus#valueOf(int)
	 */
	HttpStatus getStatusCode() throws IOException;

	int getRawStatusCode() throws IOException;

	String getStatusText() throws IOException;
}
