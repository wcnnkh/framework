package scw.core.net.http.client;

import java.util.Map;

import scw.core.net.AbstractResponse;
import scw.core.net.http.HttpRequest;

public interface HttpClient {
	<T> T invoke(final HttpRequest request, final AbstractResponse<T> response);

	String doGet(String url);

	String doPost(String url, Map<String, ?> parameterMap);
}
