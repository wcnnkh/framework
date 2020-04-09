package scw.mvc.exception;

import scw.mvc.Channel;

public interface ExceptionHandler {
	Object doHandler(Channel channel, Throwable error, ExceptionHandlerChain chain);
}
