package scw.net.http;

import java.io.IOException;
import java.net.Proxy;
import java.net.URL;

public class SimpleHttpClient extends AbstractHttpClient {

	public ClientHttpRequest create(String url, Proxy proxy, Method method)
			throws IOException {
		return new URLConnectionClientHttpRequest(new URL(url), proxy, method);
	}
	
}
