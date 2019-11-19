package scw.mvc.exception;

import scw.mvc.Action;
import scw.mvc.Channel;
import scw.mvc.ExceptionHandler;
import scw.mvc.ExceptionHandlerChain;
import scw.mvc.MVCUtils;
import scw.mvc.annotation.AppendExceptionHandler;

public final class ActionAppendExceptionHandler implements ExceptionHandler {

	public Object handler(Channel channel, Throwable error, ExceptionHandlerChain chain) {
		Action action = MVCUtils.getCurrentAction();
		if (action != null) {
			AppendExceptionHandler appendExceptionHandler = action.getAnnotation(AppendExceptionHandler.class);
			if (appendExceptionHandler != null) {
				AppendExceptionHandlerChain appendExceptionHandlerChain = new AppendExceptionHandlerChain(
						appendExceptionHandler, chain);
				return appendExceptionHandlerChain.doHandler(channel, error);
			}
		}
		return chain.doHandler(channel, error);
	}
}
