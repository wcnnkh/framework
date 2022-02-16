package io.basc.framework.http.client;

import java.io.IOException;

import io.basc.framework.http.HttpOutputMessage;
import io.basc.framework.http.HttpRequest;

public interface ClientHttpRequest extends HttpOutputMessage, HttpRequest {
	ClientHttpResponse execute() throws IOException;
}
