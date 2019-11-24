package scw.mvc.exception;

import scw.core.exception.ParameterException;
import scw.core.utils.ClassUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.SystemPropertyUtils;
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
	private static final String[] SYSTEM_ERROR_PACKAGE_PREFIX = SystemPropertyUtils.getArrayProperty(String.class,
			"mvc.system.error.package.prefix", new String[] { "java.", "javax." });

	private ResultFactory resultFactory;

	public DefaultExceptionHandler(ResultFactory resultFactory) {
		this.resultFactory = resultFactory;
	}

	public boolean isSystemError(Channel channel, Throwable error) {
		String name = error.getClass().getName();
		for (String prefix : SYSTEM_ERROR_PACKAGE_PREFIX) {
			if (name.startsWith(prefix)) {
				return true;
			}
		}
		return !ClassUtils.IGNORE_COMMON_THIRD_PARTIES_CLASS_NAME_VERIFICATION.verification(name);
	}

	public Object handler(Channel channel, Throwable error, ExceptionHandlerChain chain) {
		if (error instanceof ParameterException) {
			return resultFactory.parameterError();
		} else if (error instanceof AuthorizationFailureException) {
			return resultFactory.authorizationFailure();
		} else if (error instanceof ErrorCode || error instanceof ErrorMessage) {
			Integer code = null;
			if (error instanceof ErrorCode) {
				code = ((ErrorCode) error).getErrorCode();
			}

			String msg = error.getMessage();
			if (error instanceof ErrorMessage) {
				msg = ((ErrorMessage) error).getErrorMessage();
			}

			if (code == null) {
				return StringUtils.isEmpty(msg) ? resultFactory.error() : resultFactory.error(msg);
			} else {
				return StringUtils.isEmpty(msg) ? resultFactory.error(code) : resultFactory.error(code, msg);
			}
		}
		return isSystemError(channel, error) ? resultFactory.error() : resultFactory.error(error.getMessage());
	}
}
