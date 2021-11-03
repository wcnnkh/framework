package io.basc.framework.mvc.message.support;

import java.io.IOException;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.http.HttpMessage;
import io.basc.framework.http.client.ClientHttpResponse;
import io.basc.framework.io.IOUtils;
import io.basc.framework.mvc.message.WebMessageConverter;
import io.basc.framework.mvc.message.WebMessagelConverterException;
import io.basc.framework.net.InetUtils;
import io.basc.framework.net.message.InputMessage;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;

public class InputMessageConverter implements WebMessageConverter {

	@Override
	public boolean isAccept(HttpMessage message, TypeDescriptor typeDescriptor) {
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
	public Object read(ClientHttpResponse response, TypeDescriptor typeDescriptor)
			throws IOException, WebMessagelConverterException {
		return response;
	}

}
