package scw.mvc.exception;

import java.util.Iterator;
import java.util.LinkedList;

import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.mvc.Channel;
import scw.mvc.ExceptionHandler;
import scw.mvc.ExceptionHandlerChain;
import scw.mvc.annotation.AppendExceptionHandler;

public final class AppendExceptionHandlerChain implements ExceptionHandlerChain {
	private static Logger logger = LoggerFactory.getLogger(AppendExceptionHandlerChain.class);
	private Iterator<Class<? extends ExceptionHandler>> iterator;
	private ExceptionHandlerChain chain;

	public AppendExceptionHandlerChain(AppendExceptionHandler appendExceptionHandler, ExceptionHandlerChain chain) {
		if (appendExceptionHandler != null) {
			LinkedList<Class<? extends ExceptionHandler>> list = new LinkedList<Class<? extends ExceptionHandler>>();
			for (Class<? extends ExceptionHandler> exceptionHandler : appendExceptionHandler.value()) {
				list.add(exceptionHandler);
			}
			this.iterator.next();
		}
		this.chain = chain;
	}

	public Object doHandler(Channel channel, Throwable error) {
		ExceptionHandler exceptionHandler = getNextExceptionHandler(channel, error);
		if (exceptionHandler == null) {
			return chain == null ? null : chain.doHandler(channel, error);
		}
		return exceptionHandler.handler(channel, error, this);
	}

	private ExceptionHandler getNextExceptionHandler(Channel channel, Throwable error) {
		if (iterator == null) {
			return null;
		}

		if (iterator.hasNext()) {
			Class<? extends ExceptionHandler> handler = iterator.next();
			ExceptionHandler exceptionHandler = channel.getBean(handler);
			if (exceptionHandler == null) {
				logger.warn("无效的异常处理：{}", handler);
				return getNextExceptionHandler(channel, error);
			}

			return exceptionHandler;
		}
		return null;
	}
}
