package scw.result.exception;

import scw.beans.annotation.Bean;
import scw.core.exception.NestedExceptionUtils;
import scw.mvc.Channel;
import scw.mvc.ExceptionHandler;
import scw.mvc.ExceptionHandlerChain;

@Bean(proxy = false)
public class ResultExceptionHandler implements ExceptionHandler {
	private ExceptionResultFactory exceptionResultFactory;

	public ResultExceptionHandler(ExceptionResultFactory exceptionResultFactory) {
		this.exceptionResultFactory = exceptionResultFactory;
	}

	public Object handler(Channel channel, Throwable throwable, ExceptionHandlerChain chain) {
		return exceptionResultFactory.error(NestedExceptionUtils.getMostSpecificCause(throwable));
	}
}
