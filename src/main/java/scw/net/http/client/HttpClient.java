package scw.net.http.client;

import java.util.Map;

import scw.net.AbstractResponse;
import scw.net.http.HttpRequest;

public interface HttpClient {
	<T> T invoke(final HttpRequest request, final AbstractResponse<T> response);

	String doGet(String url);

	String doPost(String url, Map<String, ?> parameterMap);
}
