package shuchaowen.core.connection.http;

import java.io.IOException;
import java.net.MalformedURLException;

public class HttpPOST extends HttpRequestURLConnection{

	public HttpPOST(String url) throws MalformedURLException, IOException {
		super(url);
		setRequestMethod("POST");
		setDoOutput(true);
		setDoInput(true);
	}
}
