package io.basc.framework.lang;

import io.basc.framework.execution.parameter.ParameterException;

/**
 * 参数异常
 * 
 * @author wcnnkh
 *
 */
public class ParameterFormatException extends ParameterException {
	private static final long serialVersionUID = 1L;

	public ParameterFormatException(String parameterName) {
		super("Parameter format error [" + parameterName + "]");
	}

	public ParameterFormatException(Throwable e) {
		super(e);
	}

	public ParameterFormatException(String parameterName, Throwable e) {
		super("Parameter format error [" + parameterName + "]", e);
	}
}
