package shuchaowen.connection.http;

import java.io.IOException;
import java.net.MalformedURLException;

public class HttpDelete extends HttpRequestURLConnection{

	public HttpDelete(String url) throws MalformedURLException, IOException {
		super(url);
		setRequestMethod("DELETE");
		setDoInput(true);
	}
	
}
