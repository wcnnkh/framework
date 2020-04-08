package scw.net.client;

import java.io.IOException;

public interface ClientRequestCallback<R extends ClientRequest> {
	void callback(R clientRequest) throws IOException;
}
