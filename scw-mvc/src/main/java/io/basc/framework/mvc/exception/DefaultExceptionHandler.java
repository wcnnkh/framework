package io.basc.framework.mvc.exception;

import io.basc.framework.context.annotation.Provider;
import io.basc.framework.context.result.ResultFactory;
import io.basc.framework.context.result.ResultMsgCode;
import io.basc.framework.lang.NestedExceptionUtils;
import io.basc.framework.lang.ParameterException;
import io.basc.framework.mvc.HttpChannel;
import io.basc.framework.mvc.action.Action;
import io.basc.framework.security.authority.AuthorizationFailureException;

import java.io.IOException;

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
