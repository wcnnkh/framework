package scw.http.client;

import java.io.IOException;

import scw.http.HttpOutputMessage;
import scw.http.HttpRequest;

public interface ClientHttpRequest extends HttpOutputMessage, HttpRequest {
	ClientHttpResponse execute() throws IOException;
}
