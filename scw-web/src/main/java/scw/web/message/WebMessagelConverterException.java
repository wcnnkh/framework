package scw.web.message;

import scw.convert.TypeDescriptor;
import scw.core.parameter.ParameterDescriptor;
import scw.core.utils.ObjectUtils;
import scw.lang.NestedRuntimeException;
import scw.web.ServerHttpRequest;

public class WebMessagelConverterException extends NestedRuntimeException {
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
		super("Failed to convert from [" + request + "] to  [" + type + "] for value '"
				+ ObjectUtils.nullSafeToString(body) + "'", cause);
	}

	public WebMessagelConverterException(ParameterDescriptor parameterDescriptor, ServerHttpRequest request,
			Throwable cause) {
		super("Failed to convert from [" + request + "] to  [" + parameterDescriptor + "]", cause);
	}
}
