package io.basc.framework.web.message;

import java.io.IOException;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.http.HttpMessage;
import io.basc.framework.http.client.ClientHttpRequest;
import io.basc.framework.http.client.ClientHttpResponse;
import io.basc.framework.net.uri.UriComponentsBuilder;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;

public interface WebMessageConverter {
	boolean canRead(HttpMessage message, TypeDescriptor descriptor);

	Object read(ServerHttpRequest request, ParameterDescriptor parameterDescriptor)
			throws IOException, WebMessagelConverterException;

	Object read(ClientHttpResponse response, TypeDescriptor typeDescriptor)
			throws IOException, WebMessagelConverterException;

	boolean canWrite(HttpMessage message, TypeDescriptor typeDescriptor, Object value);

	void write(ServerHttpRequest request, ServerHttpResponse response, TypeDescriptor typeDescriptor, Object body)
			throws IOException, WebMessagelConverterException;

	ClientHttpRequest write(ClientHttpRequest request, ParameterDescriptor parameterDescriptor, Object parameter)
			throws IOException, WebMessagelConverterException;

	default boolean canWrite(TypeDescriptor typeDescriptor, Object parameter) {
		return false;
	}

	default UriComponentsBuilder write(UriComponentsBuilder builder, ParameterDescriptor parameterDescriptor,
			Object parameter) throws WebMessagelConverterException {
		return builder;
	}
}
