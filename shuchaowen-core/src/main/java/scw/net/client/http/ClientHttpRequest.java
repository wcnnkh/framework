package scw.net.client.http;

import java.io.IOException;

import scw.net.client.ClientRequest;
import scw.net.http.HttpRequest;

public interface ClientHttpRequest extends ClientRequest, HttpRequest {
	ClientHttpResponse execute() throws IOException;
}
