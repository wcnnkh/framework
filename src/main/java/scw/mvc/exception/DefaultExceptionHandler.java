package scw.mvc.exception;

import scw.core.exception.ParameterException;
import scw.core.utils.StringUtils;
import scw.mvc.Channel;
import scw.mvc.ExceptionHandler;
import scw.mvc.ExceptionHandlerChain;
import scw.result.ResultFactory;
import scw.security.authority.AuthorizationFailureException;

/**
 * 默认的异常处理
 * 
 * @author shuchaowen
 *
 */
public final class DefaultExceptionHandler implements ExceptionHandler {
	private ResultFactory resultFactory;

	public DefaultExceptionHandler(ResultFactory resultFactory) {
		this.resultFactory = resultFactory;
	}

	public Object handler(Channel channel, Throwable error, ExceptionHandlerChain chain) {
		if (error.getClass() == RuntimeException.class) {
			return resultFactory.error(error.getMessage());
		} else if (error instanceof ParameterException) {
			return resultFactory.parameterError();
		} else if (error instanceof AuthorizationFailureException) {
			return resultFactory.authorizationFailure();
		} else if (error instanceof ErrorMessage) {
			String msg = error.getMessage();
			if (error instanceof ErrorMessage) {
				msg = ((ErrorMessage) error).getErrorMessage();
			}

			return StringUtils.isEmpty(msg) ? resultFactory.error() : resultFactory.error(msg);
		}
		return resultFactory.error();
	}
}
