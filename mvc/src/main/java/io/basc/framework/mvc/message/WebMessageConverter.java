package io.basc.framework.mvc.message;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;

import java.io.IOException;

public interface WebMessageConverter {
	boolean canRead(ParameterDescriptor parameterDescriptor, ServerHttpRequest request);

	Object read(ParameterDescriptor parameterDescriptor, ServerHttpRequest request)
			throws IOException, WebMessagelConverterException;

	boolean canWrite(TypeDescriptor type, Object body, ServerHttpRequest request, ServerHttpResponse response);

	void write(TypeDescriptor type, Object body, ServerHttpRequest request, ServerHttpResponse response)
			throws IOException, WebMessagelConverterException;
}
