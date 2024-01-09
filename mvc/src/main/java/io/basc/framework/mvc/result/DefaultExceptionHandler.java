package io.basc.framework.mvc.result;

import java.io.IOException;

import io.basc.framework.context.annotation.ConditionalOnParameters;
import io.basc.framework.context.transaction.ResultFactory;
import io.basc.framework.execution.ExtractParameterException;
import io.basc.framework.lang.NestedExceptionUtils;
import io.basc.framework.mvc.ExceptionHandler;
import io.basc.framework.mvc.HttpChannel;
import io.basc.framework.mvc.action.Action;
import io.basc.framework.security.authority.AuthorizationFailureException;

/**
 * 默认的异常处理
 * 
 * @author wcnnkh
 *
 */
@ConditionalOnParameters
public final class DefaultExceptionHandler implements ExceptionHandler {
	private ResultFactory resultFactory;

	public DefaultExceptionHandler(ResultFactory resultFactory) {
		this.resultFactory = resultFactory;
	}

	public Object doHandle(HttpChannel httpChannel, Action action, Throwable error) throws IOException {
		if (error instanceof ExtractParameterException) {
			return resultFactory.parameterError();
		} else if (error instanceof AuthorizationFailureException) {
			return resultFactory.authorizationFailure();
		} else {
			Long code = error instanceof ResultCode ? ((ResultCode) error).getCode() : null;
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
