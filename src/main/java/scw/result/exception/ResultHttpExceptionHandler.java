package scw.result.exception;

import scw.beans.annotation.Bean;
import scw.mvc.ExceptionHandlerChain;
import scw.mvc.http.HttpChannel;
import scw.mvc.http.HttpExceptionHandler;

@Bean(proxy = false)
public class ResultHttpExceptionHandler extends HttpExceptionHandler {
	private ExceptionResultFactory exceptionResultFactory;

	public ResultHttpExceptionHandler(ExceptionResultFactory exceptionResultFactory) {
		this.exceptionResultFactory = exceptionResultFactory;
	}

	@Override
	protected Object handler(HttpChannel channel, Throwable throwable,
			ExceptionHandlerChain chain) {
		if (((HttpChannel) channel).getRequest().isAjax()) {
			return exceptionResultFactory.error(throwable);
		}
		return chain.doHandler(channel, throwable);
	}
}
