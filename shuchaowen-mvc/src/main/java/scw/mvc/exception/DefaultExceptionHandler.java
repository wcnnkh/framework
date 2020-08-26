package scw.mvc.exception;

import java.io.IOException;

import scw.core.instance.annotation.Configuration;
import scw.lang.ParameterException;
import scw.mvc.HttpChannel;
import scw.mvc.action.Action;
import scw.result.ErrorCode;
import scw.result.ResultFactory;
import scw.security.authority.AuthorizationFailureException;

/**
 * 默认的异常处理
 * 
 * @author shuchaowen
 *
 */
@Configuration(order = Integer.MIN_VALUE)
public final class DefaultExceptionHandler implements ExceptionHandler {
	private ResultFactory resultFactory;

	public DefaultExceptionHandler(ResultFactory resultFactory) {
		this.resultFactory = resultFactory;
	}

	public Object doHandle(HttpChannel httpChannel, Action action, Throwable error) throws IOException {
		if (error instanceof ParameterException) {
			return resultFactory.parameterError();
		} else if (error instanceof AuthorizationFailureException) {
			return resultFactory.authorizationFailure();
		} else {
			if (error instanceof ErrorCode) {
				return resultFactory.error(((ErrorCode) error).getCode(), error.getLocalizedMessage());
			}
			return resultFactory.error(error.getLocalizedMessage());
		}
	}
}
