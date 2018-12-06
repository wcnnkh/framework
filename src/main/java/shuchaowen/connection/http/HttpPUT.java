package shuchaowen.connection.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

public class HttpPUT extends HttpRequestURLConnection{
	public HttpPUT(String url) throws MalformedURLException, IOException {
		super(url);
		setRequestMethod("PUT");
		setDoOutput(true);
		setDoInput(true);
	}
	
	public HttpPUT(HttpURLConnection httpURLConnection) {
		super(httpURLConnection);
	}
}
