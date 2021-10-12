package io.basc.framework.mvc.message.support;

import java.io.IOException;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.core.parameter.ParameterFactory;
import io.basc.framework.mvc.message.WebMessageConverter;
import io.basc.framework.mvc.message.WebMessagelConverterException;
import io.basc.framework.mvc.message.annotation.Attribute;
import io.basc.framework.mvc.message.annotation.IP;
import io.basc.framework.value.AnyValue;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;

public class AnnotationMessageConverter implements WebMessageConverter {
	private final ParameterFactory defaultValueFactory;
	
	public AnnotationMessageConverter(ParameterFactory defaultValueFactory) {
		this.defaultValueFactory = defaultValueFactory;
	}
	
	@Override
	public boolean canRead(ParameterDescriptor parameterDescriptor, ServerHttpRequest request) {
		return parameterDescriptor.isAnnotationPresent(IP.class)
				|| parameterDescriptor.isAnnotationPresent(Attribute.class);
	}

	@Override
	public Object read(ParameterDescriptor parameterDescriptor, ServerHttpRequest request)
			throws IOException, WebMessagelConverterException {
		if (parameterDescriptor.isAnnotationPresent(IP.class)) {
			String ip = request.getIp();
			if(ip == null) {
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
	public boolean canWrite(TypeDescriptor type, Object body, ServerHttpRequest request, ServerHttpResponse response) {
		return false;
	}

	@Override
	public void write(TypeDescriptor type, Object body, ServerHttpRequest request, ServerHttpResponse response)
			throws IOException, WebMessagelConverterException {
	}

}
