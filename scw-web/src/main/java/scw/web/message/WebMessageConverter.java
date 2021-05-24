package scw.web.message;

import java.io.IOException;

import scw.convert.TypeDescriptor;
import scw.core.parameter.ParameterDescriptor;
import scw.web.ServerHttpRequest;
import scw.web.ServerHttpResponse;

public interface WebMessageConverter {
	boolean canRead(ParameterDescriptor parameterDescriptor, ServerHttpRequest request);

	Object read(ParameterDescriptor parameterDescriptor, ServerHttpRequest request)
			throws IOException, WebMessagelConverterException;

	boolean canWrite(TypeDescriptor type, Object body, ServerHttpRequest request);

	void write(TypeDescriptor type, Object body, ServerHttpRequest request, ServerHttpResponse response)
			throws IOException, WebMessagelConverterException;
}
