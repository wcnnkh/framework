package io.basc.framework.web.message.support;

import java.io.IOException;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.http.client.ClientHttpRequest;
import io.basc.framework.http.client.ClientHttpResponse;
import io.basc.framework.io.IOUtils;
import io.basc.framework.net.InetUtils;
import io.basc.framework.net.message.InputMessage;
import io.basc.framework.net.uri.UriComponentsBuilder;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;
import io.basc.framework.web.message.WebMessageConverter;
import io.basc.framework.web.message.WebMessagelConverterException;

public class InputMessageConverter implements WebMessageConverter {

	@Override
	public boolean isAccept(TypeDescriptor typeDescriptor) {
		return InputMessage.class.isAssignableFrom(typeDescriptor.getType());
	}

	@Override
	public void write(ServerHttpRequest request, ServerHttpResponse response, TypeDescriptor typeDescriptor,
			Object body) throws IOException, WebMessagelConverterException {
		InputMessage inputMessage = (InputMessage) body;
		InetUtils.writeHeader(inputMessage, response);
		IOUtils.write(inputMessage.getInputStream(), response.getOutputStream());
	}

	@Override
	public boolean isAccept(ParameterDescriptor parameterDescriptor) {
		return false;
	}

	@Override
	public Object read(ServerHttpRequest request, ParameterDescriptor parameterDescriptor)
			throws IOException, WebMessagelConverterException {
		return null;
	}

	@Override
	public ClientHttpRequest write(ClientHttpRequest request, ParameterDescriptor parameterDescriptor, Object parameter)
			throws IOException, WebMessagelConverterException {
		return request;
	}

	@Override
	public UriComponentsBuilder write(UriComponentsBuilder builder, ParameterDescriptor parameterDescriptor,
			Object parameter) throws WebMessagelConverterException {
		return builder;
	}

	@Override
	public Object read(ClientHttpResponse response, TypeDescriptor typeDescriptor)
			throws IOException, WebMessagelConverterException {
		return response;
	}

}
