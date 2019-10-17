package scw.result.exception;

import scw.core.exception.NestedExceptionUtils;
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
		Throwable error = NestedExceptionUtils.getMostSpecificCause(e);
		if (error instanceof ParameterException) {
			return resultFactory.parameterError();
		} else if (error instanceof AuthorizationFailureException) {
			return resultFactory.authorizationFailure();
		} else {
			int code = resultFactory.getDefaultErrorCode();
			if (error instanceof ErrorCode) {
				code = ((ErrorCode) error).getErrorCode();
			}

			String msg = error.getMessage();
			if (error instanceof ErrorMessage) {
				msg = ((ErrorMessage) error).getErrorMessage();
			}
			return StringUtils.isEmpty(msg) ? resultFactory.error(code) : resultFactory.error(code, msg);
		}
	}
}
