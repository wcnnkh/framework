package shuchaowen.common.net.http;

import java.io.IOException;
import java.net.MalformedURLException;

import shuchaowen.common.enums.HttpMethod;

public class HttpGet extends HttpRequestURLConnection{
	
	public HttpGet(String url) throws MalformedURLException, IOException {
		super(url);
		setRequestMethod(HttpMethod.GET.name());
		setDoInput(true);
	}
}
