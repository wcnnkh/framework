package scw.net.http.client;

import java.util.Map;

import scw.net.AbstractResponseCallback;
import scw.net.http.SimpleClientHttpRequest;

public interface HttpClient {
	<T> T invoke(final SimpleClientHttpRequest request, final AbstractResponseCallback<T> response);

	String doGet(String url);

	String doPost(String url, Map<String, ?> parameterMap);
}
