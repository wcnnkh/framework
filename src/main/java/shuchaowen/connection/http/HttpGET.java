package shuchaowen.connection.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

public class HttpGET extends HttpRequestURLConnection{
	public HttpGET(String url) throws MalformedURLException, IOException {
		super(url);
		setRequestMethod("GET");
		setDoInput(true);
	}
	
	public HttpGET(HttpURLConnection httpURLConnection) {
		super(httpURLConnection);
	}
}
