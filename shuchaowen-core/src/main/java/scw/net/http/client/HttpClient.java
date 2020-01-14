package scw.net.http.client;

import java.util.Map;

import scw.net.AbstractResponseCallback;
import scw.net.http.HttpRequest;

public interface HttpClient {
	<T> T invoke(final HttpRequest request, final AbstractResponseCallback<T> response);

	String doGet(String url);

	String doPost(String url, Map<String, ?> parameterMap);
}
