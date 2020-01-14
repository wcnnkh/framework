package scw.net.http;

import java.io.IOException;
import java.net.Proxy;
import java.net.URL;

public class DefaultClientHttpRequestFactory implements ClientHttpRequestFactory {

	public URLConnectionClientHttpRequest create(String url, Proxy proxy, Method method) throws IOException {
		return new URLConnectionClientHttpRequest(new URL(url), proxy, method);
	}

}
