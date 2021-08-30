package io.basc.framework.web.message.support;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.parameter.ParameterDescriptor;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;
import io.basc.framework.web.message.WebMessageConverter;
import io.basc.framework.web.message.WebMessagelConverterException;
import io.basc.framework.web.message.annotation.Attribute;
import io.basc.framework.web.message.annotation.IP;

import java.io.IOException;

public class AnnotationMessageConverter implements WebMessageConverter {

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
			return ip == null ? parameterDescriptor.getDefaultValue().getAsString() : ip;
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
