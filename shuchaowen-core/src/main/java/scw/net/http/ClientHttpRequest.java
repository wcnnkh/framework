package scw.net.http;

import java.io.IOException;

import scw.net.ClientRequest;

public interface ClientHttpRequest extends ClientRequest {
	Method getMethod();

	ClientHttpResponse execute() throws IOException;
}
