package scw.web.convert;

import java.io.IOException;

import scw.web.ServerHttpRequest;
import scw.web.ServerHttpResponse;

public interface WebMessageWriter {
	void write(ServerHttpRequest request, ServerHttpResponse response) throws IOException;
}
