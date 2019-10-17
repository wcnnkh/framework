package scw.result.exception;

import scw.core.exception.ParameterException;
import scw.core.utils.StringUtils;
import scw.result.ErrorCode;
import scw.result.ErrorMessage;
import scw.result.Result;
import scw.result.ResultFactory;

public final class DefaultExceptionResultFactory implements ExceptionResultFactory {
	private ResultFactory resultFactory;

	public DefaultExceptionResultFactory(ResultFactory resultFactory) {
		this.resultFactory = resultFactory;
	}

	public Result error(Throwable e) {
		if (e instanceof ParameterException) {
			return resultFactory.parameterError();
		} else if (e instanceof AuthorizationFailureException) {
			return resultFactory.authorizationFailure();
		} else {
			int code = resultFactory.getDefaultErrorCode();
			if (e instanceof ErrorCode) {
				code = ((ErrorCode) e).getErrorCode();
			}

			String msg = e.getMessage();
			if (e instanceof ErrorMessage) {
				msg = ((ErrorMessage) e).getErrorMessage();
			}
			return StringUtils.isEmpty(msg) ? resultFactory.error(code) : resultFactory.error(code, msg);
		}
	}
}
