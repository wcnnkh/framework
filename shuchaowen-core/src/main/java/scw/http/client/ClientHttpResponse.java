package scw.http.client;

import java.io.Closeable;
import java.io.IOException;

import scw.http.HttpHeaders;
import scw.http.HttpStatus;
import scw.net.message.InputMessage;

public interface ClientHttpResponse extends InputMessage,
		Closeable {
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
	
	void close();
}
