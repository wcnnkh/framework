package scw.http.client;

import java.io.IOException;

public interface ResponseExtractor<T> {
	/**
	 * Extract data from the given {@code ClientHttpResponse} and return it.
	 * @param response the HTTP response
	 * @return the extracted data
	 * @throws IOException in case of I/O errors
	 */
	T extractData(ClientHttpResponse response) throws IOException;
}
