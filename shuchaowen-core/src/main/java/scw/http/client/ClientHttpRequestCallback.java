package scw.http.client;

import java.io.IOException;

public interface ClientHttpRequestCallback {
	void callback(ClientHttpRequest clientRequest) throws IOException;
}
