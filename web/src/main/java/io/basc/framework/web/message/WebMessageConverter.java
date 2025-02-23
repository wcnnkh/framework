package io.basc.framework.web.message;

import java.io.IOException;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.execution.Parameter;
import io.basc.framework.core.execution.ParameterDescriptor;
import io.basc.framework.http.HttpMessage;
import io.basc.framework.http.client.ClientHttpRequest;
import io.basc.framework.http.client.ClientHttpResponse;
import io.basc.framework.http.server.ServerHttpRequest;
import io.basc.framework.http.server.ServerHttpResponse;
import io.basc.framework.net.client.convert.ClientMessageConverter;
import io.basc.framework.net.server.convert.ServerMessageConverter;
import io.basc.framework.net.uri.UriComponentsBuilder;

public interface WebMessageConverter extends ClientMessageConverter, ServerMessageConverter{
	boolean canRead(HttpMessage message, TypeDescriptor descriptor);

	Object read(ServerHttpRequest request, ParameterDescriptor parameterDescriptor)
			throws IOException, WebMessagelConverterException;

	Object read(ClientHttpResponse response, TypeDescriptor typeDescriptor)
			throws IOException, WebMessagelConverterException;

	boolean canWrite(HttpMessage message, Parameter parameter);

	void write(ServerHttpRequest request, ServerHttpResponse response, TypeDescriptor typeDescriptor, Object body)
			throws IOException, WebMessagelConverterException;

	ClientHttpRequest write(ClientHttpRequest request, Parameter parameter)
			throws IOException, WebMessagelConverterException;

	default boolean canWrite(TypeDescriptor typeDescriptor, Object parameter) {
		return false;
	}

	default UriComponentsBuilder write(UriComponentsBuilder builder, ParameterDescriptor parameterDescriptor,
			Object parameter) throws WebMessagelConverterException {
		return builder;
	}
}
