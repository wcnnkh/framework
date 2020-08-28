package scw.mvc.exception;

import java.io.IOException;

import scw.mvc.HttpChannel;
import scw.mvc.action.Action;

public interface ExceptionHandler {
	Object doHandle(HttpChannel httpChannel, Action action, Throwable error) throws IOException;
}
