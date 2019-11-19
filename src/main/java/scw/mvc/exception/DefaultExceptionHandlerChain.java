package scw.mvc.exception;

import java.util.Collection;
import java.util.Iterator;

import scw.core.utils.CollectionUtils;
import scw.mvc.Channel;
import scw.mvc.ExceptionHandler;
import scw.mvc.ExceptionHandlerChain;

public final class DefaultExceptionHandlerChain implements ExceptionHandlerChain {
	private Iterator<ExceptionHandler> iterator;
	private ExceptionHandlerChain chain;

	public DefaultExceptionHandlerChain(Collection<ExceptionHandler> collection, ExceptionHandlerChain chain) {
		if (!CollectionUtils.isEmpty(collection)) {
			iterator = collection.iterator();
		}
		this.chain = chain;
	}

	public Object doHandler(Channel channel, Throwable throwable) {
		ExceptionHandler exceptionHandler = getNextExceptionHandler(channel, throwable);
		if (exceptionHandler == null) {
			return chain == null ? null : chain.doHandler(channel, throwable);
		}

		return exceptionHandler.handler(channel, throwable, this);
	}

	private ExceptionHandler getNextExceptionHandler(Channel channel, Throwable throwable) {
		if (iterator == null) {
			return null;
		}

		return iterator.hasNext() ? iterator.next() : null;
	}
}
