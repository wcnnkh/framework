package shuchaowen.connection.http;

import java.io.IOException;
import java.net.MalformedURLException;

import shuchaowen.connection.http.enums.Method;

public class HttpGet extends HttpRequestURLConnection{
	
	public HttpGet(String url) throws MalformedURLException, IOException {
		super(url);
		setRequestMethod(Method.GET.name());
		setDoInput(true);
	}
}
