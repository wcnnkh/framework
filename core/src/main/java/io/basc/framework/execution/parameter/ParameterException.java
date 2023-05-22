package io.basc.framework.execution.parameter;

/**
 * 参数异常
 * 
 * @author wcnnkh
 *
 */
public class ParameterException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ParameterException(String message) {
		super(message);
	}

	public ParameterException(Throwable e) {
		super(e);
	}

	public ParameterException(String message, Throwable e) {
		super(message, e);
	}

	public static String formatParameterErrorMessage(String parameterName) {
		return "Parameter format error [" + parameterName + "]";
	}

	public static ParameterException createError(String parameterName) {
		return new ParameterException(formatParameterErrorMessage(parameterName));
	}

	public static ParameterException createError(String parameterName, Throwable e) {
		return new ParameterException(formatParameterErrorMessage(parameterName), e);
	}
}
