package scw.mvc.http;

import scw.mvc.Channel;
import scw.mvc.ExceptionHandler;
import scw.mvc.ExceptionHandlerChain;

public abstract class HttpExceptionHandler implements ExceptionHandler {
	public Object handler(Channel channel, Throwable throwable, ExceptionHandlerChain chain) {
		if (channel instanceof HttpChannel) {
			return handler((HttpChannel) channel, throwable, chain);
		}
		return chain.doHandler(channel, throwable);
	}

	protected abstract Object handler(HttpChannel channel, Throwable throwable, ExceptionHandlerChain chain);
}
