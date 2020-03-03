package scw.mvc.action.exception;

import scw.beans.annotation.Configuration;
import scw.core.utils.StringUtils;
import scw.lang.ParameterException;
import scw.mvc.Channel;
import scw.mvc.action.Action;
import scw.mvc.exception.ErrorMessage;
import scw.result.ResultFactory;
import scw.security.authority.AuthorizationFailureException;

/**
 * 默认的异常处理
 * 
 * @author shuchaowen
 *
 */
@Configuration(order=Integer.MIN_VALUE)
public final class DefaultActionExceptionHandler implements ActionExceptionHandler {
	private ResultFactory resultFactory;

	public DefaultActionExceptionHandler(ResultFactory resultFactory) {
		this.resultFactory = resultFactory;
	}

	public Object doHandler(Channel channel, Action action, Throwable error,
			ActionExceptionHandlerChain chain) {
		if (error instanceof ParameterException) {
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
