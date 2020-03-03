package scw.mvc.action.exception;

import scw.mvc.Channel;
import scw.mvc.action.Action;

public interface ActionExceptionHandler {
	Object doHandler(Channel channel, Action action, Throwable error, ActionExceptionHandlerChain chain);
}
