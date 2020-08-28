package scw.mvc.exception;

import java.io.IOException;

import scw.core.instance.annotation.Configuration;
import scw.lang.NestedExceptionUtils;
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
			Long code = error instanceof ErrorCode ? ((ErrorCode) error).getCode() : null;
			String message = NestedExceptionUtils.getMostSpecificCause(error).getLocalizedMessage();
			if (code == null) {
				if (message == null) {
					return resultFactory.error();
				}
				return resultFactory.error(message);
			} else {
				if (message == null) {
					return resultFactory.error(code);
				}
				return resultFactory.error(code, message);
			}
		}
	}
}
