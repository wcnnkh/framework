package scw.mvc.support;

import scw.core.exception.ParameterException;
import scw.core.utils.StringUtils;
import scw.mvc.Channel;
import scw.mvc.ExceptionHandler;
import scw.mvc.ExceptionHandlerChain;
import scw.result.ErrorCode;
import scw.result.ErrorMessage;
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

	public Object handler(Channel channel, Throwable throwable, ExceptionHandlerChain chain) {
		if (throwable instanceof ParameterException) {
			return resultFactory.parameterError();
		} else if (throwable instanceof AuthorizationFailureException) {
			return resultFactory.authorizationFailure();
		} else if (throwable instanceof ErrorCode || throwable instanceof ErrorMessage) {
			Integer code = null;
			if (throwable instanceof ErrorCode) {
				code = ((ErrorCode) throwable).getErrorCode();
			}

			String msg = throwable.getMessage();
			if (throwable instanceof ErrorMessage) {
				msg = ((ErrorMessage) throwable).getErrorMessage();
			}

			if (code == null) {
				return StringUtils.isEmpty(msg) ? resultFactory.error() : resultFactory.error(msg);
			} else {
				return StringUtils.isEmpty(msg) ? resultFactory.error(code) : resultFactory.error(code, msg);
			}
		}
		return resultFactory.error();
	}
}
