package scw.result.exception;

import scw.core.exception.ParameterException;
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
			return resultFactory.error();
		}
	}
}
