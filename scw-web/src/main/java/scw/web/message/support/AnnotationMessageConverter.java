package scw.web.message.support;

import java.io.IOException;

import scw.convert.TypeDescriptor;
import scw.core.parameter.ParameterDescriptor;
import scw.web.ServerHttpRequest;
import scw.web.ServerHttpResponse;
import scw.web.message.WebMessageConverter;
import scw.web.message.WebMessagelConverterException;
import scw.web.message.annotation.Attribute;
import scw.web.message.annotation.IP;

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
	public boolean canWrite(TypeDescriptor type, Object body, ServerHttpRequest request) {
		return false;
	}

	@Override
	public void write(TypeDescriptor type, Object body, ServerHttpRequest request, ServerHttpResponse response)
			throws IOException, WebMessagelConverterException {
	}

}
