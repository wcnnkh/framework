package shuchaowen.common.net.http;

import java.io.IOException;
import java.net.MalformedURLException;

import shuchaowen.common.enums.HttpMethod;

public class HttpPost extends HttpRequestURLConnection{
	
	public HttpPost(String url) throws MalformedURLException, IOException {
		super(url);
		setRequestMethod(HttpMethod.POST.name());
		setDoOutput(true);
		setDoInput(true);
	}
}
