package scw.net.http.client;

import java.io.IOException;

public interface ClientHttpResponseExtractor<T> {
	T execute(ClientHttpResponse response) throws IOException;
}
