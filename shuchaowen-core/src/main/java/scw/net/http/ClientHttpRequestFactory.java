package scw.net.http;

import java.io.IOException;
import java.net.Proxy;

public interface ClientHttpRequestFactory {
	ClientHttpRequest create(String url, Proxy proxy, Method method) throws IOException;
}
