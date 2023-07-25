package io.basc.framework.execution.param;

import io.basc.framework.lang.ParameterException;

/**
 * 提取参数异常
 * 
 * @author wcnnkh
 *
 */
public class ExtractParameterException extends ParameterException {
	private static final long serialVersionUID = 1L;

	public ExtractParameterException(String message) {
		super(message);
	}

	public ExtractParameterException(Throwable e) {
		super(e);
	}

	public ExtractParameterException(String message, Throwable e) {
		super(message, e);
	}

	public static String formatParameterErrorMessage(String parameterName) {
		return "Parameter format error [" + parameterName + "]";
	}

	public static ExtractParameterException createError(String parameterName) {
		return new ExtractParameterException(formatParameterErrorMessage(parameterName));
	}

	public static ExtractParameterException createError(String parameterName, Throwable e) {
		return new ExtractParameterException(formatParameterErrorMessage(parameterName), e);
	}
}
