package scw.net.client;

import java.io.IOException;

public interface ClientResponseExtractor<R extends ClientResponse, T> {
	T execute(R response) throws IOException;
}
