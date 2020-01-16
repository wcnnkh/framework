package scw.net.http.client;

import java.io.IOException;

import scw.net.ClientRequest;
import scw.net.http.HttpRequest;

public interface ClientHttpRequest extends ClientRequest, HttpRequest{
	ClientHttpResponse execute() throws IOException;
}
