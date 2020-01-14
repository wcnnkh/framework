package scw.net.http;

import scw.net.ClientResponse;

public interface ClientHttpResponse extends ClientResponse{
	int getResponseCode();

	String getResponseMessage();
}
