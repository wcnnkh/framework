package io.basc.framework.http.client;

import io.basc.framework.http.HttpOutputMessage;
import io.basc.framework.http.HttpRequest;

import java.io.IOException;

public interface ClientHttpRequest extends HttpOutputMessage, HttpRequest {
	ClientHttpResponse execute() throws IOException;
}
