package scw.net.http;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class HttpsRequestURLConnection extends HttpRequestURLConnection implements HttpsRequest {
	public HttpsRequestURLConnection(String url) throws MalformedURLException, IOException {
		this((HttpsURLConnection) new URL(url).openConnection());
	}

	public HttpsRequestURLConnection(String url, Proxy proxy) throws MalformedURLException, IOException {
		this((HttpsURLConnection) new URL(url).openConnection(proxy));
	}

	public HttpsRequestURLConnection(HttpsURLConnection httpURLConnection) {
		super(httpURLConnection);
	}

	@Override
	protected void init() {
		getHttpsURLConnection().setInstanceFollowRedirects(false);
		super.init();
	}

	public HttpsURLConnection getHttpsURLConnection() {
		return (HttpsURLConnection) getHttpURLConnection();
	}

}
