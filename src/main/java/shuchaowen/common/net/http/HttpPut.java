package shuchaowen.common.net.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

import shuchaowen.common.enums.HttpMethod;

public class HttpPut extends HttpRequestURLConnection{
	public HttpPut(String url) throws MalformedURLException, IOException {
		super(url);
		setRequestMethod(HttpMethod.PUT.name());
		setDoOutput(true);
		setDoInput(true);
	}
	
	public HttpPut(HttpURLConnection httpURLConnection) {
		super(httpURLConnection);
	}
}
