package io.basc.framework.http.client;

import java.io.IOException;

import io.basc.framework.http.HttpOutputMessage;
import io.basc.framework.http.HttpRequest;
import io.basc.framework.net.client.ClientRequest;

public interface ClientHttpRequest extends HttpOutputMessage, HttpRequest, ClientRequest {
	ClientHttpResponse execute() throws IOException;
}
