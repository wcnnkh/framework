package scw.mvc.exception;

import java.io.IOException;

import scw.context.annotation.Provider;
import scw.context.result.ResultMsgCode;
import scw.context.result.ResultFactory;
import scw.lang.NestedExceptionUtils;
import scw.lang.ParameterException;
import scw.mvc.HttpChannel;
import scw.mvc.action.Action;
import scw.security.authority.AuthorizationFailureException;

/**
 * 默认的异常处理
 * 
 * @author shuchaowen
 *
 */
@Provider
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
			Long code = error instanceof ResultMsgCode ? ((ResultMsgCode) error).getCode() : null;
			String message = NestedExceptionUtils.getNonEmptyMessage(error, false);
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
