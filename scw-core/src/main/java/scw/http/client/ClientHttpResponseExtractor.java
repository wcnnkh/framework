package scw.http.client;

import java.io.IOException;

@FunctionalInterface
public interface ClientHttpResponseExtractor<T> {
	T execute(ClientHttpResponse response) throws IOException;
}
