package scw.net.http;

import scw.net.ClientResponse;
import scw.util.MultiValueMap;

public interface ClientHttpResponse extends ClientResponse {
	MultiValueMap<String, String> getHeaders();

	int getResponseCode();

	String getResponseMessage();
}
