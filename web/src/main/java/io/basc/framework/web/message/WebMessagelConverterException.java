package io.basc.framework.web.message;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.param.ParameterDescriptor;
import io.basc.framework.util.ObjectUtils;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.WebException;

public class WebMessagelConverterException extends WebException {
	private static final long serialVersionUID = 1L;

	public WebMessagelConverterException(String msg) {
		super(msg);
	}

	public WebMessagelConverterException(Throwable cause) {
		super(cause);
	}

	public WebMessagelConverterException(String message, Throwable cause) {
		super(message, cause);
	}

	public WebMessagelConverterException(TypeDescriptor type, Object body, ServerHttpRequest request, Throwable cause) {
		super("Failed to convert from [" + request + "] to  [" + type + "] for value '" + ObjectUtils.toString(body)
				+ "'", cause);
	}

	public WebMessagelConverterException(ParameterDescriptor parameterDescriptor, ServerHttpRequest request,
			Throwable cause) {
		super("Failed to convert from [" + request + "] to  [" + parameterDescriptor + "]", cause);
	}
}
