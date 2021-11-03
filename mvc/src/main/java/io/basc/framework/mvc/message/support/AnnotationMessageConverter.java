package io.basc.framework.mvc.message.support;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.core.parameter.ParameterFactory;
import io.basc.framework.http.HttpMessage;
import io.basc.framework.http.client.ClientHttpResponse;
import io.basc.framework.mvc.message.WebMessageConverter;
import io.basc.framework.mvc.message.annotation.Attribute;
import io.basc.framework.mvc.message.annotation.IP;
import io.basc.framework.value.AnyValue;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;

public class AnnotationMessageConverter implements WebMessageConverter, DefaultValueFactoryAware {
	private ParameterFactory defaultValueFactory;

	@Override
	public void setDefaultValueFactory(ParameterFactory defaultValueFactory) {
		this.defaultValueFactory = defaultValueFactory;
	}

	@Override
	public boolean isAccept(ParameterDescriptor parameterDescriptor) {
		return parameterDescriptor.isAnnotationPresent(IP.class)
				|| parameterDescriptor.isAnnotationPresent(Attribute.class);
	}

	@Override
	public Object read(ServerHttpRequest request, ParameterDescriptor parameterDescriptor) {
		if (parameterDescriptor.isAnnotationPresent(IP.class)) {
			String ip = request.getIp();
			if (ip == null) {
				ip = new AnyValue(defaultValueFactory.getParameter(parameterDescriptor)).getAsString();
			}
			return ip;
		}

		Attribute attribute = parameterDescriptor.getAnnotation(Attribute.class);
		if (attribute != null) {
			return request.getAttribute(attribute.value());
		}
		return null;
	}

	@Override
	public boolean isAccept(HttpMessage message, TypeDescriptor typeDescriptor) {
		return false;
	}

	@Override
	public void write(ServerHttpRequest request, ServerHttpResponse response, TypeDescriptor typeDescriptor,
			Object body) {
	}

	@Override
	public Object read(ClientHttpResponse response, TypeDescriptor typeDescriptor) {
		return null;
	}
}
