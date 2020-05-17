package scw.net.http.server.mvc.exception;

import java.io.IOException;

import scw.net.http.server.mvc.HttpChannel;
import scw.net.http.server.mvc.action.Action;

public interface ExceptionHandler {
	Object doHandle(HttpChannel httpChannel, Action action, Throwable error) throws IOException;
}
