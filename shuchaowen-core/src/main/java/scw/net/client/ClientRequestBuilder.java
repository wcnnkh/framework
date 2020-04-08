package scw.net.client;

import java.io.IOException;
import java.net.URI;

public interface ClientRequestBuilder<T extends ClientRequest> {
	URI getUri();

	T builder() throws IOException;
}
