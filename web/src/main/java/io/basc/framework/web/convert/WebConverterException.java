package io.basc.framework.web.convert;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.util.ObjectUtils;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.WebException;

public class WebConverterException extends WebException {
	private static final long serialVersionUID = 1L;

	public WebConverterException(String msg) {
		super(msg);
	}

	public WebConverterException(Throwable cause) {
		super(cause);
	}

	public WebConverterException(String message, Throwable cause) {
		super(message, cause);
	}

	public WebConverterException(TypeDescriptor type, Object body, ServerHttpRequest request, Throwable cause) {
		super("Failed to convert from [" + request + "] to  [" + type + "] for value '" + ObjectUtils.toString(body)
				+ "'", cause);
	}

	public WebConverterException(ParameterDescriptor parameterDescriptor, ServerHttpRequest request,
			Throwable cause) {
		super("Failed to convert from [" + request + "] to  [" + parameterDescriptor + "]", cause);
	}
}
