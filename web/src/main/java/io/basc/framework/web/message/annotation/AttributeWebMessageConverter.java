package io.basc.framework.web.message.annotation;

import java.io.IOException;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.http.HttpMessage;
import io.basc.framework.http.client.ClientHttpRequest;
import io.basc.framework.http.client.ClientHttpResponse;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.util.StringUtils;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;
import io.basc.framework.web.message.WebMessageConverter;
import io.basc.framework.web.message.WebMessagelConverterException;

public class AttributeWebMessageConverter implements WebMessageConverter {

	@Override
	public boolean canRead(HttpMessage message, TypeDescriptor descriptor) {
		return descriptor.isAnnotationPresent(Attribute.class);
	}

	@Override
	public Object read(ServerHttpRequest request, ParameterDescriptor parameterDescriptor)
			throws IOException, WebMessagelConverterException {
		Attribute attribute = parameterDescriptor.getAnnotation(Attribute.class);
		if (attribute == null) {
			return null;
		}

		return request.getAttribute(
				StringUtils.isNotEmpty(attribute.value()) ? attribute.value() : parameterDescriptor.getName());
	}

	@Override
	public Object read(ClientHttpResponse response, TypeDescriptor typeDescriptor)
			throws IOException, WebMessagelConverterException {
		return null;
	}

	@Override
	public boolean canWrite(HttpMessage message, TypeDescriptor typeDescriptor, Object value) {
		return false;
	}

	@Override
	public void write(ServerHttpRequest request, ServerHttpResponse response, TypeDescriptor typeDescriptor,
			Object body) throws IOException, WebMessagelConverterException {
	}

	@Override
	public ClientHttpRequest write(ClientHttpRequest request, ParameterDescriptor parameterDescriptor, Object parameter)
			throws IOException, WebMessagelConverterException {
		return null;
	}

}
