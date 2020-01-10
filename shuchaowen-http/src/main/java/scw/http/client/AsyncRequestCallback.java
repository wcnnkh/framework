package scw.http.client;

import java.io.IOException;

public interface AsyncRequestCallback {
	/**
	 * Gets called by {@link AsyncRestTemplate#execute} with an opened {@code ClientHttpRequest}.
	 * Does not need to care about closing the request or about handling errors:
	 * this will all be handled by the {@code RestTemplate}.
	 * @param request the active HTTP request
	 * @throws java.io.IOException in case of I/O errors
	 */
	void doWithRequest(AsyncClientHttpRequest request) throws IOException;
}
