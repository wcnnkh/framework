package scw.mvc.exception;

import scw.mvc.Channel;
import scw.mvc.ExceptionHandler;
import scw.mvc.ExceptionHandlerChain;
import scw.mvc.annotation.AppendExceptionHandler;

public final class ErrorAppendExceptionHandler implements ExceptionHandler {

	public Object handler(Channel channel, Throwable error, ExceptionHandlerChain chain) {
		AppendExceptionHandler appendExceptionHandler = error.getClass().getAnnotation(AppendExceptionHandler.class);
		if (appendExceptionHandler != null) {
			ExceptionHandlerChain appendExcptionHandlerChain = new AppendExceptionHandlerChain(appendExceptionHandler,
					chain);
			return appendExcptionHandlerChain.doHandler(channel, error);
		}
		return chain.doHandler(channel, error);
	}

}
