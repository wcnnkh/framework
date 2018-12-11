package shuchaowen.connection.http;

import java.io.IOException;
import java.net.MalformedURLException;

import shuchaowen.connection.http.enums.Method;

public class HttpPost extends HttpRequestURLConnection{
	
	public HttpPost(String url) throws MalformedURLException, IOException {
		super(url);
		setRequestMethod(Method.POST.name());
		setDoOutput(true);
		setDoInput(true);
	}
}
