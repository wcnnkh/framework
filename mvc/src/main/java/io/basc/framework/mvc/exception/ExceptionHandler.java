package io.basc.framework.mvc.exception;

import io.basc.framework.mvc.HttpChannel;
import io.basc.framework.mvc.action.Action;

import java.io.IOException;

public interface ExceptionHandler {
	Object doHandle(HttpChannel httpChannel, Action action, Throwable error) throws IOException;
}
