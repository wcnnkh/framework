package scw.net.http;

import javax.net.ssl.HttpsURLConnection;

public interface HttpsRequest extends HttpRequest {
	
	HttpsURLConnection getHttpsURLConnection();
	
}
