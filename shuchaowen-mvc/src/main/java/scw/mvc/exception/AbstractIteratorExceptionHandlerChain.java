package scw.mvc.exception;

import scw.mvc.Channel;

public abstract class AbstractIteratorExceptionHandlerChain implements
		ExceptionHandlerChain {
	private final ExceptionHandlerChain chain;

	public AbstractIteratorExceptionHandlerChain(
			ExceptionHandlerChain chain) {
		this.chain = chain;
	}

	public Object doHandler(Channel channel, Throwable error) {
		ExceptionHandler handler = getNextExceptionHandler(channel, error);
		if (handler == null) {
			return chain == null ? null : chain.doHandler(channel, error);
		}

		return handler.doHandler(channel, error, this);
	}

	protected abstract ExceptionHandler getNextExceptionHandler(
			Channel channel, Throwable error);
}
