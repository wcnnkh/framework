package io.basc.framework.mvc;

import java.io.IOException;

import io.basc.framework.mvc.action.Action;

public interface ExceptionHandler {
	Object doHandle(HttpChannel httpChannel, Action action, Throwable error) throws IOException;
}
