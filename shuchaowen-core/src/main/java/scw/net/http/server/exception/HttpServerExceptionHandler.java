package scw.net.http.server.exception;

import java.io.IOException;
import java.util.Collection;

import scw.net.http.server.ServerHttpRequest;
import scw.net.http.server.ServerHttpResponse;

public interface HttpServerExceptionHandler {
	Collection<Class<? extends Throwable>> getSupports();

	boolean isSupport(ServerHttpRequest request, Throwable error);

	Object handle(ServerHttpRequest request, ServerHttpResponse response, Throwable error) throws IOException;
}
