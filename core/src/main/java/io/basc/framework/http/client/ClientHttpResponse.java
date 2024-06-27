package io.basc.framework.http.client;

import java.io.IOException;

import io.basc.framework.http.HttpInputMessage;
import io.basc.framework.http.HttpStatus;
import io.basc.framework.net.client.ClientResponse;

public interface ClientHttpResponse extends HttpInputMessage, ClientResponse {
	/**
	 * Return the HTTP status code of the response.
	 * 
	 * @return the HTTP status as an HttpStatus enum value
	 * @throws IOException              in case of I/O errors
	 * @throws IllegalArgumentException in case of an unknown HTTP status code
	 * @see HttpStatus#valueOf(int)
	 */
	default HttpStatus getStatusCode() throws IOException {
		return HttpStatus.valueOf(getRawStatusCode());
	}

	int getRawStatusCode() throws IOException;

	String getStatusText() throws IOException;

	void close();
}
