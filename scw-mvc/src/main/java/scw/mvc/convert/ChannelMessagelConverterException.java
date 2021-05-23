package scw.mvc.convert;

import scw.convert.TypeDescriptor;
import scw.core.parameter.ParameterDescriptor;
import scw.core.utils.ObjectUtils;
import scw.lang.NestedRuntimeException;
import scw.web.ServerHttpRequest;

public class ChannelMessagelConverterException extends NestedRuntimeException {
	private static final long serialVersionUID = 1L;

	public ChannelMessagelConverterException(String msg) {
		super(msg);
	}

	public ChannelMessagelConverterException(Throwable cause) {
		super(cause);
	}

	public ChannelMessagelConverterException(String message, Throwable cause) {
		super(message, cause);
	}

	public ChannelMessagelConverterException(TypeDescriptor type, Object body,
			ServerHttpRequest request, Throwable cause) {
		super("Failed to convert from [" + request + "] to  [" + type
				+ "] for value '" + ObjectUtils.nullSafeToString(body) + "'",
				cause);
	}

	public ChannelMessagelConverterException(
			ParameterDescriptor parameterDescriptor, ServerHttpRequest request,
			Throwable cause) {
		super("Failed to convert from [" + request + "] to  ["
				+ parameterDescriptor + "]", cause);
	}
}
